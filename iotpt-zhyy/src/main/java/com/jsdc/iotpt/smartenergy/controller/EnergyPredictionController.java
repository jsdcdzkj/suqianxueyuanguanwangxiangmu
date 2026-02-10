package com.jsdc.iotpt.smartenergy.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.jsdc.iotpt.model.DataModelConfig;
import com.jsdc.iotpt.model.sys.SysBuildFloor;
import com.jsdc.iotpt.service.*;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.DeviceQueryVo;
import com.jsdc.iotpt.vo.EnergyReportVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.ApiOperation;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 能耗预测
 */
@RestController
@RequestMapping("/energyPrediction")
public class EnergyPredictionController {

    @Autowired
    private DeviceCollectService deviceCollectService;
    @Autowired
    private ChintCloudDevicePointService pointService;

    @Autowired
    private SysBuildFloorService sysBuildFloorService;
    @Autowired
    private DataModelConfigService dataModelConfigService;
    @Autowired
    private ExtremeValueAnalysisService extremeValueAnalysisService;

    /**
     * 室内温湿度统计
     * 室内温度/室内湿度
     * 室外温度/室外湿度
     */
    @RequestMapping("/tempReport")
    @ApiOperation("室内温湿度统计")
    public ResultInfo tempReport(EnergyReportVo vo) {
        // 室内温度
        vo.setConfigSign("inTemp");
        vo.setChannelId("TEMP");
        String inTemp = deviceCollectService.setHumitureParamData(vo);
        // 室内湿度
        vo.setConfigSign("inHumidity");
        vo.setChannelId("RH");
        String inHumidity = deviceCollectService.setHumitureParamData(vo);

        List<SysBuildFloor> list = sysBuildFloorService.getListByBuild(new SysBuildFloor());
        Integer personnelNumber = 0;
        BigDecimal floorArea = new BigDecimal(0);
        String occupantDensity = "0";

        for (SysBuildFloor floor : list) {
            if (StringUtils.isNotNull(floor.getPersonnelNumber())) {
                personnelNumber = personnelNumber + floor.getPersonnelNumber();
            }
            if (StringUtils.isNotEmpty(floor.getFloorArea())) {
                floorArea = floorArea.add(new BigDecimal(floor.getFloorArea()));
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("inTemp", inTemp);
        jsonObject.put("inHumidity", inHumidity);

        //人员数量
        jsonObject.put("personnelNumber", personnelNumber);
        //楼层面积
        jsonObject.put("floorArea", floorArea);
        //人员密度
        if (personnelNumber > 0 && floorArea.compareTo(BigDecimal.ZERO) > 0) {
            occupantDensity = new BigDecimal(personnelNumber.toString()).divide(floorArea, 2, BigDecimal.ROUND_HALF_UP).toString();
        }
        jsonObject.put("occupantDensity", occupantDensity);
        return ResultInfo.success(jsonObject);
    }

    @RequestMapping("/getTempData")
    @ApiOperation("室内温度统计")
    public ResultInfo getTempData() {
        // 室内温度
        return ResultInfo.success(extremeValueAnalysisService.queryTempRhValData("TEMP", "200149", new DateTime().toString("yyyy-MM-dd 00:00:00"), new DateTime().plusDays(1).toString("yyyy-MM-dd 00:00:00")));
    }

    @RequestMapping("/getRhData")
    @ApiOperation("室内湿度统计")
    public ResultInfo getRhData() {
        // 室内湿度
        return ResultInfo.success(extremeValueAnalysisService.queryTempRhValData("RH", "200149", new DateTime().toString("yyyy-MM-dd 00:00:00"), new DateTime().plusDays(1).toString("yyyy-MM-dd 00:00:00")));
    }

    /**
     * 空调列表
     */
    @PostMapping("/getAirConditioningList")
    @ApiOperation("空调列表")
    public ResultInfo getAirConditioningList(@RequestBody DeviceQueryVo vo) {
        List<Integer> deviceTypes = new ArrayList<>();
        deviceTypes.add(10002);
        deviceTypes.add(200167);

        String buildIds = "";
        String floorIds = "";
        String areaIds = "";
        if (StringUtils.isNotNull(vo)) {
            if (StringUtils.isNotNull(vo.getBuildIds()) && vo.getBuildIds().size() > 0) {
                buildIds = Joiner.on(",").join(vo.getBuildIds());
            }
            if (StringUtils.isNotNull(vo.getFloorIds()) && vo.getFloorIds().size() > 0) {
                floorIds = Joiner.on(",").join(vo.getFloorIds());
            }
            if (StringUtils.isNotNull(vo.getAreaIds()) && vo.getAreaIds().size() > 0) {
                areaIds = Joiner.on(",").join(vo.getAreaIds());
            }
        }
        return ResultInfo.success(deviceCollectService.getAirConditioningList(null, null, null, null, null,
                null, 0, 1000, null, null, areaIds, buildIds, floorIds, null, deviceTypes, 1));
    }

    /**
     * 照明列表
     */
    @PostMapping("/getLightingList")
    @ApiOperation("照明列表")
    public ResultInfo getLightingList(@RequestBody DeviceQueryVo vo) {
        return ResultInfo.success(pointService.getLightingList(vo));
    }



    /**
     * 人员参数配置
     * 所有楼层列表
     */
    @RequestMapping("/getFloorList")
    @ApiOperation("楼层列表")
    public ResultInfo getFloorList(@RequestBody DeviceQueryVo vo) {
        return ResultInfo.success(sysBuildFloorService.getFloorList(null));
    }

    /**
     * 人员参数配置 保存
     * 所有楼层列表
     */
    @RequestMapping("/updFloorList")
    @ApiOperation("楼层列表保存")
    public ResultInfo updFloorList(@RequestBody DeviceQueryVo vo) {
        sysBuildFloorService.updFloorList(vo);
        return ResultInfo.success();
    }

    /**
     * 活动参数配置
     * 详情
     */
    @RequestMapping("/configView")
    @ApiOperation("活动参数配置详情")
    public ResultInfo configView(@RequestBody DataModelConfig bean) {
        return ResultInfo.success(dataModelConfigService.view(bean));
    }

    /**
     * 活动参数配置 保存
     */
    @RequestMapping("/configSaveOrUpd")
    @ApiOperation("活动参数配置保存")
    public ResultInfo configSaveOrUpd(@RequestBody DataModelConfig bean) {
        dataModelConfigService.onSaveOrUpd(bean);
        return ResultInfo.success();
    }
}
