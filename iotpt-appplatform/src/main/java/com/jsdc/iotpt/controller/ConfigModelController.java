package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.service.ConfigModelService;
import com.jsdc.iotpt.vo.ConfigModelVo;
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
@RequestMapping("/configModel")
@Api(tags = "设备型号管理")
public class ConfigModelController {

    @Autowired
    private ConfigModelService configModelService;

    /**
     * 分页查询 todo
     *
     * @return
     */
    @RequestMapping("/getPageList")
    @ApiOperation("设备型号-分页查询")
    public ResultInfo getPageList(ConfigModelVo bean) {
        return ResultInfo.success(configModelService.getPageList(bean));
    }

    @PostMapping("/getList")
    @ApiOperation("设备型号列表")
    public ResultInfo getList(ConfigModelVo bean) {
        return ResultInfo.success(configModelService.getList(bean));
    }

    /**
     * 根据id查看
     */
    @RequestMapping("/getById")
    @ApiOperation("根据id查看设备型号")
    public ResultInfo getById(ConfigModelVo bean) {
        return ResultInfo.success(configModelService.getEntityById(bean.getId()));
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    @RequestMapping("/saveOrUpdate")
    @ApiOperation("添加/编辑设备型号")
    public ResultInfo saveOrUpdateDeviceCollect(@RequestBody ConfigModelVo bean) {
        try {
            configModelService.saveOrUpdateConfigModel(bean);
        } catch (Exception e) {
            return ResultInfo.error(e.getMessage());
        }
        return ResultInfo.success(new LogVo("添加/编辑设备型号"));
    }

    /**
     * 删除 todo
     */
    @RequestMapping("/delete")
    @ApiOperation("删除设备型号")
    public ResultInfo deleteDeviceCollect(ConfigModelVo bean) {
        configModelService.delConfigModel(bean.getId());
        return ResultInfo.success();
    }


}
