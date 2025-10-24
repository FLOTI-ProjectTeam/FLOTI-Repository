package com.floti.api.domain.board.discussion.service;

import com.floti.api.domain.auth.entity.User;
import com.floti.api.domain.board.common.dto.AuthorResponse;
import com.floti.api.domain.board.discussion.dto.DiscussionPostDetailResponse;
import com.floti.api.domain.board.discussion.dto.DiscussionPostRequest;
import com.floti.api.domain.board.discussion.dto.DiscussionPostResponse;
import com.floti.api.domain.board.discussion.dto.MessageResponse;
import com.floti.api.domain.board.discussion.entity.DiscussionParticipants;
import com.floti.api.domain.board.discussion.entity.DiscussionPosts;
import com.floti.api.domain.board.discussion.entity.Messages;
import com.floti.api.domain.board.discussion.entity.PostParticipantId;
import com.floti.api.domain.board.discussion.repository.DiscussionParticipantRepository;
import com.floti.api.domain.board.discussion.repository.DiscussionPostRepository;
import com.floti.api.domain.board.discussion.repository.MessageRepository;
import com.floti.api.domain.like.service.LikeService;
import com.floti.api.error.ExceptionMessage;
import com.floti.api.error.exception.MaxParticipantExceededException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DiscussionPostService {
    private final DiscussionPostRepository discussionPostRepository;
    private final MessageRepository messageRepository;
    private final DiscussionParticipantRepository discussionParticipantRepository;
    private final LikeService likeService;

    private static final int PAGE_SIZE = 20;

    /* 1-1. 조회 */
    public Page<DiscussionPostResponse> getDiscussionPosts(String sort, int page) {
        Sort sortOrder = switch (sort.toLowerCase()) {
            case "registered" -> Sort.by("id").ascending();
            default -> Sort.by("id").descending();
        };

        Pageable pageable = PageRequest.of(page, PAGE_SIZE, sortOrder);
        Page<DiscussionPosts> discussionPostPage = discussionPostRepository.findAll(pageable);
        return discussionPostPage.map(DiscussionPostResponse::new);
    }

    /* 1-2. 검색 */
    public Page<DiscussionPostResponse> searchDiscussionPosts(String search, String sort, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<DiscussionPosts> discussionPostPage = discussionPostRepository.searchDiscussionPosts(search, sort, pageable);
        return discussionPostPage.map(DiscussionPostResponse::new);
    }

    /* 2. 상세 조회 */
    public DiscussionPostDetailResponse getDiscussionPost(Long userId, Long id) {
        DiscussionPosts discussionPost = discussionPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.POST_NOT_FOUND));

        /* 참여자 리스트 */
        List<DiscussionParticipants> discussionParticipants = discussionParticipantRepository.findByPostId(id);
        List<AuthorResponse> participantResponses = discussionParticipants.stream()
                .map(dp -> new AuthorResponse(dp.getParticipant()))
                .toList();

        /* 메시지별 추천 여부 */
        // 1. 모든 메시지 ID
        List<Messages> messages = messageRepository.findTop50ByPostIdOrderByIdDesc(id);
        List<Long> messageIds = messages.stream().map(Messages::getId).toList();

        // 2. 사용자가 추천한 메시지 ID
        Set<Long> likedMessageIds = likeService.getLikedMessageIds(userId, messageIds);

        List<MessageResponse> messageResponses = messages.stream()
                .map(m -> new MessageResponse(m, likedMessageIds.contains(m.getId())))
                .toList();

        return new DiscussionPostDetailResponse(discussionPost, participantResponses, messageResponses);
    }

    /* 3. 등록 */
    @Transactional
    public DiscussionPostResponse createDiscussionPost(User user, DiscussionPostRequest request) {
        DiscussionPosts DiscussionPost = DiscussionPosts.builder()
                .author(user)
                .title(request.getTitle())
                .intro(request.getIntro())
                .build();

        return new DiscussionPostResponse(discussionPostRepository.save(DiscussionPost));
    }

    /* 4. 수정 */
    @Transactional
    public DiscussionPostResponse updateDiscussionPost(Long userId, Long id, DiscussionPostRequest request) {
        DiscussionPosts discussionPost = discussionPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.POST_NOT_FOUND));

        if (!userId.equals(discussionPost.getAuthor().getId()))
            throw new AccessDeniedException(ExceptionMessage.UPDATE_DENIED);

        discussionPost.update(request.getTitle(), request.getIntro(), request.getMaxParticipants());
        return new DiscussionPostResponse(discussionPost);
    }

    /* 5. 삭제 */
    @Transactional
    public void deleteDiscussionPost(Long userId, Long id) {
        DiscussionPosts discussionPost = discussionPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.POST_NOT_FOUND));

        if (!userId.equals(discussionPost.getAuthor().getId()))
            throw new AccessDeniedException(ExceptionMessage.DELETE_DENIED);

        discussionPostRepository.delete(discussionPost);
    }

    /* 6. 참여 토글 */
    @Transactional
    public void toggleJoinDiscussion(User user, Long postId) {
        DiscussionPosts discussionPost = discussionPostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.POST_NOT_FOUND));
        Long userId = user.getId();

        if (discussionPost.getAuthor().getId().equals(userId))
            throw new AccessDeniedException(ExceptionMessage.HOST_CANNOT_LEAVE);

        Optional<DiscussionParticipants> discussionParticipant =
                discussionParticipantRepository.findById(new PostParticipantId(postId, userId));

        if (discussionParticipant.isPresent()) {
            discussionParticipantRepository.delete(discussionParticipant.get());
            discussionPost.decrementParticipantCount();
        } else {
            if (discussionPost.getParticipantCount() == discussionPost.getMaxParticipants())
                throw new MaxParticipantExceededException();

            discussionParticipantRepository.save(new DiscussionParticipants(discussionPost, user));
            discussionPost.incrementParticipantCount();
        }
    }
}
