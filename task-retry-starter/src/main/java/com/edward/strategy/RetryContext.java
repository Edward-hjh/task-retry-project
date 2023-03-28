package com.edward.strategy;

import com.edward.dto.TaskDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: hejh
 * @Date: 2023/3/28 15:42
 */
@Slf4j
public class RetryContext {

    /**
     * 任务处理策略集合
     */
    private final Map<Integer, AbstractTaskRetryStrategy> enumTaskMap = new ConcurrentHashMap<>(8);

    /**
     * 任务处理策略集合
     *
     * @param taskRetryStrategyMap bean名 --> 任务处理策略bean
     */
    @Autowired
    public void setTaskMap(Map<String, AbstractTaskRetryStrategy> taskRetryStrategyMap) {
        if (taskRetryStrategyMap == null) {
            return;
        }
        taskRetryStrategyMap.forEach((key, value) -> enumTaskMap.put(value.getTaskSource(), value));
    }

    /**
     * 获取ResendBaseProducer生产者集合
     *
     * @return SysMqResendLogSourceEnum --> AbstractResendProducer
     */
    public Map<Integer, AbstractTaskRetryStrategy> getTaskMap() {
        return enumTaskMap;
    }

    /**
     * 处理任务策略方法
     *
     * @param taskDTO
     */
    public void handlerTask(TaskDTO taskDTO) {
        if (Objects.isNull(taskDTO.getSource()) || Objects.isNull(enumTaskMap.get(taskDTO.getSource()))) {
            log.warn("task handler source is null");
            return;
        }
        enumTaskMap.get(taskDTO.getSource()).saveAndExecute(taskDTO);
    }

}
