package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.service.AlarmTypeConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.AlarmTypeConfigVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  todo  
 *  controller控制器
 */
@RestController
@RequestMapping("/alarmTypeConfig")
@Api(tags = "XXXXXXXXX")
public class AlarmTypeConfigController {

    @Autowired
    AlarmTypeConfigService alarmTypeConfigService;

    /**
     * 分页查询 todo
     *
     * @return
     */
    @RequestMapping("/getPageList")
    @ApiOperation("XXXX")
    public ResultInfo getPageList(AlarmTypeConfigVo bean) {
        return ResultInfo.success(alarmTypeConfigService.getPageList(bean));
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    @RequestMapping("/saveOrUpdate")
    @ApiOperation("XXXX")
    public ResultInfo saveOrUpdateAlarmTypeConfig(AlarmTypeConfigVo bean) {
        return alarmTypeConfigService.saveOrUpdateAlarmTypeConfig(bean);
    }

    /**
     * 获取实体类
     * @param bean
     * @return
     */
    @RequestMapping("/getEntity")
    @ApiOperation("XXXX")
    public ResultInfo getEntity(AlarmTypeConfigVo bean) {
        return alarmTypeConfigService.getEntityById(bean.getId());
    }
}
