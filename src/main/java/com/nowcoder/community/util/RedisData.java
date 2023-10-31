package com.nowcoder.community.util;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author: 少不入川
 * @since: 2023/10/30 9:34
 */
@Data
public class RedisData {
    private LocalDateTime expireTime;
    private Object data;
}
