package com.floti.api.domain.board.qna.entity;

import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

public class QQnaPosts extends EntityPathBase<QnaPosts> {
    public static final QQnaPosts qnaPost = new QQnaPosts("qnaPost");

    public final NumberPath<Long> id = createNumber("id", Long.class);
    public final StringPath title = createString("title");
    public final StringPath content = createString("content");

    public QQnaPosts(String variable) {
        super(QnaPosts.class, PathMetadataFactory.forVariable(variable));
    }
}
