package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.service.ConfigDeviceSignalMapService;
import com.jsdc.iotpt.service.ConfigSignalTypeService;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.ConfigDeviceSignalMapVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/configDeviceSignalMap")
@Api(tags = "设备信号关联管理")
public class ConfigDeviceSignalMapController {

    @Autowired
    ConfigDeviceSignalMapService configDeviceSignalMapService;
    @Autowired
    ConfigSignalTypeService ConfigSignalTypeService;

    /**
     * 分页查询 todo
     *
     * @return
     */
    @RequestMapping("/getPageList")
    public ResultInfo getPageList(ConfigDeviceSignalMapVo bean) {
        return ResultInfo.success(configDeviceSignalMapService.getPageList(bean));
    }

    /**
     * 设备类型绑定信号类型 todo
     *
     * @param bean
     * @return
     */
    @PostMapping("/saveOrUpdate")
    @ApiOperation("设备类型绑定信号类型")
    public ResultInfo saveOrUpdateConfigDeviceSignalMap(ConfigDeviceSignalMapVo bean) {
        return configDeviceSignalMapService.saveOrUpdateConfigDeviceSignalMap(bean);
    }

    @GetMapping("/getEntity")
    public ResultInfo getEntity(ConfigDeviceSignalMapVo bean) {
        return configDeviceSignalMapService.getEntityById(bean.getId());
    }

    @GetMapping("/getEntityByTId")
    @ApiOperation("根据设备类型查询绑定的信号类型")
    @ApiImplicitParam(name = "tId",value = "设备类型",dataType = "Integer")
    public ResultInfo getEntityByTId(Integer tId) {
        return configDeviceSignalMapService.getEntityByTId(tId);
    }


    @GetMapping("/getSignalType")
    @ApiOperation("条件查询信号类型")
    @ApiImplicitParam(name = "signalTypeName",value = "信号类型名称",dataType = "String")
    public ResultInfo getSignalType(String signalTypeName,Integer id) {
        return  ResultInfo.success(ConfigSignalTypeService.getSignalType(signalTypeName,id));
    }
}
