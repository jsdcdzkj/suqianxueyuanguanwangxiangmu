package com.jsdc.iotpt.smartsecurity.controller;

import com.jsdc.iotpt.service.FalseAlarmRecordsService;
import com.jsdc.iotpt.vo.FalseAlarmRecordsVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  todo  
 *  controller控制器
 */
@RestController
@RequestMapping("/falseAlarmRecords")
@Api(tags = "XXXXXXXXX")
public class FalseAlarmRecordsController {

    @Autowired
    FalseAlarmRecordsService falseAlarmRecordsService;

    /**
     * 分页查询 todo
     *
     * @return
     */
    @RequestMapping("/getPageList")
    @ApiOperation("XXXX")
    public ResultInfo getPageList(FalseAlarmRecordsVo bean) {
        return ResultInfo.success(falseAlarmRecordsService.getPageList(bean));
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    @RequestMapping("/saveOrUpdate")
    @ApiOperation("XXXX")
    public ResultInfo saveOrUpdateFalseAlarmRecords(FalseAlarmRecordsVo bean) {
        return falseAlarmRecordsService.saveOrUpdateFalseAlarmRecords(bean);
    }

    /**
     * 获取实体类
     * @param bean
     * @return
     */
    @RequestMapping("/getEntity")
    @ApiOperation("XXXX")
    public ResultInfo getEntity(FalseAlarmRecordsVo bean) {
        return falseAlarmRecordsService.getEntityById(bean.getId());
    }
}
