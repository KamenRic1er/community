package com.nowcoder.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;


@Data
@TableName("like_record")
@Builder
public class LikeRecord {

    @TableId(type = IdType.AUTO)
    private Integer recordId;

    private Integer userId;

    private Integer postId;

    private Integer likeOrNot;

}