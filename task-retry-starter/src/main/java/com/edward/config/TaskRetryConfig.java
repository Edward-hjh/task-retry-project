package com.edward.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: hejh
 * @Date: 2023/3/28 14:47
 */
@Data
@Component
@ConfigurationProperties(prefix = "task-retry")
public class TaskRetryConfig {
    /**
     * 定时任务每次获取重试条数
     */
    private Integer taskLimit = 50;
    /**
     * 数据保留天数
     */
    private Integer dataKeptDay = 7;
    /**
     * 最大重试次数
     */
    private Integer taskMaxRetry = 5;

    /**
     * 第一次重试间隔秒数
     */
    private Integer firstExecuteSecond = 5;
    /**
     * 第二次后的重试间隔基数
     * 间隔秒数 = delayBaseSecond * delayMultiplier^($sendIndex - 1)
     * 比如delayBaseSecond=10，delayMultiplier=10，第三次的间隔时间为：10 * 10^(3-1)=1000s
     */
    private Integer delayBaseSecond = 10;
    /**
     * 第二次后的重试间隔倍数
     */
    private Integer delayMultiplier = 10;
    /**
     * 线程池参数
     */
    private ThreadPoolParam threadPoolParam = new ThreadPoolParam();

    @Data
    public static class ThreadPoolParam {
        /**
         * 核心线程数
         */
        private int corePoolSize = 3;
        /**
         * 最大线程数
         */
        private int maxPoolSize = 50;
        /**
         * 空闲线程存活时间,seconds
         */
        private int keepAliveSeconds = 60;
        /**
         * 队列容量
         */
        private int queueCapacity = 10000;
    }
}
