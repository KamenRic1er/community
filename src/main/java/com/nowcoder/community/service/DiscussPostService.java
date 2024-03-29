package com.nowcoder.community.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.util.CommonUtil;
import com.nowcoder.community.util.RedisKeyUtil;
import com.nowcoder.community.util.SensitiveFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    private static final long POST_CACHE_EXPIRE_TIME = 180;
    @Value("${caffeine.posts.max-size}")
    private int maxSize;
    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;
    // Caffeine核心接口: Cache, LoadingCache, AsyncLoadingCache
    // 帖子列表缓存类
    private LoadingCache<String, List<DiscussPost>> postListCache;
    // 帖子总数缓存类
    private LoadingCache<Integer, Integer> postRowsCache;
    @PostConstruct
    public void init() {
        // 初始化帖子列表缓存
        postListCache = Caffeine.newBuilder()
                // 基于空间的驱逐策略：设置缓存最大容量
                .maximumSize(maxSize)
                // 基于时间的驱逐策略：在最后一次写入缓存后开始计时，在指定的时间（180s）后过期。
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                // 当本地缓存未命中的时候，就会调用与LoadingCache关联的CacheLoader中的load方法生成V
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Nullable
                    @Override
                    public List<DiscussPost> load(@NonNull String key) throws Exception {
                        if (StringUtils.isNotBlank(key)) {
                            throw new IllegalArgumentException("参数错误!");
                        }
                        String[] params = key.split(":");
                        if (params == null || params.length != 2) {
                            throw new IllegalArgumentException("参数错误!");
                        }

                        int offset = Integer.parseInt(params[0]);
                        int limit = Integer.parseInt(params[1]);

                        // 二级缓存: Redis -> mysql
                        log.debug("load post list from DB.");
                        return discussPostMapper.selectDiscussPosts(0, offset, limit, 1);
                    }
                });
        // 初始化帖子总数缓存
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer load(@NonNull Integer key) throws Exception {
                        log.debug("load post rows from DB.");
                        return discussPostMapper.selectDiscussPostRows(key);
                    }
                });
    }


    /**
     * 使用Redis+Caffeine缓存DiscussPost
     * 1.优先从缓存中取值
     * 2.取不到时从Redis中取
     * 3.Redis中无则初始化缓存数据到Redis+Caffeine
     * */
    private DiscussPost getDiscussPostFromRedis(int postId) {
        String redisKey = RedisKeyUtil.getPostKey(postId);
        return (DiscussPost) redisTemplate.opsForValue().get(redisKey);
    }
    private DiscussPost initRedisCache(int postId) {
        DiscussPost post = discussPostMapper.selectDiscussPostById(postId);
        String redisKey = RedisKeyUtil.getPostKey(postId);
        redisTemplate.opsForValue().set(redisKey, post, CommonUtil.getRandomExpireTime(POST_CACHE_EXPIRE_TIME),
                TimeUnit.SECONDS);
        return post;
    }

    @Cacheable(cacheNames = "post", key = "#id", cacheManager = "postCacheManager")
    public DiscussPost findDiscussPostById(int id) {
        DiscussPost post = getDiscussPostFromRedis(id);
        if(post == null){
            log.debug("\n\n--------------- load post " + id +" from DB ----------------\n\n");
            post = initRedisCache(id);
        }else {
            log.debug("\n\n--------------- load post " + id +" from Redis ----------------\n\n");
        }
        return post;
    }

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit, int orderMode) {
        if (userId == 0 && orderMode == 1) {
            return postListCache.get(offset + ":" + limit);
        }
        log.debug("load post list from DB.");
        return discussPostMapper.selectDiscussPosts(userId, offset, limit, orderMode);
    }

    public int findDiscussPostRows(int userId) {
        if (userId == 0) {
            return postRowsCache.get(userId);
        }
        log.debug("load post rows from DB.");
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public int addDiscussPost(DiscussPost post) {
        if (post == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        // 转义HTML标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        // 过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    @Transactional
    @CachePut(cacheNames = "post", key = "#id", cacheManager = "postCacheManager")
    public DiscussPost updateCommentCount(int id, int commentCount) {
        discussPostMapper.updateCommentCount(id, commentCount);

        DiscussPost post = discussPostMapper.selectDiscussPostById(id);
        String redisKey = RedisKeyUtil.getPostKey(id);
        redisTemplate.opsForValue().set(redisKey, post, CommonUtil.getRandomExpireTime(POST_CACHE_EXPIRE_TIME),
                TimeUnit.SECONDS);
        return post;
    }

    @Transactional
    @CachePut(cacheNames = "post", key = "#id", cacheManager = "postCacheManager")
    public DiscussPost updateType(int id, int type) {
        discussPostMapper.updateType(id, type);
        DiscussPost post = discussPostMapper.selectDiscussPostById(id);
        String redisKey = RedisKeyUtil.getPostKey(id);
        redisTemplate.opsForValue().set(redisKey, post, CommonUtil.getRandomExpireTime(POST_CACHE_EXPIRE_TIME),
                TimeUnit.SECONDS);
        return post;
    }

    @Transactional
    @CachePut(cacheNames = "post", key = "#id", cacheManager = "postCacheManager")
    public DiscussPost updateStatus(int id, int status) {
        discussPostMapper.updateStatus(id, status);
        DiscussPost post = discussPostMapper.selectDiscussPostById(id);
        String redisKey = RedisKeyUtil.getPostKey(id);
        redisTemplate.opsForValue().set(redisKey, post, CommonUtil.getRandomExpireTime(POST_CACHE_EXPIRE_TIME),
                TimeUnit.SECONDS);
        return post;
    }

    @Transactional
    @CachePut(cacheNames = "post", key = "#id", cacheManager = "postCacheManager")
    public DiscussPost updateScore(int id, double score) {
        discussPostMapper.updateScore(id, score);
        DiscussPost post = discussPostMapper.selectDiscussPostById(id);
        String redisKey = RedisKeyUtil.getPostKey(id);
        redisTemplate.opsForValue().set(redisKey, post, CommonUtil.getRandomExpireTime(POST_CACHE_EXPIRE_TIME),
                TimeUnit.SECONDS);
        return post;
    }

}
