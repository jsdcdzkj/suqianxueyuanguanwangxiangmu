package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.service.MemoryCacheService;
import com.jsdc.iotpt.vo.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author lb
 * @Date 2023/7/4 10:47
 * @Description 类描述
 **/
@Controller
@RequestMapping("/memoryCache")
public class MemoryCacheController {
    @Autowired
    MemoryCacheService memoryCacheService;


    @RequestMapping("/reloadGateway.do")
    public ResultInfo reloadGateway() {
        memoryCacheService.changeDeviceGateway();
        return ResultInfo.success();
    }

    @RequestMapping("/reloadCollect.do")
    public ResultInfo reloadCollect() {
        memoryCacheService.changeDeviceCollectVo();
        return ResultInfo.success();
    }

    @RequestMapping("/reloadWarningConfig.do")
    public ResultInfo reloadWarningConfig() {
        memoryCacheService.changeWarningConfig();
        return ResultInfo.success();
    }
}
