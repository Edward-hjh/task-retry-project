package com.edward.utils;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: hejh
 * @Date: 2023/3/28 15:45
 */
@Component
public class EnvUtil {
    private static final String SPRING_APPLICATION_NAME = "spring.application.name";

    @Resource
    Environment environment;

    public String getApplicationName() {
        return environment.getProperty(SPRING_APPLICATION_NAME);
    }
}
