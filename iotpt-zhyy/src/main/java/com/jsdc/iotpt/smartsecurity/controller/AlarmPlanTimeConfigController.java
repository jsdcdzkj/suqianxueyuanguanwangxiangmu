package com.jsdc.iotpt.smartsecurity.controller;


import com.jsdc.iotpt.model.new_alarm.AlarmPlanTimeConfig;
import com.jsdc.iotpt.service.AlarmPlanTimeConfigService;
import com.jsdc.iotpt.vo.AlarmPlanTimeConfigVO;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alarm/plan/time")
@Api(tags = "告警预案-时间计划模板")
public class AlarmPlanTimeConfigController {

    @Autowired
    private AlarmPlanTimeConfigService alarmPlanTimeConfigService;


    @PostMapping("/page")
    @ApiOperation("分页查询")
    public ResultInfo getPageList(@RequestBody AlarmPlanTimeConfigVO vo) {
        return ResultInfo.success(alarmPlanTimeConfigService.getPageList(vo));
    }

    @GetMapping("/getById")
    @ApiOperation("根据ID查询")
    public ResultInfo getById(@RequestParam Integer id) {
        return ResultInfo.success(alarmPlanTimeConfigService.getById(id));
    }

    @PostMapping("/save")
    @ApiOperation("新增或编辑")
    public ResultInfo saveOrUp(@RequestBody AlarmPlanTimeConfig entity) {
        alarmPlanTimeConfigService.saveOrUp(entity);
        return ResultInfo.success();
    }

    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResultInfo delete(@RequestBody AlarmPlanTimeConfig entity) {
        alarmPlanTimeConfigService.delete(entity);
        return ResultInfo.success();
    }

}
