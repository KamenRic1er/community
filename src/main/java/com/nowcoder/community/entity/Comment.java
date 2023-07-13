package com.nowcoder.community.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Comment {

    private int id;
    private int userId;
    // 被评论类型（帖子/回复）
    private int entityType;
    // 被评论id
    private int entityId;
    private int targetId;
    private String content;
    private int status;
    private Date createTime;

}
