package com.floti.api.domain.board.like.service;

import com.floti.api.domain.auth.repository.UserRepository;
import com.floti.api.domain.board.common.entity.LikeableEntity;
import com.floti.api.domain.board.like.dto.LikeResponse;
import com.floti.api.domain.board.like.entity.*;
import com.floti.api.domain.board.like.repository.*;
import com.floti.api.domain.board.qna.repository.AnswerRepository;
import com.floti.api.domain.board.tip.repository.CommentRepository;
import com.floti.api.domain.board.tip.repository.TipPostRepository;
import com.floti.api.error.exception.AnswerNotFoundException;
import com.floti.api.error.exception.CommentNotFoundException;
import com.floti.api.error.exception.PostNotFoundException;
import com.floti.api.error.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeTipPostRepository likeTipPostRepository;
    private final LikeCommentRepository likeCommentRepository;
    private final LikeAnswerRepository likeAnswerRepository;
    private final TipPostRepository tipPostRepository;
    private final CommentRepository commentRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;

    public LikeResponse toggleLike(Long userId,
                                   Supplier<LikeableEntity> targetSupplier,
                                   Supplier<Object> likeSupplier,
                                   Runnable saveLike,
                                   Consumer<Object> deleteLike) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException();

        LikeableEntity target = targetSupplier.get();
        Object like = likeSupplier.get();
        boolean liked = (like == null);

        if (liked) {
            saveLike.run();
            target.incrementLikeCount();
        } else {
            deleteLike.accept(like);
            target.decrementLikeCount();
        }

        return new LikeResponse(liked, target.getLikeCount());
    }

    /* 1. Tip 게시글 추천 토글 */
    @Transactional
    public LikeResponse toggleLikeTipPost(Long userId, Long postId) {
        return toggleLike(
                userId,
                () -> tipPostRepository.findById(postId).orElseThrow(PostNotFoundException::new),
                () -> likeTipPostRepository.findById(new UserPostId(userId, postId)).orElse(null),
                () -> likeTipPostRepository.save(new LikeTipPosts(userId, postId)),
                like -> likeTipPostRepository.delete((LikeTipPosts) like)
        );
    }

    /* 2. 댓글 추천 토글 */
    @Transactional
    public LikeResponse toggleLikeComment(Long userId, Long commentId) {
        return toggleLike(
                userId,
                () -> commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new),
                () -> likeCommentRepository.findById(new UserCommentId(userId, commentId)).orElse(null),
                () -> likeCommentRepository.save(new LikeComments(userId, commentId)),
                like -> likeCommentRepository.delete((LikeComments) like)
        );
    }

    /* 3. 답변 추천 토글 */
    @Transactional
    public LikeResponse toggleLikeAnswer(Long userId, Long answerId) {
        return toggleLike(
                userId,
                () -> answerRepository.findById(answerId).orElseThrow(AnswerNotFoundException::new),
                () -> likeAnswerRepository.findById(new UserAnswerId(userId, answerId)).orElse(null),
                () -> likeAnswerRepository.save(new LikeAnswers(userId, answerId)),
                like -> likeAnswerRepository.delete((LikeAnswers) like)
        );
    }
}
