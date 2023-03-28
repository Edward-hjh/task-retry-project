package com.edward.strategy;

import com.edward.constant.HandleResultEnum;
import com.edward.dto.HandleResultDTO;
import com.edward.entity.TaskRetryLog;
import com.edward.mq.RocketMQProducer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.edward.constant.Source.ROCKET_MQ;

/**
 * @Author: hejh
 * @Date: 2023/3/28 15:43
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
@Component
public class RocketMQRetryStrategy extends AbstractTaskRetryStrategy{

    @Resource
    private RocketMQProducer rocketMQProducer;


    @Override
    public int getTaskSource() {
        return ROCKET_MQ;
    }

    /**
     * 消息异步发送
     *
     * @param taskRetryLog
     * @return
     */
    @Override
    public HandleResultDTO doHandleTask(TaskRetryLog taskRetryLog) {
        String context = taskRetryLog.getContext();
        //source不为10说明其需要组装数据
        if (ROCKET_MQ != taskRetryLog.getSource()) {
            context = assembleMessage(taskRetryLog);
        }
        boolean sendResult = rocketMQProducer.sendMsg(taskRetryLog.getTopic(), taskRetryLog.getMsgKey(), context);
        return HandleResultDTO.builder()
            .result(sendResult ? HandleResultEnum.SUCCESS.getValue() : HandleResultEnum.FAIL.getValue())
            .taskRetryLog(taskRetryLog)
            .build();
    }

    /**
     * 组装message
     *
     * @param taskRetryLog
     * @return
     */
    public String assembleMessage(TaskRetryLog taskRetryLog) {
        throw new RuntimeException("Not implement.");
    }

}
