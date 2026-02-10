package com.jsdc.iotpt.service.mqtt;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dahuatech.icc.exception.ClientException;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.DateUtils;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.mapper.*;
import com.jsdc.iotpt.model.*;
import com.jsdc.iotpt.model.sys.SysBuild;
import com.jsdc.iotpt.model.sys.SysBuildArea;
import com.jsdc.iotpt.model.sys.SysBuildFloor;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.service.*;
import com.jsdc.iotpt.service.influxDB.InfluxdbService;
import com.jsdc.iotpt.util.JsdcDateUtil;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.*;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.util.Strings;
import org.influxdb.dto.QueryResult;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.jsdc.iotpt.common.DateUtils.getLastYearToday;
import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * ClassName: DataSheetService
 * Description: 设备采集数据
 * date: 2023/5/19 11:54
 *
 * @author bn
 */
@Service
@Transactional
public class DataSheetService extends BaseService<DataSheet> {

    @Autowired
    private DataSheetMapper dataSheetMapper;
    @Autowired
    private ConfigSignalTypeMapper configSignalTypeMapper;
    @Autowired
    AlertSheetMapper alertSheetMapper;
    @Autowired
    private InfluxdbService influxdbService;
    @Autowired
    private DeviceCollectMapper deviceCollectMapper;
    @Autowired
    private DeviceCollectService deviceCollectService;
    @Autowired
    private ConfigDeviceSubitemMapper configDeviceSubitemMapper;
    @Autowired
    private SysBuildAreaMapper sysBuildAreaMapper;
    @Autowired
    private SysBuildAreaService sysBuildAreaService;
    @Autowired
    private ConfigDeviceTypeService configDeviceTypeService;
    @Autowired
    private SysDictMapper sysDictMapper;
    @Autowired
    private ConfigDeviceSignalMapMapper configDeviceSignalMapMapper;
    @Autowired
    private SysBuildMapper sysBuildMapper;
    @Autowired
    private SysBuildFloorService sysBuildFloorService;

    @Autowired
    private ConfigSignalTypeItemMapper itemMapper;
    @Autowired
    private ConfigDeviceSubitemService configDeviceSubitemService;
    @Autowired
    private SysBuildService sysBuildService;

    @Autowired
    private SmartEnergyReportService smartEnergyReportService;

    /**
     * 分页查询
     */
    public Page<DataSheet> getPageList(DataSheetVo vo) {
        QueryWrapper<DataSheet> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotNull(vo)) {
            if (StringUtils.isNotEmpty(vo.getGateWayId())) {
                queryWrapper.eq("gateWayId", vo.getGateWayId());
            }
            if (StringUtils.isNotNull(vo.getType())) {
                queryWrapper.eq("type", vo.getType());
            }
            if (StringUtils.isNotEmpty(vo.getDeviceId())) {
                queryWrapper.eq("deviceId", vo.getDeviceId());
            }
            if (StringUtils.isNotEmpty(vo.getChannelId())) {
                queryWrapper.eq("channelId", vo.getChannelId());
            }
            if (StringUtils.isNotEmpty(vo.getPoint())) {
                queryWrapper.eq("point", vo.getPoint());
            }
            if (StringUtils.isNotEmpty(vo.getStartTime())) {
                queryWrapper.ge("TO_CHAR(time, 'yyyy-MM-dd')", vo.getStartTime());
            }
            if (StringUtils.isNotEmpty(vo.getEndTime())) {
                queryWrapper.le("TO_CHAR(time, 'yyyy-MM-dd')", vo.getEndTime());
            }
            //
            if (StringUtils.isNotEmpty(vo.getCodes())) {
                queryWrapper.in("channelId", vo.getCodes());
            }
        }
        queryWrapper.orderByDesc("time");
        Page<DataSheet> page = dataSheetMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);
        for (DataSheet dataSheet : page.getRecords()) {
            if (StringUtils.isNotEmpty(dataSheet.getChannelId())) {
                QueryWrapper<ConfigSignalType> configSignalTypeQueryWrapper = new QueryWrapper<>();
                configSignalTypeQueryWrapper.eq("isDel", 0);
                configSignalTypeQueryWrapper.eq("signalTypeCode", dataSheet.getChannelId());
                List<ConfigSignalType> list = configSignalTypeMapper.selectList(configSignalTypeQueryWrapper);
                if (list.size() > 0) {
                    dataSheet.setSignalTypeName(list.get(0).getSignalTypeName());
                }
            }

        }
        return page;
    }

    /**
     * 分页查询
     */
    public JSONObject getPageList2(DataSheetVo vo) {
        JSONObject jsonObject = new JSONObject();
        String sql = "";
        if (StringUtils.isNotNull(vo)) {
            if (StringUtils.isNotEmpty(vo.getGateWayId())) {
                sql += " and deviceGatewayId='" + vo.getGateWayId() + "'";
            }
            if (StringUtils.isNotEmpty(vo.getType())) {
                sql += " and deviceType='" + vo.getType() + "'";
            }
            if (StringUtils.isNotEmpty(vo.getDeviceId())) {
                sql += " and deviceCollectId='" + vo.getDeviceId() + "'";
            }
            if (StringUtils.isNotEmpty(vo.getChannelId())) {
                sql += " and channelId='" + vo.getChannelId() + "'";
            }
            if (StringUtils.isNotEmpty(vo.getStartTime())) {
                sql += " and time>='" + new DateTime(vo.getStartTime()).toString("yyyy-MM-dd 00:00:00") + "'";
            }
            if (StringUtils.isNotEmpty(vo.getEndTime())) {
                sql += " and time<'" + new DateTime(vo.getEndTime()).plusDays(1).toString("yyyy-MM-dd 00:00:00") + "'";
            }
            if (StringUtils.isNotEmpty(vo.getCodes()) && vo.getCodes().size() > 0) {
                String channelIds = "~/";
                for (int i = 0; i < vo.getCodes().size(); i++) {
                    channelIds = channelIds + "^" + vo.getCodes().get(i) + "$|";
                }
                channelIds = channelIds.substring(0, channelIds.lastIndexOf("|"));
                channelIds += "/";
                sql += " and channelId=" + channelIds;
            }
        }
        //查询每项分组的用电量
        QueryResult query = influxdbService.query(
                "SELECT dataTime, time, val, channelId from datasheet where 1=1" + sql +
                        " order by time desc" +
                        " limit " + vo.getPageSize() + " offset " + (vo.getPageNo() - 1) * vo.getPageSize());
        //解析列表类查询
        List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
        //转换成实体对象
        List<DataSheet> data = JSONObject.parseArray(JSONObject.toJSONString(maps), DataSheet.class);
        for (DataSheet dataSheet : data) {
            if (StringUtils.isNotEmpty(dataSheet.getChannelId())) {
                QueryWrapper<ConfigSignalType> configSignalTypeQueryWrapper = new QueryWrapper<>();
                configSignalTypeQueryWrapper.eq("isDel", 0);
                configSignalTypeQueryWrapper.eq("signalTypeCode", dataSheet.getChannelId());
                List<ConfigSignalType> list = configSignalTypeMapper.selectList(configSignalTypeQueryWrapper);
                if (list.size() > 0) {
                    dataSheet.setSignalTypeName(list.get(0).getSignalTypeName());
                }
            }

        }

        QueryResult queryCount = influxdbService.query(
                "SELECT count(val) from datasheet where 1=1" + sql);
        //解析列表类查询
        long count = influxdbService.countResultProcess(queryCount);

        jsonObject.put("records", data);
        jsonObject.put("total", count);
        return jsonObject;
    }


    //用电量评价

    public List<Map<String, Object>> energyEvaluate() {
        LambdaQueryWrapper<DeviceCollect> wrapper = new LambdaQueryWrapper<>();
        Double toDay1 = 0.00;
        Double lastDay1 = 0.00;
        Double toWeek1 = 0.00;
        Double lastWeek1 = 0.00;
        Double toMonth1 = 0.00;
        Double lastMonth1 = 0.00;
        Double toYear1 = 0.00;
        Double lastYear1 = 0.00;
        wrapper.eq(DeviceCollect::getIsTotalDevice, 1).eq(DeviceCollect::getIsDel, 0).isNotNull(DeviceCollect::getLogicalBuildId).eq(DeviceCollect::getLogicalFloorId, 0).eq(DeviceCollect::getLogicalAreaId, 0).eq(DeviceCollect::getDeviceType, 10005);
//        wrapper.eq(DeviceCollect::getIsDel, 0).eq(DeviceCollect::getId, 61);
        List<DeviceCollect> collect = this.deviceCollectMapper.selectList(wrapper);
        for (DeviceCollect deviceCollect : collect) {
            QueryResult toDay = influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(new Date(),
                    JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(new Date(),
                    JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            QueryResult lastDay =
                    influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-1,
                                    new Date()),
                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-1, new Date()),
                            JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            QueryResult toWeek =
                    influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(new Date(),
                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstWeekDay(new Date()),
                            JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            QueryResult lastWeek =
                    influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-7,
                                    new Date()),
                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-7,
                                    JsdcDateUtil.getFirstWeekDay(new Date())),
                            JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            QueryResult toMonth =
                    influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(new Date(),
                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(new Date(),
                            Calendar.DAY_OF_MONTH), JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            QueryResult lastMonth =
                    influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-30,
                                    new Date()), JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
                            JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(JsdcDateUtil.lastDayTime(-30, new Date()),
                                    Calendar.DAY_OF_MONTH), JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            QueryResult toYear = influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(new Date(),
                    JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(new Date(),
                    Calendar.DAY_OF_YEAR), JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            QueryResult lastYear =
                    influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-365,
                                    new Date()), JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
                            JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(JsdcDateUtil.lastDayTime(-365, new Date()),
                                    Calendar.DAY_OF_YEAR), JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            List<Map<String, Object>> toDayList = influxdbService.queryResultProcess(toDay);
            List<Map<String, Object>> lastDayList = influxdbService.queryResultProcess(lastDay);
            List<Map<String, Object>> toWeekList = influxdbService.queryResultProcess(toWeek);
            List<Map<String, Object>> lastWeekList = influxdbService.queryResultProcess(lastWeek);
            List<Map<String, Object>> toMonthList = influxdbService.queryResultProcess(toMonth);
            List<Map<String, Object>> lastMonthList = influxdbService.queryResultProcess(lastMonth);
            List<Map<String, Object>> toYearList = influxdbService.queryResultProcess(toYear);
            List<Map<String, Object>> lastYearList = influxdbService.queryResultProcess(lastYear);
            toDay1 += Double.parseDouble(this.spreadResultProcess(toDayList));
            lastDay1 += Double.parseDouble(this.spreadResultProcess(lastDayList));
            toWeek1 += Double.parseDouble(this.spreadResultProcess(toWeekList));
            lastWeek1 += Double.parseDouble(this.spreadResultProcess(lastWeekList));
            toMonth1 += Double.parseDouble(this.spreadResultProcess(toMonthList));
            lastMonth1 += Double.parseDouble(this.spreadResultProcess(lastMonthList));
            toYear1 += Double.parseDouble(this.spreadResultProcess(toYearList));
            lastYear1 += Double.parseDouble(this.spreadResultProcess(lastYearList));
        }

        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("toDay", new BigDecimal(toDay1).setScale(2, ROUND_HALF_UP).toString());
        map.put("lastDay", new BigDecimal(lastDay1).setScale(2, ROUND_HALF_UP).toString());
        map.put("toWeek", new BigDecimal(toWeek1).setScale(2, ROUND_HALF_UP).toString());
        map.put("lastWeek", new BigDecimal(lastWeek1).setScale(2, ROUND_HALF_UP).toString());
        map.put("toMonth", new BigDecimal(toMonth1).setScale(2, ROUND_HALF_UP).toString());
        map.put("lastMonth", new BigDecimal(lastMonth1).setScale(2, ROUND_HALF_UP).toString());
        map.put("toYear", new BigDecimal(toYear1).setScale(2, ROUND_HALF_UP).toString());
        map.put("lastYear", new BigDecimal(lastYear1).setScale(2, ROUND_HALF_UP).toString());
        list.add(map);
        return list;
    }


    //用电量评价根据楼层分组
    public List<EnergyEvaluateByFloorVo> energyEvaluateByFloor(String type) {
        List<EnergyEvaluateByFloorVo> energyEvaluateByFloorVos = new ArrayList<>();
        LambdaQueryWrapper<SysBuildFloor> floorWrapper = new LambdaQueryWrapper<>();
        floorWrapper.eq(SysBuildFloor::getIsDel, 0).orderByAsc(SysBuildFloor::getSort);
        List<SysBuildFloor> sysBuildFloors = sysBuildFloorService.list(floorWrapper);
        for (SysBuildFloor sysBuildFloor : sysBuildFloors) {
            EnergyEvaluateByFloorVo evaluateByFloorVo = new EnergyEvaluateByFloorVo();
            evaluateByFloorVo.setFloor(sysBuildFloor.getId() + "");
            evaluateByFloorVo.setFloorName(sysBuildFloor.getFloorName() + "");
            energyEvaluateByFloorVos.add(evaluateByFloorVo);
        }
        LambdaQueryWrapper<DeviceCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeviceCollect::getIsTotalDevice, 1).eq(DeviceCollect::getIsDel, 0).isNotNull(DeviceCollect::getLogicalBuildId).isNotNull(DeviceCollect::getLogicalFloorId).eq(DeviceCollect::getLogicalAreaId, 0)
                .eq(DeviceCollect::getDeviceType, 10005);
//        wrapper.eq(DeviceCollect::getId, 61);
        List<DeviceCollect> collect = this.deviceCollectMapper.selectList(wrapper);
        for (DeviceCollect deviceCollect : collect) {
//            boolean isflag=true;
            QueryResult toDay = influxdbService.query(this.getEnergyAndWaterByFloor(JsdcDateUtil.getDateFormat(new Date(),
                    JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(new Date(),
                    JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect, type));
            QueryResult lastDay =
                    influxdbService.query(this.getEnergyAndWaterByFloor(JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-1,
                                    new Date()),
                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-1,
                                    new Date()),
                            JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect, type));
            QueryResult toWeek =
                    influxdbService.query(this.getEnergyAndWaterByFloor(JsdcDateUtil.getDateFormat(new Date(),
                                    JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
                            JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstWeekDay(new Date()),
                                    JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect, type));
            QueryResult lastWeek =
                    influxdbService.query(this.getEnergyAndWaterByFloor(JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-7,
                                    new Date()),
                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-7,
                                    JsdcDateUtil.getFirstWeekDay(new Date())),
                            JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect, type));
            QueryResult toMonth =
                    influxdbService.query(this.getEnergyAndWaterByFloor(JsdcDateUtil.getDateFormat(new Date(),
                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(new Date(),
                            Calendar.DAY_OF_MONTH), JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect, type));
            QueryResult lastMonth =
                    influxdbService.query(this.getEnergyAndWaterByFloor(JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-30,
                                    new Date()), JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
                            JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(JsdcDateUtil.lastDayTime(-30, new Date()),
                                    Calendar.DAY_OF_MONTH), JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect, type));
            QueryResult toYear = influxdbService.query(this.getEnergyAndWaterByFloor(JsdcDateUtil.getDateFormat(new Date(),
                    JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(new Date(),
                    Calendar.DAY_OF_YEAR), JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect, type));
            QueryResult lastYear =
                    influxdbService.query(this.getEnergyAndWaterByFloor(JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-365,
                                    new Date()), JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
                            JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(JsdcDateUtil.lastDayTime(-365, new Date()),
                                    Calendar.DAY_OF_YEAR), JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect, type));
            List<Map<String, Object>> toDayList = influxdbService.queryResultProcess(toDay);
            List<Map<String, Object>> lastDayList = influxdbService.queryResultProcess(lastDay);
            List<Map<String, Object>> toWeekList = influxdbService.queryResultProcess(toWeek);
            List<Map<String, Object>> lastWeekList = influxdbService.queryResultProcess(lastWeek);
            List<Map<String, Object>> toMonthList = influxdbService.queryResultProcess(toMonth);
            List<Map<String, Object>> lastMonthList = influxdbService.queryResultProcess(lastMonth);
            List<Map<String, Object>> toYearList = influxdbService.queryResultProcess(toYear);
            List<Map<String, Object>> lastYearList = influxdbService.queryResultProcess(lastYear);
            //判断如果返回数据集合里已经有相应的楼层存在了就把数值加起来
            for (EnergyEvaluateByFloorVo vo : energyEvaluateByFloorVos) {
                if (vo.getFloor().equals(deviceCollect.getLogicalFloorId() + "")) {
                    vo.setToDay(Double.parseDouble(vo.getToDay()) + Double.parseDouble(this.spreadResultProcess(toDayList)) + "");
                    vo.setLastDay(Double.parseDouble(vo.getLastDay()) + Double.parseDouble(this.spreadResultProcess(lastDayList)) + "");
                    vo.setToWeek(Double.parseDouble(vo.getToWeek()) + Double.parseDouble(this.spreadResultProcess(toWeekList)) + "");
                    vo.setLastWeek(Double.parseDouble(vo.getLastWeek()) + Double.parseDouble(this.spreadResultProcess(lastWeekList)) + "");
                    vo.setToMonth(Double.parseDouble(vo.getToMonth()) + Double.parseDouble(this.spreadResultProcess(toMonthList)) + "");
                    vo.setLastMonth(Double.parseDouble(vo.getLastMonth()) + Double.parseDouble(this.spreadResultProcess(lastMonthList)) + "");
                    vo.setToYear(Double.parseDouble(vo.getToYear()) + Double.parseDouble(this.spreadResultProcess(toYearList)) + "");
                    vo.setLastYear(Double.parseDouble(vo.getLastYear()) + Double.parseDouble(this.spreadResultProcess(lastYearList)) + "");
//                    isflag=false;
                    break;
                }
            }
//            if (isflag){//没有相同楼宇电表的时候就添加
//                EnergyEvaluateByFloorVo evaluateByFloorVo = new EnergyEvaluateByFloorVo();
//                evaluateByFloorVo.setToDay(this.spreadResultProcess(toDayList) + "");
//                evaluateByFloorVo.setLastDay(this.spreadResultProcess(lastDayList) + "");
//                evaluateByFloorVo.setToWeek(this.spreadResultProcess(toWeekList) + "");
//                evaluateByFloorVo.setLastWeek(this.spreadResultProcess(lastWeekList) + "");
//                evaluateByFloorVo.setToMonth(this.spreadResultProcess(toMonthList) + "");
//                evaluateByFloorVo.setLastMonth(this.spreadResultProcess(lastMonthList) + "");
//                evaluateByFloorVo.setToYear(this.spreadResultProcess(toYearList) + "");
//                evaluateByFloorVo.setLastYear(this.spreadResultProcess(lastYearList) + "");
//                evaluateByFloorVo.setFloor(deviceCollect.getLogicalFloorId()+"");
//                energyEvaluateByFloorVos.add(evaluateByFloorVo);
//            }
        }
        return energyEvaluateByFloorVos;
    }

    //各楼层水电对比
    public List<WaterElectricityByFloorVo> waterElectricityByFloor(Integer timeType) {
        List<WaterElectricityByFloorVo> waterElectricityByFloorVos = new ArrayList<>();
        LambdaQueryWrapper<SysBuildFloor> floorWrapper = new LambdaQueryWrapper<>();
        floorWrapper.eq(SysBuildFloor::getIsDel, 0);
        List<SysBuildFloor> sysBuildFloors = sysBuildFloorService.list(floorWrapper);
        for (SysBuildFloor sysBuildFloor : sysBuildFloors) {
            WaterElectricityByFloorVo waterElectricityByFloorVo = new WaterElectricityByFloorVo();
            waterElectricityByFloorVo.setFloor(sysBuildFloor.getId() + "");
            waterElectricityByFloorVo.setFloorName(sysBuildFloor.getFloorName() + "");
            waterElectricityByFloorVos.add(waterElectricityByFloorVo);
        }

        QueryResult electricity = null;
        QueryResult water = null;
        LambdaQueryWrapper<DeviceCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeviceCollect::getIsTotalDevice, 0).eq(DeviceCollect::getIsDel, 0).isNotNull(DeviceCollect::getLogicalBuildId).isNotNull(DeviceCollect::getLogicalFloorId).eq(DeviceCollect::getLogicalAreaId, 0).eq(DeviceCollect::getDeviceType, 10005);
//        wrapper.eq(DeviceCollect::getId, 61);
        List<DeviceCollect> collect = this.deviceCollectMapper.selectList(wrapper);
        LambdaQueryWrapper<DeviceCollect> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(DeviceCollect::getIsTotalDevice, 0).eq(DeviceCollect::getIsDel, 0).isNotNull(DeviceCollect::getLogicalBuildId).isNotNull(DeviceCollect::getLogicalFloorId).eq(DeviceCollect::getLogicalAreaId, 0).eq(DeviceCollect::getDeviceType, 10006);
//        wrapper1.eq(DeviceCollect::getId, 61);
        List<DeviceCollect> collect1 = this.deviceCollectMapper.selectList(wrapper1);
        for (DeviceCollect deviceCollect1 : collect1) {
//            boolean isflag=true;
            switch (timeType) {
                case 1:
//                        electricity =
//                                influxdbService.query(this.getEnergyAndWaterByFloor(JsdcDateUtil.getDateFormat(new Date(),
//                                        JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(new Date(),
//                                        JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect1, "ENERGY_TOTAL"));
                    water = influxdbService.query(this.getEnergyAndWaterByFloor(JsdcDateUtil.getDateFormat(new Date(),
                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(new Date(),
                            JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect1, "WATER_L"));
                    break;
                case 2:
//                        electricity =
//                                influxdbService.query(this.getEnergyAndWaterByFloor(JsdcDateUtil.getDateFormat(new Date(),
//                                        JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
//                                        JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstWeekDay(new Date()),
//                                                JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect1, "ENERGY_TOTAL"));
                    water = influxdbService.query(this.getEnergyAndWaterByFloor(JsdcDateUtil.getDateFormat(new Date(),
                                    JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
                            JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstWeekDay(new Date()),
                                    JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect1, "WATER_L"));
                    break;
                case 3:
//                        electricity =
//                                influxdbService.query(this.getEnergyAndWaterByFloor(JsdcDateUtil.getDateFormat(new Date(),
//                                        JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
//                                        JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(new Date(),
//                                                Calendar.DAY_OF_MONTH), JsdcDateUtil.FULL_SPLIT_PATTERN),
//                                        deviceCollect1, "ENERGY_TOTAL"));
                    water = influxdbService.query(this.getEnergyAndWaterByFloor(JsdcDateUtil.getDateFormat(new Date(),
                                    JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
                            JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(new Date(),
                                    Calendar.DAY_OF_MONTH), JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect1,
                            "WATER_L"));
                    break;
                case 4:
//                        electricity =
//                                influxdbService.query(this.getEnergyAndWaterByFloor(JsdcDateUtil.getDateFormat(new Date(),
//                                        JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
//                                        JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(new Date(),
//                                                Calendar.DAY_OF_YEAR), JsdcDateUtil.FULL_SPLIT_PATTERN),
//                                        deviceCollect, "ENERGY_TOTAL"));
                    water = influxdbService.query(this.getEnergyAndWaterByFloor(JsdcDateUtil.getDateFormat(new Date(),
                                    JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
                            JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(new Date(),
                                    Calendar.DAY_OF_YEAR), JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect1,
                            "WATER_L"));
                    break;
            }
//                List<Map<String, Object>> electricityList = influxdbService.queryResultProcess(electricity);
            List<Map<String, Object>> waterList = influxdbService.queryResultProcess(water);
            //判断如果返回数据集合里已经有相应的楼层存在了就把数值加起来
            for (WaterElectricityByFloorVo vo : waterElectricityByFloorVos) {
                if (vo.getFloor().equals(deviceCollect1.getLogicalFloorId() + "")) {
                    vo.setWater(Double.parseDouble(vo.getWater()) + Double.parseDouble(this.spreadResultProcess(waterList)) + "");
//                    isflag=false;
                    break;
                }
            }
//            if (isflag){//没有相同楼宇电表的时候就添加
//                WaterElectricityByFloorVo energyEvaluateByFloorVo = new WaterElectricityByFloorVo();
//                energyEvaluateByFloorVo.setFloor(deviceCollect1.getLogicalFloorId() + "");
//                energyEvaluateByFloorVo.setWater(this.spreadResultProcess(waterList) + "");
//                energyEvaluateByFloorVo.setElectricity("0");
//                waterElectricityByFloorVos.add(energyEvaluateByFloorVo);
//            }
        }
        for (DeviceCollect deviceCollect : collect) {
//            boolean isflag=true;
            switch (timeType) {
                case 1:
                    electricity =
                            influxdbService.query(this.getEnergyAndWaterByFloor(JsdcDateUtil.getDateFormat(new Date(),
                                    JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(new Date(),
                                    JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect, "ENERGY_TOTAL"));
//                    water = influxdbService.query(this.getEnergyAndWaterByFloor(JsdcDateUtil.getDateFormat(new Date(),
//                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(new Date(),
//                            JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect, "WATER_L"));
                    break;
                case 2:
                    electricity =
                            influxdbService.query(this.getEnergyAndWaterByFloor(JsdcDateUtil.getDateFormat(new Date(),
                                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
                                    JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstWeekDay(new Date()),
                                            JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect, "ENERGY_TOTAL"));
//                    water = influxdbService.query(this.getEnergyAndWaterByFloor(JsdcDateUtil.getDateFormat(new Date(),
//                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
//                            JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstWeekDay(new Date()),
//                                    JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect, "WATER_L"));
                    break;
                case 3:
                    electricity =
                            influxdbService.query(this.getEnergyAndWaterByFloor(JsdcDateUtil.getDateFormat(new Date(),
                                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
                                    JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(new Date(),
                                            Calendar.DAY_OF_MONTH), JsdcDateUtil.FULL_SPLIT_PATTERN),
                                    deviceCollect, "ENERGY_TOTAL"));
//                    water = influxdbService.query(this.getEnergyAndWaterByFloor(JsdcDateUtil.getDateFormat(new Date(),
//                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
//                            JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(new Date(),
//                                    Calendar.DAY_OF_MONTH), JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect,
//                            "WATER_L"));
                    break;
                case 4:
                    electricity =
                            influxdbService.query(this.getEnergyAndWaterByFloor(JsdcDateUtil.getDateFormat(new Date(),
                                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
                                    JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(new Date(),
                                            Calendar.DAY_OF_YEAR), JsdcDateUtil.FULL_SPLIT_PATTERN),
                                    deviceCollect, "ENERGY_TOTAL"));
//                    water = influxdbService.query(this.getEnergyAndWaterByFloor(JsdcDateUtil.getDateFormat(new Date(),
//                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
//                            JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(new Date(),
//                                    Calendar.DAY_OF_YEAR), JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect,
//                            "WATER_L"));
                    break;
            }
            List<Map<String, Object>> electricityList = influxdbService.queryResultProcess(electricity);
            //判断如果返回数据集合里已经有相应的楼层存在了就把数值加起来
            for (WaterElectricityByFloorVo vo : waterElectricityByFloorVos) {
                if (vo.getFloor().equals(deviceCollect.getLogicalFloorId() + "")) {
                    vo.setElectricity(Double.parseDouble(vo.getElectricity()) + Double.parseDouble(this.spreadResultProcess(electricityList)) + "");
//                    isflag=false;
                    break;
                }
            }
//            if (isflag){//没有相同楼宇电表的时候就添加
//                WaterElectricityByFloorVo energyEvaluateByFloorVo = new WaterElectricityByFloorVo();
//                energyEvaluateByFloorVo.setFloor(deviceCollect.getLogicalFloorId() + "");
//                energyEvaluateByFloorVo.setElectricity(this.spreadResultProcess(electricityList));
//                waterElectricityByFloorVos.add(energyEvaluateByFloorVo);
//            }
        }
        return waterElectricityByFloorVos;
    }

    //分项排名
    public JSONObject ElectricityBySubitem() {
        LambdaQueryWrapper<SysDict> sysDictLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysDictLambdaQueryWrapper.eq(SysDict::getDictType, "device_energy_type").eq(SysDict::getIsDel, 0).eq(SysDict::getDictLabel, "用电");
        SysDict sysDict = this.sysDictMapper.selectOne(sysDictLambdaQueryWrapper);
        String dictValue = sysDict.getDictValue();

        List<String> ids = new ArrayList<>();
        LambdaQueryWrapper<ConfigDeviceSubitem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConfigDeviceSubitem::getIsDel, 0).eq(ConfigDeviceSubitem::getEnergy_type, dictValue);
        List<ConfigDeviceSubitem> configDeviceSubitems = this.configDeviceSubitemMapper.selectList(wrapper);

        for (ConfigDeviceSubitem configDeviceSubitem : configDeviceSubitems) {
            Integer sumItemId = configDeviceSubitem.getId();
//            LambdaQueryWrapper<DeviceCollect> deviceCollectLambdaQueryWrapper = new LambdaQueryWrapper<>();
//            deviceCollectLambdaQueryWrapper.eq(DeviceCollect::getSubitem, sumItemId);
//            deviceCollectLambdaQueryWrapper.and(deviceCollectLambdaQueryWrapper1 -> {
//                deviceCollectLambdaQueryWrapper.eq(DeviceCollect::getLogicalFloorId, 0).
//            });
//            .eq(DeviceCollect::getIsTotalDevice, 0);
            List<DeviceCollect> deviceCollectList = this.deviceCollectMapper.getNotTotalDeviceList(sumItemId);
//            List<DeviceCollect> deviceCollectList = this.deviceCollectMapper.selectList(deviceCollectLambdaQueryWrapper);
            List<String> deviceIdList = deviceCollectList.stream().map(new Function<DeviceCollect, String>() {
                @Override
                public String apply(DeviceCollect deviceCollect) {
                    return String.valueOf(deviceCollect.getId());
                }
            }).collect(Collectors.toList());
            String influxdbIn = this.influxdbInToString(deviceIdList);
            if (influxdbIn.isEmpty()) {
                influxdbIn = "-99";
            }
            QueryResult query = this.influxdbService.query(getEnergyBySubitem(JsdcDateUtil.getDateFormat(new Date(), JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(new Date(), Calendar.DAY_OF_MONTH), JsdcDateUtil.FULL_SPLIT_PATTERN), influxdbIn));
            List<Map<String, Object>> list = queryResultProcess1(query);
            if (!list.isEmpty()) {
                Double spread = (Double) list.get(0).get("spread");
                double v = new BigDecimal(spread).setScale(2, RoundingMode.HALF_UP).doubleValue();
                configDeviceSubitem.setVal(v);
            } else {
                configDeviceSubitem.setVal(0.00D);
            }

        }

        Collections.sort(configDeviceSubitems, new Comparator<ConfigDeviceSubitem>() {
            @Override
            public int compare(ConfigDeviceSubitem o1, ConfigDeviceSubitem o2) {
                return o2.getVal().compareTo(o1.getVal());
            }
        });


        ArrayList<String> x = new ArrayList<>();
        ArrayList<String> y = new ArrayList<>();

        Double sum = 0D;
        for (ConfigDeviceSubitem configDeviceSubitem : configDeviceSubitems) {
            sum += configDeviceSubitem.getVal();
        }


        for (ConfigDeviceSubitem configDeviceSubitem : configDeviceSubitems) {
            Double val = configDeviceSubitem.getVal();
            BigDecimal bigDecimal = new BigDecimal(val).setScale(1, RoundingMode.HALF_UP);
            String v = String.valueOf(bigDecimal);
            y.add(v);
            x.add(configDeviceSubitem.getSubitemName());
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("x", x);
        jsonObject.put("y", y);
        jsonObject.put("total", sum);
        return jsonObject;
    }

    //分项排名详情--区域用量占比
    public ArrayList<JSONObject> ElectricityBySubitem1(String floor, Integer timeType) {
        List<String> floorIds = StringUtils.str2List(floor, ",", false, false);
        if (floorIds.isEmpty()) {
            floorIds.add("-99");
        }
        LambdaQueryWrapper<SysBuildArea> areaWrapper = new LambdaQueryWrapper<>();
        areaWrapper.in(SysBuildArea::getFloorId, floorIds).eq(SysBuildArea::getIsDel, 0);
        List<SysBuildArea> areaList = this.sysBuildAreaMapper.selectList(areaWrapper);
        Map<String, String> sysBuildAreas = new HashMap<>();
        List<String> areaIds = new ArrayList<>();
        Map<String, Double> electricityBySubitem = new HashMap<>();
        for (SysBuildArea sysBuildArea : areaList) {
            sysBuildAreas.put(sysBuildArea.getId() + "", sysBuildArea.getAreaName());
            areaIds.add(sysBuildArea.getId() + "");
        }

        for (String areaId : areaIds) {
            Map<String, Object> map = new HashMap<>();
            LambdaQueryWrapper<DeviceCollect> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DeviceCollect::getIsDel, 0).eq(DeviceCollect::getLogicalAreaId, areaId);
            // 为了保证有数据这里的条件先注释掉, 等设备齐了, 再放开就能看见数据了
//                    .eq(DeviceCollect::getIsTotalDevice, 0);
            List<DeviceCollect> list = this.deviceCollectMapper.selectList(wrapper);
            // 用于存储该areaId的各个设备的用电量
            List<Map<String, Object>> electricityList = new ArrayList<>();
//            List<String> areaSpread = new ArrayList<>();
            switch (timeType) {
                case 1:
                    for (DeviceCollect collect : list) {
                        electricityList.addAll(influxdbService.queryResultProcess(influxdbService.query(getEnergyBySubitem1(JsdcDateUtil.getDateFormat(new Date(),
                                JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(new Date(),
                                JsdcDateUtil.FULL_SPLIT_PATTERN), collect.getId() + ""))));
                    }
                    break;
                case 2:
                    for (DeviceCollect collect : list) {
                        electricityList.addAll(
                                influxdbService.queryResultProcess(influxdbService.query(getEnergyBySubitem1(JsdcDateUtil.getDateFormat(new Date(),
                                                JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
                                        JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstWeekDay(new Date()),
                                                JsdcDateUtil.FULL_SPLIT_PATTERN), collect.getId() + ""))));
                    }
                    break;
                case 3:
                    for (DeviceCollect collect : list) {
                        electricityList.addAll(
                                influxdbService.queryResultProcess(influxdbService.query(getEnergyBySubitem1(JsdcDateUtil.getDateFormat(new Date(), JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(new Date(), Calendar.DAY_OF_MONTH), JsdcDateUtil.FULL_SPLIT_PATTERN),
                                        collect.getId() + ""))));
                    }
                    break;
                case 4:
                    for (DeviceCollect collect : list) {
                        electricityList.addAll(
                                influxdbService.queryResultProcess(influxdbService.query(getEnergyBySubitem1(JsdcDateUtil.getDateFormat(new Date(),
                                                JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
                                        JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(new Date(),
                                                Calendar.DAY_OF_YEAR), JsdcDateUtil.FULL_SPLIT_PATTERN),
                                        collect.getId() + ""))));
                        break;
                    }
            }
            double v = spreadResultProcess1(electricityList);
            double v2 = new BigDecimal(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
            electricityBySubitem.put(areaId, v2);
        }

        ArrayList<JSONObject> jsonObjects = new ArrayList<>();
        for (SysBuildArea sysBuildArea : areaList) {
            JSONObject val = new JSONObject();
            val.put("name", sysBuildArea.getAreaName());
            if (electricityBySubitem.get(sysBuildArea.getId().toString()) != null) {
                Double aDouble = electricityBySubitem.get(String.valueOf(sysBuildArea.getId()));

                val.put("value", aDouble);
            } else {
                val.put("value", 0D);
            }
            jsonObjects.add(val);
        }


        return jsonObjects;
    }


    //分项排名详情--分项用电占比
    public ArrayList<JSONObject> ElectricityBySubitem2(String floor, Integer timeType) {
        LambdaQueryWrapper<SysDict> sysDictLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysDictLambdaQueryWrapper.eq(SysDict::getDictType, "device_energy_type").eq(SysDict::getIsDel, 0).eq(SysDict::getDictLabel, "用电");
        SysDict sysDict = this.sysDictMapper.selectOne(sysDictLambdaQueryWrapper);
        String dictValue = sysDict.getDictValue();

        List<String> floorIds = StringUtils.str2List(floor, ",", false, false);
        if (floorIds.isEmpty()) {
            floorIds.add("-99");
            return new ArrayList<>();
        }

        LambdaQueryWrapper<ConfigDeviceSubitem> subitemWrapper = new LambdaQueryWrapper<>();
        subitemWrapper.eq(ConfigDeviceSubitem::getIsDel, 0).eq(ConfigDeviceSubitem::getEnergy_type, dictValue);
        List<ConfigDeviceSubitem> subitems = configDeviceSubitemMapper.selectList(subitemWrapper);
        for (ConfigDeviceSubitem subitem : subitems) {
            LambdaQueryWrapper<DeviceCollect> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(DeviceCollect::getLogicalFloorId, floorIds);
            wrapper.eq(DeviceCollect::getIsDel, 0).eq(DeviceCollect::getSubitem, subitem.getId());
//            .eq(DeviceCollect::getIsTotalDevice, 0);
            List<DeviceCollect> deviceCollects = this.deviceCollectMapper.selectList(wrapper);
            List<Map<String, Object>> electricityList = new ArrayList<>();
            switch (timeType) {
                case 1:
                    for (DeviceCollect deviceCollect : deviceCollects) {
                        electricityList.addAll(influxdbService.queryResultProcess(influxdbService.query(getEnergyBySubitem2(JsdcDateUtil.getDateFormat(new Date(),
                                JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(new Date(),
                                JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""))));
                    }
                    break;
                case 2:
                    for (DeviceCollect deviceCollect : deviceCollects) {
                        electricityList.addAll(
                                influxdbService.queryResultProcess(influxdbService.query(getEnergyBySubitem2(JsdcDateUtil.getDateFormat(new Date(),
                                                JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
                                        JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstWeekDay(new Date()),
                                                JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""))));
                    }
                    break;
                case 3:
                    for (DeviceCollect deviceCollect : deviceCollects) {
                        electricityList.addAll(
                                influxdbService.queryResultProcess(influxdbService.query(getEnergyBySubitem2(JsdcDateUtil.getDateFormat(new Date(),
                                                JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
                                        JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(new Date(),
                                                Calendar.DAY_OF_MONTH), JsdcDateUtil.FULL_SPLIT_PATTERN),
                                        deviceCollect.getId() + ""))));
                    }
                    break;
                case 4:
                    for (DeviceCollect deviceCollect : deviceCollects) {
                        electricityList.addAll(
                                influxdbService.queryResultProcess(influxdbService.query(getEnergyBySubitem2(JsdcDateUtil.getDateFormat(new Date(),
                                                JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
                                        JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(new Date(),
                                                Calendar.DAY_OF_YEAR), JsdcDateUtil.FULL_SPLIT_PATTERN),
                                        deviceCollect.getId() + ""))));
                    }
                    break;
            }

            double v = spreadResultProcess1(electricityList);
            double v2 = new BigDecimal(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
            subitem.setVal(v2);
        }

        ArrayList<JSONObject> jsonObjects = new ArrayList<>();
        for (ConfigDeviceSubitem subitem : subitems) {
            JSONObject jo = new JSONObject();
            jo.put("value", subitem.getVal());
            jo.put("name", subitem.getSubitemName());
            jsonObjects.add(jo);
        }
        return jsonObjects;
    }

    // 分项用电趋势
    public JSONObject ElectricityBySubitem3(String floor, Integer timeType) {
        LambdaQueryWrapper<SysDict> sysDictLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysDictLambdaQueryWrapper.eq(SysDict::getDictType, "device_energy_type").eq(SysDict::getIsDel, 0).eq(SysDict::getDictLabel, "用电");
        SysDict sysDict = this.sysDictMapper.selectOne(sysDictLambdaQueryWrapper);
        String dictValue = sysDict.getDictValue();

        List<String> floorIds = StringUtils.str2List(floor, ",", false, false);
        if (floorIds.isEmpty()) {
            floorIds.add("-99");
        }

        LambdaQueryWrapper<ConfigDeviceSubitem> subitemWrapper = new LambdaQueryWrapper<>();
        subitemWrapper.eq(ConfigDeviceSubitem::getIsDel, 0).eq(ConfigDeviceSubitem::getEnergy_type, dictValue);
        List<ConfigDeviceSubitem> subitems = configDeviceSubitemMapper.selectList(subitemWrapper);
        List<String> electricitys = new ArrayList<>();


        ArrayList<JSONObject> jsonObjectList = new ArrayList<>();
        ArrayList<String> tagList = new ArrayList<>();
        int count = 0;
        switch (timeType) {
            case 1: {
                count = JsdcDateUtil.getHour() + 1;
//                count = 24;
                for (int i = 0; i < 24; i++) {
                    tagList.add(i + "点");
                }
                break;
            }
            case 2: {
                count = JsdcDateUtil.getWeekDay();
//                count = 7;
                List<String> xString = Arrays.asList("星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期天");
                for (int i = 0; i < 7; i++) {
                    String s = xString.get(i);
                    tagList.add(s);
                }
                break;
            }
            case 3: {
                int totalMonthDate = JsdcDateUtil.getTotalMonthDate();
                count = JsdcDateUtil.getMonthDate();
                for (int i = 1; i <= totalMonthDate; i++) {
                    tagList.add(i + "日");
                }
                break;
            }
            case 4: {
                count = JsdcDateUtil.getMonth();
//                count = 12;
                for (int i = 1; i <= 12; i++) {
                    tagList.add(i + "月");
                }
                break;
            }
        }

        if (4 != timeType) {
            String startTime = "";
            String endTime = "";
            switch (timeType) {
                case 1: {
                    Date zeroHourDate = JsdcDateUtil.getZeroHourDate();
                    startTime = JsdcDateUtil.getDateFormat(zeroHourDate, JsdcDateUtil.FULL_TIME_SPLIT_PATTERN);
                    Date finalHourDate = JsdcDateUtil.getFinalHourDate();
                    endTime = JsdcDateUtil.getDateFormat(finalHourDate, JsdcDateUtil.FULL_TIME_SPLIT_PATTERN);
                    break;
                }
                case 2: {
                    Date firstWeekDay = JsdcDateUtil.getFirstWeekDay(new Date());
                    startTime = JsdcDateUtil.getDateFormat(firstWeekDay, JsdcDateUtil.FULL_SPLIT_PATTERN);
                    Date lastDayWeek = JsdcDateUtil.getLastDayWeek();
                    endTime = JsdcDateUtil.getDateFormat(lastDayWeek, JsdcDateUtil.FULL_TIME_SPLIT_PATTERN);
                    break;
                }
                case 3: {
                    Date monthFirstDay = JsdcDateUtil.getMonthFirstDay();
                    startTime = JsdcDateUtil.getDateFormat(monthFirstDay, JsdcDateUtil.FULL_SPLIT_PATTERN);
                    Date lastDayMONTH = JsdcDateUtil.getLastDayMONTH();
                    endTime = JsdcDateUtil.getDateFormat(lastDayMONTH, JsdcDateUtil.FULL_TIME_SPLIT_PATTERN);
                    break;
                }
            }

            for (ConfigDeviceSubitem configDeviceSubitem : subitems) {
                Integer sumItemId = configDeviceSubitem.getId();
                LambdaQueryWrapper<DeviceCollect> deviceCollectLambdaQueryWrapper = new LambdaQueryWrapper<>();
                deviceCollectLambdaQueryWrapper.eq(DeviceCollect::getSubitem, sumItemId);
                deviceCollectLambdaQueryWrapper.in(DeviceCollect::getLogicalFloorId, floorIds);
                List<DeviceCollect> deviceCollectList = this.deviceCollectMapper.selectList(deviceCollectLambdaQueryWrapper);
                List<String> deviceIdList = deviceCollectList.stream().map(new Function<DeviceCollect, String>() {
                    @Override
                    public String apply(DeviceCollect deviceCollect) {
                        return String.valueOf(deviceCollect.getId());
                    }
                }).collect(Collectors.toList());
                String influxdbIn = this.influxdbInToString(deviceIdList);
                if (influxdbIn.isEmpty()) {
                    influxdbIn = "-99";
                }
                List<Map<String, Object>> lists = queryResultProcess1(influxdbService.query(getEnergyBySubitem3(startTime, endTime, influxdbIn, timeType + "")));
                if (lists.isEmpty()) {
                    for (int i = 0; i < count; i++) {
                        configDeviceSubitem.getSpreadList().add(0.00D);
                    }
                } else {
                    for (Map<String, Object> map : lists) {
                        Double spread = (Double) map.get("spread");
                        if (spread == null) {
                            spread = 0.00D;
                        }
                        double v = new BigDecimal(spread).setScale(2, RoundingMode.HALF_UP).doubleValue();
                        configDeviceSubitem.getSpreadList().add(v);
                    }
                }
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("name", configDeviceSubitem.getSubitemName());
                jsonObject1.put("data", configDeviceSubitem.getSpreadList());
                jsonObjectList.add(jsonObject1);
            }

        } else {
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int dateYear = calendar.get(Calendar.YEAR);
            Map<String, List<String>> timeMap = DateUtils.getMonthsByYear(dateYear + "");
            for (ConfigDeviceSubitem subitem : subitems) {
                Integer sumItemId = subitem.getId();
                LambdaQueryWrapper<DeviceCollect> deviceCollectLambdaQueryWrapper = new LambdaQueryWrapper<>();
                deviceCollectLambdaQueryWrapper.eq(DeviceCollect::getSubitem, sumItemId);
                deviceCollectLambdaQueryWrapper.in(DeviceCollect::getLogicalFloorId, floorIds);
                List<DeviceCollect> deviceCollectList = this.deviceCollectMapper.selectList(deviceCollectLambdaQueryWrapper);
                List<String> deviceIdList = deviceCollectList.stream().map(new Function<DeviceCollect, String>() {
                    @Override
                    public String apply(DeviceCollect deviceCollect) {
                        return String.valueOf(deviceCollect.getId());
                    }
                }).collect(Collectors.toList());
                String influxdbIn = this.influxdbInToString(deviceIdList);
                if (influxdbIn.isEmpty()) {
                    influxdbIn = "-99";
                }
                List<Double> longs = new ArrayList<>();
                for (int i = 0; i < 12; i++) {
                    String spread = spreadResultProcess(influxdbService.queryResultProcess(influxdbService.query(getEnergyBySubitemYear3(timeMap.get("startTimes").get(i), timeMap.get("endTimes").get(i),
                            influxdbIn))));
                    longs.add(Double.parseDouble(spread));
                }
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("name", subitem.getSubitemName());
                jsonObject1.put("data", longs);
                jsonObjectList.add(jsonObject1);
            }
        }


        for (JSONObject jsonObject : jsonObjectList) {
            ArrayList<Double> data = (ArrayList<Double>) jsonObject.get("data");
            for (int i = 0; i < data.size(); i++) {
                if (i < count) {
                    data.subList(count, data.size()).clear();
                }
            }

        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("xLabel", tagList);
        jsonObject.put("mainData", jsonObjectList);

        return jsonObject;
    }

    /**
     * 列表查询
     */
    public List<DataSheet> getList(DataSheetVo vo) {
        QueryWrapper<DataSheet> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotNull(vo)) {
            if (StringUtils.isNotEmpty(vo.getGateWayId())) {
                queryWrapper.eq("gateWayId", vo.getGateWayId());
            }
            if (StringUtils.isNotNull(vo.getType())) {
                queryWrapper.eq("type", vo.getType());
            }
            if (StringUtils.isNotEmpty(vo.getDeviceId())) {
                queryWrapper.eq("deviceId", vo.getDeviceId());
            }
            if (StringUtils.isNotEmpty(vo.getChannelId())) {
                queryWrapper.eq("channelId", vo.getChannelId());
            }
            if (StringUtils.isNotEmpty(vo.getPoint())) {
                queryWrapper.eq("point", vo.getPoint());
            }
            if (StringUtils.isNotEmpty(vo.getStartTime())) {
                queryWrapper.ge("TO_CHAR(time, 'yyyy-MM-dd')", vo.getStartTime());
            }
            if (StringUtils.isNotEmpty(vo.getEndTime())) {
                queryWrapper.le("TO_CHAR(time, 'yyyy-MM-dd')", vo.getEndTime());
            }
            if (StringUtils.isNotEmpty(vo.getCodes())) {
                queryWrapper.in("channelId", vo.getCodes());
            }
        }
        queryWrapper.orderByDesc("time");
        return dataSheetMapper.selectList(queryWrapper);
    }

    /**
     * 补充网关发送数据的字段
     *
     * @param dsList
     */
    public List<DataSheet> addSupplyField(List<DataSheet> dsList) {
        for (DataSheet ds : dsList) {
            try {
                ds = addSupplyField(ds);
                System.out.println();
            } catch (Exception e) {
                System.out.println("补充数据异常" + e);
            }
        }
        return dsList;
    }

    public DataSheet addSupplyField(DataSheet ds) {
        //根据sn码取网关信息
        DeviceGateway gateway = null;
        DeviceCollectVo collect = null;
        WarningConfig warningConfig = null;
        Object gatewayObj = RedisUtils.getBeanValue(MemoryCacheService.gatewayKeyPrefixsn + ds.getGateWayId());
        if (null != gatewayObj) {
            gateway = (DeviceGateway) gatewayObj;
        }
        String collectKey = String.format("%s_%s", ds.getGateWayId(), ds.getDeviceId());

        Object colletObj = RedisUtils.getBeanValue(MemoryCacheService.deviceCollecKeyPrefixSNSN + collectKey);
        if (null != colletObj) {
            collect = (DeviceCollectVo) colletObj;
        }

        Object warningConfigObj = RedisUtils.getBeanValue(MemoryCacheService.warningConfigKeyPrefixSNSN + collectKey);
        Map<String, Object> map = hasWarn(collectKey, collect, ds);
        ds.setIsWarn("F");
        if ((boolean) map.get("flag")) {
            ds.setIsWarn("T");
            WarningConfig wc = (WarningConfig) map.get("data");
            ds.setWarnId(wc.getId());

            AlertSheet as = new AlertSheet();
            BeanUtils.copyProperties(ds, as);
            as.setId(null);
            as.setWarnConfigId(wc.getId());
            alertSheetMapper.insert(as);
        }


        if (null != gateway && null != collect) {
            ds.setBuildId(collect.getBuildId());
            ds.setFloorId(collect.getFloorId());
            ds.setPlace(collect.getPlace());
            ds.setDeviceCollectId(collect.getId());
            ds.setDeviceGatewayId(gateway.getId());
        }
        return ds;
    }

    private Map<String, Object> hasWarn(String collectKey, DeviceCollectVo collect, DataSheet ds) {
        Map<String, Object> map = new HashMap<>();
        map.put("flag", false);
        Object warningConfigObj = RedisUtils.getBeanValue(MemoryCacheService.warningConfigKeyPrefixSNSN + collectKey);
        if (null == warningConfigObj) {
            map.put("flag", false);
            return map;
        }
        WarningConfig warningConfig = (WarningConfig) warningConfigObj;
        if (warningConfig.getDeviceId() != collect.getId().intValue()) {
            map.put("flag", false);
            return map;
        }
        Object detailsObj =
                RedisUtils.getBeanValue(MemoryCacheService.warningSignDetailsKeyPrefixConfigId + String.valueOf(warningConfig.getId()));
        if (null != detailsObj) {

            map.put("flag", false);
            return map;
        }
        List<WarningSignDetails> details = (List<WarningSignDetails>) detailsObj;
        for (WarningSignDetails wsd : details) {
            if ("2".equals(warningConfig.getConfigType()) && 0 == wsd.getIsDel().intValue()) {
                double dataVal = NumberUtils.toDouble(ds.getVal(), 0);
                if (dataVal > wsd.getValueBegin() && dataVal < wsd.getValueEnd()) {
                    map.put("flag", true);
                    map.put("data", warningConfig);
                    return map;
                }
            }

        }

        return map;
    }


    /**
     * 能源看板总负荷趋势
     *
     * @param startTime
     * @param endTime
     * @param channelId
     * @param deviceCollectId
     * @param groupStr        分组（1d,1s等）
     * @param function        函数:(mean(val),sum(val) 等)
     * @return
     */
    public List<DataSheet> realTimeload(String startTime, String endTime, String channelId, Integer deviceCollectId, String groupStr, String function) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT");
        if (null != function) {
            sql.append(" " + function + " as totalPower");
        } else {
            sql.append(" val" + " as totalPower");
        }
        sql.append(" from datasheet where");
        sql.append(" time>='" + startTime + "'");
        sql.append(" AND time<'" + endTime + "'");
        sql.append(" AND deviceCollectId='" + deviceCollectId + "'");
        sql.append(" AND channelId='" + channelId + "'");
        if (null != function) {
            sql.append(" GROUP BY time(" + groupStr + ")");
        }
        sql.append(" ORDER BY time asc");

        return null;
    }


    /**
     * 负荷趋势环比，昨日，今日环比统计
     * 全部数据找总电表
     * 分层数据找分电表，没有分电表
     *
     * @return
     */
    public Object loadCycleRatio(String startTime, String endTime, String channelId, Integer deviceCollectId) {
        ///////按天统计
        // 1.根据时间、采集设备id，信号id、统计昨天负荷趋势
        List<DataSheet> yesterdays = realTimeload(null, null, null, null, null, null);
        // 2.根据时间、采集设备id，信号id，统计今日负荷趋势
        List<DataSheet> todya = realTimeload(null, null, null, null, null, null);

        ////按周统计
        List<DataSheet> week1 = realTimeload(null, null, null, null, "1d", "mean(val)");
        List<DataSheet> week2 = realTimeload(null, null, null, null, "1d", "mean(val)");


        ////按月统计
        List<DataSheet> month1 = realTimeload(null, null, null, null, "1d", "mean(val)");
        List<DataSheet> month2 = realTimeload(null, null, null, null, "1d", "mean(val)");


        return null;
    }


    /**
     * 按天统计用水量趋势
     * 天:time(1h)
     * 周：time(1d)
     * 月：time(1d)
     * 按年，因不具备月份函数 所以查12次数据较为准确
     *
     * @return
     */
    public Object waterTrends(String startTime, String endTime, String point, String groupStr) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT LAST(val)-FIRST(val) as totalPower from datasheet ");
        sql.append(" WHERE time>'" + startTime + "'");
        sql.append(" AND time<'" + endTime + "' ");
        sql.append(" AND channelId='" + point + "'");
        if (null != groupStr) {
            sql.append(" GROUP BY " + groupStr);
        }
        sql.append(" order by time");

        return null;
    }

    /**
     * 按日，周，月，年统计每层用水量
     * 每层的分水表，
     * 逻辑可分次查询，或者拼接所有层的code后分组
     *
     * @param startTime
     * @param endTime
     * @param point
     * @return
     */
    public Object waterTrendsFloor(String startTime, String endTime, String point) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT LAST(val)-FIRST(val) from datasheet ");
        sql.append(" WHERE time>'" + startTime + "'");
        sql.append(" AND time<'" + endTime + "'");
        sql.append(" AND channelId='" + point + "'");

        return null;
    }


    public List<ConfigDeviceSignalMapVo> getInformationReporting(Integer type, Integer id)  {
        List<ConfigDeviceSignalMapVo> signalMaps = configDeviceSignalMapMapper.getEntityByTId(type);
        List<ConfigDeviceSignalMapVo> signalMaps1 =new ArrayList<>();
        for (ConfigDeviceSignalMapVo signalMap : signalMaps) {
            ConfigDeviceSignalMapVo configDeviceSignalMapVo=new ConfigDeviceSignalMapVo();
            List<Map<String, Object>> list = influxdbService.queryResultProcess(influxdbService.query(informationReporting(signalMap.getSignalTypeCode(), id)));
            String last = "0.00";
            String time = "";
            if (list != null && list.size() != 0) {
                Map<String, Object> map = list.get(0);
                double num = (Double) map.get("last");
                if (num<0){
                    num=0.0;
                }
                time = map.get("time") + "";
                if (StrUtil.isNotEmpty(time)) {
                    time = DateUtil.formatDateTime(DateUtil.offsetHour(DateUtil.parse(time), -8));
                }
                BigDecimal numTemp = new BigDecimal(num).setScale(2, ROUND_HALF_UP);
                if (numTemp.compareTo(BigDecimal.ZERO) <= 0) {
                    last = "0.00";
                }else {
                    last = new BigDecimal(num).setScale(2, ROUND_HALF_UP).toString();
                }
            }
            signalMap.setTime(time);
            signalMap.setSignValue(last + "");
            configDeviceSignalMapVo.setId(signalMap.getId());
            configDeviceSignalMapVo.setSignalTypeName(signalMap.getSignalTypeName());
            configDeviceSignalMapVo.setSignalTypeCode(signalMap.getSignalTypeCode());
            configDeviceSignalMapVo.setSignalTypeDesc(signalMap.getSignalTypeDesc());
            configDeviceSignalMapVo.setSignValue(last);
            configDeviceSignalMapVo.setUnit(signalMap.getUnit());
            configDeviceSignalMapVo.setTime(signalMap.getTime());
            configDeviceSignalMapVo.setSignalType(signalMap.getSignalType());
            signalMaps1.add(configDeviceSignalMapVo);
        }

        for (ConfigDeviceSignalMapVo signalMap : signalMaps) {
            if ("3".equals(signalMap.getSignalType())) {
                if ("0.00".equals(signalMap.getSignValue())) {
                    signalMap.setSignValue("开启");
                } else if ("1.00".equals(signalMap.getSignValue())) {
                    signalMap.setSignValue("关闭");
                }

            }
        }
        return signalMaps1;
    }


    public List<ConfigDeviceSignalMapVo> getEntityByTIdGroup(Integer type, Integer id) throws ParseException {
        List<ConfigDeviceSignalMapVo> signalMaps = configDeviceSignalMapMapper.getEntityByTIdGroup(type);

        return signalMaps;
    }


    public List<Map<String, Object>> getReportingList(Integer id, String type, String start, String end,
                                                      Integer index, Integer size) throws ParseException {
        List<Map<String, Object>> lists =
                influxdbService.queryResultProcess
                        (influxdbService.query(ReportingList(id, type, start, end, (index - 1) * size, size)));
        DeviceCollect collect = deviceCollectMapper.selectById(id);
        for (Map<String, Object> list : lists) {
            if (!StringUtils.isNull(collect)){
                list.put("code", collect.getDeviceCode());
            }
        }
        return lists;
    }

    public Integer getReportingCount(Integer id, String type, String start, String end,
                                     Integer index, Integer size) throws ParseException {
        Integer count =
                influxdbService.queryResultProcess
                        (influxdbService.query(ReportingList(id, type, start, end, null, null))).size();
        return count;
    }


    /**
     * 今日分项用电
     * 1、各分项用电量
     * 2、各分项用电量占比
     *
     * @return
     */
    public List<JSONObject> subitemElectricity() {
        List<JSONObject> result = new ArrayList<>();
        Map<String, Double> map = new HashMap<>();
        double totalSum = 0d;//总电量
        //获取所有分项用电
        List<ConfigDeviceSubitem> list = configDeviceSubitemMapper.selectList(new LambdaQueryWrapper<ConfigDeviceSubitem>()
                .eq(ConfigDeviceSubitem::getIsDel, 0).eq(ConfigDeviceSubitem::getEnergy_type, "1"));
        for (ConfigDeviceSubitem a : list) {
            DataSheetVo bean = new DataSheetVo();
            bean.setEnergyType(1);
            bean.setSubitem(a.getId());
            bean.setIsTotalDevice(1);//查询是总设备的数据
            List<DeviceCollect> collects = deviceCollectMapper.getDeviceByArea(bean);//获取设备列表
            if (collects.isEmpty()) {// 无采集设备
//                map.put(a.getSubitemName(), 0d);
//                continue;
                bean.setIsTotalDevice(0);//查询不是总设备的数据
                collects= deviceCollectMapper.getDeviceByArea(bean);
                if (collects.isEmpty()){
                    map.put(a.getSubitemName(), 0d);
                continue;
                }
            }
            List<String> deviceIds = collects.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
            String tempTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            //查询今日的分项用电;
            QueryResult query = influxdbService.query("select spread(val) as totalPower from datasheet where deviceCollectId=~/" + influxdbInToString(deviceIds) + "/ " +
                    " and channelId ='" + G.CHANNELID_ELECTRICITY + "'  and  time >= '" + tempTime + " 00:00:00' " +
                    "AND time <= '" + tempTime + " 23:59:59' GROUP BY deviceCollectId ");
            //解析列表类查询
            List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
            double sum = 0d;//每一项的总电量
            for (Map<String, Object> item : maps) {
                sum = sum + (Double) item.get("totalPower");
            }
            totalSum = totalSum + sum;
            map.put(a.getSubitemName(), sum);
        }
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            BigDecimal value = new BigDecimal(entry.getValue());
            if (0d != totalSum) {//用电量除于总用电量，乘以100
                result.add(JSONObject.parseObject("{\"name\":\"" + entry.getKey() + "\",\"value\":\"" + String.format("%.2f", value) + "\",\"ratio\":\""
                        + String.format("%.2f", value.divide(new BigDecimal(totalSum), ROUND_HALF_UP).multiply(new BigDecimal(100))) + "\"}"));
            } else {
                result.add(JSONObject.parseObject("{\"name\":\"" + entry.getKey() + "\",\"value\":\"" + String.format("%.2f", value) + "\",\"ratio\":\"" +
                        String.format("%.2f", 0.00) + "\"}"));
            }
        }
        // 结果排序
        Collections.sort(result, Comparator.comparing(p -> p.getDouble("value")));
        Collections.reverse(result);
        result = result.subList(0, 7);
        return result;
    }

    public List<JSONObject> floorElectricity(String startTime, String endTime) {
        List<JSONObject> result = new ArrayList<>();
        Map<String, Double> map = new HashMap<>();
        double totalSum = 0d;//总电量

        ConfigDeviceType configDeviceType = new ConfigDeviceType();
        configDeviceType.setDeviceTypeCode("E_METER");
        List<ConfigDeviceType> deviceTypeList = configDeviceTypeService.getList(configDeviceType);

        // 获取所有楼层用电
        List<SysBuildFloor> list = sysBuildFloorService.list(new LambdaQueryWrapper<SysBuildFloor>()
                .eq(SysBuildFloor::getIsDel, 0));
        for (SysBuildFloor a : list) {
            DataSheetVo bean = new DataSheetVo();
            bean.setDeviceType(deviceTypeList.get(0).getId());
            bean.setAreaId(0);
            bean.setEnergyType(1);
            bean.setFloorId(a.getId());
            bean.setIsTotalDevice(1); // 查询不是总设备的数据
            List<DeviceCollect> collects = deviceCollectMapper.getDeviceByArea(bean); // 获取设备列表
            if (collects.isEmpty()) { // 无采集设备
                map.put(a.getFloorName(), 0d);
                continue;
            }
            List<String> deviceIds = collects.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
            String tempTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            // 查询今日的楼层用电
            QueryResult query = influxdbService.query("select LAST(val)-FIRST(val) as totalPower from datasheet " +
                    "where deviceCollectId='" + deviceIds.get(0) + "' " +
                    " and channelId ='" + G.CHANNELID_ELECTRICITY + "' and  time >= '" + startTime + " 00:00:00' " +
                    "AND time <= '" + endTime + " 23:59:59'");
            //解析列表类查询
            List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
            double sum = 0d;//每一项的总电量
            for (Map<String, Object> item : maps) {
                sum = sum + (Double) item.get("totalPower");
            }
            totalSum = totalSum + sum;
            map.put(a.getFloorName(), sum);
        }
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            BigDecimal value = new BigDecimal(entry.getValue());
            if (0d != totalSum) {//用电量除于总用电量，乘以100
                result.add(JSONObject.parseObject("{\"name\":\"" + entry.getKey() + "\",\"value\":\"" + String.format("%.2f", value) + "\",\"ratio\":\""
                        + String.format("%.2f", value.divide(new BigDecimal(totalSum), ROUND_HALF_UP).multiply(new BigDecimal(100))) + "\"}"));
            } else {
                result.add(JSONObject.parseObject("{\"name\":\"" + entry.getKey() + "\",\"value\":\"" + String.format("%.2f", value) + "\",\"ratio\":\"" +
                        String.format("%.2f", 0.00) + "\"}"));
            }
        }
        // 结果排序
        Collections.sort(result, Comparator.comparing(p -> p.getDouble("value")));
        Collections.reverse(result);
        result = result.subList(0, 7);
        return result;
    }

    public List<JSONObject> subitemElectricity(String startTime,String endTime) {
        List<JSONObject> result = new ArrayList<>();
        Map<String, Double> map = new HashMap<>();
        double totalSum = 0d;//总电量
        //获取所有分项用电
        List<ConfigDeviceSubitem> list = configDeviceSubitemMapper.selectList(new LambdaQueryWrapper<ConfigDeviceSubitem>()
                .eq(ConfigDeviceSubitem::getIsDel, 0).eq(ConfigDeviceSubitem::getEnergy_type, "1"));
        for (ConfigDeviceSubitem a : list) {
            DataSheetVo bean = new DataSheetVo();
            bean.setEnergyType(1);
            bean.setSubitem(a.getId());
            bean.setIsTotalDevice(1);//查询不是总设备的数据
            List<DeviceCollect> collects = deviceCollectMapper.getDeviceByArea(bean);//获取设备列表
            if (collects.isEmpty()) {// 无采集设备
                //                map.put(a.getSubitemName(), 0d);
//                continue;
                bean.setIsTotalDevice(0);//查询不是总设备的数据
                collects= deviceCollectMapper.getDeviceByArea(bean);
                if (collects.isEmpty()){
                    map.put(a.getSubitemName(), 0d);
                    continue;
                }
            }
            List<String> deviceIds = collects.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
            //查询今日的分项用电;
            QueryResult query = influxdbService.query("select MAX(val)-MIN(val) as totalPower from datasheet where deviceCollectId=~/" + influxdbInToString(deviceIds) + "/ " +
                    " and channelId ='" + G.CHANNELID_ELECTRICITY + "' and  time >= '" + startTime + "' " +
                    "AND time <= '" + endTime + "' GROUP BY deviceCollectId ");
            //解析列表类查询
            List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
            double sum = 0d;//每一项的总电量
            for (Map<String, Object> item : maps) {
                sum = sum + (Double) item.get("totalPower");
            }
            totalSum = totalSum + sum;
            map.put(a.getSubitemName(), sum);
        }
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            BigDecimal value = new BigDecimal(entry.getValue());
            if (0d != totalSum) {//用电量除于总用电量，乘以100
                result.add(JSONObject.parseObject("{\"name\":\"" + entry.getKey() + "\",\"value\":\"" + String.format("%.2f", value) + "\",\"ratio\":\""
                        + String.format("%.2f", value.divide(new BigDecimal(totalSum), ROUND_HALF_UP).multiply(new BigDecimal(100))) + "\"}"));
            } else {
                result.add(JSONObject.parseObject("{\"name\":\"" + entry.getKey() + "\",\"value\":\"" + String.format("%.2f", value) + "\",\"ratio\":\"" +
                        String.format("%.2f", 0.00) + "\"}"));
            }
        }
        // 结果排序
        Collections.sort(result, Comparator.comparing(p -> p.getDouble("value")));
        Collections.reverse(result);
        result = result.subList(0, 7);
        return result;
    }


    //组装用电量评价sql
    public String getEnergyEvaluate(String time1, String time2, String id) {

        String sql = "select  spread(val)  from datasheet WHERE channelId='" + G.CHANNELID_ELECTRICITY + "'  AND time" +
                " <'" + time1 + "' " +
                "AND " +
                "time >'" + time2 + "'  AND deviceCollectId ='" + id + "'";
        return sql;
    }

    public String spreadResultProcess(List<Map<String, Object>> list) {
        String spread = "0.00";
        if (list != null && list.size() != 0) {
            Map<String, Object> map = list.get(0);
            double num = (Double) map.get("spread");
            spread = new BigDecimal(num).setScale(2, ROUND_HALF_UP).toString();
        }

        return spread;
    }

    public double spreadResultProcess1(List<Map<String, Object>> list) {
        double sum = 0D;
        if (list != null && list.size() != 0) {
            for (Map<String, Object> map : list) {
                double spread = (Double) map.get("spread");
//                spread = new BigDecimal(num).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                sum += spread;
            }
        }
        return sum;
    }

    public String lastResultProcess(List<Map<String, Object>> list) {
        String s = "0.00";
        if (list != null && list.size() != 0) {
            for (Map<String, Object> map : list) {
                double last = (Double) map.get("last");
//                spread = new BigDecimal(num).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                s = new BigDecimal(last).setScale(2, ROUND_HALF_UP).toString();
            }
        }
        return s;
    }


    public String typeResultProcess(List<Map<String, Object>> list) {
        String s="0";
        if (list != null && list.size() != 0) {
            for (Map<String, Object> map : list) {
                s = String.valueOf(map.get("last"));
            }
        }
        return s;
    }

    //组装水电对比sql
    public String getEnergyAndWaterByFloor(String time1, String time2, DeviceCollect collect, String type) {
        String sql = "select  spread(val)  from datasheet WHERE channelId='" + type + "'  AND time <'" + time1 + "' " +
                "AND time >'" + time2 + "'  AND deviceCollectId ='" + collect.getId() + "'";
        return sql;
    }

    //组装多选sql条件
    public String influxdbInToString(List<String> ids) {

        ids = ids.stream().map(x -> "^" + x + "$").collect(Collectors.toList());


        String list = StringUtils.join(ids, "|");
        return list;
    }

    //组装分项排名sql
    public String getEnergyBySubitem(String endTime, String startTime, String ids) {


        String sql = "select  spread(val)  from datasheet WHERE channelId='" + G.CHANNELID_ELECTRICITY + "'  AND deviceCollectId =~/" + ids + "/ " + " and time <= '" + endTime + "' and time >= '" + startTime + "' ";

        return sql;
//        String sql = "select  spread(val)  from datasheet WHERE channelId='" + G.CHANNELID_ELECTRICITY + "'  AND subitem =~/" + ids + "/ " + " and time <= '" + endTime + "' and time >= '" + startTime + "' " +
//                "GROUP BY subitem";
//        return sql;
    }

    //组装分项排名sql1
    public String getEnergyBySubitem1(String time1, String time2, String id) {

        String sql =
                "seLect  spread(val)  from datasheet WHERE channelId='" + G.CHANNELID_ELECTRICITY + "'  AND deviceCollectId ='" + id + "' and " +
                        "time <'" + time1 + "' and time >'" + time2 + "'" + " GROUP BY deviceCollectId";
//                "seLect  spread(val)  from datasheet WHERE channelId='" + G.CHANNELID_ELECTRICITY + "' AND deviceCollectId ='" + id + "' and " +
//                        "time < '2023-08-17' and time > '2023-05-01'" +
//                        " GROUP BY deviceCollectId";
        return sql;
    }

    //组装分项排名sql2
    public String getEnergyBySubitem2(String time1, String time2, String id) {

        String sql =
                "seLect  spread(val)  from datasheet WHERE channelId='" + G.CHANNELID_ELECTRICITY + "'  AND deviceCollectId ='" + id + "' and " +
                        "time <'" + time1 + "' and time >'" + time2 + "'" +
                        " GROUP BY deviceCollectId";
//        "seLect  spread(val)  from datasheet WHERE channelId='" + G.CHANNELID_ELECTRICITY + "'  AND deviceCollectId ='" + id + "' and " +
//                "time <'2023-08-17' and time >'2023-05-01'" +
//                " GROUP BY deviceCollectId";
        return sql;
    }

    //组装分项排名sql3
    public String getEnergyBySubitem3(String startTime, String endTime, String id, String timeType) {
//        String sql =
//                "SELECT  SPREAD(val)  FROM datasheet WHERE channelId='" + G.CHANNELID_ELECTRICITY + "' AND subitem=~/" + id + "/ " +
//                        " AND floorId=~/" + floor + "/ " +
//                        "AND time" +
//                        " >= '" + startTime +
//                        "' AND time <'" + endTime + "'";
//        if (timeType.equals("1")) {
//            sql += "  GROUP BY time(1h),subitem";
//        } else if (timeType.equals("2")) {
//            sql += "  GROUP BY time(1d),subitem";
//        } else if (timeType.equals("3")) {
//            sql += "  GROUP BY time(1d),subitem";
//        }
//        return sql;

        String sql = "SELECT  SPREAD(val)  FROM datasheet WHERE channelId='" + G.CHANNELID_ELECTRICITY + "' AND deviceCollectId=~/" + id + "/ " +
                "AND time" +
                " >= '" + startTime +
                "' AND time <'" + endTime + "'";
        if (timeType.equals("1")) {
            sql += "  GROUP BY time(1h)";
        } else if (timeType.equals("2")) {
            sql += "  GROUP BY time(1d)";
        } else if (timeType.equals("3")) {
            sql += "  GROUP BY time(1d)";
        }
        return sql;
    }

    //组装分项排名sql3
    public String getEnergyBySubitemYear3(String time1, String time2, String id) {

        String sql =
                "SELECT  SPREAD(val)  FROM datasheet WHERE channelId='" + G.CHANNELID_ELECTRICITY + "' AND deviceCollectId=~/" + id + "/ " +
                        " AND " +
                        "time>'" + time1 + "' AND time<'" + time2 +
                        "'  ";
        return sql;
    }

    //设备监控信号上报
    public String informationReporting(String type, Integer id) {
        String sql = "SELECT  LAST(val)  FROM datasheet WHERE channelId='" + type + "' AND deviceCollectId='" + id + "'";
        return sql;
    }

    //设备监控信号上报详情
    public String ReportingList(Integer id, String type, String start, String end,
                                Integer index, Integer size) {
        String sql = "SELECT  *  FROM datasheet WHERE  deviceCollectId='" + id + "' ";
        if (!StringUtils.isEmpty(type)) {
            sql += " and channelId='" + type + "'";
        }
        if (!StringUtils.isEmpty(start.trim()) && !StringUtils.isEmpty(end.trim())) {
            sql += " AND time>='" + start + "' AND time<='" + end + "'";
        }
        sql += " ORDER BY time desc ";
        if (StringUtils.isNotNull(index) && StringUtils.isNotNull(size)) {
            sql += " LIMIT " + size + " OFFSET " + index;
        }
        return sql;
    }

    //设备监控信号上报详情
    public String ReportingListAll(Integer id, String type, String start, String end) {
        String sql = "SELECT  *  FROM datasheet WHERE deviceCollectId='" + id + "' ";
        if (!StringUtils.isEmpty(type)) {
            sql += " and channelId='" + type + "'";
        }
        if (!StringUtils.isEmpty(start) && !StringUtils.isEmpty(end)) {
            sql += " AND time>'" + start + "' AND time<'" + end + "'";
        }
        sql += " ORDER BY time asc ";

        return sql;
    }

    //设备监控检测趋势列表
    public String ReportingList(Integer id, String start, String end) {
        String sql = "SELECT  *  FROM datasheet WHERE  deviceCollectId='" + id + "' ";
        if (StringUtils.isNotNull(start) && StringUtils.isNotNull(end)) {
            sql += "AND time>'" + start + "' AND time<'" + end + "'";
        }
        sql += " ORDER BY time asc ";
        return sql;
    }


    /**
     * @desc 查询结果处理
     */
    public List<Map<String, Object>> queryResultProcess1(QueryResult queryResult) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        List<QueryResult.Result> resultList = queryResult.getResults();
        //把查询出的结果集转换成对应的实体对象，聚合成list
        for (QueryResult.Result query : resultList) {
            List<QueryResult.Series> seriesList = query.getSeries();
            if (seriesList != null && seriesList.size() != 0) {
                for (QueryResult.Series series : seriesList) {
                    List<String> columns = series.getColumns();
                    String[] keys = columns.toArray(new String[columns.size()]);
                    List<List<Object>> values = series.getValues();
                    if (values != null && values.size() != 0) {
                        for (List<Object> value : values) {
                            Map<String, Object> map = new HashMap(keys.length);
                            for (int i = 0; i < keys.length; i++) {
                                map.put(keys[i], value.get(i));
                            }
                            if (series.getTags() != null) {
                                map.putAll(series.getTags());
                            }
                            mapList.add(map);
                        }
                    }
                }
            }
        }
        return mapList;
    }


    /**
     * influx sql
     * <p>
     * 天：time(1h)
     * 月：time(1d)
     * 年：12次
     *
     * @return
     */
    public String getInfluxSql(String val, String startTime, String endTime, String groupStr, String channelId, List<String> ids) {
        String idstr = influxdbInToString(ids);
        StringBuilder sql = new StringBuilder();
        sql.append("select " + val + " as totalPower from datasheet ");
        sql.append("WHERE deviceCollectId=~/" + idstr + "/ ");
        if(StringUtils.isNotEmpty(startTime)){
            sql.append("AND time>'" + startTime + "' ");
        }
        if(StringUtils.isNotEmpty(endTime)){
            sql.append("AND time<'" + endTime + "' ");
        }
        sql.append("AND channelId='" + channelId + "' ");


        if (null != groupStr) {
            sql.append(" GROUP BY " + groupStr + ",deviceCollectId ");
        } else {
            sql.append(" GROUP BY deviceCollectId ");
        }

        if (val.indexOf("DIFFERENCE") >= 0) {
            sql.append("fill(previous) ");
        } else {
            sql.append("fill(0) ");
        }

        sql.append("ORDER BY time ");


        return sql.toString();
    }

    /**
     * influx统计执行方法
     * oracle
     * 条件：是否总设备，楼宇id、楼层id，区域id，设备为用电/水/气设备
     * influx
     * 条件：采集设备id（通过oracle条件获取），信号，字段，分组
     * result: 多个设备结果合并
     * <time1,data1>
     * <time2,data2>
     *
     * @return
     */
    public Map<String, Double> getInfluxExecute(DataSheetVo bean) {
        // 随机取个楼宇的id
        SysBuild sysBuild = sysBuildMapper.selectOne(Wrappers.<SysBuild>lambdaQuery().
                eq(SysBuild::getIsDel, 0).last("AND ROWNUM=1"));

        // 获取采集设备
        List<DeviceCollect> deviceCollects = deviceCollectMapper.getDeviceByArea(bean);

        List<String> ids = new ArrayList<>();
        // 总设备上升，其余设备下沉；如果无总设备，返回所有设备
        if (null == bean.getFloorId() && null == bean.getAreaId()) {// 楼宇总设备
            ids = deviceCollects.stream().filter(x -> (x.getIsTotalDevice() == 1) &&
                    (0 == x.getLogicalFloorId()) && (0 == x.getLogicalAreaId())).map(y -> String.valueOf(y.getId())).collect(Collectors.toList());

            if (ids.isEmpty()) {
                ids = deviceCollects.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
            }
        } else if (null != bean.getFloorId() && null == bean.getAreaId()) { // 楼层总设备
            ids = deviceCollects.stream().filter(x -> (x.getIsTotalDevice() == 1) &&
                    (0 != x.getLogicalFloorId()) && (0 == x.getLogicalAreaId())).map(y -> String.valueOf(y.getId())).collect(Collectors.toList());

            if (ids.isEmpty()) {
                ids = deviceCollects.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
            }
        } else { // 区域总设备
            ids = deviceCollects.stream().filter(x -> (x.getIsTotalDevice() == 1) &&
                    (0 != x.getLogicalAreaId())).map(y -> String.valueOf(y.getId())).collect(Collectors.toList());

            if (ids.isEmpty()) {
                ids = deviceCollects.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
            }
        }
        Map<String, Double> result = new LinkedHashMap<>();

        if (ids.isEmpty()) {
            return result;
        }

        String sql = getInfluxSql(bean.getInfluxVal(), bean.getStartTime(), bean.getEndTime(), bean.getGroupStr(), bean.getChannelId(), ids);

        // 时序数据库解析
        QueryResult query = influxdbService.query(sql);

        //解析列表类查询
        List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);


        for (Map<String, Object> map : maps) {
            if (result.containsKey(map.get("time"))) {
                result.put((String) map.get("time"), result.get(map.get("time")) + (map.get("totalPower") == null ? 0d : (Double) map.get("totalPower")));
            } else {
                result.put((String) map.get("time"), map.get("totalPower") == null ? 0d : (Double) map.get("totalPower"));
            }
        }

        return result;

    }


    public Double getThresholdLoad(DataSheetVo bean) {
        // 获取采集设备
        List<DeviceCollect> deviceCollects = deviceCollectMapper.getDeviceByArea(bean);

        List<DeviceCollect> filterCollects = new ArrayList<>();
        // 总设备上升，其余设备下沉；如果无总设备，返回所有设备
        if (null == bean.getFloorId() && null == bean.getAreaId()) {// 楼宇总设备
            filterCollects = deviceCollects.stream().filter(x -> (x.getIsTotalDevice() == 1) &&
                    (0 == x.getLogicalFloorId()) && (0 == x.getLogicalAreaId())).collect(Collectors.toList());

            if (filterCollects.isEmpty()) {
                filterCollects = deviceCollects;
            }
        } else if (null != bean.getFloorId() && null == bean.getAreaId()) { // 楼层总设备
            filterCollects = deviceCollects.stream().filter(x -> (x.getIsTotalDevice() == 1) &&
                    (0 != x.getLogicalFloorId()) && (0 == x.getLogicalAreaId())).collect(Collectors.toList());

            if (filterCollects.isEmpty()) {
                filterCollects = deviceCollects;
            }
        } else { // 区域总设备
            filterCollects = deviceCollects.stream().filter(x -> (x.getIsTotalDevice() == 1) &&
                    (0 != x.getLogicalAreaId())).collect(Collectors.toList());

            if (filterCollects.isEmpty()) {
                filterCollects = deviceCollects;
            }
        }


        Double d = filterCollects.stream().mapToDouble(x -> Double.parseDouble(x.getLoad())).sum();

        return d;
    }

    public JSONObject getAppChannelYOY(DataSheetVo bean) {
        JSONObject result = new JSONObject();

        if (Strings.isNotEmpty(bean.getTimeStr())) {
            Map<String, List<String>> thisMonths = DateUtils.getMonthsByYear(bean.getTimeStr());
            List<String> thisResult = new ArrayList<>();
            List<String> xData = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                xData.add(thisMonths.get("startTimes").get(i).substring(0, 7));
                bean.setStartTime(thisMonths.get("startTimes").get(i));
                bean.setEndTime(thisMonths.get("endTimes").get(i));
                Map<String, Double> doubleMap = getInfluxExecute(bean);
                double sum = 0d;
                for (Map.Entry<String, Double> item : doubleMap.entrySet()) {
                    sum = sum + item.getValue();
                }
                thisResult.add(String.format("%.2f", sum));
            }

            result.put("thisTime", thisResult);
            result.put("xData", xData);
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
            String thisYear = simpleDateFormat.format(new Date());
            String lastYear = String.valueOf(Integer.parseInt(thisYear) - 1);
            Map<String, List<String>> thisMonths = DateUtils.getMonthsByYear(thisYear);
            Map<String, List<String>> lastMonths = DateUtils.getMonthsByYear(lastYear);
            List<String> thisResult = new ArrayList<>();
            List<String> lastResult = new ArrayList<>();
            List<String> xData = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                xData.add(thisMonths.get("startTimes").get(i).substring(0, 7));
                bean.setStartTime(thisMonths.get("startTimes").get(i));
                bean.setEndTime(thisMonths.get("endTimes").get(i));
                Map<String, Double> doubleMap = getInfluxExecute(bean);
                double sum = 0d;
                for (Map.Entry<String, Double> item : doubleMap.entrySet()) {
                    sum = sum + item.getValue();
                }
                thisResult.add(String.format("%.2f", sum));

                bean.setStartTime(lastMonths.get("startTimes").get(i));
                bean.setEndTime(lastMonths.get("endTimes").get(i));
                doubleMap = getInfluxExecute(bean);
                sum = 0d;
                for (Map.Entry<String, Double> item : doubleMap.entrySet()) {
                    sum = sum + item.getValue();
                }
                lastResult.add(String.format("%.2f", sum));
            }

            result.put("thisTime", thisResult);
            result.put("lastTime", lastResult);
            result.put("xData", xData);
        }

        return result;
    }


    /**
     * 实时同比统计 根据信号类型,能源类型
     * 用电量、有功功率、三相电压、三相电流实时统计
     *
     * @param bean
     * @return
     */
    public JSONObject getChannelYOY(DataSheetVo bean) {

        JSONObject result = new JSONObject();

        Date date = new Date();
        if (bean.getTimeType() == 1) {// 日
            // 今日
            bean.setStartTime(DateUtils.getStartTimeOfCurrentDay(date));
            bean.setEndTime(DateUtils.getEndTimeOfCurrentDay(date));
            Map<String, Double> map = getInfluxExecute(bean);

            for (Map.Entry<String, Double> item : map.entrySet()) {
                bean.getThisXData().put(item.getKey().substring(11, 16), String.format("%.2f", item.getValue()));
            }
            result.put("thisTime", bean.getThisXData().values());
            // 去年今天
            date = DateUtils.getLastYearToday(date);
            bean.setStartTime(DateUtils.getStartTimeOfCurrentDay(date));
            bean.setEndTime(DateUtils.getEndTimeOfCurrentDay(date));
            map = getInfluxExecute(bean);
            for (Map.Entry<String, Double> item : map.entrySet()) {
                bean.getLastXData().put(item.getKey().substring(11, 16), String.format("%.2f", item.getValue()));
            }
            result.put("lastTime", bean.getLastXData().values());
            result.put("xData", bean.getThisXData().keySet());
        } else if (bean.getTimeType() == 2) {//本月
            bean.setStartTime(DateUtils.getStartTimeOfCurrentMonth(date));
            bean.setEndTime(DateUtils.getEndTimeOfCurrentMonth(date));
            Map<String, Double> map = getInfluxExecute(bean);

            for (Map.Entry<String, Double> item : map.entrySet()) {
                bean.getThisXData().put(item.getKey().substring(0, 10), String.format("%.2f", item.getValue()));
            }

            result.put("thisTime", bean.getThisXData().values());
            // 去年今天
            date = DateUtils.getLastYearToday(date);
            bean.setStartTime(DateUtils.getStartTimeOfCurrentMonth(date));
            bean.setEndTime(DateUtils.getEndTimeOfCurrentMonth(date));
            map = getInfluxExecute(bean);

            for (Map.Entry<String, Double> item : map.entrySet()) {
                bean.getLastXData().put(item.getKey().substring(0, 10), String.format("%.2f", item.getValue()));
            }
            result.put("lastTime", bean.getLastXData().values());
            result.put("xData", bean.getThisXData().keySet());
        } else if (bean.getTimeType() == 4) {// 本周
            bean.setStartTime(DateUtils.getStartTimeOfCurrentWeek(date));
            bean.setEndTime(DateUtils.getEndTimeOfCurrentWeek(date));
            bean.setGroupStr("time(1d)");
            bean.setInfluxVal("LAST(val)-FIRST(val)");
            Map<String, Double> map = getInfluxExecute(bean);
            List<String> thisResult = new ArrayList<>();
            for (Map.Entry<String, Double> item : map.entrySet()) {
                thisResult.add(String.format("%.2f", item.getValue()));
            }
            result.put("thisTime", thisResult);

            // 去年今天
            date = DateUtils.getLastYearToday(date);
            bean.setStartTime(DateUtils.getStartTimeOfCurrentWeek(date));
            bean.setEndTime(DateUtils.getStartTimeOfCurrentWeek(date));
            map = getInfluxExecute(bean);
            List<String> lastResult = new ArrayList<>();
            for (Map.Entry<String, Double> item : map.entrySet()) {
                lastResult.add(String.format("%.2f", item.getValue()));
            }
            result.put("lastTime", lastResult);
        } else { // 本年
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
            String thisYear = simpleDateFormat.format(new Date());
            String lastYear = String.valueOf(Integer.parseInt(thisYear) - 1);
            Map<String, List<String>> thisMonths = DateUtils.getMonthsByYear(thisYear);
            Map<String, List<String>> lastMonths = DateUtils.getMonthsByYear(lastYear);
            List<String> thisResult = new ArrayList<>();
            List<String> lastResult = new ArrayList<>();
            List<String> xData = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                xData.add(thisMonths.get("startTimes").get(i).substring(0, 7));
                bean.setStartTime(thisMonths.get("startTimes").get(i));
                bean.setEndTime(thisMonths.get("endTimes").get(i));
                Map<String, Double> doubleMap = getInfluxExecute(bean);
                double sum = 0d;
                for (Map.Entry<String, Double> item : doubleMap.entrySet()) {
                    sum = sum + item.getValue();
                }
                thisResult.add(String.format("%.2f", sum));

                bean.setStartTime(lastMonths.get("startTimes").get(i));
                bean.setEndTime(lastMonths.get("endTimes").get(i));
                doubleMap = getInfluxExecute(bean);
                sum = 0d;
                for (Map.Entry<String, Double> item : doubleMap.entrySet()) {
                    sum = sum + item.getValue();
                }
                lastResult.add(String.format("%.2f", sum));
            }

            result.put("thisTime", thisResult);
            result.put("lastTime", lastResult);
            result.put("xData", xData);
        }

        return result;
    }

    /**
     * 时间类型 timeType 1.日 2.月 3.年 4.周 5.实时 6.自定义
     * 时间类型 channelId 设备通道
     * 时间范围 startTime endTime 开始时间/结束时间
     */
    public JSONObject getEnergyConsumption(Integer timeType, String channelId, String startTime, String endTime) {
        JSONObject result = new JSONObject();
        JSONObject day = new JSONObject();
        JSONObject week = new JSONObject();
        JSONObject month = new JSONObject();
        JSONObject year = new JSONObject();
        JSONObject realTime = new JSONObject();
        JSONObject custom = new JSONObject();
        Date date = new Date();
        DataSheetVo bean = new DataSheetVo();
        // 能源类型 1.电 2.水 3.气
        Integer[] energyType = new Integer[]{1, 2, 3};
        // 返回值
        bean.setInfluxVal("last(val)-first(val)");

        // 类型
        if (StringUtils.isNotEmpty(channelId)) {
            bean.setChannelId(channelId);
        } else {
            bean.setChannelId("ENERGY_TOTAL");
        }

        if (null == timeType || 1 == timeType) {
            /*日*/
            bean.setGroupStr("time(1h)");
            bean.setThisXData(DateUtils.getMins(60));
            bean.setLastXData(DateUtils.getMins(60));
            bean.setSameXData(DateUtils.getMins(60));
            for (int i = 0; i < energyType.length; i++) {
                bean.setEnergyType(energyType[i]);
                JSONObject energyTypeJson = new JSONObject();
                JSONObject sum = new JSONObject();
                JSONObject percentage = new JSONObject();
                List<String> range = new ArrayList<>();
                //现在
                bean.setStartTime(DateUtils.getStartTimeOfCurrentDay(date));
                bean.setEndTime(DateUtils.getEndTimeOfCurrentDay(date));
                Map<String, Double> thisMap = getInfluxExecute(bean);
                for (Map.Entry<String, Double> item : thisMap.entrySet()) {
                    bean.getThisXData().put(item.getKey().substring(11, 16), String.format("%.2f", item.getValue()));
                }
                range.add("this-s:" + bean.getStartTime());
                range.add("this-t:" + bean.getEndTime());
                energyTypeJson.put("thisKey", bean.getThisXData().keySet());
                energyTypeJson.put("thisValue", bean.getThisXData().values());
                energyTypeJson.put("this", bean.getThisXData());
                sum.put("this", calculateTotal(bean.getThisXData()));
                //环比
                bean.setStartTime(DateUtils.getStartTimeOfCurrentDay(DateUtils.getLastday(date)));
                bean.setEndTime(DateUtils.getEndTimeOfCurrentDay(DateUtils.getLastday(date)));
                Map<String, Double> lastMap = getInfluxExecute(bean);
                for (Map.Entry<String, Double> item : lastMap.entrySet()) {
                    bean.getLastXData().put(item.getKey().substring(11, 16), String.format("%.2f", item.getValue()));
                }
                range.add("last-s:" + bean.getStartTime());
                range.add("last-t:" + bean.getEndTime());
                energyTypeJson.put("lastKey", bean.getLastXData().keySet());
                energyTypeJson.put("lastValue", bean.getLastXData().values());
                energyTypeJson.put("last", bean.getLastXData());
                sum.put("last", calculateTotal(bean.getLastXData()));
                //同比
                bean.setStartTime(DateUtils.getEndTimeOfCurrentDay(DateUtils.getLastYearToday(date)));
                bean.setEndTime(DateUtils.getEndTimeOfCurrentDay(DateUtils.getLastYearToday(date)));
                Map<String, Double> sameMap = getInfluxExecute(bean);
                for (Map.Entry<String, Double> item : sameMap.entrySet()) {
                    bean.getSameXData().put(item.getKey().substring(11, 16), String.format("%.2f", item.getValue()));
                }
                range.add("same-s:" + bean.getStartTime());
                range.add("same-t:" + bean.getEndTime());
                energyTypeJson.put("sameKey", bean.getSameXData().keySet());
                energyTypeJson.put("sameValue", bean.getSameXData());
                energyTypeJson.put("same", bean.getSameXData());
                sum.put("same", calculateTotal(bean.getSameXData()));

                energyTypeJson.put("sum", sum);
                energyTypeJson.put("rang", range);

                /*同比*/
                BigDecimal a = new BigDecimal(String.valueOf(sum.getOrDefault("this", 0)));
                BigDecimal b = new BigDecimal(String.valueOf(sum.getOrDefault("last", 0)));
                BigDecimal YoY;
                if (b.compareTo(BigDecimal.ZERO) == 0 || b.equals(BigDecimal.ZERO)) {
                    YoY = BigDecimal.ZERO;
                } else {
                    // 计算 ((b-a)/b)×100%
                    YoY = b.subtract(a).divide(b, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
                }
                percentage.put("YoY", YoY);
                energyTypeJson.put("percentage", percentage);

                if (energyType[i] == 1) {
                    //电
                    day.put("electricity", energyTypeJson);
                } else if (energyType[i] == 2) {
                    //水
                    day.put("water", energyTypeJson);
                } else if (energyType[i] == 3) {
                    //气
                    day.put("gas", energyTypeJson);
                }
            }
        }
        if (null == timeType || 2 == timeType) {
            /*月*/
            bean.setGroupStr("time(1d)");
            for (int i = 0; i < energyType.length; i++) {
                if(energyType[i] == 2){
                    bean.setChannelId("WATER_L");
                }
                if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
                    bean.setThisXData(DateUtils.getDatesBetween(startTime, endTime));
                    bean.setLastXData(DateUtils.getDatesBetween(startTime, endTime));
                    bean.setSameXData(DateUtils.getDatesBetween(startTime, endTime));
                } else {
                    bean.setThisXData(DateUtils.getMonthDays(date));
                    bean.setLastXData(DateUtils.getLastMonthDays(date));
                    bean.setSameXData(DateUtils.getLastYearDays(date));
                }
                bean.setEnergyType(energyType[i]);
                JSONObject energyTypeJson = new JSONObject();
                JSONObject sum = new JSONObject();
                JSONObject percentage = new JSONObject();
                List<String> range = new ArrayList<>();
                //现在
                if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
                    bean.setStartTime(startTime);
                    bean.setEndTime(DateUtils.getEndTimeOfCurrentDay(DateUtil.parseDate(endTime)));
                } else {
                    bean.setStartTime(DateUtils.getStartTimeOfCurrentMonth(date));
                    bean.setEndTime(DateUtils.getEndTimeOfCurrentMonth(date));
                }
                Map<String, Double> thisMap = getInfluxExecute(bean);
                for (Map.Entry<String, Double> item : thisMap.entrySet()) {
                    bean.getThisXData().put(item.getKey().substring(0, 10), String.format("%.2f", item.getValue()));
                }
                range.add("this-s:" + bean.getStartTime());
                range.add("this-t:" + bean.getEndTime());
                energyTypeJson.put("thisKey", bean.getThisXData().keySet());
                energyTypeJson.put("thisValue", bean.getThisXData().values());
                energyTypeJson.put("this", bean.getThisXData());
                sum.put("this", calculateTotal(bean.getThisXData()));
                //环比
                if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
                    bean.setStartTime(startTime);
                    bean.setEndTime(endTime);
                } else {
                    bean.setStartTime(DateUtils.getStartTimeOfCurrentMonth(DateUtils.getLastMonth(date)));
                    bean.setEndTime(DateUtils.getEndTimeOfCurrentMonth(DateUtils.getLastMonth(date)));
                }
                Map<String, Double> lastMap = getInfluxExecute(bean);
                for (Map.Entry<String, Double> item : lastMap.entrySet()) {
                    bean.getLastXData().put(item.getKey().substring(0, 10), String.format("%.2f", item.getValue()));
                }
                range.add("last-s:" + bean.getStartTime());
                range.add("last-t:" + bean.getEndTime());
                energyTypeJson.put("lastKey", bean.getLastXData().keySet());
                energyTypeJson.put("lastValue", bean.getLastXData().values());
                energyTypeJson.put("last", bean.getLastXData());
                sum.put("last", calculateTotal(bean.getLastXData()));
                //同比
                if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
                    bean.setStartTime(startTime);
                    bean.setEndTime(endTime);
                } else {
                    bean.setStartTime(DateUtils.getStartTimeOfCurrentMonth(DateUtils.getLastYearToday(date)));
                    bean.setEndTime(DateUtils.getEndTimeOfCurrentMonth(DateUtils.getLastYearToday(date)));
                }
                Map<String, Double> sameMap = getInfluxExecute(bean);
                for (Map.Entry<String, Double> item : sameMap.entrySet()) {
                    bean.getSameXData().put(item.getKey().substring(0, 10), String.format("%.2f", item.getValue()));
                }
                range.add("same-s:" + bean.getStartTime());
                range.add("same-t:" + bean.getEndTime());
                energyTypeJson.put("sameKey", bean.getSameXData().keySet());
                energyTypeJson.put("sameValue", bean.getSameXData().values());
                energyTypeJson.put("same", bean.getSameXData());
                sum.put("same", calculateTotal(bean.getSameXData()));

                energyTypeJson.put("sum", sum);
                energyTypeJson.put("rang", range);

                /*同比*/
                BigDecimal a = new BigDecimal(String.valueOf(sum.getOrDefault("this", 0)));
                BigDecimal b = new BigDecimal(String.valueOf(sum.getOrDefault("last", 0)));
                BigDecimal YoY;
                if (b.compareTo(BigDecimal.ZERO) == 0 || b.equals(BigDecimal.ZERO)) {
                    YoY = BigDecimal.ZERO;
                } else {
                    // 计算 ((b-a)/b)×100%
                    YoY = b.subtract(a).divide(b, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
                }
                percentage.put("YoY", YoY);
                energyTypeJson.put("percentage", percentage);

                if (energyType[i] == 1) {
                    //电
                    month.put("electricity", energyTypeJson);
                } else if (energyType[i] == 2) {
                    //水
                    month.put("water", energyTypeJson);
                } else if (energyType[i] == 3) {
                    //气
                    month.put("gas", energyTypeJson);
                }
            }
        }
        if (null == timeType || 3 == timeType) {
            /*年*/
            bean.setGroupStr("time(1d)");
            for (int i = 0; i < energyType.length; i++) {
                if(energyType[i] == 2){
                    bean.setChannelId("WATER_L");
                }
                Date lastDate = DateUtil.offsetMonth(date, -12);
                if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
                    Integer yearStart = Integer.valueOf(startTime.substring(0, 4));
                    Integer monthStart = Integer.valueOf(startTime.substring(5, 7));
                    Integer yearEnd = Integer.valueOf(endTime.substring(0, 4));
                    Integer monthEnd = Integer.valueOf(endTime.substring(5, 7));
                    bean.setThisXData(DateUtils.getDatesBetweenMonths(yearStart, monthStart, yearEnd, monthEnd));
                    bean.setLastXData(DateUtils.getDatesBetweenMonths(yearStart - 1, monthStart, yearEnd - 1, monthEnd));
                } else {
                    bean.setThisXData(DateUtils.getDatesBetweenMonths(DateUtil.year(date), 1, DateUtil.year(date), 12));
                    bean.setLastXData(DateUtils.getDatesBetweenMonths(DateUtil.year(lastDate), 1, DateUtil.year(lastDate), 12));
                }
                bean.setEnergyType(energyType[i]);
                JSONObject energyTypeJson = new JSONObject();
                JSONObject sum = new JSONObject();
                JSONObject percentage = new JSONObject();
                List<String> range = new ArrayList<>();
                //现在
                if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
                    bean.setStartTime(startTime + "-01T00:00:00Z");
                    bean.setEndTime(endTime + "-31T23:59:59Z");
                } else {
                    bean.setStartTime(DateUtil.year(date) + "-01-01T00:00:00Z");
                    bean.setEndTime(DateUtil.year(date) + "-12-31T23:59:59Z");
                }
                Map<String, Double> thisMap = getInfluxExecute(bean);
                for (Map.Entry<String, Double> item : thisMap.entrySet()) {
                    BigDecimal source = new BigDecimal(String.format("%.2f", item.getValue()));
                    BigDecimal target = new BigDecimal(bean.getThisXData().get(item.getKey().substring(0, 7)));
                    bean.getThisXData().put(item.getKey().substring(0, 7), String.valueOf(source.add(target)));
                }
                range.add("this-s:" + bean.getStartTime());
                range.add("this-t:" + bean.getEndTime());
                energyTypeJson.put("thisKey", bean.getThisXData().keySet());
                energyTypeJson.put("thisValue", bean.getThisXData().values());
                energyTypeJson.put("this", bean.getThisXData());
                sum.put("this", calculateTotal(bean.getThisXData()));
                //环比/同比
                if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
                    bean.setStartTime(Integer.valueOf(startTime.substring(0, 4)) - 1 + "-01T00:00:00Z");
                    bean.setEndTime(Integer.valueOf(endTime.substring(0, 4)) - 1 + "-31T23:59:59Z");
                } else {
                    bean.setStartTime(DateUtil.year(lastDate) + "-01-01T00:00:00Z");
                    bean.setEndTime(DateUtil.year(lastDate) + "-12-31T23:59:59Z");
                }
                Map<String, Double> lastMap = getInfluxExecute(bean);
                for (Map.Entry<String, Double> item : lastMap.entrySet()) {
                    BigDecimal source = new BigDecimal(String.format("%.2f", item.getValue()));
                    BigDecimal target = new BigDecimal(bean.getLastXData().get(item.getKey().substring(0, 7)));
                    bean.getLastXData().put(item.getKey().substring(0, 7), String.valueOf(source.add(target)));
                }
                range.add("last-s:" + bean.getStartTime());
                range.add("last-t:" + bean.getEndTime());
                energyTypeJson.put("lastKey", bean.getLastXData().keySet());
                energyTypeJson.put("lastValue", bean.getLastXData().values());
                energyTypeJson.put("last", bean.getLastXData());
                sum.put("last", calculateTotal(bean.getLastXData()));

                energyTypeJson.put("sum", sum);
                energyTypeJson.put("rang", range);

                /*同比*/
                BigDecimal a = new BigDecimal(String.valueOf(sum.getOrDefault("this", 0)));
                BigDecimal b = new BigDecimal(String.valueOf(sum.getOrDefault("last", 0)));
                BigDecimal YoY;
                if (b.compareTo(BigDecimal.ZERO) == 0 || b.equals(BigDecimal.ZERO)) {
                    YoY = BigDecimal.ZERO;
                } else {
                    // 计算 ((b-a)/b)×100%
                    YoY = b.subtract(a).divide(b, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
                }
                percentage.put("YoY", YoY);
                energyTypeJson.put("percentage", percentage);


                if (energyType[i] == 1) {
                    //电
                    year.put("electricity", energyTypeJson);
                } else if (energyType[i] == 2) {
                    //水
                    year.put("water", energyTypeJson);
                } else if (energyType[i] == 3) {
                    //气
                    year.put("gas", energyTypeJson);
                }
            }
        }
        if (null == timeType || 4 == timeType) {
            /*周*/
            bean.setInfluxVal("LAST(val)-FIRST(val)");
            bean.setGroupStr("time(1d)");
            bean.setThisXData(DateUtils.getDatesBetween(
                    DateUtil.formatDate(DateUtil.offsetDay(date, -7)),
                    DateUtil.formatDate(date)
            ));
            bean.setLastXData(DateUtils.getDatesBetween(
                    DateUtil.formatDate(DateUtil.offsetDay(date, -14)),
                    DateUtil.formatDate(DateUtil.offsetDay(date, -7))
            ));
            bean.setSameXData(DateUtils.getDatesBetween(
                    DateUtil.formatDate(
                            DateUtil.offsetDay(getLastYearToday(date), -7)
                    ),
                    DateUtil.formatDate(getLastYearToday(date))
            ));
            for (int i = 0; i < energyType.length; i++) {
                bean.setEnergyType(energyType[i]);
                JSONObject energyTypeJson = new JSONObject();
                JSONObject sum = new JSONObject();
                JSONObject percentage = new JSONObject();
                List<String> range = new ArrayList<>();
                //现在
                bean.setStartTime(DateUtil.formatDate(DateUtil.offsetDay(date, -7)));
                bean.setEndTime(DateUtil.formatDate(date));
                Map<String, Double> thisMap = getInfluxExecute(bean);
                for (Map.Entry<String, Double> item : thisMap.entrySet()) {
                    bean.getThisXData().put(item.getKey().substring(0, 10), String.format("%.2f", item.getValue()));
                }
                range.add("this-s:" + bean.getStartTime());
                range.add("this-t:" + bean.getEndTime());
                energyTypeJson.put("this", bean.getThisXData());
                sum.put("this", calculateTotal(bean.getThisXData()));
                //环比
                bean.setStartTime(DateUtil.formatDate(DateUtil.offsetDay(date, -14)));
                bean.setEndTime(DateUtil.formatDate(DateUtil.offsetDay(date, -7)));
                Map<String, Double> lastMap = getInfluxExecute(bean);
                for (Map.Entry<String, Double> item : lastMap.entrySet()) {
                    bean.getLastXData().put(item.getKey().substring(0, 10), String.format("%.2f", item.getValue()));
                }
                range.add("last-s:" + bean.getStartTime());
                range.add("last-t:" + bean.getEndTime());
                energyTypeJson.put("lastKey", bean.getLastXData().keySet());
                energyTypeJson.put("lastValue", bean.getLastXData().values());
                energyTypeJson.put("last", bean.getLastXData());
                sum.put("last", calculateTotal(bean.getLastXData()));
                //同比
                Date someDate = getLastYearToday(date);
                bean.setStartTime(DateUtil.formatDate(DateUtil.offsetDay(someDate, -7)));
                bean.setEndTime(DateUtil.formatDate(someDate));
                Map<String, Double> sameMap = getInfluxExecute(bean);
                for (Map.Entry<String, Double> item : sameMap.entrySet()) {
                    bean.getSameXData().put(item.getKey().substring(0, 10), String.format("%.2f", item.getValue()));
                }
                range.add("same-s:" + bean.getStartTime());
                range.add("same-t:" + bean.getEndTime());
                energyTypeJson.put("sameKey", bean.getSameXData().keySet());
                energyTypeJson.put("sameValue", bean.getSameXData().values());
                energyTypeJson.put("same", bean.getSameXData());
                sum.put("same", calculateTotal(bean.getSameXData()));

                energyTypeJson.put("sum", sum);
                energyTypeJson.put("rang", range);

                /*同比*/
                BigDecimal a = new BigDecimal(String.valueOf(sum.getOrDefault("this", 0)));
                BigDecimal b = new BigDecimal(String.valueOf(sum.getOrDefault("last", 0)));
                BigDecimal YoY;
                if (b.compareTo(BigDecimal.ZERO) == 0 || b.equals(BigDecimal.ZERO)) {
                    YoY = BigDecimal.ZERO;
                } else {
                    // 计算 ((b-a)/b)×100%
                    YoY = b.subtract(a).divide(b, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
                }
                percentage.put("YoY", YoY);
                energyTypeJson.put("percentage", percentage);

                if (energyType[i] == 1) {
                    //电
                    week.put("electricity", energyTypeJson);
                } else if (energyType[i] == 2) {
                    //水
                    week.put("water", energyTypeJson);
                } else if (energyType[i] == 3) {
                    //气
                    week.put("gas", energyTypeJson);
                }
            }
        }

        if (null == timeType || 5 == timeType) {
            bean.setInfluxVal("last(val)");
            bean.setGroupStr("time(1h)");
            LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
            linkedHashMap.put(DateUtils.format(date), "0.00");
            bean.setThisXData(linkedHashMap);

            for (int i = 0; i < energyType.length; i++) {
                bean.setEnergyType(energyType[i]);
                JSONObject energyTypeJson = new JSONObject();
                JSONObject sum = new JSONObject();
                List<String> range = new ArrayList<>();
                //现在 实时 最后一条数据
//                bean.setStartTime(DateUtils.format(org.apache.commons.lang3.time.DateUtils.addHours(date, -1)));
//                bean.setEndTime(DateUtils.format(date));
                Map<String, Double> thisMap = getInfluxExecute(bean);
                for (Map.Entry<String, Double> item : thisMap.entrySet()) {
                    bean.getThisXData().put(item.getKey().substring(11, 16), String.format("%.2f", item.getValue()));
                }
                range.add("this-s:" + bean.getStartTime());
                range.add("this-t:" + bean.getEndTime());
                energyTypeJson.put("thisKey", bean.getThisXData().keySet());
                energyTypeJson.put("thisValue", bean.getThisXData().values());
                energyTypeJson.put("this", bean.getThisXData());
                sum.put("this", calculateTotal(bean.getThisXData()));

                if (energyType[i] == 1) {
                    String res = "0.00";
                    String sql = "SELECT LAST(val) FROM datasheet WHERE channelId = 'ENERGY_TOTAL'";
                    List<Map<String, Object>> lists = queryResultProcess1(influxdbService.query(sql));
                    for (int x = 0; x < lists.size(); x++) {
                        Map<String, Object> map = lists.get(x);
                        Double spread = (Double) map.get("last");
                        if (spread == null) {
                            spread = 0.00;
                        }
                        res = new BigDecimal(spread).setScale(2, RoundingMode.HALF_UP).toString();
                    }
                    //电
                    energyTypeJson.put("thisValue",res);
                    realTime.put("electricity", energyTypeJson);
                } else if (energyType[i] == 2) {
                    String res = "0.00";
                    String sql = "SELECT LAST(val) FROM datasheet WHERE channelId = 'WATER_P'";
                    List<Map<String, Object>> lists = queryResultProcess1(influxdbService.query(sql));
                    for (int x = 0; x < lists.size(); x++) {
                        Map<String, Object> map = lists.get(x);
                        Double spread = (Double) map.get("last");
                        if (spread == null) {
                            spread = 0.00;
                        }
                        res = new BigDecimal(spread).setScale(2, RoundingMode.HALF_UP).toString();
                    }
                    //水
                    energyTypeJson.put("thisValue",res);
                    realTime.put("water", energyTypeJson);
                } else if (energyType[i] == 3) {
                    //气
                    realTime.put("gas", energyTypeJson);
                }
            }
        }

        if (null == timeType || 6 == timeType) {
            bean.setGroupStr(null);
            bean.setStartTime(DateUtils.format(date));
            bean.setEndTime(DateUtils.format(date));
            LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
            linkedHashMap.put(DateUtils.format(date), "0.00");
            bean.setThisXData(linkedHashMap);
            for (int i = 0; i < energyType.length; i++) {
                bean.setEnergyType(energyType[i]);
                if(energyType[i] == 2){
                    bean.setChannelId("WATER_L");
                }
                JSONObject energyTypeJson = new JSONObject();
                JSONObject percentage = new JSONObject();
                energyTypeJson.put("day", "0.00");
                energyTypeJson.put("lastDay", "0.00");
                energyTypeJson.put("week", "0.00");
                energyTypeJson.put("lastWeek", "0.00");
                energyTypeJson.put("month", "0.00");
                energyTypeJson.put("lastMonth", "0.00");
                energyTypeJson.put("year", "0.00");
                energyTypeJson.put("lastYear", "0.00");
                //当天
                bean.setStartTime(DateUtil.formatDate(date) + " 00:00:00");
                bean.setEndTime(DateUtil.formatDate(date) + " 23:59:59");
                Map<String, Double> dayMap = getInfluxExecute(bean);
                for (Map.Entry<String, Double> item : dayMap.entrySet()) {
                    energyTypeJson.put("day", String.format("%.2f", item.getValue()));
                }
                //昨天
                bean.setStartTime(DateUtil.formatDate(DateUtil.offsetDay(date, -1)) + " 00:00:00");
                bean.setEndTime(DateUtil.formatDate(DateUtil.offsetDay(date, -1)) + " 23:59:59");
                Map<String, Double> lastDayMap = getInfluxExecute(bean);
                for (Map.Entry<String, Double> item : lastDayMap.entrySet()) {
                    energyTypeJson.put("lastDay", String.format("%.2f", item.getValue()));
                }
                //这周
                bean.setStartTime(DateUtils.format(DateUtil.offsetDay(date, -7)));
                bean.setEndTime(DateUtils.format(DateUtil.offsetDay(date, 0)));
                Map<String, Double> weekMap = getInfluxExecute(bean);
                for (Map.Entry<String, Double> item : weekMap.entrySet()) {
                    energyTypeJson.put("week", String.format("%.2f", item.getValue()));
                }
                //上周
                bean.setStartTime(DateUtils.format(DateUtil.offsetDay(date, -14)));
                bean.setEndTime(DateUtils.format(DateUtil.offsetDay(date, -7)));
                Map<String, Double> lastWeekMap = getInfluxExecute(bean);
                for (Map.Entry<String, Double> item : lastWeekMap.entrySet()) {
                    energyTypeJson.put("lastWeek", String.format("%.2f", item.getValue()));
                }
                //当月
                // 获取当前日期
                Calendar calendar = Calendar.getInstance();
                // 设置日期为当月的第一天
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                // 将日期设置为下个月
                calendar.add(Calendar.MONTH, 1);
                // 将日期设置为上个月的最后一天
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                // 获取当月的最后一天
                int lastDay = calendar.get(Calendar.DAY_OF_MONTH);
                // 获取当前日期
                LocalDate today = LocalDate.now();
                // 获取当前年份和月份
                int yearNew = today.getYear();
                int monthNew = today.getMonthValue();

                bean.setStartTime(yearNew + "-" + String.format("%02d", monthNew) + "-" + "01" + " 00:00:00");
                bean.setEndTime(yearNew + "-" + String.format("%02d", monthNew) + "-" + lastDay + " 23:59:59");
                Map<String, Double> monthMap = getInfluxExecute(bean);
                for (Map.Entry<String, Double> item : monthMap.entrySet()) {
                    energyTypeJson.put("month", String.format("%.2f", item.getValue()));
                }
                //上月
                yearNew = calendar.get(Calendar.YEAR);
                monthNew = calendar.get(Calendar.MONTH);
                lastDay = calendar.get(Calendar.DAY_OF_MONTH);
                bean.setStartTime(yearNew + "-" + String.format("%02d", monthNew) + "-" + "01" + " 00:00:00");
                bean.setEndTime(yearNew + "-" + String.format("%02d", monthNew) + "-" + lastDay + " 23:59:59");
                Map<String, Double> lastMonthMap = getInfluxExecute(bean);
                for (Map.Entry<String, Double> item : lastMonthMap.entrySet()) {
                    energyTypeJson.put("lastMonth", String.format("%.2f", item.getValue()));
                }
                //今年
                bean.setStartTime(DateUtil.year(date) + "-" + "01" + "-" + "01" + " 00:00:00");
                bean.setEndTime(DateUtil.year(date) + "-" + "12" + "-" + "31" + " 23:59:59");
                Map<String, Double> yearMap = getInfluxExecute(bean);
                for (Map.Entry<String, Double> item : yearMap.entrySet()) {
                    energyTypeJson.put("year", String.format("%.2f", item.getValue()));
                }
                //去年
                bean.setStartTime(DateUtil.year(date) - 1 + "-" + "01" + "-" + "01" + " 00:00:00");
                bean.setEndTime(DateUtil.year(date) - 1 + "-" + "12" + "-" + "31" + " 23:59:59");
                Map<String, Double> lastYearMap = getInfluxExecute(bean);
                for (Map.Entry<String, Double> item : lastYearMap.entrySet()) {
                    energyTypeJson.put("lastYear", String.format("%.2f", item.getValue()));
                }

                /*同比*/
                BigDecimal a = new BigDecimal(String.valueOf(energyTypeJson.getOrDefault("day", 0)));
                BigDecimal b = new BigDecimal(String.valueOf(energyTypeJson.getOrDefault("lastDay", 0)));
                BigDecimal YOY;
                if (b.compareTo(BigDecimal.ZERO) == 0 || b.equals(BigDecimal.ZERO)) {
                    YOY = BigDecimal.ZERO;
                } else {
                    YOY = b.subtract(a).divide(b, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
                }
                percentage.put("day", YOY);

                a = new BigDecimal(String.valueOf(energyTypeJson.getOrDefault("week", 0)));
                b = new BigDecimal(String.valueOf(energyTypeJson.getOrDefault("lastWeek", 0)));
                if (b.compareTo(BigDecimal.ZERO) == 0 || b.equals(BigDecimal.ZERO)) {
                    YOY = BigDecimal.ZERO;
                } else {
                    YOY = b.subtract(a).divide(b, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
                }
                percentage.put("week", YOY);

                a = new BigDecimal(String.valueOf(energyTypeJson.getOrDefault("month", 0)));
                b = new BigDecimal(String.valueOf(energyTypeJson.getOrDefault("lastMonth", 0)));
                if (b.compareTo(BigDecimal.ZERO) == 0 || b.equals(BigDecimal.ZERO)) {
                    YOY = BigDecimal.ZERO;
                } else {
                    YOY = b.subtract(a).divide(b, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
                }
                percentage.put("month", YOY);

                a = new BigDecimal(String.valueOf(energyTypeJson.getOrDefault("year", 0)));
                b = new BigDecimal(String.valueOf(energyTypeJson.getOrDefault("lastYear", 0)));
                if (b.compareTo(BigDecimal.ZERO) == 0 || b.equals(BigDecimal.ZERO)) {
                    YOY = BigDecimal.ZERO;
                } else {
                    YOY = b.subtract(a).divide(b, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
                }
                percentage.put("year", YOY);

                energyTypeJson.put("percentage", percentage);

                if (energyType[i] == 1) {
                    //电
                    custom.put("electricity", energyTypeJson);
                } else if (energyType[i] == 2) {
                    //水
                    custom.put("water", energyTypeJson);
                } else if (energyType[i] == 3) {
                    //气
                    custom.put("gas", energyTypeJson);
                }
            }
        }

        result.put("day", day);
        result.put("week", week);
        result.put("month", month);
        result.put("year", year);
        result.put("realTime", realTime);
        result.put("custom", custom);
        return result;
    }

    public BigDecimal calculateTotal(LinkedHashMap<String, String> sameXData) {
        BigDecimal total = BigDecimal.valueOf(0.0);
        // 检查 sameXData 不为空
        if (sameXData != null) {
            // 迭代 sameXData 中的值，并将其转换为 double 类型后累加
            for (String value : sameXData.values()) {
                try {
                    BigDecimal bigDecimal = new BigDecimal(value);
                    total = total.add(bigDecimal);
                } catch (NumberFormatException e) {
                    // 如果值无法解析为 double，可以进行适当的处理，例如跳过或记录错误
                    System.err.println("Error parsing value: " + value);
                }
            }
        }
        return total;
    }

    public String getFloorWater(DataSheetVo bean) {

        Date date = new Date();

        if (bean.getTimeType() == 1) {
            bean.setStartTime(DateUtils.getStartTimeOfCurrentDay(date));
            bean.setEndTime(DateUtils.getEndTimeOfCurrentDay(date));

        } else if (bean.getTimeType() == 2) {
            bean.setStartTime(DateUtils.getStartTimeOfCurrentMonth(date));
            bean.setEndTime(DateUtils.getEndTimeOfCurrentMonth(date));
        } else {
            bean.setStartTime(DateUtils.getStartTimeOfCurrentYear(date));
            bean.setEndTime(DateUtils.getEndTimeOfCurrentYear(date));
        }
        Map<String, Double> map = getInfluxExecute(bean);
        double sum = 0d;
        for (Map.Entry<String, Double> item : map.entrySet()) {
            sum = sum + item.getValue();
        }


        return String.format("%.2f", sum);
    }


    /**
     * 实时趋势
     *
     * @param bean
     * @return
     */

    public JSONObject getChannelLoad(DataSheetVo bean) {
        JSONObject result = new JSONObject();

        Date date = new Date();

        if (bean.getTimeType() == 1) {// 日
            // 今日
            bean.setStartTime(DateUtils.getStartTimeOfCurrentDay(date));
            bean.setEndTime(DateUtils.getEndTimeOfCurrentDay(date));
            Map<String, Double> map = getInfluxExecute(bean);


            for (Map.Entry<String, Double> item : map.entrySet()) {
                bean.getThisXData().put(item.getKey().substring(11, 16), String.format("%.2f", item.getValue()));
            }
            result.put("thisTime", bean.getThisXData().values());
            result.put("xData", bean.getThisXData().keySet());
        } else if (bean.getTimeType() == 2) {//本月
            bean.setStartTime(DateUtils.getStartTimeOfCurrentMonth(date));
            bean.setEndTime(DateUtils.getEndTimeOfCurrentMonth(date));
            Map<String, Double> map = getInfluxExecute(bean);

            for (Map.Entry<String, Double> item : map.entrySet()) {
                bean.getThisXData().put(item.getKey().substring(0, 10), String.format("%.2f", item.getValue()));
            }
            result.put("thisTime", bean.getThisXData().values());
            result.put("xData", bean.getThisXData().keySet());
        } else if (bean.getTimeType() == 4) {// 本周
            bean.setStartTime(DateUtils.getStartTimeOfCurrentWeek(date));
            bean.setEndTime(DateUtils.getEndTimeOfCurrentWeek(date));
            Map<String, Double> map = getInfluxExecute(bean);
            List<String> thisResult = new ArrayList<>();
            for (Map.Entry<String, Double> item : map.entrySet()) {
                thisResult.add(String.format("%.2f", item.getValue()));
            }
            result.put("thisTime", thisResult);
        } else if (bean.getTimeType() == 5) {// 上个月
            LocalDate now = LocalDate.now();
            LocalDate firstDayOfLastMonth = now.with(TemporalAdjusters.firstDayOfMonth()).minusMonths(1);
            LocalDate lastDayOfLastMonth = now.with(TemporalAdjusters.firstDayOfMonth()).minusDays(1);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String firstDay = firstDayOfLastMonth.format(formatter);
            String lastDay = lastDayOfLastMonth.format(formatter);

            bean.setStartTime(firstDay);
            bean.setEndTime(lastDay);

            Map<String, Double> map = getInfluxExecute(bean);

            for (Map.Entry<String, Double> item : map.entrySet()) {
                bean.getThisXData().put(item.getKey().substring(0, 10), String.format("%.2f", item.getValue()));
            }
            result.put("lastTime", bean.getThisXData().values());
            result.put("xData", bean.getThisXData().keySet());
        } else { // 本年
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
            String thisYear = simpleDateFormat.format(new Date());
            Map<String, List<String>> thisMonths = DateUtils.getMonthsByYear(thisYear);
            List<String> thisResult = new ArrayList<>();
            List<String> xData = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                xData.add(thisMonths.get("startTimes").get(i).substring(0, 7));
                bean.setStartTime(thisMonths.get("startTimes").get(i));
                bean.setEndTime(thisMonths.get("endTimes").get(i));
                Map<String, Double> doubleMap = getInfluxExecute(bean);
                double sum = 0d;
                for (Map.Entry<String, Double> item : doubleMap.entrySet()) {
                    sum = sum + item.getValue();
                }
                thisResult.add(String.format("%.2f", sum));
            }

            result.put("thisTime", thisResult);
            result.put("xData", xData);
        }


        return result;
    }


    /**
     * 实时环比统计
     *
     * @param bean
     * @return
     */
    public JSONObject getChannelMOM(DataSheetVo bean) {
        JSONObject result = new JSONObject();

        Date date = new Date();
        if (bean.getTimeType() == 1) {// 日
            // 今日
            bean.setStartTime(DateUtils.getStartTimeOfCurrentDay(date));
            bean.setEndTime(DateUtils.getEndTimeOfCurrentDay(date));
            Map<String, Double> map = getInfluxExecute(bean);

            for (Map.Entry<String, Double> item : map.entrySet()) {

                bean.getThisXData().put(item.getKey().substring(11, 16), String.format("%.2f", item.getValue()));
            }
            result.put("thisTime", bean.getThisXData().values());
            // 昨天
            date = DateUtils.getLastday(date);
            bean.setStartTime(DateUtils.getStartTimeOfCurrentDay(date));
            bean.setEndTime(DateUtils.getEndTimeOfCurrentDay(date));
            map = getInfluxExecute(bean);

            for (Map.Entry<String, Double> item : map.entrySet()) {
                bean.getLastXData().put(item.getKey().substring(11, 16), String.format("%.2f", item.getValue()));
            }
            result.put("lastTime", bean.getLastXData().values());
            result.put("xData", bean.getThisXData().keySet());
        } else if (bean.getTimeType() == 2) {//本月
            bean.setStartTime(DateUtils.getStartTimeOfCurrentMonth(date));
            bean.setEndTime(DateUtils.getEndTimeOfCurrentMonth(date));
            Map<String, Double> map = getInfluxExecute(bean);
            for (Map.Entry<String, Double> item : map.entrySet()) {
                bean.getThisXData().put(item.getKey().substring(0, 10), String.format("%.2f", item.getValue()));
            }


            result.put("thisTime", bean.getThisXData().values());
            // 上个月某一天
            date = DateUtils.getLastMonth(date);
            bean.setStartTime(DateUtils.getStartTimeOfCurrentMonth(date));
            bean.setEndTime(DateUtils.getEndTimeOfCurrentMonth(date));
            map = getInfluxExecute(bean);

            for (Map.Entry<String, Double> item : map.entrySet()) {
                bean.getLastXData().put(item.getKey().substring(0, 10), String.format("%.2f", item.getValue()));
            }
            result.put("lastTime", bean.getLastXData().values());
            result.put("xData", bean.getThisXData().keySet().stream().map(x->x.substring(5,10)).collect(Collectors.toList()));
        } else if (bean.getTimeType() == 4) {// 本周
            bean.setStartTime(DateUtils.getStartTimeOfCurrentWeek(date));
            bean.setEndTime(DateUtils.getEndTimeOfCurrentWeek(date));
            Map<String, Double> map = getInfluxExecute(bean);
            List<String> thisResult = new ArrayList<>();
            for (Map.Entry<String, Double> item : map.entrySet()) {
                thisResult.add(String.format("%.2f", item.getValue()));
            }
            result.put("thisTime", thisResult);

            // 上周某天
            date = DateUtils.getLastWeek(date);
            bean.setStartTime(DateUtils.getStartTimeOfCurrentWeek(date));
            bean.setEndTime(DateUtils.getStartTimeOfCurrentWeek(date));
            map = getInfluxExecute(bean);
            List<String> lastResult = new ArrayList<>();
            for (Map.Entry<String, Double> item : map.entrySet()) {
                lastResult.add(String.format("%.2f", item.getValue()));
            }
            result.put("lastTime", lastResult);
        } else { // 本年
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
            String thisYear = simpleDateFormat.format(new Date());
            String lastYear = String.valueOf(Integer.parseInt(thisYear) - 1);
            Map<String, List<String>> thisMonths = DateUtils.getMonthsByYear(thisYear);
            Map<String, List<String>> lastMonths = DateUtils.getMonthsByYear(lastYear);
            List<String> thisResult = new ArrayList<>();
            List<String> lastResult = new ArrayList<>();
            List<String> xData = new ArrayList<>();
            List<String> xData2 = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                xData.add(thisMonths.get("startTimes").get(i).substring(0, 7));
                xData2.add(lastMonths.get("startTimes").get(i).substring(0, 7));

                bean.setStartTime(thisMonths.get("startTimes").get(i));
                bean.setEndTime(thisMonths.get("endTimes").get(i));
                Map<String, Double> doubleMap = getInfluxExecute(bean);
                double sum = 0d;
                for (Map.Entry<String, Double> item : doubleMap.entrySet()) {
                    sum = sum + item.getValue();
                }
                thisResult.add(String.format("%.2f", sum));

                bean.setStartTime(lastMonths.get("startTimes").get(i));
                bean.setEndTime(lastMonths.get("endTimes").get(i));
                doubleMap = getInfluxExecute(bean);
                sum = 0d;
                for (Map.Entry<String, Double> item : doubleMap.entrySet()) {
                    sum = sum + item.getValue();
                }
                lastResult.add(String.format("%.2f", sum));
            }

            result.put("thisTime", thisResult);
            result.put("lastTime", lastResult);
            result.put("xData", xData.stream().map(x->x.substring(5,7)).collect(Collectors.toList()));
            result.put("xData2", xData2.stream().map(x->x.substring(5,7)).collect(Collectors.toList()));
        }

        return result;
    }


    public JSONObject consumptionTrend(DataSheetVo bean) {
        JSONObject result = new JSONObject();

            // 今日

            Map<String, Double> map = getInfluxExecute(bean);
        LinkedHashMap<String,String> xdata = new LinkedHashMap<>() ;
            for (Map.Entry<String, Double> item : map.entrySet()) {
                xdata.put(item.getKey().substring(0, 10), String.format("%.2f", item.getValue()));
            }
            result.put("thisTime", xdata.values());
            result.put("xData", xdata.keySet());

        return result;
    }


    /**
     * 实时负荷趋势sql
     *
     * @param ids
     * @param channelId
     * @param startTime
     * @param endTime
     * @return
     */
    private String getRealTimeLoad(List<String> ids, String channelId, String startTime, String endTime) {
        String idstr = influxdbInToString(ids);
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT LAST(val) as totalPower from datasheet ");
        sql.append("WHERE deviceCollectId=~/" + idstr + "/ ");
        sql.append("AND time>='" + startTime + "' ");
        sql.append("AND time<'" + endTime + "' ");
        sql.append("AND channelId='" + channelId + "' ");
        sql.append("GROUP BY time(10m),deviceCollectId ");
        sql.append("order by time ");
        return sql.toString();
    }

    /**
     * 1.实时负荷趋势;
     * oracle
     * 条件：能源类型为用电设备、楼宇、是总设备
     * 结果：查询出当前楼宇下用电总设备
     * influx
     * 条件：采集设备id（通过oracle结果），信号（总有功功率），时间开始、结束
     * ps: 存在俩个用电总设备
     */
    public Map<String, Double> getRealTimeLoad(DataSheetVo bean) {

        if (null == bean.getStartTime()) {
            bean.setStartTime(DateUtils.getStartTimeOfCurrentDay(new Date()));
        }
        if (null == bean.getEndTime()) {
            bean.setEndTime(DateUtils.getEndTimeOfCurrentDay(new Date()));
        }
        // 总有功功率
        String channelId = "P_TOTAL";
        // 能源类型为用电的设备
        bean.setEnergyType(1);
        // 是否是总设备
        bean.setIsTotalDevice(1);
        // 先随便给一个楼宇的id 后期从表里取
        SysBuild sysBuild = sysBuildMapper.selectOne(Wrappers.<SysBuild>lambdaQuery().
                eq(SysBuild::getIsDel, 0).last("AND ROWNUM=1"));
        bean.setBuildId(sysBuild.getId());

        List<DeviceCollect> deviceCollects = deviceCollectMapper.getDeviceByArea(bean);

        List<String> ids = deviceCollects.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());

        Map<String, Double> result = new LinkedHashMap<>();

        String sql = getRealTimeLoad(ids, channelId, bean.getStartTime(), bean.getEndTime());
        // 时序数据库解析
        QueryResult query = influxdbService.query(sql);
        //解析列表类查询
        List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);

        for (Map<String, Object> map : maps) {
            if (result.containsKey(map.get("time"))) {
                result.put((String) map.get("time"), result.get(map.get("time")) + (map.get("totalPower") == null ? 0d : (Double) map.get("totalPower")));
            } else {
                result.put((String) map.get("time"), map.get("totalPower") == null ? 0d : (Double) map.get("totalPower"));
            }

        }
        return result;
    }


    /**
     * 月度用水统计sql
     * 因为只有一个总水表，所以不用考虑分组的问题
     *
     * @return
     */
    private String getMonthsWaterSql(List<String> ids, String channelId, String startTime, String endTime) {
        String idstr = influxdbInToString(ids);
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT LAST(val)-FIRST(val) as totalPower from datasheet  ");
        sql.append("WHERE deviceCollectId=~/" + idstr + "/ ");
        sql.append("AND time>='" + startTime + "' ");
        sql.append("AND time<'" + endTime + "' ");
        sql.append("AND channelId='" + channelId + "' ");

        return sql.toString();
    }

    /**
     * 月度用水同比统计
     * oracle:
     * 条件：能源类型为用水设备、楼宇、是总设备
     * influx:
     * 条件：采集设备id（通过oracle结果）、信号（用水量）、时间开始结束
     * ps：单个用水总设备，时序库不支持按月查询 查12次吧
     * x轴数据前端搞一下
     *
     * @param bean
     * @return
     */
    public JSONObject getMonthsWater(DataSheetVo bean) {
        // 信号用水
        String channelId = "WATER_L";
        // 能源类型为用水
        bean.setEnergyType(2);
        // 是否是总设备
        bean.setIsTotalDevice(1);
        // 先随便给一个楼宇的id 后期从表里取
        SysBuild sysBuild = sysBuildMapper.selectOne(Wrappers.<SysBuild>lambdaQuery().
                eq(SysBuild::getIsDel, 0).last("AND ROWNUM=1"));
        bean.setBuildId(sysBuild.getId());

        List<DeviceCollect> deviceCollects = deviceCollectMapper.getDeviceByArea(bean);

        List<String> ids = deviceCollects.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());

        // 获取当年
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        String thisYear = simpleDateFormat.format(new Date());
        String lastYear = String.valueOf(Integer.parseInt(thisYear) - 1);
        // 获取月份
        Map<String, List<String>> thisMonths = DateUtils.getMonthsByYear(thisYear);
        Map<String, List<String>> lastMonths = DateUtils.getMonthsByYear(lastYear);

        JSONObject result = new JSONObject();
        // 实时
        List<Double> thisResult = new ArrayList<>();
        // 同比
        List<Double> lastResult = new ArrayList<>();


        for (int i = 0; i < 12; i++) {
            String sql = getMonthsWaterSql(ids, channelId, thisMonths.get("startTimes").get(i), thisMonths.get("endTimes").get(i));
            // 时序数据库解析
            QueryResult query = influxdbService.query(sql);
            //解析列表类查询
            List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
            Double sum = 0d;
            for (Map<String, Object> x : maps) {
                sum = sum + (null == x.get("totalPower") ? 0d : (Double) x.get("totalPower"));
            }
            thisResult.add(sum);

            sql = getMonthsWaterSql(ids, channelId, lastMonths.get("startTimes").get(i), lastMonths.get("endTimes").get(i));
            query = influxdbService.query(sql);
            maps = influxdbService.queryResultProcess(query);
            sum = 0d;
            for (Map<String, Object> x : maps) {
                sum = sum + (null == x.get("totalPower") ? 0d : (Double) x.get("totalPower"));
            }
            lastResult.add(sum);


        }


        result.put("thisResult", thisResult);
        result.put("lastResult", lastResult);

        return result;
    }


    // 设备类型排名sql
    private String getDeviceTypeTopSql(List<String> ids, String channelId, String startTime, String endTime, String groupStr) {
        String idstr = influxdbInToString(ids);
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT " + groupStr + " as totalPower from datasheet ");
        sql.append("WHERE deviceCollectId=~/" + idstr + "/ ");
        sql.append("AND time>='" + startTime + "' ");
        sql.append("AND time<'" + endTime + "' ");
        sql.append("AND channelId='" + channelId + "' ");
        sql.append("GROUP BY deviceCollectId ");
        return sql.toString();
    }

    // 设备类型排名
    public List<JSONObject> getDeviceTypeTop(DataSheetVo bean) {
        if (bean.getTimeType() == 2) {
            try {
                Date date = new SimpleDateFormat("yyyy-MM").parse(bean.getTimeStr());
                bean.setStartTime(DateUtils.getStartTimeOfCurrentMonth(date));
                bean.setEndTime(DateUtils.getEndTimeOfCurrentMonth(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (bean.getTimeType() == 3) {
            try {
                Date date = new SimpleDateFormat("yyyy").parse(bean.getTimeStr());
                bean.setStartTime(DateUtils.getStartTimeOfCurrentYear(date));
                bean.setEndTime(DateUtils.getEndTimeOfCurrentYear(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            if (null == bean.getStartTime()) {
                bean.setStartTime(DateUtils.getStartTimeOfCurrentDay(new Date()));
            }
            if (null == bean.getEndTime()) {
                bean.setEndTime(DateUtils.getEndTimeOfCurrentDay(new Date()));
            }
        }

        List<JSONObject> result = new ArrayList<>();

        // 1.获取所有(用电，用水。。。)采集设备 根据类型分组
        List<DeviceCollectVo> deviceCollects = deviceCollectMapper.getDeviceByType(bean);

        // 类型
//        Map<String, List<DeviceCollectVo>> groups = deviceCollects.stream().collect(Collectors.groupingBy(DeviceCollectVo::getDeviceTypeName));
        Map<String, List<DeviceCollectVo>> groups = new LinkedHashMap<>();
        if (bean.getInstanceType() == null) {
            groups = deviceCollects.stream().collect(Collectors.groupingBy(DeviceCollectVo::getSubitemName));
        } else {
            groups = deviceCollects.stream().collect(Collectors.groupingBy(DeviceCollectVo::getDeviceTypeName));
        }

        List<ConfigDeviceSubitem> subitems=configDeviceSubitemMapper.
                selectList(Wrappers.<ConfigDeviceSubitem>lambdaQuery().
                        eq(ConfigDeviceSubitem::getIsDel,0).
                        eq(ConfigDeviceSubitem::getEnergy_type,String.valueOf(bean.getEnergyType())));

        for (ConfigDeviceSubitem item:subitems) {
            if (!groups.containsKey(item.getSubitemName())){
                groups.put(item.getSubitemName(),new ArrayList<>());
            }
        }




        for (Map.Entry<String, List<DeviceCollectVo>> item : groups.entrySet()) {
            if (item.getValue().isEmpty()) {
                result.add(JSONObject.parseObject("{\"name\":\"" + item.getKey() + "\",\"value\":\"" + 0d + "\"}"));
                continue;
            }

            // 汇总采集设备id，查时序数据库 条件：in（设备id）and channelId='用电量'
            List<String> ids = item.getValue().stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
            String sql = getDeviceTypeTopSql(ids, bean.getChannelId(), bean.getStartTime(), bean.getEndTime(), bean.getGroupStr());
            // 时序数据库解析
            QueryResult query = influxdbService.query(sql);
            //解析列表类查询
            List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
            Double sum = 0d;
            for (Map<String, Object> x : maps) {
                sum = sum + (null == x.get("totalPower") ? 0d : (Double) x.get("totalPower"));
            }

            result.add(JSONObject.parseObject("{\"name\":\"" + item.getKey() + "\",\"value\":\"" + sum + "\"}"));

        }
        // 结果排序
        Collections.sort(result, Comparator.comparing(p -> p.getDouble("value")));

        Collections.reverse(result);

        return result;
    }


    /**
     * 区域设备能耗
     *
     * @return
     */
    public List<JSONObject> getAreaDeviceTop(DataSheetVo bean) {
        if (bean.getTimeType() == 2) {
            try {
                Date date = new SimpleDateFormat("yyyy-MM").parse(bean.getTimeStr());
                bean.setStartTime(DateUtils.getStartTimeOfCurrentMonth(date));
                bean.setEndTime(DateUtils.getEndTimeOfCurrentMonth(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (bean.getTimeType() == 3) {
            try {
                Date date = new SimpleDateFormat("yyyy-MM").parse(bean.getTimeStr());
                bean.setStartTime(DateUtils.getStartTimeOfCurrentMonth(date));
                bean.setEndTime(DateUtils.getEndTimeOfCurrentMonth(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            if (null == bean.getStartTime()) {
                bean.setStartTime(DateUtils.getStartTimeOfCurrentDay(new Date()));
            }
            if (null == bean.getEndTime()) {
                bean.setEndTime(DateUtils.getEndTimeOfCurrentDay(new Date()));
            }
        }


        List<JSONObject> result = new ArrayList<>();


        // 1.根据楼层，楼宇，区域获取下级所有区域
        SysBuildAreaVo areaVo = new SysBuildAreaVo();
        areaVo.setId(bean.getAreaId());
        areaVo.setBuildId(bean.getBuildId());
        areaVo.setFloorId(bean.getFloorId());
        List<SysBuildArea> areas = sysBuildAreaService.getAllList(areaVo);


        // 2.遍历区域，获取区域下所有设备，并判断是否有主设备；有:直接取总设备的id取值，没有：取剩余所有设备的和
        for (SysBuildArea item : areas) {
            bean.setAreaId(item.getId());
            // 获取所有设备
            List<DeviceCollect> deviceCollects = deviceCollectMapper.getDeviceByArea(bean);
            // 无采集设备
            if (deviceCollects.isEmpty()) {
                result.add(JSONObject.parseObject("{\"name\":\"" + item.getAreaName() + "\",\"value\":\"" + 0d + "\"}"));
                continue;
            }
            // 总设备
            List<String> deviceIds = deviceCollects.stream().filter(x -> x.getIsTotalDevice() == 1).map(y -> String.valueOf(y.getId())).collect(Collectors.toList());
            // 没有总设备
            if (deviceIds.isEmpty()) {
                deviceIds = deviceCollects.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
            }

            String sql = getDeviceTypeTopSql(deviceIds, bean.getChannelId(), bean.getStartTime(), bean.getEndTime(), bean.getGroupStr());

            // 时序数据库解析
            QueryResult query = influxdbService.query(sql);
            //解析列表类查询
            List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
            Double sum = 0d;
            for (Map<String, Object> x : maps) {
                sum = sum + (null == x.get("totalPower") ? 0d : (Double) x.get("totalPower"));
            }


            result.add(JSONObject.parseObject("{\"name\":\"" + item.getAreaName() + "\",\"value\":\"" + sum + "\"}"));

        }

        // 结果排序
        // 结果排序
        Collections.sort(result, Comparator.comparing(p -> p.getDouble("value")));

        Collections.reverse(result);

        return result;
    }

    /**
     * 区域设备能耗报表
     *
     * @return
     */
    public List<JSONObject> getAreaDeviceReport(DataSheetVo bean) {
        if (bean.getTimeType() == 2) {
            try {
                Date date = new SimpleDateFormat("yyyy-MM").parse(bean.getTimeStr());
                bean.setStartTime(DateUtils.getStartTimeOfCurrentMonth(date));
                bean.setEndTime(DateUtils.getEndTimeOfCurrentMonth(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (bean.getTimeType() == 3) {
            try {
                Date date = new SimpleDateFormat("yyyy-MM").parse(bean.getTimeStr());
                bean.setStartTime(DateUtils.getStartTimeOfCurrentMonth(date));
                bean.setEndTime(DateUtils.getEndTimeOfCurrentMonth(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            if (null == bean.getStartTime()) {
                bean.setStartTime(DateUtils.getStartTimeOfCurrentDay(new Date()));
            }
            if (null == bean.getEndTime()) {
                bean.setEndTime(DateUtils.getEndTimeOfCurrentDay(new Date()));
            }
        }


        List<JSONObject> result = new ArrayList<>();


        // 1.根据楼层，楼宇，区域获取下级所有区域
        SysBuildAreaVo areaVo = new SysBuildAreaVo();
        areaVo.setId(bean.getAreaId());
        areaVo.setBuildId(bean.getBuildId());
        areaVo.setFloorId(bean.getFloorId());
        List<SysBuildArea> areas = sysBuildAreaService.getAllList(areaVo);


        // 2.遍历区域，获取区域下所有设备，并判断是否有主设备；有:直接取总设备的id取值，没有：取剩余所有设备的和
        for (SysBuildArea item : areas) {
            bean.setAreaId(item.getId());
            // 获取所有设备
            List<DeviceCollect> deviceCollects = deviceCollectMapper.getDeviceByArea(bean);
            // 无采集设备
            if (deviceCollects.isEmpty()) {
                result.add(JSONObject.parseObject("{\"name\":\"" + item.getAreaName() + "\",\"value\":\"" + 0d + "\"}"));
                continue;
            }
            // 总设备
            List<String> deviceIds = deviceCollects.stream().filter(x -> x.getIsTotalDevice() == 1).map(y -> String.valueOf(y.getId())).collect(Collectors.toList());
            // 没有总设备
            if (deviceIds.isEmpty()) {
                deviceIds = deviceCollects.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
            }

            String sql = getDeviceTypeTopSql(deviceIds, bean.getChannelId(), bean.getStartTime(), bean.getEndTime(), bean.getGroupStr());

            // 时序数据库解析
            QueryResult query = influxdbService.query(sql);
            //解析列表类查询
            List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
            Double sum = 0d;
            for (Map<String, Object> x : maps) {
                sum = sum + (null == x.get("totalPower") ? 0d : (Double) x.get("totalPower"));
            }


            result.add(JSONObject.parseObject("{\"name\":\"" + item.getAreaName() + "\",\"value\":\"" + sum + "\"}"));

        }

        // 结果排序
        // 结果排序
        Collections.sort(result, Comparator.comparing(p -> p.getDouble("value")));

        Collections.reverse(result);

        return result;
    }

    public List<Map<String, Object>> getReportingListAll(Integer id, String type, String start, String end) {
        List<Map<String, Object>> lists =
                influxdbService.queryResultProcess
                        (influxdbService.query(ReportingListAll(id, type, start, end)));
        DeviceCollect collect = deviceCollectMapper.selectById(id);
        for (Map<String, Object> list : lists) {
            if (!StringUtils.isNull(collect)){
                list.put("code", collect.getDeviceCode());
            }
        }
        return lists;
    }


    public JSONObject getPowerMaxMOM(DataSheetVo bean) {
        JSONObject jsonObject = new JSONObject();
        Date date = new Date();
        if (bean.getTimeType() == 1) {
            bean.setStartTime(DateUtils.getStartTimeOfCurrentDay(date));
            bean.setEndTime(DateUtils.getEndTimeOfCurrentDay(date));
        } else if (bean.getTimeType() == 2) {
            bean.setStartTime(DateUtils.getStartTimeOfCurrentMonth(date));
            bean.setEndTime(DateUtils.getEndTimeOfCurrentMonth(date));
        } else {
            bean.setStartTime(DateUtils.getStartTimeOfCurrentYear(date));
            bean.setEndTime(DateUtils.getEndTimeOfCurrentYear(date));
        }

        Map<String, Double> map = getInfluxExecute(bean);

        jsonObject.put("thisValue", 0);
        jsonObject.put("thisTime", "");
        for (Map.Entry<String, Double> item : map.entrySet()) {
            jsonObject.put("thisValue", String.format("%.2f", item.getValue()));
            jsonObject.put("thisTime", item.getKey().substring(0, 19).replaceAll("T", " "));
        }


        date = DateUtils.getLastday(date);
        bean.setStartTime(DateUtils.getStartTimeOfCurrentDay(date));
        bean.setEndTime(DateUtils.getEndTimeOfCurrentDay(date));
        map = getInfluxExecute(bean);
        jsonObject.put("lastValue", 0);
        jsonObject.put("lastTime", "");
        for (Map.Entry<String, Double> item : map.entrySet()) {
            jsonObject.put("lastValue", String.format("%.2f", item.getValue()));
            jsonObject.put("lastTime", item.getKey().substring(0, 16).replaceAll("T", " "));
        }

        return jsonObject;
    }
    //设备监控--能耗趋势日月年统计

    public List<Map<String, Object>> energyTrend(Integer id) {
        LambdaQueryWrapper<DeviceCollect> wrapper = new LambdaQueryWrapper<>();
        Double toDay1 = 0.00;
        Double lastDay1 = 0.00;
        Double toWeek1 = 0.00;
        Double lastWeek1 = 0.00;
        Double toMonth1 = 0.00;
        Double lastMonth1 = 0.00;
        Double toYear1 = 0.00;
        Double lastYear1 = 0.00;
        wrapper.eq(StringUtils.isNotNull(id), DeviceCollect::getId, id);
        List<DeviceCollect> collect = this.deviceCollectMapper.selectList(wrapper);
        for (DeviceCollect deviceCollect : collect) {
            QueryResult toDay = influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(new Date(),
                    JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(new Date(),
                    JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            QueryResult lastDay =
                    influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-1,
                                    new Date()),
                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-1, new Date()),
                            JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            QueryResult toWeek =
                    influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(new Date(),
                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstWeekDay(new Date()),
                            JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            QueryResult lastWeek =
                    influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-7,
                                    new Date()),
                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-7,
                                    JsdcDateUtil.getFirstWeekDay(new Date())),
                            JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            QueryResult toMonth =
                    influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(new Date(),
                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(new Date(),
                            Calendar.DAY_OF_MONTH), JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            QueryResult lastMonth =
                    influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-30,
                                    new Date()), JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
                            JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(JsdcDateUtil.lastDayTime(-30, new Date()),
                                    Calendar.DAY_OF_MONTH), JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            QueryResult toYear = influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(new Date(),
                    JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(new Date(),
                    Calendar.DAY_OF_YEAR), JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            QueryResult lastYear =
                    influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-365,
                                    new Date()), JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
                            JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(JsdcDateUtil.lastDayTime(-365, new Date()),
                                    Calendar.DAY_OF_YEAR), JsdcDateUtil.FULL_SPLIT_PATTERN), deviceCollect.getId() + ""));
            List<Map<String, Object>> toDayList = influxdbService.queryResultProcess(toDay);
            List<Map<String, Object>> lastDayList = influxdbService.queryResultProcess(lastDay);
            List<Map<String, Object>> toWeekList = influxdbService.queryResultProcess(toWeek);
            List<Map<String, Object>> lastWeekList = influxdbService.queryResultProcess(lastWeek);
            List<Map<String, Object>> toMonthList = influxdbService.queryResultProcess(toMonth);
            List<Map<String, Object>> lastMonthList = influxdbService.queryResultProcess(lastMonth);
            List<Map<String, Object>> toYearList = influxdbService.queryResultProcess(toYear);
            List<Map<String, Object>> lastYearList = influxdbService.queryResultProcess(lastYear);
            toDay1 += Double.parseDouble(this.spreadResultProcess(toDayList));
            lastDay1 += Double.parseDouble(this.spreadResultProcess(lastDayList));
            toWeek1 += Double.parseDouble(this.spreadResultProcess(toWeekList));
            lastWeek1 += Double.parseDouble(this.spreadResultProcess(lastWeekList));
            toMonth1 += Double.parseDouble(this.spreadResultProcess(toMonthList));
            lastMonth1 += Double.parseDouble(this.spreadResultProcess(lastMonthList));
            toYear1 += Double.parseDouble(this.spreadResultProcess(toYearList));
            lastYear1 += Double.parseDouble(this.spreadResultProcess(lastYearList));
        }

        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("toDay", new BigDecimal(toDay1).setScale(2, ROUND_HALF_UP).toString());
        map.put("lastDay", new BigDecimal(lastDay1).setScale(2, ROUND_HALF_UP).toString());
        map.put("toWeek", new BigDecimal(toWeek1).setScale(2, ROUND_HALF_UP).toString());
        map.put("lastWeek", new BigDecimal(lastWeek1).setScale(2, ROUND_HALF_UP).toString());
        map.put("toMonth", new BigDecimal(toMonth1).setScale(2, ROUND_HALF_UP).toString());
        map.put("lastMonth", new BigDecimal(lastMonth1).setScale(2, ROUND_HALF_UP).toString());
        map.put("toYear", new BigDecimal(toYear1).setScale(2, ROUND_HALF_UP).toString());
        map.put("lastYear", new BigDecimal(lastYear1).setScale(2, ROUND_HALF_UP).toString());
        list.add(map);
        return list;
    }

    public Map energyTrendAVG(Integer id) {
        List dayAvgList = influxdbService.queryResultProcess(influxdbService.query(energyTrendAVGSql(
                "1", id)));
        List monthAvgList = influxdbService.queryResultProcess(influxdbService.query(energyTrendAVGSql(
                "2", id)));
        List yearAvgList = influxdbService.queryResultProcess(influxdbService.query(energyTrendAVGSql(
                "3", id)));
        Double dayAvg = spreadResultProcess1(dayAvgList);
        Double monthAvg = spreadResultProcess1(monthAvgList);
        Double yearAvg = spreadResultProcess1(yearAvgList);
        Map<String, Object> map = new HashMap<>();
        if (dayAvgList.size() > 0) {
            Double avgDay = dayAvg / dayAvgList.size();
            map.put("dayAvg", new BigDecimal(avgDay).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        } else {
            map.put("dayAvg", new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        }
        if (monthAvgList.size() > 0) {
            Double avgMonth = monthAvg / monthAvgList.size();
            map.put("monthAvg", new BigDecimal(avgMonth).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        } else {
            map.put("monthAvg", new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        }
        if (yearAvgList.size() > 0) {
            Double avgYear = yearAvg / yearAvgList.size();
            map.put("yearAvg", new BigDecimal(avgYear).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        } else {
            map.put("yearAvg", new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        }
        return map;
    }

    //设备监控信号上报
    public String energyTrendAVGSql(String type, Integer id) {
        String sql = "SELECT  spread(val)  FROM datasheet WHERE channelId='ENERGY_TOTAL' AND deviceCollectId='" + id +
                "' ";
        if ("1".equals(type)) {
            sql += " GROUP BY time(1d)  fill(0)";
        } else if ("2".equals(type)) {
            sql += " GROUP BY time(30d)  fill(0)";
        } else {
            sql += " GROUP BY time(365d)  fill(0)";
        }
        return sql;
    }


    public Map cumulativeEmission(Integer id) {
        String energy = spreadResultProcess(influxdbService.queryResultProcess(influxdbService.query(cumulativeEmissionSql(id))));
        String co2 = new BigDecimal(Double.parseDouble(energy) * 0.785 / 1000).setScale(2, ROUND_HALF_UP).toString();
        String carbon = new BigDecimal(Double.parseDouble(energy) * 1.229).setScale(2, ROUND_HALF_UP).toString();
        Map<String, Object> map = new HashMap<>();
        map.put("energy", energy);
        map.put("co2", co2);
        map.put("carbon", carbon);
        return map;
    }

    //设备监控信号上报
    public String cumulativeEmissionSql(Integer id) {
        String sql = "SELECT  spread(val)  FROM datasheet WHERE channelId='ENERGY_TOTAL' AND deviceCollectId='" + id + "' ";
        return sql;
    }


    /**
     * 设备监控--用电量报表
     *
     * @param bean
     * @return
     */
    public Page getElectricityTable(DataSheetVo bean) {
        Page page = new Page<>();
        DeviceCollect collect = deviceCollectMapper.selectById(bean.getDeviceId());
        Date date = bean.getTime();
        if (bean.getTimeType() == 1) {// 日
            // 今日
            bean.setStartTime(DateUtils.getStartTimeOfCurrentDay(date));
            bean.setEndTime(DateUtils.getEndTimeOfCurrentDay(date));
            List<Map<String, Object>> map = getInfluxExecute2(bean);
            List<Map<String, Object>> map1 = getInfluxExecute3(bean);
            for (Map<String, Object> objectMap : map) {
                objectMap.put("code", collect.getDeviceCode());
                objectMap.put("type", "ENERGY_TOTAL");
                objectMap.put("val1", "0");
                objectMap.put("val2", "0");
                objectMap.put("val3", "0");
                objectMap.put("val4", "0");
                objectMap.put("time", objectMap.get("time").toString().substring(11, 19));
                objectMap.put("totalPower", String.format("%.2f", objectMap.get("totalPower")));
            }
            page.setRecords(map);
            page.setTotal(map1.size());
            return page;

        } else if (bean.getTimeType() == 2) {//本月
            bean.setStartTime(DateUtils.getStartTimeOfCurrentMonth(date));
            bean.setEndTime(DateUtils.getEndTimeOfCurrentMonth(date));
            List<Map<String, Object>> map = getInfluxExecute2(bean);
            List<Map<String, Object>> map1 = getInfluxExecute3(bean);
            for (Map<String, Object> objectMap : map) {
                objectMap.put("code", collect.getDeviceCode());
                objectMap.put("type", "ENERGY_TOTAL");
                objectMap.put("val1", "0");
                objectMap.put("val2", "0");
                objectMap.put("val3", "0");
                objectMap.put("val4", "0");
                objectMap.put("time", objectMap.get("time").toString().substring(0, 10));
                objectMap.put("totalPower", String.format("%.2f", objectMap.get("totalPower")));
            }
            page.setRecords(map);
            page.setTotal(map1.size());
            return page;
        } else { // 本年
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
            String thisYear = simpleDateFormat.format(new Date());
            Map<String, List<String>> thisMonths = DateUtils.getMonthsByYear(thisYear);
            List<Map<String, Object>> list = new ArrayList<>();
            if (bean.getPageNo() == 1) {
                for (int i = 0; i < 10; i++) {
                    Map<String, Object> objectMap = new HashMap<>();
                    bean.setStartTime(thisMonths.get("startTimes").get(i));
                    bean.setEndTime(thisMonths.get("endTimes").get(i));
                    List<Map<String, Object>> doubleMap = getInfluxExecute2(bean);
                    objectMap.put("code", collect.getDeviceCode());
                    objectMap.put("type", "ENERGY_TOTAL");
                    objectMap.put("val1", "0");
                    objectMap.put("val2", "0");
                    objectMap.put("val3", "0");
                    objectMap.put("val4", "0");
                    double sum = 0d;
                    for (Map<String, Object> map : doubleMap) {
                        sum = sum + Double.parseDouble(map.get("totalPower") + "");
                    }
                    objectMap.put("totalPower", String.format("%.2f", sum));
                    objectMap.put("time", thisMonths.get("startTimes").get(i).substring(0, 7));
                    list.add(objectMap);
                }
            } else {
                for (int i = 10; i < 12; i++) {
                    Map<String, Object> objectMap = new HashMap<>();
                    bean.setStartTime(thisMonths.get("startTimes").get(i));
                    bean.setEndTime(thisMonths.get("endTimes").get(i));
                    List<Map<String, Object>> doubleMap = getInfluxExecute2(bean);
                    objectMap.put("code", collect.getDeviceCode());
                    objectMap.put("type", "ENERGY_TOTAL");
                    objectMap.put("val1", "0");
                    objectMap.put("val2", "0");
                    objectMap.put("val3", "0");
                    objectMap.put("val4", "0");
                    double sum = 0d;
                    for (Map<String, Object> map : doubleMap) {
                        sum = sum + Double.parseDouble(map.get("totalPower") + "");
                    }
                    objectMap.put("totalPower", String.format("%.2f", sum));
                    objectMap.put("time", thisMonths.get("startTimes").get(i).substring(0, 7));
                    list.add(objectMap);
                }
            }
            page.setRecords(list);
            page.setTotal(12);
            return page;
        }
    }

    public Map<String, Double> getInfluxExecute1(DataSheetVo bean) {
        // 获取采集设备
        DeviceCollect deviceCollects = deviceCollectMapper.selectById(bean.getDeviceId());

        List<String> ids = new ArrayList<>();
        ids.add(deviceCollects.getId() + "");
        Map<String, Double> result = new LinkedHashMap<>();
        if (ids.isEmpty()) {
            return result;
        }
        String sql = getInfluxSql(bean.getInfluxVal(), bean.getStartTime(), bean.getEndTime(), bean.getGroupStr(), bean.getChannelId(), ids);

        // 时序数据库解析
        QueryResult query = influxdbService.query(sql);

        //解析列表类查询
        List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);


        for (Map<String, Object> map : maps) {
            if (result.containsKey(map.get("time"))) {
                result.put((String) map.get("time"), result.get(map.get("time")) + (map.get("totalPower") == null ? 0d : (Double) map.get("totalPower")));
            } else {
                result.put((String) map.get("time"), map.get("totalPower") == null ? 0d : (Double) map.get("totalPower"));
            }

        }

        return result;

    }

    public JSONObject getChannelYOY1(DataSheetVo bean) {

        JSONObject result = new JSONObject();

        Date date = bean.getTime();
        if (bean.getTimeType() == 1) {// 日
            // 今日
            bean.setStartTime(DateUtils.getStartTimeOfCurrentDay(date));
            bean.setEndTime(DateUtils.getEndTimeOfCurrentDay(date));
            Map<String, Double> map = getInfluxExecute1(bean);
            for (Map.Entry<String, Double> item : map.entrySet()) {
                bean.getThisXData().put(item.getKey().substring(11, 16), String.format("%.2f", item.getValue()));
            }
            result.put("thisTime", bean.getThisXData().values());
            result.put("xData", bean.getThisXData().keySet());
        } else if (bean.getTimeType() == 2) {//本月
            bean.setStartTime(DateUtils.getStartTimeOfCurrentMonth(date));
            bean.setEndTime(DateUtils.getEndTimeOfCurrentMonth(date));
            Map<String, Double> map = getInfluxExecute1(bean);

            for (Map.Entry<String, Double> item : map.entrySet()) {
                bean.getThisXData().put(item.getKey().substring(0, 10), String.format("%.2f", item.getValue()));
            }

            result.put("thisTime", bean.getThisXData().values());
            result.put("xData", bean.getThisXData().keySet());
        } else if (bean.getTimeType() == 4) {// 本周
            bean.setStartTime(DateUtils.getStartTimeOfCurrentWeek(date));
            bean.setEndTime(DateUtils.getEndTimeOfCurrentWeek(date));
            bean.setGroupStr("time(1d)");
            bean.setInfluxVal("LAST(val)-FIRST(val)");
            Map<String, Double> map = getInfluxExecute1(bean);
            List<String> thisResult = new ArrayList<>();
            for (Map.Entry<String, Double> item : map.entrySet()) {
                thisResult.add(String.format("%.2f", item.getValue()));
            }
            result.put("thisTime", thisResult);
        } else { // 本年
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
            String thisYear = simpleDateFormat.format(bean.getTime());
            Map<String, List<String>> thisMonths = DateUtils.getMonthsByYear(thisYear);
            List<String> thisResult = new ArrayList<>();
            List<String> xData = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                xData.add(thisMonths.get("startTimes").get(i).substring(0, 7));
                bean.setStartTime(thisMonths.get("startTimes").get(i));
                bean.setEndTime(thisMonths.get("endTimes").get(i));
                Map<String, Double> doubleMap = getInfluxExecute1(bean);
                double sum = 0d;
                for (Map.Entry<String, Double> item : doubleMap.entrySet()) {
                    sum = sum + item.getValue();
                }
                thisResult.add(String.format("%.2f", sum));
            }
            result.put("thisTime", thisResult);
            result.put("xData", xData);
        }

        return result;
    }


    public List getInfluxExecute2(DataSheetVo bean) {
        // 获取采集设备
        DeviceCollect deviceCollects = deviceCollectMapper.selectById(bean.getDeviceId());

        List<String> ids = new ArrayList<>();
        ids.add(deviceCollects.getId() + "");

        String sql = getElectricityTableSql(bean.getInfluxVal(), bean.getStartTime(), bean.getEndTime(),
                bean.getGroupStr(), bean.getChannelId(), ids, bean.getPageNo(), bean.getPageSize());

        // 时序数据库解析
        QueryResult query = influxdbService.query(sql);

        //解析列表类查询
        List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);


        return maps;

    }

    public List getInfluxExecute3(DataSheetVo bean) {
        // 获取采集设备
        DeviceCollect deviceCollects = deviceCollectMapper.selectById(bean.getDeviceId());

        List<String> ids = new ArrayList<>();
        ids.add(deviceCollects.getId() + "");

        String sql = getInfluxSql(bean.getInfluxVal(), bean.getStartTime(), bean.getEndTime(),
                bean.getGroupStr(), bean.getChannelId(), ids);

        // 时序数据库解析
        QueryResult query = influxdbService.query(sql);

        //解析列表类查询
        List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);


        return maps;

    }

    /**
     * 导出
     */
    public void export(List<Map<String, Object>> vo, HttpServletResponse response) {
        // 查询数据

        ExcelWriter writer = ExcelUtil.getWriter();

        writer.addHeaderAlias("time", "时间");
        writer.addHeaderAlias("code", "设备编号");
        writer.addHeaderAlias("type", "能耗类别");
        writer.addHeaderAlias("totalPower", "用电量-总（kWh）");
        writer.addHeaderAlias("val1", "用电量-尖（kWh）");
        writer.addHeaderAlias("val2", "用电量-峰（kWh）");
        writer.addHeaderAlias("val3", "用电量-平（kWh）");
        writer.addHeaderAlias("val4", "用电量-谷（kWh）");
        writer.setOnlyAlias(true);

        writer.write(vo, true);
        OutputStream outputStream = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/vnd.ms-excel");
        try {
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("能耗.xls", "UTF-8"));
            outputStream = response.getOutputStream();
            writer.flush(outputStream, true);
//            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 设备监控--用电量报表导出
     *
     * @param bean
     * @return
     */
    public void TableExport(DataSheetVo bean, HttpServletResponse response) {

        DeviceCollect collect = deviceCollectMapper.selectById(bean.getDeviceId());
        Date date = bean.getTime();
        if (bean.getTimeType() == 1) {// 日
            // 今日
            bean.setStartTime(DateUtils.getStartTimeOfCurrentDay(date));
            bean.setEndTime(DateUtils.getEndTimeOfCurrentDay(date));
            List<Map<String, Object>> map = getInfluxExecute2(bean);
            for (Map<String, Object> objectMap : map) {
                objectMap.put("code", collect.getDeviceCode());
                objectMap.put("type", "ENERGY_TOTAL");
                objectMap.put("val1", "0");
                objectMap.put("val2", "0");
                objectMap.put("val3", "0");
                objectMap.put("val4", "0");
            }
            export(map, response);

        } else if (bean.getTimeType() == 2) {//本月
            bean.setStartTime(DateUtils.getStartTimeOfCurrentMonth(date));
            bean.setEndTime(DateUtils.getEndTimeOfCurrentMonth(date));
            List<Map<String, Object>> map = getInfluxExecute2(bean);
            for (Map<String, Object> objectMap : map) {
                objectMap.put("code", collect.getDeviceCode());
                objectMap.put("type", "ENERGY_TOTAL");
                objectMap.put("val1", "0");
                objectMap.put("val2", "0");
                objectMap.put("val3", "0");
                objectMap.put("val4", "0");
            }
            export(map, response);
        } else { // 本年
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
            String thisYear = simpleDateFormat.format(new Date());
            Map<String, List<String>> thisMonths = DateUtils.getMonthsByYear(thisYear);
            List<Map<String, Object>> list = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Map<String, Object> objectMap = new HashMap<>();
                bean.setStartTime(thisMonths.get("startTimes").get(i));
                bean.setEndTime(thisMonths.get("endTimes").get(i));
                List<Map<String, Object>> doubleMap = getInfluxExecute2(bean);
                objectMap.put("code", collect.getDeviceCode());
                objectMap.put("type", "ENERGY_TOTAL");
                objectMap.put("val1", "0");
                objectMap.put("val2", "0");
                objectMap.put("val3", "0");
                objectMap.put("val4", "0");
                double sum = 0d;
                for (Map<String, Object> map : doubleMap) {
                    for (Map.Entry<String, Object> item : map.entrySet()) {
                        sum = sum + Double.parseDouble(item.getValue() + "");
                    }
                }
                objectMap.put(thisMonths.get("startTimes").get(i).substring(0, 7), String.format("%.2f", sum));
                list.add(objectMap);
            }
            export(list, response);
        }
    }

    public String getElectricityTableSql(String val, String startTime, String endTime, String groupStr,
                                         String channelId, List<String> ids, Integer index, Integer size) {
        String idstr = influxdbInToString(ids);
        StringBuilder sql = new StringBuilder();
        sql.append("select " + val + " as totalPower from datasheet ");
        sql.append("WHERE deviceCollectId=~/" + idstr + "/ ");
        sql.append("AND time>'" + startTime + "' ");
        sql.append("AND time<'" + endTime + "' ");
        sql.append("AND channelId='" + channelId + "' ");


        if (null != groupStr) {
            sql.append(" GROUP BY " + groupStr + ",deviceCollectId ");
        } else {
            sql.append(" GROUP BY deviceCollectId ");
        }

        if (val.indexOf("DIFFERENCE") >= 0) {
            sql.append("fill(previous) ");
        } else {
            sql.append("fill(0) ");
        }

        sql.append("ORDER BY time ");

        if (StringUtils.isNotNull(index) && StringUtils.isNotNull(size)) {
            sql.append(" limit " + size + " offset " + (index - 1) * size);
        }

        return sql.toString();
    }


    public JSONObject getDeviceFloorTop(DataSheetVo bean) {
        JSONObject jsonObject = new JSONObject();
        if (bean.getTimeType() == 2) {
            try {
                Date date = new SimpleDateFormat("yyyy-MM").parse(bean.getTimeStr());
                bean.setStartTime(DateUtils.getStartTimeOfCurrentMonth(date));
                bean.setEndTime(DateUtils.getEndTimeOfCurrentMonth(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (bean.getTimeType() == 3) {
            try {
                Date date = new SimpleDateFormat("yyyy").parse(bean.getTimeStr());
                bean.setStartTime(DateUtils.getStartTimeOfCurrentYear(date));
                bean.setEndTime(DateUtils.getEndTimeOfCurrentYear(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            if (null == bean.getStartTime()) {
                bean.setStartTime(DateUtils.getStartTimeOfCurrentDay(new Date()));
            }
            if (null == bean.getEndTime()) {
                bean.setEndTime(DateUtils.getEndTimeOfCurrentDay(new Date()));
            }
        }

        List<JSONObject> result = new ArrayList<>();

        List<JSONObject> resultRadio = new ArrayList<>();

        List<SysBuildFloor> buildFloors = sysBuildFloorService.list(Wrappers.<SysBuildFloor>lambdaQuery().
                eq(SysBuildFloor::getIsDel, 0).orderByDesc(SysBuildFloor::getSort));

        // 1.获取所有(用电，用水。。。)采集设备 根据类型分组
        List<DeviceCollectVo> deviceCollects = deviceCollectMapper.getDeviceByFloor(bean);


        Map<Integer, List<DeviceCollectVo>> groups = deviceCollects.stream().filter(x -> x.getFloorName() != null).collect(Collectors.groupingBy(DeviceCollectVo::getLogicalFloorId));

        for (SysBuildFloor item : buildFloors) {

            if (null == groups.get(item.getId()) || groups.get(item.getId()).isEmpty()) {
                result.add(JSONObject.parseObject("{\"name\":\"" + item.getFloorName() + "\",\"value\":\"" + 0d + "\"}"));
                resultRadio.add(JSONObject.parseObject("{\"name\":\"" + item.getFloorName() + "\",\"value\":\"" + 0d + "\"}"));
                continue;
            }

            // 汇总采集设备id，查时序数据库 条件：in（设备id）and channelId='用电量'
            List<String> ids = groups.get(item.getId()).stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
            String sql = getDeviceTypeTopSql(ids, bean.getChannelId(), bean.getStartTime(), bean.getEndTime(), bean.getGroupStr());
            // 时序数据库解析
            QueryResult query = influxdbService.query(sql);
            //解析列表类查询
            List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
            Double sum = 0d;
            for (Map<String, Object> x : maps) {
                sum = sum + (null == x.get("totalPower") ? 0d : (Double) x.get("totalPower"));
            }

            result.add(JSONObject.parseObject("{\"name\":\"" + item.getFloorName() + "\",\"value\":\"" + sum + "\"}"));
            resultRadio.add(JSONObject.parseObject("{\"name\":\"" + item.getFloorName() + "\",\"value\":\"" + sum + "\"}"));


        }


        jsonObject.put("floorNames", resultRadio);
        // 结果排序
        Collections.sort(result, Comparator.comparing(p -> p.getDouble("value")));

        Collections.reverse(result);


        jsonObject.put("data", result);


        return jsonObject;
    }


    // 设备类型排名sql
    public String getDeviceCollectTopSql(String id, String channelId, String startTime, String endTime, String groupStr) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT " + groupStr + " as totalPower from datasheet ");
        sql.append("WHERE deviceCollectId=~/" + id + "/ ");
        sql.append("AND time>='" + startTime + "' ");
        sql.append("AND time<'" + endTime + "' ");
        sql.append("AND channelId='" + channelId + "' ");
        sql.append("GROUP BY deviceCollectId ");
        return sql.toString();
    }

    public String getAreaLoadSql(String id, String channelId, String startTime, String endTime, String groupStr) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT " + groupStr + " as totalPower from datasheet ");
        sql.append("WHERE areaId=~/" + id + "/ ");
        sql.append("AND time>='" + startTime + "' ");
        sql.append("AND time<'" + endTime + "' ");
        sql.append("AND channelId='" + channelId + "' ");
        sql.append("GROUP BY deviceCollectId ");
        return sql.toString();
    }


    public String getLoadSql(String startTime, String endTime, String deviceCollectId) {
        String sql = "select LAST(val) as totalPower from datasheet where channelId='P_Total'  and time>='" + startTime + "' and time<='" + endTime + "' AND deviceCollectId='" + deviceCollectId + "' group  by time(1h)";
        return sql;
    }

    public JSONObject getSubitemEnergy(DeviceQueryVo bean) {
        JSONObject jsonObject = new JSONObject();


        // 获取设备
        List<DeviceCollectVo> deviceCollects = deviceCollectMapper.getSubitemEnergy(bean);


        List<String> ids = deviceCollects.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());




        String channelId = "";
        String influxVal = "";
        switch (bean.getEnergyType()) {
            case "1":
                channelId = "ENERGY_TOTAL";
                influxVal = "last(val)-first(val)";
                break;
            case "2":
                channelId = "WATER_L";
                influxVal = "MAX(val)-MIN(val)";
                break;
            default:
                channelId = "GAS_METERING";
                influxVal = "MAX(val)-MIN(val)";
                break;

        }





            //虚拟设备查oracle数据
            bean.setDeviceCode( deviceCollects.get(0).getDeviceCode());

//            if(!deviceCollects.get(0).getName().contains("空调外机") && !"空调电表（单设备）".equals(deviceCollects.get(0).getDeviceTypeName())){
                //真实设备
                String sql = getInfluxSql(influxVal, bean.getTimeStart(), bean.getTimeEnd(), null, channelId, ids);


                // 时序数据库解析
                QueryResult query = influxdbService.query(sql);

                //解析列表类查询
                List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);

                Double sum = 0d;
                for (Map<String, Object> map : maps) {
                    for (Map<String, Object> item : maps) {
                        sum = sum + (null == item.get("totalPower") ? 0d : (Double) item.get("totalPower"));
                    }
                }

                jsonObject.put("data",String.format("%.2f",sum));

//            }
//            else {
//                if("空调电表（单设备）".equals(deviceCollects.get(0).getDeviceTypeName())){
//                    // 找到第一个"-"的位置
//                    int firstDashIndex = bean.getDeviceCode().indexOf("-");
//
//
//                    // 从第一个"-"之后开始查找第二个"-"的位置
//                    int secondDashIndex = bean.getDeviceCode().indexOf("-", firstDashIndex + 1);
//
//
//                    // 提取第二个"-"之后的所有内容
//                    String result = bean.getDeviceCode().substring(secondDashIndex + 1);
//                    bean.setDeviceCode(result);
//                }
//
//                AcDailyelectricity acDailyelectricity =  acDailyelectricityMapper.sb(bean) ;
//                //虚拟设备
//                jsonObject.put("data", acDailyelectricity.getTotalElePower() );
//            }









        String sql2 = getInfluxSql(influxVal, bean.getTimeStart(), bean.getTimeEnd(), "time(1h)", channelId, ids);
        Map<String, Double> result = new LinkedHashMap<>();

        // 时序数据库解析
        QueryResult query2 = influxdbService.query(sql2);

        //解析列表类查询
        List<Map<String, Object>> maps2 = influxdbService.queryResultProcess(query2);

            for (Map<String, Object> map : maps2) {
                if (result.containsKey(map.get("time"))) {
                    result.put((String) map.get("time"), result.get(map.get("time")) + (map.get("totalPower") == null ? 0d : (Double) map.get("totalPower")));
                } else {
                    result.put((String) map.get("time"), map.get("totalPower") == null ? 0d : (Double) map.get("totalPower"));
                }
            }

            LinkedHashMap<String, String> thisXData = DateUtils.getTimeHours(bean.getTimeStart(), bean.getTimeEnd());


            for (Map.Entry<String, Double> item : result.entrySet()) {
                thisXData.put(item.getKey().substring(0, 19).replaceAll("T", " "), String.format("%.2f", item.getValue()));
            }




            jsonObject.put("xData", thisXData.keySet());


            jsonObject.put("yData", thisXData.values());



        return jsonObject;

    }

    /**
     * 空气质量
     * 趋势图 0-24点
     *
     * @param startTime
     * @param endTime
     * @param deviceCollectIds
     * @param channelId
     * @return
     */
    public String getAqdSql(String startTime, String endTime, List<Integer> deviceCollectIds, String channelId) {
        String sql = "select LAST(val) as totalPower from datasheet where channelId='" + channelId + "'  and time>='" + startTime + "' and time<='" + endTime  + "'" ;
        for (int i = 0; i < deviceCollectIds.size(); i++) {
            if (i == 0) {//~/^admin$|^username$/
                sql += " and deviceCollectId=~/^" + deviceCollectIds.get(0) + "$";
            } else if (i > 0 && i < (deviceCollectIds.size() - 1)) {
                sql += "|^" + deviceCollectIds.get(i) + "$";
            }
        }
        sql += "/ group  by time(1h)";
        return sql;
    }

    public Map sankiDiagram(String startTime, String endTime) {
        Map<String, Object> result = new HashMap<>();
        //能耗排名-楼层
        List<Map<String, String>> resultFloorsTop = new ArrayList<>();
        //能耗排名-分项
        List<Map<String, String>> resultSubitemTop = new ArrayList<>();
        List<Map<String, String>> resultKey = new ArrayList<>();
        List<Map<String, String>> resultKeyWater = new ArrayList<>();
        //水电
        Map<String, Map<String, String>> totalKey = new HashMap<>();
        Map<String, Map<String, String>> totalKeyWater = new HashMap<>();
        //分项
        Map<Integer, String> subitemNameKey = new HashMap<>();
        Map<Integer, String> subitemNameKeyWater = new HashMap<>();
        //楼层
        Map<Integer, String> floorNameKey = new LinkedHashMap<>();
        Map<Integer, String> floorNameKeyWater = new LinkedHashMap<>();

        List<Map<String, String>> resultDataD = new ArrayList<>();
        List<Map<String, String>> resultDataS = new ArrayList<>();
        Date date = new Date();
//        String startTime = DateUtil.year(date) + "-" + "01" + "-" + "01" + " 00:00:00";
//        String endTime = DateUtil.year(date) + "-" + "12" + "-" + "31" + " 23:59:59";
        //获取所有分项用电
        List<ConfigDeviceSubitem> configDeviceSubitems = configDeviceSubitemMapper.selectList(new LambdaQueryWrapper<ConfigDeviceSubitem>()
                .eq(ConfigDeviceSubitem::getIsDel, 0));
        for (ConfigDeviceSubitem a : configDeviceSubitems) {
            DataSheetVo bean = new DataSheetVo();
            bean.setEnergyType(1);
            bean.setSubitem(a.getId());
            bean.setIsTotalDevice(1);//查询是总设备的数据
            List<DeviceCollect> collects = deviceCollectMapper.getDeviceByArea(bean);//获取设备列表
            if (collects.isEmpty()) {// 无采集设备
//                map.put(a.getSubitemName(), 0d);
//                continue;
                bean.setIsTotalDevice(0);//查询不是总设备的数据
                collects= deviceCollectMapper.getDeviceByArea(bean);
                if (collects.isEmpty()){
//                    map.put(a.getSubitemName(), 0d);
                    continue;
                }
            }
        }
        Map<Integer, ConfigDeviceSubitem> configDeviceSubitemMap = configDeviceSubitems.stream().collect(Collectors.toMap(ConfigDeviceSubitem::getId, Function.identity()));
        //楼层
        List<SysBuildFloor> sysBuildFloors = sysBuildFloorService.list(Wrappers.<SysBuildFloor>lambdaQuery().eq(SysBuildFloor::getIsDel, G.ISDEL_NO).orderByAsc(SysBuildFloor::getSort));
        Map<Integer, SysBuildFloor> sysBuildFloorMap = sysBuildFloors.stream().collect(Collectors.toMap(SysBuildFloor::getId, Function.identity()));
        //采集设备
        List<DeviceCollect> deviceCollects = deviceCollectService.list(Wrappers.<DeviceCollect>lambdaQuery()
                .eq(DeviceCollect::getIsTotalDevice,1)//是否总设备 0否 1是
                .eq(DeviceCollect::getIsDel, G.ISDEL_NO));
        Map<Integer, DeviceCollect> deviceCollectMap = deviceCollects.stream().collect(Collectors.toMap(DeviceCollect::getId, Function.identity()));

        //用电
        String sql = "select last(val)-first(val) as totalPower from datasheet where channelId='" + G.CHANNELID_ELECTRICITY + "'  and time>='" + startTime + "' and time<='" + endTime + "' group  by deviceCollectId";
        //时序数据库解析
        QueryResult query = influxdbService.query(sql);
        //解析查询结果
        Map<Integer, Map<String, String>> deviceMap = new HashMap<>();
        for (QueryResult.Result queryResult : query.getResults()) {
            for (QueryResult.Series series : queryResult.getSeries()) {
                Map<String, String> tags = series.getTags();
                String deviceCollectId = tags.get("deviceCollectId");
                if (null != deviceCollectId && deviceCollectMap.get(Integer.valueOf(deviceCollectId)) != null) {
                    DeviceCollect deviceCollect = deviceCollectMap.get(Integer.valueOf(deviceCollectId));
                    Map<String, String> map = new HashMap<>();
                    map.put("subitem", String.valueOf(deviceCollect.getSubitem()));
                    map.put("floorId", String.valueOf(deviceCollect.getFloorId()));
                    map.put("value", String.valueOf(series.getValues().get(0).get(1)));
                    deviceMap.put(deviceCollect.getId(), map);
                }
            }
        }

        //用水
        sql = "select last(val)-first(val) as totalPower from datasheet where channelId='" + G.CHANNELID_WATER + "'  and time>='" + startTime + "' and time<='" + endTime + "' group  by deviceCollectId";
        //时序数据库解析
        query = influxdbService.query(sql);
        for (QueryResult.Result queryResult : query.getResults()) {
            for (QueryResult.Series series : queryResult.getSeries()) {
                Map<String, String> tags = series.getTags();
                String deviceCollectId = tags.get("deviceCollectId");
                if (null != deviceCollectId && deviceCollectMap.get(Integer.valueOf(deviceCollectId)) != null) {
                    DeviceCollect deviceCollect = deviceCollectMap.get(Integer.valueOf(deviceCollectId));
                    Map<String, String> map = new HashMap<>();
                    map.put("subitem", String.valueOf(deviceCollect.getSubitem()));
                    map.put("floorId", String.valueOf(deviceCollect.getFloorId()));
                    map.put("value", String.valueOf(series.getValues().get(0).get(1)));
                    deviceMap.put(deviceCollect.getId(), map);
                }
            }
        }

        //水电-楼层
        Map<String, String> energyTypeFloor = new HashMap<>();
        //分项-楼层
        Map<String, String> subitemFloorD = new HashMap<>();
        Map<String, String> subitemFloorS = new HashMap<>();
        //汇总数据
        for (Map.Entry<Integer, Map<String, String>> deviceCollectEntry : deviceMap.entrySet()) {
            Map<String, String> deviceCollect = deviceCollectEntry.getValue();
            SysBuildFloor sysBuildFloor = sysBuildFloorMap.get(Integer.valueOf(deviceCollect.get("floorId")));
            ConfigDeviceSubitem subitem = configDeviceSubitemMap.get(Integer.valueOf(deviceCollect.get("subitem")));
            if (null != sysBuildFloor && null != subitem) {
                //水电-楼层
                if (StringUtils.isNotEmpty(subitem.getEnergy_type())) {
                    String key = subitem.getEnergy_type() + "-" + sysBuildFloor.getId();
                    if ("1".equals(subitem.getEnergy_type())) {
                        Map<String, String> map = new HashMap<>();
                        map.put("name", "电能（总值）");
                        map.put("depth", "0");
                        totalKey.put(subitem.getEnergy_type(), map);
                        if (StringUtils.isNotEmpty(energyTypeFloor.get(key))) {
                            BigDecimal a = new BigDecimal(deviceCollect.get("value"));
                            BigDecimal b = new BigDecimal(energyTypeFloor.get(key));
                            energyTypeFloor.put(key, String.valueOf(a.add(b).setScale(2, RoundingMode.HALF_UP)));
                        } else {
                            energyTypeFloor.put(key, String.valueOf(new BigDecimal(deviceCollect.get("value")).setScale(2, RoundingMode.HALF_UP)));
                        }
                        //分项-楼层
                        String key2 = subitem.getId() + "-" + sysBuildFloor.getId();
                        if (StringUtils.isNotEmpty(subitemFloorD.get(key2))) {
                            BigDecimal a = new BigDecimal(deviceCollect.get("value"));
                            BigDecimal b = new BigDecimal(subitemFloorD.get(key2));
                            subitemFloorD.put(key2, String.valueOf(a.add(b).setScale(2, RoundingMode.HALF_UP)));
                        } else {
                            subitemFloorD.put(key2, String.valueOf(new BigDecimal(deviceCollect.get("value")).setScale(2, RoundingMode.HALF_UP)));
                        }

                        //key
                        subitemNameKey.put(subitem.getId(), subitem.getSubitemName());
                        floorNameKey.put(sysBuildFloor.getId(), sysBuildFloor.getFloorName());
                    }
                    if ("2".equals(subitem.getEnergy_type())) {
                        Map<String, String> map = new HashMap<>();
                        map.put("name", "水能（总值）");
                        map.put("depth", "0");
                        totalKeyWater.put(subitem.getEnergy_type(), map);
                        if (StringUtils.isNotEmpty(energyTypeFloor.get(key))) {
                            BigDecimal a = new BigDecimal(deviceCollect.get("value"));
                            BigDecimal b = new BigDecimal(energyTypeFloor.get(key));
                            energyTypeFloor.put(key, String.valueOf(a.add(b).setScale(2, RoundingMode.HALF_UP)));
                        } else {
                            energyTypeFloor.put(key, String.valueOf(new BigDecimal(deviceCollect.get("value")).setScale(2, RoundingMode.HALF_UP)));
                        }

                        //分项-楼层
                        String key2 = subitem.getId() + "-" + sysBuildFloor.getId();
                        if (StringUtils.isNotEmpty(subitemFloorS.get(key2))) {
                            BigDecimal a = new BigDecimal(deviceCollect.get("value"));
                            BigDecimal b = new BigDecimal(subitemFloorS.get(key2));
                            subitemFloorS.put(key2, String.valueOf(a.add(b).setScale(2, RoundingMode.HALF_UP)));
                        } else {
                            subitemFloorS.put(key2, String.valueOf(new BigDecimal(deviceCollect.get("value")).setScale(2, RoundingMode.HALF_UP)));
                        }

                        //key
                        subitemNameKeyWater.put(subitem.getId(), subitem.getSubitemName());
                        floorNameKeyWater.put(sysBuildFloor.getId(), sysBuildFloor.getFloorName());
                    }
                }
                //分项-楼层
//                String key = subitem.getId() + "-" + sysBuildFloor.getId();
//                if (StringUtils.isNotEmpty(subitemFloor.get(key))) {
//                    BigDecimal a = new BigDecimal(deviceCollect.get("value"));
//                    BigDecimal b = new BigDecimal(subitemFloor.get(key));
//                    subitemFloor.put(key, String.valueOf(a.add(b).setScale(2, RoundingMode.HALF_UP)));
//                } else {
//                    subitemFloor.put(key, String.valueOf(new BigDecimal(deviceCollect.get("value")).setScale(2, RoundingMode.HALF_UP)));
//                }


//                subitemNameKeyWater.put(subitem.getId(), subitem.getSubitemName());

            }


        }

        //水电-楼层
        for (Map.Entry<String, String> sysBuildFloorEntry : energyTypeFloor.entrySet()) {
            String[] typeFloor = sysBuildFloorEntry.getKey().split("-");
            if ("1".equals(typeFloor[0])) {
                Map<String, String> map = new HashMap<>();
                map.put("source", "电能（总值）");
                map.put("target", sysBuildFloorMap.get(Integer.valueOf(typeFloor[1])).getFloorName());
                map.put("value", sysBuildFloorEntry.getValue());
                resultDataD.add(map);
                //能耗排名-楼层
                Map<String, String> mapFloor = new HashMap<>();
                mapFloor.put("id",sysBuildFloorMap.get(Integer.valueOf(typeFloor[1])).getFloorName());
                mapFloor.put("dataE",sysBuildFloorEntry.getValue());
                mapFloor.put("dataW",sysBuildFloorEntry.getValue());
                resultFloorsTop.add(mapFloor);
            }
            if ("2".equals(typeFloor[0])) {
                Map<String, String> map = new HashMap<>();
                map.put("source", "水能（总值）");
                map.put("target", sysBuildFloorMap.get(Integer.valueOf(typeFloor[1])).getFloorName());
                map.put("value", sysBuildFloorEntry.getValue());
                resultDataS.add(map);
            }
        }

        //分项-楼层 电
        for (Map.Entry<String, String> sysBuildFloorEntry : subitemFloorD.entrySet()) {
            String[] itemFloor = sysBuildFloorEntry.getKey().split("-");
            Map<String, String> map = new HashMap<>();
            map.put("target", configDeviceSubitemMap.get(Integer.valueOf(itemFloor[0])).getSubitemName());
            map.put("source", sysBuildFloorMap.get(Integer.valueOf(itemFloor[1])).getFloorName());
            map.put("value", sysBuildFloorEntry.getValue());
            resultDataD.add(map);
            //能耗排名-分项
            Map<String, String> mapFloor = new HashMap<>();
            mapFloor.put("name",configDeviceSubitemMap.get(Integer.valueOf(itemFloor[0])).getSubitemName());
            mapFloor.put("ratio",sysBuildFloorEntry.getValue());
            mapFloor.put("value",sysBuildFloorEntry.getValue());
            resultSubitemTop.add(mapFloor);
        }
        //分项-楼层 水
        for (Map.Entry<String, String> sysBuildFloorEntry : subitemFloorS.entrySet()) {
            String[] itemFloor = sysBuildFloorEntry.getKey().split("-");
            Map<String, String> map = new HashMap<>();
            map.put("target", configDeviceSubitemMap.get(Integer.valueOf(itemFloor[0])).getSubitemName());
            map.put("source", sysBuildFloorMap.get(Integer.valueOf(itemFloor[1])).getFloorName());
            map.put("value", sysBuildFloorEntry.getValue());
            resultDataS.add(map);
        }

        for (Map.Entry<String, Map<String, String>> m : totalKey.entrySet()) {
            Map<String, String> map = m.getValue();
            resultKey.add(map);
        }

        for (Map.Entry<String, Map<String, String>> m : totalKeyWater.entrySet()) {
            Map<String, String> map = m.getValue();
            resultKeyWater.add(map);
        }

        for (Map.Entry<Integer, String> m : floorNameKey.entrySet()) {
            Map<String, String> map = new HashMap<>();
            map.put("name", m.getValue());
            map.put("depth", "1");
            resultKey.add(map);
        }

        for (Map.Entry<Integer, String> m : floorNameKeyWater.entrySet()) {
            Map<String, String> map = new HashMap<>();
            map.put("name", m.getValue());
            map.put("depth", "1");
            resultKeyWater.add(map);
        }

        for (Map.Entry<Integer, String> m : subitemNameKey.entrySet()) {
            Map<String, String> map = new HashMap<>();
            map.put("name", m.getValue());
            map.put("depth", "2");
            resultKey.add(map);
        }

        for (Map.Entry<Integer, String> m : subitemNameKeyWater.entrySet()) {
            Map<String, String> map = new HashMap<>();
            map.put("name", m.getValue());
            map.put("depth", "2");
            resultKeyWater.add(map);
        }

        result.put("valueD", resultDataD);
        result.put("valueS", resultDataS);
        result.put("key", resultKey);
        result.put("keyWater", resultKeyWater);
        result.put("resultFloorsTop", resultFloorsTop);
        result.put("resultSubitemTop", resultSubitemTop);
        return result;
    }

    public List<Map<String, String>> sankiDiagramAllKey() {
        List<Map<String, String>> result = new ArrayList();
        //楼层
        List<SysBuildFloor> sysBuildFloors = sysBuildFloorService.list(Wrappers.<SysBuildFloor>lambdaQuery().eq(SysBuildFloor::getIsDel, G.ISDEL_NO).orderByAsc(SysBuildFloor::getSort));
        //设备分项
        List<ConfigDeviceSubitem> configDeviceSubitems = configDeviceSubitemMapper.selectList(Wrappers.<ConfigDeviceSubitem>lambdaQuery().eq(ConfigDeviceSubitem::getIsDel, G.ISDEL_NO));
        Map<String, String> mapTotal = new HashMap<>();
        mapTotal.put("name", "电能（总值）");
        mapTotal.put("depth", "0");
        result.add(mapTotal);
        mapTotal = new HashMap<>();
        mapTotal.put("name", "水能（总值）");
        mapTotal.put("depth", "0");
        result.add(mapTotal);
        for (SysBuildFloor sysBuildFloor : sysBuildFloors) {
            Map<String, String> map = new HashMap<>();
            map.put("name", sysBuildFloor.getFloorName());
            map.put("depth", "1");
            result.add(map);
        }
        for (ConfigDeviceSubitem configDeviceSubitem : configDeviceSubitems) {
            Map<String, String> map = new HashMap<>();
            map.put("name", configDeviceSubitem.getSubitemName());
            map.put("depth", "2");
            result.add(map);
        }

        return result;
    }

    public JSONObject overall() {
        JSONObject data = new JSONObject();
//        // 获取当前日期
//        Calendar calendar = Calendar.getInstance();
//        // 将日期设置为上个月
//        calendar.add(Calendar.MONTH, -1);
//        // 获取上个月的最后一天
//        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // 获取上个月的最后一天的 DateTime 对象
        cn.hutool.core.date.DateTime lastDayOfLastMonth = DateUtil.endOfMonth(DateUtil.offsetMonth(DateUtil.date(), -1));
        // 获取上个月最后一天的具体日期 (int 类型)
        int lastDay = lastDayOfLastMonth.dayOfMonth();

        Date date = DateUtil.lastMonth();
//        Date date = new Date();
        String time = new SimpleDateFormat("yyyy-MM").format(date);
        String startTime = time + "-01";
        String endTime = time + "-" + lastDay;
        String sql = "select spread(val) as totalPower from datasheet where channelId='" + G.CHANNELID_ELECTRICITY + "'  and time>='" + startTime + " 00:00:00" + "' and time<='" + endTime + " 23:59:59" + "' group  by deviceCollectId";
        //时序数据库解析
        QueryResult query = influxdbService.query(sql);
        //解析列表类查询
        List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
        BigDecimal sum = new BigDecimal(0.0);
        for (Map<String, Object> item : maps) {
            BigDecimal total = new BigDecimal(String.valueOf(null == item.get("totalPower") ? 0d :  item.get("totalPower")));
            sum = sum.add(total);
        }

        //初始化
        //indicators 指标
        //standard 标准
        //difference 差异对比
        JSONObject JsonData = new JSONObject();
        JsonData.put("indicators",55);
        JsonData.put("standard",55);
        JsonData.put("difference",0);
        JsonData.put("name","单位面积能耗");
        data.put("energy", JsonData);
        JsonData.put("indicators",4.0);
        JsonData.put("standard",4.0);
        JsonData.put("difference",0);
        JsonData.put("name","空调系统");
        data.put("airConditioning", JsonData);
        JsonData = new JSONObject();
        JsonData.put("indicators",9.0);
        JsonData.put("standard",9.0);
        JsonData.put("difference",0);
        JsonData.put("name","照明用电");
        data.put("lighting", JsonData);
        JsonData = new JSONObject();
        JsonData.put("indicators",9.0);
        JsonData.put("standard",9.0);
        JsonData.put("difference",0);
        JsonData.put("name","电气设备");
        data.put("electrical", JsonData);

        //单位面积能耗
        //计算公式：总能耗 / 建筑面积
        //单位：kWh/m²·a 或 kgce/m²·a（标准煤每年每平方米）
        BigDecimal a = sum.multiply(new BigDecimal(12));
        BigDecimal b = new BigDecimal(29600);
        BigDecimal indicators = a.divide(b, 0, RoundingMode.DOWN);
        BigDecimal areaSubtract =  new BigDecimal("55").subtract(indicators);
        BigDecimal difference =  areaSubtract.divide(new BigDecimal("55"),3,RoundingMode.DOWN).multiply(new BigDecimal(100)).setScale(1,RoundingMode.DOWN);
        JsonData = new JSONObject();
        JsonData.put("indicators",indicators);
        JsonData.put("standard", 55);
        JsonData.put("difference",difference);
        JsonData.put("name","单位面积能耗");
        data.put("energy", JsonData);

        //低效等级：能耗≥70
        //基准等级：44 ≤ 能耗 ＜ 70
        //节能等级：38.5 ≤ 能耗 ＜ 44
        //绿色等级：能耗 ＜ 38.5
        JsonData = new JSONObject();
        BigDecimal result = a.divide(b, 0, RoundingMode.DOWN);
        int intValue = result.intValue();
        if(intValue>=70){
            JsonData.put("value", 6);
            JsonData.put("msg", "低效等级");
        }else if(44 <= intValue && intValue < 69){
            JsonData.put("value", 7);
            JsonData.put("msg", "基准等级");
        }else if(38.5 <= intValue && intValue < 43){
            JsonData.put("value", 8);
            JsonData.put("msg", "节能等级");
        }else{
            JsonData.put("value", 9);
            JsonData.put("msg", "绿色等级");
        }
        JsonData.put("name","楼宇能耗指标");
        data.put("building", JsonData);



        BigDecimal electricalSum = new BigDecimal(0);

        List<JSONObject> list = subitemElectricity(startTime,endTime);
        List<JSONObject> top = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {

            JSONObject jsonObject = list.get(i);
            JsonData = new JSONObject();
            String name = jsonObject.getString("name");
            if(StringUtils.isNotEmpty(name) && name.replaceAll("\\s+", "").contains("空调")){
                //计算公式：总能耗 / 照明面积 13365
                a = new BigDecimal(jsonObject.getString("value")).multiply(new BigDecimal(12));
                b = new BigDecimal(13365);
                indicators = a.divide(b, 0, RoundingMode.DOWN);
                areaSubtract =  new BigDecimal("4.0").subtract(indicators);
                difference =  areaSubtract.divide(new BigDecimal("4.0"),3,RoundingMode.DOWN).multiply(new BigDecimal(100)).setScale(1,RoundingMode.DOWN);

                JsonData.put("indicators",indicators);
                JsonData.put("standard",4.0);
                JsonData.put("difference",difference);
                JsonData.put("name",name);
                data.put("airConditioning", JsonData);
            }else if(StringUtils.isNotEmpty(name) && name.replaceAll("\\s+", "").contains("照明")){
                //计算公式：总能耗 / 照明面积 13365
                a = new BigDecimal(jsonObject.getString("value")).multiply(new BigDecimal(12));
                b = new BigDecimal(13365);
                indicators = a.divide(b, 0, RoundingMode.DOWN);
                areaSubtract =  new BigDecimal("9.0").subtract(indicators);
                difference =  areaSubtract.divide(new BigDecimal("9.0"),3,RoundingMode.DOWN).multiply(new BigDecimal(100)).setScale(1,RoundingMode.DOWN);

                JsonData.put("indicators",indicators);
                JsonData.put("standard",9.0);
                JsonData.put("difference",difference);
                JsonData.put("name",name);
                data.put("lighting", JsonData);
            }else {
                //todo 暂定计算公式：总能耗 / 建筑面积
                a = new BigDecimal(jsonObject.getString("value")).multiply(new BigDecimal(12));
                b = new BigDecimal(29600);
                indicators = a.divide(b, 0, RoundingMode.DOWN);
                areaSubtract =  new BigDecimal("9.0").subtract(indicators);
                difference =  areaSubtract.divide(new BigDecimal("9.0"),3,RoundingMode.DOWN).multiply(new BigDecimal(100)).setScale(1,RoundingMode.DOWN);

                JsonData.put("indicators",indicators);
                JsonData.put("standard",10.0);
                JsonData.put("difference",difference);
                JsonData.put("name",name);
            }
            //电气设备功率密度（除了照明用电和空调系统的累加）
            if(StringUtils.isNotEmpty(name) && !name.replaceAll("\\s+", "").contains("空调") && !name.replaceAll("\\s+", "").contains("照明")){
                electricalSum = electricalSum.add(new BigDecimal(jsonObject.getString("value")));
            }

            top.add(JsonData);
        }
        data.put("top", top);


        //电气设备功率密度
        a = electricalSum.multiply(new BigDecimal(12));
        b = new BigDecimal(29600);
        indicators = a.divide(b, 0, RoundingMode.DOWN);
        areaSubtract =  new BigDecimal("9.0").subtract(indicators);
        difference =  areaSubtract.divide(new BigDecimal("9.0"),3,RoundingMode.DOWN).multiply(new BigDecimal(100)).setScale(1,RoundingMode.DOWN);

        JsonData = new JSONObject();
        JsonData.put("indicators",indicators);
        JsonData.put("standard",10.0);
        JsonData.put("difference",difference);
        JsonData.put("name","电气设备");
        data.put("electrical", JsonData);

        return data;
    }

    public Map<String, String>getValByType(String id){
        List<Map<String, Object>> wendu = influxdbService.queryResultProcess(influxdbService.query(this.getLastVal(id,"TEMP")));
        List<Map<String, Object>> shidu = influxdbService.queryResultProcess(influxdbService.query(this.getLastVal(id,"RH")));
        List<Map<String, Object>> PM = influxdbService.queryResultProcess(influxdbService.query(this.getLastVal(id,"PM2.5")));
        List<Map<String, Object>> anQi = influxdbService.queryResultProcess(influxdbService.query(this.getLastVal(id,"CH2O")));
        Map<String,String> map=new HashMap<>();
        map.put("wendu",lastResultProcess(wendu));
        map.put("shidu",lastResultProcess(shidu));
        map.put("PM",lastResultProcess(PM));
        map.put("anQi",lastResultProcess(anQi));
        return map;
    }


    public String getLastVal(String id ,String type) {
        String sql =
                "select  last(val)  from datasheet WHERE channelId='"+type+"'  AND deviceCollectId ='" + id + "'";
        return sql;
    }

    public String getToiletIsUse(String id){
        List<Map<String, Object>> isUse = influxdbService.queryResultProcess(influxdbService.query(this.isUse(id)));
        String s = typeResultProcess(isUse);
        return s;
    }

    public String isUse(String id) {
        String sql =
                "select  last(val)  from datasheet WHERE channelId='HumanStatus'  AND deviceCollectId ='" + id + "'";
        return sql;
    }


    public String liftSelect(String func,String group ,String deviceCode,String deviceSn ,String start,String end,
                             String passengerIn ,String motion,String workMode) {

        String sql ="select  "+func;
        sql+=" from devicelift WHERE 1=1 ";
        if (StringUtils.isNotEmpty(deviceCode)){
            sql+=" and deviceCode='"+deviceCode+"'";
        }
        if (StringUtils.isNotEmpty(deviceSn)){
            sql+=" and deviceSn='"+deviceSn+"'";
        }
        if (StringUtils.isNotEmpty(start)){
            sql+=" and time>='"+start+"'";
        }
        if (StringUtils.isNotEmpty(end)){
            sql+=" and time<='"+end+"'";
        }
        if (StringUtils.isNotEmpty(passengerIn)){
            sql+=" and passengerIn='"+passengerIn+"'";
        }
        if (StringUtils.isNotEmpty(workMode)){
            sql+=" and workMode='"+workMode+"'";
        }
        if (StringUtils.isNotEmpty(motion)){
           if ("2".equals(motion)){
               sql+=" and motion !='0'";
           }else{
               sql+=" and motion='"+motion+"'";
           }
        }
        if (!StringUtils.isEmpty(group)){
            sql+=" group by " + group;
        }
        return sql;
    }
    public List<Double> countResultProcess(List<Map<String, Object>> list) {
        List<Double> counts=new ArrayList<>();
        if(list != null && list.size() != 0) {
            for (Map<String, Object> map : list) {
                double num = StringUtils.isNotNull(map.get("count"))?(double)map.get("count"):0.0   ;
                counts.add(num);
            }
        }
        return counts;
    }


    //电梯承载率



    public Map<String,String> runStatistics(String deviceCode){
        Map <String ,String> map=new HashMap<>();
        List<Map<String, Object>> todayRunTime = influxdbService.queryResultProcess(influxdbService.query(liftSelect(
                " SPREAD(cumulativeRunTimeVal) ", null, deviceCode, null,
                DateUtils.getStartTimeOfCurrentDay(new Date()), DateUtils.getEndTimeOfCurrentDay(new Date()), null
                , null,null)));
        List<Map<String, Object>> allRunTime = influxdbService.queryResultProcess(influxdbService.query(liftSelect(
                " SPREAD(cumulativeRunTimeVal) ", null, deviceCode, null,
                null, null, null
                , null,null)));
        List<Map<String, Object>> todayRunNum = influxdbService.queryResultProcess(influxdbService.query(liftSelect(
                " SPREAD(cumulativeRunNumVal) ", null, deviceCode, null,
                DateUtils.getStartTimeOfCurrentDay(new Date()), DateUtils.getEndTimeOfCurrentDay(new Date()), null
                , null,null)));
        List<Map<String, Object>> allRunNum = influxdbService.queryResultProcess(influxdbService.query(liftSelect(
                " SPREAD(cumulativeRunNumVal) ", null, deviceCode, null,
                null, null, null
                , null,null)));
        List<Map<String, Object>> todayMileage = influxdbService.queryResultProcess(influxdbService.query(liftSelect(
                " SPREAD(mileageVal) ", null, deviceCode, null,
                DateUtils.getStartTimeOfCurrentDay(new Date()), DateUtils.getEndTimeOfCurrentDay(new Date()), null
                , null,null)));
        List<Map<String, Object>> allMileage = influxdbService.queryResultProcess(influxdbService.query(liftSelect(
                " SPREAD(mileageVal) ", null, deviceCode, null,
                null, null, null
                , null,null)));
        String todayRunTimes = spreadResultProcess(todayRunTime);
        String allRunTimes = spreadResultProcess(allRunTime);
        String todayRunNums = spreadResultProcess(todayRunNum);
        String allRunNums = spreadResultProcess(allRunNum);
        String todayMileages = spreadResultProcess(todayMileage);
        String allMileages = spreadResultProcess(allMileage);
        map.put("todayRunTimes",todayRunTimes);
        map.put("allRunTimes",allRunTimes);
        map.put("todayRunNums",todayRunNums);
        map.put("allRunNums",allRunNums);
        map.put("todayMileages",todayMileages);
        map.put("allMileages",allMileages);
        return  map;
    }





    public String outTemp(String deviceId){
        String temp="0";
        List<Map<String, Object>> list = influxdbService.queryResultProcess(influxdbService.query(getLastVal("TEMP",
                deviceId)));
        for (Map<String, Object> map : list) {
            temp=map.get("last")+"";
        }
        return temp;
    }

    /**
     * 驾驶舱
     * 实时水压监测统计 sql
     */
    public String selectWaterPressureSql(String startTime, String endTime) {

        String sql = "SELECT LAST(val) FROM datasheet WHERE channelId = 'WATER_P' ";
        sql += " AND time >= '" + startTime + "' AND time < '" + endTime + "'";
        sql += " GROUP BY time(1h)";
        return sql;
    }

    /**
     * 驾驶舱
     * 实时水压监测统计
     */
    public JSONObject getWaterPressureData() {
        JSONObject jsonObject = new JSONObject();
        List<String> nameList = new ArrayList<>();
        for (int i=0;i<24;i++) {
            if (i<10) {
                nameList.add("0"+i+":00");
            } else {
                nameList.add(i+":00");
            }
        }
        List<String> dataList = new ArrayList<>();
        List<Map<String, Object>> lists = queryResultProcess1(influxdbService.query(selectWaterPressureSql(new DateTime().toString("yyyy-MM-dd"), new DateTime().plusDays(1).toString("yyyy-MM-dd"))));
        if (lists.isEmpty()) {
            for (int i = 0; i < nameList.size(); i++) {
                dataList.add("0.00");
            }
        } else {
            for (Map<String, Object> map : lists) {
                Double spread = (Double) map.get("last");
                if (spread == null) {
                    spread = 0.00;
                }
                dataList.add(new BigDecimal(spread).setScale(2, RoundingMode.HALF_UP).toString());
            }
        }
        jsonObject.put("nameList", nameList);
        jsonObject.put("dataList", dataList);

        return jsonObject;
    }



    public  List<Map<String, Object>> airLog(SysLogVo vo){
        List<Map<String, Object>> lists =
                influxdbService.queryResultProcess
                        (influxdbService.query(selectAirLog(vo)));
        return lists;
    }

    public  List<Map<String, Object>> getEnetgyAll(String id){
        List<Map<String, Object>> lists =
                influxdbService.queryResultProcess
                        (influxdbService.query(selectEnetgyAll(id)));
        return lists;
    }

    public String selectEnetgyAll( String id) {

        String sql = "select  spread(val)  from datasheet WHERE channelId='" + G.CHANNELID_ELECTRICITY + "'  AND deviceCollectId ='" + id + "'";
        return sql;
    }

    public String selectAirLog(SysLogVo vo) {
        String sql = "SELECT * FROM syslog WHERE 1=1 ";
        if (StringUtils.isEmpty(vo.getLogType())){
//            sql+=" AND (logType='10-1' OR logType='10-3' OR logType='10-5') ";
            sql+=" AND (logType='10-1' OR logType='10-3') ";
            if("1".equals(vo.getApi())){
                sql+=" AND api=~/^\\/acController.*/ ";
            }else if ("0".equals(vo.getApi())) {
                sql+=" AND api=~/app/ ";
            }
        }else {
            sql+=" AND logType='"+vo.getLogType()+"' ";
            if("1".equals(vo.getApi())){
                sql+=" AND api=~/^\\/deviceCollect.*/ ";
            }else if ("0".equals(vo.getApi())){
                sql+=" AND api=~/app/  ";
            }
        }

        if (StringUtils.isNotEmpty(vo.getContent())){
            sql+=" AND content1  =~/"+vo.getContent()+"/  ";
        }
        if (StringUtils.isNotEmpty(vo.getDeviceCode())){
            sql+=" AND val_string  =~/"+vo.getDeviceCode()+"/  ";
        }
        if (StringUtils.isNotEmpty(vo.getOperatorName())){
            sql+=" AND operatorName  =~/"+vo.getOperatorName()+"/  ";
        }
        if (StringUtils.isNotEmpty(vo.getStartTime())&&StringUtils.isNotEmpty(vo.getEndTime())){
            sql+="  and time>='"+vo.getStartTime()+"' and time<='"+vo.getEndTime()+"' ";
        }
        sql +="  ORDER BY time desc ";
        if(vo.getPageSize()!=0){
            sql+= " LIMIT "+vo.getPageSize()+" OFFSET "+ vo.getPageSize()*(vo.getPageNo()-1);
        }
        return sql;
    }

    public Map<String,String> getZHCZ(Integer id){
        List<Map<String, Object>> wendu = influxdbService.queryResultProcess(influxdbService.query( getLastVal(id + "", "OL_STATE")));
        Map<String,String> map=new HashMap<>();
        map.put("on",lastResultProcess(wendu));
        return map;
    }

    public Map<String,String> getZHCZ_A(Integer id){
        List<Map<String, Object>> wendu = influxdbService.queryResultProcess(influxdbService.query( getLastVal(id + "", "I_A")));
        Map<String,String> map=new HashMap<>();
        map.put("I_A",lastResultProcess(wendu));
        return map;
    }


    public List<Map<String, Object>> energyTrend_ZHCZ(Integer id) {
        Double toDay1 = 0.00;
        Double lastDay1 = 0.00;
        Double toWeek1 = 0.00;
        Double lastWeek1 = 0.00;
        Double toMonth1 = 0.00;
        Double lastMonth1 = 0.00;
        Double toYear1 = 0.00;
        Double lastYear1 = 0.00;

            QueryResult toDay = influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(new Date(),
                    JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(new Date(),
                    JsdcDateUtil.FULL_SPLIT_PATTERN), id + ""));
            QueryResult lastDay =
                    influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-1,
                            new Date()),
                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-1, new Date()),
                            JsdcDateUtil.FULL_SPLIT_PATTERN), id+ ""));
            QueryResult toWeek =
                    influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(new Date(),
                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstWeekDay(new Date()),
                            JsdcDateUtil.FULL_SPLIT_PATTERN), id + ""));
            QueryResult lastWeek =
                    influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-7,
                            new Date()),
                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-7,
                            JsdcDateUtil.getFirstWeekDay(new Date())),
                            JsdcDateUtil.FULL_SPLIT_PATTERN), id + ""));
            QueryResult toMonth =
                    influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(new Date(),
                            JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(new Date(),
                            Calendar.DAY_OF_MONTH), JsdcDateUtil.FULL_SPLIT_PATTERN), id+ ""));
            QueryResult lastMonth =
                    influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-30,
                            new Date()), JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
                            JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(JsdcDateUtil.lastDayTime(-30, new Date()),
                                    Calendar.DAY_OF_MONTH), JsdcDateUtil.FULL_SPLIT_PATTERN), id + ""));
            QueryResult toYear = influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(new Date(),
                    JsdcDateUtil.FULL_TIME_SPLIT_PATTERN), JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(new Date(),
                    Calendar.DAY_OF_YEAR), JsdcDateUtil.FULL_SPLIT_PATTERN), id + ""));
            QueryResult lastYear =
                    influxdbService.query(this.getEnergyEvaluate(JsdcDateUtil.getDateFormat(JsdcDateUtil.lastDayTime(-365,
                            new Date()), JsdcDateUtil.FULL_TIME_SPLIT_PATTERN),
                            JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(JsdcDateUtil.lastDayTime(-365, new Date()),
                                    Calendar.DAY_OF_YEAR), JsdcDateUtil.FULL_SPLIT_PATTERN), id + ""));
            List<Map<String, Object>> toDayList = influxdbService.queryResultProcess(toDay);
            List<Map<String, Object>> lastDayList = influxdbService.queryResultProcess(lastDay);
            List<Map<String, Object>> toWeekList = influxdbService.queryResultProcess(toWeek);
            List<Map<String, Object>> lastWeekList = influxdbService.queryResultProcess(lastWeek);
            List<Map<String, Object>> toMonthList = influxdbService.queryResultProcess(toMonth);
            List<Map<String, Object>> lastMonthList = influxdbService.queryResultProcess(lastMonth);
            List<Map<String, Object>> toYearList = influxdbService.queryResultProcess(toYear);
            List<Map<String, Object>> lastYearList = influxdbService.queryResultProcess(lastYear);
            toDay1 += Double.parseDouble(this.spreadResultProcess(toDayList));
            lastDay1 += Double.parseDouble(this.spreadResultProcess(lastDayList));
            toWeek1 += Double.parseDouble(this.spreadResultProcess(toWeekList));
            lastWeek1 += Double.parseDouble(this.spreadResultProcess(lastWeekList));
            toMonth1 += Double.parseDouble(this.spreadResultProcess(toMonthList));
            lastMonth1 += Double.parseDouble(this.spreadResultProcess(lastMonthList));
            toYear1 += Double.parseDouble(this.spreadResultProcess(toYearList));
            lastYear1 += Double.parseDouble(this.spreadResultProcess(lastYearList));

        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("toDay", new BigDecimal(toDay1).setScale(2, ROUND_HALF_UP).toString());
        map.put("lastDay", new BigDecimal(lastDay1).setScale(2, ROUND_HALF_UP).toString());
        map.put("toWeek", new BigDecimal(toWeek1).setScale(2, ROUND_HALF_UP).toString());
        map.put("lastWeek", new BigDecimal(lastWeek1).setScale(2, ROUND_HALF_UP).toString());
        map.put("toMonth", new BigDecimal(toMonth1).setScale(2, ROUND_HALF_UP).toString());
        map.put("lastMonth", new BigDecimal(lastMonth1).setScale(2, ROUND_HALF_UP).toString());
        map.put("toYear", new BigDecimal(toYear1).setScale(2, ROUND_HALF_UP).toString());
        map.put("lastYear", new BigDecimal(lastYear1).setScale(2, ROUND_HALF_UP).toString());
        list.add(map);
        return list;
    }


    /**
     * 变压器检测
     * sql语句
     */
    public String getLastValByHourSql(String startTime, String endTime, String channelId, String deviceCollectId, Integer pageNo, Integer pageSize, String sort) {
        String sql = "SELECT last(val) as val FROM datasheet WHERE channelId = '" + channelId + "' AND deviceCollectId= '" + deviceCollectId + "'" +
                " AND time >= '" + startTime + "' AND time < '" + endTime + "'";
        sql += "  GROUP BY time(1h)";
        if (StringUtils.isNotEmpty(sort)) {
            sql += " ORDER by time " + sort;
        }
        if (null != pageNo && null != pageSize) {
            sql += " limit " + pageSize + " offset " + (pageNo - 1) * pageSize;
        }

        return sql;
    }

    /**
     * 变压器检测
     * 根据时间、信号、设备，查询最新值
     */
    public String getLastValSql(String startTime, String endTime, String channelId, String deviceCollectId) {
        String val = "0.00";

        String sql = "SELECT last(val) as val FROM datasheet WHERE channelId = '" + channelId + "' AND deviceCollectId= '" + deviceCollectId + "'" +
                " AND time >= '" + startTime + "' AND time < '" + endTime + "'";

        List<Map<String, Object>> lists = queryResultProcess1(influxdbService.query(sql));

        for (int x = 0; x < lists.size(); x++) {
            Map<String, Object> map = lists.get(x);
            Double spread = (Double) map.get("val");
            if (spread == null) {
                spread = 0.00;
            }
            val = new BigDecimal(spread).setScale(2, RoundingMode.HALF_UP).toString();
        }
        return val;
    }

    /**
     * 变压器检测
     * 最大负荷报表
     */
    public List<TransformerVo> loadReport(DeviceQueryVo deviceQueryVo) {
        List<TransformerVo> data = new ArrayList<>();
        //1:日 2：月
        if ("1".equals(deviceQueryVo.getTimeType())) {
            //默认查询今日
            if (StringUtils.isEmpty(deviceQueryVo.getTimeStart())) {
                deviceQueryVo.setTimeStart(new DateTime().toString("yyyy-MM-dd"));
            }
            deviceQueryVo.setTimeEnd(new DateTime(deviceQueryVo.getTimeStart()).plusDays(1).toString("yyyy-MM-dd"));

            List<Map<String, Object>> maps = influxdbService.queryResultProcess(influxdbService.query(getLastValByHourSql(deviceQueryVo.getTimeStart(), deviceQueryVo.getTimeEnd(), "STAP_Total", deviceQueryVo.getDeviceId(), deviceQueryVo.getPageNo(), deviceQueryVo.getPageSize(), "asc")));
            //转换成实体对象
            data = JSONObject.parseArray(JSONObject.toJSONString(maps), TransformerVo.class);
            for (TransformerVo transformerVo : data) {
                transformerVo.setTime(transformerVo.getTime().replace("T"," ").replace("Z",""));
                if (StringUtils.isNotEmpty(transformerVo.getVal())) {
                    transformerVo.setLoad_occupancy(new BigDecimal(transformerVo.getVal()).divide(new BigDecimal(1250000)).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).toString() + "%");
                } else {
                    transformerVo.setVal("0");
                    transformerVo.setLoad_occupancy("0%");
                }
                transformerVo.setRated_capacity("1250000");
            }
        } else {
            //默认查询本月
            if (StringUtils.isEmpty(deviceQueryVo.getTimeStart())) {
                deviceQueryVo.setTimeStart(new DateTime().toString("yyyy-MM-01"));
            }
            deviceQueryVo.setTimeEnd(new DateTime(deviceQueryVo.getTimeStart()).plusMonths(1).toString("yyyy-MM-dd"));

            String lastDay = new DateTime(deviceQueryVo.getTimeStart()).plusMonths(1).plusDays(-1).toString("dd");
            for (int i=0;i<Integer.parseInt(lastDay);i++) {
//                System.out.println("timeStart：" + new DateTime(deviceQueryVo.getTimeStart()).plusDays(i).toString("yyyy-MM-dd"));
//                System.out.println("timeEnd：" + new DateTime(deviceQueryVo.getTimeStart()).plusDays(i+1).toString("yyyy-MM-dd"));

                String timeStart = new DateTime(deviceQueryVo.getTimeStart()).plusDays(i).toString("yyyy-MM-dd");
                String timeEnd = new DateTime(deviceQueryVo.getTimeStart()).plusDays(i+1).toString("yyyy-MM-dd");

                //最新负载率
                String stap_total = getLastValSql(timeStart, timeEnd, "STAP_Total", deviceQueryVo.getDeviceId());

                TransformerVo transformerVo = new TransformerVo();
                transformerVo.setTime(timeStart);
                if (StringUtils.isNotEmpty(stap_total)) {
                    transformerVo.setVal(stap_total);
                    transformerVo.setLoad_occupancy(new BigDecimal(stap_total).divide(new BigDecimal(1250000)).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).toString() + "%");
                } else {
                    transformerVo.setVal("0");
                    transformerVo.setLoad_occupancy("0%");
                }
                transformerVo.setRated_capacity("1250000");
                data.add(transformerVo);
            }
        }

        return data;
    }

}
