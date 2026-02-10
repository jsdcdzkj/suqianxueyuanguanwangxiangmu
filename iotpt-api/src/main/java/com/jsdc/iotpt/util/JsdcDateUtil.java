package com.jsdc.iotpt.util;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 时间工具类
 *
 * @author MrBird
 */
public abstract class JsdcDateUtil {

    public static final String FULL_TIME_PATTERN = "yyyyMMddHHmmss";

    public static final String FULL_TIME_SPLIT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String FULL_SPLIT_PATTERN = "yyyy-MM-dd";

    public static final String CST_TIME_PATTERN = "EEE MMM dd HH:mm:ss zzz yyyy";

    public static String formatFullTime(LocalDateTime localDateTime) {
        return formatFullTime(localDateTime, FULL_TIME_PATTERN);
    }

    public static String formatFullTime(LocalDateTime localDateTime, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return localDateTime.format(dateTimeFormatter);
    }

    public static String getDateFormat(Date date, String dateFormatType) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormatType, Locale.CHINA);
        return simpleDateFormat.format(date);
    }

    public static String formatCstTime(String date, String format) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CST_TIME_PATTERN, Locale.US);
        Date usDate = simpleDateFormat.parse(date);
        return JsdcDateUtil.getDateFormat(usDate, format);
    }

    public static String formatInstant(Instant instant, String format) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return localDateTime.format(DateTimeFormatter.ofPattern(format));
    }

//    public static String formatUtcTime(String utcTime) {
//        try {
//            String t = StringUtils.replace(utcTime, "T", Strings.SPACE);
//            String z = StringUtils.replace(t, "Z", Strings.EMPTY);
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FULL_TIME_SPLIT_PATTERN);
//            LocalDateTime localDateTime = LocalDateTime.parse(z, formatter);
//            LocalDateTime now = localDateTime.plusHours(8);
//            return formatFullTime(now, FULL_TIME_SPLIT_PATTERN);
//        } catch (Exception ignore) {
//            return Strings.EMPTY;
//        }
//    }


    /**
     * @Description: 查询几天前的时间
     * @param: [num]
     * @return: java.lang.String
     * @author: 苹果
     * @date: 2023/7/19
     * @time: 10:59
     */
    public static Date lastDayTime(Integer num,Date day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day);
        calendar.add(Calendar.DATE, num);
        return calendar.getTime();
    }

    /**
     * @Description: 查询本周第一天
     * @param: [type]
     * @return: java.lang.String
     * @author: 苹果
     * @date: 2023/7/19
     * @time: 11:57
     */
    public static Date getFirstWeekDay(Date day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day);
        //如果是周日
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }
        Integer i = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.add(Calendar.DATE, -i + 1);
        return calendar.getTime();
    }

    public static Date getFirstDay(Date day,Integer cType) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day);
        calendar.set(cType, 1);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * @Description: 查询本周第一天
     * @param: [type]
     * @return: java.lang.String
     * @author: yc
     * @date: 2023/7/19
     * @time: 11:57
     */
    public static Date getMonthFirstDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * @Description: 获取今天的0点
     * @param: [type]
     * @return: java.lang.String
     * @author: yc
     * @date: 2023/7/19
     * @time: 11:57
     */
    public static Date getZeroHourDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * @Description: 获取今天的0点
     * @param: [type]
     * @return: java.lang.String
     * @author: yc
     * @date: 2023/7/19
     * @time: 11:57
     */
    public static Date getFinalHourDate() {
        Calendar ca=Calendar.getInstance();
        ca.set(Calendar.HOUR_OF_DAY, 0);
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        ca.set(Calendar.MILLISECOND, 0);
        ca.add(Calendar.DAY_OF_MONTH, 1);
        return ca.getTime();
    }


    /***
     * @Description: 获取当前周最后一天 00:00:00
     * @param: [day]
     * @return: java.lang.String
     * @author: 苹果
     * @date: 2023/8/18
     * @time: 16:51
     */
    public static Date getLastDayWeek(){
       return JsdcDateUtil.getFirstWeekDay(JsdcDateUtil.lastDayTime(7,new Date()));
    }


    /***
     * @Description: 获取当前月最后一天 00:00:00
     * @param: [day]
     * @return: java.lang.String
     * @author: 苹果
     * @date: 2023/8/18
     * @time: 16:51
     */
    public static Date getLastDayMONTH(){
        return JsdcDateUtil.getFirstDay(JsdcDateUtil.lastDayTime(30,new Date()),
                Calendar.DAY_OF_MONTH);
    }

//    public static void main(String[] args) {


//        System.out.println(
//              JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstWeekDay(JsdcDateUtil.lastDayTime(7,new Date())),
//                      JsdcDateUtil.FULL_SPLIT_PATTERN)+" 00:00:00"
//        );
//
//        System.out.println(
//                JsdcDateUtil.getDateFormat(JsdcDateUtil.getFirstDay(JsdcDateUtil.lastDayTime(30,new Date()),
//                        Calendar.DAY_OF_MONTH),
//                        JsdcDateUtil.FULL_SPLIT_PATTERN)+" 00:00:00"
//        );
//    }

    /**
     * 获取某一年每个月的开始结束时间
     * @param year
     * @return
     */
    public static Map<String,String> getMonthByYear(String year){
        Map<String,String> monthMap = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            String timeStart = year+"-"+getMonth(i+"")+"-01";
            String timeEnd = "";
            if(i == 12){
                 timeEnd = Integer.parseInt(year)+1+"-01-01";
            }else{
                 timeEnd = year+"-"+getMonth(i+1+"")+"-01";
            }
            monthMap.put(i+"",timeStart+","+timeEnd);
        }
        return monthMap;
    }

    private static String getMonth(String month){
            if(month.length() == 1){
                return "0"+month;
            }else {
                return month;
            }
    }


    public static List<String> getDatesInRange(Date startDate, Date endDate) {
        List<String> datesInRange = new ArrayList<>();
        long betweenDay = DateUtil.between(startDate, endDate, DateUnit.DAY);


        for (int i = 0; i <= betweenDay; i++) {
            Date date = DateUtil.offsetDay(startDate, i);
            System.out.println(DateUtil.formatDate(date));
            datesInRange.add(DateUtil.formatDate(date));
        }
        return datesInRange;
    }

    public static int getHour() {
        Calendar ca=Calendar.getInstance();
        return ca.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMonth() {
        Calendar ca=Calendar.getInstance();
        return ca.get(Calendar.MONTH) + 1;
    }

    public static int getWeekDay() {
        Calendar ca=Calendar.getInstance();
        return ca.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public static int getMonthDate() {
        Calendar ca=Calendar.getInstance();
        return ca.get(Calendar.DATE);
    }

    public static int getTotalMonthDate() {
        Calendar ca=Calendar.getInstance();
        int days = ca.getActualMaximum(Calendar.DAY_OF_MONTH);
        return days;
    }


    public static void main(String[] args) {
        int w = getWeekDay() ;
        int month = getMonth() ;
        int monthDate = getMonthDate();
        System.out.println(month);
    }

}
