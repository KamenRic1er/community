package com.nowcoder.community;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

/**
 * @author: 少不入川
 * @since: 2023/10/30 17:31
 */
@Getter
public enum LikeRecordError implements IEnum<String> {
    // 在这里添加枚举值，例如：
    LIKE_RECORD_NOT_FOUND("点赞记录未找到"),
    LIKE_RECORD_ALREADY_EXISTS("点赞记录已存在")
    ;

    private String message;

    LikeRecordError(String message) {
        this.message = message;
    }

    @Override
    public String getValue() {
        return this.message;
    }
}
