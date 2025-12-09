SET NAMES utf8mb4;

CREATE TABLE users (
	user_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	email VARCHAR(320) NOT NULL UNIQUE,
	username VARCHAR(10) NOT NULL UNIQUE,
	password CHAR(60) NOT NULL,
	nickname VARCHAR(15) NOT NULL,
	profile_image VARCHAR(50), -- profile/아이디.확장자
	last_login DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE refresh_tokens (
	token_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	user_id BIGINT UNSIGNED NOT NULL,
	refresh_token VARCHAR(255) NOT NULL UNIQUE,
	expires_at DATETIME NOT NULL,
	FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 태그 관련 --
CREATE TABLE tags (
	tag_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	tag_name VARCHAR(15) NOT NULL UNIQUE
);

CREATE TABLE post_tags (
	board_type TINYINT UNSIGNED CHECK (board_type BETWEEN 1 AND 4), -- ENUM (TIP: 1, CHALLENGE: 2, DISCUSSION: 3, QNA: 4)
	post_id BIGINT UNSIGNED, -- 직접 삭제
	tag_id BIGINT UNSIGNED,
	PRIMARY KEY (board_type, post_id, tag_id),
	FOREIGN KEY (tag_id) REFERENCES tags(tag_id)
);

CREATE INDEX idx_tag_id_board_type_post_id ON post_tags(tag_id, board_type, post_id); -- 태그 검색

CREATE TABLE user_tags (
	user_id BIGINT UNSIGNED NOT NULL,
	tag_id BIGINT UNSIGNED NOT NULL,
	PRIMARY KEY (user_id, tag_id),
	FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
	FOREIGN KEY (tag_id) REFERENCES tags(tag_id)
);

-- TIP 게시판 관련 --
CREATE TABLE tip_posts (
	post_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	author_id BIGINT UNSIGNED NOT NULL,
	title VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    thumbnail VARCHAR(65), -- tip/thumbnail/날짜_UUID.확장자
    comment_count INT UNSIGNED DEFAULT 0,
    like_count INT UNSIGNED DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE INDEX idx_author_id_post_id ON tip_posts(author_id, post_id); -- 사용자 TIP 게시글 조회*
CREATE INDEX idx_comment_count_post_id ON tip_posts(comment_count, post_id); -- 댓글순 조회*
CREATE INDEX idx_like_count_post_id ON tip_posts(like_count, post_id); -- 좋아요순 조회

CREATE TABLE like_tip_posts (
	user_id BIGINT UNSIGNED NOT NULL,
	post_id BIGINT UNSIGNED NOT NULL,
	PRIMARY KEY (user_id, post_id),
	FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
	FOREIGN KEY (post_id) REFERENCES tip_posts(post_id) ON DELETE CASCADE
);

CREATE TABLE comments (
	comment_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	post_id BIGINT UNSIGNED NOT NULL, -- 직접 삭제
	parent_id BIGINT UNSIGNED,
	author_id BIGINT UNSIGNED NOT NULL,
	content VARCHAR(255) NOT NULL,
	like_count INT UNSIGNED DEFAULT 0,
	created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (author_id) REFERENCES users(user_id) ON DELETE CASCADE,
	FOREIGN KEY (post_id) REFERENCES tip_posts(post_id) ON DELETE CASCADE
);

CREATE INDEX idx_author_id_comment_id ON comments(author_id, comment_id); -- 게시판별 사용자 댓글 조회**
CREATE INDEX idx_post_id_comment_id ON comments(post_id, comment_id); -- 게시글별 댓글 조회

CREATE TABLE like_comments (
	user_id BIGINT UNSIGNED NOT NULL,
	comment_id BIGINT UNSIGNED NOT NULL,
	PRIMARY KEY (user_id, comment_id),
	FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
	FOREIGN KEY (comment_id) REFERENCES comments(comment_id) ON DELETE CASCADE
);

-- Q&A 게시판 관련 --
CREATE TABLE qna_posts (
	post_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	author_id BIGINT UNSIGNED NOT NULL,
	title VARCHAR(100) NOT NULL,
	content TEXT NOT NULL,
	answer_count INT UNSIGNED DEFAULT 0,
	is_completed TINYINT DEFAULT 0, -- 0 = FALSE, 1 = TRUE
	created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (author_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE INDEX idx_author_id_post_id ON qna_posts(author_id, post_id); -- 사용자 Q&A 게시글 조회*
CREATE INDEX idx_answer_count_post_id ON qna_posts(answer_count, post_id); -- 답변순 조회
CREATE INDEX idx_is_completed_post_id ON qna_posts(is_completed, post_id); -- 완료 여부 조회

CREATE TABLE answers (
	answer_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	post_id BIGINT UNSIGNED NOT NULL, -- 직접 삭제
	author_id BIGINT UNSIGNED NOT NULL,
	content VARCHAR(255) NOT NULL,
	like_count INT UNSIGNED DEFAULT 0,
	is_accepted TINYINT DEFAULT 0, -- 0 = FALSE, 1 = TRUE
	created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (author_id) REFERENCES users(user_id) ON DELETE CASCADE,
	FOREIGN KEY (post_id) REFERENCES qna_posts(post_id) ON DELETE CASCADE
);

CREATE INDEX idx_author_id_answer_id ON answers(author_id, answer_id); -- 게시판별 사용자 답변 조회**
CREATE INDEX idx_post_id_answer_id ON answers(post_id, answer_id); -- 게시글별 답변 조회

CREATE TABLE like_answers (
	user_id BIGINT UNSIGNED NOT NULL,
	answer_id BIGINT UNSIGNED NOT NULL,
	PRIMARY KEY (user_id, answer_id),
	FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
	FOREIGN KEY (answer_id) REFERENCES answers(answer_id) ON DELETE CASCADE
);

-- 챌린지 게시판 관련 --
CREATE TABLE challenge_posts (
	post_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	author_id BIGINT UNSIGNED NOT NULL,
	title VARCHAR(100) NOT NULL,
	intro VARCHAR(255) NOT NULL,
	content VARCHAR(255) NOT NULL,
	max_participants TINYINT UNSIGNED CHECK (max_participants BETWEEN 2 AND 10),
	current_participants TINYINT UNSIGNED DEFAULT 1,
	start_date DATETIME NOT NULL,
	end_date DATETIME NOT NULL,
	is_completed TINYINT DEFAULT 0, -- 0 = FALSE, 1 = TRUE
	created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (author_id) REFERENCES users(user_id) ON DELETE RESTRICT
);

CREATE INDEX idx_author_id_post_id ON challenge_posts(author_id, post_id); -- 사용자 챌린지 게시글 조회*
CREATE INDEX idx_is_completed_post_id ON challenge_posts(is_completed, post_id); -- 완료 여부 조회

CREATE TABLE challenge_participants (
	challenge_id BIGINT UNSIGNED NOT NULL,
	participant_id BIGINT UNSIGNED NOT NULL,
	progress TINYINT UNSIGNED CHECK (progress BETWEEN 0 AND 100),
	contribution INT UNSIGNED DEFAULT 0,
	PRIMARY KEY (challenge_id, participant_id),
	FOREIGN KEY (challenge_id) REFERENCES challenge_posts(post_id) ON DELETE CASCADE,
	FOREIGN KEY (participant_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE INDEX idx_challenge_id_contribution ON challenge_participants(challenge_id, contribution); -- 공헌도순 조회
CREATE INDEX idx_participant_id_challenge_id ON challenge_participants(participant_id, challenge_id); -- 참여 챌린지 조회

CREATE TABLE feeds (
	feed_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	challenge_id BIGINT UNSIGNED NOT NULL,
	author_id BIGINT UNSIGNED NOT NULL,
	content VARCHAR(255) NOT NULL,
	created_at DATETIME DEFAULT CURRENT_TIMESTAMP,  -- 작성 시간
	FOREIGN KEY (challenge_id) REFERENCES challenge_posts(post_id) ON DELETE CASCADE,
	FOREIGN KEY (author_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE INDEX idx_participant_id_feed_id ON feeds(challenge_id, feed_id); -- 챌린지별 피드 조회
CREATE INDEX idx_author_id_feed_id ON feeds(author_id, feed_id); -- 사용자 피드 조회*

-- 토론 게시판 관련 --
CREATE TABLE discussion_posts (
	post_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	author_id BIGINT UNSIGNED NOT NULL,
	title VARCHAR(100) NOT NULL,
	intro VARCHAR(255) NOT NULL,
	max_participants TINYINT UNSIGNED CHECK (max_participants BETWEEN 2 AND 5),
	current_participants TINYINT UNSIGNED DEFAULT 1,
	created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (author_id) REFERENCES users(user_id) ON DELETE RESTRICT
);

CREATE INDEX idx_author_id_post_id ON discussion_posts(author_id, post_id); -- 사용자 토론 게시글 조회*

CREATE TABLE discussion_participants (
	discussion_id BIGINT UNSIGNED NOT NULL,
	participant_id BIGINT UNSIGNED NOT NULL,
	PRIMARY KEY (discussion_id, participant_id),
	FOREIGN KEY (discussion_id) REFERENCES discussion_posts(post_id) ON DELETE CASCADE,
	FOREIGN KEY (participant_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE INDEX idx_participant_id_discussion_id ON discussion_participants(participant_id, discussion_id); -- 참여 토론방 조회

CREATE TABLE messages (
	message_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	discussion_id BIGINT UNSIGNED NOT NULL,
	author_id BIGINT UNSIGNED,
	content VARCHAR(255) NOT NULL,
	like_count TINYINT UNSIGNED DEFAULT 0,
	created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (author_id) REFERENCES users(user_id) ON DELETE SET NULL
);

CREATE INDEX idx_created_at ON messages(discussion_id, created_at); -- 토론방별 메시지 조회

CREATE TABLE like_messages (
	message_id BIGINT UNSIGNED NOT NULL,
	user_id BIGINT UNSIGNED NOT NULL,
	PRIMARY KEY (message_id, user_id),
	FOREIGN KEY (message_id) REFERENCES messages(message_id) ON DELETE CASCADE,
	FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 초기 데이터 (DUMMY DATA) --

-- 1. Users
INSERT INTO users (email, username, password, nickname) VALUES 
('user1@example.com', 'user1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'user1'), 
('user2@example.com', 'user2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'user2'),
('user3@example.com', 'user3', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'user3');

-- 2. Tags
INSERT INTO tags (tag_name) VALUES ('독서'), ('영어'), ('공부'), ('운동'), ('플랭크'), ('문제풀이');

-- 3. Challenge Posts
-- 챌린지 1
INSERT INTO challenge_posts (author_id, title, intro, content, max_participants, current_participants, start_date, end_date, is_completed) 
VALUES (1, '7일 동안 하루 30분 독서 챌린지', '매일 30분씩 책을 읽으며 꾸준한 습관을 만들어봐요!', '• 목표: 하루 30분 이상 독서 후 인증\n• 인증 방식: 피드에 읽은 책 & 느낀 점 공유하기', 10, 3, '2025-01-01 00:00:00', '2025-02-01 00:00:00', 0);
-- 태그 연결 (Board Type 2: Challenge)
INSERT INTO post_tags (board_type, post_id, tag_id) VALUES (2, 1, (SELECT tag_id FROM tags WHERE tag_name = '독서'));

-- 챌린지 2
INSERT INTO challenge_posts (author_id, title, intro, content, max_participants, current_participants, start_date, end_date, is_completed) 
VALUES (1, '4주 영어 단어 암기 챌린지', '매일 20개씩 단어를 외우며 어휘력을 늘려봐요!', '하루 20개씩 영단어 암기!', 10, 1, '2025-01-01 00:00:00', '2025-02-01 00:00:00', 0);
INSERT INTO post_tags (board_type, post_id, tag_id) VALUES 
(2, 2, (SELECT tag_id FROM tags WHERE tag_name = '영어')),
(2, 2, (SELECT tag_id FROM tags WHERE tag_name = '공부'));

-- 챌린지 3
INSERT INTO challenge_posts (author_id, title, intro, content, max_participants, current_participants, start_date, end_date, is_completed) 
VALUES (1, '30일 플랭크 도전! 코어 강화 챌린지', '하루 1분부터 시작하는 플랭크 루틴 도전!', '플랭크로 코어를 튼튼하게!', 10, 1, '2025-01-01 00:00:00', '2025-02-01 00:00:00', 0);
INSERT INTO post_tags (board_type, post_id, tag_id) VALUES 
(2, 3, (SELECT tag_id FROM tags WHERE tag_name = '운동')),
(2, 3, (SELECT tag_id FROM tags WHERE tag_name = '플랭크'));

-- 4. Participants (챌린지 1에 3명 모두 참여)
INSERT INTO challenge_participants (challenge_id, participant_id, progress, contribution) VALUES
(1, 1, 15, 10),
(1, 2, 10, 5),
(1, 3, 0, 0),
(2, 1, 0, 0),
(3, 1, 0, 0);

-- 5. Feeds (챌린지 1에 대한 피드)
INSERT INTO feeds (challenge_id, author_id, content, created_at) VALUES
(1, 2, '📚 오늘 읽은 책: 『미라클 모닝』\n아침을 어떻게 보내느냐가 중요하다는 걸 깨달았어요!\n여러분도 오늘 30분 독서 챌린지 완료하셨나요?', '2024-12-01 12:00:00');

-- 챌린지 4 (user1이 참여하지 않은 챌린지)
INSERT INTO challenge_posts (author_id, title, intro, content, max_participants, current_participants, start_date, end_date, is_completed) 
VALUES (2, '1일 1커밋 챌린지', '매일매일 깃허브에 잔디를 심어봐요!', '개발자의 기본은 꾸준함! 하루에 한 번 커밋하기.', 5, 1, '2025-12-01 00:00:00', '2026-01-01 00:00:00', 0);
INSERT INTO post_tags (board_type, post_id, tag_id) VALUES (2, 4, (SELECT tag_id FROM tags WHERE tag_name = '공부'));
INSERT INTO challenge_participants (challenge_id, participant_id, progress, contribution) VALUES (4, 2, 0, 0);