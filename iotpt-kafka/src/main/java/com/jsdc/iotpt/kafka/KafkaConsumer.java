package com.jsdc.iotpt.kafka;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.mapper.AlertSheetMapper;
import com.jsdc.iotpt.mapper.DataSheetMapper;
import com.jsdc.iotpt.mapper.HeartSheetMapper;
import com.jsdc.iotpt.mapper.WarnSheetMapper;
import com.jsdc.iotpt.model.*;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.service.*;
import com.jsdc.iotpt.service.influxDB.InfluxdbService;
import com.jsdc.iotpt.service.mqtt.AlertSheetService;
import com.jsdc.iotpt.service.mqtt.DataSheetService;
import com.jsdc.iotpt.util.RedisDictDataUtil;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.WarningConfigVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @projectName: IOT
 * @className: KafkaConsumer
 * @author: wp
 * @description: kafka消费者
 * @date: 2023/5/11 9:40
 */
@Service
@Slf4j
public class KafkaConsumer {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AlertSheetMapper alertSheetMapper;

    @Autowired
    private DataSheetMapper dataSheetMapper;

    @Autowired
    private DataSheetService dataSheetService;
    @Autowired
    AlertSheetService alertSheetService;

    @Autowired
    HeartSheetMapper heartSheetMapper;

    @Autowired
    DeviceCollectService deviceCollectService;

    @Autowired
    DeviceGatewayService deviceGatewayService;

    @Autowired
    private InfluxdbService influxdbService;

    @Autowired
    private WarnSheetMapper warnSheetMapper;
    @Autowired
    MemoryCacheService memoryCacheService;

    /**
     * 电流
     */
    static List<String> listA = Arrays.asList("I_A", "I_B", "I_C");
//    static List<String> listA = Arrays.asList("Energy_A", "Energy_ALL", "v_A");

    /**
     * 电压
     */
    static List<String> listV = Arrays.asList("U_A", "U_B", "U_C");
//    static List<String> listV = Arrays.asList("Rate_W_A", "Rate_W_ALL", "Rate_A");

    @Autowired
    SmartEnergyReportService smartEnergyReportService;


    /**
     * 数据
     *
     * @param record
     */
    @KafkaListener(topics = {"DATA_TOPIC"})
    public void receiveMsg(ConsumerRecord<String, Object> record) {
        //topic
        String topic = record.topic();
        //消息内容
        String msg = String.valueOf(record.value());

        List<DataSheet> dataSheets = JSON.parseArray(msg, DataSheet.class);

        // 三项电流
        Map<Integer, List<Double>> threeA = new HashMap<>();

        // 三项电压
        Map<Integer, List<Double>> threeV = new HashMap<>();

        //暂时只对告警数据筛选 暂时不对数据筛选 代码保留
        //dataSheets = dataSheetService.addSupplyField(dataSheets);

        for (DataSheet x : dataSheets) {
            // 设备主键，数据，信号主键为空时，当前数据无效
            if (null == x.getDeviceId() || null == x.getVal() || null == x.getChannelId()) {
                continue;
            }

            // 根据code获取采集设备
            DeviceCollect deviceCollect = deviceCollectService.getOne(Wrappers.<DeviceCollect>lambdaQuery()
                    .eq(DeviceCollect::getDeviceCode, x.getDeviceId()));

            // 未找到采集设备
            if (null == deviceCollect) {
                continue;
            }

//            // 如果网关不存在，通过主题逆推网关,主题与网关一对一关系
//            if (null == x.getGateWayId()) {
//
//                DeviceGateway deviceGateway = deviceGatewayService.
//                        getOne(Wrappers.<DeviceGateway>lambdaQuery().
//                                eq(DeviceGateway::getSubscribeTopicId, x.getTopicId()));
//                if (null == deviceGateway) {
//                    continue;
//                }
//                x.setGateWayId(deviceGateway.getDeviceCode());
//                x.setDeviceGatewayId(deviceGateway.getId());
//            } else {
//                DeviceGateway deviceGateway = deviceGatewayService.
//                        getOne(Wrappers.<DeviceGateway>lambdaQuery().
//                                eq(DeviceGateway::getDeviceCode, x.getGateWayId()));
//                if (null == deviceGateway) {
//                    continue;
//                }
//                x.setDeviceGatewayId(deviceGateway.getId());
//            }



            // 三项电流
            if (listA.contains(x.getChannelId())) {
                if (!threeA.containsKey(deviceCollect.getId())) {
                    threeA.put(deviceCollect.getId(), new ArrayList<Double>());
                }
                threeA.get(deviceCollect.getId()).add(Double.parseDouble(x.getVal()));
            } else if (listV.contains(x.getChannelId())) {
                // 三项电压
                if (!threeV.containsKey(deviceCollect.getId())) {
                    threeV.put(deviceCollect.getId(), new ArrayList<Double>());
                }
                threeV.get(deviceCollect.getId()).add(Double.parseDouble(x.getVal()));
            }


            Map<String, String> tagsMap = new HashMap<>();
            // 网关id
            tagsMap.put("deviceGatewayId", String.valueOf(deviceCollect.getGatewayId()));
            // 采集设备id
            tagsMap.put("deviceCollectId", String.valueOf(deviceCollect.getId()));
            // 分项id
            tagsMap.put("subitem", String.valueOf(deviceCollect.getSubitem()));
            // 设备类型
            tagsMap.put("deviceType", String.valueOf(deviceCollect.getDeviceType()));
            // 采集设备所在的楼宇
            tagsMap.put("buildId", String.valueOf(deviceCollect.getBuildId()));
            //  采集设备所在的楼层
            tagsMap.put("floorId", String.valueOf(deviceCollect.getFloorId()));
            // 采集设备所在的区域
            tagsMap.put("areaId", String.valueOf(deviceCollect.getAreaId()));
            // 信号主键
            tagsMap.put("channelId", x.getChannelId());

            //fields 监测值
            Map<String, Object> fieldsMap = new HashMap<>();
            fieldsMap.put("val", Float.parseFloat(x.getVal()));
            Date date = new Date();
            long time = date.getTime() + 28800000;
            // 采集时间
            fieldsMap.put("dataTime", DateUtil.format(date, "yyyy-MM-dd HH:mm:ss"));


            influxdbService.insert("datasheet", time, tagsMap, fieldsMap);


        }

        for (Map.Entry item : threeA.entrySet()) {
            String value = smartEnergyReportService.onImbalanceCount((List<Double>) item.getValue());
            if (null != value) {
                smartEnergyReportService.insertTripartiteImbalance(String.valueOf((Integer) item.getKey()), "2", Float.parseFloat(value));
            }
        }

        for (Map.Entry item : threeV.entrySet()) {
            String value = smartEnergyReportService.onImbalanceCount((List<Double>) item.getValue());
            if (null != value) {
                smartEnergyReportService.insertTripartiteImbalance(String.valueOf((Integer) item.getKey()), "1", Float.parseFloat(value));
            }
        }

    }


    @KafkaListener(topics = {"ALERT_TOPIC"})
    public void alertMsg(ConsumerRecord<String, Object> record) {
        //topic
        String topic = record.topic();
        //消息内容
        String msg = String.valueOf(record.value());

        logger.info("msg=====test===============" + msg);
        //[{"alertTime":"2024-01-09 08:44:34","deviceId":"random_2FLOORTEMP","gateWayId":"202309134816","signalId":"RH","topicId":10,"val":"楂樻姤璀?"}]
        //领祺网关下发告警信息方案
        //系统配置的告警配置会下发到领祺网关中 。网关下发的报警 都是应该入库

        List<AlertSheet> alertSheets = JSON.parseArray(msg, AlertSheet.class);


        Map<String, SysDict> dictMap = RedisDictDataUtil.getDictByType("warnType");
        Object signalTypeObj = RedisUtils.getBeanValue(MemoryCacheService.allSignalTypePrefix);
        Map<String, String> signalTypeMap =  new HashMap<>();
        if(null != signalTypeObj && signalTypeObj instanceof Map){
            signalTypeMap = (Map<String, String>)signalTypeObj;
        }
        for (AlertSheet x : alertSheets) {
            // 设备主键，数据，信号主键为空时，当前数据无效
            if (null == x.getDeviceId() || null == x.getVal() || null == x.getSignalId()) {
                continue;
            }

            //此代码针对领祺网关数据 ，理论上所有网关的编码到根据网关配置解析以后就应该是真正的设备编码
            String tempdi = x.getDeviceId();
            if(tempdi.startsWith("random_")){
                x.setDeviceId(tempdi.replaceFirst("random_",""));
            }
            logger.info("msg===x.setDeviceId===>>>>======" + x.getDeviceId());
            System.out.println("msg===x.setDeviceId===>>>>======" + x.getDeviceId());

            // 采集设备
            DeviceCollect deviceCollect = deviceCollectService.getOne(Wrappers.<DeviceCollect>lambdaQuery()
                    .eq(DeviceCollect::getDeviceCode, x.getDeviceId()));

            // 未找到采集设备
            if (null == deviceCollect) {
                continue;
            }

            // 网关设备
            DeviceGateway deviceGateway = deviceGatewayService.getById(deviceCollect.getGatewayId());

            if (null == deviceGateway) {
                continue;
            }

//            // 如果网关不存在，通过主题逆推网关,主题与网关一对一关系
//            if (null == x.getGateWayId()) {
//
//                DeviceGateway deviceGateway = deviceGatewayService.
//                        getOne(Wrappers.<DeviceGateway>lambdaQuery().
//                                eq(DeviceGateway::getSubscribeTopicId, x.getTopicId()));
//                if (null == deviceGateway) {
//                    continue;
//                }
//
//                x.setGateWayId(deviceGateway.getDeviceCode());
//                x.setDeviceGatewayId(deviceGateway.getId());
//            } else {
//                DeviceGateway deviceGateway = deviceGatewayService.
//                        getOne(Wrappers.<DeviceGateway>lambdaQuery().
//                                eq(DeviceGateway::getDeviceCode, x.getGateWayId()));
//                if (null == deviceGateway) {
//                    continue;
//                }
//                x.setDeviceGatewayId(deviceGateway.getId());
//            }




            //
            String collectKey = String.format("%s_%s", deviceGateway.getDeviceCode(), x.getDeviceId());


            //领祺网关下发告警信息方案
            //系统配置的告警配置会下发到领祺网关中 。网关下发的报警 都是应该入库
            Map<String, Object> map = alertSheetService.hasWarn(collectKey, deviceCollect, x);

            if (map.containsKey("flag") && (Boolean) map.get("flag")) {
                WarningConfigVo wcVo = (WarningConfigVo) map.get("data");
                x.setWarnConfigId(wcVo.getId());
                x.setIsWarn("T");
            } else {
                x.setIsWarn("F");
            }


            Map<String, String> tagsMap = new HashMap<>();
            // 网关id
            tagsMap.put("deviceGatewayId", String.valueOf(deviceGateway.getId()));
            // 采集设备id
            tagsMap.put("deviceCollectId", String.valueOf(deviceCollect.getId()));
            // 分项id
            tagsMap.put("subitem", String.valueOf(deviceCollect.getSubitem()));
            // 采集设备所在的楼宇
            tagsMap.put("buildId", String.valueOf(deviceCollect.getBuildId()));
            //  采集设备所在的楼层
            tagsMap.put("floorId", String.valueOf(deviceCollect.getFloorId()));
            // 采集设备所在的区域
            tagsMap.put("areaId", String.valueOf(deviceCollect.getAreaId()));
            // 信号主键
            tagsMap.put("signalId", x.getSignalId());
            // 告警规则id
            tagsMap.put("warnConfigId", String.valueOf(x.getWarnConfigId()));

            //fields 监测值
            Map<String, Object> fieldsMap = new HashMap<>();
            fieldsMap.put("val", Float.parseFloat(x.getVal()));

            // 是否触发告警
            fieldsMap.put("isWarn", x.getIsWarn());

            Date date = new Date();
            long time = date.getTime() + 28800000;
            // 采集时间
            fieldsMap.put("alertTime", DateUtil.format(date, "yyyy-MM-dd HH:mm:ss"));

            if (x.getIsWarn().equals("T")) {
                WarningConfigVo wcVo = (WarningConfigVo) map.get("data");
                //
                influxdbService.insert("warnsheet", time, tagsMap, fieldsMap);
                WarnSheet warnSheet = new WarnSheet();
                BeanUtils.copyProperties(x, warnSheet);
                warnSheet.setDeviceCollectId(deviceCollect.getId());
                warnSheet.setType(deviceCollect.getDeviceType());
                warnSheet.setDeviceGatewayId(deviceCollect.getGatewayId());
                warnSheet.setAreaId(deviceCollect.getAreaId());
                warnSheet.setFloorId(deviceCollect.getFloorId());
                warnSheet.setBuildId(deviceCollect.getBuildId());
                warnSheet.setSubitem(deviceCollect.getSubitem());
                warnSheet.setWarnLevel(wcVo.getWarnLevel());
                warnSheet.setWarnSource("0");
                SysDict sd = dictMap.get(wcVo.getWarnLevel());
                warnSheet.setWarnContent(sd.getDictLabel() + ":" + StringUtils.trimToEmpty(signalTypeMap.get(x.getSignalId())) + ":" + x.getVal());
                warnSheetMapper.insert(warnSheet);
            }

            influxdbService.insert("alertsheet", time, tagsMap, fieldsMap);
        }


        logger.info("kafka接收消息： topic: " + record.topic() + "-消息内容: " + record.value());
    }

    @KafkaListener(topics = {"HEART_TOPIC"})
    public void heartMsg(ConsumerRecord<String, Object> record) {
        //topic
        String topic = record.topic();
        //消息内容
        String msg = String.valueOf(record.value());

        List<HeartSheet> heartSheets = JSON.parseArray(msg, HeartSheet.class);


        for (HeartSheet x : heartSheets) {
            // 设备主键，数据，信号主键为空时，当前数据无效
            if (null == x.getDeviceId()) {
                continue;
            }

            // 采集设备
            DeviceCollect deviceCollect = deviceCollectService.getOne(Wrappers.<DeviceCollect>lambdaQuery()
                    .eq(DeviceCollect::getDeviceCode, x.getDeviceId()));

            // 未找到采集设备
            if (null == deviceCollect) {
                continue;
            }

//            // 如果网关不存在，通过主题逆推网关,主题与网关一对一关系
//            if (null == x.getGateWayId()) {
//
//                DeviceGateway deviceGateway = deviceGatewayService.
//                        getOne(Wrappers.<DeviceGateway>lambdaQuery().
//                                eq(DeviceGateway::getSubscribeTopicId, x.getTopicId()));
//                if (null == deviceGateway) {
//                    continue;
//                }
//                x.setGateWayId(deviceGateway.getDeviceCode());
//                x.setDeviceGatewayId(deviceGateway.getId());
//            } else {
//                DeviceGateway deviceGateway = deviceGatewayService.
//                        getOne(Wrappers.<DeviceGateway>lambdaQuery().
//                                eq(DeviceGateway::getDeviceCode, x.getGateWayId()));
//                if (null == deviceGateway) {
//                    continue;
//                }
//                x.setDeviceGatewayId(deviceGateway.getId());
//            }




            memoryCacheService.heartDevice(deviceCollect.getGatewayId(), deviceCollect.getId());

//            Map<String,String> tagsMap=new HashMap<>();
//            // 网关id
//            tagsMap.put("deviceGatewayId",String.valueOf(x.getDeviceGatewayId()));
//            // 采集设备id
//            tagsMap.put("deviceCollectId",String.valueOf(deviceCollect.getId()));
//            // 分项id
//            tagsMap.put("subitem",String.valueOf(deviceCollect.getSubitem()));
//            // 采集设备所在的楼宇
//            tagsMap.put("buildId",String.valueOf(deviceCollect.getBuildId()));
//            //  采集设备所在的楼层
//            tagsMap.put("floorId",String.valueOf(deviceCollect.getFloorId()));
//            // 采集设备所在的区域
//            tagsMap.put("areaId",String.valueOf(deviceCollect.getAreaId()));
//
//
//            //fields 监测值
//            Map<String,Object> fieldsMap = new HashMap<>();
//            fieldsMap.put("val",Float.parseFloat(x.getVal()));
//            Date date=new Date();
//            long time=date.getTime()+28800000;
//            // 采集时间
//            fieldsMap.put("heartTime", DateUtil.format(date,"yyyy-MM-dd HH:mm:ss"));
//
//
//
//            influxdbService.insert("heartsheet",time,tagsMap,fieldsMap);


        }


        logger.info("kafka接收消息： topic: " + record.topic() + "-消息内容: " + record.value());
    }


}
