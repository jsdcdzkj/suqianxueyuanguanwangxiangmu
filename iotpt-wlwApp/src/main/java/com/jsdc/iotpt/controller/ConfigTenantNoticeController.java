package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.service.ConfigTenantNoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.ConfigTenantNoticeVo;
import org.springframework.beans.factory.annotation.Autowired;
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
    @ApiOperation("XXXX")
    public ResultInfo saveOrUpdateConfigTenantNotice(ConfigTenantNoticeVo bean) {
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
}
