package com.floti.api.domain.board.tip.service;

import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.board.tip.dto.BaseCommentResponse;
import com.floti.api.domain.board.tip.dto.CommentRequest;
import com.floti.api.domain.board.tip.dto.CommentResponse;
import com.floti.api.domain.board.tip.dto.ReplyResponse;
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

import java.util.*;

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

        List<CommentResponse> topComments = new ArrayList<>();
        Map<Long, CommentResponse> parentMap = new HashMap<>();

        /* 댓글 트리 생성 */
        for (Comments comment : comments) {
            if (comment.getParentId() == null) {
                CommentResponse parent = new CommentResponse(comment);
                topComments.add(parent);
                parentMap.put(comment.getId(), parent);
            } else {
                CommentResponse parent = parentMap.get(comment.getParentId());
                if (parent != null) parent.addReply(new ReplyResponse(comment));
            }
        }

        return topComments;
    }

    /* 2. 등록 */
    @Transactional
    public BaseCommentResponse createComment(User user, Long postId, CommentRequest request) {
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

        if (parentId == null)
            return new CommentResponse(comment);
        return new ReplyResponse(comment);
    }

    /* 3. 수정 */
    @Transactional
    public BaseCommentResponse updateComment(Long userId, Long id, CommentRequest request) {
        Comments comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.COMMENT_NOT_FOUND));

        if (!userId.equals(comment.getAuthor().getId()))
            throw new AccessDeniedException(ExceptionMessage.UPDATE_DENIED);

        comment.update(request.getContent());

        if (comment.getParentId() == null)
            return new CommentResponse(comment);
        return new ReplyResponse(comment);
    }

    /* 4. 삭제 */
    @Transactional
    public void deleteComment(Long userId, Long id) {
        Comments comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.COMMENT_NOT_FOUND));

        if (comment.getAuthor() == null || !userId.equals(comment.getAuthor().getId()))
            throw new AccessDeniedException(ExceptionMessage.DELETE_DENIED);

        if (comment.getParentId() == null) comment.softDelete();
        else commentRepository.delete(comment);

        TipPosts tipPost = tipPostRepository.getReferenceById(comment.getPostId());
        tipPost.decrementCommentCount();
    }
}
