package com.floti.api.domain.board.tip.entity;

import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

public class QTipPosts extends EntityPathBase<TipPosts> {
    public static final QTipPosts tipPost = new QTipPosts("tipPost");

    public final NumberPath<Long> id = createNumber("id", Long.class);
    public final StringPath title = createString("title");
    public final StringPath content = createString("content");
    public final NumberPath<Integer> likeCount = createNumber("likeCount", Integer.class);

    public QTipPosts(String variable) {
        super(TipPosts.class, PathMetadataFactory.forVariable(variable));
    }
}
