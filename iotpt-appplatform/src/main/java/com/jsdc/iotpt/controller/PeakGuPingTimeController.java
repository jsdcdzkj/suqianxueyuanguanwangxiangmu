package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.common.Constants;
import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.model.PeakGuPingTime;
import com.jsdc.iotpt.service.PeakGuPingTimeService;
import com.jsdc.iotpt.vo.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/peak_gu_ping_time")
public class PeakGuPingTimeController {
    @Autowired
    private PeakGuPingTimeService peakGuPingTimeService ;



    /**
     * 电费-峰谷配置新增
     * Author wzn
     * Date 2023/5/9 9:14
     */
    @PostMapping("/addPeakGuPingTime")
    @LogInfo(value = LogEnums.LOG_ELECTRIC_CHARGE_ADD,model = Constants.MODEL_YYZT)
    public ResultInfo addPeakGuPingTime(@RequestBody PeakGuPingTime peakGuPingTime) {
        peakGuPingTimeService.addPeakGuPingTime(peakGuPingTime);
        return ResultInfo.success();
    }


    /**
     * 电费-峰谷配置修改
     * Author wzn
     * Date 2023/5/9 9:15
     */
    @PostMapping("/updatePeakGuPingTime")
    @LogInfo(value = LogEnums.LOG_ELECTRIC_CHARGE_UPD,model = Constants.MODEL_YYZT)
    public ResultInfo updatePeakGuPingTime(@RequestBody PeakGuPingTime peakGuPingTime) {
        peakGuPingTimeService.updatePeakGuPingTime(peakGuPingTime);
        return ResultInfo.success();
    }

    /**
     * 峰谷配置删除
     * Author wzn
     * Date 2023/5/9 9:17
     */
    @PostMapping("/delPeakGuPingTime")
    @LogInfo(value = LogEnums.LOG_ELECTRIC_CHARGE_DEL,model = Constants.MODEL_YYZT)
    public ResultInfo delPeakGuPingTime(Integer id) {
        PeakGuPingTime p = new PeakGuPingTime();
        p.setId(id);
        p.setIsDel(1);
        peakGuPingTimeService.updateById(p);
        return ResultInfo.success();
    }

    /**
     * 峰谷配置列表
     * Author wzn
     * Date 2023/5/9 9:17
     */
    @PostMapping("/getList")
    public ResultInfo getList(@RequestBody PeakGuPingTime peakGuPingTime) {
        List<PeakGuPingTime> peakGuPingTimes = peakGuPingTimeService.getList(peakGuPingTime);
        return ResultInfo.success(peakGuPingTimes);
    }




    /**
     * 区域详情
     * Author wzn
     * Date 2023/5/9 9:18
     */
    @PostMapping("/info")
    public ResultInfo info(String id) {
        PeakGuPingTime peakGuPingTime = peakGuPingTimeService.info(id);
        return ResultInfo.success(peakGuPingTime);
    }



}
