package com.jsdc.iotpt.common;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;

import com.jsdc.iotpt.vo.ResultInfo;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: LogAspect
 * Description:
 * date: 2022/6/13 14:43
 *
 * @author bn
 */
@Aspect
public class LogAspect {


/*
    *//**
     * 前置操作
     *
     * @param point
     * @param hyhLog
     * @return
     * @throws Throwable
     *//*
    @Before("@annotation(hyhLog)")
    public void before(JoinPoint point, HyhLog hyhLog) throws Throwable {

        SysLog sysLog=new SysLog();
        sysLog.setId(IdUtil.simpleUUID());
        sysLog.setOrgCode(ApiUtils.getApiLoginInfo().getOrgCode());
        sysLog.setOrgType(ApiUtils.getApiLoginInfo().getOrgType());
        Object[] args = point.getArgs();
        if (ArrayUtils.isNotEmpty(args)) {
            List<Object> logArgs = Arrays.stream(args).filter(arg ->
                    (!(arg instanceof HttpServletRequest) &&
                            !(arg instanceof HttpServletResponse)&&
                            !(arg instanceof HttpSession)))
                    .collect(Collectors.toList());
           sysLog.setParams(JSONObject.toJSONString(logArgs));
        }
        sysLog.setRequestUrl(ApiUtils.getApiLoginInfo().getRequest_url());
        sysLog.setClassName(point.getTarget().getClass().getName());
        sysLog.setMethodName(point.getSignature().getName());
        sysLog.setCreateTime(new Date());
        sysLog.setFlag(1);
        sysLog.insert();
    }

    *//**
     * 后置操作
     *
     * @param point
     * @param hyhLog
     * @return
     * @throws Throwable
     *//*
    @AfterReturning(value = "@annotation(hyhLog)",returning="resultInfo")
    public void after(JoinPoint point, HyhLog hyhLog, ResultInfo resultInfo) throws Throwable {
        SysLog sysLog=new SysLog();
        sysLog.setId(IdUtil.simpleUUID());
        sysLog.setFlag(2);

        sysLog.setResult(JSONObject.toJSONString(resultInfo));
        sysLog.setCode(resultInfo.getCode());
        sysLog.setCreateTime(new Date());
        sysLog.insert();
    }*/
}
