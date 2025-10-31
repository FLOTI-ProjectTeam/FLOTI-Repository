package com.floti.api.domain.board.discussion.entity;

import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

import java.time.LocalDateTime;

public class QDiscussionRooms extends EntityPathBase<DiscussionRooms> {
    public static final QDiscussionRooms discussionRoom = new QDiscussionRooms("discussionRoom");

    public final NumberPath<Long> id = createNumber("id", Long.class);
    public final StringPath title = createString("title");
    public final StringPath intro = createString("intro");
    public final DateTimePath<LocalDateTime> recentActivityAt
            = createDateTime("recentActivityAt", LocalDateTime.class);

    public QDiscussionRooms(String variable) {
        super(DiscussionRooms.class, PathMetadataFactory.forVariable(variable));
    }
}
