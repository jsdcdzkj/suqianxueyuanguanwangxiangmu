package com.jsdc.iotpt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置类  配合
 * <code>
 *     <--密码安全加密-->
 *         <dependency>
 *             <groupId>org.springframework.boot</groupId>
 *             <artifactId>spring-boot-starter-security</artifactId>
 *         </dependency>
 *         <dependency>
 *             <groupId>org.bouncycastle</groupId>
 *             <artifactId>bcprov-jdk18on</artifactId>
 *             <version>1.78</version>
 *         </dependency>
 *         <--密码安全加密-->
 * </code>
 * 使用 如何开启这 Spring Security 那么Spring Security默认开启CSRF（跨站请求伪造）防护，要求所有非GET、HEAD、TRACE、OPTIONS请求必须携带CSRF令牌
 *  。前端要添加 vue
 *  <code>
 *      // main.js
 * import axios from 'axios';
 *
 * // 自动从 Cookie 读取 XSRF-TOKEN 并添加到请求头
 * axios.defaults.xsrfCookieName  = 'XSRF-TOKEN'; // Cookie 名称
 * axios.defaults.xsrfHeaderName  = 'X-XSRF-TOKEN'; // 请求头名称
 * axios.defaults.withCredentials  = true; // 允许跨域携带 Cookie
 *  </code>
 * 存储CSRF 令牌
 *
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // 关闭CSRF
                .authorizeRequests(auth -> auth
                        .anyRequest().permitAll() // 允许所有请求无需认证
                );
        return http.build();
    }
}