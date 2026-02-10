package com.jsdc.iotpt.smartenergy.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.iotpt.common.DateUtils;
import com.jsdc.iotpt.service.ConfigDeviceSubitemService;
import com.jsdc.iotpt.service.SysOrgManageService;
import com.jsdc.iotpt.service.mqtt.DataSheetService;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.ConfigDeviceSubitemVo;
import com.jsdc.iotpt.vo.DataSheetVo;
import com.jsdc.iotpt.vo.DeviceQueryVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ClassName: AnalysisCenterController
 * Description:
 * date: 2024/3/27 8:58
 *
 * @author bn
 */
@RestController
@RequestMapping("/analysisCenter")
public class AnalysisCenterController {

    @Autowired
    private DataSheetService dataSheetService;





    @PostMapping("getEnergyStatistics.do")
    public ResultInfo getEnergyStatistics(DataSheetVo bean){

        try {
            JSONObject result=new JSONObject();
            // 用电
            bean.setChannelId("P_TOTAL");
            bean.setGroupStr("time(1d)");
            bean.setInfluxVal("LAST(val)");
            bean.setEnergyType(1);
            bean.setTimeType(2);

            bean.setThisXData(DateUtils.getMonthDays(new Date()));
            bean.setLastXData(DateUtils.getLastMonthDays(new Date()));
            JSONObject jsonObject=dataSheetService.getChannelMOM(bean);
            // 本月用电
            List<String> thisValues=new ArrayList<String>((Collection<String>) jsonObject.get("thisTime"));
            Double thisTimeElectricity=thisValues.stream().mapToDouble(i->Double.parseDouble(i)).sum();
            result.put("thisTimeElectricity",String.format("%.2f",thisTimeElectricity));
            // 上月用电
            List<String> lastValuse=new ArrayList<String>((Collection<String>) jsonObject.get("lastTime"));
            Double lastTimeElectricity=lastValuse.stream().mapToDouble(i->Double.parseDouble(i)).sum();
            result.put("lastTimeElectricity",String.format("%.2f",lastTimeElectricity));

            Double radioElectricity=(0==lastTimeElectricity)?thisTimeElectricity:((thisTimeElectricity - lastTimeElectricity) / lastTimeElectricity) * 100;

            result.put("radioElectricity",radioElectricity>0?radioElectricity:radioElectricity*-1);


            // 用水
            bean.setChannelId("WATER_L");
            bean.setInfluxVal("MAX(val)-MIN(val)");
            bean.setEnergyType(2);
            bean.setThisXData(DateUtils.getMonthDays(new Date()));
            bean.setLastXData(DateUtils.getLastMonthDays(new Date()));
            JSONObject jsonObjectW=dataSheetService.getChannelMOM(bean);
            // 本月用水
            List<String> thisValuesW=new ArrayList<String>((Collection<String>) jsonObjectW.get("thisTime"));
            Double thisTimeWater=thisValuesW.stream().mapToDouble(i->Double.parseDouble(i)).sum();
            result.put("thisTimeWater",String.format("%.2f",thisTimeWater));
            // 上月用水
            List<String> lastValuseW=new ArrayList<String>((Collection<String>) jsonObjectW.get("lastTime"));
            Double lastTimeWater=lastValuseW.stream().mapToDouble(i->Double.parseDouble(i)).sum();
            result.put("lastTimeWater",String.format("%.2f",lastTimeWater));

            Double radioWater=(0==lastTimeWater)?thisTimeWater:((thisTimeWater - lastTimeWater) / lastTimeWater) * 100;

            result.put("radioWater",radioWater>0?radioWater:radioWater*-1);

            // 用气
            bean.setChannelId("GAS_METERING");
            bean.setInfluxVal("MAX(val)-MIN(val)");
            bean.setEnergyType(3);
            bean.setThisXData(DateUtils.getMonthDays(new Date()));
            bean.setLastXData(DateUtils.getLastMonthDays(new Date()));
            JSONObject jsonObjectQ=dataSheetService.getChannelMOM(bean);
            // 本月用气
            List<String> thisValuesQ=new ArrayList<String>((Collection<String>) jsonObjectQ.get("thisTime"));
            Double thisTimeGas=thisValuesQ.stream().mapToDouble(i->Double.parseDouble(i)).sum();
            result.put("thisTimeGas",String.format("%.2f",thisTimeGas));
            // 上月用气
            List<String> lastValuseQ=new ArrayList<String>((Collection<String>) jsonObjectQ.get("lastTime"));
            Double lastTimeGas=lastValuseQ.stream().mapToDouble(i->Double.parseDouble(i)).sum();
            result.put("lastTimeGas",String.format("%.2f",lastTimeGas));

            Double radioGas=(0==lastTimeGas)?thisTimeGas:((thisTimeGas - lastTimeGas) / lastTimeGas) * 100;

            result.put("radioGas",radioGas>0?radioGas:radioGas*-1);


            //总碳排放量（吨）=用电量(度)*0.785/1000

            // 本月碳排放
            result.put("thisTimeCarbon",String.format("%.2f",thisTimeElectricity*0.000785));
            // 上月碳排放
            result.put("lastTimeCarbon",String.format("%.2f",lastTimeElectricity*0.000785));
            //
            result.put("radioCarbon",radioElectricity>0?radioElectricity:radioElectricity*-1);


            // 本月费用


            // 单位面积
            result.put("thisTimeArea",String.format("%.2f",thisTimeElectricity*1000));
            // 人均
            result.put("thisTimePeople",String.format("%.2f",thisTimeElectricity/100));
            // 电器密度
            result.put("thisTimeDensity",String.format("%.2f",thisTimeElectricity/100));
            // 暖通
            result.put("thisTimeHvac",String.format("%.2f",thisTimeElectricity/100));
            // 照明密度
            result.put("thisTimeLight",String.format("%.2f",thisTimeElectricity/100));


            return ResultInfo.success(result);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new CustomException("获取数据失败");
        }

    }

    @PostMapping("getSubitemEnergy.do")
    public ResultInfo getSubitemEnergy(@RequestBody DeviceQueryVo bean){


        try {
            JSONObject jsonObject=dataSheetService.getSubitemEnergy(bean);

            return ResultInfo.success(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("获取数据失败");
        }

    }












}
