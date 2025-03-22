package com.jzo2o.market.config;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jzo2o.redis.properties.RedisSyncProperties;

@Configuration
public class ThreadPoolConfiguration {
    @Bean("syncThreadPool")
    public ThreadPoolExecutor synchronizeThreadPool(RedisSyncProperties redisSyncProperties) {
        // 定义线程池参数
        int corePoolSize = 1; // 核心线程数
        int maxPoolSize = redisSyncProperties.getQueueNum();// 最大线程数
        long keepAliveTime = 120;// 线程空闲时间
        TimeUnit unit = TimeUnit.SECONDS;// 时间单位
        // 指定拒绝策略为 DiscardPolicy
        RejectedExecutionHandler rejectedHandler = new ThreadPoolExecutor.DiscardPolicy();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize,maxPoolSize,keepAliveTime,unit,new SynchronousQueue<>(),rejectedHandler);
        return threadPoolExecutor;
    }
}
