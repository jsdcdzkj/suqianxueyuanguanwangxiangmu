package com.jsdc.iotpt.operationmgmt.controller;

import com.jsdc.iotpt.dto.chintcloud.CurtainDTO;
import com.jsdc.iotpt.model.curtain.Curtain;
import com.jsdc.iotpt.service.curtain.CurtainService;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.CurtainVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: 园区物联网管控平台
 * @author: jxl
 * @create: 2024-11-26 09:08
 **/

@RestController
@RequestMapping("/curtain")
@Api("窗帘管理")
public class CurtainController {
    @Autowired
    private CurtainService curtainService;


    @PostMapping("/saveOrUpd")
    @ApiOperation(value = "新增、修改", notes = "根据页面参数传递")
    public ResultInfo saveOrUpd(@RequestBody Curtain bean) {
        try {
            Long count = curtainService.isExistJudgeCount(bean);
            if (count > 0) {
                throw new CustomException("窗帘已存在");
            }
            curtainService.saveOrUpd(bean);
            return ResultInfo.success("操作成功");
        } catch (Exception e) {
            throw new CustomException("操作失败");
        }
    }


    @RequestMapping("/deleteById")
    @ApiOperation(value = "删除")
    public ResultInfo deleteById(Integer id) {
        try {
            curtainService.deleteById(id);
            return ResultInfo.success("操作成功");
        } catch (Exception e) {
            throw new CustomException("删除失败");
        }
    }

    @RequestMapping("/getCurtainById")
    @ApiOperation(value = "详情")
    public ResultInfo getCurtainById(Integer id) {
        try {
            return ResultInfo.success(curtainService.getCurtainById(id));
        } catch (Exception e) {
            throw new CustomException("获取详情失败");
        }
    }


    @RequestMapping("/selectPageList")
    @ApiOperation(value = "获取窗帘分页列表", notes = "curtainName 窗帘名称")
    public ResultInfo selectPageList(@RequestBody CurtainDTO bean) {
        try {
            return ResultInfo.success(curtainService.selectPageList(bean));
        } catch (Exception e) {
            throw new CustomException("获取窗帘分页列表失败");
        }
    }
    @RequestMapping("/selectByMeetingId")
    @ApiOperation(value = "获取会议室下窗帘设备列表", notes = "meetingId 会议室Id")
    public ResultInfo selectByMeetingId(Integer meetingId) {
        try {
            return ResultInfo.success(curtainService.selectByMeetingId(meetingId));
        } catch (Exception e) {
            throw new CustomException("获取会议室下窗帘设备列表失败");
        }
    }

    @RequestMapping("/curtainControl")
    @ApiOperation(value = "窗帘设备单独控制", notes = "controlType 控制类型 0升 1 停 2 降 id 设备id")
    public ResultInfo curtainControl(@RequestBody CurtainVo bean) {
        try {
            curtainService.curtainControl(bean);
            return ResultInfo.success();
        } catch (Exception e) {
            throw new CustomException("操作失败");
        }
    }

}
