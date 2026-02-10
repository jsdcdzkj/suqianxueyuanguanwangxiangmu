package com.jsdc.iotpt.operationmgmt.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.MeetingRoomConfig;
import com.jsdc.iotpt.model.MeetingRoomReservation;
import com.jsdc.iotpt.model.sys.SysBuildFloor;
import com.jsdc.iotpt.service.MeetingRoomService;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会议室管理
 */
@RestController
@RequestMapping("/meetingRoom")
@Api(tags = "会议室管理")
public class MeetingRoomConfigController {

    @Autowired
    private MeetingRoomService meetingRoomService;

    /**
     * 新增/编辑会议室
     */
    @PostMapping("/addMeetingRoom")
    @ApiOperation(value = "新增/编辑会议室", notes = "airConditioner 空调设备id集合 lighting照明id集合 " +
            "multimedia多媒体设备id集合 " +
            "induction人体感应设备id集合")
    public ResultInfo addOrUpdateMeetingRoom(@RequestBody MeetingRoomConfig bean) {
        try {
            meetingRoomService.addOrUpdateMeetingRoom(bean);
            return ResultInfo.success();
        } catch (Exception e) {
            throw new CustomException("操作失败");
        }
    }

    /**
     * 删除会议室
     */
    @PostMapping("/delMeetingRoom")
    @ApiOperation("删除会议室")
    public ResultInfo delMeetingRoom(Integer id) {
        try {
            meetingRoomService.delMeetingRoom(id);
            return ResultInfo.success();
        } catch (Exception e) {
            throw new CustomException("删除失败");
        }
    }

    @PostMapping("/getFloorList")
    @ApiOperation("查询拥有会议室的楼层")
    public ResultInfo getMeetingRoomFloorList() {
        try {
            List<SysBuildFloor> floorList = meetingRoomService.getMeetingRoomFloorList();
            return ResultInfo.success(floorList);
        } catch (Exception e) {
            throw new CustomException("查询拥有会议室的楼层失败");
        }
    }

    /**
     * 查询会议室列表分页
     */
    @PostMapping("/getMeetingRoomPage")
    @ApiOperation("查询会议室列表分页")
    public ResultInfo getMeetingRoomPage(@RequestParam(name = "pageIndex") Integer pageIndex, @RequestParam(name = "pageSize") Integer pageSize,
                                         MeetingRoomConfig bean) {
        try {
            Page<MeetingRoomConfig> page = meetingRoomService.getMeetingRoomPage(bean, pageIndex, pageSize);
            return ResultInfo.success(page);
        } catch (Exception e) {
            throw new CustomException("查询会议室列表失败");
        }
    }

    /**
     * 根据id查询会议室
     */
    @PostMapping("/getMeetingRoomById")
    @ApiOperation("根据id查询会议室")
    public ResultInfo getMeetingRoomById(MeetingRoomConfig bean) {
        try {
            MeetingRoomConfig meetingRoomConfig = meetingRoomService.getMeetingRoomById(bean.getId());
            return ResultInfo.success(meetingRoomConfig);
        } catch (Exception e) {
            throw new CustomException("查询会议室失败");
        }
    }


    /**
     * 新增/编辑会议室预约
     */
    @PostMapping("/addMeetingRoomOrder")
    @ApiOperation("新增/编辑会议室预约")
    public ResultInfo addOrUpdateMeetingRoomOrder(@RequestBody MeetingRoomReservation bean) {
        try {
            meetingRoomService.addOrUpdateMeetingRoomOrder(bean);
            return ResultInfo.success();
        } catch (Exception e) {
            throw new CustomException("操作失败");
        }
    }

    /**
     * 查询会议室预约列表分页
     */
    @PostMapping("/getMeetingRoomOrderPage")
    @ApiOperation("查询会议室预约列表分页")
    public ResultInfo getMeetingRoomOrderPage(@RequestParam(name = "pageIndex") Integer pageIndex, @RequestParam(name = "pageSize") Integer pageSize,
                                              MeetingRoomReservation bean) {
        try {
            Page<MeetingRoomReservation> page = meetingRoomService.getMeetingRoomOrderPage(bean, pageIndex, pageSize);
            return ResultInfo.success(page);
        } catch (Exception e) {
            throw new CustomException("获取列表失败");
        }
    }

    /**
     * 查询会议室预约列表分页
     */
    @PostMapping("/getMeetingRoomOrderById")
    @ApiOperation("查询会议室预约详情")
    public ResultInfo getMeetingRoomOrderById(String id) {
        try {
            return ResultInfo.success(meetingRoomService.getMeetingRoomOrderById(id));
        } catch (Exception e) {
            throw new CustomException("查询会议室预约失败");
        }
    }

    /**
     * 查询当天的预约情况
     */
    @PostMapping("/getMeetingRoomOrderList")
    @ApiOperation("查询当天的预约情况")
    public ResultInfo getMeetingRoomOrderList(MeetingRoomReservation bean) {
        try {
            return ResultInfo.success(meetingRoomService.getMeetingRoomOrderList(bean));
        } catch (Exception e) {
            throw new CustomException("查询当天的预约情况失败");
        }
    }


}
