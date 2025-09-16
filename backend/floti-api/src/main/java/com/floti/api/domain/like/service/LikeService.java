package com.floti.api.domain.like.service;

import com.floti.api.domain.auth.repository.UserRepository;
import com.floti.api.domain.board.common.entity.LikeableEntity;
import com.floti.api.domain.like.dto.LikeResponse;
import com.floti.api.domain.like.entity.LikeAnswers;
import com.floti.api.domain.like.entity.LikeTipPosts;
import com.floti.api.domain.like.entity.UserAnswerId;
import com.floti.api.domain.like.entity.UserPostId;
import com.floti.api.domain.like.repository.LikeAnswerRepository;
import com.floti.api.domain.like.repository.LikeTipPostRepository;
import com.floti.api.domain.board.qna.repository.AnswerRepository;
import com.floti.api.domain.board.tip.repository.TipPostRepository;
import com.floti.api.error.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeTipPostRepository likeTipPostRepository;
    private final LikeAnswerRepository likeAnswerRepository;
    private final TipPostRepository tipPostRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;

    public LikeResponse toggleLike(Long userId,
                                   Supplier<LikeableEntity> targetSupplier,
                                   Supplier<Object> likeSupplier,
                                   Runnable saveLike,
                                   Consumer<Object> deleteLike) {
        if (!userRepository.existsById(userId))
            throw new EntityNotFoundException(ExceptionMessage.USER_NOT_FOUND);

        LikeableEntity likeableEntity = targetSupplier.get();
        Object likeEntity = likeSupplier.get();
        boolean liked = (likeEntity == null);

        if (liked) {
            saveLike.run();
            likeableEntity.incrementLikeCount();
        } else {
            deleteLike.accept(likeEntity);
            likeableEntity.decrementLikeCount();
        }

        return new LikeResponse(liked, likeableEntity.getLikeCount());
    }

    /* 1. Tip 게시글 추천 토글 */
    @Transactional
    public LikeResponse toggleLikeTipPost(Long userId, Long postId) {
        return toggleLike(
                userId,
                () -> tipPostRepository.findById(postId)
                        .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.POST_NOT_FOUND)),
                () -> likeTipPostRepository.findById(new UserPostId(userId, postId)).orElse(null),
                () -> likeTipPostRepository.save(new LikeTipPosts(userId, postId)),
                like -> likeTipPostRepository.delete((LikeTipPosts) like)
        );
    }

    /* 2. 답변 추천 토글 */
    @Transactional
    public LikeResponse toggleLikeAnswer(Long userId, Long answerId) {
        return toggleLike(
                userId,
                () -> answerRepository.findById(answerId)
                        .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.ANSWER_NOT_FOUND)),
                () -> likeAnswerRepository.findById(new UserAnswerId(userId, answerId)).orElse(null),
                () -> likeAnswerRepository.save(new LikeAnswers(userId, answerId)),
                like -> likeAnswerRepository.delete((LikeAnswers) like)
        );
    }
}
