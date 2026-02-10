package com.jsdc.iotpt.smartsecurity.controller;

import com.jsdc.iotpt.model.new_alarm.AlarmPlanConfig;
import com.jsdc.iotpt.service.AlarmPlanConfigService;
import com.jsdc.iotpt.vo.AlarmPlanConfigVO;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alarm/plan")
@Api(tags = "告警配置-告警预案")
public class AlarmPlanConfigController {


    @Autowired
    private AlarmPlanConfigService alarmPlanConfigService;

    @PostMapping("/page")
    @ApiOperation("分页查询")
    public ResultInfo getPageList(@RequestBody AlarmPlanConfigVO vo) {
        return ResultInfo.success(alarmPlanConfigService.getPageList(vo));
    }

    @GetMapping("/getById")
    @ApiOperation("根据ID查询")
    public ResultInfo getById(@RequestParam Integer id) {
        return ResultInfo.success(alarmPlanConfigService.getById(id));
    }

    @PostMapping("/save")
    @ApiOperation("新增或编辑")
    public ResultInfo saveOrUp(@RequestBody AlarmPlanConfig entity) {
        alarmPlanConfigService.saveOrUp(entity);
        return ResultInfo.success();
    }

    @PostMapping("/switch/enable")
    @ApiOperation("启用停用")
    public ResultInfo switchEnable(@RequestBody AlarmPlanConfig entity) {
        alarmPlanConfigService.switchEnable(entity);
        return ResultInfo.success();
    }

    @PostMapping("/batchDel")
    @ApiOperation("批量删除")
    public ResultInfo batchDel(@RequestBody List<Integer> ids) {
        alarmPlanConfigService.batchDel(ids);
        return ResultInfo.success();
    }

}
