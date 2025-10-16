import { TipPostResponse, CommentResponse } from '@/types/community/tip';

const URL: string = 'https://image.utoimage.com/preview/cp872722/2022/12/202212008462_500.jpg';

export const dummyPosts: TipPostResponse[] = [
  {
    id: 1,
    author: { id: 101, nickname: "Alice", profileImage: "https://placekitten.com/50/50" },
    title: "첫 번째 팁",
    content: "이건 더미 내용입니다. 화면 레이아웃 확인용으로 작성했어요.",
    thumbnail: URL,
    commentCount: 3,
    likeCount: 5,
    liked: false,
  },
  {
    id: 2,
    author: { id: 102, nickname: "Bob", profileImage: "https://placekitten.com/51/51" },
    title: "두 번째 팁",
    content: "API 연결 전 임시 데이터입니다.",
    thumbnail: URL,
    commentCount: 0,
    likeCount: 2,
    liked: true,
  },
  {
    id: 3,
    author: { id: 103, nickname: "Carol", profileImage: "https://placekitten.com/52/52" },
    title: "세 번째 팁",
    content: "UI 테스트용 더미 글입니다.",
    thumbnail: URL,
    commentCount: 1,
    likeCount: 0,
    liked: false,
  },
];

export const dummyComments: CommentResponse[] = [
  {
      id: 1,
      postId: 1,
      author: {
          id: 1,
          nickname: "Alice",
          profileImage: "https://i.pravatar.cc/150?img=1",
      },
      content: "좋은 글 감사합니다!",
      thumbnail: "",
      likeCount: 5,
      deleted: false,
      replies: [
          {
              id: 2,
              postId: 1,
              author: {
                  id: 2,
                  nickname: "Bob",
                  profileImage: "https://i.pravatar.cc/150?img=2",
              },
              content: "저도 공감합니다!",
              thumbnail: "",
              likeCount: 2,
              deleted: false,
              replies: [],
          },
      ],
  },
  {
      id: 3,
      postId: 1,
      author: {
          id: 3,
          nickname: "Charlie",
          profileImage: "https://i.pravatar.cc/150?img=3",
      },
      content: "조금 다른 의견이 있습니다.",
      thumbnail: "",
      likeCount: 1,
      deleted: false,
      replies: [],
  },
  {
      id: 4,
      postId: 2,
      author: {
          id: 4,
          nickname: "David",
          profileImage: "https://i.pravatar.cc/150?img=4",
      },
      content: "이 글 정말 도움이 되었어요.",
      thumbnail: "",
      likeCount: 3,
      deleted: false,
      replies: [],
  },
];