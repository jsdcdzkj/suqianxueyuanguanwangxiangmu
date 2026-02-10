package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.service.WarningDisposeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.WarningDisposeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  告警信息处理 controller控制器
 *  @author lb
 */
@RestController
@RequestMapping("/warningDispose")
@Api(tags = "告警信息处理")
public class WarningDisposeController {

    @Autowired
    WarningDisposeService warningDisposeService;

    /**
     * 告警信息处理分页查询
     *
     * @return
     */
    @RequestMapping("/getPageList")
    @ApiOperation("告警信息处理分页查询")
    public ResultInfo getPageList(WarningDisposeVo bean) {
        return ResultInfo.success(warningDisposeService.getPageList(bean));
    }


    @RequestMapping("/getList")
    @ApiOperation("告警信息处理分页查询")
    public ResultInfo getList(WarningDisposeVo bean) {
        return ResultInfo.success(warningDisposeService.getList(bean));
    }
    /**
     * 添加/编辑
     *
     * @param bean
     * @return
     */
    @RequestMapping("/saveOrUpdate")
    @ApiOperation("告警信息处理添加/编辑")
    public ResultInfo saveOrUpdateWarningDispose(WarningDisposeVo bean) {
        return warningDisposeService.saveOrUpdateWarningDispose(bean);
    }

    /**
     * 获取实体类
     * @param bean
     * @return
     */
    @RequestMapping("/getEntity")
    @ApiOperation("告警信息处理获取实体类")
    public ResultInfo getEntity(WarningDisposeVo bean) {
        return warningDisposeService.getEntityById(bean.getId());
    }
}
