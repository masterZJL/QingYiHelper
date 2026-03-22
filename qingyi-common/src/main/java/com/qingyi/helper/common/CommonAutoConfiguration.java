package com.qingyi.helper.common;

import com.qingyi.helper.common.exception.GlobalExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 公共模块自动配置
 */
@Configuration
public class CommonAutoConfiguration {

    // Exception handlers are registered via @RestControllerAdvice in GlobalExceptionHandler
}
