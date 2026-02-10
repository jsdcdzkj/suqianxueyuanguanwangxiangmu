package com.jsdc.iotpt.common.logaop;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.sys.SysLog;
import com.jsdc.iotpt.util.IpRegion;
import com.jsdc.iotpt.service.IpRegionService;
import com.jsdc.iotpt.service.SysBuildFloorService;
import com.jsdc.iotpt.service.SysLogService;
import com.jsdc.iotpt.service.influxDB.InfluxdbService;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.AirCMDVo;
import com.jsdc.iotpt.vo.AirConditionCMDVo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@Aspect
@Slf4j
@Component
public class LogAspect {

    @Autowired
    private SysLogService logService;

    @Autowired
    private InfluxdbService influxdbService;

    @Autowired
    private SysBuildFloorService sysBuildFloorService;

    @Autowired
    private IpRegionService ipRegionService;

    ThreadLocal<SysUser> userThreadLocal = new ThreadLocal<>();

    @Pointcut(value = "@annotation(com.jsdc.iotpt.common.logaop.LogInfo)")
    public void logPointcut() {

    }

    @Before("logPointcut()")
    public void doBeforeReturning(JoinPoint joinPoint) {
        try {
            // 当前登录用户
            SysUser user = (SysUser) StpUtil.getSession().get(StpUtil.getLoginIdByToken(StpUtil.getTokenValue()).toString());
            userThreadLocal.set(user);
        }catch (Exception ignored) {}
    }

    @AfterReturning(value = "logPointcut()", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        try {
            SysUser sysUser = userThreadLocal.get();
            if (sysUser == null) {
                try {
                    sysUser = (SysUser) StpUtil.getSession().get(StpUtil.getLoginIdByToken(StpUtil.getTokenValue()).toString());
                }catch (NotLoginException notLoginException) {
                    return;
                }
            }
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            LogInfo logInfo = method.getAnnotation(LogInfo.class);
            // 操作描述
            LogEnums logEnums = logInfo.value();
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
            // 请求接口地址
            String api = request.getRequestURI();
            // 客户端IP地址
            String ip = getIpAddr(request);

            // 请求参数
            String requestBody;
            if (StringUtils.isEmpty(joinPoint.getArgs())) {
                requestBody = "";
            } else if (joinPoint.getArgs()[0] instanceof MultipartFile) {
                requestBody = ((MultipartFile) joinPoint.getArgs()[0]).getOriginalFilename();
            }else {
                // 登录 密码脱敏
                if ("1".equals(logEnums.getValue())) {
                    JSONArray jsonArray = JSON.parseArray(JSON.toJSONString(joinPoint.getArgs()));
                    for (Object o : jsonArray) {
                        Map<String, String> loginVo = (Map<String, String>) o;
                        if (StringUtils.isNotBlank(loginVo.get("password"))) {
                            loginVo.put("password", "******");
                        }
                    }
                    requestBody = jsonArray.toJSONString();
                } else {
                    requestBody = JSON.toJSONString(joinPoint.getArgs());
                }
            }
            String responseBody = JSON.toJSONString(result);

            SysLog sysLog = new SysLog();
            sysLog.setLogType(logEnums.getValue());
            sysLog.setModel(logInfo.model());
            sysLog.setContent(logInfo.model() + "-" + logEnums.getDesc());

            if (logEnums.getValue().equals("10-1")){//空调日志
                AirConditionCMDVo object=(AirConditionCMDVo)joinPoint.getArgs()[0];
                String mode=getModel(object);
                String windSpeed=getWindSpeed(object);
                sysLog.setContent1(logInfo.model() + "-" + logEnums.getDesc()+"-"+(object.getOnOff().equals(1)?"开":"关")+"-模式:"+mode+"-风速:"+windSpeed+mode+"-温度:"+object.getTemp());
            }

            if (logEnums.getValue().equals("10-3")) {//空调日志
                AirCMDVo object = (AirCMDVo)joinPoint.getArgs()[0];
                if(!Objects.isNull(object) && !Objects.isNull(object.getVo())) {
                    AirConditionCMDVo cmdVo = object.getVo();
                    String mode=getModel(cmdVo);
                    String windSpeed=getWindSpeed(cmdVo);
                    sysLog.setContent1(logInfo.model() + "-" + logEnums.getDesc() + "-" + (cmdVo.getOnOff().equals(1) ? "开" : "关")+"-模式:"+mode+"-风速:"+windSpeed+mode+"-温度:"+cmdVo.getTemp());
                }else{
                    sysLog.setContent1(logInfo.model() + "-" + logEnums.getDesc() + "-" );
                }
            }

            sysLog.setApi(api);
            sysLog.setRequestAddr(ip);
            sysLog.setRequestBody(requestBody);
            sysLog.setResponseBody(responseBody);
            sysLog.setOperator(sysUser.getId());
            sysLog.setOperateTime(new Date());
            sysLog.setOperatorName(sysUser.getRealName());
            sysLog.setIsDel(0);

            // IP归属地
            IpRegion region = ipRegionService.getIpRegion(ip);
            if (StringUtils.isNotNull(region)) {
                sysLog.setIpGeolocation(region.toString());
            }

            logService.save(sysLog);

            Map<String, String> tagsMap = new HashMap<>();
            Map<String, Object> fieldsMap = new HashMap<>();

            tagsMap.put("logType", logEnums.getValue());
            tagsMap.put("api", api);
            tagsMap.put("requestAddr", ip);
            tagsMap.put("operator", String.valueOf(sysUser.getId()));
            tagsMap.put("operatorName", sysUser.getRealName());
            tagsMap.put("content", sysLog.getContent());

            if (logEnums.getValue().equals("10-1")){//空调日志
                AirConditionCMDVo object=(AirConditionCMDVo)joinPoint.getArgs()[0];
                String mode=getModel(object);
                String windSpeed=getWindSpeed(object);
                tagsMap.put("content1",logInfo.model() + "-" + logEnums.getDesc()+"-"+(object.getOnOff().equals(1)?"开":"关")+"-模式:"+mode+"-风速:"+windSpeed+"-温度:"+object.getTemp());
            }

            if (logEnums.getValue().equals("10-3")) {//空调日志
                AirCMDVo object = (AirCMDVo)joinPoint.getArgs()[0];
                if(!Objects.isNull(object) && !Objects.isNull(object.getVo())) {
                    AirConditionCMDVo cmdVo = object.getVo();
                    String mode=getModel(cmdVo);
                    String windSpeed=getWindSpeed(cmdVo);
                    tagsMap.put("content1",logInfo.model() + "-" + logEnums.getDesc()+"-"+(cmdVo.getOnOff().equals(1)?"开":"关")+"-模式:"+mode+"-风速:"+windSpeed+"-温度:"+cmdVo.getTemp());
                }else{
                    tagsMap.put("content1",logInfo.model() + "-" + logEnums.getDesc()+"-");
                }
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("requestBody", requestBody);
            jsonObject.put("responseBody", responseBody);

            fieldsMap.put("val_string", jsonObject.toJSONString());

            long time = new Date().getTime() + 28800000;
            influxdbService.insert("syslog", time, tagsMap, fieldsMap);
        } catch (Exception e) {
            log.error("save log error：", e);
        }
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (!org.springframework.util.StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (!org.springframework.util.StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (!org.springframework.util.StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (!org.springframework.util.StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (!org.springframework.util.StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 对于通过多个代理的情况，第一个IP为客户端真实IP
        if (org.springframework.util.StringUtils.hasText(ip) && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        // 处理本地地址
        if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        return ip;
    }

    private String getModel(AirConditionCMDVo vo){
        //0:无效；1:制冷；2:抽湿；3:送风；4:制热；5:自动
        if (null ==vo.getModal()){
            return "默认";
        } else if (0==vo.getModal()){
            return "无效";
        } else if (1==vo.getModal()) {
            return "制冷";
        } else if (2==vo.getModal()) {
            return "抽湿";
        }else if (3==vo.getModal()) {
            return "送风";
        } else if (4==vo.getModal()) {
            return "制热";
        }else {
            return "自动";
        }
    }

    private String getWindSpeed(AirConditionCMDVo vo){
        //0:无效；1：自动风速；2：低档； 3：中低档；4：中档；5: 中高档；6：高档；
        if (null ==vo.getModal()){
            return "默认";
        }else if (0==vo.getWindSpeed()){
            return "无效";
        } else if (1==vo.getWindSpeed()) {
            return "自动风速";
        } else if (2==vo.getWindSpeed()) {
            return "低档";
        }else if (3==vo.getWindSpeed()) {
            return "中低档";
        } else if (4==vo.getWindSpeed()) {
            return "中档";
        } else if (5==vo.getWindSpeed()) {
            return "中高档";
        } else {
            return "高档";
        }
    }

}
