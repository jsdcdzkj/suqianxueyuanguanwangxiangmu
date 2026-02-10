package com.jsdc.iotpt.common;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName: HyhLogAutoConfiguration
 * Description:
 * date: 2022/6/13 14:41
 *
 * @author bn
 */
@ConditionalOnWebApplication
@Configuration(proxyBeanMethods = false)
public class HyhLogAutoConfiguration {

}
