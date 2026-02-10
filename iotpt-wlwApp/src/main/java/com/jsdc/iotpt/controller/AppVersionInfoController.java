package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.service.AppVersionInfoService;
import com.jsdc.iotpt.vo.AppVersionInfoVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * controller控制器
 */
@RestController
@RequestMapping("/app/appVersionInfo")
@Api(tags = "app版本信息管理")
public class AppVersionInfoController {

    @Autowired
    private AppVersionInfoService appVersionInfoService;

    /**
     * 分页查询 todo
     *
     * @return
     */
    @PostMapping("/getLastOne")
    @ApiOperation("最新版本")
    public ResultInfo getLastOne() {
        return appVersionInfoService.getLastOne();
    }
}
