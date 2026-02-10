package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.service.FireAlarmInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.FireAlarmInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  todo  
 *  controller控制器
 */
@RestController
@RequestMapping("/fireAlarmInfo")
@Api(tags = "XXXXXXXXX")
public class FireAlarmInfoController {

    @Autowired
    FireAlarmInfoService fireAlarmInfoService;

    /**
     * 分页查询 todo
     *
     * @return
     */
    @RequestMapping("/getPageList")
    @ApiOperation("XXXX")
    public ResultInfo getPageList(FireAlarmInfoVo bean) {
        return ResultInfo.success(fireAlarmInfoService.getPageList(bean));
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    @RequestMapping("/saveOrUpdate")
    @ApiOperation("XXXX")
    public ResultInfo saveOrUpdateFireAlarmInfo(FireAlarmInfoVo bean) {
        return fireAlarmInfoService.saveOrUpdateFireAlarmInfo(bean);
    }

    /**
     * 获取实体类
     * @param bean
     * @return
     */
    @RequestMapping("/getEntity")
    @ApiOperation("XXXX")
    public ResultInfo getEntity(FireAlarmInfoVo bean) {
        return fireAlarmInfoService.getEntityById(bean.getId());
    }
}
