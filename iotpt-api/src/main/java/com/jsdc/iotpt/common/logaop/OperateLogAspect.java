package com.jsdc.iotpt.common.logaop;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.sys.SysOperateLog;
import com.jsdc.iotpt.service.SysOperateLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * OperateLogAspect
 *
 * @author 许森森
 * @date 2024/10/22
 */
//@Aspect
//@Slf4j
//@Component
public class OperateLogAspect {

//    @Value("${spring.application.name}")
//    private String applicationName;
//
//    @Autowired
//    private SysOperateLogService operateLogService;
//
//
//    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
//    public void logPointcut() {
//
//    }
//
//    @Around("logPointcut()")
//    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
//        Object result = joinPoint.proceed();
//        try {
//            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//            HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
//            String operator;
//            try {
//                SysUser sysUser = (SysUser) StpUtil.getSession().get(StpUtil.getLoginIdByToken(StpUtil.getTokenValue()).toString());
//                operator = sysUser.getRealName();
//            }catch (NotLoginException ignored) {
//                operator = "未登录";
//            }
//            // 请求接口地址
//            String api = request.getRequestURI();
//            // 请求方式
//            String method = request.getMethod();
//            // 客户端IP地址
//            String ip = getIpAddr(request);
//            // 请求参数
//            Object[] args = joinPoint.getArgs();
//            String requestBody = Arrays.stream(args)
//                    .map(arg -> {
//                        if (arg instanceof MultipartFile) {
//                            MultipartFile file = (MultipartFile) arg;
//                            // 记录文件名和大小，避免转换文件流
//                            return "MultipartFile[name=" + file.getOriginalFilename() + ", size=" + file.getSize() + "]";
//                        }else if (arg instanceof HttpServletResponse) {
//                            // 跳过 HttpServletResponse 的序列化
//                            return "HttpServletResponse[skipped]";
//                        } else {
//                            try {
//                                // 对于其他参数，尝试转换为 JSON 字符串
//                                return JSON.toJSONString(arg);
//                            } catch (Exception e) {
//                                // 若转换失败，记录异常并跳过
//                                return "请求内容无法序列化";
//                            }
//                        }
//                    }).collect(Collectors.joining(", "));
//            // 响应参数
//            String responseBody;
//            // 检查返回值是否是流文件类型
//            if (result instanceof InputStream || result instanceof OutputStream || result instanceof Writer) {
//                // 如果是流文件，直接返回，不进行日志记录转换
//                responseBody = "响应内容为流文件，无法序列化";
//            } else {
//                try {
//                    // 非流文件，记录响应内容
//                    responseBody = JSON.toJSONString(result);
//                } catch (Exception e) {
//                    responseBody = "响应内容无法序列化";
//                }
//
//            }
//
//            SysOperateLog operateLog = new SysOperateLog();
//            operateLog.setModel(applicationName);
//            operateLog.setMethod(method);
//            operateLog.setApi(api);
//            operateLog.setRequestIp(ip);
//            operateLog.setRequestBody(requestBody);
//            operateLog.setResponseBody(responseBody);
//            operateLog.setOperator(operator);
//            operateLog.setOperateTime(new Date());
//            operateLogService.save(operateLog);
//        }catch (Exception e) {
//            log.error("save log error：", e);
//        }
//        return result;
//    }
//
//    private String getIpAddr(HttpServletRequest request) {
//        String ip = request.getHeader("x-forwarded-for");
//        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("Proxy-Client-IP");
//        }
//        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("WL-Proxy-Client-IP");
//        }
//        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getRemoteAddr();
//        }
//        return ip;
//    }

}
