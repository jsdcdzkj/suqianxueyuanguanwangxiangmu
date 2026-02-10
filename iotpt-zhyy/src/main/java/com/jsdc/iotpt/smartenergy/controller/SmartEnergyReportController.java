package com.jsdc.iotpt.smartenergy.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.model.DeviceCollect;
import com.jsdc.iotpt.model.DeviceEnergyUse;
import com.jsdc.iotpt.model.SubmiteValue;
import com.jsdc.iotpt.model.ConfigDeviceSubitemValue;
import com.jsdc.iotpt.model.sys.SysBuildFloor;
import com.jsdc.iotpt.service.*;
import com.jsdc.iotpt.service.mqtt.DataSheetService;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 智慧应用 智慧能源 所有报表接口
 */
@RestController
@RequestMapping("/smartEnergyReport")
public class SmartEnergyReportController {

    @Autowired
    private SmartEnergyReportService smartEnergyReportService;
    @Autowired
    private ConfigDeviceSubitemService configDeviceSubitemService;
    @Autowired
    private ConfigDeviceTypeService configDeviceTypeService;
    @Autowired
    private SysOrgManageService sysOrgManageService;
    @Autowired
    private DeviceCollectService deviceCollectService;
    @Autowired
    private DataSheetService dataSheetService;

    @Autowired
    private SysBuildFloorService sysBuildFloorService;

    @Autowired
    private DeviceEnergyUseService deviceEnergyUseService ;

    @Autowired
    private ConfigDeviceSubitemValueService configDeviceSubitemValueService ;

    @Autowired
    private DeviceCollectService collectService;

    @Autowired
    private RedisUtils redisUtils ;

    @Autowired
    private SysBuildService sysBuildService;

    /**
     * 设备分项
     */
    @PostMapping("/getDeviceSubitemList")
    @ApiOperation("设备分项列表")
    public ResultInfo getDeviceSubitemList(ConfigDeviceSubitemVo bean) {
        return ResultInfo.success(configDeviceSubitemService.getList(bean));
    }

    /**
     * 查询设备类型列表
     */
    @PostMapping("/getDeviceTypeList")
    @ApiOperation("查询设备类型列表")
    public ResultInfo getDeviceTypeList(ConfigDeviceTypeVo bean) {
        return ResultInfo.success(configDeviceTypeService.getList(bean));
    }

    /**
     * 多维分析树状图专用
     * 区域三级树形图
     */
    @PostMapping("/areaTreeList")
    public ResultInfo areaTreeList(Integer buildId) {
        return ResultInfo.success(sysOrgManageService.areaTreeList3(buildId));
    }

    /**
     * 能源抄表
     * 区域三级树形图 楼宇楼层+总表分表
     * Author thr
     * Date 2024/10/17 09:11
     */
    @PostMapping("/areaTreeZfList")
    public ResultInfo areaTreeZfList(Integer buildId) {
        return ResultInfo.success(sysOrgManageService.areaTreeZfList(buildId));
    }

    /**
     * 逻辑位置 任意层级可选择
     * 区域三级树形图
     */
    @PostMapping("/areaTreeList2")
    public ResultInfo areaTreeList2() {
        return ResultInfo.success(sysOrgManageService.areaTreeList2());
    }

    /**
     * 逻辑位置 任意层级可选择
     * 区域三级树形图
     */
    @PostMapping("/buildFloorTreeList")
    public ResultInfo buildFloorTreeList() {
        return ResultInfo.success(sysOrgManageService.buildFloorTreeList());
    }

    /**
     * 采集终端列表
     */
    @PostMapping("/getDeviceCollectList")
    @ApiOperation("采集终端列表")
    public ResultInfo getDeviceCollectList(DeviceCollectVo bean) {
        return ResultInfo.success(deviceCollectService.getList(bean));
    }

    /**
     * 多区域查询设备列表
     * 采集终端列表
     */
    @PostMapping("/getDeviceCollectListNew")
    @ApiOperation("多区域查询设备列表")
    public ResultInfo getDeviceCollectListNew(@RequestBody DeviceCollectVo bean) {
        return ResultInfo.success(deviceCollectService.getListNew(bean));
    }

    /**
     * 多维分析
     */
    @PostMapping("/multidimensionalAnalysis")
    @ApiOperation("多维分析")
    public ResultInfo multidimensionalAnalysis(@RequestBody SmartEnergyReportVo vo) {
        return ResultInfo.success(smartEnergyReportService.multidimensionalAnalysis(vo));
    }



    /**
     * 能耗检测
     */
    @RequestMapping("/getDeviceInfo")
    @ApiOperation("能耗检测")
    public ResultInfo getDeviceInfo(@RequestBody DeviceQueryVo queryVo) {
        return deviceCollectService.getDeviceInfo(queryVo);
    }

    @RequestMapping("/getDeviceInfo2")
    @ApiOperation("能耗检测")
    public ResultInfo getDeviceInfo2(@RequestBody DeviceQueryVo queryVo) {
        return deviceCollectService.getDeviceInfo2(queryVo);
    }


    /**
     * 能耗检测-详情-信号上报
     */
    @RequestMapping("/getDeviceDataByChannelId")
    @ApiOperation("能耗检测-详情-信号上报")
    public ResultInfo getDeviceDataByChannelId(String deviceId) {
        return deviceCollectService.getDeviceDataByChannelId(deviceId);
    }

    /**
     * 能耗检测-详情-基本信息
     */
    @RequestMapping("/getDeviceDetail")
    @ApiOperation("能耗检测-详情-基本信息")
    public ResultInfo getDeviceDetail(String deviceId) {
        DeviceCollect deviceCollect = deviceCollectService.getById(deviceId);
        return ResultInfo.success(deviceCollect);
    }

    /**
     * 明细分析
     *
     * @return
     * @paramn queryVo
     */
    @RequestMapping("/deviceDetailsAnalysis")
    @ApiOperation("能源抄表")
    public ResultInfo deviceDetailsAnalysis(DeviceQueryVo queryVo) {
        return deviceCollectService.deviceDetailsAnalysis(queryVo);
    }
    @RequestMapping("/deviceDetailsAnalysis2")
    @ApiOperation("能源抄表-折线图")
    public ResultInfo deviceDetailsAnalysis2(DeviceQueryVo queryVo) {
        return deviceCollectService.deviceDetailsAnalysis2(queryVo);
    }

    /**
     * 能耗报表
     *
     * @return
     * @paramn queryVo
     */
    @RequestMapping("/analysis")
    @ApiOperation("能耗报表")
    public ResultInfo analysis(@RequestBody DeviceQueryVo queryVo) {
        return deviceCollectService.analysis(queryVo);
    }

    /**
     * 复费率报表
     *
     * @return
     * @paramn queryVo
     */
    @RequestMapping("/multiRate")
    @ApiOperation("复费率报表")
    public ResultInfo multiRate(@RequestBody DeviceQueryVo queryVo) {
        return deviceCollectService.multiRate(queryVo);
    }


    /**
     * 明细导出
     * Author wzn
     * Date 2023/8/3 17:26
     */
    @RequestMapping("/deviceExport")
    public void westExport(HttpServletResponse response, DeviceQueryVo queryVo) throws Exception {
        ResultInfo resultInfo = deviceCollectService.deviceDetailsAnalysis(queryVo);
        if (resultInfo.getCode() != 0) {
            throw new CustomException(resultInfo.getMsg());
        }
        JSONObject result = (JSONObject) resultInfo.getData();
        List<String> strings = (List<String>) result.get("columns");
        List<DeviceExportVo> deviceExportVoList = new ArrayList<>();
        DeviceExportVo deviceExportVo = null;

        List<JSONObject> datas = (List<JSONObject>) result.get("data");
        BigExcelWriter writer = ExcelUtil.getBigWriter();
        writer.addHeaderAlias("deviceName", "设备名称");
        for (String s : strings) {
            writer.addHeaderAlias(s, s);
        }
        datas.forEach(x -> {
            strings.forEach(s -> {
                if (!x.containsKey(s)) {
                    x.put(s, "0.00");
                }
            });
        });
        //只导出定义字段
        writer.setOnlyAlias(true);
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(datas, true);
        //out为OutputStream，需要写出到的目标流
        //response为HttpServletResponse对象
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
        response.setHeader("Content-Disposition", "attachment;filename=test.xls");
        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        // 关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }

    /**
     * 能耗报表导出
     */
    @RequestMapping("/analysisExport")
    public void analysisExport(HttpServletResponse response, @RequestBody DeviceQueryVo queryVo) throws Exception {
        queryVo.setPageNo(null);
        queryVo.setPageSize(null);
        ResultInfo resultInfo = deviceCollectService.analysis(queryVo);
        if (resultInfo.getCode() != 0) {
            throw new CustomException(resultInfo.getMsg());
        }
        JSONObject result = (JSONObject) resultInfo.getData();
        List<String> strings = (List<String>) result.get("columns");

        List<JSONObject> datas = (List<JSONObject>) result.get("data");
        BigExcelWriter writer = ExcelUtil.getBigWriter();
        writer.addHeaderAlias("deviceName", "设备名称");
        writer.addHeaderAlias("deviceCode", "设备编码");
        writer.addHeaderAlias("areaName", "所属区域");
        writer.addHeaderAlias("address", "安装地址");
        for (String s : strings) {
            writer.addHeaderAlias(s, s);
        }
        datas.forEach(x -> {
            strings.forEach(s -> {
                if (!x.containsKey(s)) {
                    x.put(s, "0.00");
                }
            });
        });

        //只导出定义字段
        writer.setOnlyAlias(true);
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(datas, true);
        //out为OutputStream，需要写出到的目标流
        //response为HttpServletResponse对象
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
        response.setHeader("Content-Disposition", "attachment;filename=test.xls");
        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        // 关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }

    /**
     * 复费率报表导出
     */
    @RequestMapping("/multiRateExport")
    public void multiRateExport(HttpServletResponse response, @RequestBody DeviceQueryVo queryVo) throws Exception {
        queryVo.setPageNo(null);
        queryVo.setPageSize(null);
        ResultInfo resultInfo = deviceCollectService.multiRate(queryVo);
        if (resultInfo.getCode() != 0) {
            throw new CustomException(resultInfo.getMsg());
        }
        JSONObject result = (JSONObject) resultInfo.getData();
        List<String> strings = (List<String>) result.get("columns");

        List<JSONObject> datas = (List<JSONObject>) result.get("data");
        BigExcelWriter writer = ExcelUtil.getBigWriter();
        writer.addHeaderAlias("deviceName", "设备名称");
        writer.addHeaderAlias("deviceCode", "设备编码");
        writer.addHeaderAlias("areaName", "所属区域");
        writer.addHeaderAlias("address", "安装地址");
        writer.addHeaderAlias("time", "日期");
        writer.addHeaderAlias("sum", "用电量-总(kWh)");
        writer.addHeaderAlias("peak", "用电量-峰(kWh)");
        writer.addHeaderAlias("gu", "用电量-谷(kWh)");
        writer.addHeaderAlias("ping", "用电量-平(kWh)");
        //只导出定义字段
        writer.setOnlyAlias(true);
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(datas, true);
        //out为OutputStream，需要写出到的目标流
        //response为HttpServletResponse对象
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
        response.setHeader("Content-Disposition", "attachment;filename=test.xls");
        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        // 关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }

    /**
     * 设备类型排名，设备类型占比
     *
     * @return
     * @paramn queryVo
     */
    @RequestMapping(value = "/getDeviceTypeTop.do", method = RequestMethod.POST)
    @ApiOperation("明细分析-设备类型排名，设备类型占比")
    public ResultInfo getDeviceTypeTop(@RequestBody DataSheetVo bean) {


        if (1 == bean.getEnergyType()) {
            bean.setChannelId("ENERGY_TOTAL");
        } else {
            bean.setChannelId("WATER_L");
        }
        bean.setGroupStr("MAX(val)-MIN(val)");
        return ResultInfo.success(dataSheetService.getDeviceTypeTop(bean));
    }


    /**
     * 设备类型排名，设备类型占比
     *
     * @return
     * @paramn queryVo
     */
    @RequestMapping(value = "/getDeviceFloorTop.do", method = RequestMethod.POST)
    @ApiOperation("明细分析-设备类型排名，设备类型占比")
    public ResultInfo getDeviceFloorTop(@RequestBody DataSheetVo bean) {


        if (1 == bean.getEnergyType()) {
            bean.setChannelId("ENERGY_TOTAL");
        } else {
            bean.setChannelId("WATER_L");
        }
        bean.setGroupStr("MAX(val)-MIN(val)");
        return ResultInfo.success(dataSheetService.getDeviceFloorTop(bean));
    }


    /**
     * 设备区域设排名，设备区域设占比
     *
     * @return
     * @paramn queryVo
     */
    @RequestMapping(value = "/getAreaDeviceTop.do", method = RequestMethod.POST)
    @ApiOperation("明细分析-设备区域设排名，设备区域设占比")
    public ResultInfo getAreaDeviceTop(@RequestBody DataSheetVo bean) {


        if (1 == bean.getEnergyType()) {
            bean.setChannelId("ENERGY_TOTAL");
        } else {
            bean.setChannelId("WATER_L");
        }
        bean.setGroupStr("DIFFERENCE(LAST(val))");
        return ResultInfo.success(dataSheetService.getAreaDeviceTop(bean));
    }

    /**
     * 区域设备能耗报表
     *
     * @return
     * @paramn queryVo
     */
    @RequestMapping(value = "/getAreaDeviceReport.do", method = RequestMethod.POST)
    @ApiOperation("区域设备能耗报表")
    public ResultInfo getAreaDeviceReport(@RequestBody DataSheetVo bean) {
        if (1 == bean.getEnergyType()) {
            bean.setChannelId("ENERGY_TOTAL");
        } else {
            bean.setChannelId("WATER_L");
        }
        bean.setGroupStr("DIFFERENCE(LAST(val))");
        return ResultInfo.success(dataSheetService.getAreaDeviceReport(bean));
    }


    @PostMapping("/getFloorList")
    public ResultInfo getFloorList(SysBuildFloor bean) {
        QueryWrapper<SysBuildFloor> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        queryWrapper.orderByAsc("sort");
        if (StringUtils.isNotNull(bean)) {
            if (StringUtils.isNotNull(bean.getDictBuilding())) {
                queryWrapper.eq("dictBuilding", bean.getDictBuilding());
            }
        }
        List<SysBuildFloor> sysBuildList = sysBuildFloorService.list(queryWrapper);
        return ResultInfo.success(sysBuildList);
    }


    /**
     * 修改设备分项内容
     *
     * @param
     * @return
     */
    @RequestMapping("/updSubitem")
    @ApiOperation("编辑设备分项")
    public ResultInfo updSubitem(Integer id, Integer subitem) {
        DeviceCollect deviceCollect = deviceCollectService.getById(id);
        deviceCollect.setSubitem(subitem);
        boolean b = deviceCollectService.updateById(deviceCollect);
        if (b) {
            return ResultInfo.success("修改成功");
        }
        return ResultInfo.error("修改失败");
    }


    /**
     *
     *能耗预警 大额用电设备
     * @author wzn
     * @date 2024/11/25 15:58
     */
    @RequestMapping("/highValueElectricalEquipment")
    public ResultInfo highValueElectricalEquipment() {
        return ResultInfo.success( deviceCollectService.highValueElectricalEquipment());
    }


    /**
     *近7日用电趋势
     *
     * @author wzn
     * @date 2024/11/25 17:31
     */
    @RequestMapping("/consumptionTrend")
    public ResultInfo consumptionTrend(String id) {
        // 获取今天的日期
        LocalDate today = LocalDate.now();

        // 计算7天前的日期
        LocalDate sevenDaysAgo = today.minusDays(6);

        // 创建日期格式器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 格式化日期
        String formattedSevenDaysAgo = sevenDaysAgo.format(formatter);
        String formattedToday = today.format(formatter);

        DataSheetVo bean = new DataSheetVo();

        bean.setStartTime(formattedSevenDaysAgo+" 00:00:00");
        bean.setEndTime(formattedToday+ " 23:59:59");

        bean.setId(Integer.valueOf(id));
        bean.setGroupStr("time(1d)");
        // 用电设备
        bean.setEnergyType(1);
        // 类型
        bean.setChannelId("ENERGY_TOTAL");
        // 返回值
        bean.setInfluxVal("last(val)-first(val)");

        return ResultInfo.success(dataSheetService.consumptionTrend(bean));
    }



    /**
     *
     *用能规划列表
     * @author wzn
     * @date 2024/11/26 16:17
     */
    @RequestMapping("/getList")
    public ResultInfo getList(DeviceEnergyUse deviceEnergyUse) {
        return ResultInfo.success(deviceEnergyUseService.getList(deviceEnergyUse));
    }

    /**
     *
     *调整定额
     * @author wzn
     * @date 2024/11/26 17:25
     */
    @RequestMapping("/updateVal")
    public ResultInfo updateVal(DeviceEnergyUse deviceEnergyUse) {
        deviceEnergyUseService.updateById(deviceEnergyUse);
        return ResultInfo.success();
    }

    /**
     *
     *去年同期定额值
     * @author wzn
     * @date 2024/11/26 17:30
     */
    @RequestMapping("/theSamePeriodLastYear")
    public ResultInfo theSamePeriodLastYear(DeviceEnergyUse deviceEnergyUse) {
        return ResultInfo.success(deviceEnergyUseService.theSamePeriodLastYear(deviceEnergyUse));
    }


    /**
     *
     *用能异常 数据展示
     * @author wzn
     * @date 2024/11/27 11:48
     */
    @RequestMapping("/info")
    public ResultInfo info() {
        return ResultInfo.success(deviceEnergyUseService.info());
    }


    /**
     *
     *空载电流平均值
     * @author wzn
     * @date 2024/11/28 14:28
     */
    @RequestMapping("/noLoadCurrent")
    public ResultInfo noLoadCurrent() {
        EnergyUseAnomalyVo energyUseAnomalyVo  = new EnergyUseAnomalyVo();
        Double I_A = (Double) redisUtils.getBeanValue("I_A");
        if(null == I_A) {
            energyUseAnomalyVo = deviceEnergyUseService.noLoadCurrent() ;
            redisUtils.setBeanValue("I_A", energyUseAnomalyVo.getI_A());
            redisUtils.setBeanValue("I_B", energyUseAnomalyVo.getI_B());
            redisUtils.setBeanValue("I_C", energyUseAnomalyVo.getI_C());
        }else {
            Double I_B = (Double) redisUtils.getBeanValue("I_B");
            Double I_C = (Double) redisUtils.getBeanValue("I_C");
            energyUseAnomalyVo.setI_A(I_A);
            energyUseAnomalyVo.setI_B(I_B);
            energyUseAnomalyVo.setI_C(I_C);

        }
        return ResultInfo.success(energyUseAnomalyVo);
    }


    /**
     *
     *每月平均电流
     * @author wzn
     * @date 2024/11/29 17:02
     */
    @RequestMapping("/averageMonthlyCurrent")
    public ResultInfo averageMonthlyCurrent() {
        return ResultInfo.success(deviceEnergyUseService.averageMonthlyCurrent());
    }


    /**
     *
     *分项月定额列表
     * @author wzn
     * @date 2024/12/02 16:40
     */
    @RequestMapping("/getSubValue")
    public ResultInfo getSubValue(String year) {
        return ResultInfo.success(deviceEnergyUseService.getSubValue( year));
    }

    /**
     *
     *分项调整定额
     * @author wzn
     * @date 2024/12/02 16:59
     */
    @RequestMapping("/updateSubmiteValue")
    public ResultInfo updateSubmiteValue(ConfigDeviceSubitemValue configDeviceSubitemValue) {
        QueryWrapper<ConfigDeviceSubitemValue> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("subitemId", configDeviceSubitemValue.getId()) ;
        queryWrapper.eq("year", configDeviceSubitemValue.getYear()) ;
        configDeviceSubitemValueService.update(configDeviceSubitemValue,queryWrapper) ;
        return ResultInfo.success();
    }


    /**
     *
     *分项月数据
     * @author wzn
     * @date 2024/12/03 14:06
     */
    @RequestMapping("/getSubmitMonth")
    public ResultInfo getSubmitMonth(SubmiteValue submiteValue) {
        return ResultInfo.success( deviceEnergyUseService.getSubmitMonth(submiteValue));
    }



    /**
     *分项月调整额度
     *
     * @author wzn
     * @date 2024/12/03 14:11
     */
    @RequestMapping("/setSubmitMonthValue")
    public ResultInfo setSubmitMonthValue(SubmiteValue submiteValue) {

        deviceEnergyUseService.setSubmitMonthValue(submiteValue) ;
        return ResultInfo.success();
    }



    /**
     *
     *能耗分项监测列表
     * @author wzn
     * @date 2024/12/03 16:19
     */
    @RequestMapping("/itemizedMonitoringList")
    public ResultInfo itemizedMonitoringList() {


        return ResultInfo.success(deviceEnergyUseService.itemizedMonitoringList() );
    }


    /**
     * 实时负荷监测
     * Author wzn
     * Date 2024/4/25 14:58
     */
    @RequestMapping("/getLoad")
    public ResultInfo getLoad() {
        return ResultInfo.success(collectService.getLoad());
    }



    /**
     * 整体指标
     */
    @RequestMapping("/overall.do")
    public ResultInfo overall() {
        Map data = (Map) RedisUtils.getBeanValue("overall");
        if(data == null){
            data = dataSheetService.overall();
            RedisUtils.setBeanValue("overall", data);
        }
        return ResultInfo.success(data);
    }



    /**
     * 变压器检测
     * 最大负荷报表
     */
    @RequestMapping("/loadReport")
    public ResultInfo loadReport(@RequestBody DeviceQueryVo deviceQueryVo) {
        return ResultInfo.success(dataSheetService.loadReport(deviceQueryVo));
    }

    /**
     * 变压器检测
     * 最大负荷报表导出
     */
    @RequestMapping("/loadReportExport")
    public void loadReportExport(HttpServletResponse response, @RequestBody DeviceQueryVo queryVo) throws Exception {
        queryVo.setPageNo(null);
        queryVo.setPageSize(null);
        List<TransformerVo> datas = dataSheetService.loadReport(queryVo);
        BigExcelWriter writer = ExcelUtil.getBigWriter();
        writer.addHeaderAlias("time", "时间");
        writer.addHeaderAlias("rated_capacity", "额定容量(VA)");
        writer.addHeaderAlias("val", "最大运行负荷(VA)");
        writer.addHeaderAlias("load_occupancy", "最大负荷占用率");
        //只导出定义字段
        writer.setOnlyAlias(true);
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(datas, true);
        //out为OutputStream，需要写出到的目标流
        //response为HttpServletResponse对象
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
        response.setHeader("Content-Disposition", "attachment;filename=test.xls");
        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        // 关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }

}
