package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.service.AlarmRecordsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.AlarmRecordsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  todo  
 *  controller控制器
 */
@RestController
@RequestMapping("/alarmRecords")
@Api(tags = "XXXXXXXXX")
public class AlarmRecordsController {

    @Autowired
    AlarmRecordsService alarmRecordsService;

    /**
     * 分页查询 todo
     *
     * @return
     */
    @RequestMapping("/getPageList")
    @ApiOperation("XXXX")
    public ResultInfo getPageList(AlarmRecordsVo bean) {
        return ResultInfo.success(alarmRecordsService.getPageList(bean));
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    @RequestMapping("/saveOrUpdate")
    @ApiOperation("XXXX")
    public ResultInfo saveOrUpdateAlarmRecords(AlarmRecordsVo bean) {
        return alarmRecordsService.saveOrUpdateAlarmRecords(bean);
    }

    /**
     * 获取实体类
     * @param bean
     * @return
     */
    @RequestMapping("/getEntity")
    @ApiOperation("XXXX")
    public ResultInfo getEntity(AlarmRecordsVo bean) {
        return ResultInfo.success(alarmRecordsService.getEntityById(bean.getId()));
    }
}
