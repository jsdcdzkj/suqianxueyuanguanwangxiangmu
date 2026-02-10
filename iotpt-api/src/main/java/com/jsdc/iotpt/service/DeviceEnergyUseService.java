package com.jsdc.iotpt.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.*;
import com.jsdc.iotpt.model.*;
import com.jsdc.iotpt.model.ConfigDeviceSubitemValue;
import com.jsdc.iotpt.service.influxDB.InfluxdbService;
import com.jsdc.iotpt.vo.*;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.poi.excel.sax.ElementName.c;

@Service
@Transactional
public class DeviceEnergyUseService extends BaseService<DeviceEnergyUse> {

    @Autowired
    private DeviceEnergyUseMapper deviceEnergyUseMapper;

    @Autowired
    private InfluxdbService influxdbService;

    @Autowired
    private ConfigDeviceSubitemMapper configDeviceSubitemMapper ;

    @Autowired
    private ConfigDeviceSubitemService configDeviceSubitemService ;
    @Autowired
    private ConfigDeviceSubitemValueMapper configDeviceSubitemValueMapper;


    @Autowired
    private DeviceCollectMapper deviceCollectMapper;


    @Autowired
    private SubmiteValueMapper submiteValueMapper;

    public List<DeviceEnergyUse> getList(DeviceEnergyUse deviceEnergyUse) {

        //查有无当前数据  没有新增
        QueryWrapper<DeviceEnergyUse> queryWrapper = new QueryWrapper<>();
        queryWrapper.apply("SUBSTR(searchDate,0,4) ='"+deviceEnergyUse.getYear()+"'") ;
        List<DeviceEnergyUse> deviceEnergyUseList = deviceEnergyUseMapper.selectList(queryWrapper);
        if(CollectionUtils.isEmpty(deviceEnergyUseList)){
            DeviceEnergyUse deviceEnergyUse1 =null ;
            for(int i= 1 ;i<13 ;i++){
                deviceEnergyUse1 = new DeviceEnergyUse();

                //todo 产品需求 固定0.1
                deviceEnergyUse1.setControlValue("0.1");
                deviceEnergyUse1.setRatedValue("0");
                if(i<10){
                    deviceEnergyUse1.setSearchDate(deviceEnergyUse.getYear()+"-0"+i);
                }else {
                    deviceEnergyUse1.setSearchDate(deviceEnergyUse.getYear()+"-"+i);
                }
                deviceEnergyUseMapper.insert(deviceEnergyUse1) ;
            }
        }
        //查电量
        // 定义日期格式
        QueryWrapper<DeviceEnergyUse> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.apply("SUBSTR(searchDate,0,4) ='"+deviceEnergyUse.getYear()+"'") ;
        List<DeviceEnergyUse> deviceEnergyUseList2 = deviceEnergyUseMapper.selectList(queryWrapper2);
        for(DeviceEnergyUse d:deviceEnergyUseList2){
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // 给定的月份字符串
            String monthString = d.getSearchDate();

            // 解析给定的月份字符串
            YearMonth yearMonth = YearMonth.parse(monthString, inputFormatter);

            // 获取该月的第一天
            LocalDate firstDay = yearMonth.atDay(1);
            LocalDateTime firstDayDateTime = firstDay.atStartOfDay();

            // 获取该月的最后一天
            LocalDate lastDay = yearMonth.atEndOfMonth();
            LocalDateTime lastDayDateTime = lastDay.atTime(23, 59, 59);



            String sql = "select sum(totalPower) as totalPower  from (select last(val)-first(val) as totalPower from datasheet WHERE deviceCollectId=~/^200364|^200363$/  AND channelId='ENERGY_TOTAL'  and time <='"+lastDayDateTime.format(outputFormatter)+"' and time>='"+firstDayDateTime.format(outputFormatter)+"'  group by deviceCollectId)" ;



            // 时序数据库解析
            QueryResult query = influxdbService.query(sql);
            //解析列表类查询
            List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);

            if(CollectionUtils.isNotEmpty(maps)){
                Double val = (Double) maps.get(0).get("totalPower");
                d.setVal(val);
            }else {
                d.setVal(0.00);
            }




        }


        return deviceEnergyUseList2 ;

    }


public String theSamePeriodLastYear(DeviceEnergyUse deviceEnergyUse){
        String val = "";
    DeviceEnergyUse deviceEnergyUse1 = deviceEnergyUseMapper.selectById(deviceEnergyUse.getId());


    // 指定日期的格式
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

    // 当前指定的日期
    String currentYearMonth = deviceEnergyUse1.getSearchDate();

    // 解析字符串为LocalDate对象
    LocalDate date = LocalDate.parse(currentYearMonth + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    // 减去一年
    LocalDate lastYearDate = date.minusYears(1);

    // 格式化为“yyyy-MM”格式
    String formattedLastYearDate = lastYearDate.format(formatter);

    // 输出结果
    System.out.println("去年同一月份的日期: " + formattedLastYearDate);

    QueryWrapper<DeviceEnergyUse> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("searchDate", formattedLastYearDate);
    DeviceEnergyUse deviceEnergyUse2 = deviceEnergyUseMapper.selectOne(queryWrapper);
    if(null != deviceEnergyUse2){
        val = deviceEnergyUse2.getRatedValue() ;
    }
        return val;
    }


    /**
     *
     *用能异常 数据展示
     * @author wzn
     * @date 2024/11/27 11:48
     */
    public EnergyUseAnomalyVo info(){
        EnergyUseAnomalyVo energyUseAnomalyVo = new EnergyUseAnomalyVo();
        QueryWrapper<DeviceEnergyUse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("searchDate", DateUtil.format(new Date(), "yyyy-MM"));
        DeviceEnergyUse deviceEnergyUse = deviceEnergyUseMapper.selectOne(queryWrapper);
        energyUseAnomalyVo.setControlValue(deviceEnergyUse.getControlValue()) ;

        if(StringUtils.isNotBlank(deviceEnergyUse.getControlValue())){
            //定额
            if(StringUtils.isNotBlank(deviceEnergyUse.getRatedValue())){

                Double ratedValue = Double.parseDouble(deviceEnergyUse.getRatedValue());
                //实际用电
                Double realVal = getVal(DateUtil.format(new Date(), "yyyy-MM")) ;

//                String result = calculateRatio(realVal, ratedValue);

                double result = 0.00 ;
                if(ratedValue !=0 && ratedValue !=null){
                     result = (realVal / ratedValue) * 100;
                }else {
                    result = realVal;
                }

                BigDecimal bd = new BigDecimal(Double.toString(result));
                bd = bd.setScale(2, RoundingMode.HALF_UP);

                // 转换为字符串
                String formattedNumber = bd.toString();
                energyUseAnomalyVo.setRealcontrolValue(formattedNumber+"");
                Double controlValue =   0.1 ;
                if (realVal <= ratedValue) {
                    energyUseAnomalyVo.setStatus("0");
                } else if (realVal >= ratedValue && realVal <= ratedValue * (controlValue+1)) {
                    energyUseAnomalyVo.setStatus("1");
                } else {
                    energyUseAnomalyVo.setStatus("2");
                    // 可以在这里添加更多的警告处理逻辑，比如发送邮件或者短信通知
                }
            }
        }

        DeviceEnergyUse deviceEnergyUse2 = new DeviceEnergyUse() ;
        deviceEnergyUse2.setYear(DateUtil.format(new Date(), "yyyy"));
        List<DeviceEnergyUse> deviceEnergyUseList = getList(deviceEnergyUse2 ) ;

        Double totalRatedValue = 0.00 ;
        Double totalRealVal = 0.00 ;
        for(DeviceEnergyUse dd:deviceEnergyUseList){
            totalRatedValue+=Double.parseDouble(dd.getRatedValue());
            totalRealVal+=dd.getVal() ;
            energyUseAnomalyVo.setTotalAnnualQuota(totalRatedValue+"");
            energyUseAnomalyVo.setActualEnergyConsumption(totalRealVal+"");
        }
        return energyUseAnomalyVo;
    }

    public static String calculateRatio(double part, double whole) {


        // 计算比例
        BigDecimal ratio = BigDecimal.valueOf(part).divide(BigDecimal.valueOf(whole), 4, RoundingMode.HALF_UP);
        // 转换为百分比
        BigDecimal percentage = ratio.multiply(BigDecimal.valueOf(100));
        BigDecimal num1 = new BigDecimal("100");
       ;
        // 格式化为保留两位小数的字符串
        return  percentage.subtract(num1).setScale(2, RoundingMode.HALF_UP).toString() + "%";
    }

    public Double getVal(String time) {
        double val = 0.00;
        // 给定的月份字符串
        String monthString = time;
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 解析给定的月份字符串
        YearMonth yearMonth = YearMonth.parse(monthString, inputFormatter);

        // 获取该月的第一天
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDateTime firstDayDateTime = firstDay.atStartOfDay();

        // 获取该月的最后一天
        LocalDate lastDay = yearMonth.atEndOfMonth();
        LocalDateTime lastDayDateTime = lastDay.atTime(23, 59, 59);


        String sql = "select last(val)-first(val) as totalPower from datasheet WHERE deviceCollectId=~/^200364|^200363$/  AND channelId='ENERGY_TOTAL'  and time <='" + lastDayDateTime.format(outputFormatter) + "' and time>='" + firstDayDateTime.format(outputFormatter) + "'";


        // 时序数据库解析
        QueryResult query = influxdbService.query(sql);
        //解析列表类查询
        List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);

        if (CollectionUtils.isNotEmpty(maps)) {
            val = (Double) maps.get(0).get("totalPower");
        }
        return val ;
    }


    public EnergyUseAnomalyVo currentAveraging(){
        // 获取当前年份
        int currentYear = LocalDate.now().getYear();

        // 今年的第一天 00:00:00
        LocalDateTime firstDayOfYear = LocalDate.of(currentYear, 1, 1).atStartOfDay();
        System.out.println("今年的第一天: " + formatDateTime(firstDayOfYear));

        // 今年的最后一天 23:59:59
        LocalDateTime lastDayOfYear = LocalDate.of(currentYear, 1, 1)
                .with(TemporalAdjusters.lastDayOfYear())
                .atTime(23, 59, 59);
        System.out.println("今年的最后一天: " + formatDateTime(lastDayOfYear));
        return null ;
    }

    private static String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }


    /**
     *
     *空载电流平均值
     * @author wzn
     * @date 2024/11/28 14:28
     */
    public EnergyUseAnomalyVo noLoadCurrent(){
        EnergyUseAnomalyVo energyUseAnomalyVo  = new EnergyUseAnomalyVo();
        List<String> timeList = generateYearDates();
        Double I_A =0.00 ;
        for (String time : timeList) {
            String sql = "select mean(val) as totalPower from datasheet WHERE deviceCollectId=~/^200364|^200363$/  AND channelId='I_A'  and time <='" + time + " 05:00:00' and time>='" + time + " 00:00:00'";

            // 时序数据库解析
            QueryResult query = influxdbService.query(sql);
            //解析列表类查询
            List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);

            if (CollectionUtils.isNotEmpty(maps)) {
                I_A += (Double) maps.get(0).get("totalPower");
            }
        }
        I_A = I_A/timeList.size() ;
         I_A = Math.round(I_A * 100.0) / 100.0;
        energyUseAnomalyVo.setI_A(I_A);


        Double I_B =0.00 ;
        for (String time : timeList) {
            String sql = "select mean(val) as totalPower from datasheet WHERE deviceCollectId=~/^200364|^200363$/  AND channelId='I_B'  and time <='" + time + " 05:00:00' and time>='" + time + " 00:00:00'";

            // 时序数据库解析
            QueryResult query = influxdbService.query(sql);
            //解析列表类查询
            List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);

            if (CollectionUtils.isNotEmpty(maps)) {
                I_B += (Double) maps.get(0).get("totalPower");
            }
        }
        I_B = I_B/timeList.size() ;
        I_B = Math.round(I_B * 100.0) / 100.0;
        energyUseAnomalyVo.setI_B(I_B);



        Double I_C =0.00 ;
        for (String time : timeList) {
            String sql = "select mean(val) as totalPower from datasheet WHERE deviceCollectId=~/^200364|^200363$/  AND channelId='I_C'  and time <='" + time + " 05:00:00' and time>='" + time + " 00:00:00'";

            // 时序数据库解析
            QueryResult query = influxdbService.query(sql);
            //解析列表类查询
            List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);

            if (CollectionUtils.isNotEmpty(maps)) {
                I_C += (Double) maps.get(0).get("totalPower");
            }
        }
        I_C = I_C/timeList.size() ;
        I_C = Math.round(I_C * 100.0) / 100.0;
        energyUseAnomalyVo.setI_C(I_C);
        return energyUseAnomalyVo ;
    }


    private static List<String> generateYearDates() {
        // 定义日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 获取当前年份的第一天
        LocalDate startOfYear = LocalDate.now().withDayOfYear(1);

        // 获取当前年份的最后一天
        LocalDate endOfYear = startOfYear.plusYears(1).minusDays(1);

        // 创建结果列表
        List<String> result = new ArrayList<>();

        // 遍历从年初到年末的所有日期
        while (!startOfYear.isAfter(endOfYear)) {
            result.add(startOfYear.format(formatter));
            startOfYear = startOfYear.plusDays(1);  // 增加一天
        }

        return result;
    }



    /**
     *
     *每月平均电流
     * @author wzn
     * @date 2024/11/29 17:02
     */
    public JSONObject  averageMonthlyCurrent(){
        JSONObject jsonObject = new JSONObject();
        List<String> months = getYearMonths();
        jsonObject.put("xData", months);
        List<String> I_A = new ArrayList<>() ;
        List<String> I_B = new ArrayList<>() ;
        List<String> I_C = new ArrayList<>() ;
        for (String month : months) {
            LocalDate firstDay, lastDay;

            try {
                // 获取月份的第一天和最后一天
                firstDay = getFirstDayOfMonth(month);
                lastDay = getLastDayOfMonth(month);

                String sql = "select mean(val) as totalPower from datasheet WHERE deviceCollectId=~/^200364|^200363$/  AND channelId='I_A'  and time <='" + lastDay + " 23:59:59' and time>='" + firstDay + " 00:00:00'";

                // 时序数据库解析
                QueryResult query = influxdbService.query(sql);
                //解析列表类查询
                List<Map<String, Object>> maps = influxdbService.queryResultProcess(query);

                if (CollectionUtils.isNotEmpty(maps)) {
                    I_A.add(maps.get(0).get("totalPower")+"") ;
                }else {
                    I_A.add("0") ;
                }



                String sql2 = "select mean(val) as totalPower from datasheet WHERE deviceCollectId=~/^200364|^200363$/  AND channelId='I_B'  and time <='" + lastDay + " 23:59:59' and time>='" + firstDay + " 00:00:00'";

                // 时序数据库解析
                QueryResult query2 = influxdbService.query(sql2);
                //解析列表类查询
                List<Map<String, Object>> maps2 = influxdbService.queryResultProcess(query2);

                if (CollectionUtils.isNotEmpty(maps2)) {
                    I_B.add(maps2.get(0).get("totalPower")+"") ;
                }else {
                    I_B.add("0") ;
                }


                String sql3 = "select mean(val) as totalPower from datasheet WHERE deviceCollectId=~/^200364|^200363$/  AND channelId='I_C'  and time <='" + lastDay + " 23:59:59' and time>='" + firstDay + " 00:00:00'";

                // 时序数据库解析
                QueryResult query3 = influxdbService.query(sql3);
                //解析列表类查询
                List<Map<String, Object>> maps3 = influxdbService.queryResultProcess(query3);

                if (CollectionUtils.isNotEmpty(maps3)) {
                    I_C.add(maps3.get(0).get("totalPower")+"") ;
                }else {
                    I_C.add("0") ;
                }
            } catch (Exception e) {
                System.err.println("Invalid date format. Please use 'yyyy-MM'.");
            }
        }
        jsonObject.put("I_A", I_A);
        jsonObject.put("I_B", I_B);
        jsonObject.put("I_C", I_C);
        return jsonObject ;
    }


    public static List<String> getYearMonths() {
        List<String> months = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        // 设置日历为当前年的1月1日
        calendar.set(year, Calendar.JANUARY, 1);

        // 循环12次，每次增加一个月
        for (int i = 0; i < 12; i++) {
            // 获取当前月份的年份和月份
            int currentMonth = calendar.get(Calendar.MONTH);
            String month = String.format("%04d-%02d", year, currentMonth + 1); // 月份从0开始，所以需要+1
            months.add(month);
            // 增加一个月
            calendar.add(Calendar.MONTH, 1);
        }

        return months;
    }


    public static LocalDate getFirstDayOfMonth(String month) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth yearMonth = YearMonth.parse(month, formatter);
        return yearMonth.atDay(1);  // 获取该月的第一天
    }

    public static LocalDate getLastDayOfMonth(String month) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth yearMonth = YearMonth.parse(month, formatter);
        return yearMonth.atEndOfMonth();  // 获取该月的最后一天
    }


    public List<ConfigDeviceSubitem>  getSubValue(String year) {
        List<ConfigDeviceSubitem> configDeviceSubitems = configDeviceSubitemMapper.existenceOrNot(year) ;
        if(CollectionUtils.isNotEmpty(configDeviceSubitems)){
            //往子表插数据
            List<ConfigDeviceSubitem> configDeviceSubitemList = configDeviceSubitemService.getList(new ConfigDeviceSubitemVo()) ;
            for(ConfigDeviceSubitem c:configDeviceSubitemList){
                ConfigDeviceSubitemValue configDeviceSubitemValue = new ConfigDeviceSubitemValue();
                configDeviceSubitemValue.setSubitemId(c.getId()+"");
                configDeviceSubitemValue.setYear(year);
                configDeviceSubitemValue.setRatedValue("0");
                configDeviceSubitemValueMapper.insert(configDeviceSubitemValue);
            }
        }

        //查实际能耗值
        List<ConfigDeviceSubitem> configDeviceSubitems2 = configDeviceSubitemMapper.getSubValue(year) ;


        // 获取2024年的第一天
        LocalDate firstDayOfYear = LocalDate.of(Integer.parseInt(year), 1, 1);

        // 获取2024年的最后一天
        LocalDate lastDayOfYear = LocalDate.of(Integer.parseInt(year), 12, 31);
        // 如果需要确保是该年的最后一天，可以使用TemporalAdjusters.lastDayOfYear()
        lastDayOfYear = lastDayOfYear.with(TemporalAdjusters.lastDayOfYear());



        for(ConfigDeviceSubitem c:configDeviceSubitems2){

            DataSheetVo bean = new DataSheetVo();
            bean.setEnergyType(1);
            bean.setSubitem(c.getId());
            bean.setIsTotalDevice(1);//查询是总设备的数据
            List<DeviceCollect> collects = deviceCollectMapper.getDeviceByArea(bean);//获取设备列表
            if (collects.isEmpty()) {// 无采集设备
//                map.put(a.getSubitemName(), 0d);
//                continue;
                bean.setIsTotalDevice(0);//查询不是总设备的数据
                collects= deviceCollectMapper.getDeviceByArea(bean);
            }
            List<String> deviceIds = collects.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
            String sql3 = " select sum(totalPower) as totalPower from (select last(val)-first(val) as totalPower from datasheet WHERE subitem='"+c.getId()+"'  AND channelId='ENERGY_TOTAL'  and time <='" + lastDayOfYear + " 23:59:59' and time>='" + firstDayOfYear +" 00:00:00"+ "' and deviceCollectId=~/" + influxdbInToString(deviceIds) + "/ group by deviceCollectId) ";

            // 时序数据库解析
            QueryResult query3 = influxdbService.query(sql3);
            //解析列表类查询
            List<Map<String, Object>> maps3 = influxdbService.queryResultProcess(query3);

            if (CollectionUtils.isNotEmpty(maps3)) {
                c.setActualValue(String.format("%.2f", Double.parseDouble( maps3.get(0).get("totalPower")+"")));
            }else {
                c.setActualValue("0");
            }

            //去年定额值
            int currentYear = Integer.parseInt(year); // 你可以从用户输入、配置文件或其他地方获取这个值

            // 获取去年的年份
            int previousYear = currentYear - 1;

            QueryWrapper<ConfigDeviceSubitemValue> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("subitemId", c.getId());
            queryWrapper.eq("year", previousYear);
            ConfigDeviceSubitemValue configDeviceSubitemValue =  configDeviceSubitemValueMapper.selectOne(queryWrapper);
            if(null != configDeviceSubitemValue){
                //去年定额值

                // 示例数据
                if("0".equals(configDeviceSubitemValue.getRatedValue())){
                    c.setLastRatedValue("0");

                    c.setYearOnYearGrowthRate(c.getRatedValue()+"%");
                }else {
                    BigDecimal thisYearSales = new BigDecimal(c.getRatedValue());
                    BigDecimal lastYearSales = new BigDecimal(configDeviceSubitemValue.getRatedValue() );

                    // 计算同比增长率
                    String growth = calculateYoYGrowth(thisYearSales, lastYearSales);
                    c.setLastRatedValue(configDeviceSubitemValue.getRatedValue());
                    c.setYearOnYearGrowthRate(growth);
                }

            }else {
                c.setLastRatedValue("0");
                c.setYearOnYearGrowthRate(c.getRatedValue()+"%");
            }

            if(StringUtils.isNotEmpty( c.getRatedValue())){
                //查有无当前数据  没有新增
                QueryWrapper<SubmiteValue> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.eq("subitemId", c.getId()) ;
                queryWrapper2.apply("SUBSTR(searchDate,0,4) ='"+year+"'") ;
                List<SubmiteValue> submiteValueList = submiteValueMapper.selectList(queryWrapper2);
                Double sum = 0.00 ;
                if(CollectionUtils.isNotEmpty(submiteValueList)){
                    for(SubmiteValue s:submiteValueList){
                        sum+= Double.parseDouble(s.getRatedValue()) ;
                    }

                }

                Double remainingValue = Double.parseDouble(c.getRatedValue())- sum ;
                c.setResidualCredit(remainingValue+"");
            }else {
                c.setResidualCredit("0");
            }

//            SubmiteValue submiteValue = new SubmiteValue();
//            submiteValue.setSubitemId(c.getId()+"");
//            submiteValue.setYear(year);
//            List<SubmiteValue> submiteValueList = this.getSubmitMonth(submiteValue) ;
//            c.setChild(submiteValueList);
        }



        return  configDeviceSubitems2;
    }


    // 计算同比增长率
    public static String calculateYoYGrowth(BigDecimal currentPeriodValue, BigDecimal previousPeriodValue) {
        if (previousPeriodValue.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Previous period value cannot be zero for growth calculation.");
        }

        // 计算增长量
        BigDecimal growthAmount = currentPeriodValue.subtract(previousPeriodValue);

        // 计算增长率
        BigDecimal growthRate = growthAmount.divide(previousPeriodValue, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));

        // 格式化输出
        return formatGrowthRate(growthRate);
    }

    // 格式化增长率
    private static String formatGrowthRate(BigDecimal growthRate) {
        // 使用正负号来表示增长或减少
        String sign = growthRate.signum() == -1 ? "" : "+";
        // 格式化为两位小数
        return sign + growthRate.setScale(2, RoundingMode.HALF_UP).toString() + "%";
    }

    //组装多选sql条件
    public String influxdbInToString(List<String> ids) {

        ids = ids.stream().map(x -> "^" + x + "$").collect(Collectors.toList());


        String list = com.jsdc.iotpt.util.StringUtils.join(ids, "|");
        return list;
    }



    public List<SubmiteValue> getSubmitMonth(SubmiteValue submiteValue) {

        //查有无当前数据  没有新增
        QueryWrapper<SubmiteValue> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("subitemId", submiteValue.getSubitemId()) ;
        queryWrapper.apply("SUBSTR(searchDate,0,4) ='"+submiteValue.getYear()+"'") ;
        List<SubmiteValue> submiteValueList = submiteValueMapper.selectList(queryWrapper);
        if(CollectionUtils.isEmpty(submiteValueList)){
            SubmiteValue submiteValue1 =null ;
            for(int i= 1 ;i<13 ;i++){
                submiteValue1 = new SubmiteValue();
                submiteValue1.setSubitemId(submiteValue.getSubitemId());
                submiteValue1.setRatedValue("0");
                if(i<10){
                    submiteValue1.setSearchDate(submiteValue.getYear()+"-0"+i);
                }else {
                    submiteValue1.setSearchDate(submiteValue.getYear()+"-"+i);
                }
                submiteValueMapper.insert(submiteValue1) ;
            }
        }
        //查电量
        // 定义日期格式
        QueryWrapper<SubmiteValue> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("subitemId", submiteValue.getSubitemId()) ;
        queryWrapper2.apply("SUBSTR(searchDate,0,4) ='"+submiteValue.getYear()+"'") ;
        List<SubmiteValue> submiteValueList1 = submiteValueMapper.selectList(queryWrapper2);

        Double sum = 0.00 ;
        for(SubmiteValue s : submiteValueList1){
            sum+=Double.parseDouble(s.getRatedValue()) ;
        }
        for(SubmiteValue s : submiteValueList1){


            DataSheetVo bean = new DataSheetVo();
            bean.setEnergyType(1);
            bean.setSubitem(Integer.valueOf(submiteValue.getSubitemId()));
            bean.setIsTotalDevice(1);//查询是总设备的数据
            List<DeviceCollect> collects = deviceCollectMapper.getDeviceByArea(bean);//获取设备列表
            if (collects.isEmpty()) {// 无采集设备

                bean.setIsTotalDevice(0);//查询不是总设备的数据
                collects= deviceCollectMapper.getDeviceByArea(bean);
            }
            List<String> deviceIds = collects.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());


            // 给定的月份字符串
            String monthString =  s.getSearchDate() ;
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // 解析给定的月份字符串
            YearMonth yearMonth = YearMonth.parse(monthString, inputFormatter);

            // 获取该月的第一天
            LocalDate firstDay = yearMonth.atDay(1);
            LocalDateTime firstDayDateTime = firstDay.atStartOfDay();

            // 获取该月的最后一天
            LocalDate lastDay = yearMonth.atEndOfMonth();
            LocalDateTime lastDayDateTime = lastDay.atTime(23, 59, 59);



            String sql3 = " select sum(totalPower) as totalPower from (select last(val)-first(val) as totalPower from datasheet WHERE subitem='"+submiteValue.getSubitemId()+"'  AND channelId='ENERGY_TOTAL'  and time <='" + lastDayDateTime.format(outputFormatter) + "' and time>='" + firstDayDateTime.format(outputFormatter)+ "' and deviceCollectId=~/" + influxdbInToString(deviceIds) + "/ group by deviceCollectId) ";

            // 时序数据库解析
            QueryResult query3 = influxdbService.query(sql3);
            //解析列表类查询
            List<Map<String, Object>> maps3 = influxdbService.queryResultProcess(query3);

            if (CollectionUtils.isNotEmpty(maps3)) {
                s.setActualValue(String.format("%.2f", Double.parseDouble( maps3.get(0).get("totalPower")+"")));
            }else {
                s.setActualValue("0");
            }

            // 指定日期的格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

            // 当前指定的日期
            String currentYearMonth = s.getSearchDate();

            // 解析字符串为LocalDate对象
            LocalDate date = LocalDate.parse(currentYearMonth + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // 减去一年
            LocalDate lastYearDate = date.minusYears(1);

            // 格式化为“yyyy-MM”格式
            String formattedLastYearDate = lastYearDate.format(formatter);

            // 输出结果
            System.out.println("去年同一月份的日期: " + formattedLastYearDate);

            QueryWrapper<SubmiteValue> queryWrapper3 = new QueryWrapper<>();
            queryWrapper3.eq("subitemId", submiteValue.getSubitemId());
            queryWrapper3.eq("searchDate", formattedLastYearDate);

            SubmiteValue submiteValue1 =   submiteValueMapper.selectOne(queryWrapper3);
            if(null != submiteValue1){
                //去年定额值
                s.setLastRatedValue(submiteValue1.getRatedValue());
                // 示例数据
                if("0".equals(submiteValue1.getRatedValue())){
                    s.setYearOnYearGrowthRate(s.getRatedValue()+"%");
                }else {
                    BigDecimal thisYearSales = new BigDecimal(s.getRatedValue());
                    BigDecimal lastYearSales = new BigDecimal(submiteValue1.getRatedValue() );

                    // 计算同比增长率
                    String growth = calculateYoYGrowth(thisYearSales, lastYearSales);
                    s.setYearOnYearGrowthRate(growth);
                }

            }else {
                s.setYearOnYearGrowthRate(s.getRatedValue()+"%");
                s.setLastRatedValue("0");
            }



            QueryWrapper<ConfigDeviceSubitemValue> queryWrapper4 = new QueryWrapper<>();
            queryWrapper4.eq("subitemId", submiteValue.getSubitemId());
            queryWrapper4.eq("year", submiteValue.getYear());
            ConfigDeviceSubitemValue configDeviceSubitemValue = configDeviceSubitemValueMapper.selectOne(queryWrapper4);


            double v = Double.parseDouble(configDeviceSubitemValue.getRatedValue()) - sum;
            if(v>0){
                s.setResidualCredit(v+"") ;
            }else {
                s.setResidualCredit("0") ;
            }


        }


        return submiteValueList1 ;

    }

public void setSubmitMonthValue(SubmiteValue submiteValue){

    submiteValueMapper.updateById(submiteValue) ;
}



    public List<ConfigDeviceSubitem> itemizedMonitoringList(){
        String year = DateUtil.format(new Date(), "yyyy");
        List<ConfigDeviceSubitem> configDeviceSubitems = configDeviceSubitemMapper.existenceOrNot(year) ;

        if(CollectionUtils.isNotEmpty(configDeviceSubitems)){
            //往子表插数据
            List<ConfigDeviceSubitem> configDeviceSubitemList = configDeviceSubitemService.getList(new ConfigDeviceSubitemVo()) ;
            for(ConfigDeviceSubitem c:configDeviceSubitemList){
                ConfigDeviceSubitemValue configDeviceSubitemValue = new ConfigDeviceSubitemValue();
                configDeviceSubitemValue.setSubitemId(c.getId()+"");
                configDeviceSubitemValue.setYear(year);
                configDeviceSubitemValue.setRatedValue("0");
                configDeviceSubitemValueMapper.insert(configDeviceSubitemValue);
            }
        }

        //查实际能耗值

        List<ConfigDeviceSubitem> configDeviceSubitems2 = configDeviceSubitemMapper.getSubValue(year) ;

        // 获取2024年的第一天
        LocalDate firstDayOfYear = LocalDate.of(Integer.parseInt(year), 1, 1);

        // 获取2024年的最后一天
        LocalDate lastDayOfYear = LocalDate.of(Integer.parseInt(year), 12, 31);
        // 如果需要确保是该年的最后一天，可以使用TemporalAdjusters.lastDayOfYear()
        lastDayOfYear = lastDayOfYear.with(TemporalAdjusters.lastDayOfYear());

        for(ConfigDeviceSubitem c:configDeviceSubitems2) {


            //实际能耗
            c.setActualValue(getValByTime(c.getId(),firstDayOfYear+" 00:00:00",lastDayOfYear+" 23:59:59")+"") ;

            //能耗同比
            String nowDay = DateUtil.format(new Date(), "yyyy-MM-dd");

            //今日
            c.setNowValue(getValByTime(c.getId(),nowDay+" 00:00:00",nowDay+" 23:59:59")+"") ;

            // 获取当前日期
            LocalDate today = LocalDate.now();

            // 计算去年今天的日期
            LocalDate lastYearToday = today.minusYears(1);

            // 定义日期格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // 格式化日期
            String formattedDate = lastYearToday.format(formatter);

            //去年今日
            c.setLastValue(getValByTime(c.getId(),formattedDate+" 00:00:00",formattedDate+" 23:59:59")+""); ;



            // 计算昨天的日期
            LocalDate yesterday = today.minusDays(1);

            // 格式化昨天的日期
            String formattedYesterday = yesterday.format(formatter);

            c.setYesterdayValue(getValByTime(c.getId(),formattedYesterday+" 00:00:00",formattedYesterday+" 23:59:59")+""); ;


            // 计算同比差异
            double difference = Double.parseDouble(c.getNowValue()) - Double.parseDouble(c.getLastValue());

            // 计算同比增长率
            if(Double.parseDouble(c.getLastValue()) !=0.00 && StringUtils.isNotEmpty(c.getLastValue())){
                double growthRate = (difference / Double.parseDouble(c.getLastValue()) * 100);
                c.setYearOnYearGrowthRate(growthRate+"") ;
            }else {
                c.setYearOnYearGrowthRate(difference+"") ;
            }


            //能耗同比



            //----能耗环比
            // 计算同比差异
            double difference2 = Double.parseDouble(c.getNowValue()) - Double.parseDouble(c.getYesterdayValue());

            // 计算同比增长率
            double growthRate2 = (difference2 / Double.parseDouble(c.getYesterdayValue()) * 100);


            if(Double.parseDouble(c.getYesterdayValue()) !=0.00 && StringUtils.isNotEmpty(c.getYesterdayValue())){
                c.setChainRatio(growthRate2+""); ;
            }else {
                c.setChainRatio(difference2+"");
            }

            double partValue = Double.parseDouble(c.getActualValue()); // 例如，某部门的销售额
            // 这是总数值
            double totalValue = Double.parseDouble(c.getRatedValue()); // 例如，整个公司的销售额

            if(totalValue !=0.00 ){
                // 计算占比
                double percentage = (partValue / totalValue) * 100;
                c.setPercentage(percentage+""); ;
            }else {
                c.setPercentage(partValue+"");
            }

        }
        return configDeviceSubitems2 ;
    }




    public Double getValByTime(Integer subitemId,String startTime, String endTime){
    DataSheetVo bean = new DataSheetVo();
    bean.setEnergyType(1);
    bean.setSubitem(subitemId);
    bean.setIsTotalDevice(1);//查询是总设备的数据
    List<DeviceCollect> collects = deviceCollectMapper.getDeviceByArea(bean);//获取设备列表
    if (collects.isEmpty()) {// 无采集设备
//                map.put(a.getSubitemName(), 0d);
//                continue;
        bean.setIsTotalDevice(0);//查询不是总设备的数据
        collects= deviceCollectMapper.getDeviceByArea(bean);
    }
    List<String> deviceIds = collects.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList());
    String sql3 = " select sum(totalPower) as totalPower from (select last(val)-first(val) as totalPower from datasheet WHERE subitem='"+subitemId+"'  AND channelId='ENERGY_TOTAL'  and time <='" + endTime + "' and time>='" + startTime +""+ "' and deviceCollectId=~/" + influxdbInToString(deviceIds) + "/ group by deviceCollectId) ";

    // 时序数据库解析
    QueryResult query3 = influxdbService.query(sql3);
    //解析列表类查询
    List<Map<String, Object>> maps3 = influxdbService.queryResultProcess(query3);
        if (CollectionUtils.isNotEmpty(maps3)) {
            return Double.parseDouble(maps3.get(0).get("totalPower")+"");
        }else {
            return 0.00;
        }

}

}


