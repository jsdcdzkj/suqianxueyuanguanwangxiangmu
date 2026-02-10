package com.jsdc.iotpt.common;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HyhLog {
    /**
     * 前置描述
     *
     * @return
     */
    String before() default "";

    /**
     *  后置描述
     * @return
     */
    String after() default "";

}
