package com.jsdc.iotpt.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.jsdc.iotpt.mapper.DeviceCollectMapper;
import com.jsdc.iotpt.mapper.SysBuildMapper;
import com.jsdc.iotpt.model.ConfigDeviceType;
import com.jsdc.iotpt.model.DeviceCollect;
import com.jsdc.iotpt.model.sys.SysBuild;
import com.jsdc.iotpt.service.ConfigDeviceTypeService;
import com.jsdc.iotpt.service.DeviceCollectService;
import com.jsdc.iotpt.service.influxDB.InfluxdbService;
import com.jsdc.iotpt.service.mqtt.DataSheetService;
import com.jsdc.iotpt.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.influxdb.dto.QueryResult;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description: 统计分析
 * date: 2023/12/14 10:28
 *
 * @author bn
 */
@RestController
@RequestMapping("/app/energy")
@Api(tags = "统计分析")
public class EnergyReportController {


    @Autowired
    private DataSheetService dataSheetService;

    @Autowired
    private SysBuildMapper sysBuildMapper;

    @Autowired
    private ConfigDeviceTypeService configDeviceTypeService;

    @Autowired
    private DeviceCollectMapper deviceCollectMapper;

    @Autowired
    private DeviceCollectService deviceCollectService;
    @Autowired
    private InfluxdbService influxdbService;
    /**
     * 综合大屏
     * 能耗统计
     */
    @PostMapping("/energyReport")
    @ApiOperation("能耗统计")
    public ResultInfo energyReport(EnergyReportVo vo) {
        //楼宇集合 测试写死
        LambdaQueryWrapper<SysBuild> wrapper = new LambdaQueryWrapper();
        wrapper.eq(SysBuild::getIsDel, 0);
        List<SysBuild> buildList = sysBuildMapper.selectList(wrapper);
        List<Integer> buildIds = buildList.stream().map(SysBuild::getId).collect(Collectors.toList());
        vo.setLogicalBuildIds(buildIds);

        // 电
        ConfigDeviceType configDeviceType = new ConfigDeviceType();
        configDeviceType.setDeviceTypeCode("E_METER");
        List<ConfigDeviceType> deviceTypeList = configDeviceTypeService.getList(configDeviceType);

        List<Integer> collectIds = new ArrayList<>();
        if (deviceTypeList.size() > 0) {
            DeviceCollectVo collectVo = new DeviceCollectVo();
            collectVo.setDeviceType(deviceTypeList.get(0).getId());
            collectVo.setLogicalBuildIds(buildIds);
            List<DeviceCollect> collectList = deviceCollectService.getListByBuildId(collectVo);

            collectIds = collectList.stream().map(DeviceCollect::getId).collect(Collectors.toList());
        }
        if (collectIds.size() == 0) {
            collectIds.add(-1);
        }

        vo.setChannelId("ENERGY_TOTAL");
        vo.setDeviceIdList(collectIds);
        //时间类型 1 年度 2 月度 3 日
        vo.setTimeType("1");
        String powerYear = deviceCollectService.setEnergyParamData(vo);


        // 水
        ConfigDeviceType configDeviceType2 = new ConfigDeviceType();
        configDeviceType2.setDeviceTypeCode("W_METER");
        List<ConfigDeviceType> deviceTypeList2 = configDeviceTypeService.getList(configDeviceType2);

        if (deviceTypeList2.size() > 0) {
            DeviceCollectVo collectVo = new DeviceCollectVo();
            collectVo.setDeviceType(deviceTypeList2.get(0).getId());
            collectVo.setLogicalBuildIds(buildIds);
            List<DeviceCollect> collectList = deviceCollectService.getListByBuildId(collectVo);

            collectIds = collectList.stream().map(DeviceCollect::getId).collect(Collectors.toList());
        }
        if (collectIds.size() == 0) {
            collectIds.add(-1);
        }

        vo.setChannelId("WATER_L");
        vo.setDeviceIdList(collectIds);
        //时间类型 1 年度 2 月度 3 日
        vo.setTimeType("1");
        String waterYear = deviceCollectService.setEnergyParamData(vo);




        EnergyReportVo dataVo = new EnergyReportVo();
        dataVo.setPowerYear(powerYear);
        dataVo.setWaterYear(waterYear);
        DecimalFormat df = new DecimalFormat("0.00");

        //总碳排放量（吨）=用电量(度)*0.785/1000
        Double d = Double.parseDouble(powerYear);
        double toc = d * 0.785 / 1000;
        dataVo.setTocYear(df.format(toc));

        //当量值：总标煤（tce）=总用电量（万度）*1.229
        double tce = d / 10000 * 1.229;
        dataVo.setCoalYear(df.format(tce));


        return ResultInfo.success(dataVo);
    }


    @PostMapping("/energyReportApp")
    @ApiOperation("能耗统计")
    public ResultInfo energyReportApp(EnergyReportVo vo) {
        //楼宇集合 测试写死
        LambdaQueryWrapper<SysBuild> wrapper = new LambdaQueryWrapper();
        wrapper.eq(SysBuild::getIsDel, 0);
        List<SysBuild> buildList = sysBuildMapper.selectList(wrapper);
        List<Integer> buildIds = buildList.stream().map(SysBuild::getId).collect(Collectors.toList());
        vo.setLogicalBuildIds(buildIds);

        // 电
        ConfigDeviceType configDeviceType = new ConfigDeviceType();
        configDeviceType.setDeviceTypeCode("E_METER");
        List<ConfigDeviceType> deviceTypeList = configDeviceTypeService.getList(configDeviceType);

        List<Integer> collectIds = new ArrayList<>();
        if (deviceTypeList.size() > 0) {
            DeviceCollectVo collectVo = new DeviceCollectVo();
            collectVo.setDeviceType(deviceTypeList.get(0).getId());
            collectVo.setLogicalBuildIds(buildIds);
            collectVo.setLogicalFloorId(vo.getLogicalFloorId());
            List<DeviceCollect> collectList = deviceCollectService.getListByBuildId2(collectVo);

            collectIds = collectList.stream().map(DeviceCollect::getId).collect(Collectors.toList());
        }
        if (collectIds.size() == 0) {
            collectIds.add(-1);
        }

        vo.setChannelId("ENERGY_TOTAL");
        vo.setDeviceIdList(collectIds);
        //时间类型 1 年度 2 月度 3 日
        vo.setTimeType("7");


        Date date = DateUtil.parseDate(vo.getYear()+"-01-01") ;
        Long time= date.getTime() ;
        vo.setStartTime(new DateTime(time).toString("yyyy-MM-01 00:00:00"));
        vo.setEndTime(new DateTime(time).plusYears(1).toString("yyyy-MM-01 00:00:00"));


        String sql = "select sum(totalPower) as totalPower  from (select last(val)-first(val) as totalPower from datasheet WHERE deviceCollectId=~/^200364|^200363$/  AND channelId='ENERGY_TOTAL'  and time <='"+vo.getEndTime()+"' and time>='"+vo.getStartTime()+"'  group by deviceCollectId)" ;



        // 时序数据库解析
        QueryResult query = influxdbService.query(sql);
        //解析列表类查询
        List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
        String powerYear = "" ;
        if(CollectionUtils.isNotEmpty(maps)){
            Double val = (Double) maps.get(0).get("totalPower");
            powerYear = val +"" ;
        }else {
            powerYear =  "0.00" ;
        }


//        String powerYear = deviceCollectService.setEnergyParamData(vo);


        // 水
        ConfigDeviceType configDeviceType2 = new ConfigDeviceType();
        configDeviceType2.setDeviceTypeCode("W_METER");
        List<ConfigDeviceType> deviceTypeList2 = configDeviceTypeService.getList(configDeviceType2);

        if (deviceTypeList2.size() > 0) {
            DeviceCollectVo collectVo = new DeviceCollectVo();
            collectVo.setDeviceType(deviceTypeList2.get(0).getId());
            collectVo.setLogicalBuildIds(buildIds);
            collectVo.setLogicalFloorId(vo.getLogicalFloorId());
            List<DeviceCollect> collectList = deviceCollectService.getListByBuildId2(collectVo);

            collectIds = collectList.stream().map(DeviceCollect::getId).collect(Collectors.toList());
        }
        if (collectIds.size() == 0) {
            collectIds.add(-1);
        }

        vo.setChannelId("WATER_L");
        vo.setDeviceIdList(collectIds);
        //时间类型 1 年度 2 月度 3 日
        vo.setTimeType("7");
        String waterYear = deviceCollectService.setEnergyParamData(vo);




        EnergyReportVo dataVo = new EnergyReportVo();
        dataVo.setPowerYear(powerYear);
        dataVo.setWaterYear(waterYear);
        DecimalFormat df = new DecimalFormat("0.00");

        //总碳排放量（吨）=用电量(度)*0.785/1000
        Double d = Double.parseDouble(powerYear);
        double toc = d * 0.785 / 1000;
        dataVo.setTocYear(df.format(toc));

        //当量值：总标煤（tce）=总用电量（万度）*1.229
        double tce = d / 10000 * 1.229;
        dataVo.setCoalYear(df.format(tce));


        return ResultInfo.success(dataVo);
    }

    /**
     * 月度用水同比
     *
     * @param bean
     * @return
     */
    @PostMapping("/getMonthsWaterYOY")
    @ApiOperation("月度用水同比")
    public ResultInfo getAppMonthsWaterYOY(DataSheetVo bean) {


        // 能源类型为用水
        bean.setEnergyType(2);
        bean.setGroupStr("time(1d)");
        // 信号为用水量
        bean.setChannelId("WATER_L");
        // 返回值
        bean.setInfluxVal("MAX(val)-MIN(val)");


        return ResultInfo.success(dataSheetService.getAppChannelYOY(bean));
    }


    /**
     * 月度用电同比
     *
     * @param bean
     * @return
     */
    @PostMapping("/getAppElectricityYOY")
    @ApiOperation("月度用电同比")
    public ResultInfo getAppElectricityYOY(DataSheetVo bean) {



        bean.setGroupStr("time(1d)");
        // 用电设备
        bean.setEnergyType(1);
        // 类型
        bean.setChannelId("ENERGY_TOTAL");
        // 返回值
        bean.setInfluxVal("MAX(val)-MIN(val)");

        return ResultInfo.success(dataSheetService.getAppChannelYOY(bean));
    }






}
