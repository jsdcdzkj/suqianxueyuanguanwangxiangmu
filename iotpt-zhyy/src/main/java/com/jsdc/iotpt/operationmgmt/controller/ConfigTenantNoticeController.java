package com.jsdc.iotpt.operationmgmt.controller;

import com.jsdc.iotpt.service.ConfigTenantNoticeService;
import com.jsdc.iotpt.vo.ConfigTenantNoticeVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  todo  
 *  controller控制器
 */
@RestController
@RequestMapping("/configTenantNotice")
@Api(tags = "XXXXXXXXX")
public class ConfigTenantNoticeController {

    @Autowired
    ConfigTenantNoticeService configTenantNoticeService;

    /**
     * 分页查询 todo
     *
     * @return
     */
    @RequestMapping("/getPageList")
    @ApiOperation("XXXX")
    public ResultInfo getPageList(ConfigTenantNoticeVo bean) {
        return ResultInfo.success(configTenantNoticeService.getPageList(bean));
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    @RequestMapping("/saveOrUpdate")
    @ApiOperation("修改规则")
    public ResultInfo saveOrUpdateConfigTenantNotice(@RequestBody ConfigTenantNoticeVo bean) {
        return configTenantNoticeService.saveOrUpdateConfigTenantNotice(bean);
    }

    /**
     * 获取实体类
     * @param bean
     * @return
     */
    @RequestMapping("/getEntity")
    @ApiOperation("XXXX")
    public ResultInfo getEntity(ConfigTenantNoticeVo bean) {
        return configTenantNoticeService.getEntityById(bean.getId());
    }


    /**
     * 根据租户id获取类
     * @param bean
     * @return
     */
    @RequestMapping("/getEntityByTId")
    @ApiOperation("根据租户id获取类")
    public ResultInfo getEntityByTId(ConfigTenantNoticeVo bean) {
        return ResultInfo.success(configTenantNoticeService.getEntityByTId(bean.getId()));
    }
}
