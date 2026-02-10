package com.jsdc.iotpt.smartenergy.controller;

import com.jsdc.iotpt.model.DeviceCollect;
import com.jsdc.iotpt.service.DeviceCollectService;
import com.jsdc.iotpt.service.ExtremeValueAnalysisService;
import com.jsdc.iotpt.vo.ExtremeValueAnalysisVo;
import com.jsdc.iotpt.vo.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 极值分析
 */
@RestController
@RequestMapping("/extremeValueAnalysis")
public class ExtremeValueAnalysisController {

    @Autowired
    private DeviceCollectService deviceCollectService;
    @Autowired
    private ExtremeValueAnalysisService extremeValueAnalysisService;

    /**
     * 1、所有的电表（除了空调外机电表）
     */
    @RequestMapping("/electricityMeterData")
    public ResultInfo electricityMeterData() {
        List<DeviceCollect> list = deviceCollectService.electricityMeterData(null);
        return ResultInfo.success(list);
    }

    /**
     * 2、所有的4P空开
     */
    @RequestMapping("/airSwitchData")
    public ResultInfo airSwitchData() {
        List<DeviceCollect> list = deviceCollectService.airSwitchData(null);
        return ResultInfo.success(list);
    }

    /**
     * 极值分析
     * 折线图数据
     */
    @RequestMapping("/getDataInfo")
    public ResultInfo getDataInfo(@RequestBody ExtremeValueAnalysisVo vo) {
        return ResultInfo.success(extremeValueAnalysisService.getDataInfo(vo));
    }

}
