import { TipPostResponse, CommentResponse } from '@/types/community/tip';

const URL: string = 'https://image.utoimage.com/preview/cp872722/2022/12/202212008462_500.jpg';

export const dummyPosts: TipPostResponse[] = [
  {
    id: 1,
    author: { id: 101, nickname: "Alice", profileImage: null },
    title: "첫 번째 팁",
    content: "이건 더미 내용입니다. 화면 레이아웃 확인용으로 작성했어요.",
    thumbnail: URL,
    commentCount: 3,
    likeCount: 5,
    liked: false,
    createdAt: "2025.10.25 09:12",
  },
  {
    id: 2,
    author: { id: 102, nickname: "Bob", profileImage: null },
    title: "두 번째 팁",
    content: "API 연결 전 임시 데이터입니다.",
    thumbnail: URL,
    commentCount: 0,
    likeCount: 2,
    liked: true,
    createdAt: "2025.10.24 18:47",
  },
  {
    id: 3,
    author: { id: 103, nickname: "Carol", profileImage: null },
    title: "세 번째 팁",
    content: "UI 테스트용 더미 글입니다.",
    thumbnail: URL,
    commentCount: 1,
    likeCount: 0,
    liked: false,
    createdAt: "2025.10.23 13:22",
  },
];

export const dummyComments: CommentResponse[] = [
  {
    id: 1,
    postId: 1,
    parentId: null,
    author: { id: 1, nickname: "Alice", profileImage: null },
    content: "좋은 글 감사합니다!",
    deleted: false,
    createdAt: "2025.10.24 10:15",
    replies: [
      {
        id: 2,
        postId: 1,
        parentId: 1,
        author: { id: 2, nickname: "Bob", profileImage: null },
        content: "저도 공감합니다!",
        deleted: false,
        createdAt: "2025.10.25 10:20",
        replies: []
      },
    ],
  },
  {
    id: 3,
    postId: 1,
    parentId: null,
    author: { id: 3, nickname: "Charlie", profileImage: null },
    content: "조금 다른 의견이 있습니다.",
    deleted: false,
    createdAt: "2025.10.25 15:42",
    replies: [],
  },
  {
    id: 4,
    postId: 3,
    parentId: null,
    author: { id: 4, nickname: "David", profileImage: null },
    content: "이 글 정말 도움이 되었어요.",
    deleted: false,
    createdAt: "2025.10.23 12:30",
    replies: [],
  },
];