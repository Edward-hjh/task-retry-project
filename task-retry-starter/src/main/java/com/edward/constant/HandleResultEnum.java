package com.edward.constant;

/**
 * 任务处理结果枚举
 * @Author: hejh
 * @Date: 2023/3/28 15:32
 */
public enum HandleResultEnum {
    /**
     * 执行成功
     */
    SUCCESS(1, "执行成功"),
    /**
     * 执行失败
     */
    FAIL(0, "执行失败"),
    /**
     * 异步等待
     */
    sync(2, "异步等待"),
    ;

    private final Integer value;
    private final String desc;

    private HandleResultEnum(Integer value, String desc) {
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
