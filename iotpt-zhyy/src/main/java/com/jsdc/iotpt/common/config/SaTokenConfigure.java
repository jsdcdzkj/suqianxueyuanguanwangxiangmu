package com.jsdc.iotpt.common.config;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.interceptor.SaRouteInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.temp.SaTempUtil;
import com.alibaba.fastjson.JSONObject;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.ResultInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName SaTokenConfig
 * @Description TODO
 * @Author xujian
 * @Date 2022/3/29 16:09
 * @Version 1.0
 */
@Configuration
public class SaTokenConfigure  implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册路由拦截器，自定义认证规则
        registry.addInterceptor(new SaRouteInterceptor((req, res, handler)->{
            SaRouter.match("/api/**","/checkApi/**").check(r->{
                String accessToken = req.getHeader("accessToken");
                if(StringUtils.isNotEmpty(accessToken)){
                    String value = SaTempUtil.parseToken(accessToken, String.class);
                    if(StringUtils.isNotEmpty(value)){
                        SaRouter.stop();
                    }else{
                        SaRouter.back(JSONObject.toJSON(ResultInfo.error("token过期，请重新获取！")));
                    }
                }else{
                    SaRouter.back(JSONObject.toJSON(ResultInfo.error("无访问权限")));
                }
            });
            if(!"OPTIONS".equals(req.getMethod())){
                if (StpUtil.getLoginIdByToken(StpUtil.getTokenValue()) != null){
                    SaRouter.match("/**", "/login");
                } else {
                    SaRouter.match("/**", "/login").back(JSONObject.toJSON(ResultInfo.error(40003,"登录状态失效！")));
                }
            }

        })).addPathPatterns("/**").excludePathPatterns("/openApi/strangerRecord/reportStrangerRecords",
                "/openApi/checkRecord/reportCheckRecords", "/openApi/checkRecord/reportVideo", "/acController/testNginx",
                "/toilet/**","/indicator/**","/login","/event/**",
                "/Subscribe/**","/multimodal/callback/**", "/multimodal/subscribe/**",
                "/eleDoorplateAPI/**","/device/lift/**","/videoPull/**","/warningInfo/**","/cloudbox/**",
                "/food/selectReserveListToday","/recreation/**","/deviceCollect/**",
                "/login.do", "/sysMenu/getMenuTree.do","/food/export", "/minio/**","/sign/**","*.html","swagger-ui.html",
                "/configTopic/**", "/kafka/**","/contingency_plan/**","/sysFile/**", "/meetingRoom/**","/point/**",
                "/patrolTask/**","/kickout.do","/checkLogin.do","/statistics/getSubitemEnergy","/smartEnergyReport/**",
                "/statistics/energyForecast","/statistics/energyConsumption","/smartEnergyReport/aiBulidTreeList",
                "/smartEnergyReport/aiAreareeList","/smartEnergyReport/aiEnergy","/smartEnergyReport/aiSubitemIdList",
                "/alarmRecords/**","/alarm/**","/investmentPerson/investmentPromotionPortal","/investmentPerson/buildInfo",
                "/sysDict/getRedisDictList","/investmentPerson/roomInfo","/customer/webAdd");
    }

    // 获取配置Bean (以代码的方式配置Sa-Token, 此配置会覆盖yml中的配置)
    @Bean
    @Primary
    public SaTokenConfig getSaTokenConfigPrimary() {
        SaTokenConfig config = new SaTokenConfig();
        config.setTokenName("accessToken");             // token名称 (同时也是cookie名称)
        config.setTimeout(2 * 24 * 60 * 60);       // token有效期，单位s 默认2天
//        config.setActivityTimeout(-1);              // token临时有效期 (指定时间内无操作就视为token过期) 单位: 秒
        config.setIsConcurrent(true);               // 是否允许同一账号并发登录 (为true时允许一起登录, 为false时新登录挤掉旧登录)
        config.setIsShare(false);                    // 在多人登录同一账号时，是否共用一个token (为true时所有登录共用一个token, 为false时每次登录新建一个token)
        config.setTokenStyle("uuid");               // token风格
        config.setIsReadHead(true);                 // 是否尝试从header里读取token
        config.setIsReadCookie(false);              // 是否尝试从cookie里读取token
        config.setIsLog(true);                     // 是否输出操作日志
        config.setAutoRenew(true);                  // 自动续签，指定时间内有操作，则会自动续签
        return config;
    }

}
