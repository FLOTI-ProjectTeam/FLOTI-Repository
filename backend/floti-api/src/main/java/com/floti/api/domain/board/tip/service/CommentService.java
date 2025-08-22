package com.floti.api.domain.board.tip.service;

import com.floti.api.domain.auth.entity.Users;
import com.floti.api.domain.auth.repository.UserRepository;
import com.floti.api.domain.board.qna.dto.QnaPostResponse;
import com.floti.api.domain.board.tip.dto.CommentRequest;
import com.floti.api.domain.board.tip.dto.CommentResponse;
import com.floti.api.domain.board.tip.entity.Comments;
import com.floti.api.domain.board.tip.entity.TipPosts;
import com.floti.api.domain.board.tip.repository.CommentRepository;
import com.floti.api.domain.board.tip.repository.TipPostRepository;
import com.floti.api.error.ExceptionMessage;
import com.floti.api.error.PostNotFoundException;
import com.floti.api.error.CommentNotFoundException;
import com.floti.api.error.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final TipPostRepository tipPostRepository;
    private final UserRepository userRepository;

    /* 1. 조회 */
    public List<CommentResponse> getComments(Long postId) {
        List<Comments> comments = commentRepository.findByPostId(postId);

        if (comments.isEmpty())
            return Collections.emptyList();

        Map<Long, CommentResponse> commentMap = comments.stream()
                .collect(Collectors.toMap(Comments::getId, CommentResponse::new));
        List<CommentResponse> topComments = new ArrayList<>();

        for (Comments comment : comments) {
            CommentResponse temp = commentMap.get(comment.getId());
            if (comment.getParentId() == null) {
                topComments.add(temp);
            } else {
                CommentResponse parent = commentMap.get(comment.getParentId());
                if (parent != null)
                    parent.getReplies().add(temp);
            }
        }

        return topComments;
    }

    /* 2. 등록 */
    @Transactional
    public CommentResponse createComment(Long userId, Long postId, CommentRequest commentRequest) {
        Users author = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        if (!tipPostRepository.existsById(postId))
            throw new PostNotFoundException();

        Comments comment = Comments.builder()
                .postId(postId)
                .parentId(commentRequest.getParentId())
                .author(author)
                .content(commentRequest.getContent())
                .build();

        return new CommentResponse(commentRepository.save(comment));
    }

    /* 3. 수정 */
    @Transactional
    public CommentResponse updateComment(Long userId, Long id, CommentRequest commentRequest) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException();

        Comments comment = commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);

        if (!userId.equals(comment.getAuthor().getId()))
            throw new AccessDeniedException(ExceptionMessage.COMMENT_UPDATE_DENIED);

        comment.update(commentRequest);
        return new CommentResponse(comment);
    }

    /* 4. 삭제 */
    @Transactional
    public void deleteComment(Long userId, Long id) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException();

        Comments comment = commentRepository.findById(id).orElseThrow(PostNotFoundException::new);

        if (!userId.equals(comment.getAuthor().getId()))
            throw new AccessDeniedException(ExceptionMessage.COMMENT_DELETE_DENIED);

        commentRepository.delete(comment);
    }
}
