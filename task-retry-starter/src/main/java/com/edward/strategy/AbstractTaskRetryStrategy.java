package com.edward.strategy;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.edward.config.TaskRetryConfig;
import com.edward.constant.HandleResultEnum;
import com.edward.constant.TaskRetryLogStatusEnum;
import com.edward.dto.HandleResultDTO;
import com.edward.dto.TaskDTO;
import com.edward.entity.TaskRetryLog;
import com.edward.mapper.TaskRetryLogMapper;
import com.edward.utils.EnvUtil;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @Author: hejh
 * @Date: 2023/3/28 15:42
 */
@Slf4j
public abstract class AbstractTaskRetryStrategy {
    @Resource
    private TaskRetryLogMapper taskRetryLogMapper;
    @Resource
    private TaskRetryConfig taskRetryConfig;
    @Resource(name = "taskRetryExecutor")
    private Executor taskRetryExecutor;

    @Resource
    private EnvUtil envUtil;

    /**
     * 获取任务来源，请确保每个任务对应一个来源
     * 自定义重试策略来源不能与默认来源{@link com.edward.constant.Source}重复
     *
     * @return int 返回消息来源枚举值
     */
    public abstract int getTaskSource();

    /**
     * 持久化到数据库并异步执行任务
     *
     * @param taskDTO
     */
    public void saveAndExecute(TaskDTO taskDTO) {
        TaskRetryLog taskRetryLog = new TaskRetryLog();
        taskRetryLog.setId(IdWorker.getId());
        taskRetryLog.setProjectName(envUtil.getApplicationName());
        taskRetryLog.setSource(taskDTO.getSource());
        taskRetryLog.setMsgKey(taskDTO.getMsgKey());
        taskRetryLog.setStatus(TaskRetryLogStatusEnum.INIT.getValue());
        taskRetryLog.setRetryCount(0);
        taskRetryLog.setTopic(taskDTO.getTopic());
        taskRetryLog.setContext(taskDTO.getContext());
        taskRetryLog.setCreateTime(LocalDateTime.now());
        taskRetryLogMapper.insert(taskRetryLog);

        CompletableFuture.runAsync(() -> executeTask(taskRetryLog), taskRetryExecutor);

    }

    public void executeTask(TaskRetryLog taskRetryLog) {
        HandleResultDTO handleResultDTO;
        try {
            handleResultDTO = doHandleTask(taskRetryLog);
        } catch (Exception e) {
            handleResultDTO = HandleResultDTO.builder()
                .result(HandleResultEnum.FAIL.getValue())
                .reason(e.getLocalizedMessage())
                .taskRetryLog(taskRetryLog)
                .build();
            log.error("Failed mission execution", e);
        }
        handleResult(handleResultDTO);
    }

    /**
     * 任务处理抽象方法
     *
     * @param taskRetryLog
     * @return
     */
    public abstract HandleResultDTO doHandleTask(TaskRetryLog taskRetryLog);

    /**
     * 处理任务执行结果，异步等待不处理
     *
     * @param handleResultDTO
     */
    public void handleResult(HandleResultDTO handleResultDTO) {
        if (Objects.isNull(handleResultDTO) || Objects.isNull(handleResultDTO.getResult()) || Objects.isNull(handleResultDTO.getTaskRetryLog())) {
            return;
        }
        TaskRetryLog taskRetryLog = handleResultDTO.getTaskRetryLog();
        //update DB
        TaskRetryLog updateEntity = new TaskRetryLog();
        if (HandleResultEnum.SUCCESS.getValue().equals(handleResultDTO.getResult())) {
            //执行成功
            if (log.isInfoEnabled()) {
                log.info("Task execution successful, id={}, data：{}", taskRetryLog.getId(), taskRetryLog);
            }
            updateEntity.setStatus(TaskRetryLogStatusEnum.TASK_SUCCESS.getValue());
        } else if (HandleResultEnum.FAIL.getValue().equals(handleResultDTO.getResult())) {
            //执行失败
            log.error("Task execution failed, id={}, data：{}， fail reason：{}", taskRetryLog.getId(), taskRetryLog, handleResultDTO.getReason());
            int resendCount = taskRetryLog.getRetryCount() + 1;
            if (taskRetryLog.getRetryCount() < taskRetryConfig.getTaskMaxRetry()) {
                updateEntity.setNextSendTime(getNextSendTime(resendCount));
                updateEntity.setStatus(TaskRetryLogStatusEnum.RETRYING.getValue());
                updateEntity.setRetryCount(resendCount);
            } else {
                updateEntity.setStatus(TaskRetryLogStatusEnum.TASK_FAIL.getValue());
            }
        } else {
            return;
        }
        updateEntity.setId(taskRetryLog.getId());
        taskRetryLogMapper.updateById(updateEntity);
    }

    /**
     * 获取下次执行时间
     * 公式：DELAY_SECOND * DELAY_MULTIPLIER^(${send_count}-1)
     *
     * @param sendCount 第几次发送
     * @return LocalDateTime下一次发送时间
     */
    public LocalDateTime getNextSendTime(double sendCount) {
        if (sendCount == 1) {
            return LocalDateTime.now().plusSeconds(taskRetryConfig.getFirstExecuteSecond());
        }
        double pow = Math.pow(taskRetryConfig.getDelayMultiplier(), sendCount - 1);
        return LocalDateTime.now().plusSeconds((long)(taskRetryConfig.getDelayBaseSecond() * pow));
    }

    public void updateMessageById(long id, String context) {
        TaskRetryLog updateEntity = new TaskRetryLog();
        updateEntity.setId(id);
        updateEntity.setContext(context);
        taskRetryLogMapper.updateById(updateEntity);
    }

}
