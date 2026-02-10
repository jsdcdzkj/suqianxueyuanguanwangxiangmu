package com.jsdc.iotpt.smartsecurity.controller;

import com.jsdc.iotpt.model.PatrolReport;
import com.jsdc.iotpt.service.PatrolReportService;
import com.jsdc.iotpt.service.PatrolTaskService;
import com.jsdc.iotpt.vo.PatrolTaskVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/patrolTask")
@Api(tags = "巡更任务")
public class PatrolTaskController {
    @Autowired
    private PatrolTaskService patrolTaskService;
    @Autowired
    private PatrolReportService patrolReportService;


    @RequestMapping("getVideo")
    @ApiOperation(value = "视频巡更")
    @ApiImplicitParam(name = "id", value = "任务ID", dataType = "Integer", required = true)
    public ResultInfo getVideo(String id) {
        return ResultInfo.success(patrolTaskService.getVideo(id));
    }

    @RequestMapping("getPoint")
    @ApiOperation(value = "电子巡更查看")
    @ApiImplicitParam(name = "id", value = "任务ID", dataType = "Integer", required = true)
    public ResultInfo getPoint(String id) {
        return ResultInfo.success(patrolTaskService.getPoint(id));
    }

    @RequestMapping("clock")
    @ApiOperation(value = "打卡")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "任务ID", dataType = "Integer", required = true),
            @ApiImplicitParam(name = "deviceId", value = "设备ID", dataType = "Integer", required = true),
            @ApiImplicitParam(name = "clockNum", value = "实际打卡次数", dataType = "Integer", required = true),
            @ApiImplicitParam(name = "taskStatus", value = "任务状态 0待处理 1已处理 2超时未处理 3缺卡", dataType = "String", required = true)
    })
    public ResultInfo clock(Integer id, Integer deviceId) {
        return ResultInfo.success(patrolTaskService.clock(id, deviceId));
    }


    @RequestMapping("report")
    @ApiOperation(value = "上报")
    public ResultInfo report(PatrolReport report, MultipartFile file) {
        return ResultInfo.success(patrolReportService.report(report, file));
    }

    /**
     * 巡更任务列表
     * Author wzn
     * Date 2024/1/9 9:46
     */
    @RequestMapping("selectStallList")
    @ApiOperation(value = "巡更任务列表巡更任务列表 代办接口 missionStatus=1 我的任务接口 userId=当前用户ID，从缓存拿")
    public ResultInfo selectStallList(PatrolTaskVo patrolTaskVo) {
        return ResultInfo.success(patrolTaskService.selectStallList(patrolTaskVo));
    }


    /**
     * 视频巡更查看
     * Author wzn
     * Date 2024/1/11 13:55
     */
    @RequestMapping("videoTourInfo")
    @ApiOperation(value = "patrolDeviceOrPointList 左侧设备  patrolInfoVo巡更信息  patrolReportList异常事件列表   patrolClockList打卡时间列表")
    public ResultInfo videoTourInfo(String taskId, String userId) {
        return ResultInfo.success(patrolTaskService.videoTourInfo(taskId, userId));
    }


}
