package com.jsdc.iotpt.smartsecurity.controller;

import com.jsdc.iotpt.service.AlarmStatisticsService;
import com.jsdc.iotpt.vo.AlarmStatisticsVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/alarm/statistics")
@Api(tags = "新告警统计概览")
public class AlarmStatisticsController {
    @Autowired
    private AlarmStatisticsService alarmStatisticsService;

    @PostMapping("/pieChart")
    @ApiOperation("饼图-告警类型统计")
    public ResultInfo pieChart(@RequestBody AlarmStatisticsVo vo) {
        return ResultInfo.success(alarmStatisticsService.pieChart(vo));
    }
    @PostMapping("/rank")
    @ApiOperation("告警排名")
    public ResultInfo rank(@RequestBody AlarmStatisticsVo vo) {
        return ResultInfo.success(alarmStatisticsService.rank(vo));
    }
    @PostMapping("/alarmNum")
    @ApiOperation("告警数量")
    public ResultInfo alarmNum(@RequestBody AlarmStatisticsVo vo) {
        return ResultInfo.success(alarmStatisticsService.alarmNum(vo));
    }


    @PostMapping("/alarmTrend")
    @ApiOperation("告警趋势")
    public ResultInfo alarmTrend(@RequestBody AlarmStatisticsVo vo) {
        return ResultInfo.success(alarmStatisticsService.alarmTrend(vo));
    }

    @PostMapping("/securityAssessment")
    @ApiOperation("运营安全评估")
    public ResultInfo securityAssessment(@RequestBody AlarmStatisticsVo vo) {
        return ResultInfo.success(alarmStatisticsService.securityAssessment(vo));
    }

    @PostMapping("/disposal")
    @ApiOperation("告警处置")
    public ResultInfo disposal(@RequestBody AlarmStatisticsVo vo) {
        return ResultInfo.success(alarmStatisticsService.disposal(vo));
    }
}
