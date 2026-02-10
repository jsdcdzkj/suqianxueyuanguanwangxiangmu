package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.model.WarningInfoDetails;
import com.jsdc.iotpt.service.WarningInfoDetailsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.WarningInfoDetailsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 告警信息明细 controller控制器
 *
 * @author lb
 */
@RestController
@RequestMapping("/warningInfoDetails")
@Api(tags = "告警信息明细")
public class WarningInfoDetailsController {

    @Autowired
    WarningInfoDetailsService warningInfoDetailsService;

    /**
     * 告警信息明细分页查询
     *
     * @return
     */
    @RequestMapping("/getPageList")
    @ApiOperation("告警信息明细分页查询")
    public ResultInfo getPageList(WarningInfoDetailsVo bean) {
        return ResultInfo.success(warningInfoDetailsService.getPageList(bean));
    }

    /**
     * 添加/编辑
     *
     * @param bean
     * @return
     */
    @RequestMapping("/saveOrUpdate")
    @ApiOperation("告警信息明细 添加/编辑")
    public ResultInfo saveOrUpdateWarningInfoDetails(WarningInfoDetailsVo bean) {
        return warningInfoDetailsService.saveOrUpdateWarningInfoDetails(bean);
    }

    /**
     * 获取实体类
     *
     * @param bean
     * @return
     */
    @RequestMapping("/getEntity")
    @ApiOperation("告警信息明细 获取实体类")
    public ResultInfo getEntity(WarningInfoDetailsVo bean) {
        return warningInfoDetailsService.getEntityById(bean.getId());
    }

    /**
     *  告警明细列表
     * @return
     */
    @RequestMapping("/getList")
    @ApiOperation("告警信息明细 获取列表")
    public ResultInfo getList(WarningInfoDetails bean){

        return ResultInfo.success(warningInfoDetailsService.getList(bean));
    }
}
