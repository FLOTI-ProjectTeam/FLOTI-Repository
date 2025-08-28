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
import com.floti.api.error.*;
import com.floti.api.error.exception.CommentNotFoundException;
import com.floti.api.error.exception.PostNotFoundException;
import com.floti.api.error.exception.ReplyNotAllowedException;
import com.floti.api.error.exception.UserNotFoundException;
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
    private final LikeCommentRepository likeCommentRepository;

    /* 1. 조회 */
    public List<CommentResponse> getComments(Long userId, Long postId) {
        List<Comments> comments = commentRepository.findByPostId(postId);

        if (comments.isEmpty())
            return Collections.emptyList();

        /* 댓글별 추천 여부 */
        // 1. 모든 댓글 ID
        List<Long> commentIds = comments.stream().map(Comments::getId).toList();

        // 2. 사용자가 추천한 댓글 ID
        Set<Long> likedCommentIds = likeCommentRepository.findByUserIdAndCommentIdIn(userId, commentIds)
                .stream()
                .map(LikeComments::getCommentId)
                .collect(Collectors.toSet());

        Map<Long, CommentResponse> commentMap = comments.stream()
                .collect(Collectors.toMap(Comments::getId,
                        c -> new CommentResponse(c, likedCommentIds.contains(c.getId()))));

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
    public CommentResponse createComment(Long userId, Long postId, CommentRequest request) {
        Users author = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        TipPosts tipPost = tipPostRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        Long parentId = request.getParentId();

        if (parentId != null) {
            Comments parentComment = commentRepository.findByIdAndPostId(parentId, postId)
                    .orElseThrow(CommentNotFoundException::new);

            if (parentComment.isDeleted())
                throw new CommentNotFoundException();

            if (parentComment.getParentId() != null)
                throw new ReplyNotAllowedException();
        }

        Comments comment = Comments.builder()
                .postId(postId)
                .parentId(parentId)
                .author(author)
                .content(request.getContent())
                .build();

        commentRepository.save(comment);
        tipPost.incrementCommentCount();

        return new CommentResponse(comment);
    }

    /* 3. 수정 */
    @Transactional
    public CommentResponse updateComment(Long userId, Long id, CommentRequest request) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException();

        Comments comment = commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);

        if (!userId.equals(comment.getAuthor().getId()))
            throw new AccessDeniedException(ExceptionMessage.UPDATE_DENIED);

        comment.update(request);
        return new CommentResponse(comment);
    }

    /* 4. 삭제 */
    @Transactional
    public void deleteComment(Long userId, Long id) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException();

        Comments comment = commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);

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
