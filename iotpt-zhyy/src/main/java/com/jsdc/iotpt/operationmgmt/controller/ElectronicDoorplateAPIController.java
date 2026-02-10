package com.jsdc.iotpt.operationmgmt.controller;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.MeetingRoomConfig;
import com.jsdc.iotpt.model.MeetingRoomReservation;
import com.jsdc.iotpt.service.MeetingRoomService;
import com.jsdc.iotpt.vo.MeetingRoomReservationVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author lb
 * @Date 2023/10/19 15:49
 * @Description 类描述
 * 电子门牌
 **/
@RestController
@RequestMapping("/eleDoorplateAPI")
@Api(tags = "会议室管理")
public class ElectronicDoorplateAPIController {

    @Autowired
    private MeetingRoomService meetingRoomService;

    /**
     * 今日会议预约一览
     *
     * @param pageNo
     * @param pageSize
     * @param roomId
     * @return
     */
    @PostMapping("/getTodayReservation")
    @ApiOperation("今日会议预约一览")
    public ResultInfo getTodayReservation(Integer pageNo, Integer pageSize, Integer roomId) {
        try {
            Page<MeetingRoomReservation> page = meetingRoomService.getTodayReservation(pageNo, pageSize, roomId);
            return ResultInfo.success(page);
        } catch (Exception e) {
            return ResultInfo.error(e.getMessage());
        }
    }

    /**
     * 根据会议室id 获取最近的一个会议 获取参会人员信息 签到  未签到 扫码签到二维码
     *
     * @param roomId
     * @return
     */
    @PostMapping("/getCurrenReservation")
    @ApiOperation("根据会议室id 获取最近的一个会议 获取参会人员信息 签到  未签到 扫码签到二维码")
    public ResultInfo getCurrenReservation(Integer roomId) {
        try {
            Map<String, Object> map = meetingRoomService.getCurrenReservation(roomId);

            return ResultInfo.success(map);
        } catch (Exception e) {
            return ResultInfo.error(e.getMessage());
        }
    }

    /**
     * 获取今天8点到17点会议室状态
     *
     * @param roomId
     * @return
     */
    @PostMapping("/getTodayListStatus")
    @ApiOperation("获取今天8点到17点会议室状态")
    public ResultInfo getTodayListStatus(Integer roomId) {
        try {
            JSONArray array = meetingRoomService.getTodayListStatus(roomId);

            return ResultInfo.success(array);
        } catch (Exception e) {
            return ResultInfo.error(e.getMessage());
        }
    }


    @PostMapping("/getMeetingRoomPage")
    @ApiOperation("查询会议室列表分页")
    public ResultInfo getMeetingRoomPage() {
        try {
            Page<MeetingRoomConfig> page = meetingRoomService.getMeetingRoomPage(new MeetingRoomConfig(), 1, 1000);
            return ResultInfo.success(page);
        } catch (Exception e) {
            return ResultInfo.error(e.getMessage());
        }
    }

    @PostMapping("/validationMeetingPwd")
    @ApiOperation(value = "修改会议室验证密码", notes = "传递参数：密码：meetingPwd(必填)")
    public ResultInfo validationMeetingPwd(String meetingPwd) {
        Map<String ,String >  map = new HashMap<>();
        String dbPwd = meetingRoomService.getChangeMeettingPwd();
        if(dbPwd.equals(meetingPwd)){
            map.put("result","T");
            map.put("msg","验证密码成功");
        }else{
            map.put("result","F");
            map.put("msg","验证密码失败");
        }
        return ResultInfo.success(map);
    }

    /**
     * 当前会议室状态
     *
     * @param roomId
     * @return
     */
    @PostMapping("/roomStatus")
    @ApiOperation("当前会议室状态")
    public ResultInfo roomStatus(Integer roomId) {
        try {
            MeetingRoomConfig byId = meetingRoomService.getById(roomId);
            if (byId.getRoomStatus() == 2){
                // 会议中
                return ResultInfo.success(2);
            }else {
                // 当前空闲
                return ResultInfo.success(1);
            }
        } catch (Exception e) {
            return ResultInfo.error(e.getMessage());
        }
    }
}
