package com.edward.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.edward.config.TaskRetryConfig;
import com.edward.constant.TaskRetryLogStatusEnum;
import com.edward.entity.TaskRetryLog;
import com.edward.mapper.TaskRetryLogMapper;
import com.edward.strategy.AbstractTaskRetryStrategy;
import com.edward.strategy.RetryContext;
import com.edward.utils.EnvUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: hejh
 * @Date: 2023/3/28 15:41
 */
@Slf4j
public class TaskBaseService {
    @Resource
    TaskRetryConfig taskRetryConfig;
    @Resource
    TaskRetryLogMapper taskRetryLogMapper;

    @Resource
    EnvUtil envUtil;
    @Resource
    RetryContext retryContext;

    /**
     * 重发定时任务
     *
     * @param ids 记录ID,为空即查询需要推送的、最近三天的数据
     */
    public void retryJob(List<Long> ids) {
        LambdaQueryWrapper<TaskRetryLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TaskRetryLog::getProjectName, envUtil.getApplicationName());
        if (CollectionUtils.isEmpty(ids)) {
            queryWrapper.le(TaskRetryLog::getRetryCount, taskRetryConfig.getTaskMaxRetry());
            queryWrapper.in(TaskRetryLog::getStatus, TaskRetryLogStatusEnum.INIT.getValue(), TaskRetryLogStatusEnum.RETRYING.getValue());
            queryWrapper.le(TaskRetryLog::getNextSendTime, LocalDateTime.now());
        } else {
            queryWrapper.in(TaskRetryLog::getId, ids);
        }
        queryWrapper.last(String.format("limit %s", taskRetryConfig.getTaskLimit()));
        List<TaskRetryLog> taskRetryLogs = taskRetryLogMapper.selectList(queryWrapper);
        taskRetryLogs.forEach(item -> {
            AbstractTaskRetryStrategy taskRetryStrategy = retryContext.getTaskMap().get(item.getSource());
            taskRetryStrategy.executeTask(item);
        });
        log.info("taskRetryJobHandler is executed. Parameters：{}", ids);
    }

    /**
     * 删除表前 KEEP_DAY 天历史数据
     */
    public void removeHistory() {
        LambdaQueryWrapper<TaskRetryLog> queryWrapper = new QueryWrapper<TaskRetryLog>().lambda();
        LocalDateTime leTime = LocalDateTime.now().minusDays(taskRetryConfig.getDataKeptDay());
        queryWrapper.lt(TaskRetryLog::getCreateTime, leTime)
            .eq(TaskRetryLog::getProjectName, envUtil.getApplicationName())
            .in(TaskRetryLog::getStatus, TaskRetryLogStatusEnum.TASK_SUCCESS.getValue())
            .orderByAsc(TaskRetryLog::getCreateTime)
            .last("limit 100");
        int delete = taskRetryLogMapper.delete(queryWrapper);
        log.info("MqResendJobHandler is executed. Delete {}records", delete);
    }

}
