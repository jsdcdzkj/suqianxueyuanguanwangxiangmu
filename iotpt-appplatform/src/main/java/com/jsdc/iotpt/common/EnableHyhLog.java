package com.jsdc.iotpt.common;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 *  开启日志注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({HyhLogAutoConfiguration.class})
public @interface EnableHyhLog {
}
