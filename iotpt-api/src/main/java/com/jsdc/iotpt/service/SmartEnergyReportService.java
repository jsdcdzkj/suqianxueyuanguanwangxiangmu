package com.jsdc.iotpt.service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.iotpt.mapper.SysBuildAreaMapper;
import com.jsdc.iotpt.mapper.SysDictMapper;
import com.jsdc.iotpt.model.ConfigDeviceType;
import com.jsdc.iotpt.model.DeviceCollect;
import com.jsdc.iotpt.model.WaterPrice;
import com.jsdc.iotpt.service.influxDB.InfluxdbService;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.DeviceCollectVo;
import com.jsdc.iotpt.vo.SmartEnergyReportVo;
import org.influxdb.dto.QueryResult;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SmartEnergyReportService {

    @Autowired
    private InfluxdbService influxdbService;
    @Autowired
    private SysDictMapper sysDictMapper;
    @Autowired
    private SysBuildAreaMapper sysBuildAreaMapper;
    @Autowired
    private ConfigDeviceTypeService configDeviceTypeService;
    @Autowired
    private DeviceCollectService deviceCollectService;

    @Autowired
    private WaterPriceService waterPriceService;


//    public static void main(String[] args) {
//        String dateStr1 = "2017-03-01";
//        Date date1 = DateUtil.parse(dateStr1);
//
//        String dateStr2 = "2017-04-01";
//        Date date2 = DateUtil.parse(dateStr2);
//
//        //相差一个月，31天
//        long betweenDay = DateUtil.between(date1, date2, DateUnit.HOUR);
//
//        System.out.println(betweenDay);
//
//
//    }

    /**
     * 多维分析
     */
    public JSONObject multidimensionalAnalysis(SmartEnergyReportVo vo) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        //名称 x轴 value
        List<String> nameList = new ArrayList<>();
        //值 y轴 value
        List<String> valList = new ArrayList<>();

        double total = 0d;

        //多时间
        if (StringUtils.isNotNull(vo.getTimeList()) && vo.getTimeList().size() > 0) {
            //获取设备id集合
            List<Integer> collectIds = new ArrayList<>();
            if (StringUtils.isNotNull(vo.getDeviceSubitemId())) {
                collectIds = this.getCollectIds(vo, "subitem", vo.getDeviceSubitemId());
            } else {
                if (StringUtils.isNotNull(vo.getAreaId())) {
                    collectIds = this.getCollectIds(vo, "area", vo.getAreaId());
                } else if (StringUtils.isNotNull(vo.getFloorId())) {
                    collectIds = this.getCollectIds(vo, "floor", vo.getFloorId());
                } else if (StringUtils.isNotNull(vo.getBuildId())) {
                    collectIds = this.getCollectIds(vo, "build", vo.getBuildId());
                }
            }
            //采集设备id
            if (StringUtils.isNull(collectIds) || collectIds.size() == 0) {
                collectIds = new ArrayList<>();
                collectIds.add(-1);
            }

            for (String str : vo.getTimeList()) {
                String[] times = str.split("至");
                vo.setStartTime(times[0]);
                vo.setEndTime(times[1]);

                vo.setCollectIds(collectIds);

                //结果
                SmartEnergyReportVo smartEnergyReportVo = this.multidimensionalQuery(vo);

                //查询条件：时间类型 0任意时间 1 年度 2 月度 3 周 4 日
                if (vo.getTimeType().equals("2")) {
                    //名称 x轴 value
                    nameList = new ArrayList<>();
                    //值 y轴 value
                    valList = new ArrayList<>();
                }
                //空数据赋值x轴y轴数据
                this.setXYValue(smartEnergyReportVo, vo, nameList, valList);

                smartEnergyReportVo.setName(str);

                //饼状图
                if(vo.getDateType().equals("1")) {
                    SmartEnergyReportVo vo1 = new SmartEnergyReportVo();
                    BeanUtils.copyProperties(vo, vo1);
                    vo1.setDateType("2");
                    SmartEnergyReportVo smartEnergyReportVoBzt = this.multidimensionalQuery(vo1);
                    smartEnergyReportVo.setVal(smartEnergyReportVoBzt.getVal());
                    total += Double.parseDouble(smartEnergyReportVoBzt.getVal());
                } else {
                    total += Double.parseDouble(smartEnergyReportVo.getVal());
                }

                jsonArray.add(smartEnergyReportVo);
            }
        }

        //多对象 设备分项
        if (StringUtils.isNotNull(vo.getDeviceSubitemIds()) && vo.getDeviceSubitemIds().size() > 0) {
            for (Integer id : vo.getDeviceSubitemIds()) {
                vo.setDeviceSubitemId(id);

                //获取设备id集合
                List<Integer> collectIds = this.getCollectIds(vo, "subitem", id);
                //采集设备id
                if (StringUtils.isNull(collectIds) || collectIds.size() == 0) {
                    collectIds = new ArrayList<>();
                    collectIds.add(-1);
                }
                vo.setCollectIds(collectIds);

                //结果
                SmartEnergyReportVo smartEnergyReportVo = this.multidimensionalQuery(vo);

                //查询条件：时间类型 0任意时间 1 年度 2 月度 3 周 4 日
                if (vo.getTimeType().equals("2")) {
                    //名称 x轴 value
                    nameList = new ArrayList<>();
                    //值 y轴 value
                    valList = new ArrayList<>();
                }
                //空数据赋值x轴y轴数据
                this.setXYValue(smartEnergyReportVo, vo, nameList, valList);

                smartEnergyReportVo.setName(id.toString());
                //饼状图
                if(vo.getDateType().equals("1")) {
                    SmartEnergyReportVo vo1 = new SmartEnergyReportVo();
                    BeanUtils.copyProperties(vo, vo1);
                    vo1.setDateType("2");
                    SmartEnergyReportVo smartEnergyReportVoBzt = this.multidimensionalQuery(vo1);
                    smartEnergyReportVo.setVal(smartEnergyReportVoBzt.getVal());
                    total += Double.parseDouble(smartEnergyReportVoBzt.getVal());
                } else {
                    total += Double.parseDouble(smartEnergyReportVo.getVal());
                }
                jsonArray.add(smartEnergyReportVo);
            }
        }

        //多对象 楼宇
        if (StringUtils.isNotNull(vo.getBuildIds()) && vo.getBuildIds().size() > 0) {
            for (Integer id : vo.getBuildIds()) {
                //获取设备id集合
                List<Integer> collectIds = this.getCollectIds(vo, "build", id);
                //采集设备id
                if (StringUtils.isNull(collectIds) || collectIds.size() == 0) {
                    collectIds = new ArrayList<>();
                    collectIds.add(-1);
                }

                vo.setBuildId(id);
                vo.setFloorId(null);
                vo.setAreaId(null);

                vo.setCollectIds(collectIds);

                //结果
                SmartEnergyReportVo smartEnergyReportVo = this.multidimensionalQuery(vo);

                //查询条件：时间类型 0任意时间 1 年度 2 月度 3 周 4 日
                if (vo.getTimeType().equals("2")) {
                    //名称 x轴 value
                    nameList = new ArrayList<>();
                    //值 y轴 value
                    valList = new ArrayList<>();
                }
                //空数据赋值x轴y轴数据
                this.setXYValue(smartEnergyReportVo, vo, nameList, valList);

                smartEnergyReportVo.setName(id.toString());
                smartEnergyReportVo.setLevel("1");
                //饼状图
                if(vo.getDateType().equals("1")) {
                    SmartEnergyReportVo vo1 = new SmartEnergyReportVo();
                    BeanUtils.copyProperties(vo, vo1);
                    vo1.setDateType("2");
                    SmartEnergyReportVo smartEnergyReportVoBzt = this.multidimensionalQuery(vo1);
                    smartEnergyReportVo.setVal(smartEnergyReportVoBzt.getVal());
                    total += Double.parseDouble(smartEnergyReportVoBzt.getVal());
                } else {
                    total += Double.parseDouble(smartEnergyReportVo.getVal());
                }
                jsonArray.add(smartEnergyReportVo);
            }
        }
        //多对象 楼层
        if (StringUtils.isNotNull(vo.getFloorIds()) && vo.getFloorIds().size() > 0) {
            for (Integer id : vo.getFloorIds()) {
                //获取设备id集合
                List<Integer> collectIds = this.getCollectIds(vo, "floor", id);
                //采集设备id
                if (StringUtils.isNull(collectIds) || collectIds.size() == 0) {
                    collectIds = new ArrayList<>();
                    collectIds.add(-1);
                }

                vo.setFloorId(id);
                vo.setBuildId(null);
                vo.setAreaId(null);

                vo.setCollectIds(collectIds);

                //结果
                SmartEnergyReportVo smartEnergyReportVo = this.multidimensionalQuery(vo);

                //查询条件：时间类型 0任意时间 1 年度 2 月度 3 周 4 日
                if (vo.getTimeType().equals("2")) {
                    //名称 x轴 value
                    nameList = new ArrayList<>();
                    //值 y轴 value
                    valList = new ArrayList<>();
                }
                //空数据赋值x轴y轴数据
                this.setXYValue(smartEnergyReportVo, vo, nameList, valList);

                smartEnergyReportVo.setName(id.toString());
                smartEnergyReportVo.setLevel("2");
                //饼状图
                if(vo.getDateType().equals("1")) {
                    SmartEnergyReportVo vo1 = new SmartEnergyReportVo();
                    BeanUtils.copyProperties(vo, vo1);
                    vo1.setDateType("2");
                    SmartEnergyReportVo smartEnergyReportVoBzt = this.multidimensionalQuery(vo1);
                    smartEnergyReportVo.setVal(smartEnergyReportVoBzt.getVal());
                    total += Double.parseDouble(smartEnergyReportVoBzt.getVal());
                } else {
                    total += Double.parseDouble(smartEnergyReportVo.getVal());
                }
                jsonArray.add(smartEnergyReportVo);
            }
        }
        //多对象 区域
        if (StringUtils.isNotNull(vo.getAreaIds()) && vo.getAreaIds().size() > 0) {
            for (Integer id : vo.getAreaIds()) {
                //获取设备id集合
                List<Integer> collectIds = this.getCollectIds(vo, "area", id);
                //采集设备id
                if (StringUtils.isNull(collectIds) || collectIds.size() == 0) {
                    collectIds = new ArrayList<>();
                    collectIds.add(-1);
                }

                vo.setAreaId(id);
                vo.setBuildId(null);
                vo.setFloorId(null);

                vo.setCollectIds(collectIds);

                //结果
                SmartEnergyReportVo smartEnergyReportVo = this.multidimensionalQuery(vo);

                //查询条件：时间类型 0任意时间 1 年度 2 月度 3 周 4 日
                if (vo.getTimeType().equals("2")) {
                    //名称 x轴 value
                    nameList = new ArrayList<>();
                    //值 y轴 value
                    valList = new ArrayList<>();
                }
                //空数据赋值x轴y轴数据
                this.setXYValue(smartEnergyReportVo, vo, nameList, valList);

                smartEnergyReportVo.setName(id.toString());
                smartEnergyReportVo.setLevel("3");
                //饼状图
                if(vo.getDateType().equals("1")) {
                    SmartEnergyReportVo vo1 = new SmartEnergyReportVo();
                    BeanUtils.copyProperties(vo, vo1);
                    vo1.setDateType("2");
                    SmartEnergyReportVo smartEnergyReportVoBzt = this.multidimensionalQuery(vo1);
                    smartEnergyReportVo.setVal(smartEnergyReportVoBzt.getVal());
                    total += Double.parseDouble(smartEnergyReportVoBzt.getVal());
                } else {
                    total += Double.parseDouble(smartEnergyReportVo.getVal());
                }
                jsonArray.add(smartEnergyReportVo);
            }
        }

//        //多对象 设备类型/分类
//        if (StringUtils.isNotNull(vo.getDeviceTypeIds()) && vo.getDeviceTypeIds().size() > 0) {
//            for (Integer id : vo.getDeviceTypeIds()) {
//                vo.setDeviceTypeId(id);
//                //结果
//                SmartEnergyReportVo smartEnergyReportVo = this.multidimensionalQuery(vo);
//                //空数据赋值x轴y轴数据
//                this.setXYValue(smartEnergyReportVo, vo, nameList, valList);
//                smartEnergyReportVo.setName(id.toString());
//                total += Double.parseDouble(smartEnergyReportVo.getVal());
//                jsonArray.add(smartEnergyReportVo);
//            }
//        }
        DecimalFormat df = new DecimalFormat("0.00");
        for (Object object : jsonArray) {
            SmartEnergyReportVo smartEnergyReportVo = (SmartEnergyReportVo) object;
            if (total > 0d) {
                double val = Double.parseDouble(smartEnergyReportVo.getVal()) / total * 100;
                smartEnergyReportVo.setRatio(df.format(val) + "%");
            } else {
                smartEnergyReportVo.setRatio("0%");
            }

        }
        jsonObject.put("vo", jsonArray);
        return jsonObject;
    }

    /**
     * 多维分析
     * 获取设备id集合
     */
    public List<Integer> getCollectIds(SmartEnergyReportVo vo, String type, Integer id) {
        ConfigDeviceType configDeviceType = new ConfigDeviceType();
        //能源类型 1 电 2 水
        if (vo.getEnergyType().equals("1")) {
            //E_METER	智能电表 ICB 智能空开
            List<String> codes = new ArrayList();
            codes.add("E_METER");
            codes.add("ICB4");
            codes.add("ICB");
            codes.add("DAJIN");//空调电表（大金）
            codes.add("KTDB");//空调电表（格力）
            configDeviceType.setDeviceTypeCodes(codes);
        } else {
            //W_METER	智能水表
            configDeviceType.setDeviceTypeCodes(null);
            configDeviceType.setDeviceTypeCode("W_METER");
        }

        List<ConfigDeviceType> deviceTypeList = configDeviceTypeService.getList(configDeviceType);

        //测试楼宇写死id
        if (deviceTypeList.size() > 0) {
            DeviceCollectVo collectVo = new DeviceCollectVo();
            List<Integer> deviceTypes = deviceTypeList.stream().map(ConfigDeviceType::getId).collect(Collectors.toList());
            collectVo.setDeviceTypes(deviceTypes);
            List<DeviceCollect> collectList = new ArrayList<>();
            if (type.equals("build")) {
                collectVo.setLogicalBuildId(id);
                collectList = deviceCollectService.getListByBuildId(collectVo);
            } else if (type.equals("floor")) {
                collectVo.setLogicalFloorId(id);
                collectList = deviceCollectService.getListByFloorId(collectVo);
            } else if (type.equals("area")) {
                collectVo.setLogicalAreaId(id);
                collectList = deviceCollectService.getListByAreaId(collectVo);
                if (collectList.size() == 0) {
                    collectList = deviceCollectService.getListByAreaId2(collectVo);
                }
            } else if (type.equals("subitem")) {
                collectVo.setSubitem(vo.getDeviceSubitemId());
                collectList = deviceCollectService.getListBySubItemId(collectVo);
            }
            List<Integer> collectIds = collectList.stream().map(DeviceCollect::getId).collect(Collectors.toList());
            return collectIds;
        }

        return null;
    }

    /**
     * 多维分析
     * 空数据赋值x轴y轴数据
     */
    public void setXYValue(SmartEnergyReportVo smartEnergyReportVo, SmartEnergyReportVo vo, List<String> nameList, List<String> valList) {
        if (smartEnergyReportVo.getNameList().size() == 0) {
            //查询条件：时间类型 0任意时间 1 年度 2 月度 3 周 4 日
            if (nameList.size() == 0 || vo.getTimeType().equals("2")) {
                Date date1 = DateUtil.parse(vo.getStartTime());
                Date date2 = DateUtil.parse(vo.getEndTime());
                //统计维度：时间类型 1 小时 2 日
                if (vo.getDateType().equals("1")) {
                    //相差小时
                    long betweenHour = DateUtil.between(date1, date2, DateUnit.HOUR) + 24;
                    for (int i = 0; i < betweenHour; i++) {
                        nameList.add(i + "点");
                        valList.add("0");
                    }
                } else {
                    //相差天
                    List<String> weekList = new ArrayList<>();
                    weekList.add("周一");
                    weekList.add("周二");
                    weekList.add("周三");
                    weekList.add("周四");
                    weekList.add("周五");
                    weekList.add("周六");
                    weekList.add("周日");
                    long betweenDay = DateUtil.between(date1, date2, DateUnit.DAY) + 1;
                    for (int i = 0; i < betweenDay; i++) {
                        //查询条件：时间类型 0任意时间 1 年度 2 月度 3 周 4 日
                        if (vo.getTimeType().equals("0")) {
                            nameList.add("第" + (i + 1) + "天");
                        } else if (vo.getTimeType().equals("1")) {
                            nameList.add(new DateTime(vo.getStartTime()).plusDays(i).toString("MM-dd"));
                        } else if (vo.getTimeType().equals("2")) {
                            nameList.add(new DateTime(vo.getStartTime()).plusDays(i).toString("dd日"));
                        } else if (vo.getTimeType().equals("3")) {
                            nameList.add(weekList.get(i));
                        } else if (vo.getTimeType().equals("4")) {
                            nameList.add(new DateTime(vo.getStartTime()).plusDays(i).toString("yyyy-MM-dd"));
                        }
                        valList.add("0");
                    }
                }
                smartEnergyReportVo.setNameList(nameList);
                smartEnergyReportVo.setValList(valList);
            } else {
                smartEnergyReportVo.setNameList(nameList);
                smartEnergyReportVo.setValList(valList);
            }
        }
    }

    /**
     * 多维分析
     * sql语句
     */
    public SmartEnergyReportVo multidimensionalQuery(SmartEnergyReportVo vo) {
        String sql = "select spread(val) from datasheet" +
                " where val>0";
        if (StringUtils.isNotEmpty(vo.getStartTime())) {
            sql += " and time>='" + new DateTime(vo.getStartTime()).toString("yyyy-MM-dd 00:00:00") + "'";
        }
        if (StringUtils.isNotEmpty(vo.getEndTime())) {
            sql += " and time<'" + new DateTime(vo.getEndTime()).plusDays(1).toString("yyyy-MM-dd 00:00:00") + "'";
        }
//        if (StringUtils.isNotNull(vo.getDeviceSubitemId())) {
//            sql += " and subitem='" + vo.getDeviceSubitemId() + "'";
//        }
//        if (StringUtils.isNotNull(vo.getBuildId())) {
//            sql += " and buildId='" + vo.getBuildId() + "'";
//        }
//        if (StringUtils.isNotNull(vo.getFloorId())) {
//            sql += " and floorId='" + vo.getFloorId() + "'";
//        }
//        if (StringUtils.isNotNull(vo.getAreaId())) {
//            sql += " and areaId='" + vo.getAreaId() + "'";
//        }
        if (StringUtils.isNotNull(vo.getDeviceTypeId())) {
            sql += " and deviceType='" + vo.getDeviceTypeId() + "'";
        }
        if (StringUtils.isNotNull(vo.getEnergyType())) {
            //能源类型 1 电 2 水
            if (vo.getEnergyType().equals("1")) {
                sql += " and (channelId = 'ENERGY_TOTAL')";
            } else {
                sql += " and (channelId = 'WATER_L')";
            }
        }
        //采集设备id
        if (StringUtils.isNotNull(vo.getCollectIds()) && vo.getCollectIds().size() > 0) {
            String deviceIds = "~/";
            for (int i = 0; i < vo.getCollectIds().size(); i++) {
                deviceIds = deviceIds + "^" + vo.getCollectIds().get(i) + "$|";
            }
            deviceIds = deviceIds.substring(0, deviceIds.lastIndexOf("|"));
            deviceIds += "/";
            sql += " and deviceCollectId=" + deviceIds;
        }
        //统计维度：时间类型 1 小时 2 日
        if (StringUtils.isNotNull(vo.getDateType())) {
            if (vo.getDateType().equals("1")) {
                sql += " group by time(1h),deviceCollectId";
            } else {
                sql += " group by time(1d),deviceCollectId";
            }
        }
//        sql = "select spread(val) from datasheet where val>0  and time>='2023-07-18 00:00:00' and time<'2023-08-18 00:00:00'  and (channelId = 'Energy_ALL')  group by time(1d),deviceCollectId";
        QueryResult query = influxdbService.query(sql);
        List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);

        List<String> nameList = new ArrayList<>();
        List<String> valList = new ArrayList<>();
        List<String> weekList = new ArrayList<>();
        weekList.add("周一");
        weekList.add("周二");
        weekList.add("周三");
        weekList.add("周四");
        weekList.add("周五");
        weekList.add("周六");
        weekList.add("周日");
        DecimalFormat df = new DecimalFormat("0.00");
        double sum = 0d;

        List<WaterPrice> waterPrice = waterPriceService.getWaterPrice();

        Map<String, Double> result = new LinkedHashMap<>();
        for (Map<String, Object> map : maps) {
            if (result.containsKey(map.get("time"))) {
                result.put((String) map.get("time"), result.get(map.get("time")) + (map.get("spread") == null ? 0d : (Double) map.get("spread")));
            } else {
                result.put((String) map.get("time"), map.get("spread") == null ? 0d : (Double) map.get("spread"));
            }
        }

        if (result.size() > 0) {
            int i = 0;
            for (String key : result.keySet()) {
                if (StringUtils.isNotNull(result.get(key))) {
                    //能源类型 1 电 2 水
                    if (vo.getEnergyType().equals("1")) {
                        //统计维度：数据类型 1 用量 2 碳排放 3 标准煤 4 费用
                        if (vo.getDataType().equals("1")) {
                            String s = df.format(result.get(key));
                            Double d = Double.parseDouble(s);
                            sum += d;
                            valList.add(df.format(d));
                        } else if (vo.getDataType().equals("2")) {
                            String s = df.format(result.get(key));
                            //总碳排放量（吨）=用电量(度)*0.785/1000
                            Double d = Double.parseDouble(s);
                            double toc = d * 0.785 / 1000;
                            sum += toc;
                            valList.add(df.format(toc));
                        } else if (vo.getDataType().equals("3")) {
                            String s = df.format(result.get(key));
                            Double d = Double.parseDouble(s);
                            //当量值：总标煤（tce）=总用电量（万度）*1.229
                            double tce = d / 10000 * 1.229;
                            sum += tce;
                            valList.add(df.format(tce));
                        } else if (vo.getDataType().equals("4")) {
                            String s = df.format(result.get(key));
                            Double d = Double.parseDouble(s);
                            //费用
                            double money = d * Double.parseDouble("1");
                            sum += money;
                            valList.add(df.format(money));
                        }
                    } else {
                        //统计维度：数据类型 1 用量 2 碳排放 3 标准煤 4 费用
                        if (vo.getDataType().equals("1")) {
                            String s = df.format(result.get(key));
                            Double d = Double.parseDouble(s);
                            sum += d;
                            valList.add(df.format(d));
                        } else if (vo.getDataType().equals("2")) {
                            String s = df.format(result.get(key));
                            //总碳排放量（吨）=用电量(度)*0.785/1000
                            Double d = Double.parseDouble(s);
                            double toc = d * 0.785 / 1000;
                            sum += toc;
                            valList.add(df.format(toc));
                        } else if (vo.getDataType().equals("3")) {
                            String s = df.format(result.get(key));
                            Double d = Double.parseDouble(s);
                            //当量值：总标煤（tce）=总用电量（万度）*1.229
                            double tce = d / 10000 * 1.229;
                            sum += tce;
                            valList.add(df.format(tce));
                        } else if (vo.getDataType().equals("4")) {
                            String s = df.format(result.get(key));
                            Double d = Double.parseDouble(s);
                            //费用
                            double money = d * Double.parseDouble(waterPrice.get(0).getPrice());
                            sum += money;
                            valList.add(df.format(money));
                        }
                    }
                } else {
                    valList.add("0");
                }

                //统计维度：时间类型 1 小时 2 日
                if (StringUtils.isNotEmpty(vo.getDateType())) {
                    if (vo.getDateType().equals("1")) {
                        nameList.add(i + "点");
                    } else {
                        //查询条件：时间类型 0任意时间 1 年度 2 月度 3 周 4 日
                        if (vo.getTimeType().equals("0")) {
                            nameList.add("第" + (i + 1) + "天");
                        } else if (vo.getTimeType().equals("1")) {
                            nameList.add(new DateTime(key).toString("MM-dd"));
                        } else if (vo.getTimeType().equals("2")) {
                            nameList.add(new DateTime(key).toString("dd日"));
                        } else if (vo.getTimeType().equals("3")) {
                            nameList.add(weekList.get(i));
                        } else if (vo.getTimeType().equals("4")) {
                            nameList.add(new DateTime(key).toString("yyyy-MM-dd"));
                        }
                    }
                }
                i++;
            }
        }

        SmartEnergyReportVo reportVo = new SmartEnergyReportVo();
        reportVo.setValList(valList);
        reportVo.setNameList(nameList);
        reportVo.setVal(df.format(sum));
        return reportVo;
    }

    /**
     * 电能质量-三项不平衡统计
     * 4-6%
     * 6-8%
     * 8-10%
     * 10-12%
     * 12%以上
     * 正常区间 0-4%
     */
    public JSONObject tripartiteImbalanceReport(SmartEnergyReportVo vo) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        //查询条件：时间类型 0任意时间 1 年度 2 月度 3 周 4 日
        if (StringUtils.isNotNull(vo.getTimeType())) {
            if (vo.getTimeType().equals("1")) {
                // 年度
                vo.setStartTime(new DateTime(vo.getStartTime()).toString("yyyy-01-01 00:00:00"));
                vo.setEndTime(new DateTime(vo.getEndTime()).plusYears(1).toString("yyyy-01-01 00:00:00"));
            } else if (vo.getTimeType().equals("2")) {
                // 月度
                vo.setStartTime(new DateTime(vo.getStartTime()).toString("yyyy-MM-01 00:00:00"));
                vo.setEndTime(new DateTime(vo.getEndTime()).plusMonths(1).toString("yyyy-MM-01 00:00:00"));
            } else if (vo.getTimeType().equals("4")) {
                // 天
                vo.setStartTime(new DateTime(vo.getStartTime()).toString("yyyy-MM-dd 00:00:00"));
                vo.setEndTime(new DateTime(vo.getEndTime()).plusDays(1).toString("yyyy-MM-dd 00:00:00"));
            }
        }
        vo.setMinVal("0.0");
        vo.setMaxVal("0.04");
        String sum = this.tripartiteImbalanceQuery(vo);
        vo.setMinVal("0.04");
        vo.setMaxVal("0.06");
        String sum2 = this.tripartiteImbalanceQuery(vo);
        vo.setMinVal("0.06");
        vo.setMaxVal("0.08");
        String sum3 = this.tripartiteImbalanceQuery(vo);
        vo.setMinVal("0.08");
        vo.setMaxVal("0.10");
        String sum4 = this.tripartiteImbalanceQuery(vo);
        vo.setMinVal("0.10");
        vo.setMaxVal("0.12");
        String sum5 = this.tripartiteImbalanceQuery(vo);
        vo.setMinVal("0.12");
        vo.setMaxVal("");
        String sum6 = this.tripartiteImbalanceQuery(vo);

        DecimalFormat df = new DecimalFormat("0.00");
        double total = Double.parseDouble(sum) + Double.parseDouble(sum2) + Double.parseDouble(sum3) + Double.parseDouble(sum4) + Double.parseDouble(sum5) + Double.parseDouble(sum6);
        double zb = 0d;
        double zb2 = 0d;
        double zb3 = 0d;
        double zb4 = 0d;
        double zb5 = 0d;
        double zb6 = 0d;
        if (total > 0d) {
            zb = Double.parseDouble(sum) / total * 100d;
            zb2 = Double.parseDouble(sum2) / total * 100d;
            zb3 = Double.parseDouble(sum3) / total * 100d;
            zb4 = Double.parseDouble(sum4) / total * 100d;
            zb5 = Double.parseDouble(sum5) / total * 100d;
            zb6 = Double.parseDouble(sum6) / total * 100d;
        }

        Map<String, String> map = new HashMap<>();
        map.put("name", "4%以下(正常区间)");
        map.put("value", sum);
        map.put("zb", df.format(zb) + "%");
        jsonArray.add(map);

        Map<String, String> map2 = new HashMap<>();
        map2.put("name", "4-6%");
        map2.put("value", sum2);
        map2.put("zb", df.format(zb2) + "%");
        jsonArray.add(map2);

        Map<String, String> map3 = new HashMap<>();
        map3.put("name", "6-8%");
        map3.put("value", sum3);
        map3.put("zb", df.format(zb3) + "%");
        jsonArray.add(map3);

        Map<String, String> map4 = new HashMap<>();
        map4.put("name", "8-10%");
        map4.put("value", sum4);
        map4.put("zb", df.format(zb4) + "%");
        jsonArray.add(map4);

        Map<String, String> map5 = new HashMap<>();
        map5.put("name", "10-12%");
        map5.put("value", sum5);
        map5.put("zb", df.format(zb5) + "%");
        jsonArray.add(map5);

        Map<String, String> map6 = new HashMap<>();
        map6.put("name", "12%以上");
        map6.put("value", sum6);
        map6.put("zb", df.format(zb6) + "%");
        jsonArray.add(map6);

        jsonObject.put("vo", jsonArray);
        return jsonObject;
    }

    /**
     * 电能质量-三项不平衡统计
     * sql语句
     */
    public String tripartiteImbalanceQuery(SmartEnergyReportVo vo) {
        String sql = "select count(val) from tripartiteImbalance" +
                " where 1=1";
        if (StringUtils.isNotEmpty(vo.getStartTime())) {
            sql += " and time>='" + vo.getStartTime() + "'";
        }
        if (StringUtils.isNotEmpty(vo.getEndTime())) {
            sql += " and time<'" + vo.getEndTime() + "'";
        }
        if (StringUtils.isNotEmpty(vo.getDeviceType())) {
            sql += " and type='" + vo.getDeviceType() + "'";
        }
        if (StringUtils.isNotNull(vo.getDeviceId())) {
            sql += " and deviceId='" + vo.getDeviceId() + "'";
        }
        if (StringUtils.isNotEmpty(vo.getMinVal())) {
            sql += " and val>=" + vo.getMinVal();
        }
        if (StringUtils.isNotEmpty(vo.getMaxVal())) {
            sql += " and val<" + vo.getMaxVal();
        }
        QueryResult query = influxdbService.query(sql);
        List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);
        DecimalFormat df = new DecimalFormat("0");
        double sum = 0;
        if (maps.size() > 0) {
            for (int i = 0; i < maps.size(); i++) {
                if (StringUtils.isNotNull(maps.get(i).get("count"))) {
                    double s = (double) maps.get(i).get("count");
                    sum += s;
                }
            }
        }
        return df.format(sum);
    }

    /**
     * 三项不平衡
     * 数据解析入库，根据公式计算三项不平衡值存入新表
     */
    public String onImbalanceCount(List<Double> list) {
        if (list.size() != 3) {
            return null;
        }
        String val = "0";
//        System.out.println(list.toString());
        Double max = Collections.max(list);
//        System.out.println("集合中最大值为：" + max);
        Double min = Collections.min(list);
//        System.out.println("集合中最小值为：" + min);

        //(最大值-最小值)/最大值
        if (max > 0d && min > 0d) {
            BigDecimal bd = new BigDecimal(max).subtract(new BigDecimal(min)).divide(new BigDecimal(max), 2, BigDecimal.ROUND_HALF_UP);
            val = bd.toString();
        }
        return val;
    }

    /**
     * 插入三项不平衡数据
     */
    public void insertTripartiteImbalance(String deviceId, String type, float val) {
        //tags 类型、id等需要频繁作为条件的字段，类似关系型数据库需要加索引的字段
        //必须为字符串
        Map<String, String> tagsMap = new HashMap<>();
        tagsMap.put("deviceId", deviceId);//设备id
        tagsMap.put("type", type);//类型 1电压 2电流
        //fields 监测值
        Map<String, Object> fieldsMap = new HashMap<>();
        fieldsMap.put("val", val);
        Date date = new Date();
        long time = date.getTime() + 28800000;
        //不传time
        influxdbService.insert("tripartiteImbalance", time, tagsMap, fieldsMap);
    }

}
