package com.jsdc.iotpt.operationmgmt.controller;

import com.jsdc.iotpt.service.MissionItemRecordService;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 任务项记录
 *
 * @author zln
 */
@RestController
@RequestMapping("/missionItemRecord")
@Api(tags = "任务项记录")
public class MissionItemRecordController {

    @Autowired
    private MissionItemRecordService missionService;

    @PostMapping("selectRegionByDevice.do")
    @ApiOperation(value = "根据所属区域获取所有设备", notes = "根据areaId获取设备信息")
    public ResultInfo selectRegionByDevice(Integer areaId) {
        try {
            return ResultInfo.success(missionService.selectRegionByDevice(areaId, null));
        } catch (Exception e) {
            throw new CustomException("获取所有设备失败");
        }
    }


    @PostMapping("selectAreaList.do")
    @ApiOperation(value = "查询所有区域")
    public ResultInfo selectAreaList(String name) {
        try {
            return ResultInfo.success(missionService.selectAreaList(name));
        } catch (Exception e) {
            throw new CustomException("查询所有区域失败");
        }
    }

}
