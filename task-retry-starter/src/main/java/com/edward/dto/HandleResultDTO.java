package com.edward.dto;

import com.edward.constant.HandleResultEnum;
import com.edward.entity.TaskRetryLog;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: hejh
 * @Date: 2023/3/28 15:36
 */
@Builder
@Data
public class HandleResultDTO implements Serializable {

    private static final long serialVersionUID = -8250181129129770570L;
    /**
     * 任务重试记录实体
     */
    private TaskRetryLog taskRetryLog;
    /**
     * 处理结果，1-成功，0-失败，2-异步等待 {@link HandleResultEnum}
     */
    private Integer result;
    /**
     * 失败原因
     */
    private String reason;

}
