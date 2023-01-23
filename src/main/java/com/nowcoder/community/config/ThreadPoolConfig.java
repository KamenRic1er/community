package com.nowcoder.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
// 使注解Async生效，使用该注解的方法可以在多线程的环境下被异步的调用
@EnableAsync
public class ThreadPoolConfig {
}
