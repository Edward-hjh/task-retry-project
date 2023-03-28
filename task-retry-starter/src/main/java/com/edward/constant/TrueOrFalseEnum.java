package com.edward.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @Author: hejh
 * @Date: 2023/3/28 15:36
 */
@AllArgsConstructor
@Getter
public enum TrueOrFalseEnum {
    /**
     * 是否枚举
     */
    FALSE(0, "否"),
    TRUE(1, "是");

    private final Integer value;
    private final String desc;

    public static TrueOrFalseEnum of(Integer value) {
        return Arrays.stream(TrueOrFalseEnum.values()).filter(trueOrFalseEnum -> trueOrFalseEnum.getValue().equals(value))
            .findFirst().orElse(TrueOrFalseEnum.FALSE);
    }
}
