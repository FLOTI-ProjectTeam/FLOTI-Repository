package com.floti.api.domain.like.service;

import com.floti.api.domain.board.common.entity.LikeableEntity;
import com.floti.api.domain.board.discussion.repository.MessageRepository;
import com.floti.api.domain.board.qna.repository.AnswerRepository;
import com.floti.api.domain.board.tip.repository.TipPostRepository;
import com.floti.api.domain.like.dto.LikeResponse;
import com.floti.api.domain.like.entity.*;
import com.floti.api.domain.like.repository.*;
import com.floti.api.error.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeTipPostRepository likeTipPostRepository;
    private final LikeAnswerRepository likeAnswerRepository;
    private final LikeMessageRepository likeMessageRepository;
    private final TipPostRepository tipPostRepository;
    private final AnswerRepository answerRepository;
    private final MessageRepository messageRepository;

    /* 추천 토글 */
    public <T> LikeResponse toggleLike(Long userId,
                                       Supplier<LikeableEntity> targetSupplier,
                                       Supplier<T> likeSupplier,
                                       Runnable saveLike,
                                       Consumer<T> deleteLike) {
        LikeableEntity likeableEntity = targetSupplier.get();
        T likeEntity = likeSupplier.get();
        boolean liked = (likeEntity == null);

        if (userId.equals(likeableEntity.getAuthor().getId()))
            throw new AccessDeniedException("작성자는 추천할 수 없습니다.");

        if (liked) {
            saveLike.run();
            likeableEntity.incrementLikeCount();
        } else {
            deleteLike.accept(likeEntity);
            likeableEntity.decrementLikeCount();
        }

        return new LikeResponse(liked, likeableEntity.getLikeCount());
    }

    /* 메시지별 추천 여부 반환 */
    public Set<Long> getLikedMessageIds(Long userId, List<Long> messageIds) {
        return likeMessageRepository.findByUserIdAndMessageIdIn(userId, messageIds)
                .stream()
                .map(LikeMessages::getMessageId)
                .collect(Collectors.toSet());
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
                likeTipPostRepository::delete
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
                likeAnswerRepository::delete
        );
    }

    /* 3. 메시지 추천 토글 */
    @Transactional
    public LikeResponse toggleLikeMessage(Long userId, Long messageId) {
        return toggleLike(
                userId,
                () -> messageRepository.findById(messageId)
                        .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.MESSAGE_NOT_FOUND)),
                () -> likeMessageRepository.findById(new UserMessageId(userId, messageId)).orElse(null),
                () -> likeMessageRepository.save(new LikeMessages(userId, messageId)),
                likeMessageRepository::delete
        );
    }
}
