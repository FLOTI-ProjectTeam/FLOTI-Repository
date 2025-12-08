import { ChallengeSummaryResponse, ChallengeDetailResponse } from '@/types/community/challenge';

export const DUMMY_CHALLENGES: ChallengeSummaryResponse[] = [
    {
        id: 1,
        title: '7일 동안 하루 30분 독서 챌린지',
        description: '매일 30분씩 책을 읽으며 꾸준한 습관을 만들어봐요!',
        startDate: '2025-01-01',
        endDate: '2025-02-01',
        participantCount: 3,
        maxParticipants: 10,
        status: 'IN_PROGRESS',
        tags: ['독서'],
        myProgress: 0
    },
    {
        id: 2,
        title: '4주 영어 단어 암기 챌린지',
        description: '매일 20개씩 단어를 외우며 어휘력을 늘려봐요!',
        startDate: '2025-01-01',
        endDate: '2025-02-01',
        participantCount: 3,
        maxParticipants: 10,
        status: 'IN_PROGRESS',
        tags: ['영어', '공부'],
        myProgress: 0
    },
    {
        id: 3,
        title: '한 달 동안 매일 문제 풀이 챌린지',
        description: '수학, 영어, 논술 등 원하는 과목 문제를 매일 풀어요!',
        startDate: '2025-01-01',
        endDate: '2025-02-01',
        participantCount: 3,
        maxParticipants: 10,
        status: 'IN_PROGRESS',
        tags: ['문제풀이', '공부'],
        myProgress: 0
    },
    {
        id: 4,
        title: '30일 플랭크 도전! 코어 강화 챌린지',
        description: '하루 1분부터 시작하는 플랭크 루틴 도전!',
        startDate: '2025-01-01',
        endDate: '2025-02-01',
        participantCount: 3,
        maxParticipants: 10,
        status: 'IN_PROGRESS',
        tags: ['운동', '플랭크'],
        myProgress: 0
    }
];

export const DUMMY_CHALLENGE_DETAIL: ChallengeDetailResponse = {
    ...DUMMY_CHALLENGES[0],
    content: '• 목표: 하루 30분 이상 독서 후 인증\n• 인증 방식: 피드에 읽은 책 & 느낀 점 공유하기',
    myProgress: 15,
    totalProgress: 30
};

export const DUMMY_FEEDS = [
    {
        id: 1,
        author: { nickname: 'user1', profileImage: null },
        content: `📌 [DAY 1] 독서 완료!\n📕 『아주 작은 습관의 힘』 - 1장 (p.1~30) 읽음\n책에서 "습관이 우리 정체성을 만든다"는 말이 인상적이었어요. 그동안 큰...`,
        createdAt: '2024-12-01T10:00:00Z',
        likeCount: 5,
        liked: true
    },
    {
        id: 2,
        author: { nickname: 'user2', profileImage: null },
        content: `📚 오늘 읽은 책: 『미라클 모닝』\n아침을 어떻게 보내느냐가 중요하다는 걸 깨달았어요!\n여러분도 오늘 30분 독서 챌린지 완료하셨나요? 어떤 책 읽으셨나요? 함께...`,
        createdAt: '2024-12-01T12:00:00Z',
        likeCount: 2,
        liked: false
    }
];
