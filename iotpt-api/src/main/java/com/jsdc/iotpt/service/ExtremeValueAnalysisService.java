package com.jsdc.iotpt.service;

import com.alibaba.fastjson.JSONObject;
import com.jsdc.iotpt.service.influxDB.InfluxdbService;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.ExtremeValueAnalysisVo;
import org.influxdb.dto.QueryResult;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 极值分析
 */
@Service
@Transactional
public class ExtremeValueAnalysisService {

    @Autowired
    private InfluxdbService influxdbService;

//    public static void main(String[] args) {
//        System.out.println(new DateTime("2024-12-17T08:34:40.435Z".replaceAll("Z","")).toString("yyyy-MM-dd HH:mm:ss"));
//    }

    /**
     * 极值分析
     */
    public JSONObject getDataInfo(ExtremeValueAnalysisVo vo) {
        JSONObject jsonObject = new JSONObject();

        // 时间类型 1.日 2 月
        if ("1".equals(vo.getTimeType())) {
            vo.setStartTime(new DateTime(vo.getTimeStr() + "T00:00:00").toString("yyyy-MM-dd 00:00:00"));
            vo.setEndTime((new DateTime(vo.getTimeStr() + "T00:00:00").plusDays(1).toString("yyyy-MM-dd 00:00:00")));
        } else {
            vo.setStartTime(new DateTime(vo.getTimeStr() + "-01T00:00:00").toString("yyyy-MM-dd 00:00:00"));
            vo.setEndTime((new DateTime(vo.getTimeStr() + "-01T00:00:00").plusMonths(1).toString("yyyy-MM-dd 00:00:00")));
        }

        List<ExtremeValueAnalysisVo> voList = new ArrayList<>();

        // 1相电压
        // 2相电流
        // 3相有功功率
        // 4相无功功率
        // 5相视在功率
        // 6相端子温度
        // 7频率
        if ("1".equals(vo.getParamType())) {
            // 1相电压

            //A相电压 U_A
            ExtremeValueAnalysisVo analysisVo = new ExtremeValueAnalysisVo();
            analysisVo.setName("A相电压");
            queryMaxMinValMeanData("MAX(val)", "U_A", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo);
            queryMaxMinValMeanData("MIN(val)", "U_A", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo);
            queryMaxMinValMeanData("MEAN(val)", "U_A", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo);
            voList.add(analysisVo);

            //B相电压 U_B
            ExtremeValueAnalysisVo analysisVo2 = new ExtremeValueAnalysisVo();
            analysisVo2.setName("B相电压");
            queryMaxMinValMeanData("MAX(val)", "U_B", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo2);
            queryMaxMinValMeanData("MIN(val)", "U_B", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo2);
            queryMaxMinValMeanData("MEAN(val)", "U_B", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo2);
            voList.add(analysisVo2);

            //C相电压 U_C
            ExtremeValueAnalysisVo analysisVo3 = new ExtremeValueAnalysisVo();
            analysisVo3.setName("C相电压");
            queryMaxMinValMeanData("MAX(val)", "U_C", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo3);
            queryMaxMinValMeanData("MIN(val)", "U_C", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo3);
            queryMaxMinValMeanData("MEAN(val)", "U_C", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo3);
            voList.add(analysisVo3);

            Map<String, Object> map = new HashMap<>();
            JSONObject jsonObjectU_A = queryValData("U_A", vo.getTimeType(), vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), new JSONObject());
            JSONObject jsonObjectU_B = queryValData("U_B", vo.getTimeType(), vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), new JSONObject());
            JSONObject jsonObjectU_C = queryValData("U_C", vo.getTimeType(), vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), new JSONObject());
            map.put("A相电压", jsonObjectU_A);
            map.put("B相电压", jsonObjectU_B);
            map.put("C相电压", jsonObjectU_C);

            jsonObject.put("mapData", map);
        } else if ("2".equals(vo.getParamType())) {
            //2相电流

            //A相电流 I_A
            ExtremeValueAnalysisVo analysisVo = new ExtremeValueAnalysisVo();
            analysisVo.setName("A相电流");
            queryMaxMinValMeanData("MAX(val)", "I_A", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo);
            queryMaxMinValMeanData("MIN(val)", "I_A", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo);
            queryMaxMinValMeanData("MEAN(val)", "I_A", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo);
            voList.add(analysisVo);

            //B相电流 I_B
            ExtremeValueAnalysisVo analysisVo2 = new ExtremeValueAnalysisVo();
            analysisVo2.setName("B相电流");
            queryMaxMinValMeanData("MAX(val)", "I_B", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo2);
            queryMaxMinValMeanData("MIN(val)", "I_B", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo2);
            queryMaxMinValMeanData("MEAN(val)", "I_B", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo2);
            voList.add(analysisVo2);

            //C相电流 I_C
            ExtremeValueAnalysisVo analysisVo3 = new ExtremeValueAnalysisVo();
            analysisVo3.setName("C相电流");
            queryMaxMinValMeanData("MAX(val)", "I_C", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo3);
            queryMaxMinValMeanData("MIN(val)", "I_C", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo3);
            queryMaxMinValMeanData("MEAN(val)", "I_C", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo3);
            voList.add(analysisVo3);

            Map<String, Object> map = new HashMap<>();
            JSONObject jsonObjectU_A = queryValData("I_A", vo.getTimeType(), vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), new JSONObject());
            JSONObject jsonObjectU_B = queryValData("I_B", vo.getTimeType(), vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), new JSONObject());
            JSONObject jsonObjectU_C = queryValData("I_C", vo.getTimeType(), vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), new JSONObject());
            map.put("A相电流", jsonObjectU_A);
            map.put("B相电流", jsonObjectU_B);
            map.put("C相电流", jsonObjectU_C);

            jsonObject.put("mapData", map);

        } else if ("3".equals(vo.getParamType())) {
            //3相有功功率

            //A相有功功率 P_A
            ExtremeValueAnalysisVo analysisVo = new ExtremeValueAnalysisVo();
            analysisVo.setName("A相有功功率");
            queryMaxMinValMeanData("MAX(val)", "P_A", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo);
            queryMaxMinValMeanData("MIN(val)", "P_A", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo);
            queryMaxMinValMeanData("MEAN(val)", "P_A", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo);
            voList.add(analysisVo);

            //B相有功功率 P_B
            ExtremeValueAnalysisVo analysisVo2 = new ExtremeValueAnalysisVo();
            analysisVo2.setName("B相有功功率");
            queryMaxMinValMeanData("MAX(val)", "P_B", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo2);
            queryMaxMinValMeanData("MIN(val)", "P_B", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo2);
            queryMaxMinValMeanData("MEAN(val)", "P_B", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo2);
            voList.add(analysisVo2);

            //C相有功功率 P_C
            ExtremeValueAnalysisVo analysisVo3 = new ExtremeValueAnalysisVo();
            analysisVo3.setName("C相有功功率");
            queryMaxMinValMeanData("MAX(val)", "P_C", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo3);
            queryMaxMinValMeanData("MIN(val)", "P_C", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo3);
            queryMaxMinValMeanData("MEAN(val)", "P_C", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo3);
            voList.add(analysisVo3);

            Map<String, Object> map = new HashMap<>();
            JSONObject jsonObjectU_A = queryValData("P_A", vo.getTimeType(), vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), new JSONObject());
            JSONObject jsonObjectU_B = queryValData("P_B", vo.getTimeType(), vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), new JSONObject());
            JSONObject jsonObjectU_C = queryValData("P_C", vo.getTimeType(), vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), new JSONObject());
            map.put("A相有功功率", jsonObjectU_A);
            map.put("B相有功功率", jsonObjectU_B);
            map.put("C相有功功率", jsonObjectU_C);

            jsonObject.put("mapData", map);

        } else if ("4".equals(vo.getParamType())) {
            //4相无功功率

            //A相无功功率 QAA
            ExtremeValueAnalysisVo analysisVo = new ExtremeValueAnalysisVo();
            analysisVo.setName("A相无功功率");
            queryMaxMinValMeanData("MAX(val)", "QAA", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo);
            queryMaxMinValMeanData("MIN(val)", "QAA", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo);
            queryMaxMinValMeanData("MEAN(val)", "QAA", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo);
            voList.add(analysisVo);

            //B相无功功率 QAB
            ExtremeValueAnalysisVo analysisVo2 = new ExtremeValueAnalysisVo();
            analysisVo2.setName("B相无功功率");
            queryMaxMinValMeanData("MAX(val)", "QAB", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo2);
            queryMaxMinValMeanData("MIN(val)", "QAB", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo2);
            queryMaxMinValMeanData("MEAN(val)", "QAB", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo2);
            voList.add(analysisVo2);

            //C相无功功率 QAC
            ExtremeValueAnalysisVo analysisVo3 = new ExtremeValueAnalysisVo();
            analysisVo3.setName("C相无功功率");
            queryMaxMinValMeanData("MAX(val)", "QAC", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo3);
            queryMaxMinValMeanData("MIN(val)", "QAC", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo3);
            queryMaxMinValMeanData("MEAN(val)", "QAC", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo3);
            voList.add(analysisVo3);

            Map<String, Object> map = new HashMap<>();
            JSONObject jsonObjectU_A = queryValData("QAA", vo.getTimeType(), vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), new JSONObject());
            JSONObject jsonObjectU_B = queryValData("QAB", vo.getTimeType(), vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), new JSONObject());
            JSONObject jsonObjectU_C = queryValData("QAC", vo.getTimeType(), vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), new JSONObject());
            map.put("A相无功功率", jsonObjectU_A);
            map.put("B相无功功率", jsonObjectU_B);
            map.put("C相无功功率", jsonObjectU_C);

            jsonObject.put("mapData", map);

        } else if ("5".equals(vo.getParamType())) {
            //5相视在功率

            //A相视在功率 SAA
            ExtremeValueAnalysisVo analysisVo = new ExtremeValueAnalysisVo();
            analysisVo.setName("A相视在功率");
            queryMaxMinValMeanData("MAX(val)", "SAA", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo);
            queryMaxMinValMeanData("MIN(val)", "SAA", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo);
            queryMaxMinValMeanData("MEAN(val)", "SAA", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo);
            voList.add(analysisVo);

            //B相视在功率 SAB
            ExtremeValueAnalysisVo analysisVo2 = new ExtremeValueAnalysisVo();
            analysisVo2.setName("B相视在功率");
            queryMaxMinValMeanData("MAX(val)", "SAB", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo2);
            queryMaxMinValMeanData("MIN(val)", "SAB", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo2);
            queryMaxMinValMeanData("MEAN(val)", "SAB", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo2);
            voList.add(analysisVo2);

            //C相视在功率 SAC
            ExtremeValueAnalysisVo analysisVo3 = new ExtremeValueAnalysisVo();
            analysisVo3.setName("C相视在功率");
            queryMaxMinValMeanData("MAX(val)", "SAC", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo3);
            queryMaxMinValMeanData("MIN(val)", "SAC", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo3);
            queryMaxMinValMeanData("MEAN(val)", "SAC", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo3);
            voList.add(analysisVo3);

            Map<String, Object> map = new HashMap<>();
            JSONObject jsonObjectU_A = queryValData("SAA", vo.getTimeType(), vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), new JSONObject());
            JSONObject jsonObjectU_B = queryValData("SAB", vo.getTimeType(), vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), new JSONObject());
            JSONObject jsonObjectU_C = queryValData("SAC", vo.getTimeType(), vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), new JSONObject());
            map.put("A相视在功率", jsonObjectU_A);
            map.put("B相视在功率", jsonObjectU_B);
            map.put("C相视在功率", jsonObjectU_C);

            jsonObject.put("mapData", map);

        } else if ("6".equals(vo.getParamType())) {
            //6相端子温度

            //A相端子温度 TTA
            ExtremeValueAnalysisVo analysisVo = new ExtremeValueAnalysisVo();
            analysisVo.setName("A相端子温度");
            queryMaxMinValMeanData("MAX(val)", "TTA", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo);
            queryMaxMinValMeanData("MIN(val)", "TTA", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo);
            queryMaxMinValMeanData("MEAN(val)", "TTA", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo);
            voList.add(analysisVo);

            //B相端子温度 TTB
            ExtremeValueAnalysisVo analysisVo2 = new ExtremeValueAnalysisVo();
            analysisVo2.setName("B相端子温度");
            queryMaxMinValMeanData("MAX(val)", "TTB", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo2);
            queryMaxMinValMeanData("MIN(val)", "TTB", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo2);
            queryMaxMinValMeanData("MEAN(val)", "TTB", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo2);
            voList.add(analysisVo2);

            //C相端子温度 TTC
            ExtremeValueAnalysisVo analysisVo3 = new ExtremeValueAnalysisVo();
            analysisVo3.setName("C相端子温度");
            queryMaxMinValMeanData("MAX(val)", "TTC", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo3);
            queryMaxMinValMeanData("MIN(val)", "TTC", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo3);
            queryMaxMinValMeanData("MEAN(val)", "TTC", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo3);
            voList.add(analysisVo3);

            Map<String, Object> map = new HashMap<>();
            JSONObject jsonObjectU_A = queryValData("TTA", vo.getTimeType(), vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), new JSONObject());
            JSONObject jsonObjectU_B = queryValData("TTB", vo.getTimeType(), vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), new JSONObject());
            JSONObject jsonObjectU_C = queryValData("TTC", vo.getTimeType(), vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), new JSONObject());
            map.put("A相端子温度", jsonObjectU_A);
            map.put("B相端子温度", jsonObjectU_B);
            map.put("C相端子温度", jsonObjectU_C);

            jsonObject.put("mapData", map);
        } else if ("7".equals(vo.getParamType())) {
            //6频率

            //频率 HZ
            ExtremeValueAnalysisVo analysisVo = new ExtremeValueAnalysisVo();
            analysisVo.setName("频率");
            queryMaxMinValMeanData("MAX(val)", "HZ", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo);
            queryMaxMinValMeanData("MIN(val)", "HZ", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo);
            queryMaxMinValMeanData("MEAN(val)", "HZ", vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), analysisVo);
            voList.add(analysisVo);

            Map<String, Object> map = new HashMap<>();
            JSONObject jsonObjectU_A = queryValData("HZ", vo.getTimeType(), vo.getDeviceId(), vo.getStartTime(), vo.getEndTime(), new JSONObject());
            map.put("频率", jsonObjectU_A);

            jsonObject.put("mapData", map);
        }

        jsonObject.put("voList", voList);

        return jsonObject;
    }

    /**
     * 极值分析
     *
     * @param countType 统计计算类型 最大值、最小值、平均值
     * @param channelId 信号类型
     * @param deviceId  设备id
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    public void queryMaxMinValMeanData(String countType, String channelId, String deviceId, String startTime, String endTime, ExtremeValueAnalysisVo analysisVo) {
        String sql = "SELECT " + countType + " as val from datasheet" +
                " where channelId = '" + channelId + "'";

        if (StringUtils.isNotEmpty(deviceId)) {
            sql += " and deviceCollectId = '" + deviceId + "'";
        }
        if (StringUtils.isNotEmpty(startTime)) {
            sql += " and time >= '" + startTime + "'";
        }
        if (StringUtils.isNotEmpty(endTime)) {
            sql += " and time < '" + endTime + "'";
        }

        QueryResult query = influxdbService.query(sql);

        List<Map<String, Object>> mapList = influxdbService.queryResultProcess(query);

        DecimalFormat df = new DecimalFormat("0.00");
        double sum = 0;
        for (Map<String, Object> map : mapList) {
            if (StringUtils.isNotNull(map.get("val"))) {
                double val = (double) map.get("val");
                sum += val;

                //最大值
                if ("MAX(val)".equals(countType)) {
                    analysisVo.setMaxVal(df.format(sum));
                    analysisVo.setMaxOccurrenceTime(new DateTime(map.get("time").toString().replaceAll("Z", "")).toString("yyyy-MM-dd HH:mm:ss"));
                } else if ("MIN(val)".equals(countType)) {
                    //最小值
                    analysisVo.setMinVal(df.format(sum));
                    analysisVo.setMinOccurrenceTime(new DateTime(map.get("time").toString().replaceAll("Z", "")).toString("yyyy-MM-dd HH:mm:ss"));
                } else if ("MEAN(val)".equals(countType)) {
                    //平均值
                    analysisVo.setMeanVal(df.format(sum));
                }
            }
        }
    }

    /**
     * 折线图
     *
     * @param channelId 信号类型
     * @param timeType  时间类型 1.日 2 月
     * @param deviceId  设备id
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    public JSONObject queryValData(String channelId, String timeType, String deviceId, String startTime, String endTime, JSONObject jsonObject) {
        String sql = "SELECT last(val) as val from datasheet" +
                " where channelId = '" + channelId + "'";

        if (StringUtils.isNotEmpty(deviceId)) {
            sql += " and deviceCollectId = '" + deviceId + "'";
        }
        if (StringUtils.isNotEmpty(startTime)) {
            sql += " and time >= '" + startTime + "'";
        }
        if (StringUtils.isNotEmpty(endTime)) {
            sql += " and time < '" + endTime + "'";
        }
        //10分钟
        sql += " group by time(10m)";
        QueryResult query = influxdbService.query(sql);

        List<Map<String, Object>> mapList = influxdbService.queryResultProcess(query);

        DecimalFormat df = new DecimalFormat("0.00");

        List<String> nameList = new ArrayList<>();
        List<String> valueList = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            if (StringUtils.isNotNull(map.get("time"))) {
                nameList.add(new DateTime(map.get("time").toString().replaceAll("Z", "")).toString("yyyy-MM-dd HH:mm:ss"));
                if (StringUtils.isNotNull(map.get("val"))) {
                    double val = (double) map.get("val");
                    valueList.add(df.format(val));
                } else {
                    valueList.add("0.00");
                }
            }
        }

        jsonObject.put("nameList", nameList);
        jsonObject.put("valueList", valueList);

        return jsonObject;
    }


    public JSONObject queryTempRhValData(String channelId, String deviceId, String startTime, String endTime) {
        JSONObject jsonObject = new JSONObject();
        String sql = "SELECT last(val) as val from datasheet" +
                " where channelId = '" + channelId + "'";

        if (StringUtils.isNotEmpty(deviceId)) {
            sql += " and deviceCollectId = '" + deviceId + "'";
        }
        if (StringUtils.isNotEmpty(startTime)) {
            sql += " and time >= '" + startTime + "'";
        }
        if (StringUtils.isNotEmpty(endTime)) {
            sql += " and time < '" + endTime + "'";
        }
        sql += " group by time(1h)";
        QueryResult query = influxdbService.query(sql);

        List<Map<String, Object>> mapList = influxdbService.queryResultProcess(query);

        DecimalFormat df = new DecimalFormat("0.00");

        List<String> nameList = new ArrayList<>();
        List<String> valueList = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            if (StringUtils.isNotNull(map.get("time"))) {
                nameList.add(new DateTime(map.get("time").toString().replaceAll("Z", "")).toString("yyyy-MM-dd HH:mm:ss"));
                if (StringUtils.isNotNull(map.get("val"))) {
                    double val = (double) map.get("val");
                    valueList.add(df.format(val));
                } else {
                    valueList.add("0.00");
                }
            }
        }

        jsonObject.put("nameList", nameList);
        jsonObject.put("valueList", valueList);

        return jsonObject;
    }

}
