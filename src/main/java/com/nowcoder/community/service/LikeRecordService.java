package com.nowcoder.community.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nowcoder.community.dao.LikeRecordMapper;
import com.nowcoder.community.entity.LikeRecord;
import org.springframework.stereotype.Service;

/**
 * @author: 少不入川
 * @since: 2023/10/25 13:27
 */
@Service
public class LikeRecordService extends ServiceImpl<LikeRecordMapper, LikeRecord> {

    public void like(int userId, int postId){
        LikeRecord likeRecord = getOne(lambdaQuery().eq(LikeRecord::getUserId, userId).eq(LikeRecord::getPostId,
                postId));
        if(ObjectUtil.isNull(likeRecord)){
            likeRecord = LikeRecord.builder()
                    .userId(userId)
                    .postId(postId)
                    .yesOrNo(1)
                    .build();
            save(likeRecord);
            return;
        }
        if(likeRecord.getYesOrNo() == 1){
            lambdaUpdate().eq(LikeRecord::getUserId, userId).eq(LikeRecord::getPostId, postId).set(LikeRecord::getYesOrNo
                    , 0);
        }else{
            lambdaUpdate().eq(LikeRecord::getUserId, userId).eq(LikeRecord::getPostId, postId).set(LikeRecord::getYesOrNo
                    , 1);
        }
    }

}
