package com.nowcoder.community.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
@EnableScheduling
@EnableAsync
public class ThreadPoolConfig {

    // 如果serviceA、serviceB对象之间相互依赖，serviceA和serviceB总一个一个会先实例化，而serviceA或serviceB里面使用了@Async注解，会导致循环依赖异常：
    // org.springframework.beans.factory.BeanCurrentlyInCreationException
    // 解决办法：
    // 1.在A类上加@Lazy，保证A对象实例化晚于B对象
    // 2.不使用@Async注解，通过自定义异步工具类发起异步线程（线程池）
    // 3.不要让@Async的Bean参与循环依赖

    @Bean
    public ThreadPoolTaskExecutor executor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(100);
        taskExecutor.setQueueCapacity(50);
        taskExecutor.setKeepAliveSeconds(200);
        taskExecutor.setThreadNamePrefix("async-");
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.initialize();

        return taskExecutor;
    }

}
