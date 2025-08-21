package com.floti.api.domain.like.service;

import com.floti.api.domain.auth.repository.UserRepository;
import com.floti.api.domain.like.dto.LikeResponse;
import com.floti.api.domain.board.common.entity.UserPostId;
import com.floti.api.domain.like.entity.LikeTipPosts;
import com.floti.api.domain.board.tip.entity.TipPosts;
import com.floti.api.domain.like.repository.LikeTipPostRepository;
import com.floti.api.domain.board.tip.repository.TipPostRepository;
import com.floti.api.error.PostNotFoundException;
import com.floti.api.error.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final TipPostRepository tipPostRepository;
    private final UserRepository userRepository;
    private final LikeTipPostRepository likeTipPostRepository;

    /* 1. Tip 게시판 추천 토글 */
    @Transactional
    public LikeResponse toggleLikeTipPost(Long userId, Long id) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException();

        TipPosts tipPost = tipPostRepository.findById(id).orElseThrow(PostNotFoundException::new);

        LikeTipPosts likeTipPost = likeTipPostRepository.findById(new UserPostId(userId, id)).orElse(null);
        boolean liked = (likeTipPost == null);

        if (liked) {
            likeTipPostRepository.save(new LikeTipPosts(userId, id));
            tipPost.incrementLikeCount();
        } else {
            likeTipPostRepository.delete(likeTipPost);
            tipPost.decrementLikeCount();
        }

        return new LikeResponse(liked, tipPost.getLikeCount());
    }
}
