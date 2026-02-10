package com.jsdc.iotpt.controller;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.mapper.MissionMapper;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.operate.Mission;
import com.jsdc.iotpt.service.*;
import com.jsdc.iotpt.vo.MeetingRoomReservationVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/app/sysUser")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private MeetingRoomService meetingRoomService;
    @Autowired
    private MissionMapper missionMapper;

    /**
     * 用户列表
     *
     * @return
     */
    @PostMapping("/getList")
    @ApiOperation(value = "用户列表", httpMethod = "POST")
    public ResultInfo getList(SysUser bean) {
        return ResultInfo.success(sysUserService.getList(bean));
    }

    /**
     * 智慧办公角标数量
     *
     * @return
     */
    @PostMapping("/cornerMarkerNum")
    @ApiOperation(value = "智慧办公角标数量", httpMethod = "POST")
    public ResultInfo cornerMarkerNum(SysUser bean) {
        JSONObject result = new JSONObject();
        SysUser user = sysUserService.getUser();
        // 会议管理角标
        MeetingRoomReservationVo meetingRoomReservationVo = new MeetingRoomReservationVo();
        meetingRoomReservationVo.setListType(1);
        meetingRoomReservationVo.setSequence(1);
        meetingRoomReservationVo.setRoomStatus(1);
        Page<MeetingRoomReservationVo> myAttendedMeetingsList = meetingRoomService.getMyAttendedMeetingsList(1, 100, meetingRoomReservationVo);
        int meetingNum = myAttendedMeetingsList.getRecords().size();
        result.put("meetingNum", meetingNum);
        // 娱乐健身角标
        int recreationNum = 0;


        result.put("recreationNum", recreationNum);
        //报事报修角标 只统计个人未处理数量
        Long missionNum = missionMapper.selectCount(new LambdaQueryWrapper<Mission>()
                .eq(Mission::getIs_del, 0)
                .eq(Mission::getStates, 2)
                .eq(Mission::getCreateUser, user.getId()));
        result.put("missionNum", missionNum);
        return ResultInfo.success(result);
    }


}
