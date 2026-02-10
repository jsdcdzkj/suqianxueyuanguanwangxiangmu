package com.jsdc.iotpt.common.logaop;

import com.jsdc.iotpt.enums.LogEnums;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogInfo {

    LogEnums value();

    String model() default "";

}
