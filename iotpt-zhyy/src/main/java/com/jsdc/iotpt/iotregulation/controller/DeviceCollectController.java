package com.jsdc.iotpt.iotregulation.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.common.Constants;
import com.jsdc.iotpt.common.DateUtils;
import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.model.ConfigSignalType;
import com.jsdc.iotpt.model.DeviceCollect;
import com.jsdc.iotpt.model.sys.SysBuildFloor;
import com.jsdc.iotpt.service.*;
import com.jsdc.iotpt.service.mqtt.DataSheetService;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.*;

/**
 * todo
 * controller控制器
 */
@RestController
@RequestMapping("/deviceCollect")
@Api(tags = "设备监控")
public class DeviceCollectController {

    @Value("${linkmqtt.ip}")
    private String mqtt_ip;
    @Autowired
    private DeviceCollectService deviceCollectService;
    @Autowired
    private DataSheetService dataSheetService;
    @Autowired
    private SysOrgManageService sysOrgManageService;
    @Autowired
    private SysBuildAreaService areaService;
    @Autowired
    private ConfigDeviceTypeService configDeviceTypeService;
    @Autowired
    private SysBuildFloorService floorService;

    /**
     * @Description: 设备监控
     * @param: [areaId, name, type]
     * @return: com.jsdc.iotpt.vo.ResultInfo
     * @author: 苹果
     * @date: 2023/7/26
     * @time: 9:27
     */
    @PostMapping("/deviceMonitoring")
    @ApiOperation("设备监控")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "areaId",value = "区域",dataType = "Integer"),
            @ApiImplicitParam(name = "floorId",value = "楼层",dataType = "Integer"),
            @ApiImplicitParam(name = "buildId",value = "建筑",dataType = "Integer"),
            @ApiImplicitParam(name = "name",value = "设备名称",dataType = "String"),
            @ApiImplicitParam(name = "type",value = "设备类型",dataType = "Integer"),
            @ApiImplicitParam(name = "areaIds",value = "区域ids",dataType = "List<Integer>"),
            @ApiImplicitParam(name = "buildIds",value = "建筑ids",dataType = "List<Integer>"),
            @ApiImplicitParam(name = "floorIds",value = "楼层ids",dataType = "List<Integer>"),
            @ApiImplicitParam(name = "onLineStatus",value = "设备状态",dataType = "Integer"),
            @ApiImplicitParam(name = "id",value = "设备id",dataType = "Integer"),
            @ApiImplicitParam(name = "pageIndex",value = "当前页数",dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize",value = "显示条数",dataType = "Integer")
    })
    public ResultInfo deviceMonitoring(Integer onLineStatus,Integer areaId,Integer floorId,Integer buildId, String name,
                                       Integer type, Integer pageIndex,Integer pageSize,String areaName,Integer deviceType, String areaIds ,String buildIds ,String floorIds,String id,Integer isAir ){
        List<Integer> deviceTypes = new ArrayList<>();
        if (null != isAir){
            deviceTypes.add(10002);
            deviceTypes.add(200167);
        }
        return ResultInfo.success(deviceCollectService.deviceMonitoring(onLineStatus,areaId,floorId,buildId, name,
                type, pageIndex, pageSize,areaName,deviceType,areaIds,buildIds,floorIds,id,deviceTypes,isAir));
    }

    @PostMapping("/deviceMonitoring2")
    @ApiOperation("设备监控")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "areaId",value = "区域",dataType = "Integer"),
            @ApiImplicitParam(name = "floorId",value = "楼层",dataType = "Integer"),
            @ApiImplicitParam(name = "buildId",value = "建筑",dataType = "Integer"),
            @ApiImplicitParam(name = "name",value = "设备名称",dataType = "String"),
            @ApiImplicitParam(name = "type",value = "设备类型",dataType = "Integer"),
            @ApiImplicitParam(name = "areaIds",value = "区域ids",dataType = "List<Integer>"),
            @ApiImplicitParam(name = "buildIds",value = "建筑ids",dataType = "List<Integer>"),
            @ApiImplicitParam(name = "floorIds",value = "楼层ids",dataType = "List<Integer>"),
            @ApiImplicitParam(name = "onLineStatus",value = "设备状态",dataType = "Integer"),
            @ApiImplicitParam(name = "id",value = "设备id",dataType = "Integer"),
            @ApiImplicitParam(name = "pageIndex",value = "当前页数",dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize",value = "显示条数",dataType = "Integer")
    })
    public ResultInfo deviceMonitoring2(Integer onLineStatus,Integer areaId,Integer floorId,Integer buildId, String name,
                                       Integer type, Integer pageIndex,Integer pageSize,String areaName,Integer deviceType, String areaIds ,String buildIds ,String floorIds,String id,String deviceTypes,Integer isAir ){


        // 使用 split 方法将字符串分割成数组
        String[] itemsArray = deviceTypes.split(",");

        // 将数组转换为 List
        List<String> itemsList = Arrays.asList(itemsArray);
        return ResultInfo.success(deviceCollectService.deviceMonitoring2(onLineStatus,areaId,floorId,buildId, name,
                type, pageIndex, pageSize,areaName,deviceType,areaIds,buildIds,floorIds,id,itemsList,isAir));
    }

    /**
     * @Description: 设备监控--信号上报
     * @param: [type, id]
     * @return: com.jsdc.iotpt.vo.ResultInfo
     * @author: 苹果
     * @date: 2023/7/26
     * @time: 9:55
     */
    @PostMapping("/getInformationReporting")
    @ApiOperation("设备监控--信号上报")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "设备id",dataType = "Integer"),
            @ApiImplicitParam(name = "typeId",value = "设备类型id",dataType = "Integer")
    })
    public ResultInfo getInformationReporting(Integer typeId,Integer id) throws ParseException {
        return ResultInfo.success(dataSheetService.getInformationReporting(typeId, id));
    }


    @PostMapping("/getEntityByTIdGroup")
    public ResultInfo getEntityByTIdGroup(Integer typeId,Integer id) throws ParseException {
        return ResultInfo.success(dataSheetService.getEntityByTIdGroup(typeId, id));
    }


    @PostMapping("/getReportingList")
    @ApiOperation("设备监控--信号上报详情趋势")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "设备id",dataType = "Integer"),
            @ApiImplicitParam(name = "type",value = "设备类型",dataType = "String"),
            @ApiImplicitParam(name = "start",value = "开始时间",dataType = "String"),
            @ApiImplicitParam(name = "end",value = "结束时间",dataType = "String"),
            @ApiImplicitParam(name = "pageIndex",value = "当前页",dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize",value = "条数",dataType = "Integer")
    })
    public ResultInfo getReportingList(Integer id,String type,String start,String end,
                                       Integer pageIndex,Integer pageSize) throws ParseException {
        end = end+" 23:59:59";
        if (StringUtils.isNull(pageIndex)||StringUtils.isNull(pageSize)){
            return  ResultInfo.error("参数验证失败");
        }
        List<Map<String, Object>> reportingList = dataSheetService.getReportingList(id, type, start, end, pageIndex, pageSize);
        reportingList.forEach(x->{
            String time  = x.get("time")+"";
            if(StrUtil.isNotEmpty(time)){
                time = DateUtil.formatDateTime(DateUtil.offsetHour(DateUtil.parse(time),-8));
                x.put("time",time);
            }
        });
        return ResultInfo.success(reportingList);
    }

    @PostMapping("/getReportingCount")
    public ResultInfo getReportingCount(Integer id,String type,String start,String end,
                                       Integer pageIndex,Integer pageSize) throws ParseException {
        end = end+" 23:59:59";
        return ResultInfo.success(dataSheetService.getReportingCount(id, type,start,end,pageIndex,pageSize));
    }

    /**
     * 区域三级树形图
     * Author csx
     * Date 2023/6/30 10:11
     */
    @PostMapping("/areaTreeList")
    public ResultInfo areaTreeList() {
        return ResultInfo.success(sysOrgManageService.areaTreeList());
    }




    @PostMapping("/getList")
    public ResultInfo getList(SysBuildAreaVo vo) {
        return ResultInfo.success(areaService.getList(vo));
    }

    /**
     * 列表
     * @param bean
     * @return
     */
    @PostMapping("/deviceType")
    public ResultInfo getList(ConfigDeviceTypeVo bean) {
        return ResultInfo.success(configDeviceTypeService.getList(bean));
    }

    /**
     * 根据id查看
     */
    @PostMapping("/getById")
    public ResultInfo getById(DeviceCollectVo bean) {
        return ResultInfo.success(deviceCollectService.getDeviceCollect(bean.getId()));
    }

    @PostMapping("/getReportingListAll")
    @ApiOperation("设备监控--信号上报详情趋势")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "设备id",dataType = "Integer"),
            @ApiImplicitParam(name = "type",value = "设备类型",dataType = "String"),
            @ApiImplicitParam(name = "start",value = "开始时间",dataType = "String"),
            @ApiImplicitParam(name = "end",value = "结束时间",dataType = "String"),
            @ApiImplicitParam(name = "pageIndex",value = "当前页",dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize",value = "条数",dataType = "Integer")
    })
    public ResultInfo getReportingList(Integer id,String type,String start,String end) throws ParseException {
        end = end+" 23:59:59";
        List<Map<String, Object>> reportingListAll = dataSheetService.getReportingListAll(id, type, start, end);
//        reportingListAll.forEach(x->{
//            String time  = x.get("time")+"";
//            if(StrUtil.isNotEmpty(time)){
//                time = DateUtil.formatDateTime(DateUtil.offsetHour(DateUtil.parse(time),-8));
//                x.put("time",time);
//            }
//        });
        return ResultInfo.success(reportingListAll);
    }

    @PostMapping("/energyTrend")
    @ApiOperation("设备监控--能耗趋势日月年统计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "设备id",dataType = "Integer")
    })
   public  ResultInfo energyTrend(Integer id){
        return  ResultInfo.success(dataSheetService.energyTrend(id));
   }


    @PostMapping("/energyTrendAVG")
    @ApiOperation("设备监控--能耗趋势日月年统计平均值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "设备id",dataType = "Integer")
    })
    public  ResultInfo energyTrendAVG(Integer id){
        return  ResultInfo.success(dataSheetService.energyTrendAVG(id));
    }


    @PostMapping("/cumulativeEmission")
    @ApiOperation("设备监控--累计用电量/碳排/二氧化碳")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "设备id",dataType = "Integer")
    })
    public  ResultInfo cumulativeEmission(Integer id){
        return  ResultInfo.success(dataSheetService.cumulativeEmission(id));
    }

    @PostMapping("/getElectricityYOY")
    @ApiOperation("设备监控--用电量折线图")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "设备id",dataType = "Integer")
    })
    public ResultInfo getElectricityYOY(@RequestBody DataSheetVo bean){
        //timeType 从前端传
        if (bean.getTimeType()==1){
            bean.setGroupStr("time(1h)");
            bean.setThisXData(DateUtils.getMins(60));
            bean.setLastXData(DateUtils.getMins(60));
        }else {
            bean.setGroupStr("time(1d)");
            bean.setThisXData(DateUtils.getMonthDays(new Date()));
            bean.setLastXData(DateUtils.getLastYearDays(new Date()));
        }
        // 用电设备
        bean.setEnergyType(1);
        // 类型
        bean.setChannelId("ENERGY_TOTAL");
        // 返回值
        bean.setInfluxVal("DIFFERENCE(LAST(val))");


        return ResultInfo.success(dataSheetService.getChannelYOY1(bean));
    }

    @PostMapping("/getElectricityTable")
    @ApiOperation("设备监控--用电量报表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "设备id",dataType = "Integer")
    })
    public ResultInfo getElectricityTable(@RequestBody DataSheetVo bean){
        //timeType 从前端传
        if (bean.getTimeType()==1){
            bean.setGroupStr("time(1h)");
            bean.setThisXData(DateUtils.getMins(60));
            bean.setLastXData(DateUtils.getMins(60));
        }else {
            bean.setGroupStr("time(1d)");
            bean.setThisXData(DateUtils.getMonthDays(new Date()));
            bean.setLastXData(DateUtils.getLastYearDays(new Date()));
        }
        // 用电设备
        bean.setEnergyType(1);
        // 类型
        bean.setChannelId("ENERGY_TOTAL");
        // 返回值
        bean.setInfluxVal("DIFFERENCE(LAST(val))");


        return ResultInfo.success(dataSheetService.getElectricityTable(bean));
    }

    /**
     * 导出
     */
    @GetMapping("/toExport")
    @ApiOperation("设备监控--导出用电量报表")
    public void toExport( DataSheetVo bean, HttpServletResponse response) {
        //timeType 从前端传
        if (bean.getTimeType()==1){
            bean.setGroupStr("time(10m)");
            bean.setThisXData(DateUtils.getMins(10));
            bean.setLastXData(DateUtils.getMins(10));
        }else {
            bean.setGroupStr("time(1d)");
            bean.setThisXData(DateUtils.getMonthDays(new Date()));
            bean.setLastXData(DateUtils.getLastYearDays(new Date()));
        }
        // 用电设备
        bean.setEnergyType(1);
        // 类型
        bean.setChannelId("ENERGY_TOTAL");
        // 返回值
        bean.setInfluxVal("DIFFERENCE(LAST(val))");

        dataSheetService.TableExport(bean, response);
    }


    @PostMapping("remoteControl")
    @ApiOperation("远程控制")
    public ResultInfo remoteControl(@RequestBody DeviceCollectVo vo) {
        // 构建远程控制json命令
        String jsonString = this.deviceCollectService.remoteControl(vo);
        System.out.println(jsonString);
        String result = HttpUtil.post(mqtt_ip+ "/mqtt/remoteControl", jsonString);
        ResultInfo resultInfo = JSON.parseObject(result, ResultInfo.class);
        if (resultInfo.getCode() == 0) {
            return ResultInfo.success();
        }
//        return ResultInfo.success(jsonString);
        return ResultInfo.error("请求失败");

    }

    @PostMapping("getSignalByDevice")
    @ApiOperation("通过设备获取信号")
    public ResultInfo getSignalByDevice(@RequestBody ConfigSignalType configSignalType) {
        Integer id = configSignalType.getId();
        List<ConfigSignalType> configSignalTypeList = this.deviceCollectService.getSignalByDevice(id);
        return ResultInfo.success(configSignalTypeList);
    }

    @PostMapping("getFloorByBId")
    @ApiOperation("通过大楼id获取楼层")
    public ResultInfo getFloorByBId(Integer id) {
        SysBuildFloor vo =new SysBuildFloor();
        vo.setDictBuilding(id);
        List<SysBuildFloor> list = this.floorService.getListByBuild(vo);
        return ResultInfo.success(list);
    }

    /**
     * 分页查询 todo
     *
     * @return
     */
    @RequestMapping("/getPageList")
    @ApiOperation("采集终端-分页查询")
    public ResultInfo getPageList(DeviceCollectVo bean) {
        return ResultInfo.success(deviceCollectService.getPageList(bean));
    }


    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    @RequestMapping("/saveOrUpdate")
    @ApiOperation("添加/编辑采集终端")
    @LogInfo(LogEnums.LOG_DEVICE_COLLECT)
    public ResultInfo saveOrUpdateDeviceCollect(@RequestBody DeviceCollectVo bean) {
        return deviceCollectService.saveOrUpdateDeviceCollect(bean);
    }

    /**
     *  关联设备
     * @param bean
     * @return
     */
    @PostMapping("/bindDevice")
    @ApiOperation("关联设备")
    public ResultInfo bindDevice(DeviceCollectVo bean){
        return deviceCollectService.bindDevice(bean);
    }

    /**
     *  解除关联设备
     * @param bean
     * @return
     */
    @PostMapping("/delBindDevice")
    @ApiOperation("解除关联")
    public ResultInfo delBindDevice(DeviceCollectVo bean){
        return deviceCollectService.delBindDevice(bean);
    }

    /**
     * 删除 todo
     */
    @RequestMapping("/delete")
    @ApiOperation("删除采集终端")
    @LogInfo(LogEnums.LOG_DEVICE_COLLECT)
    public ResultInfo deleteDeviceCollect(DeviceCollect bean) {
        deviceCollectService.deleteDeviceCollect(bean.getId());
        return ResultInfo.success("删除成功", new LogVo("删除"));
    }

    /**
     * 批量编辑
     */
    @RequestMapping("/batchUpdate")
    @ApiOperation("批量编辑采集终端")
    @LogInfo(LogEnums.LOG_DEVICE_COLLECT)
    public ResultInfo batchUpdateDeviceCollect(@RequestBody DeviceCollectVo bean) {
        deviceCollectService.batchEditDeviceCollect(bean);
        return ResultInfo.success("操作成功", new LogVo("批量编辑"));
    }

    /**
     * 导出
     */
    @RequestMapping(value = "/toExportTemplate", method = RequestMethod.GET)
    @ApiOperation("导出采集终端")
    public void toExportTemplate(DeviceCollectVo vo, HttpServletResponse response) {
        deviceCollectService.exportDeviceCollect(vo, response);
    }

    @RequestMapping(value = "/deviceByType", method = RequestMethod.GET)
    @ApiOperation("设备监控设备状态统计")
    public ResultInfo deviceByType(String type){
        return  ResultInfo.success(deviceCollectService.deviceByType(type));
    }


    @RequestMapping(value = "/airLog", method = RequestMethod.GET)
    @ApiOperation("空调日志")
    public  ResultInfo airLog(SysLogVo vo){
        return  ResultInfo.success(deviceCollectService.airLog(vo));
    }



    @RequestMapping(value = "/air17Log", method = RequestMethod.GET)
    @ApiOperation("智慧插座日志")
    public  ResultInfo air_17_Log(SysLogVo vo){
        return  ResultInfo.success(deviceCollectService.air17Log(vo));
    }

    @PostMapping("/getEnetgyAll")
    public ResultInfo getEnetgyAll(String id){
        return  ResultInfo.success(deviceCollectService.getEnetgyAll(id));
    }


    @PostMapping("/getReportingListPage")
    @ApiOperation("设备监控-分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "设备id",dataType = "Integer"),
            @ApiImplicitParam(name = "type",value = "设备类型",dataType = "String"),
            @ApiImplicitParam(name = "start",value = "开始时间",dataType = "String"),
            @ApiImplicitParam(name = "end",value = "结束时间",dataType = "String"),
            @ApiImplicitParam(name = "pageIndex",value = "当前页",dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize",value = "条数",dataType = "Integer")
    })
    public ResultInfo getReportingListPage(Integer id,String type,String start,String end,
                                       Integer pageIndex,Integer pageSize) throws ParseException {
        end = end+" 23:59:59";
        if (StringUtils.isNull(pageIndex)||StringUtils.isNull(pageSize)){
            return  ResultInfo.error("参数验证失败");
        }
        List<Map<String, Object>> reportingList = dataSheetService.getReportingList(id, type, start, end, pageIndex, pageSize);
        reportingList.forEach(x->{
            String time  = x.get("time")+"";
            if(StrUtil.isNotEmpty(time)){
                time = DateUtil.formatDateTime(DateUtil.offsetHour(DateUtil.parse(time),-8));
                x.put("time",time);
            }
        });
        Page page =new Page(pageIndex,pageSize);
        page.setRecords(reportingList);
        page.setTotal(dataSheetService.getReportingCount(id, type,start,end,pageIndex,pageSize));
        return ResultInfo.success(page);
    }



    @PostMapping("/energyTrend_ZHCZ")
    @ApiOperation("设备监控--智能插座能耗趋势日月年统计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "设备id",dataType = "Integer")
    })
    public  ResultInfo energyTrend_ZHCZ(Integer id){
        return  ResultInfo.success(dataSheetService.energyTrend_ZHCZ(id));
    }
}
