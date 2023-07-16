package com.nowcoder.community.config;

import com.nowcoder.community.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import java.util.Objects;

// 配置 -> 数据库 -> 调用
@Configuration
public class QuartzConfig {

    // FactoryBean可简化Bean的实例化过程:
    // 1.通过FactoryBean封装Bean的实例化过程.
    // 2.将FactoryBean装配到Spring容器里.
    // 3.将FactoryBean注入给其他的Bean.
    // 4.该Bean得到的是FactoryBean所管理的对象实例.

    // https://blog.csdn.net/MinggeQingchun/article/details/126360682


    // Quartz中的Job类无法使用依赖注入： https://blog.csdn.net/fly_captain/article/details/84257781

    @Autowired
    private QuartzJobFactory quartzJobFactory;

    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setName("postScoreRefreshJob");
        factoryBean.setGroup("communityJobGroup");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    // SimpleTrigger要比CronTrigger简单，只需要设置简单的循环周期即可，而后者可以通过cron表达式实现定时任务
    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger() {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        // 关联任务
        factoryBean.setJobDetail(postScoreRefreshJobDetail().getObject());
        factoryBean.setName("postScoreRefreshTrigger");
        factoryBean.setGroup("communityTriggerGroup");
        factoryBean.setJobDataMap(new JobDataMap());
        // 设置执行周期
        factoryBean.setRepeatInterval(1000 * 60 * 5);
        return factoryBean;
    }

    @Bean  //中心配置
    SchedulerFactoryBean schedulerFactoryBean(){
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        // 自定义Job工厂，将由Quartz创建的Job类交给Spring管理，从而避免依赖注入失效
        schedulerFactoryBean.setJobFactory(quartzJobFactory);
        schedulerFactoryBean.setTriggers(postScoreRefreshTrigger().getObject());
        return schedulerFactoryBean;
    }

}
