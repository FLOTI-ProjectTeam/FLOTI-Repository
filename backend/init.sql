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
    comment_count INT UNSIGNED NOT NULL DEFAULT 0,
    like_count INT UNSIGNED NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE INDEX idx_author_id_post_id ON tip_posts(author_id, post_id); -- 사용자 TIP 게시글 조회**
CREATE INDEX idx_like_count_post_id ON tip_posts(like_count, post_id); -- 좋아요순 조회

CREATE TABLE like_tip_posts (
	user_id BIGINT UNSIGNED,
	post_id BIGINT UNSIGNED,
	PRIMARY KEY (user_id, post_id),
	FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
	FOREIGN KEY (post_id) REFERENCES tip_posts(post_id) ON DELETE CASCADE
);

CREATE TABLE comments (
	comment_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	post_id BIGINT UNSIGNED NOT NULL,
	parent_id BIGINT UNSIGNED,
	author_id BIGINT UNSIGNED, -- 직접 삭제
	content VARCHAR(200) NOT NULL,
	like_count INT UNSIGNED NOT NULL DEFAULT 0,
    is_deleted TINYINT NOT NULL DEFAULT 0, -- 0 = FALSE, 1 = TRUE
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES tip_posts(post_id) ON DELETE CASCADE,
	FOREIGN KEY (author_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE INDEX idx_author_id_comment_id ON comments(author_id, comment_id); -- 게시판별 사용자 댓글 조회**
CREATE INDEX idx_post_id_comment_id ON comments(post_id, comment_id); -- 게시글별 댓글 조회


-- Q&A 게시판 관련 --
CREATE TABLE qna_posts (
	post_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	author_id BIGINT UNSIGNED NOT NULL,
	title VARCHAR(100) NOT NULL,
	content TEXT NOT NULL,
	answer_count INT UNSIGNED NOT NULL DEFAULT 0,
	is_accepted TINYINT NOT NULL DEFAULT 0, -- 0 = FALSE, 1 = TRUE
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (author_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE INDEX idx_author_id_post_id ON qna_posts(author_id, post_id); -- 사용자 Q&A 게시글 조회**
CREATE INDEX idx_is_accepted_answer_count_post_id ON qna_posts(is_accepted, answer_count, post_id); -- 답변순 조회

CREATE TABLE answers (
	answer_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	post_id BIGINT UNSIGNED NOT NULL,
	author_id BIGINT UNSIGNED,
	content VARCHAR(1000) NOT NULL,
	like_count INT UNSIGNED NOT NULL DEFAULT 0,
	is_accepted TINYINT NOT NULL DEFAULT 0, -- 0 = FALSE, 1 = TRUE
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES qna_posts(post_id) ON DELETE CASCADE,
	FOREIGN KEY (author_id) REFERENCES users(user_id)
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
	intro VARCHAR(50) NOT NULL,
	content VARCHAR(255) NOT NULL,
	max_participants TINYINT UNSIGNED CHECK (max_participants BETWEEN 2 AND 10),
	current_participants TINYINT UNSIGNED DEFAULT 1,
	start_date DATETIME NOT NULL,
	end_date DATETIME NOT NULL,
	is_completed TINYINT DEFAULT 0, -- 0 = FALSE, 1 = TRUE
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
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
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 작성 시간
	FOREIGN KEY (challenge_id) REFERENCES challenge_posts(post_id) ON DELETE CASCADE,
	FOREIGN KEY (author_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE INDEX idx_participant_id_feed_id ON feeds(challenge_id, feed_id); -- 챌린지별 피드 조회
CREATE INDEX idx_author_id_feed_id ON feeds(author_id, feed_id); -- 사용자 피드 조회*

-- 토론 게시판 관련 --
CREATE TABLE discussion_rooms (
	room_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	author_id BIGINT UNSIGNED NOT NULL,
	title VARCHAR(100) NOT NULL,
	intro VARCHAR(255) NOT NULL,
	max_participants TINYINT UNSIGNED CHECK (max_participants BETWEEN 2 AND 5),
	participant_count TINYINT UNSIGNED DEFAULT 1,
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    recent_activity_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 직접 갱신
	FOREIGN KEY (author_id) REFERENCES users(user_id) ON DELETE RESTRICT
);

CREATE INDEX idx_author_id_room_id ON discussion_rooms(author_id, room_id); -- 사용자 토론방 조회*
CREATE INDEX idx_recent_activity_at_room_id ON discussion_rooms(recent_activity_at, room_id); -- 최근활동순 조회

CREATE TABLE discussion_participants (
    room_id BIGINT UNSIGNED NOT NULL,
	participant_id BIGINT UNSIGNED NOT NULL,
	PRIMARY KEY (room_id, participant_id),
	FOREIGN KEY (room_id) REFERENCES discussion_rooms(room_id) ON DELETE CASCADE,
	FOREIGN KEY (participant_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE INDEX idx_participant_id_room_id ON discussion_participants(participant_id, room_id); -- 참여 토론방 조회

CREATE TABLE messages (
	message_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	room_id BIGINT UNSIGNED NOT NULL,
	author_id BIGINT UNSIGNED,
	content VARCHAR(255) NOT NULL,
	like_count TINYINT UNSIGNED DEFAULT 0,
	created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (room_id) REFERENCES discussion_rooms(room_id) ON DELETE CASCADE,
FOREIGN KEY (author_id) REFERENCES users(user_id) ON DELETE SET NULL
);

CREATE INDEX idx_room_id_created_at ON messages(room_id, created_at); -- 토론방별 메시지 조회

CREATE TABLE like_messages (
	message_id BIGINT UNSIGNED NOT NULL,
	user_id BIGINT UNSIGNED NOT NULL,
	PRIMARY KEY (message_id, user_id),
	FOREIGN KEY (message_id) REFERENCES messages(message_id) ON DELETE CASCADE,
	FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);