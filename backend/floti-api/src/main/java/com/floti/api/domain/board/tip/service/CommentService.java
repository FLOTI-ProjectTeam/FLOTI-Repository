package com.floti.api.domain.board.tip.service;

import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.board.tip.dto.CommentRequest;
import com.floti.api.domain.board.tip.dto.CommentResponse;
import com.floti.api.domain.board.tip.entity.Comments;
import com.floti.api.domain.board.tip.entity.TipPosts;
import com.floti.api.domain.board.tip.repository.CommentRepository;
import com.floti.api.domain.board.tip.repository.TipPostRepository;
import com.floti.api.error.ExceptionMessage;
import com.floti.api.error.exception.ReplyNotAllowedException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final TipPostRepository tipPostRepository;

    /* 1. 조회 */
    public List<CommentResponse> getComments(Long postId) {
        List<Comments> comments = commentRepository.findByPostId(postId);

        if (comments.isEmpty())
            return Collections.emptyList();

        Map<Long, CommentResponse> commentMap = comments.stream()
                .collect(Collectors.toMap(Comments::getId, CommentResponse::new));

        /* 댓글 트리 생성 */
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
    public CommentResponse createComment(User user, Long postId, CommentRequest request) {
        TipPosts tipPost = tipPostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.POST_NOT_FOUND));
        Long parentId = request.getParentId();

        if (parentId != null) {
            Comments parentComment = commentRepository.findByIdAndPostId(parentId, postId)
                    .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.COMMENT_NOT_FOUND));

            if (parentComment.isDeleted())
                throw new EntityNotFoundException(ExceptionMessage.COMMENT_NOT_FOUND);

            if (parentComment.getParentId() != null)
                throw new ReplyNotAllowedException();
        }

        Comments comment = Comments.builder()
                .postId(postId)
                .parentId(parentId)
                .author(user)
                .content(request.getContent())
                .build();

        commentRepository.save(comment);
        tipPost.incrementCommentCount();

        return new CommentResponse(comment);
    }

    /* 3. 수정 */
    @Transactional
    public CommentResponse updateComment(Long userId, Long id, CommentRequest request) {
        Comments comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.COMMENT_NOT_FOUND));

        if (!userId.equals(comment.getAuthor().getId()))
            throw new AccessDeniedException(ExceptionMessage.UPDATE_DENIED);

        comment.update(request.getContent());
        return new CommentResponse(comment);
    }

    /* 4. 삭제 */
    @Transactional
    public void deleteComment(Long userId, Long id) {
        Comments comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.COMMENT_NOT_FOUND));

        if (comment.getAuthor() == null || !userId.equals(comment.getAuthor().getId()))
            throw new AccessDeniedException(ExceptionMessage.DELETE_DENIED);

        if (comment.getParentId() == null) {
            comment.softDelete();
        } else {
            commentRepository.delete(comment);
        }

        TipPosts tipPost = tipPostRepository.getReferenceById(comment.getPostId());
        tipPost.decrementCommentCount();
    }
}
