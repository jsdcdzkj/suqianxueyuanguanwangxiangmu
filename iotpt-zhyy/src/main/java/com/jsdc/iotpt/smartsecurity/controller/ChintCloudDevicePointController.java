package com.jsdc.iotpt.smartsecurity.controller;

import com.jsdc.iotpt.common.Constants;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.model.ChintCloudDevicePoint;
import com.jsdc.iotpt.service.ChintCloudDevicePointService;
import com.jsdc.iotpt.service.SysUserService;
import com.jsdc.iotpt.vo.ChintCloudDevicePointOpearteVo;
import com.jsdc.iotpt.vo.ChintCloudDevicePointVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 泰杰赛智慧照明系统 照明设备管理
 */
@RestController
@RequestMapping("/chint/device/point")
@Api(tags = "泰杰照明设备管理")
@Slf4j
public class ChintCloudDevicePointController {


    @Autowired
    private ChintCloudDevicePointService pointService;
    @Autowired
    private SysUserService sysUserService;

    private static final String PASSWORD = "123456";

    @PostMapping("/statistics")
    @ApiOperation("统计")
    public ResultInfo statistics() {
        return ResultInfo.success(pointService.statistics());
    }

    @LogInfo(value = LogEnums.LOG_LIGHT_CONTROLS, model = Constants.MODEL_ZHYY)
    @GetMapping("/radar")
    @ApiOperation("雷达切换 1自动，0手动")
    public ResultInfo radar(@RequestParam String value, @RequestParam String password) {
        boolean permissionFlag = sysUserService.validationPermission(Constants.ZHYY_PERMISSIONS_LIGHT, Constants.ZHYY_PERMISSIONS_KEY, G.APP_WISDOM);
        if (!permissionFlag) {
            return ResultInfo.error("您没有权限");
        }
        if (!Objects.equals(PASSWORD, password)) {
            throw new RuntimeException("密码错误");
        }
        pointService.radarSwitch(value);
        return ResultInfo.success();
    }

    @LogInfo(value = LogEnums.LOG_LIGHT_CONTROLS, model = Constants.MODEL_ZHYY)
    @GetMapping("/master")
    @ApiOperation("照明总控 0-关，1-开")
    public ResultInfo master(@RequestParam String value, @RequestParam String password) {
        boolean permissionFlag = sysUserService.validationPermission(Constants.ZHYY_PERMISSIONS_LIGHT, Constants.ZHYY_PERMISSIONS_KEY, G.APP_WISDOM);
        if (!permissionFlag) {
            return ResultInfo.error("您没有权限");
        }
        if (!Objects.equals(PASSWORD, password)) {
            throw new RuntimeException("密码错误");
        }
        pointService.masterSwitch(value);
        return ResultInfo.success();
    }

    @PostMapping("/page")
    @ApiOperation("分页查询")
    public ResultInfo pageList(@RequestBody ChintCloudDevicePointVo point) {
        return ResultInfo.success(pointService.pageList(point));
    }

    @PostMapping("/list")
    @ApiOperation("列表（不分页）")
    public ResultInfo list(@RequestBody ChintCloudDevicePoint point) {
        return ResultInfo.success(pointService.list(point));
    }


    @LogInfo(value = LogEnums.LOG_LIGHT_CONTROLS, model = Constants.MODEL_ZHYY)
    @PostMapping("/switchByPoint")
    @ApiOperation("根据点位开关灯")
    public ResultInfo switchByPoint(@RequestBody ChintCloudDevicePointOpearteVo vo) {
        boolean permissionFlag = sysUserService.validationPermission(Constants.ZHYY_PERMISSIONS_LIGHT, Constants.ZHYY_PERMISSIONS_KEY, G.APP_WISDOM);
        if (!permissionFlag) {
            return ResultInfo.error("您没有权限");
        }
        pointService.switchByPoint(vo.getPointList(), vo.getValue());
        return ResultInfo.success();
    }

    @LogInfo(value = LogEnums.LOG_LIGHT_CONTROLS, model = Constants.MODEL_ZHYY)
    @GetMapping("/switchBySpace")
    @ApiOperation("根据区域开关灯")
    public ResultInfo switchBySpace(@RequestParam(required = false) Integer buildId,
                                    @RequestParam(required = false) Integer floorId,
                                    @RequestParam(required = false) Integer areaId,
                                    @RequestParam String value, @RequestParam String password) {
        boolean permissionFlag = sysUserService.validationPermission(Constants.ZHYY_PERMISSIONS_LIGHT, Constants.ZHYY_PERMISSIONS_KEY, G.APP_WISDOM);
        if (!permissionFlag) {
            return ResultInfo.error("您没有权限");
        }
        if (!Objects.equals(PASSWORD, password)) {
            throw new RuntimeException("密码错误");
        }
        pointService.switchBySpace(buildId, floorId, areaId, value);
        return ResultInfo.success();
    }



}
