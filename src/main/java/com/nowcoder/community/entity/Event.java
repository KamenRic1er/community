package com.nowcoder.community.entity;

import java.util.HashMap;
import java.util.Map;

public class Event {


    // todo
    //  将消息体使用md5算法获得唯一标识，在对消息进行处理前，将这个md5值放入redis，消息开始消费的时候将该值删除
    //  每当消费者对其进行消费的时候，首先在Redis中查询消息的唯一表示是否存在于Redis中，这样就可以避免重复消费

    // 触发主题
    private String topic;
    // 触发事件的用户的Id
    private int userId;
    // 触发的实体类型（点赞评论关注）
    private int entityType;
    // 触发的实体Id（帖子ID/评论ID/用户ID）
    private int entityId;
    private int entityUserId;
    private Map<String, Object> data = new HashMap<>();

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

}
