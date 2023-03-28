package com.edward.config;

import com.edward.job.RetryJobService;
import com.edward.job.TaskBaseService;
import com.edward.mq.RocketMQProducer;
import com.edward.strategy.RetryContext;
import com.edward.strategy.RocketMQRetryStrategy;
import com.edward.utils.EnvUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author: hejh
 * @Date: 2023/3/28 15:18
 */
@Import({RocketMQRetryStrategy.class, EnvUtil.class, RocketMQProducer.class})
@Configuration
@ConditionalOnProperty(prefix = "task-retry", name = "enable", havingValue = "true")
@EnableConfigurationProperties(TaskRetryConfig.class)
@MapperScan(basePackages = "com.akulaku.common.task.retry.starter.mapper")
public class TaskRetryAutoConfiguration {

    @Resource
    private TaskRetryConfig taskRetryConfig;

    @Bean
    @ConditionalOnProperty(prefix = "task-retry", name = "job", havingValue = "true")
    public RetryJobService resendJobService() {
        return new RetryJobService();
    }

    @Bean
    public TaskBaseService jobBaseService() {
        return new TaskBaseService();
    }

    @Bean
    public RetryContext resendContext() {
        return new RetryContext();
    }

    /**
     * 任务重试线程池
     * @return
     */
    @Bean(name = "taskRetryExecutor")
    public Executor getTaskRetryExecutor() {
        ThreadPoolTaskExecutor taskRetryExecutor = new ThreadPoolTaskExecutor();
        taskRetryExecutor.setCorePoolSize(taskRetryConfig.getThreadPoolParam().getCorePoolSize());
        taskRetryExecutor.setMaxPoolSize(taskRetryConfig.getThreadPoolParam().getMaxPoolSize());
        taskRetryExecutor.setQueueCapacity(taskRetryConfig.getThreadPoolParam().getQueueCapacity());
        taskRetryExecutor.setKeepAliveSeconds(taskRetryConfig.getThreadPoolParam().getKeepAliveSeconds());
        taskRetryExecutor.setThreadNamePrefix("task-retry-pool-");
        //拒绝策略
        taskRetryExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskRetryExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskRetryExecutor.initialize();
        return taskRetryExecutor;
    }

}
