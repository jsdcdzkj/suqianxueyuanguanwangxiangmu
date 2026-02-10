package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.service.ConfigDeviceTypeService;
import com.jsdc.iotpt.vo.ConfigDeviceTypeVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/app/configDeviceType")
@Api(tags = "设备类型配置")
public class ConfigDeviceTypeController {
    @Autowired
    ConfigDeviceTypeService configDeviceTypeService;

    @PostMapping("/getConfigDeviceTypeList")
    @ApiOperation(value = "查询设备类型列表",notes = "设备类型：deviceTypeName：设备类型名称,deviceTypeCode:设备类型编码,deviceTypeCodes:设备类型编码集合")
    public ResultInfo getList(ConfigDeviceTypeVo bean) {
        return ResultInfo.success(configDeviceTypeService.getList(bean));
    }
}
