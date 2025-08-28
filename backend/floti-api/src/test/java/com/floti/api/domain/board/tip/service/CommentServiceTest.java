package com.floti.api.domain.board.tip.service;

import com.floti.api.domain.auth.entity.Users;
import com.floti.api.domain.auth.repository.UserRepository;
import com.floti.api.domain.board.like.entity.LikeComments;
import com.floti.api.domain.board.like.repository.LikeCommentRepository;
import com.floti.api.domain.board.tip.dto.CommentRequest;
import com.floti.api.domain.board.tip.dto.CommentResponse;
import com.floti.api.domain.board.tip.entity.Comments;
import com.floti.api.domain.board.tip.entity.TipPosts;
import com.floti.api.domain.board.tip.repository.CommentRepository;
import com.floti.api.domain.board.tip.repository.TipPostRepository;
import com.floti.api.error.exception.CommentNotFoundException;
import com.floti.api.error.exception.ReplyNotAllowedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TipPostRepository tipPostRepository;

    @Mock
    private LikeCommentRepository likeCommentRepository;

    @Mock
    private UserRepository userRepository;

    private static final Long VALID_ID = 1L;
    private static final Long INVALID_ID = 9999L;

    private final Users testUser = Users.builder().id(VALID_ID).nickname("테스터01").build();
    private final TipPosts testPost = TipPosts.builder().author(testUser).build();
    private final Comments testComment = Comments.builder()
            .id(VALID_ID).postId(VALID_ID).author(testUser).content("첫번째 댓글").build();
    private final Comments testReply = Comments.builder()
            .id(INVALID_ID).postId(VALID_ID).parentId(VALID_ID).author(testUser).content("첫번째 답글").build();

    @BeforeEach
    void setUp() {
        testPost.incrementCommentCount();
        testPost.incrementCommentCount();
    }

    @Test
    @DisplayName("getComments: 댓글 있음 - 댓글 리스트 반환")
    void getComments_exist() {
        //given
        List<Comments> comments = List.of(testComment, testReply);
        LikeComments likeComment = new LikeComments(VALID_ID, VALID_ID);

        when(commentRepository.findByPostId(anyLong())).thenReturn(comments);
        when(likeCommentRepository.findByUserIdAndCommentIdIn(anyLong(), anyList())).thenReturn(List.of(likeComment));

        //when
        List<CommentResponse> responses = commentService.getComments(VALID_ID, VALID_ID);

        //then
        assertEquals(1, responses.size());
        assertEquals("첫번째 댓글", responses.get(0).getContent());
        assertTrue(responses.get(0).isLiked());
        assertEquals("첫번째 답글", responses.get(0).getReplies().get(0).getContent());
        assertFalse(responses.get(0).getReplies().get(0).isLiked());
    }

    @Test
    @DisplayName("getComments: 댓글 없음 - 빈 리스트 반환")
    void getComments_empty() {
        //given
        when(commentRepository.findByPostId(anyLong())).thenReturn(Collections.emptyList());

        //when
        List<CommentResponse> responses = commentService.getComments(VALID_ID, INVALID_ID);

        //then
        assertTrue(responses.isEmpty());
    }

    @Test
    @DisplayName("createComment: 댓글 미지정 - 댓글 등록")
    void createComment_noReply() {
        //given
        CommentRequest request = new CommentRequest();
        request.setContent("두번째 댓글");

        int previousCommentCount = testPost.getCommentCount();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(tipPostRepository.findById(anyLong())).thenReturn(Optional.of(testPost));
        when(commentRepository.save(any(Comments.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //when
        CommentResponse response = commentService.createComment(VALID_ID, VALID_ID, request);

        //then
        assertEquals(VALID_ID, response.getPostId());
        assertEquals("두번째 댓글", response.getContent());
        assertEquals("테스터01", response.getAuthor().getNickname());
        assertEquals(previousCommentCount + 1, testPost.getCommentCount());
    }

    @Test
    @DisplayName("createComment: 댓글 있음 - 답글 등록")
    void createComment_reply() {
        //given
        CommentRequest request = new CommentRequest();
        request.setParentId(VALID_ID);
        request.setContent("두번째 답글");

        int previousCommentCount = testPost.getCommentCount();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(tipPostRepository.findById(anyLong())).thenReturn(Optional.of(testPost));
        when(commentRepository.findByIdAndPostId(anyLong(), anyLong())).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comments.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //when
        CommentResponse response = commentService.createComment(VALID_ID, VALID_ID, request);

        //then
        assertEquals(VALID_ID, response.getPostId());
        assertEquals(VALID_ID, response.getParentId());
        assertEquals("두번째 답글", response.getContent());
        assertEquals("테스터01", response.getAuthor().getNickname());
        assertEquals(previousCommentCount + 1, testPost.getCommentCount());
    }

    @Test
    @DisplayName("createComment: 댓글 없음 - CommentNotFoundException")
    void createComment_fail_commentNotFound() {
        //given
        CommentRequest request = new CommentRequest();
        request.setParentId(INVALID_ID);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(tipPostRepository.findById(anyLong())).thenReturn(Optional.of(testPost));
        when(commentRepository.findByIdAndPostId(anyLong(), anyLong())).thenReturn(Optional.empty());

        //when
        CommentNotFoundException exception = assertThrows(CommentNotFoundException.class, () -> {
            commentService.createComment(VALID_ID, VALID_ID, request);
        });

        //then
        assertEquals("댓글을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("createComment: 삭제된 댓글 - CommentNotFoundException")
    void createComment_fail_deletedComment() {
        //then
        CommentRequest request = new CommentRequest();
        request.setParentId(VALID_ID);

        testComment.softDelete();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(tipPostRepository.findById(anyLong())).thenReturn(Optional.of(testPost));
        when(commentRepository.findByIdAndPostId(anyLong(), anyLong())).thenReturn(Optional.of(testComment));

        //when
        CommentNotFoundException exception = assertThrows(CommentNotFoundException.class, () -> {
            commentService.createComment(VALID_ID, VALID_ID, request);
        });

        //then
        assertEquals("댓글을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("createComment: 답글 - ReplyNotAllowedException")
    void createComment_fail_replyToReply() {
        //given
        CommentRequest request = new CommentRequest();
        request.setParentId(INVALID_ID);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(tipPostRepository.findById(anyLong())).thenReturn(Optional.of(testPost));
        when(commentRepository.findByIdAndPostId(anyLong(), anyLong())).thenReturn(Optional.of(testReply));

        //when
        ReplyNotAllowedException exception = assertThrows(ReplyNotAllowedException.class, () -> {
            commentService.createComment(VALID_ID, VALID_ID, request);
        });

        //then
        assertEquals("답글은 최상위 댓글에만 작성할 수 있습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("updateComment: 댓글 수정")
    void updateComment_success() {
        //given
        CommentRequest request = new CommentRequest();
        request.setContent("수정된 댓글");

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(testComment));

        //when
        CommentResponse response = commentService.updateComment(VALID_ID, VALID_ID, request);

        //then
        assertEquals("수정된 댓글", response.getContent());
    }

    @Test
    @DisplayName("deleteComment: 댓글 삭제")
    void deleteComment_noReply() {
        //given
        int previousCommentCount = testPost.getCommentCount();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(testComment));
        when(tipPostRepository.getReferenceById(anyLong())).thenReturn(testPost);

        //when
        commentService.deleteComment(VALID_ID, VALID_ID);

        //then
        assertTrue(testComment.isDeleted());
        assertEquals(previousCommentCount - 1, testPost.getCommentCount());
    }

    @Test
    @DisplayName("deleteComment: 답글 삭제")
    void deleteComment_reply() {
        //given
        int previousCommentCount = testPost.getCommentCount();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(testReply));
        when(tipPostRepository.getReferenceById(anyLong())).thenReturn(testPost);

        //when
        commentService.deleteComment(VALID_ID, VALID_ID);

        //then
        verify(commentRepository).delete(testReply);
        assertEquals(previousCommentCount - 1, testPost.getCommentCount());
    }
}
