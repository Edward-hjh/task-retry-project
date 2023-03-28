package com.edward.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author: hejh
 * @Date: 2023/3/28 15:20
 */
@TableName("t_task_retry_log")
@Data
@NoArgsConstructor
public class TaskRetryLog {
    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 项目名，项目名和source唯一确定一个记录
     */
    private String projectName;
    /**
     * 发送策略来源 {@link com.edward.constant.Source}
     */
    private Integer source;
    /**
     * 消息唯一标识
     */
    private String msgKey;
    /**
     * 任务状态 0=执行中 1=重试中 2=任务执行失败 3=任务执行成功 {@link com.edward.constant.TaskRetryLogStatusEnum}
     */
    private Integer status;
    /**
     * 重试次数
     */
    private Integer retryCount;
    /**
     * 消息topic
     */
    private String topic;
    /**
     * 上下文
     */
    private String context;
    /**
     * 下一次发送时间，实际执行时间取决于定时任务最大间隔
     */
    private LocalDateTime nextSendTime;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 最后更新时间
     */
    private LocalDateTime updateTime;
}
