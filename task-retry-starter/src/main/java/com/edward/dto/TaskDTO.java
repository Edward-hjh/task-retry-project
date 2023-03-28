package com.edward.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 任务执行DTO
 * @Author: hejh
 * @Date: 2023/3/28 15:37
 */
@Data
public class TaskDTO implements Serializable {
    /**
     * 发送策略来源 10-mq
     */
    private Integer source;
    /**
     * 上下文
     */
    private String context;
    /**
     * topic
     */
    private String topic;
    /**
     * 消息key
     */
    private String msgKey;

}
