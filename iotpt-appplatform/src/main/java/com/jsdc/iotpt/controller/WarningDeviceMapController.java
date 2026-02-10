package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.service.WarningDeviceMapService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.WarningDeviceMapVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 告警配置设备关联controller控制器
 * @author lb
 */
@RestController
@RequestMapping("/warningDeviceMap")
@Api(tags = "告警配置设备关联")
public class WarningDeviceMapController {

    @Autowired
    WarningDeviceMapService warningDeviceMapService;

    /**
     * 告警配置设备关联 分页查询
     *
     * @return
     */
    @RequestMapping("/getPageList")
    @ApiOperation("告警配置设备关联分页查询 ")
    public ResultInfo getPageList(WarningDeviceMapVo bean) {
        return ResultInfo.success(warningDeviceMapService.getPageList(bean));
    }

    /**
     * 添加/编辑
     *
     * @param bean
     * @return
     */
    @RequestMapping("/saveOrUpdate")
    @ApiOperation("告警配置设备关联添加/编辑")
    public ResultInfo saveOrUpdateWarningDeviceMap(WarningDeviceMapVo bean) {
        return warningDeviceMapService.saveOrUpdateWarningDeviceMap(bean);
    }

    /**
     * 获取实体类
     *
     * @param bean
     * @return
     */
    @RequestMapping("/getEntity")
    @ApiOperation("告警配置设备关联获取实体类")
    public ResultInfo getEntity(WarningDeviceMapVo bean) {
        return warningDeviceMapService.getEntityById(bean.getId());
    }
}
