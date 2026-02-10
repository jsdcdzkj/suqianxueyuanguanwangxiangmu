package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.service.WarningSignDetailsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.WarningSignDetailsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 告警配置明细 controller控制器
 *
 * @author lb
 */
@RestController
@RequestMapping("/warningSignDetails")
@Api(tags = "告警配置明细")
public class WarningSignDetailsController {

    @Autowired
    WarningSignDetailsService warningSignDetailsService;

    /**
     * 告警配置明细分页查询
     *
     * @return
     */
    @RequestMapping("/getPageList")
    @ApiOperation("告警配置明细分页查询")
    public ResultInfo getPageList(WarningSignDetailsVo bean) {
        return ResultInfo.success(warningSignDetailsService.getPageList(bean));
    }

    /**
     * 添加/编辑
     *
     * @param bean
     * @return
     */
    @RequestMapping("/saveOrUpdate")
    @ApiOperation("告警配置明细添加/编辑 ")
    public ResultInfo saveOrUpdateWarningSignDetails(WarningSignDetailsVo bean) {
        return warningSignDetailsService.saveOrUpdateWarningSignDetails(bean);
    }

    /**
     * 获取实体类
     *
     * @param bean
     * @return
     */
    @RequestMapping("/getEntity")
    @ApiOperation("告警配置明细获取实体类")
    public ResultInfo getEntity(WarningSignDetailsVo bean) {
        return warningSignDetailsService.getEntityById(bean.getId());
    }
}
