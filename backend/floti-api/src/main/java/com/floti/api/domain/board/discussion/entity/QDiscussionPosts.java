package com.floti.api.domain.board.discussion.entity;

import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

public class QDiscussionPosts extends EntityPathBase<DiscussionPosts> {
    public static final QDiscussionPosts discussionPost = new QDiscussionPosts("discussionPost");

    public final NumberPath<Long> id = createNumber("id", Long.class);
    public final StringPath title = createString("title");
    public final StringPath intro = createString("intro");

    public QDiscussionPosts(String variable) {
        super(DiscussionPosts.class, PathMetadataFactory.forVariable(variable));
    }
}
