package com.floti.api.domain.auth.entity;

import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

import java.time.LocalDateTime;

/**
 * QueryDSL에서 User 엔티티를 타입 안전하게 참조하기 위한 수동 Q 클래스.
 * 다른 도메인에서 사용자 ID나 닉네임 등을 조건으로 사용할 때 이용된다.
 */
public class QUser extends EntityPathBase<User> {
    public static final QUser user = new QUser("user");

    /** 기본 키 */
    public final NumberPath<Long> id = createNumber("id", Long.class);
    /** 이메일 */
    public final StringPath email = createString("email");
    /** 아이디(사용자명) */
    public final StringPath username = createString("username");
    /** 비밀번호 */
    public final StringPath password = createString("password");
    /** 닉네임 */
    public final StringPath nickname = createString("nickname");
    /** 프로필 이미지 경로 */
    public final StringPath profileImage = createString("profileImage");
    /** 마지막 로그인 시각 */
    public final DateTimePath<LocalDateTime> lastLogin = createDateTime("lastLogin", LocalDateTime.class);

    public QUser(String variable) {
        super(User.class, PathMetadataFactory.forVariable(variable));
    }
}
