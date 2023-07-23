package com.nowcoder.community.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

/**
 * @Author: 少不入川
 * @Date: 2023/7/15 21:56
 */
@Configuration
public class CacheConfig {


    // 默认CacheManager: 如果未指定cacheManager则使用该配置
    @Primary
    @Bean
    public CacheManager cacheManager(){
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(128)
                .maximumSize(256)
                .expireAfterWrite(60, TimeUnit.SECONDS));
        return cacheManager;
    }


    @Bean
    public CacheManager userCacheManager(){
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("user");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(200)
                .maximumSize(500)
                .expireAfterWrite(60, TimeUnit.SECONDS));
        return cacheManager;
    }

    @Bean
    public CacheManager postCacheManager(){
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("post");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(250)
                .expireAfterWrite(180, TimeUnit.SECONDS));
        return cacheManager;
    }

}
