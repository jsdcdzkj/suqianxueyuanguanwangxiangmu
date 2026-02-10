package com.jsdc.iotpt.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.mapper.*;
import com.jsdc.iotpt.model.ConfigDeviceType;
import com.jsdc.iotpt.model.new_alarm.AlarmCategory;
import com.jsdc.iotpt.model.new_alarm.AlarmGroup;
import com.jsdc.iotpt.model.new_alarm.AlarmRecords;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.AlarmRecordsVo;
import com.jsdc.iotpt.vo.AlarmStatisticsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AlarmStatisticsService {
    @Autowired
    private AlarmRecordsMapper alarmRecordsMapper;
    @Autowired
    private AlarmCategoryMapper alarmCategoryMapper;

    @Autowired
    AlarmGroupMapper alarmGroupMapper;
    @Autowired
    private MissionMapper missionMapper;

    @Autowired
    DeviceCollectMapper  deviceCollectMapper;

    public List<AlarmStatisticsVo> pieChart(AlarmStatisticsVo vo) {
        return alarmRecordsMapper.pieChart(vo);
    }
    public List<AlarmStatisticsVo> pieChartNoRank(AlarmStatisticsVo vo) {
        List<AlarmStatisticsVo> alarmStatisticsVos = alarmRecordsMapper.pieChartNoRank(vo);
        List<AlarmCategory> categoryList = alarmCategoryMapper.selectList(new LambdaQueryWrapper<AlarmCategory>().eq(AlarmCategory::getIsDel, 0));
//                .ne(AlarmCategory::getCode, "YE"));
        List<AlarmStatisticsVo> list = new ArrayList<>();
        for (AlarmCategory ac : categoryList) {
            AlarmStatisticsVo alarmStatisticsVo = alarmStatisticsVos.stream().filter(x -> Objects.equals(x.getCode(), ac.getCode())).findFirst().orElse(null);
            list.add(alarmStatisticsVo);
        }
        return list;
    }
    //与项目经理沟通-> 前15条
    public List<AlarmStatisticsVo> rank(AlarmStatisticsVo vo) {
        // 项目经理-> 先屏蔽预警
        vo.setAlarmType("1");
        if (Objects.equals(vo.getRankType(),"1")){
            return alarmRecordsMapper.areaRank(vo).stream().limit(15).collect(Collectors.toList());
        }else if (Objects.equals(vo.getRankType(),"2")){
            return alarmRecordsMapper.pieChart(vo).stream().limit(15).collect(Collectors.toList());
        }else if (Objects.equals(vo.getRankType(),"3")){
            return alarmRecordsMapper.levelRank(vo).stream().limit(15).collect(Collectors.toList());
        }else {
            return alarmRecordsMapper.areaRank(vo).stream().limit(15).collect(Collectors.toList());
        }
    }

    public Map<String, Object> alarmNum(AlarmStatisticsVo vo) {
        Map<String, Object> map = new HashMap<>();
        Long l = alarmRecordsMapper.alarmNum(vo);
        map.put("all", l);
        List<String> list = Arrays.asList("1", "2", "3");
        vo.setHandleStatus(list);
        Long n = alarmRecordsMapper.alarmNum(vo);
        map.put("processed", n);
        // 趋势图
        if (Objects.equals(vo.getRadio(),"1")){
//            String format = DateUtil.format(new Date(), "yyyy-MM-dd");
//            vo.setStartTime(format);
//            vo.setEndTime(format);
            map.put("chart",  alarmRecordsMapper.trendChartDay(vo));
        }else if (Objects.equals(vo.getRadio(),"2")){
//            Date date = new Date();
            String date = vo.getStartTime() + "-01";
//            // 1. 当月第一天（格式：yyyy-MM-dd）
            String first = DateUtil.format(DateUtil.beginOfMonth(DateUtil.parseDate(date)),  "yyyy-MM-dd");
            vo.setStartTime(first);
            // 2. 当月最后一天（格式：yyyy-MM-dd）
            String last = DateUtil.format(DateUtil.endOfMonth(DateUtil.parseDate(date)),  "yyyy-MM-dd");
            vo.setEndTime(last);
            map.put("chart",  alarmRecordsMapper.trendChartDay(vo));
        }else if (Objects.equals(vo.getRadio(),"3")){
            String date = vo.getStartTime() + "-01-01";
            // 3. 当年第一天（格式：yyyy-MM-dd）
            String first = DateUtil.format(DateUtil.beginOfYear(DateUtil.parseDate(date)),  "yyyy-MM-dd");
            vo.setStartTime(first);
            // 4. 当年最后一天（格式：yyyy-MM-dd）
            String last = DateUtil.format(DateUtil.endOfYear(DateUtil.parseDate(date)),  "yyyy-MM-dd");
            vo.setEndTime(last);
            map.put("chart",  alarmRecordsMapper.trendChartMonth(vo));
        }else if (Objects.equals(vo.getRadio(),"4")){
            map.put("chart",  alarmRecordsMapper.trendChartDay(vo));
        }
        return map;
    }

    public Map<String, Object> alarmTrend(AlarmStatisticsVo vo) {
        Map<String, Object> result = new HashMap<>();
        List<String> xData = new ArrayList<>();
        // 日
        if ("1".equals(vo.getRadio())) {
            List<DateTime> dateList = DateUtil.rangeToList(DateUtil.parseDate(vo.getStartTime()), DateUtil.parseDate(vo.getEndTime()), DateField.DAY_OF_YEAR);
            dateList.forEach(i -> xData.add(DateUtil.formatDate(i)));
        }
        // 月
        else if ("2".equals(vo.getRadio())) {
            String month = vo.getStartTime() + "-01";
            DateTime startDate = DateUtil.beginOfMonth(DateUtil.parseDate(month));
            DateTime endDate = DateUtil.endOfMonth(DateUtil.parseDate(month));
            List<DateTime> dateList = DateUtil.rangeToList(startDate, endDate, DateField.DAY_OF_YEAR);
            dateList.forEach(i -> xData.add(DateUtil.formatDate(i)));
        }
        // 年
        else if ("3".equals(vo.getRadio())) {
            for (int i = 1; i <= 12; i++) {
                if (i < 10) {
                    xData.add(vo.getStartTime() + "-0" + i);
                }else {
                    xData.add(vo.getStartTime() + "-" + i);
                }
            }
        }
        result.put("xData", xData);

        List<AlarmCategory> categoryList =
                alarmCategoryMapper.selectList(new LambdaQueryWrapper<AlarmCategory>().eq(AlarmCategory::getIsDel, 0));
//                .ne(AlarmCategory::getCode, "YE"));
        List<Map<String, Object>> yData = new ArrayList<>();
        for (AlarmCategory category : categoryList) {
            Map<String, Object> item = new HashMap<>();
            item.put("categoryId", category.getId());
            item.put("label", category.getName());
            item.put("code", category.getCode());
            item.put("data", new ArrayList<Integer>());
            yData.add(item);
        }
        for (String date : xData) {
            List<AlarmRecords> list = alarmRecordsMapper.selectList(new LambdaQueryWrapper<AlarmRecords>().eq(AlarmRecords::getIsDel, 0).like(AlarmRecords::getAlarmTime, date));
            Map<Integer, List<AlarmRecords>> recordsMap = list.stream().collect(Collectors.groupingBy(AlarmRecords::getAlarmCategory));
            for (Map<String, Object> map : yData) {
                List<AlarmRecords> itemList = recordsMap.get((Integer) map.get("categoryId"));
                List<Integer> data = (ArrayList<Integer>) map.get("data");
                if (CollUtil.isEmpty(itemList)) {
                    data.add(0);
                }else {
                    data.add(itemList.size());
                }
            }
        }
        result.put("yData", yData);
        return result;
    }

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static long calculateHours(String startTime, String endTime) {
        LocalDateTime start = LocalDateTime.parse(startTime,  FORMATTER);
        LocalDateTime end = LocalDateTime.parse(endTime,  FORMATTER);
        // 计算总秒数后手动转换为小时和剩余分钟
        Duration duration = Duration.between(start,  end);
        long totalHours = duration.toHours();
        long remainingMinutes = duration.toMinutes()  % 60;

        // 向上取整逻辑
        return remainingMinutes > 0 ? totalHours + 1 : totalHours;
    }

    /**
     * 运营安全评估
     * <code><![CDATA[功能完整性(满分5分)\n</br>
     * 计算公式:<B>功能完整性得分</B>=实际实现的功能数量/计划实现的功能数量x5(1/1x5)
     *  说明:统计智慧楼宇系统实际实现的功能与计划功能的比值，评估系统功能的完备程度。点位覆盖率(满分5分)
     *
     * 计算公式:点位覆盖率得分=实际覆盖的点位数量/应覆盖的点位数量x5(1/1x5)
     *  说明:计算实际安装传感器、监控设备等覆盖的点位与应覆盖点位的比例，反映监控的全面性。有效报警率(满分5分)
     *
     * 计算公式:有效报警率得分=有效报警次数/总报警次数x5
     *  说明:有效报警指真实的安全事件报警，统计有效报警在总报警中的占比，衡量报警系统的准确性报警处置率(满分5分)
     *
     * 计算公式:报警处置率得分=已处置的报警次数/总报警次数x5
     *   说明:评估对报警的响应和处理情况，计算已处置报警与总报警的比率。处置及时率(满分5分)
     *
     * 计算公式:处置及时率得分=及时处置的报警次数/应处置的报警次数x5
     * 说明:及时处置指在规定时间内完成的报警处理，统计及时处置的报警占比，反映响应速度设备在线率(满分5分)
     *
     * 计算公式:设备在线率得分=设备在线数量/总设备数量x5     *
     * 说明:计算设备正常运行时间与总运行时间的比例，体现设备的稳定性和可靠性
     *
     *  }]]></code>
     *
     * @param vo
     * @return
     */
    public JSONObject securityAssessment(AlarmStatisticsVo vo) {
        JSONObject jsonObject = new JSONObject();
        BigDecimal overallScore = BigDecimal.ZERO;
        if (Objects.equals(vo.getRadio(), "1")) {
            vo.setStartTime(vo.getStartTime() + " 00:00:00");
            vo.setEndTime(vo.getEndTime() + " 23:59:59");
        } else if (Objects.equals(vo.getRadio(), "2")) {
            String date = vo.getStartTime() + "-01";
            // 1. 当月第一天（格式：yyyy-MM-dd）
            String first = DateUtil.format(DateUtil.beginOfMonth(DateUtil.parseDate(date)), "yyyy-MM-dd HH:mm:ss");
            vo.setStartTime(first);
            // 2. 当月最后一天（格式：yyyy-MM-dd）
            String last = DateUtil.format(DateUtil.endOfMonth(DateUtil.parseDate(date)), "yyyy-MM-dd HH:mm:ss");
            vo.setEndTime(last);
        } else if (Objects.equals(vo.getRadio(), "3")) {
            String date = vo.getStartTime() + "-01-01";
            // 3. 当年第一天（格式：yyyy-MM-dd）
            String first = DateUtil.format(DateUtil.beginOfYear(DateUtil.parseDate(date)), "yyyy-MM-dd HH:mm:ss");
            vo.setStartTime(first);
            // 4. 当年最后一天（格式：yyyy-MM-dd）
            String last = DateUtil.format(DateUtil.endOfYear(DateUtil.parseDate(date)), "yyyy-MM-dd HH:mm:ss");
            vo.setEndTime(last);
        } else if (Objects.equals(vo.getRadio(), "4")) {
            vo.setStartTime(vo.getStartTime()+ " 00:00:00");
            vo.setEndTime(vo.getEndTime()+ " 23:59:59");
        }

        List<String> sa = new ArrayList<>();


        List<AlarmRecordsVo> alarmRecordsVos = alarmRecordsMapper.securityAssessment(vo);
        //有效报警次数
        BigDecimal validAlarmCount =  BigDecimal.ZERO;
        //总报警次数
        BigDecimal totalAlarmCount =  BigDecimal.ZERO;
        //已处置的报警次数
        BigDecimal handledAlarmCount = BigDecimal.ZERO;


        //及时处置的报警次数 在当天发生的告警当天处理的数量
        BigDecimal  timelyDisposalAlarmCount = BigDecimal.ZERO;

        if(null != alarmRecordsVos && alarmRecordsVos.size() > 0){
            totalAlarmCount = BigDecimal.valueOf(alarmRecordsVos.size());
            for (AlarmRecordsVo v:alarmRecordsVos ) {
                //1=误报  不等于误报
                if(!"1".equalsIgnoreCase(v.getHandleStatus())){
                    validAlarmCount = validAlarmCount.add(BigDecimal.ONE);
                }
                //1=误报 2=上报  3=忽略
                if("1".equalsIgnoreCase(v.getHandleStatus())
                        || "2".equalsIgnoreCase(v.getHandleStatus())
                        ||  "3".equalsIgnoreCase(v.getHandleStatus())){
                    handledAlarmCount = handledAlarmCount.add(BigDecimal.ONE);
                }

                if (null != v.getHandleTime() && StringUtils.isNotEmpty(v.getAlarmTime())) {
                    String handleDate = v.getHandleTime().substring(0, 10);
                    String alarmDate = v.getAlarmTime().substring(0, 10);
                    if (handleDate.equals(alarmDate)) {
                        timelyDisposalAlarmCount = timelyDisposalAlarmCount.add(BigDecimal.ONE);
                    }
                }

            }
        }
//        overallScore.add(timelyDisposalAlarmCount); //3

        //有效报警率得分 = 有效报警次数/总报警次数x5
        BigDecimal validAlarmRateScore = BigDecimal.ZERO;
        if (totalAlarmCount.compareTo(BigDecimal.ZERO) > 0) {
            validAlarmRateScore = validAlarmCount
                    .divide(totalAlarmCount, 4, RoundingMode.HALF_UP) // 保留4位小数进行中间计算
                    .multiply(new BigDecimal(5))                     // 乘以5
                    .setScale(2, RoundingMode.HALF_UP);               // 最终结果保留2位小数
        }


        //报警处置率得分 = 已处置的报警次数/总报警次数x5  已处置的报警次数指点击上报误报忽略的次数
        BigDecimal disposalRateScore = BigDecimal.ZERO;
        if (totalAlarmCount.compareTo(BigDecimal.ZERO) > 0) {
            disposalRateScore = handledAlarmCount
                    .divide(totalAlarmCount, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(5))
                    .setScale(2, RoundingMode.HALF_UP);
        }


        // 处置及时率得分 = 及时处置次数/已处置次数*5
        BigDecimal timelyRateScore = BigDecimal.ZERO;
        if (timelyDisposalAlarmCount.compareTo(BigDecimal.ZERO) > 0) {
            timelyRateScore = timelyDisposalAlarmCount
                    .divide(totalAlarmCount, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(5))
                    .setScale(2, RoundingMode.HALF_UP);
        }


        sa.add(String.valueOf(1 / 1 * 5));//功能完整性得分 1
        overallScore = overallScore.add(BigDecimal.valueOf(5));

        //设备在线率得分 设备在线率得分=设备在线数量/总设备数量x5
        String onlineRateScore = calculateOfflineRate(vo);
        sa.add(onlineRateScore);//设备在线率得分
        overallScore = overallScore.add(new BigDecimal(onlineRateScore));

        sa.add(timelyRateScore.toString()); // 处置及时率得分
        overallScore = overallScore.add(timelyRateScore);

        sa.add(disposalRateScore.toString()); //报警处置率得分
        overallScore = overallScore.add(disposalRateScore);

        sa.add(validAlarmRateScore.toString()); //有效报警率得分
        overallScore = overallScore.add(validAlarmRateScore);


        sa.add(String.valueOf(1 / 1 * 5));//点位覆盖率得分 2
        overallScore = overallScore.add(BigDecimal.valueOf(5));

        String grade = generateGrade(overallScore);
        jsonObject.put("radarChart",sa);
        jsonObject.put("overallScore",overallScore.setScale(2, RoundingMode.HALF_UP));
        jsonObject.put("grade",grade);

        jsonObject.put("suggest","");
        if(StringUtils.isNotEmpty(grade)) {
            AlarmGroup alarmGroup = alarmGroupMapper.selectById(vo.getGroupId());
            Map<String, List<SysDict>> dictLists = (Map<String, List<SysDict>>) RedisUtils.getBeanValue(MemoryCacheService.allDictListPrefix);
            List<SysDict> sdList = dictLists.get(alarmGroup.getCode());
            if(null != sdList && sdList.size() > 0){
                for (SysDict sysDict : sdList) {
                    if (sysDict.getDictLabel().endsWith(grade)) {
                        jsonObject.put("suggest", sysDict.getDictValue());
                    }
                }
            }
        }

        return jsonObject;
    }

    /**
     *         1	ANFANG	安防系统
     *         2	XIAOFANG	消防系统
     *         3	NENGYUAN	能源监测系统
     *         4	IOT	IOT设备管理系统
     * @param vo
     * @return
     */
    private String calculateOfflineRate(AlarmStatisticsVo vo) {
        return "1";
    }
    /**
     * 规则：     *
     * 根据6元素相加得到综合分，每个元素为5分，共30分,分别对应7个评分等级。具体为：     *
     * S=30     *
     * 25≤A<30     *
     * 20≤B<25     *
     * 15≤C<20     *
     * 10≤D<15     *
     * 5≤E<10     *
     * 0≤F<5
     * @param overallScore
     * @return
     */
    private String generateGrade(BigDecimal overallScore) {
        if (overallScore == null) {
            return "";
        }
        double score = overallScore.setScale(2, RoundingMode.HALF_UP).doubleValue();

        if (score == 30) {
            return "S";
        } else if (score >= 25) {
            return "A";
        } else if (score >= 20) {
            return "B";
        } else if (score >= 15) {
            return "C";
        } else if (score >= 10) {
            return "D";
        } else if (score >= 5) {
            return "E";
        } else {
            return "F";
        }
    }

    public Map<String, Object> disposal(AlarmStatisticsVo vo) {
        Long actualHours = 0L;
        if (Objects.equals(vo.getRadio(), "1")) {
            vo.setStartTime(vo.getStartTime() + " 00:00:00");
            vo.setEndTime(vo.getEndTime() + " 23:59:59");
            actualHours = calculateHours(vo.getStartTime(), vo.getEndTime());
        } else if (Objects.equals(vo.getRadio(), "2")) {
            String date = vo.getStartTime() + "-01";
            // 1. 当月第一天（格式：yyyy-MM-dd）
            String first = DateUtil.format(DateUtil.beginOfMonth(DateUtil.parseDate(date)), "yyyy-MM-dd HH:mm:ss");
            vo.setStartTime(first);
            // 2. 当月最后一天（格式：yyyy-MM-dd）
            String last = DateUtil.format(DateUtil.endOfMonth(DateUtil.parseDate(date)), "yyyy-MM-dd HH:mm:ss");
            vo.setEndTime(last);
            actualHours = calculateHours(vo.getStartTime(), vo.getEndTime());
        } else if (Objects.equals(vo.getRadio(), "3")) {
            String date = vo.getStartTime() + "-01-01";
            // 3. 当年第一天（格式：yyyy-MM-dd）
            String first = DateUtil.format(DateUtil.beginOfYear(DateUtil.parseDate(date)), "yyyy-MM-dd HH:mm:ss");
            vo.setStartTime(first);
            // 4. 当年最后一天（格式：yyyy-MM-dd）
            String last = DateUtil.format(DateUtil.endOfYear(DateUtil.parseDate(date)), "yyyy-MM-dd HH:mm:ss");
            vo.setEndTime(last);
            actualHours = calculateHours(vo.getStartTime(), vo.getEndTime());
        } else if (Objects.equals(vo.getRadio(), "4")) {
            actualHours = calculateHours(vo.getStartTime()+ " 00:00:00", vo.getEndTime()+ " 23:59:59");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("XY", StringUtils.isNotEmpty(missionMapper.alarmAvgXiangYingTime(vo)) ?
                missionMapper.alarmAvgXiangYingTime(vo) : "0");
        map.put("HFL", missionMapper.alarmIsNotWarn(vo) != 0 ? missionMapper.alarmHuiFu(vo) : "0");
        map.put("HF", StringUtils.isNotEmpty(missionMapper.alarmAvgHuiFuTime(vo)) ? missionMapper.alarmAvgHuiFuTime(vo) : "0");
        // 7*24 = 168
        map.put("GJS", Math.round(alarmRecordsMapper.averageNumber(vo)  * 100.0 / actualHours) / 100.0);
        map.put("FB", alarmRecordsMapper.distribution(vo.getStartTime(),vo.getEndTime(),vo.getRadio()));
        return map;
    }
}
