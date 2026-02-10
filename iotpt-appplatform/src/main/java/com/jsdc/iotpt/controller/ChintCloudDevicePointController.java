package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.common.Constants;
import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.model.ChintCloudDevicePoint;
import com.jsdc.iotpt.service.ChintCloudDevicePointService;
import com.jsdc.iotpt.vo.ChintCloudDevicePointVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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


    @LogInfo(value = LogEnums.LOG_LIGHT_SYNCDATA, model = Constants.MODEL_YYZT)
    @PostMapping("/refresh")
    @ApiOperation("数据同步")
    public ResultInfo refresh() {
        pointService.refresh();
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

    @LogInfo(value = LogEnums.LOG_LIGHT_UPDATE, model = Constants.MODEL_YYZT)
    @PostMapping("/edit")
    @ApiOperation("修改")
    public ResultInfo edit(@RequestBody ChintCloudDevicePoint point) {
        pointService.edit(point);
        return ResultInfo.success();
    }

    @LogInfo(value = LogEnums.LOG_LIGHT_DELETE, model = Constants.MODEL_YYZT)
    @GetMapping("/del")
    @ApiOperation("删除")
    public ResultInfo del(@RequestParam Integer id) {
        pointService.del(id);
        return ResultInfo.success();
    }

}
