package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.service.ConfigDeviceSubitemService;
import com.jsdc.iotpt.vo.ConfigDeviceSubitemVo;
import com.jsdc.iotpt.vo.LogVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * todo
 * controller控制器
 */
@RestController
@RequestMapping("/configDeviceSubitem")
@Api(tags = "设备分项管理")
public class ConfigDeviceSubitemController {

    @Autowired
    private ConfigDeviceSubitemService configDeviceSubitemService;

    /**
     * 分页查询 todo
     *
     * @return
     */
    @RequestMapping("/getPageList")
    @ApiOperation("设备分项-分页查询")
    public ResultInfo getPageList(ConfigDeviceSubitemVo bean) {
        return ResultInfo.success(configDeviceSubitemService.getPageList(bean));
    }

    @PostMapping("/getList")
    @ApiOperation("设备分项列表")
    public ResultInfo getList(ConfigDeviceSubitemVo bean) {
        return ResultInfo.success(configDeviceSubitemService.getList(bean));
    }

    /**
     * 根据id查看
     */
    @RequestMapping("/getById")
    @ApiOperation("根据id查看设备分项")
    public ResultInfo getById(ConfigDeviceSubitemVo bean) {
        return ResultInfo.success(configDeviceSubitemService.getEntityById(bean.getId()));
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    @RequestMapping("/saveOrUpdate")
    @ApiOperation("添加/编辑设备分项")
    @LogInfo(LogEnums.LOG_CONFIG_BREAKDOWN)
    public ResultInfo saveOrUpdateDeviceCollect(@RequestBody ConfigDeviceSubitemVo bean) {
        return configDeviceSubitemService.saveOrUpdateConfigDeviceSubitem(bean);
    }

    /**
     * 删除 todo
     */
    @RequestMapping("/delete")
    @ApiOperation("删除设备分项")
    @LogInfo(LogEnums.LOG_CONFIG_BREAKDOWN)
    public ResultInfo deleteDeviceCollect(ConfigDeviceSubitemVo bean) {
        configDeviceSubitemService.delConfigDeviceSubitem(bean.getId());
        return ResultInfo.success(new LogVo( "删除设备分项"));
    }


}
