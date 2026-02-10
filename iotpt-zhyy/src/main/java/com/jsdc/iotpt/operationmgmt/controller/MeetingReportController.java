package com.jsdc.iotpt.operationmgmt.controller;

import com.alibaba.fastjson.JSONObject;
import com.jsdc.iotpt.model.MeetingRoomConfig;
import com.jsdc.iotpt.service.MeetingRoomService;
import com.jsdc.iotpt.service.SysOrgManageService;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.MeetingReportVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 会议统计分析
 */
@RestController
@RequestMapping("/meetingReport")
@Api(tags = "会议统计分析")
public class MeetingReportController {

    @Autowired
    private MeetingRoomService meetingRoomService;

    @Autowired
    private SysOrgManageService sysOrgManageService;

    /**
     * 会议室总量、小会议室、大会议室、多功能会议室
     * roomType 会议室类型 1:小会议室 2:大会议室 3:多功能会议室
     */
    @PostMapping("/getInfo")
    @ApiOperation("会议室数量统计")
    public ResultInfo getInfo(MeetingRoomConfig bean) {
        Long total = meetingRoomService.getMeetingRoomCount(bean);
        bean.setRoomType(1);
        Long num1 = meetingRoomService.getMeetingRoomCount(bean);
        bean.setRoomType(2);
        Long num2 = meetingRoomService.getMeetingRoomCount(bean);
        bean.setRoomType(3);
        Long num3 = meetingRoomService.getMeetingRoomCount(bean);

        JSONObject object = new JSONObject();
        object.put("total", total);
        object.put("num1", num1);
        object.put("num2", num2);
        object.put("num3", num3);

        return ResultInfo.success(object);
    }

    /**
     * 单位树形图结构
     */
    @PostMapping("/orgTreeList")
    public ResultInfo orgTreeList() {
        return ResultInfo.success(sysOrgManageService.findOrg());
    }

    /**
     * 会议室部门使用情况
     */
    @PostMapping("/floorReport")
    @ApiOperation("会议室部门使用情况")
    public ResultInfo floorReport(MeetingReportVo vo) {
        if (StringUtils.isNotEmpty(vo.getStartTime())) {
            vo.setStartTime(new DateTime(vo.getStartTime()).toString("yyyy-MM-dd 00:00:00"));
        }
        if (StringUtils.isNotEmpty(vo.getEndTime())) {
            vo.setEndTime(new DateTime(vo.getEndTime()).plusDays(1).toString("yyyy-MM-dd 00:00:00"));
        }
        return meetingRoomService.floorReport(vo);
    }

    /**
     * 使用频率 日月年
     */
    @PostMapping("/useCountReport")
    @ApiOperation("会议室使用频率")
    public ResultInfo useCountReport(MeetingReportVo vo) {
        //时间类型 1 年度 2 月度 3 日
        if (vo.getTimeType().equals("1")) {
            // 年度
            vo.setStartTime(new DateTime().toString("yyyy-01-01 00:00:00"));
            vo.setEndTime(new DateTime().plusYears(1).toString("yyyy-01-01 00:00:00"));
        }
        if (vo.getTimeType().equals("2")) {
            // 月度
            vo.setStartTime(new DateTime().toString("yyyy-MM-01 00:00:00"));
            vo.setEndTime(new DateTime().plusMonths(1).toString("yyyy-MM-01 00:00:00"));
        }
        if (vo.getTimeType().equals("3")) {
            // 天
            vo.setStartTime(new DateTime().toString("yyyy-MM-dd 00:00:00"));
            vo.setEndTime(new DateTime().plusDays(1).toString("yyyy-MM-dd 00:00:00"));
        }
        return ResultInfo.success(meetingRoomService.useCountReport(vo));
    }

    /**
     * 会议室使用趋势分析
     */
    @PostMapping("/useTrendsReport")
    @ApiOperation("会议室使用趋势分析")
    public ResultInfo useTrendsReport(MeetingReportVo vo) {
        return meetingRoomService.getUseTrendsReport(vo);
    }

}
