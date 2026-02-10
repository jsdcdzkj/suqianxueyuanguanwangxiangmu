package com.jsdc.iotpt.operationmgmt.controller;

import com.jsdc.iotpt.model.MeetingRoomConfig;
import com.jsdc.iotpt.service.MeetingRoomService;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 *  todo  
 *  controller控制器
 */
@RestController
@RequestMapping("/config2F")
@Api(tags = "2F配置")
public class Config2FController {


    @Autowired
    MeetingRoomService meetingRoomService;




    /**
     * 会议室查询 todo
     *
     * @return
     */
    @RequestMapping("/getMeetList")
    @ApiOperation("会议室下拉")
    public ResultInfo getMeetList(MeetingRoomConfig bean) {
        return ResultInfo.success(meetingRoomService.getList(bean));
    }




}
