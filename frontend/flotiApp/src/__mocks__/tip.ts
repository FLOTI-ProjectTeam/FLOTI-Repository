import { TipPostResponse, CommentResponse } from '@/types/community/tip';

const URL: string = 'https://image.utoimage.com/preview/cp872722/2022/12/202212008462_500.jpg';

export const dummyPosts: TipPostResponse[] = [
  {
    id: 1,
    author: { username: "alice", nickname: "Alice", profileImage: null },
    title: "첫 번째 팁",
    content: "이건 더미 내용입니다. **화면 레이아웃 확인용**으로 작성했어요.",
    thumbnail: URL,
    commentCount: 3,
    likeCount: 5,
    liked: false,
    createdAt: "2025-10-25T09:12:00",
  },
  {
    id: 2,
    author: { username: "bob", nickname: "Bob", profileImage: null },
    title: "두 번째 팁",
    content: "API 연결 전 *임시 데이터*입니다.",
    thumbnail: null,
    commentCount: 0,
    likeCount: 2,
    liked: true,
    createdAt: "2025-10-24T18:47:00",
  },
  {
    id: 3,
    author: { username: "carol", nickname: "Carol", profileImage: null },
    title: "세 번째 팁",
    content: `UI ~~테스트용~~ 더미 글입니다.
> 인용
- 리스트
[링크](https://www.google.com)`,
    thumbnail: URL,
    commentCount: 1,
    likeCount: 0,
    liked: false,
    createdAt: "2025-10-23T13:22:00",
  },
];

export const dummyComments: CommentResponse[] = [
  {
    id: 1,
    postId: 1,
    parentId: null,
    author: { username: "alice", nickname: "Alice", profileImage: null },
    content: "좋은 글 감사합니다!",
    deleted: false,
    createdAt: "2025-10-24T10:15:00",
    replies: [
      {
        id: 2,
        postId: 1,
        parentId: 1,
        author: { username: "bob", nickname: "Bob", profileImage: null },
        content: "저도 공감합니다!",
        deleted: false,
        createdAt: "2025-10-25T10:20:00",
        replies: []
      },
    ],
  },
  {
    id: 3,
    postId: 1,
    parentId: null,
    author: { username: "charlie", nickname: "Charlie", profileImage: null },
    content: "조금 다른 의견이 있습니다.",
    deleted: false,
    createdAt: "2025-10-25T15:42:00",
    replies: [],
  },
  {
    id: 4,
    postId: 3,
    parentId: null,
    author: { username: "david", nickname: "David", profileImage: null },
    content: "이 글 정말 도움이 되었어요.",
    deleted: false,
    createdAt: "2025-10-23T12:30:00",
    replies: [],
  },
];