package com.edward.constant;

/**
 * @Author: hejh
 * @Date: 2023/3/28 15:35
 */
public enum TaskRetryLogStatusEnum {
    /**
     * 执行中
     */
    INIT(0, "执行中"),
    /**
     * 重试中
     */
    RETRYING(1, "重试中"),
    /**
     * 任务执行失败
     */
    TASK_FAIL(2, "任务执行失败"),

    /**
     * 任务执行成功
     */
    TASK_SUCCESS(3, "任务执行成功");

    /**
     * 状态值
     */
    private final Integer value;
    /**
     * 描述
     */
    private final String desc;

    TaskRetryLogStatusEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public Integer getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
