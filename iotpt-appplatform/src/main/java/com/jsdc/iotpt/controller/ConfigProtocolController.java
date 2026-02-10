package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.service.ConfigProtocolService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.ConfigProtocolVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @authon thr
 * @describe 协议管理
 */
@RestController
@RequestMapping("/configProtocol")
@Api(tags = "协议管理")
public class ConfigProtocolController {

    @Autowired
    ConfigProtocolService configProtocolService;

    /**
     * 分页查询 todo
     *
     * @return
     */
    @RequestMapping("/getPageList")
    @ApiOperation("协议管理-分页查询")
    public ResultInfo getPageList(ConfigProtocolVo bean) {
        return ResultInfo.success(configProtocolService.getPageList(bean));
    }

    /**
     * 协议管理列表 todo
     *
     * @param bean
     * @return
     */
    @PostMapping("/getList")
    @ApiOperation("协议管理列表")
    public ResultInfo getList(ConfigProtocolVo bean) {
        return ResultInfo.success(configProtocolService.getList(bean));
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    @RequestMapping("/saveOrUpdate")
    @ApiOperation("添加/编辑协议")
    public ResultInfo saveOrUpdateConfigProtocol(@RequestBody ConfigProtocolVo bean) {
        return configProtocolService.saveOrUpdateConfigProtocol(bean);
    }

    /**
     * 获取实体类
     * @param bean
     * @return
     */
    @RequestMapping("/getEntity")
    @ApiOperation("协议详情")
    public ResultInfo getEntity(ConfigProtocolVo bean) {
        return configProtocolService.getEntityById(bean.getId());
    }
}
