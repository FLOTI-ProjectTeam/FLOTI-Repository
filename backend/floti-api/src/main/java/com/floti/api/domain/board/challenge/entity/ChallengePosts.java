package com.floti.api.domain.board.challenge.entity;

import com.floti.api.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 챌린지 게시글을 나타내는 엔티티이다.
 * 기존 Q&A 게시판과 TIP 게시판처럼 작성자, 제목, 내용 등의 기본 정보를 포함하며
 * 추가로 최대 참여 인원, 현재 참여 인원, 시작/종료 날짜, 완료 여부를 저장한다.
 */
@Entity
@Getter
@Table(name = "challenge_posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChallengePosts {
    /**
     * 게시글 ID (기본 키). MySQL의 AUTO_INCREMENT와 매핑된다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    /**
     * 작성자. users 테이블과 외래키로 연결된다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    /**
     * 제목. 최대 100자.
     */
    @Column(nullable = false, length = 100)
    private String title;

    /**
     * 한 줄 소개. 검색과 리스트 노출 시 사용된다. 최대 50자.
     */
    @Column(nullable = false, length = 50)
    private String intro;

    /**
     * 챌린지 상세 설명. 최대 255자. 실제 비즈니스 요구에 따라 필요 시 TEXT로 변경할 수 있다.
     */
    @Column(nullable = false, length = 255)
    private String content;

    /**
     * 최대 참가 인원. 2~10명 사이의 값을 갖는다.
     */
    @Column(name = "max_participants", nullable = false)
    private int maxParticipants;

    /**
     * 현재 참가 인원. 작성자가 자동으로 1명으로 포함되며 참가/탈퇴 시 갱신된다.
     */
    @Column(name = "current_participants", nullable = false)
    private int currentParticipants;

    /**
     * 챌린지 시작일.
     */
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    /**
     * 챌린지 종료일.
     */
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    /**
     * 챌린지 종료 여부. 종료일이 지났거나 완료 처리 시 true로 설정된다.
     */
    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted;

    /**
     * 게시글 생성 시간. @PrePersist로 자동 채워진다.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 생성자. Lombok의 @Builder를 통해 필요한 필드만 받아 초기화하며
     * 작성자를 포함하여 현재 참가 인원 수를 1로 초기화한다.
     */
    @Builder
    public ChallengePosts(User author, String title, String intro, String content,
                          int maxParticipants, LocalDateTime startDate, LocalDateTime endDate) {
        this.author = author;
        this.title = title;
        this.intro = intro;
        this.content = content;
        this.maxParticipants = maxParticipants;
        this.currentParticipants = 1; // 작성자는 기본적으로 챌린지에 참여하므로 1로 시작
        this.startDate = startDate;
        this.endDate = endDate;
        this.isCompleted = false;
    }

    /**
     * 엔티티가 처음 persist 될 때 생성 시간을 현재 시각으로 채워준다.
     */
    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    /**
     * 챌린지 정보를 수정한다. 게시글 작성자는 제목, 소개, 내용, 최대 인원, 시작일, 종료일을 변경할 수 있다.
     * 현재 참가 인원보다 작은 인원으로는 변경할 수 없다.
     */
    public void update(String title, String intro, String content,
                       int maxParticipants, LocalDateTime startDate, LocalDateTime endDate) {
        this.title = title;
        this.intro = intro;
        this.content = content;
        this.maxParticipants = maxParticipants;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * 참가 인원 수를 1 증가시킨다. 최대 참가 인원보다 큰 값으로 증가하지 않도록 서비스 레이어에서 검증한다.
     */
    public void incrementParticipants() {
        this.currentParticipants++;
    }

    /**
     * 참가 인원 수를 1 감소시킨다. 음수가 되지 않도록 체크한다.
     */
    public void decrementParticipants() {
        if (this.currentParticipants > 0) {
            this.currentParticipants--;
        }
    }

    /**
     * 챌린지 상태를 완료로 변경한다.
     */
    public void complete() {
        this.isCompleted = true;
    }
}