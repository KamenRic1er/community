package com.nowcoder.community.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Message {

    private int id;
    // formId中要注意id=1为系统消息
    private int fromId;
    private int toId;
    private String conversationId;
    private String content;
    private int status;
    private Date createTime;

}
