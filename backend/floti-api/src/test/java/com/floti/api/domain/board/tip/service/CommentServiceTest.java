package com.floti.api.domain.board.tip.service;

import com.floti.api.domain.auth.entity.Users;
import com.floti.api.domain.auth.repository.UserRepository;
import com.floti.api.domain.board.tip.dto.CommentRequest;
import com.floti.api.domain.board.tip.dto.CommentResponse;
import com.floti.api.domain.board.tip.entity.Comments;
import com.floti.api.domain.board.tip.entity.TipPosts;
import com.floti.api.domain.board.tip.repository.CommentRepository;
import com.floti.api.domain.board.tip.repository.TipPostRepository;
import com.floti.api.error.CommentNotFoundException;
import com.floti.api.error.ReplyNotAllowedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test") //application-test.yml 사용
@Transactional
public class CommentServiceTest {
    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TipPostRepository tipPostRepository;

    @Autowired
    private UserRepository userRepository;

    private Long testUserId;
    private Long testPostId;
    private Long testCommentId;
    private Long testReplyId;

    @BeforeEach //테스트용 데이터 생성
    void setUp() {
        Users user = Users.builder()
                .email("test01@gmail.com")
                .username("test01")
                .password("password123")
                .nickname("테스터01")
                .build();
        userRepository.save(user);
        testUserId = user.getId();

        TipPosts tipPost = TipPosts.builder()
                .author(user)
                .title("원본 제목")
                .content("원본 내용")
                .build();
        tipPostRepository.save(tipPost);
        testPostId = tipPost.getId();

        Comments comment = Comments.builder()
                .postId(testPostId)
                .author(user)
                .content("댓글 내용")
                .build();
        commentRepository.save(comment);
        tipPost.incrementCommentCount();
        testCommentId = comment.getId();

        Comments reply = Comments.builder()
                .postId(testPostId)
                .parentId(testCommentId)
                .author(user)
                .content("답글 내용")
                .build();
        commentRepository.save(reply);
        tipPost.incrementCommentCount();
        testReplyId = reply.getId();
    }

    @Test
    @DisplayName("getComments: 댓글 조회")
    void getComments_exist() {
        List<CommentResponse> responses = commentService.getComments(testPostId);

        assertEquals(1, responses.size());
        assertEquals("댓글 내용", responses.get(0).getContent());
        assertEquals("답글 내용", responses.get(0).getReplies().get(0).getContent());
    }

    @Test
    @DisplayName("getComments: 댓글 조회 결과 없음")
    void getComments_empty() {
        TipPosts tipPost = TipPosts.builder()
                .author(userRepository.findById(testUserId).get())
                .title("테스트 제목")
                .content("테스트 내용")
                .build();
        tipPostRepository.save(tipPost);

        List<CommentResponse> responses = commentService.getComments(tipPost.getId());

        assertTrue(responses.isEmpty());
    }

    @Test
    @DisplayName("createComment: 댓글 등록")
    void createComment_noReply() {
        CommentRequest request = new CommentRequest();
        request.setContent("등록된 내용");

        CommentResponse response = commentService.createComment(testUserId, testPostId, request);
        TipPosts tipPost = tipPostRepository.findById(response.getPostId()).get();

        assertNotNull(response.getId());
        assertEquals(testPostId, response.getPostId());
        assertEquals("등록된 내용", response.getContent());
        assertEquals("테스터01", response.getAuthor().getNickname());
        assertEquals(3, tipPost.getCommentCount());
    }

    @Test
    @DisplayName("createComment: 답글 등록")
    void createComment_reply() {
        CommentRequest request = new CommentRequest();
        request.setParentId(testCommentId);
        request.setContent("등록된 내용");

        CommentResponse response = commentService.createComment(testUserId, testPostId, request);
        TipPosts tipPost = tipPostRepository.findById(response.getPostId()).get();

        assertNotNull(response.getId());
        assertEquals(testPostId, response.getPostId());
        assertEquals(testCommentId, response.getParentId());
        assertEquals("등록된 내용", response.getContent());
        assertEquals("테스터01", response.getAuthor().getNickname());
        assertEquals(3, tipPost.getCommentCount());
    }

    @Test
    @DisplayName("createComment: 답글 등록 [댓글 없음]")
    void createComment_fail_commentNotFound() {
        CommentRequest request = new CommentRequest();
        request.setParentId(9999L);
        request.setContent("등록된 내용");

        CommentNotFoundException exception = assertThrows(CommentNotFoundException.class, () -> {
            commentService.createComment(testUserId, testPostId, request);
        });

        assertEquals("댓글을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("createComment: 답글 등록 [댓글 단계 초과]")
    void createComment_fail_replyDepthExceeded() {
        CommentRequest request = new CommentRequest();
        request.setParentId(testReplyId);
        request.setContent("등록된 내용");

        ReplyNotAllowedException exception = assertThrows(ReplyNotAllowedException.class, () -> {
            commentService.createComment(testUserId, testPostId, request);
        });

        assertEquals("답글은 최상위 댓글에만 작성할 수 있습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("updateComment: 댓글 수정")
    void updateComment_success() {
        CommentRequest request = new CommentRequest();
        request.setContent("수정된 내용");

        CommentResponse response = commentService.updateComment(testUserId, testCommentId, request);

        assertEquals("수정된 내용", response.getContent());
    }

    @Test
    @DisplayName("deleteComment: 댓글 삭제")
    void deleteComment_noReply() {
        commentService.deleteComment(testUserId, testCommentId);

        List<CommentResponse> responses = commentService.getComments(testPostId);
        TipPosts tipPost = tipPostRepository.findById(testPostId).get();

        assertTrue(responses.get(0).isDeleted());
        assertNull(responses.get(0).getContent());
        assertEquals("답글 내용", responses.get(0).getReplies().get(0).getContent());
        assertEquals(1, tipPost.getCommentCount());
    }

    @Test
    @DisplayName("deleteComment: 답글 삭제")
    void deleteComment_reply() {
        commentService.deleteComment(testUserId, testReplyId);

        List<CommentResponse> responses = commentService.getComments(testPostId);
        TipPosts tipPost = tipPostRepository.findById(testPostId).get();

        assertTrue(responses.get(0).getReplies().isEmpty());
        assertEquals(1, tipPost.getCommentCount());
    }

    @Test
    @DisplayName("createComment: 답글 등록 [삭제된 댓글]")
    void createComment_fail_deletedComment() {
        commentService.deleteComment(testUserId, testCommentId);

        CommentRequest request = new CommentRequest();
        request.setParentId(testCommentId);
        request.setContent("테스트 내용");

        CommentNotFoundException exception = assertThrows(CommentNotFoundException.class, () -> {
            commentService.createComment(testUserId, testPostId, request);
        });

        assertEquals("댓글을 찾을 수 없습니다.", exception.getMessage());
    }
}
