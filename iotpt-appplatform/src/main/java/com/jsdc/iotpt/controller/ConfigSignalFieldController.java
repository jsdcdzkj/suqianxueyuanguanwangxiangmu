package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.service.ConfigSignalFieldService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.ConfigSignalFieldVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * todo
 * controller控制器
 */
@RestController
@RequestMapping("/configSignalField")
@Api(tags = "信号字段管理")
public class ConfigSignalFieldController {

    @Autowired
    ConfigSignalFieldService configSignalFieldService;

    /**
     * 分页查询 todo
     *
     * @return
     */
    @RequestMapping("/getPageList")
    @ApiOperation("信号字段管理-分页查询")
    public ResultInfo getPageList(ConfigSignalFieldVo bean) {
        return ResultInfo.success(configSignalFieldService.getPageList(bean));
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    @RequestMapping("/saveOrUpdate")
    @ApiOperation("添加/编辑信号字段")
    public ResultInfo saveOrUpdateConfigSignalField(ConfigSignalFieldVo bean) {
        return configSignalFieldService.saveOrUpdateConfigSignalField(bean);
    }

    /**
     * 获取实体类
     *
     * @param bean
     * @return
     */
    @RequestMapping("/getEntity")
    @ApiOperation("信号字段详情")
    public ResultInfo getEntity(ConfigSignalFieldVo bean) {
        return configSignalFieldService.getEntityById(bean.getId());
    }
}
