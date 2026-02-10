package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.service.DeviceAccessControlService;
import com.jsdc.iotpt.vo.DeviceAccessControlVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * todo
 *  controller控制器
 */
@RestController
@RequestMapping("/deviceAccessControl")
@Api(tags = "门禁设备管理")
public class DeviceAccessControlController {

    @Autowired
    DeviceAccessControlService deviceAccessControlService;

    /**
     * 分页查询 todo
     *
     * @return
     */
    @RequestMapping("/getPageList")
    @ApiOperation("获取数据服务集合")
    public ResultInfo getPageList(DeviceAccessControlVo bean) {
        return ResultInfo.success(deviceAccessControlService.getPageList(bean));
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    @RequestMapping("/saveOrUpdate")
    @ApiOperation("添加/编辑数据服务")
    public ResultInfo saveOrUpdateDeviceAccessControl(@RequestBody DeviceAccessControlVo bean) {
        return deviceAccessControlService.saveOrUpdateDeviceAccessControl(bean);
    }


    /**
     * 获取实体类
     *
     * @param bean
     * @return
     */
    @RequestMapping("/getEntity")
    @ApiOperation("根据id获取数据服务")
    public ResultInfo getEntity(DeviceAccessControlVo bean) {
        return deviceAccessControlService.getEntityById(bean.getId());
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @ApiOperation("删除数据服务")
    public ResultInfo delete(DeviceAccessControlVo bean) {
        return deviceAccessControlService.delete(bean.getId());
    }
}
