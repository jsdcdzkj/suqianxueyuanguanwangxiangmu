package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.common.Constants;
import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.model.ChintCloudDevicePoint;
import com.jsdc.iotpt.service.ChintCloudDevicePointService;
import com.jsdc.iotpt.service.SysUserService;
import com.jsdc.iotpt.vo.ChintCloudDevicePointOpearteVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/chint/device/point")
@Api(tags = "泰杰照明设备管理")
@Slf4j
public class ChintCloudDevicePointController {

    @Autowired
    private ChintCloudDevicePointService chintCloudDevicePointService;
    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/list")
    @ApiOperation("列表（不分页）")
    public ResultInfo list(@RequestBody ChintCloudDevicePoint point) {
        return ResultInfo.success(chintCloudDevicePointService.list(point));
    }

    @PostMapping("/statistics")
    @ApiOperation("统计")
    public ResultInfo statistics() {
        return ResultInfo.success(chintCloudDevicePointService.wlwAppStatistics());
    }

    @GetMapping("/floor")
    @ApiOperation("查询楼层设备（type 0：全部，1开启，2关闭）")
    public ResultInfo groupByFloor(@RequestParam(required = false, defaultValue = "0") String type) {
        return ResultInfo.success(chintCloudDevicePointService.groupByFloor(type));
    }


    @LogInfo(value = LogEnums.LOG_LIGHT_CONTROLS, model = Constants.MODEL_APP)
    @PostMapping("/switchByPoint")
    @ApiOperation("根据点位开关灯")
    public ResultInfo switchByPoint(@RequestBody ChintCloudDevicePointOpearteVo vo) {
        boolean permissionFlag = sysUserService.validationPermission(Constants.APP_PERMISSIONS_LIGHT, Constants.APP_PERMISSIONS_KEY);
        if (!permissionFlag) {
            return ResultInfo.error("您没有权限");
        }
        try {
            chintCloudDevicePointService.switchByPoint(vo.getPointList(), vo.getValue());
            return ResultInfo.success();
        }catch (Exception e) {
            return ResultInfo.error("调用泰杰照明系统失败，请稍后再试！");
        }
    }

    @LogInfo(value = LogEnums.LOG_LIGHT_CONTROLS, model = Constants.MODEL_APP)
    @GetMapping("/master")
    @ApiOperation("照明总控 0-关，1-开")
    public ResultInfo master(@RequestParam String value) {
        boolean permissionFlag = sysUserService.validationPermission(Constants.APP_PERMISSIONS_LIGHT, Constants.APP_PERMISSIONS_KEY);
        if (!permissionFlag) {
            return ResultInfo.error("您没有权限");
        }
        try {
            chintCloudDevicePointService.masterSwitch(value);
            return ResultInfo.success();
        }catch (Exception e) {
            return ResultInfo.error("调用泰杰照明系统失败，请稍后再试！");
        }
    }

    @GetMapping("/detail")
    @ApiOperation("详情")
    public ResultInfo detail(@RequestParam Integer id) {
        return ResultInfo.success(chintCloudDevicePointService.detail(id));
    }

    @LogInfo(value = LogEnums.LOG_LIGHT_CONTROLS, model = Constants.MODEL_APP)
    @GetMapping("/switchByBuild")
    @ApiOperation("根据区域楼层开关灯")
    public ResultInfo switchByBuild(@RequestParam(required = false) Integer floorId, @RequestParam(required = false) Integer areaId, @RequestParam String value) {
        boolean permissionFlag = sysUserService.validationPermission(Constants.APP_PERMISSIONS_LIGHT, Constants.APP_PERMISSIONS_KEY);
        if (!permissionFlag) {
            return ResultInfo.error("您没有权限");
        }
        try {
            chintCloudDevicePointService.switchByBuild(floorId, areaId, value);
            return ResultInfo.success();
        }catch (Exception e) {
            return ResultInfo.error("调用泰杰照明系统失败，请稍后再试！");
        }
    }

}
