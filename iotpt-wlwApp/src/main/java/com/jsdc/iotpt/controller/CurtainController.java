package com.jsdc.iotpt.controller;

import com.alibaba.fastjson.JSONArray;
import com.jsdc.iotpt.service.curtain.CurtainService;
import com.jsdc.iotpt.vo.CurtainVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: 园区物联网管控平台
 * @author: jxl
 * @create: 2024-11-28 09:09
 **/
@RestController
@RequestMapping("/app/curtain")
@Api("窗帘管理")
public class CurtainController {

    @Autowired
    private CurtainService curtainService;

    @RequestMapping("/selectByMeetingId")
    @ApiOperation(value = "获取会议室下窗帘设备列表", notes = "meetingId 会议室Id")
    public ResultInfo selectByMeetingId(Integer meetingId) {
        return ResultInfo.success(curtainService.selectByMeetingId(meetingId));
    }

    @RequestMapping("/curtainControl")
    @ApiOperation(value = "窗帘设备单独控制", notes = "controlType 控制类型 0升 1 停 2 降 id 设备id")
    public ResultInfo curtainControl(@RequestBody CurtainVo bean) {
        try {
            curtainService.curtainControl(bean);
            return ResultInfo.success();
        } catch (Exception e) {
            return ResultInfo.error("操作失败");
        }
    }
    @RequestMapping("/getCurtainById")
    @ApiOperation(value = "详情")
    public ResultInfo getCurtainById(Integer id) {
        return ResultInfo.success(curtainService.getCurtainById(id));
    }


    @GetMapping("/getCurtain")
    @ApiOperation(value = "详情")
    public JSONArray getCurtain(){
        return curtainService.getCurtain();
    }

    @GetMapping("/getCurtains")
    @ApiOperation(value = "详情")
    public ResultInfo getCurtains(){

        return ResultInfo.success(curtainService.getCurtains());
    }

}
