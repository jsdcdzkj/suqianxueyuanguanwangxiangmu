package com.jsdc.iotpt.mqtt;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aspose.words.WarningInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.common.SpringContextHolder;
import com.jsdc.iotpt.common.SysConfig;
import com.jsdc.iotpt.common.SystemKey;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.config.InfluxDBConfig;
import com.jsdc.iotpt.controller.MqttController;
import com.jsdc.iotpt.kafka.KafkaProducer;
import com.jsdc.iotpt.mapper.AlarmContentConfigMapper;
import com.jsdc.iotpt.mapper.WarnSheetMapper;
import com.jsdc.iotpt.model.*;
import com.jsdc.iotpt.model.new_alarm.AlarmContentConfig;
import com.jsdc.iotpt.model.new_alarm.AlarmRecords;
import com.jsdc.iotpt.model.sys.SysBuildArea;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.service.*;
import com.jsdc.iotpt.service.influxDB.InfluxdbService;
import com.jsdc.iotpt.service.mqtt.AlertSheetService;
import com.jsdc.iotpt.util.RedisDictDataUtil;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.DeviceCollectVo;
import com.jsdc.iotpt.vo.WarningConfigVo;
import com.jsdc.iotpt.vo.lift.LiftData;
import com.jsdc.iotpt.websocket.WsClientPool;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;

import javax.websocket.Session;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * ClassName: MqttMessageListener
 * Description:
 * date: 2023/7/15 9:13
 *
 * @author bn
 */
@Slf4j
public class MqttMessageListener implements IMqttMessageListener {

    // 订阅主题的id
    private Integer topicId;

    private KafkaProducer kafkaProducer = SpringContextHolder.getBean(KafkaProducer.class);

    private InfluxdbService influxdbService = SpringContextHolder.getBean(InfluxdbService.class);


    private WarnSheetMapper warnSheetMapper= SpringContextHolder.getBean(WarnSheetMapper.class);


    private AlarmContentConfigMapper alarmContentConfigMapper=SpringContextHolder.getBean(AlarmContentConfigMapper.class);

    private AlarmRecordsService alarmRecordsService= SpringContextHolder.getBean(AlarmRecordsService.class);



    private InfluxDBConfig influxDBConfig = SpringContextHolder.getBean(InfluxDBConfig.class);

    private DeviceCollectService deviceCollectService = SpringContextHolder.getBean(DeviceCollectService.class);

    private MemoryCacheService memoryCacheService= SpringContextHolder.getBean(MemoryCacheService.class);

    private  DeviceGatewayService deviceGatewayService=SpringContextHolder.getBean(DeviceGatewayService.class);
    private  SysBuildAreaService sysBuildAreaService=SpringContextHolder.getBean(SysBuildAreaService.class);

    private AlertSheetService alertSheetService=SpringContextHolder.getBean(AlertSheetService.class);


    private SmartEnergyReportService smartEnergyReportService=SpringContextHolder.getBean(SmartEnergyReportService.class);

    // 主题
    private ConfigTopicService configTopicService = SpringContextHolder.getBean(ConfigTopicService.class);

    // 模板
    private ConfigSignalFieldService configSignalFieldService = SpringContextHolder.getBean(ConfigSignalFieldService.class);

    // 连接
    private ConfigLinkService configLinkService = SpringContextHolder.getBean(ConfigLinkService.class);

    static List<String> listA = Arrays.asList("I_A", "I_B", "I_C");

    static List<String> listV = Arrays.asList("U_A", "U_B", "U_C");

    private RedisUtils redisUtils=SpringContextHolder.getBean(RedisUtils.class);

    private WsClientPool wsClientPool = WsClientPool.getInstance();

    private SysConfig sysConfig = SysConfig.getInstance();

    private static final ConcurrentHashMap<String, Object> sessionLocks = new ConcurrentHashMap<>();

    // 网关解析
    private JSONObject wgjx;
    // 设备解析
    private JSONObject sbjx;
    // 信号解析
    private JSONObject xhjx;
    // 心跳解析
    private JSONObject xtjx;
    // 报警解析
    private JSONObject bjjx;
    // 控制解析
    private JSONObject kzjx;
    @Value("${linkmqtt.ip}")
    private String mqttUrl;

    /**
     * 系统的mqtt客户端id
     */
    private String mqttClientId;


    /**
     * 当前连接id
     */
    private Integer linkId;

    public MqttMessageListener(String mqttClientId, Integer linkId) {
        this.mqttClientId = mqttClientId;
        this.linkId = linkId;
    }

    public void saveDataSheets(ArrayList<DataSheet> dataSheets) throws Exception {

        long startTime = System.currentTimeMillis();
        // 三项电流
        Map<Integer, List<Double>> threeA = new HashMap<>();
        // 三项电压
        Map<Integer, List<Double>> threeV = new HashMap<>();
        BatchPoints batchPoints = BatchPoints.database(influxDBConfig.database).retentionPolicy("autogen") // 设置保留策略
                .consistency(InfluxDB.ConsistencyLevel.ALL) // 设置一致性级别
                .build();
        Map<String, DeviceCollect> collectMap = new HashMap<>();

        Map<String, DeviceGateway> gatewayHashMap = new HashMap<>();
        for (DataSheet x : dataSheets) {
            // 设备主键，数据，信号主键为空时，当前数据无效
            if (null == x.getDeviceId() || null == x.getVal() || null == x.getChannelId()||null==x.getGateWayId()) {
                continue;
            }

            if (!(gatewayHashMap.containsKey(x.getGateWayId()))){
                DeviceGateway deviceGateway=deviceGatewayService.getOne(Wrappers.<DeviceGateway>lambdaQuery().
                        eq(DeviceGateway::getIsDel,0).eq(DeviceGateway::getDeviceCode,x.getGateWayId()));
                gatewayHashMap.put(deviceGateway.getDeviceCode(),deviceGateway);
            }

            DeviceGateway deviceGateway=gatewayHashMap.get(x.getGateWayId());

            if (null==deviceGateway){
                continue;
            }

            if (!collectMap.containsKey(x.getDeviceId())) {
                // 根据code获取采集设备
                DeviceCollect deviceCollect = deviceCollectService.getOne(Wrappers.<DeviceCollect>lambdaQuery()
                        .eq(DeviceCollect::getDeviceCode, x.getDeviceId()).eq(DeviceCollect::getIsDel,0).eq(DeviceCollect::getGatewayId,deviceGateway.getId()));
                collectMap.put(deviceGateway.getDeviceCode()+"-"+x.getDeviceId(), deviceCollect);
            }

            DeviceCollect deviceCollect = collectMap.get(x.getGateWayId()+"-"+x.getDeviceId());
            // 未找到采集设备
            if (null == deviceCollect) {
                continue;
            }


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

//            if ("WATER_L".equals(x.getChannelId())){
//                float f=Float.parseFloat(x.getVal());
//                Integer whVal = (int)f;
//                fieldsMap.put("val", Float.parseFloat(Integer.toHexString(whVal)));
//            }else {
            fieldsMap.put("val", Float.parseFloat(x.getVal()));
//            }
            Date date = new Date();
            long time = date.getTime() + 28800000;
            // 采集时间
            fieldsMap.put("dataTime", DateUtil.format(date, "yyyy-MM-dd HH:mm:ss"));


            Point builder = Point.measurement("datasheet").tag(tagsMap).fields(fieldsMap).time(time, TimeUnit.MILLISECONDS).build();
//            builder.time(time, TimeUnit.MILLISECONDS);
//            builder.tag(tagsMap);
//            builder.fields(fieldsMap);
//            builder.build();
            batchPoints.point(builder);
//            influxdbService.insert("datasheet", time, tagsMap, fieldsMap);


        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                influxdbService.batchInsert(batchPoints);

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
        }).start();


//        log.info("数据保存时长: {}" , (System.currentTimeMillis() - startTime));
//        System.out.println();
    }

    public void saveDataSheetsZNCZ(String dataSheets,String topic) throws Exception {
        long time = new Date().getTime() + 28800000;
        Date date = new Date();
        String[] split = topic.split("/");
        JSONObject data=JSON.parseObject(dataSheets);
        JSONObject tage=new JSONObject();
        JSONObject field=new JSONObject();
        tage.put("channelId","ENERGY_TOTAL");
        field.put("val",data.getString("energy"));
        Map<String, String> tages = getTages(tage);
        Map<String, Object> fields = getFields(field);
        tage.put("channelId","OL_STATE");
        field.put("val",data.getString("onOff"));
        Map<String, String> tages1 = getTages(tage);
        Map<String, Object> fields1 = getFields(field);
        influxdbService.insert("datasheet", time, tages1, fields1);
        tage.put("channelId","I_A");
        field.put("val",data.getString("cur"));
        Map<String, String> tages2 = getTages(tage);
        Map<String, Object> fields2 = getFields(field);
        influxdbService.insert("datasheet", time, tages2, fields2);
    }

    public void saveControlSheets(ArrayList<ControlSheet> controlSheets) throws Exception {
        BatchPoints batchPoints = BatchPoints.database(influxDBConfig.database).retentionPolicy("autogen") // 设置保留策略
                .consistency(InfluxDB.ConsistencyLevel.ALL) // 设置一致性级别
                .build();

        Map<String, DeviceCollect> collectMap = new HashMap<>();

        for (ControlSheet x : controlSheets) {
            // 设备主键，数据，信号主键为空时，当前数据无效
            if (null == x.getDeviceId() || null == x.getVal() || null == x.getChannelId()) {
                continue;
            }


            if (!collectMap.containsKey(x.getDeviceId())) {
                // 根据code获取采集设备
                DeviceCollect deviceCollect = deviceCollectService.getOne(Wrappers.<DeviceCollect>lambdaQuery()
                        .eq(DeviceCollect::getDeviceCode, x.getDeviceId()));
                collectMap.put(x.getDeviceId(), deviceCollect);
            }

            DeviceCollect deviceCollect = collectMap.get(x.getDeviceId());
            // 未找到采集设备
            if (null == deviceCollect) {
                continue;
            }
            Map<String, String> tagsMap = new HashMap<>();
            // 网关id
            tagsMap.put("deviceGatewayId", String.valueOf(deviceCollect.getGatewayId()));
            // 采集设备id
            tagsMap.put("deviceCollectId", String.valueOf(deviceCollect.getId()));
            // 信号主键
            tagsMap.put("channelId", x.getChannelId());
            // 控制结果
            tagsMap.put("controlResult", x.getControlResult());
            //fields 监测值
            Map<String, Object> fieldsMap = new HashMap<>();
            fieldsMap.put("val", Float.parseFloat(x.getVal()));
            Date date = new Date();
            long time = date.getTime() + 28800000;
            // 采集时间
            fieldsMap.put("controlTime", DateUtil.format(date, "yyyy-MM-dd HH:mm:ss"));


            Point builder = Point.measurement("controlSheet").tag(tagsMap).fields(fieldsMap).time(time, TimeUnit.MILLISECONDS).build();
            batchPoints.point(builder);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                influxdbService.batchInsert(batchPoints);
            }
        }).start();


    }


    public void saveAlertSheets(ArrayList<AlertSheet> alertSheets) throws Exception{
        log.info("MQTT告警信息：{}", JSON.toJSONString(alertSheets));
        Map<String, SysDict> dictMap = RedisDictDataUtil.getDictByType("warnType");
        Object signalTypeObj = RedisUtils.getBeanValue(MemoryCacheService.allSignalTypePrefix);
        Map<String, String> signalTypeMap =  new HashMap<>();
        if(null != signalTypeObj && signalTypeObj instanceof Map){
            signalTypeMap = (Map<String, String>)signalTypeObj;
        }
        Map<String, DeviceGateway> gatewayHashMap = new HashMap<>();
        Map<String, DeviceCollect> collectMap = new HashMap<>();
        for (AlertSheet x : alertSheets) {
            // 设备主键，数据，信号主键为空时，当前数据无效
            if (null == x.getDeviceId() || null == x.getVal() || null == x.getSignalId()||null==x.getGateWayId()) {
                continue;
            }

            if (!(gatewayHashMap.containsKey(x.getGateWayId()))){
                DeviceGateway deviceGateway=deviceGatewayService.getOne(Wrappers.<DeviceGateway>lambdaQuery().
                        eq(DeviceGateway::getIsDel,0).eq(DeviceGateway::getDeviceCode,x.getGateWayId()));
                gatewayHashMap.put(deviceGateway.getDeviceCode(),deviceGateway);
            }

            DeviceGateway deviceGateway=gatewayHashMap.get(x.getGateWayId());

            if (null==deviceGateway){
                continue;
            }

            //此代码针对领祺网关数据 ，理论上所有网关下的设备编码 根据网关配置解析以后就应该是真正的设备编码
            String tempdi = x.getDeviceId();
            if(tempdi.startsWith("random_")){
                x.setDeviceId(tempdi.replaceFirst("random_",""));
            }
            if(tempdi.endsWith("_random")){
                x.setDeviceId(tempdi.replaceFirst("_random",""));
            }

//            log.info("msg===x.setDeviceId===>>>>====== {}" , x.getDeviceId());

            if (!collectMap.containsKey(x.getDeviceId())) {
                // 根据code获取采集设备
                DeviceCollect deviceCollect = deviceCollectService.getOne(Wrappers.<DeviceCollect>lambdaQuery()
                        .eq(DeviceCollect::getDeviceCode, x.getDeviceId()).eq(DeviceCollect::getIsDel,0).eq(DeviceCollect::getGatewayId,deviceGateway.getId()));
                collectMap.put(deviceGateway.getDeviceCode()+"-"+x.getDeviceId(), deviceCollect);
            }

            DeviceCollect deviceCollect = collectMap.get(x.getGateWayId()+"-"+x.getDeviceId());
            // 未找到采集设备
            if (null == deviceCollect) {
                continue;
            }



            //
            String collectKey = String.format("%s_%s", deviceGateway.getDeviceCode(), x.getDeviceId());

            //新报警
//            DeviceCollect device = deviceCollectService.getById(deviceCollect.getId());
            LambdaQueryWrapper<AlarmContentConfig> alarmWrapper=new LambdaQueryWrapper<>();
            alarmWrapper.eq(AlarmContentConfig::getIsDel,0).like(AlarmContentConfig::getAlarmCode,x.getSignalId()).eq(AlarmContentConfig::getDeviceType,deviceCollect.getDeviceType());
            AlarmContentConfig alarmContentConfig = alarmContentConfigMapper.selectOne(alarmWrapper);
            if (StringUtils.isNotNull(alarmContentConfig)){
                AlarmRecords records = new AlarmRecords();
                records.setDeviceName(deviceCollect.getName()).setDeviceId(deviceCollect.getId()).setDeviceType(alarmContentConfig.getDeviceType())
                        .setDeviceSource(2).setAlarmLevel(alarmContentConfig.getAlarmLevel()).setAlarmContentId(alarmContentConfig.getId()).setAlarmContentStr(alarmContentConfig.getAlarmContent()+"，报警数值："+x.getVal())
                        .setIsDel(0).setAlarmCategory(alarmContentConfig.getAlarmCategory()).setAlarmStatus("1").setHandleStatus("0").setAlarmTime(x.getAlertTime()).setBuildId(deviceCollect.getBuildId())
                        .setFloorId(deviceCollect.getFloorId()).setAreaId(deviceCollect.getAreaId()).setGatewayId(deviceCollect.getGatewayId()).setAlarmType(alarmContentConfig.getAlarmType());
                alarmRecordsService.saveRecordAndExecutePlan(records);
            }

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
            WarningConfigVo wcVo = (WarningConfigVo) map.get("data");
            if (x.getIsWarn().equals("T")) {
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
                if (StringUtils.isNotBlank(x.getAlertRecover())) {
                    //是产生告警还是告警恢复  0恢复，1产生;
                    warnSheet.setS(Integer.parseInt(x.getAlertRecover()));
                }
//                warnSheet.setWarnStars(getWarnStars(wcVo,x.getVal()));
                SysDict sd = dictMap.get(wcVo.getWarnType());
                if (org.springframework.util.StringUtils.isEmpty(wcVo.getContent())){
                    warnSheet.setWarnContent(sd.getDictLabel() + ":" + StringUtils.trimToEmpty(signalTypeMap.get(x.getSignalId())) + ":" + StringUtils.strRounding(x.getVal()));
                }else{
                    warnSheet.setWarnContent( wcVo.getContent().replace("$val",StringUtils.strRounding(x.getVal())));
                }

                warnSheet.setWarnCategory("4");
                warnSheet.setWarnType(wcVo.getWarnType());
                warnSheet.setWarnDeviceType("1");
                warnSheetMapper.insert(warnSheet);



                try{
                    System.out.println("======send wss =====start");
                    LambdaQueryWrapper<SysBuildArea> wrapper=new LambdaQueryWrapper();
                    wrapper.eq(SysBuildArea::getId,deviceCollect.getAreaId()).eq(SysBuildArea::getIsDel,0);
                    SysBuildArea sba = sysBuildAreaService.getOne(wrapper);

                    MqttController mqttController= SpringContextHolder.getBean(MqttController.class);
//                    mqttController.sendMsg(warningInfoVo);
                    System.out.println("======send wss =====end");
                }catch (Exception e){
                    System.out.println("======send wss =====error");
                    e.printStackTrace();
                    log.error(e.getMessage());
                }
            }

            influxdbService.insert("alertsheet", time, tagsMap, fieldsMap);


        }







    }

    public void saveHeartSheets(HeartSheet bean) throws Exception{

        if (null == bean.getDeviceId()) {
            return;
        }
        // 采集设备
        DeviceGateway deviceGateway = deviceGatewayService.getOne(Wrappers.<DeviceGateway>lambdaQuery()
                .eq(DeviceGateway::getDeviceCode, bean.getDeviceId()).eq(DeviceGateway::getIsDel,0));

        if (null == deviceGateway) {
            return;
        }

        List<DeviceCollect> collects=deviceCollectService.getBaseMapper().
                selectList(Wrappers.<DeviceCollect>lambdaQuery().
                        eq(DeviceCollect::getGatewayId,deviceGateway.getId()).
                        eq(DeviceCollect::getIsDel,0));

        collects.forEach(x->{
            memoryCacheService.heartDevice(x.getGatewayId(),x.getId());
        });
    }


    @Override
    public void messageArrived(String topic, MqttMessage message) {

        try {
            long startTime = System.currentTimeMillis();
            String msgType = null;
            String payload = new String(message.getPayload());

            if (payload.indexOf(":") == -1) {
                return;
            }
            // 一、查询当前topic详细信息
            ConfigTopic topicData = configTopicService.
                    getOne(Wrappers.<ConfigTopic>lambdaQuery().
                            eq(ConfigTopic::getLinkId, linkId).
                            eq(ConfigTopic::getTopicName, topic).
                            eq(ConfigTopic::getTopicStatus, 1));

            //智能插座
            if(topic.contains("devicesend")){
                System.out.println(payload);
                saveDataSheetsZNCZ(payload,topic);
                return;
            }


            // 二、获取topic类型字段
            String type = topicData.getTemplateKey();

            JSONObject fromObject = JSONObject.parseObject(payload);

            if (null != type) {
                Object value = fromObject.get(type);
                if (null == value) {
                    return;
                }
                String val = value.toString();

                if (val.equals(topicData.getAlarmVal())) {// 告警解析模板
                    if (null != topicData.getTransferId()) {
                        ConfigSignalField configSignalField = new ConfigSignalField();
                        configSignalField.setModelId(topicData.getTransferId());
                        configSignalField.setType("2");
                        // 系统字段对比表
                        List<ConfigSignalField> configSignalFields = configSignalFieldService.getList(configSignalField);
                        // 对比数据拆分成网关解析，信号解析，设备解析
                        telData(2, configSignalFields);

                        Map map = new HashMap();
                        AlertSheet alertSheet = new AlertSheet();
                        ArrayList<AlertSheet> alertSheets = new ArrayList<>();
                        boolean isArray = testAlert(fromObject, map, alertSheet, alertSheets, bjjx);

                        if (!isArray) {
                            alertSheets.add(alertSheet);
                        }

                        saveAlertSheets(alertSheets);
//                        kafkaProducer.sendMsg(G.KAFKA_ALERTING_TOPIC, JSON.toJSONString(alertSheets));
                    }
                    msgType = "2";

                } else if (val.equals(topicData.getControlVal())) { // 控制解析模板
                    if (null != topicData.getTransferId()) {
                        ConfigSignalField configSignalField = new ConfigSignalField();
                        configSignalField.setModelId(topicData.getTransferId());
                        configSignalField.setType("4");
                        // 系统字段对比表
                        List<ConfigSignalField> configSignalFields = configSignalFieldService.getList(configSignalField);
                        // 对比数据拆分成网关解析，信号解析，设备解析
                        telData(4, configSignalFields);
                        Map map = new HashMap();
                        ControlSheet controlSheet = new ControlSheet();
                        ArrayList<ControlSheet> controlSheets = new ArrayList<>();

                        boolean isArray = testControl(fromObject, map, controlSheet, controlSheets, kzjx);
                        if (!isArray) {
                            controlSheets.add(controlSheet);
                        }
                        saveControlSheets(controlSheets);
                    }
                    msgType = "4";
                } else if (val.equals(topicData.getHeartVal())) { // 心跳解析模板
                    if (null != topicData.getTransferId()) {
                        ConfigSignalField configSignalField = new ConfigSignalField();
                        configSignalField.setModelId(topicData.getTransferId());
                        configSignalField.setType("3");
                        // 系统字段对比表
                        List<ConfigSignalField> configSignalFields = configSignalFieldService.getList(configSignalField);
                        // 对比数据拆分成网关解析，信号解析，设备解析
                        telData(3, configSignalFields);

                        Map map = new HashMap();
                        HeartSheet heartSheet = new HeartSheet();
                        testHeart(fromObject, map, heartSheet, xtjx);
//                        List<HeartSheet> heartSheets = new ArrayList<>();
//                        heartSheets.add(heartSheet);
                        saveHeartSheets(heartSheet);
//                        kafkaProducer.sendMsg(G.KAFKA_HEARTBEAT_TOPIC, JSON.toJSONString(heartSheets));
                    }
                    msgType = "3";
                } else { // 数据解析模板
                    if (null != topicData.getTransferId()) {
                        ConfigSignalField configSignalField = new ConfigSignalField();
                        configSignalField.setModelId(topicData.getTransferId());
                        configSignalField.setType("1");
                        // 系统字段对比表
                        List<ConfigSignalField> configSignalFields = configSignalFieldService.getList(configSignalField);
                        // 对比数据拆分成网关解析，信号解析，设备解析
                        telData(1, configSignalFields);
                        // 数据解析
                        Map map = new HashMap();
                        DataSheet dataSheet = new DataSheet();
                        ArrayList<DataSheet> dataSheets = new ArrayList<>();
                        testData(fromObject, map, dataSheet, dataSheets, wgjx, sbjx, xhjx);
//                        log.info("数据解析时长: {}" , (System.currentTimeMillis() - startTime));
                        saveDataSheets(dataSheets);
//                        kafkaProducer.sendMsg(G.KAFKA_DATA_TOPIC,JSON.toJSONString(dataSheets));
                    }
                    msgType = "1";
                }

            } else {
                if (null != topicData.getTransferId()) {
                    ConfigSignalField configSignalField = new ConfigSignalField();
                    configSignalField.setModelId(topicData.getTransferId());
                    configSignalField.setType("1");
                    // 系统字段对比表
                    List<ConfigSignalField> configSignalFields = configSignalFieldService.getList(configSignalField);
                    // 对比数据拆分成网关解析，信号解析，设备解析
                    telData(1, configSignalFields);

                    // 数据解析
                    Map map = new HashMap();
                    DataSheet dataSheet = new DataSheet();
                    ArrayList<DataSheet> dataSheets = new ArrayList<>();
                    testData(fromObject, map, dataSheet, dataSheets, wgjx, sbjx, xhjx);
                    saveDataSheets(dataSheets);
//                    kafkaProducer.sendMsg(G.KAFKA_DATA_TOPIC,JSON.toJSONString(dataSheets));
                }
                msgType = "1";
                // 当前数据未配置指定解析字段，是否已默认的数据类型解析？
            }

            sendMsg(String.format("C%sT%s%s", linkId, msgType, topic), topic, payload);
        } catch (Exception e) {
            return;
        }

    }


    private DataSheet setVal(String key, String value, DataSheet dataSheet) throws Exception {
        Field nameField = dataSheet.getClass().getField(key);
        char[] cs = key.toCharArray();
        cs[0] -= 32;
        Method set = dataSheet.getClass().getMethod("set" + String.valueOf(cs), nameField.getType());
        if (value != null && !value.equals("")) {
            if (nameField.getType().getName().contains("String")) {
                set.invoke(dataSheet, value);
            } else if (nameField.getType().getName().contains("Integer")) {
                set.invoke(dataSheet, Integer.parseInt(value));
            } else if (nameField.getType().getName().contains("Date")) {
                if (value.length() > 10 && value.substring(0, 10).matches("[\\d]{4}-[\\d]{2}-[\\d]{2}")) {
                    Date date = new DateTime(value.replaceFirst(" ", "T")).toDate();
                    set.invoke(dataSheet, date);
                } else if (value.matches("[\\d]{10}|[\\d]{13}")) { // 有可能是10位或者13位时间戳
                    if (value.matches("[\\d]{10}")) {
                        value = value + "000";
                    }
                    Date date = new DateTime(Long.valueOf(value)).toDate();
                    set.invoke(dataSheet, date);
                }
            }
        }

        return dataSheet;
    }

    private HeartSheet setVal(String key, String value, HeartSheet heartSheet) throws Exception {
        Field nameField = heartSheet.getClass().getField(key);
        char[] cs = key.toCharArray();
        cs[0] -= 32;
        Method set = heartSheet.getClass().getMethod("set" + String.valueOf(cs), nameField.getType());
        if (value != null && !value.equals("")) {
            if (nameField.getType().getName().contains("String")) {
                set.invoke(heartSheet, value);
            } else if (nameField.getType().getName().contains("Integer")) {
                set.invoke(heartSheet, Integer.parseInt(value));
            } else if (nameField.getType().getName().contains("Date")) {
                if (value.length() > 10 && value.substring(0, 10).matches("[\\d]{4}-[\\d]{2}-[\\d]{2}")) {
                    Date date = new DateTime(value.replaceFirst(" ", "T")).toDate();
                    set.invoke(heartSheet, date);
                } else if (value.matches("[\\d]{10}|[\\d]{13}")) { // 有可能是10位或者13位时间戳
                    if (value.matches("[\\d]{10}")) {
                        value = value + "000";
                    }
                    Date date = new DateTime(Long.valueOf(value)).toDate();
                    set.invoke(heartSheet, date);
                }
            }
        }

        return heartSheet;
    }

    private AlertSheet setVal(String key, String value, AlertSheet alertSheet) throws Exception {
        Field nameField = alertSheet.getClass().getField(key);
        char[] cs = key.toCharArray();
        cs[0] -= 32;
        Method set = alertSheet.getClass().getMethod("set" + String.valueOf(cs), nameField.getType());
        if (value != null && !value.equals("")) {
            if (nameField.getType().getName().contains("String")) {
                set.invoke(alertSheet, value);
            } else if (nameField.getType().getName().contains("Integer")) {
                set.invoke(alertSheet, Integer.parseInt(value));
            } else if (nameField.getType().getName().contains("Date")) {
                if (value.length() > 10 && value.substring(0, 10).matches("[\\d]{4}-[\\d]{2}-[\\d]{2}")) {
                    Date date = new DateTime(value.replaceFirst(" ", "T")).toDate();
                    set.invoke(alertSheet, date);
                } else if (value.matches("[\\d]{10}|[\\d]{13}")) { // 有可能是10位或者13位时间戳
                    if (value.matches("[\\d]{10}")) {
                        value = value + "000";
                    }
                    Date date = new DateTime(Long.valueOf(value)).toDate();
                    set.invoke(alertSheet, date);
                }
            }
        }

        return alertSheet;
    }

    private ControlSheet setVal(String key, String value, ControlSheet controlSheet) throws Exception {
        Field nameField = controlSheet.getClass().getField(key);
        char[] cs = key.toCharArray();
        cs[0] -= 32;
        Method set = controlSheet.getClass().getMethod("set" + String.valueOf(cs), nameField.getType());
        if (value != null && !value.equals("")) {
            if (value.contains("random")) {
                value = value.split("_")[1];
            }
            if (nameField.getType().getName().contains("String")) {
                set.invoke(controlSheet, value);
            } else if (nameField.getType().getName().contains("Integer")) {
                set.invoke(controlSheet, Integer.parseInt(value));
            } else if (nameField.getType().getName().contains("Date")) {
                if (value.length() > 10 && value.substring(0, 10).matches("[\\d]{4}-[\\d]{2}-[\\d]{2}")) {
                    Date date = new DateTime(value.replaceFirst(" ", "T")).toDate();
                    set.invoke(controlSheet, date);
                } else if (value.matches("[\\d]{10}|[\\d]{13}")) { // 有可能是10位或者13位时间戳
                    if (value.matches("[\\d]{10}")) {
                        value = value + "000";
                    }
                    Date date = new DateTime(Long.valueOf(value)).toDate();
                    set.invoke(controlSheet, date);
                }
            }
        }

        return controlSheet;
    }


    public boolean iteraJson(String str, Map res, DataSheet dataSheet, JSONObject wabc, JSONObject sabc, JSONObject xabc) throws Exception {
        //因为json串中不一定有逗号，但是一定有：号，所以这里判断没有则已经value了
        if (str.toString().indexOf(":") == -1) {
            return true;
        }
        JSONObject fromObject = JSON.parseObject(str);
        Iterator keys = fromObject.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next().toString();
            Object value = fromObject.get(key);
            if (iteraJson(value.toString(), res, dataSheet, wabc, sabc, xabc)) {
                res.put(key, value);
                if (wabc.get(key) != null) {
                    setVal(wabc.get(key).toString(), value.toString(), dataSheet);
                }
                if (sabc.get(key) != null) {
                    setVal(sabc.get(key).toString(), value.toString(), dataSheet);
                }
                if (xabc.get(key) != null) {
                    setVal(xabc.get(key).toString(), value.toString(), dataSheet);
                }

            }
        }
        return false;
    }

    public boolean iteraJson(String str, Map res, HeartSheet dataSheet, JSONObject wabc) throws Exception {
        //因为json串中不一定有逗号，但是一定有：号，所以这里判断没有则已经value了
        if (str.toString().indexOf(":") == -1) {
            return true;
        }
        JSONObject fromObject = JSON.parseObject(str);
        Iterator keys = fromObject.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next().toString();
            Object value = fromObject.get(key);
            if (iteraJson(value.toString(), res, dataSheet, wabc)) {
                res.put(key, value);
                if (wabc.get(key) != null) {
                    setVal(wabc.get(key).toString(), value.toString(), dataSheet);
                }
            }
        }
        return false;
    }

    public boolean iteraJson(String str, Map res, AlertSheet dataSheet, JSONObject jabc) throws Exception {
        //因为json串中不一定有逗号，但是一定有：号，所以这里判断没有则已经value了
        if (str.toString().indexOf(":") == -1) {
            return true;
        }
        JSONObject fromObject = JSON.parseObject(str);
        Iterator keys = fromObject.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next().toString();
            Object value = fromObject.get(key);
            if (iteraJson(value.toString(), res, dataSheet, jabc)) {
                res.put(key, value);
                if (jabc.get(key) != null) {
                    setVal(jabc.get(key).toString(), value.toString(), dataSheet);
                }
            }
        }
        return false;
    }

    public boolean iteraJson(String str, Map res, ControlSheet controlSheet, JSONObject jabc) throws Exception {
        //因为json串中不一定有逗号，但是一定有：号，所以这里判断没有则已经value了
        if (str.toString().indexOf(":") == -1) {
            return true;
        }
        JSONObject fromObject = JSON.parseObject(str);
        Iterator keys = fromObject.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next().toString();
            Object value = fromObject.get(key);
            if (iteraJson(value.toString(), res, controlSheet, jabc)) {
                res.put(key, value);
                if (jabc.get(key) != null) {
                    setVal(jabc.get(key).toString(), value.toString(), controlSheet);
                }
            }
        }
        return false;
    }


    /**
     * 数据解析
     */
    public boolean testData(JSONObject fromObject, Map map, DataSheet dataSheet, List<DataSheet> list, JSONObject wabc, JSONObject sabc, JSONObject xabc) throws Exception {
        Set<String> keys = fromObject.keySet();
        dataSheet.setTopicId(topicId);
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = fromObject.get(key);
            String val = value.toString();
            if (val.indexOf("[{") == -1) {
                //说明不存在数组json即格式为："[{" 开头的数据。可以允许是[10,11,12]的非json数组
                if (val.indexOf(":") == -1 || (val.length() > 10 && val.substring(0, 10).matches("[\\d]{4}-[\\d]{2}-[\\d]{2}"))) {
                    //当字符串中不存在：说明已经是值了，如果存在：也可能是日期类型的数据，所以用正则表达式匹配，如果是日期，就直接放入Map中
                    map.put(key.replaceAll("\"", ""), val);
                    if (wabc.getString(key) != null) {
                        dataSheet = setVal(wabc.getString(key), val, dataSheet);
                    }
                    if (sabc.getString(key) != null) {
                        dataSheet = setVal(sabc.getString(key), val, dataSheet);
                    }
                    if (xabc.getString(key) != null) {
                        dataSheet = setVal(xabc.getString(key), val, dataSheet);
                    }
                } else {

                    iteraJson(val, map, dataSheet, wabc, sabc, xabc);

                }
            }
        }

        Iterator<String> iterator2 = keys.iterator();
        while (iterator2.hasNext()) {
            String key = iterator2.next();
            Object value = fromObject.get(key);
            String val = value.toString();
            if (val.indexOf("[{") != -1) {
                //包含随机ID
                if (key.contains("random")) {
                    if (wabc.getString("random") != null) {
                        setVal(wabc.getString("random"), key.split("_")[0], dataSheet);
                    }
                    if (sabc.getString("random") != null) {
                        setVal(sabc.getString("random"), key.split("_")[0], dataSheet);
                    }
                }
                //说明存在数组json即格式为：[{开头的json数组
                if (val.indexOf("[{") == 0) {
                    //说明当前value就是一个json数组
                    //去除[括号
                    JSONArray jsonArray = JSONArray.parseArray(val);
                    for (Object o : jsonArray) {
                        JSONObject jsonObject = JSONObject.parseObject(o.toString());
                        testData(jsonObject, map, dataSheet, list, wabc, sabc, xabc);
//                        Iterator<String> iterator1 = xabc.keySet().iterator();
//                        while (iterator1.hasNext()) {
//                            //厂商参数key
//                            String next = iterator1.next();
//                            //对应数据值参数
//                            String xkey = xabc.getString(next);
////                            if(isJson(jsonObject.getString(next))){
////                                JSONObject valObject = JSONObject.parseObject(val);
////                                testData(valObject, map, dataSheet, list,wabc,sabc,xabc);
////                            }else {
//                                setVal(xkey, jsonObject.getString(next), dataSheet);
////                            }
//
//                        }
//                        Iterator<String> iterator3 = sabc.keySet().iterator();
//                        while (iterator3.hasNext()) {
//                            //厂商参数key
//                            String next = iterator3.next();
//                            //对应数据值参数
//                            String skey = sabc.getString(next);
////                            if(isJson(jsonObject.getString(next))){
////                                JSONObject valObject = JSONObject.parseObject(val);
////                                testData(valObject, map, dataSheet, list,wabc,sabc,xabc);
////                            }else {
//                                setVal(skey, jsonObject.getString(next), dataSheet);
////                            }
//
//                        }
                        DataSheet sheet = new DataSheet();
                        BeanUtils.copyProperties(dataSheet, sheet);
                        list.add(sheet);
                    }
                    map.put(key.replaceAll("\"", ""), jsonArray);
                } else {
                    //说明value可能是一个json，这个json中任然包含数组。例如：{inner:[{a:1,b:2,c:3}]}
                    if (val.indexOf(":") == -1) {
                        return true;
                    }
                    JSONObject valObject = JSONObject.parseObject(val);
                    testData(valObject, map, dataSheet, list, wabc, sabc, xabc);//符合当前递归条件
                }
            }
        }
        return false;
    }


    /**
     * 告警解析
     *
     * @param fromObject
     * @param map
     * @param alertSheet
     * @param list
     * @param jabc
     */
    public boolean testAlert(JSONObject fromObject, Map map, AlertSheet alertSheet, ArrayList<AlertSheet> list, JSONObject jabc) throws Exception {

        boolean isArray = false;
        Set<String> keys = fromObject.keySet();
        Iterator<String> iterator = keys.iterator();
        alertSheet.setTopicId(topicId);
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = fromObject.get(key);
            String val = value.toString();
            if (val.indexOf("[{") == -1) {
                //说明不存在数组json即格式为："[{" 开头的数据。可以允许是[10,11,12]的非json数组
                if (val.indexOf(":") == -1 || (val.length() > 10 && val.substring(0, 10).matches("[\\d]{4}-[\\d]{2}-[\\d]{2}"))) {
                    //当字符串中不存在：说明已经是值了，如果存在：也可能是日期类型的数据，所以用正则表达式匹配，如果是日期，就直接放入Map中
                    map.put(key.replaceAll("\"", ""), val);
                    if (jabc.getString(key) != null) {
                        alertSheet = setVal(jabc.getString(key), val, alertSheet);
                    }
                } else {
                    iteraJson(val, map, alertSheet, jabc);
                }
            }
        }

        Iterator<String> iterator2 = keys.iterator();
        while (iterator2.hasNext()) {
            String key = iterator2.next();
            Object value = fromObject.get(key);
            String val = value.toString();
            if (val.indexOf("[{") != -1) {
                isArray = true;
                //包含随机ID
                if (key.contains("random")) {
                    if (jabc.getString("random") != null) {
                        setVal(jabc.getString("random"), key.split("_")[1], alertSheet);
                    }
                }
                //说明存在数组json即格式为：[{开头的json数组
                if (val.indexOf("[{") == 0) {
                    //说明当前value就是一个json数组
                    //去除[括号
                    JSONArray jsonArray = JSONArray.parseArray(val);
                    for (Object o : jsonArray) {
                        JSONObject jsonObject = JSONObject.parseObject(o.toString());
                        Iterator<String> iterator1 = jabc.keySet().iterator();
                        while (iterator1.hasNext()) {
                            //厂商参数key
                            String next = iterator1.next();
                            //对应数据值参数
                            String xkey = jabc.getString(next);
                            setVal(xkey, jsonObject.getString(next), alertSheet);
                        }
                        AlertSheet sheet = new AlertSheet();
                        BeanUtils.copyProperties(alertSheet, sheet);
                        list.add(sheet);
                    }
                    map.put(key.replaceAll("\"", ""), jsonArray);
                } else {
                    //说明value可能是一个json，这个json中任然包含数组。例如：{inner:[{a:1,b:2,c:3}]}
                    if (val.indexOf(":") == -1) {
                        return true;
                    }
                    JSONObject valObject = JSONObject.parseObject(val);
                    testAlert(valObject, map, alertSheet, list, jabc);//符合当前递归条件
                }
            }
        }
        return isArray;


    }

    /**
     * 控制解析
     *
     * @param fromObject
     * @param map
     * @param controlSheet
     * @param controlSheets
     * @param kzjx
     * @return
     */
    private boolean testControl(JSONObject fromObject, Map map, ControlSheet controlSheet, ArrayList<ControlSheet> controlSheets, JSONObject kzjx) throws Exception {
        boolean isArray = false;
        Set<String> keys = fromObject.keySet();
        Iterator<String> iterator = keys.iterator();
        controlSheet.setTopicId(topicId);


        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = fromObject.get(key);
            String val = value.toString();
            if (val.indexOf("[{") == -1) {
                //说明不存在数组json即格式为："[{" 开头的数据。可以允许是[10,11,12]的非json数组
                if (val.indexOf(":") == -1 || (val.length() > 10 && val.substring(0, 10).matches("[\\d]{4}-[\\d]{2}-[\\d]{2}"))) {
                    //当字符串中不存在：说明已经是值了，如果存在：也可能是日期类型的数据，所以用正则表达式匹配，如果是日期，就直接放入Map中
                    map.put(key.replaceAll("\"", ""), val);
                    if (kzjx.getString(key) != null) {
                        controlSheet = setVal(kzjx.getString(key), val, controlSheet);
                    }
                } else {
                    iteraJson(val, map, controlSheet, kzjx);
                }
            }
        }

//        Iterator<String> iterator2 = keys.iterator();
//        while (iterator2.hasNext()) {
//            String key = iterator2.next();
//            Object value = fromObject.get(key);
//            String val = value.toString();
//            if (val.indexOf("[{") != -1) {
//                isArray=true;
//                //包含随机ID
//                if (key.contains("random")) {
//                    if (jabc.getString("random") != null) {
//                        setVal(jabc.getString("random"), key.split("_")[1], alertSheet);
//                    }
//                }
//                //说明存在数组json即格式为：[{开头的json数组
//                if (val.indexOf("[{") == 0) {
//                    //说明当前value就是一个json数组
//                    //去除[括号
//                    JSONArray jsonArray = JSONArray.parseArray(val);
//                    for (Object o : jsonArray) {
//                        JSONObject jsonObject = JSONObject.parseObject(o.toString());
//                        Iterator<String> iterator1 = jabc.keySet().iterator();
//                        while (iterator1.hasNext()) {
//                            //厂商参数key
//                            String next = iterator1.next();
//                            //对应数据值参数
//                            String xkey = jabc.getString(next);
//                            setVal(xkey, jsonObject.getString(next), alertSheet);
//                        }
//                        AlertSheet sheet = new AlertSheet();
//                        BeanUtils.copyProperties(alertSheet, sheet);
//                        list.add(sheet);
//                    }
//                    map.put(key.replaceAll("\"", ""), jsonArray);
//                } else {
//                    //说明value可能是一个json，这个json中任然包含数组。例如：{inner:[{a:1,b:2,c:3}]}
//                    if (val.indexOf(":") == -1) {
//                        return true;
//                    }
//                    JSONObject valObject = JSONObject.parseObject(val);
//                    testAlert(valObject, map, alertSheet, list,jabc);//符合当前递归条件
//                }
//            }
//        }
        return isArray;


    }


    /**
     * 心跳解析
     *
     * @param fromObject
     * @param map
     * @param heartSheet
     * @param xabc
     */
    public boolean testHeart(JSONObject fromObject, Map map, HeartSheet heartSheet, JSONObject xabc) throws Exception {
        Set<String> keys = fromObject.keySet();
        Iterator<String> iterator = keys.iterator();
        heartSheet.setTopicId(topicId);
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = fromObject.get(key);
            String val = value.toString();
            if (val.indexOf("[{") == -1) {
                //说明不存在数组json即格式为："[{" 开头的数据。可以允许是[10,11,12]的非json数组
                if (val.indexOf(":") == -1 || (val.length() > 10 && val.substring(0, 10).matches("[\\d]{4}-[\\d]{2}-[\\d]{2}"))) {
                    //当字符串中不存在：说明已经是值了，如果存在：也可能是日期类型的数据，所以用正则表达式匹配，如果是日期，就直接放入Map中
                    map.put(key.replaceAll("\"", ""), val);
                    if (xabc.getString(key) != null) {
                        heartSheet = setVal(xabc.getString(key), val, heartSheet);
                    }
                } else {
                    iteraJson(val, map, heartSheet, xabc);
                }
            }
        }

        // 正常心跳只有一层 如果有多层 将此处注释释放
//        Iterator<String> iterator2 = keys.iterator();
//        while (iterator2.hasNext()) {
//            String key = iterator2.next();
//            Object value = fromObject.get(key);
//            String val = value.toString();
//            if (val.indexOf("[{") != -1) {
//                //包含随机ID
//                if (key.contains("random")) {
//                    if (xabc.getString("random") != null) {
//                        setVal(xabc.getString("random"), key.split("_")[1], heartSheet);
//                    }
//                    if (xabc.getString("random") != null) {
//                        setVal(xabc.getString("random"), key.split("_")[1], heartSheet);
//                    }
//                }
//                //说明存在数组json即格式为：[{开头的json数组
//                if (val.indexOf("[{") == 0) {
//                    //说明当前value就是一个json数组
//                    //去除[括号
//                    JSONArray jsonArray = JSONArray.parseArray(val);
//                    for (Object o : jsonArray) {
//                        JSONObject jsonObject = JSONObject.parseObject(o.toString());
//                        Iterator<String> iterator1 = xabc.keySet().iterator();
//                        while (iterator1.hasNext()) {
//                            //厂商参数key
//                            String next = iterator1.next();
//                            //对应数据值参数
//                            String xkey = xabc.getString(next);
//                            setVal(xkey, jsonObject.getString(next), heartSheet);
//                        }
//                        HeartSheet sheet = new HeartSheet();
//                        BeanUtils.copyProperties(sheet, heartSheet);
//                        list.add(sheet);
//                    }
//                    map.put(key.replaceAll("\"", ""), jsonArray);
//                } else {
//                    //说明value可能是一个json，这个json中任然包含数组。例如：{inner:[{a:1,b:2,c:3}]}
//                    if (val.indexOf(":") == -1) {
//                        return true;
//                    }
//                    JSONObject valObject = JSONObject.parseObject(val);
//                    testHeart(valObject, map, heartSheet, list,xabc);//符合当前递归条件
//                }
//            }
//        }


        return false;

    }


    /**
     * type：1数据 2告警 3心跳
     * 数据模板解析成网关，信号，设备，心跳，报警模板
     *
     * @param configSignalFields
     */
    public void telData(Integer type, List<ConfigSignalField> configSignalFields) {
        if (configSignalFields.isEmpty()) {
            return;
        }
        if (1 == type && null != wgjx && null != sbjx && null != xhjx) {
            wgjx.clear();
            sbjx.clear();
            xhjx.clear();
        } else if (2 == type && null != bjjx) {
            bjjx.clear();
        } else if (3 == type && null != xtjx) {
            xtjx.clear();
        } else if (4 == type && null != kzjx) {
            kzjx.clear();
        }
        configSignalFields.forEach(x -> {
            if (1 == type) {
                SystemKey systemKey = SystemKey.getSeemingKey(x.getSystemKey());
                switch (systemKey) {
                    case GATEWAY_INFO:
                        if (null == wgjx) {
                            wgjx = new JSONObject();
                        }
                        wgjx.put(x.getGatewayKey(), x.getSystemKey());
                        break;
                    case DEVICE_INFO:
                        if (null == sbjx) {
                            sbjx = new JSONObject();
                        }
                        sbjx.put(x.getGatewayKey(), x.getSystemKey());
                        break;
                    case SIGNAL_INFO:
                        if (null == xhjx) {
                            xhjx = new JSONObject();
                        }
                        xhjx.put(x.getGatewayKey(), x.getSystemKey());
                        break;
                    default:
                        break;
                }
            } else if (2 == type) {
                if (null == bjjx) {
                    bjjx = new JSONObject();
                }
                bjjx.put(x.getGatewayKey(), x.getSystemKey());
            } else if (3 == type) {
                if (null == xtjx) {
                    xtjx = new JSONObject();
                }
                xtjx.put(x.getGatewayKey(), x.getSystemKey());
            } else if (4 == type) {
                if (null == kzjx) {
                    kzjx = new JSONObject();
                }
                kzjx.put(x.getGatewayKey(), x.getSystemKey());
            }

        });
    }



    /**
     * 符合条件需向前端推送消息
     *
     * @param key
     * @param topic
     * @param msg
     */
    public void sendMsg(String key, String topic, String msg) {
        if (sysConfig.getTopicOnline().containsKey(key) &&
                sysConfig.getTopicOnline().get(key)) {
            if (wsClientPool.needThisDevice(topic)) {

                Session wsSession = wsClientPool.getOnlineClientMap().get(topic);
                if (wsSession == null) {
                    return;
                }

                try {
                    // 异步调用
                    wsSession.getAsyncRemote().sendText(msg);
                    sysConfig.getTopicOnline().put(key, false);
                } catch (Throwable e) {
                    return;
                }

            }
        }


    }

    public String getWarnStars(WarningConfigVo configVo,String val){
        String star="3";
        try{
        if (configVo.getWarnType().equals(G.CHANNELID_ELECTRICITY_NUM)||configVo.getWarnType().equals(G.CHANNELID_WATER_NUM)){
            if (configVo.getDetails().size()>0){
                for (WarningSignDetails detail : configVo.getDetails()) {
                    if (detail.getValueBegin()<Double.parseDouble(val)&&Double.parseDouble(val)<detail.getValueEnd()&&detail.getIsEnable()==0){
                        star=detail.getWarnLevel();
                        return star;
                    }
                }
            }
        }
        }catch (Exception e){
            System.out.println(e);
        }
        return star;
    }

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }



    private SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Map<String,String> getTages(JSONObject jsonObject){
        Map<String,String> tagsMap=new HashMap<>();

        tagsMap.put("areaId",jsonObject.getString("areaId"));

        tagsMap.put("buildId",jsonObject.getString("buildId"));

        tagsMap.put("channelId",jsonObject.getString("channelId"));

        tagsMap.put("deviceCollectId",jsonObject.getString("deviceCollectId"));

        tagsMap.put("deviceGatewayId","");

        tagsMap.put("deviceType",jsonObject.getString("deviceType"));

        tagsMap.put("floorId",jsonObject.getString("floorId"));

        tagsMap.put("subitem","");
        return tagsMap;
    }

    public Map<String, Object> getFields(JSONObject jsonObject){
        Map<String, Object> fieldsMap = new HashMap<>();
        fieldsMap.put("val", jsonObject.getFloat("val"));
        Date date = new Date();
        // 采集时间
        fieldsMap.put("dataTime", DateUtil.format(date, "yyyy-MM-dd HH:mm:ss"));
        return fieldsMap;
    }




    public Map<String,String> getLiftTages(LiftData lift){
          Map<String,String> tagsMap=new HashMap<>();
        if (null!=lift.getDeviceCode())
            tagsMap.put("deviceCode",lift.getDeviceCode());
        if (null!=lift.getDeviceSn())
            tagsMap.put("deviceSn",lift.getDeviceSn());
        if (null!=lift.getStatus())
            tagsMap.put("status",lift.getStatus());
        if (null!=lift.getUploadTime())
            tagsMap.put("uploadTime",format.format(lift.getUploadTime()));
            tagsMap.put("systemTime",format.format(new Date()));
        if (null!=lift.getDoor())
            tagsMap.put("door",lift.getDoor());
        if (null!=lift.getDoorLock())
            tagsMap.put("doorLock",lift.getDoorLock());
        if (null!=lift.getPassengerIn())
            tagsMap.put("passengerIn",lift.getPassengerIn());
        if (null!=lift.getMotion())
            tagsMap.put("motion",lift.getMotion());
        return tagsMap;
    }

    public Map<String, Object> getLiftFields(LiftData lift){
        Map<String, Object> fieldsMap = new HashMap<>();
        if (null!=lift.getCumulativeRunTimeVal())
        fieldsMap.put("cumulativeRunTimeVal", lift.getCumulativeRunTimeVal());
        if (null!=lift.getCumulativeRunNumVal())
        fieldsMap.put("cumulativeRunNumVal", lift.getCumulativeRunNumVal());
        if (null!=lift.getCumulativeRunTimeFVal())
            fieldsMap.put("cumulativeRunTimeFVal", lift.getCumulativeRunTimeFVal());
        if (null!=lift.getCumulativeRunNumFVal())
            fieldsMap.put("cumulativeRunNumFVal", lift.getCumulativeRunNumFVal());
        if (null!=lift.getMileageVal())
        fieldsMap.put("mileageVal", lift.getMileageVal());
        if (null!=lift.getFloorVal())
        fieldsMap.put("floorVal", lift.getFloorVal());
        if (null!=lift.getMotionVal())
        fieldsMap.put("motionVal", lift.getMotionVal());
        if (null!=lift.getVelocityVal())
        fieldsMap.put("velocityVal", lift.getVelocityVal());
        if (null!=lift.getPassengerIn())
        fieldsMap.put("passengerInVal", lift.getPassengerInVal());
        if (null!=lift.getDoorVal())
        fieldsMap.put("doorVal", lift.getDoorVal());
        if (null!=lift.getStatusVal())
        fieldsMap.put("statusVal", lift.getStatusVal());
        Date date = new Date();
        long time = date.getTime() + 28800000;
        // 采集时间
        fieldsMap.put("dataTime", DateUtil.format(date, "yyyy-MM-dd HH:mm:ss"));

        return fieldsMap;
    }

}
