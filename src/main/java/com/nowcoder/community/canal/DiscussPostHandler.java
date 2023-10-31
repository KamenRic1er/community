package com.nowcoder.community.canal;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.ElasticsearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

/**
 * @author: 少不入川
 * @since: 2023/10/31 9:03
 * https://blog.csdn.net/qq_48649411/article/details/125802516
 */
@Slf4j
@CanalTable("discuss_post")
@Component
public class DiscussPostHandler implements EntryHandler<DiscussPost> {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Override
    public void insert(DiscussPost discussPost) {
        log.debug("监听到插入操作，同步到ES中......");
        elasticsearchService.saveDiscussPost(discussPost);
    }

    @Override
    public void update(DiscussPost before, DiscussPost after) {
        log.debug("监听到更新操作，同步到ES中......");
        elasticsearchService.saveDiscussPost(after);
    }

    @Override
    public void delete(DiscussPost discussPost) {
        log.debug("监听到删除操作，同步到ES中......");
        elasticsearchService.deleteDiscussPost(discussPost.getId());
    }
}
