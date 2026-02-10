package com.jsdc.iotpt.mqtt;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.common.minio.service.MinioTemplate;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.common.SpringContextHolder;
import com.jsdc.iotpt.common.SysConfig;
import com.jsdc.iotpt.common.SystemKey;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.config.InfluxDBConfig;
import com.jsdc.iotpt.kafka.KafkaProducer;
import com.jsdc.iotpt.mapper.GroundLocksMapper;
import com.jsdc.iotpt.model.*;
import com.jsdc.iotpt.service.*;
import com.jsdc.iotpt.service.influxDB.InfluxdbService;
import com.jsdc.iotpt.util.Base64Utils;
import com.jsdc.iotpt.util.exception.SpringUtils;
import com.jsdc.iotpt.util.uuid.IdUtils;
import com.jsdc.iotpt.water.WaterUtils;
import com.jsdc.iotpt.websocket.WsClientPool;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.DigestUtils;

import javax.websocket.Session;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * ClassName: MqttClientCallback
 * Description: 常规MQTT回调函数
 * date: 2023/6/30 10:49
 *
 * @author bn
 */
public class MqttClientCallback implements MqttCallback {


    static Logger logger = LoggerFactory.getLogger(MqttClientCallback.class);


    private KafkaProducer kafkaProducer = SpringContextHolder.getBean(KafkaProducer.class);
    private MinioTemplate minioTemplate = SpringContextHolder.getBean(MinioTemplate.class);


    private DeviceGatewayService deviceGatewayService = SpringContextHolder.getBean(DeviceGatewayService.class);
    // 主题
    private ConfigTopicService configTopicService = SpringContextHolder.getBean(ConfigTopicService.class);

    // 模板
    private ConfigSignalFieldService configSignalFieldService = SpringContextHolder.getBean(ConfigSignalFieldService.class);

    // 连接
    private ConfigLinkService configLinkService = SpringContextHolder.getBean(ConfigLinkService.class);

    private WsClientPool wsClientPool = WsClientPool.getInstance();

    private SysConfig sysConfig = SysConfig.getInstance();
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

    /**
     * 系统的mqtt客户端id
     */
    private String mqttClientId;

    /**
     * 当前连接id
     */
    private Integer linkId;


    private String prefix = SpringContextHolder.getBean(Environment.class).getProperty("sysMqtt.prefix");

    private InfluxDBConfig influxDBConfig = SpringContextHolder.getBean(InfluxDBConfig.class);
    private DeviceCollectService deviceCollectService = SpringContextHolder.getBean(DeviceCollectService.class);
    private InfluxdbService influxdbService = SpringContextHolder.getBean(InfluxdbService.class);

    public MqttClientCallback(String mqttClientId, Integer linkId) {
        this.mqttClientId = mqttClientId;
        this.linkId = linkId;
    }

    /**
     * MQTT 断开连接会执行此方法
     */
    @Override
    public void connectionLost(Throwable throwable) {
        boolean flag = true;
        while (true) {
            try {
                Thread.sleep(10);
                ConfigLink configLink=configLinkService.getById(linkId);

                ConfigTopic configTopic=new ConfigTopic();
                configTopic.setTopicType(1);
                configTopic.setTopicStatus(1);
                configTopic.setLinkId(configLink.getId());
                List<ConfigTopic> configTopics=configTopicService.getList(configTopic);

                List<String> topicNames=configTopics.stream().map(x-> "$share/bn/"+x.getTopicName()).collect(Collectors.toList());
                List<Integer> topicIds=configTopics.stream().map(x->x.getId()).collect(Collectors.toList());
                String[] msgtopic=topicNames.toArray(new String[topicNames.size()]);
                if (!MqttClientConnect.mqttClients.get(mqttClientId).getMqttClient().isConnected()){
                    MqttClientConnect.mqttClients.get(mqttClientId).getMqttClient().connect(MqttClientConnect.mqttClients.get(mqttClientId).getOptions());
                }
                MqttClientConnect.mqttClients.get(mqttClientId).sub(mqttClientId,configLink.getId(),topicIds,msgtopic,3000);

//                if (MqttClientConnect.mqttClients.containsKey(mqttClientId)&&!MqttClientConnect.mqttClients.get(mqttClientId).getMqttClient().isConnected()){
//                    MqttClientConnect.mqttClients.get(mqttClientId).getMqttClient().connect(MqttClientConnect.mqttClients.get(mqttClientId).getOptions());
//                    MqttClientConnect.mqttClients.get(mqttClientId).sub(mqttClientId,configLink.getId(),topicIds,msgtopic,3000);
//                }
//                i++;
//                if (i>5){
//                    System.out.println("重启次数达到峰值，断掉当前连接，重写连接！");
//                    long clientId = System.currentTimeMillis();
//                    configLink.setClientId(String.valueOf(clientId));
//                    configLinkService.updateById(configLink);
//
//                    MqttClientConnect mqttClientConnect = new MqttClientConnect();
//                    mqttClientConnect.setMqttClientId(configLink.getClientId());
//                    mqttClientConnect.setLinkId(configLink.getId());
//
//                    mqttClientConnect.setMqttClient(
//                            String.format("%s%s","tcp://",configLink.getServiceAddress()),
//                            configLink.getClientId(),
//                            configLink.getUsername(),
//                            configLink.getPassword(),
//                            configLink.getId(),
//                            false,
//                            new MqttClientCallback(configLink.getClientId(),configLink.getId()));
//
//                    MqttClientConnect.mqttClients.put(configLink.getClientId(), mqttClientConnect);
//
//                    MqttClientConnect.mqttClients.get(configLink.getClientId()).sub(configLink.getClientId(),configLink.getId(),topicIds,msgtopic,3000);
//
//
//                    // 移除之前连接
//                    MqttClientConnect.mqttClients.get(mqttClientId).getMqttClient().close();
//                    MqttClientConnect.mqttClients.remove(mqttClientId);
//
//
//                }
                //订阅消息
//                    client.subscribe(topic, qos);
                System.out.println("MQTT重新连接成功:" + MqttClientConnect.mqttClients.get(mqttClientId).getMqttClient().getServerURI());
                break;
            } catch (Exception e) {
                logger.info("Exception", e);
                flag = false;
                continue;
            }
        }
        //log.error(throwable.getMessage(), throwable);
    }

    /**
     * publish发布成功后会执行到这里
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        System.out.println("deliveryComplete");
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


    public boolean iteraJson(String str, Map res, DataSheet dataSheet, JSONObject wabc) throws Exception {
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

    public void saveDataSheets(List<Map<String, Object>> dataSheets, String deviceId) throws Exception {

        DeviceGateway deviceGateway = deviceGatewayService.getOne(Wrappers.<DeviceGateway>lambdaQuery().
                eq(DeviceGateway::getIsDel, 0).eq(DeviceGateway::getDeviceCode, deviceId));

        if (deviceGateway == null) {
            return;
        }

        // 根据code获取采集设备
        DeviceCollect deviceCollect = deviceCollectService.getOne(Wrappers.<DeviceCollect>lambdaQuery()
                .eq(DeviceCollect::getDeviceCode, deviceId).eq(DeviceCollect::getIsDel, 0).eq(DeviceCollect::getGatewayId, deviceGateway.getId()));

        if (null == deviceCollect) {
            return;
        }


        BatchPoints batchPoints = BatchPoints.database(influxDBConfig.database).retentionPolicy("autogen") // 设置保留策略
                .consistency(InfluxDB.ConsistencyLevel.ALL) // 设置一致性级别
                .build();


        Map<String, String> tagsMap = new HashMap<>();


        for (Map<String, Object> item : dataSheets) {
            for (Map.Entry<String, Object> x : item.entrySet()) {
                if (x.getKey().equals("batterys") ||
                        x.getKey().equals("data") ||
                        x.getKey().equals("wpstatus") ||
                        x.getKey().equals("wrstatus") ||
                        x.getKey().equals("volvalue")) {

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
                    tagsMap.put("channelId", x.getKey());

                    //fields 监测值
                    Map<String, Object> fieldsMap = new HashMap<>();

                    fieldsMap.put("val", Float.parseFloat(String.valueOf(x.getValue())));

                    Date date = new Date();
                    long time = date.getTime() + 28800000;
                    // 采集时间
                    fieldsMap.put("dataTime", DateUtil.format(date, "yyyy-MM-dd HH:mm:ss"));

                    Point builder = Point.measurement("datasheet").tag(tagsMap).fields(fieldsMap).time(time, TimeUnit.MILLISECONDS).build();
                    batchPoints.point(builder);
                }
            }
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                influxdbService.batchInsert(batchPoints);
            }
        }).start();


//        System.out.println();
    }

    /**
     * 数据解析
     */
    public boolean testData(JSONObject fromObject, Map map, DataSheet dataSheet, List<DataSheet> list, JSONObject wabc, JSONObject sabc, JSONObject xabc) throws Exception {
        Set<String> keys = fromObject.keySet();
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
                } else {
                    iteraJson(val, map, dataSheet, wabc);
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
                        setVal(wabc.getString("random"), key.split("_")[1], dataSheet);
                    }
                    if (sabc.getString("random") != null) {
                        setVal(sabc.getString("random"), key.split("_")[1], dataSheet);
                    }
                }
                //说明存在数组json即格式为：[{开头的json数组
                if (val.indexOf("[{") == 0) {
                    //说明当前value就是一个json数组
                    //去除[括号
                    JSONArray jsonArray = JSONArray.parseArray(val);
                    for (Object o : jsonArray) {
                        JSONObject jsonObject = JSONObject.parseObject(o.toString());
                        Iterator<String> iterator1 = xabc.keySet().iterator();
                        while (iterator1.hasNext()) {
                            //厂商参数key
                            String next = iterator1.next();
                            //对应数据值参数
                            String xkey = xabc.getString(next);
                            setVal(xkey, jsonObject.getString(next), dataSheet);
                        }
                        Iterator<String> iterator3 = sabc.keySet().iterator();
                        while (iterator3.hasNext()) {
                            //厂商参数key
                            String next = iterator3.next();
                            //对应数据值参数
                            String skey = sabc.getString(next);
                            setVal(skey, jsonObject.getString(next), dataSheet);
                        }
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

//    @Override
//    public void messageArrived(String topic, MqttMessage message) throws Exception {
//        String payload = new String(message.getPayload());
//        System.out.println(mqttClientId);
//        System.out.println(linkId);
//        System.out.println(topic);
//        System.out.println(payload);
//
//        if (payload.indexOf(":")==-1){
//            return;
//        }
//
//
//
//        if (sysConfig.getTopicOnline().containsKey(topic)&&
//                sysConfig.getTopicOnline().get(topic)){
//            if (wsClientPool.needThisDevice(topic)){
//
//                Session wsSession = wsClientPool.getOnlineClientMap().get(topic);
//                if (wsSession == null)
//                {
//                    return;
//                }
//
//                try
//                {
//                    // 异步调用
//                    wsSession.getAsyncRemote().sendText(payload);
//                    sysConfig.getTopicOnline().put(topic,false);
//                } catch (Throwable e)
//                {
//                    return;
//                }
//
//            }
//        }
//
//        // 第一步 将消息解析成JSONObject
//        JSONObject fromObject = JSONObject.parseObject(payload);
//
//        // 第二步 根据topic决定数据是否需要区分
//        if (sysConfig.getTopicType().containsKey(topic)){
//            String type=sysConfig.getTopicType().get(topic);
//            Object value=fromObject.get(type);
//            String val=value.toString();
//
//            String key=topic+type+val;
//            // 根据type值决定走向的模板，有数据走对应模板 无数据 说明数据未配置 ，可丢弃 也可保存至无法解析的数据库内
//            if (sysConfig.getMqttKafka().containsKey(key)){
//                // 走报警解析
//                if (sysConfig.getMqttKafka().get(key).equals(G.KAFKA_ALERTING_TOPIC)){
//                    Map map = new HashMap();
//                    AlertSheet alertSheet = new AlertSheet();
//                    ArrayList<AlertSheet> alertSheets = new ArrayList<>();
//                    testAlert(fromObject,map,alertSheet,alertSheets,sysConfig.getBjjx().get(topic));
//
//                    System.out.println(JSON.toJSONString(alertSheets));
//                    System.out.println();
//
////                    kafkaProducer.sendMsg(G.KAFKA_ALERTING_TOPIC,);
//
//                }else if (sysConfig.getMqttKafka().get(key).equals(G.KAFKA_HEARTBEAT_TOPIC)){ // 走心跳解析
//                    Map map = new HashMap();
//                    HeartSheet heartSheet = new HeartSheet();
////                    ArrayList<HeartSheet> heartSheets = new ArrayList<>();
//                    testHeart(fromObject,map,heartSheet,sysConfig.getXtjx().get(topic));
//
//                    System.out.println(JSON.toJSONString(heartSheet));
//                    System.out.println(heartSheet.toString());
//
//                }else {// 数据解析
//                    Map map = new HashMap();
//                    DataSheet dataSheet = new DataSheet();
//                    ArrayList<DataSheet> dataSheets = new ArrayList<>();
//                    testData(fromObject,map,dataSheet,dataSheets,sysConfig.getWgjx().get(topic),sysConfig.getSbjx().get(topic),sysConfig.getXhjx().get(topic));
//
//                    System.out.println(JSON.toJSONString(dataSheets));
//                    System.out.println(dataSheets.toString());
//                    kafkaProducer.sendMsg(G.KAFKA_DATA_TOPIC,JSON.toJSONString(dataSheets));
//                }
//
//            }else {
//                // 是直接丢弃，还是单独入库，后续讨论一下
//
//            }
//        }else {
//            // 当前topic不需要区分数据 直接走数据解析
//            Map map = new HashMap();
//            DataSheet dataSheet = new DataSheet();
//            ArrayList<DataSheet> dataSheets = new ArrayList<>();
//            testData(fromObject,map,dataSheet,dataSheets,sysConfig.getWgjx().get(topic),sysConfig.getSbjx().get(topic),sysConfig.getXhjx().get(topic));
//            System.out.println(dataSheets);
//            kafkaProducer.sendMsg(G.KAFKA_DATA_TOPIC,JSON.toJSONString(dataSheets));
//        }
//
//
//        /**
//         *  用于解析数据 目前的数据类型
//         *  A:{data:[{val1},{val2}]} 力控
//         *  B:{data:{item1:[{val1},{val2}]}} 领祺
//         *
//         *
//         */
//
//        /**
//         * 领祺
//         * {"type":"real","sn":"202301041727","clientId":"123456","time":"2023-05-10 17:17:02",
//         * "data":{"random_tongxunmozu":
//         * [{"id":"零线电流","quality":"1","value":"0.000"},
//         * {"id":"电网频率","quality":"1","value":"50.000"},
//         * {"id":"端子 A 温度","quality":"1","value":"28.650"},
//         * {"id":"B相电流","quality":"1","value":"-0.001"},
//         * {"id":"合闸事件总次数","quality":"1","value":"52.000"},
//         * {"id":"分闸事件总次数","quality":"1","value":"51.000"},
//         * {"id":"遥信629","quality":"1","value":"0.000"}],
//         * "random_temp":
//         * [{"id":"温度","quality":"1","value":"26.100"},
//         * {"id":"湿度","quality":"1","value":"26.200"},
//         * {"id":"温度上限值","quality":"1","value":"92.000"},
//         * {"id":"湿度上限值","quality":"1","value":"91.000"}]
//         * }}
//         */
//
//        /**
//         * 力控
//         * {"gateWayId":"666888","sendTime":"2023-05-10 17:17:07.449",
//         * "data":[{"channelId":"Tempurate","deviceId":"temp_2","point":"temp","type":"2","value":"0.000","timestamp":"2023-05-10 17:17:07.449"},
//         * {"channelId":"Tempurate","deviceId":"temp_2","point":"humidity","type":"2","value":"0.000","timestamp":"2023-05-10 17:17:07.449"},
//         * {"channelId":"Tempurate","deviceId":"temp_2","point":"temp_max","type":"2","value":"0.000","timestamp":"2023-05-10 17:17:07.449"},
//         * {"channelId":"Tempurate","deviceId":"temp_2","point":"humidity_max","type":"2","value":"0.000","timestamp":"2023-05-10 17:17:07.449"}]}
//         */
//
//
//    }


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

    public void saveDataSheets(ArrayList<DataSheet> dataSheets) {

        long startTime = System.currentTimeMillis();
//        // 三项电流
//        Map<Integer, List<Double>> threeA = new HashMap<>();
//        // 三项电压
//        Map<Integer, List<Double>> threeV = new HashMap<>();
        BatchPoints batchPoints = BatchPoints.database(influxDBConfig.database).retentionPolicy("autogen") // 设置保留策略
                .consistency(InfluxDB.ConsistencyLevel.ALL) // 设置一致性级别
                .build();
        Map<String, DeviceCollect> collectMap = new HashMap<>();
        for (DataSheet x : dataSheets) {
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


//            // 三项电流
//            if (listA.contains(x.getChannelId())) {
//                if (!threeA.containsKey(deviceCollect.getId())) {
//                    threeA.put(deviceCollect.getId(), new ArrayList<Double>());
//                }
//                threeA.get(deviceCollect.getId()).add(Double.parseDouble(x.getVal()));
//            } else if (listV.contains(x.getChannelId())) {
//                // 三项电压
//                if (!threeV.containsKey(deviceCollect.getId())) {
//                    threeV.put(deviceCollect.getId(), new ArrayList<Double>());
//                }
//                threeV.get(deviceCollect.getId()).add(Double.parseDouble(x.getVal()));
//            }


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
            }
        }).start();


//        logger.info("数据保存时长。。。" + (System.currentTimeMillis() - startTime));
//        System.out.println();
    }


    /**
     * mqtt数据解析
     * $目前思路是多个数据模板，对于解析来说，将模板拆分的越清晰，解析出来效果越好，
     * 之所以拆出多个模板，是为了防止网关多的时候，可能使用的字段一致，含义不一致，代码指向就很难区分这些问题
     * 数据类型模板：Map<topic,type> topicType 通过mqtt主题进行索引data是否需要区分类型（数据，报警，心跳）
     * 网关解析模板：Map<topic,json>  wgjx 通过mqtt主题进行索引
     * 设备解析模板：Map<topic,json> sbjx 通过mqtt主题进行索引
     * 信号解析模板：Map<topic,json> xhjx 通过mqtt主题进行索引
     * mqtt发送至kafka模板：Map<topic+type+value,kafka> 通过拼接字符串进行索引 主要是为了防止key值重复
     * <p>
     * <p>
     * 1.根据topic从数据类型模板里去值；有：表示当前网关的数据需进行分类，无：表示当前网关无报警与心跳
     * <p>
     * <p>
     * subscribe订阅后得到的消息会执行到这里
     */

    public ConcurrentHashMap<String, ConfigTopic> topics = new ConcurrentHashMap<>();


    public void setLightPub(MqttMessage message){
        System.out.println(new String(message.getPayload()));
        JSONObject lightData=JSON.parseObject(new String(message.getPayload()));
        if (lightData.containsKey("type")&&"change".equals(lightData.getString("type"))){
            JSONObject data=lightData.getJSONObject("data");
            JSONArray controlRandom=data.getJSONArray("LightControl_random");

            for (int i = 0; i < controlRandom.size(); i++) {
                JSONObject item=controlRandom.getJSONObject(i);

                if (item.getString("id").equals("NowState")){
                    RedisUtils.setBeanValue("NowState",Double.parseDouble(item.getString("value")),20l);
                }
            }
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        try {
            if (!topics.containsKey(topic)) {
                List<ConfigTopic> topicDatas = configTopicService.getBaseMapper().selectList(Wrappers.<ConfigTopic>lambdaQuery()
                        .eq(ConfigTopic::getLinkId, linkId)
                        .eq(ConfigTopic::getTopicName, topic)
                        .eq(ConfigTopic::getTopicStatus, 1)
                );
                topics.put(topic, topicDatas.get(0));
            }

            if (topic.contains("SENSOR")) {
                String[] ccc = topic.split("/");
                List<Map<String, Object>> data = WaterUtils.parse(new String(message.getPayload()));
                saveDataSheets(data, ccc[2]);

            }else if(topic.contains("Lightpub")){
                setLightPub(message);
            } else if (topic.contains("uploadzs")) {
                String[] ccc = topic.split("/");

                if (StringUtils.isNotBlank(new String(message.getPayload())) && null != JSONObject.parseObject(new String(message.getPayload()))){
                    groundLockCameraControl(message);
                }

            } else {
                MqttClientConnect.mqttClients.get(mqttClientId).getClientTopics().get(topics.get(topic).getId()).messageArrived(topic, message);
            }
        } catch (Exception e) {
            logger.info("msg", e);
        }
//        try {
//            long startTime=System.currentTimeMillis();
//            String msgType=null;
//            String payload = new String(message.getPayload());
//            if (payload.indexOf(":")==-1){
//                return;
//            }
//            // 一、查询当前topic详细信息
//            ConfigTopic topicData=configTopicService.
//                    getOne(Wrappers.<ConfigTopic>lambdaQuery().
//                            eq(ConfigTopic::getLinkId,linkId).
//                            eq(ConfigTopic::getTopicName,topic).
//                            eq(ConfigTopic::getTopicStatus,1));
//
//            // 二、获取topic类型字段
//            String type=topicData.getTemplateKey();
//
//            JSONObject fromObject = JSONObject.parseObject(payload);
//
//            if(null != type){
//                Object value=fromObject.get(type);
//                if (null==value){
//                    return;
//                }
//                String val=value.toString();
//
//                if (val.equals(topicData.getAlarmVal())){// 告警解析模板
//                    if (null!=topicData.getTransferId()){
//                        ConfigSignalField configSignalField=new ConfigSignalField();
//                        configSignalField.setModelId(topicData.getTransferId());
//                        configSignalField.setType("2");
//                        // 系统字段对比表
//                        List<ConfigSignalField> configSignalFields=configSignalFieldService.getList(configSignalField);
//                        // 对比数据拆分成网关解析，信号解析，设备解析
//                        telData(2,configSignalFields);
//
//                        Map map = new HashMap();
//                        AlertSheet alertSheet = new AlertSheet();
//                        ArrayList<AlertSheet> alertSheets = new ArrayList<>();
//                        boolean isArray=testAlert(fromObject,map,alertSheet,alertSheets,bjjx);
//
//                        if (!isArray){
//                            alertSheets.add(alertSheet);
//                        }
//
//                        kafkaProducer.sendMsg(G.KAFKA_ALERTING_TOPIC,JSON.toJSONString(alertSheets));
//                    }
//                    msgType="2";
//
//                }else if (val.equals(topicData.getHeartVal())){ // 心跳解析模板
//                    if (null!=topicData.getTransferId()){
//                        ConfigSignalField configSignalField=new ConfigSignalField();
//                        configSignalField.setModelId(topicData.getTransferId());
//                        configSignalField.setType("3");
//                        // 系统字段对比表
//                        List<ConfigSignalField> configSignalFields=configSignalFieldService.getList(configSignalField);
//                        // 对比数据拆分成网关解析，信号解析，设备解析
//                        telData(3,configSignalFields);
//
//                        Map map = new HashMap();
//                        HeartSheet heartSheet = new HeartSheet();
//                        testHeart(fromObject,map,heartSheet,xtjx);
//                        List<HeartSheet> heartSheets=new ArrayList<>();
//                        heartSheets.add(heartSheet);
//                        kafkaProducer.sendMsg(G.KAFKA_HEARTBEAT_TOPIC,JSON.toJSONString(heartSheets));
//                    }
//                    msgType="3";
//                }else { // 数据解析模板
//                    if (null!=topicData.getTransferId()) {
//                        ConfigSignalField configSignalField=new ConfigSignalField();
//                        configSignalField.setModelId(topicData.getTransferId());
//                        configSignalField.setType("1");
//                        // 系统字段对比表
//                        List<ConfigSignalField> configSignalFields=configSignalFieldService.getList(configSignalField);
//                        // 对比数据拆分成网关解析，信号解析，设备解析
//                        telData(1,configSignalFields);
//                        // 数据解析
//                        Map map = new HashMap();
//                        DataSheet dataSheet = new DataSheet();
//                        ArrayList<DataSheet> dataSheets = new ArrayList<>();
//                        testData(fromObject,map,dataSheet,dataSheets,wgjx,sbjx,xhjx);
//                        System.err.println("数据解析时长。。。"+(System.currentTimeMillis()-startTime));
//                        saveDataSheets(dataSheets);
//                    }
//                    msgType="1";
//                }
//
//            }else {
//                if (null!=topicData.getTransferId()){
//                    ConfigSignalField configSignalField=new ConfigSignalField();
//                    configSignalField.setModelId(topicData.getTransferId());
//                    configSignalField.setType("1");
//                    // 系统字段对比表
//                    List<ConfigSignalField> configSignalFields=configSignalFieldService.getList(configSignalField);
//                    // 对比数据拆分成网关解析，信号解析，设备解析
//                    telData(1,configSignalFields);
//
//                    // 数据解析
//                    Map map = new HashMap();
//                    DataSheet dataSheet = new DataSheet();
//                    ArrayList<DataSheet> dataSheets = new ArrayList<>();
//                    testData(fromObject,map,dataSheet,dataSheets,wgjx,sbjx,xhjx);
//                    saveDataSheets(dataSheets);
////                    kafkaProducer.sendMsg(G.KAFKA_DATA_TOPIC,JSON.toJSONString(dataSheets));
//                }
//                msgType="1";
//                // 当前数据未配置指定解析字段，是否已默认的数据类型解析？
//            }
//
//            sendMsg(String.format("C%sT%s%s",linkId,msgType,topic),topic,payload);
//        }catch (Exception e){
//            return;
//        }


        // 第一步 将消息解析成JSONObject
//        fromObject = JSONObject.parseObject(payload);
//
//        // 第二步 根据topic决定数据是否需要区分
//        if (sysConfig.getTopicType().containsKey(topic)){
//            type=sysConfig.getTopicType().get(topic);
//            Object value=fromObject.get(type);
//            String val=value.toString();
//
//            String key=topic+type+val;
//            // 根据type值决定走向的模板，有数据走对应模板 无数据 说明数据未配置 ，可丢弃 也可保存至无法解析的数据库内
//            if (sysConfig.getMqttKafka().containsKey(key)){
//                // 走报警解析
//                if (sysConfig.getMqttKafka().get(key).equals(G.KAFKA_ALERTING_TOPIC)){
//                    Map map = new HashMap();
//                    AlertSheet alertSheet = new AlertSheet();
//                    ArrayList<AlertSheet> alertSheets = new ArrayList<>();
//                    testAlert(fromObject,map,alertSheet,alertSheets,sysConfig.getBjjx().get(topic));
//
//                    System.out.println(JSON.toJSONString(alertSheets));
//                    System.out.println();
//
////                    kafkaProducer.sendMsg(G.KAFKA_ALERTING_TOPIC,);
//
//                }else if (sysConfig.getMqttKafka().get(key).equals(G.KAFKA_HEARTBEAT_TOPIC)){ // 走心跳解析
//                    Map map = new HashMap();
//                    HeartSheet heartSheet = new HeartSheet();
////                    ArrayList<HeartSheet> heartSheets = new ArrayList<>();
//                    testHeart(fromObject,map,heartSheet,sysConfig.getXtjx().get(topic));
//
//                    System.out.println(JSON.toJSONString(heartSheet));
//                    System.out.println(heartSheet.toString());
//
//                }else {// 数据解析
//                    Map map = new HashMap();
//                    DataSheet dataSheet = new DataSheet();
//                    ArrayList<DataSheet> dataSheets = new ArrayList<>();
//                    testData(fromObject,map,dataSheet,dataSheets,sysConfig.getWgjx().get(topic),sysConfig.getSbjx().get(topic),sysConfig.getXhjx().get(topic));
//
//                    System.out.println(JSON.toJSONString(dataSheets));
//                    System.out.println(dataSheets.toString());
//                    kafkaProducer.sendMsg(G.KAFKA_DATA_TOPIC,JSON.toJSONString(dataSheets));
//                }
//
//            }else {
//                // 是直接丢弃，还是单独入库，后续讨论一下
//
//            }
//        }else {
//            // 当前topic不需要区分数据 直接走数据解析
//            Map map = new HashMap();
//            DataSheet dataSheet = new DataSheet();
//            ArrayList<DataSheet> dataSheets = new ArrayList<>();
//            testData(fromObject,map,dataSheet,dataSheets,sysConfig.getWgjx().get(topic),sysConfig.getSbjx().get(topic),sysConfig.getXhjx().get(topic));
//            System.out.println(dataSheets);
//            kafkaProducer.sendMsg(G.KAFKA_DATA_TOPIC,JSON.toJSONString(dataSheets));
//        }


        /**
         *  用于解析数据 目前的数据类型
         *  A:{data:[{val1},{val2}]} 力控
         *  B:{data:{item1:[{val1},{val2}]}} 领祺
         *
         *
         */

        /**
         * 领祺
         * {"type":"real","sn":"202301041727","clientId":"123456","time":"2023-05-10 17:17:02",
         * "data":{"random_tongxunmozu":
         * [{"id":"零线电流","quality":"1","value":"0.000"},
         * {"id":"电网频率","quality":"1","value":"50.000"},
         * {"id":"端子 A 温度","quality":"1","value":"28.650"},
         * {"id":"B相电流","quality":"1","value":"-0.001"},
         * {"id":"合闸事件总次数","quality":"1","value":"52.000"},
         * {"id":"分闸事件总次数","quality":"1","value":"51.000"},
         * {"id":"遥信629","quality":"1","value":"0.000"}],
         * "random_temp":
         * [{"id":"温度","quality":"1","value":"26.100"},
         * {"id":"湿度","quality":"1","value":"26.200"},
         * {"id":"温度上限值","quality":"1","value":"92.000"},
         * {"id":"湿度上限值","quality":"1","value":"91.000"}]
         * }}
         */

        /**
         * 力控
         * {"gateWayId":"666888","sendTime":"2023-05-10 17:17:07.449",
         * "data":[{"channelId":"Tempurate","deviceId":"temp_2","point":"temp","type":"2","value":"0.000","timestamp":"2023-05-10 17:17:07.449"},
         * {"channelId":"Tempurate","deviceId":"temp_2","point":"humidity","type":"2","value":"0.000","timestamp":"2023-05-10 17:17:07.449"},
         * {"channelId":"Tempurate","deviceId":"temp_2","point":"temp_max","type":"2","value":"0.000","timestamp":"2023-05-10 17:17:07.449"},
         * {"channelId":"Tempurate","deviceId":"temp_2","point":"humidity_max","type":"2","value":"0.000","timestamp":"2023-05-10 17:17:07.449"}]}
         */


    }


    // 地锁相机控制代码
    public void groundLockCameraControl(MqttMessage message) throws MqttException {
        // JSON 数据转 对象
        JSONObject messageJson = JSONObject.parseObject(new String(message.getPayload()));
        Map<String, Object> msgMap = (Map<String, Object>)messageJson;
        logger.info("地锁接收的数据:{}" , JSONObject.toJSONString(msgMap));
        // 得到map的键值对
        String msg_type = MapUtils.getString(msgMap, "msg_type");

        if (!StringUtils.equals(msg_type, "IVS")) {
            logger.debug("地锁接收的数据: {}, 不做处理", "msg_type不是IVS");
            return;
        }
        // msg_data
        Map<String, Object> msg_data = (Map<String, Object>) msgMap.get("msg_data");

        Integer zone_id = 0;
        Integer parking_state = 0;
        // product_h
        Map<String, Object> product_h = (Map<String, Object>) msg_data.get("product_h");
        if (null != product_h){
            // parking
            Map<String, Object> parking = (Map<String, Object>) product_h.get("parking");
            if (null != parking){
                // zone_id
                zone_id = MapUtils.getInteger(parking, "zone_id");
                // parking_state 车位状态
                parking_state = MapUtils.getInteger(parking, "parking_state");
            }
        }
        if(null == parking_state){
            logger.debug("地锁接收的数据: {}, 不做处理", "parking_state为空");
            return;
        }
        Map<String, Object> device_info = (Map<String, Object>) msg_data.get("device_info");

        Map<String, Object> parking = (Map<String, Object>) product_h.get("parking");
        Map<String, Object> plate = (Map<String, Object>) product_h.get("plate");
        Map<String, Object> reco = (Map<String, Object>) product_h.get("reco");
        Map<String, Object> car_pos = (Map<String, Object>) product_h.get("car_pos");

        Map<String, Object> alarm_info = (Map<String, Object>) msg_data.get("alarm_info");
        // 判断入库的字段是否为空
        if (null == device_info || null == parking || null == plate || null == reco || null == car_pos || null == alarm_info) {
            logger.debug("地锁接收的数据: {}, 不做处理", "device_info、parking、plate、reco、car_pos、alarm_info为空");
            return;
        }
        // 得到入库的组装对象
        GroundLocks groundLocks = conventGroundLocks(device_info, parking, plate, reco, car_pos, alarm_info, msg_data, msgMap);


        // 发送控制数据
        String clientId = "";

        // 车牌类型
        Integer car_type = MapUtils.getInteger(plate, "type");
        String car_type_name = null == carTypeMap().get(car_type)?"":carTypeMap().get(car_type);;

        //1：⼊场；2：在场； 4：出场； 8：空场；

        int option = 0;
        switch (parking_state){
            case 1:
                // 入场
                logger.info("地锁-执行逻辑判断 入场 -----------------------");
                // 落锁 发送控制数据 1:升锁;  3:降锁后不⾃动升锁（D类型地锁）
                option = 3;
                // 入库停车表
                break;
            case 2:
                // 在场
                logger.info("地锁-执行逻辑判断 在场 -----------------------");
                // 在场逻辑
                // 如果第一次识别是无牌车,根据group_id,调整车牌信息
                break;
            case 4:
                // 出场
                logger.info("地锁-执行逻辑判断 出场 -----------------------");
                // 出场逻辑
                // 出场停车表
                // 升起
                option = 1;
                // 操作
                break;
            case 8:
                // 空场
                logger.info("地锁-执行逻辑判断 空场 -----------------------");
                // 空场逻辑
                // 升起
                option = 1;
                break;
            default:
                break;
        }
        // 准备新增数据库
        SpringUtils.getBean(GroundLocksMapper.class).insert(groundLocks);

        // 入库停车表
        //                            stallCarService.saveInRecord(StallCar.builder()
//                                    .busNumber(groundLocks.getPlate_plate())
//                                    .plateType(car_type_name)
//                                    .stallCode(groundLocks.getParking_zone_name())
//                                    .deviceBusinessId(groundLocks.getReco_group_id())
//                                    .stopTime(groundLocks.getReco_time())
//                                    .completeFlag(0)
//                                    .build()
//                            );
                        // 如果第一次识别是无牌车,根据group_id,调整车牌信息
//                            // 入库停车表
//                            stallCarService.saveInRecord(StallCar.builder()
//                                    .busNumber(groundLocks.getPlate_plate())
//                                    .plateType(car_type_name)
//                                    .stallCode(groundLocks.getParking_zone_name())
//                                    .deviceBusinessId(groundLocks.getReco_group_id())
//                                    .stopTime(groundLocks.getReco_time())
//                                    .completeFlag(0)
//                                    .build()
//                            );
//                            SpringUtils.getBean(CarService.class).carIn(plateNo, parking_zone_name, reco_car_brand, reco_car_type);
    }

    public static String decodePath(String encodedPath) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encodedPath);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            // 处理非法Base64输入
            System.err.println(" 解码失败: " + e.getMessage());
            return null; // 或抛出自定义异常
        }
    }




    // 车辆类型
    public Map<Integer, String> carTypeMap(){
        Map<Integer, String> carTypeMap = new HashMap<>();
        //⻋牌类型
        carTypeMap.put(0, "未知车牌");
        carTypeMap.put(1, "蓝牌小汽车");
        carTypeMap.put(2, "黑牌小汽车");
        carTypeMap.put(3, "单排黄牌");
        carTypeMap.put(4, "双排黄牌(大客车)");
        carTypeMap.put(5, "警车车牌");
        carTypeMap.put(6, "武警车牌");
        carTypeMap.put(7, "个性化车牌");
        carTypeMap.put(8, "单排军车牌");
        carTypeMap.put(9, "双排军车牌");
        carTypeMap.put(10, "使馆车牌");
        carTypeMap.put(11, "香港进出中国大陆车牌");
        carTypeMap.put(12, "农用车牌");
        carTypeMap.put(13, "教练车牌");
        carTypeMap.put(14, "澳门进出中国大陆车牌");
        carTypeMap.put(15, "双层武警车牌");
        carTypeMap.put(16, "武警总队车牌");
        carTypeMap.put(17, "双层武警总队车牌");
        carTypeMap.put(18, "民航车牌");
        carTypeMap.put(19, "新能源车牌");
        carTypeMap.put(20, "系能源车牌大");
        carTypeMap.put(21, "应急");
        carTypeMap.put(22, "领馆");
        carTypeMap.put(31, "无牌车");
        return carTypeMap;
    }

    // 对象组装
    private GroundLocks conventGroundLocks(Map<String, Object> device_info,
                                           Map<String, Object> parking,
                                           Map<String, Object> plate,
                                           Map<String, Object> reco,
                                           Map<String, Object> car_pos,
                                           Map<String, Object> alarm_info,
                                           Map<String, Object> msg_data,
                                           Map<String, Object> msgMap
    ) {
        // 如果是1，车辆要入场，再用车牌号跟数据库对比，如果是我们公司的车，则发送落锁指令
        // sn 设备号
        String sn = MapUtils.getString(msgMap, "sn");
        // timestamp 时间戳
        String timestamp = MapUtils.getString(msgMap, "timestamp");
        String msg_type = MapUtils.getString(msgMap, "msg_type");
        //1. 按照“key=value”格式，使⽤“&”符号，按顺序拼接为字符串，格式如下： sn=xxx&timestamp=xxx&msg_id=xxx&msg_type=xxx
        String snTemp = "sn=" + sn + "&timestamp=" + timestamp + "&msg_id=GO2021070112000101&msg_type=GroundlockOption";
        //2. 将拼接字符串进⾏32位MD5计算，得到⼤写的数字签名
        String sign = DigestUtils.md5DigestAsHex(snTemp.getBytes()).toUpperCase();

        // 车牌 base64 解密
        String plateNo = null;
        // 车位名 base64 解密
        String parking_zone_name = null;
        String reco_car_brand = null;
        String reco_car_type = null;
        try {
            plateNo = Base64Utils.decode(MapUtils.getString(plate, "plate"));
            parking_zone_name = Base64Utils.decode(MapUtils.getString(parking, "zone_name"));
            reco_car_brand = Base64Utils.decode(MapUtils.getString(reco, "car_brand"));
            reco_car_type = Base64Utils.decode(MapUtils.getString(reco, "car_type"));
        } catch (Exception e) {
            logger.error("base64解密失败: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        GroundLocks groundLocks = GroundLocks.builder().id(IdUtils.fastSimpleUUID()).sign(sign).msg_type(msg_type).build();
        groundLocks.setMsg_id(MapUtils.getString(msgMap, "msg_id", ""));
        groundLocks.setTimestamp(timestamp);
        groundLocks.setSerial(MapUtils.getString(msg_data, "serial", ""));
        groundLocks.setTime_s(MapUtils.getString(msg_data, "time_s", ""));
        groundLocks.setTrigger_type(MapUtils.getInteger(msg_data, "trigger_type", 0));
        groundLocks.setDev_name(MapUtils.getString(device_info, "dev_name", ""));
        groundLocks.setIp(MapUtils.getString(device_info, "ip", ""));
        groundLocks.setSn(MapUtils.getString(device_info, "sn", ""));
        groundLocks.setParking_zone_id(MapUtils.getInteger(parking, "zone_id", 0));
        groundLocks.setParking_zone_name(parking_zone_name);
        groundLocks.setParking_parking_state(MapUtils.getInteger(parking, "parking_state"));
        groundLocks.setPlate_plate_color(MapUtils.getInteger(plate, "color"));
        groundLocks.setPlate_confidence(MapUtils.getInteger(plate, "confidence"));
        groundLocks.setPlate_plate(plateNo);
        groundLocks.setPlate_plate_type(MapUtils.getInteger(plate, "plate_type"));
        groundLocks.setPlate_type(MapUtils.getInteger(plate, "type"));
        groundLocks.setReco_flag(MapUtils.getInteger(reco, "reco_flag"));
        groundLocks.setReco_group_id(MapUtils.getInteger(reco, "group_id"));
        groundLocks.setReco_car_brand(reco_car_brand);
        groundLocks.setReco_time(MapUtils.getString(reco, "reco_time"));
        groundLocks.setReco_id(MapUtils.getInteger(reco, "reco_id"));
        groundLocks.setReco_car_type(reco_car_type);
        groundLocks.setCar_pos(MapUtils.getInteger(car_pos, "pos"));
        groundLocks.setAlarm_status(MapUtils.getInteger(alarm_info, "alarm_status"));
        groundLocks.setAlarm_whitelist(MapUtils.getInteger(alarm_info, "alarm_whitelist"));

        // 图片
//        groundLocks.setBg_img((JSONArray) msg_data.get("bg_img"));

        groundLocks.setGroup_id(MapUtils.getInteger(reco, "group_id"));
        return groundLocks;
    }

    // 升锁降锁操作
    public static void liftAndDropOperation(String sign, String sn, String timestamp, Integer option, Integer zone_id, String clientId) throws MqttException {
        liftAndDropOperation(sign, sn, timestamp, option, zone_id, clientId, 0);
    }
    // 升锁降锁操作(强制操作)
    public static void liftAndDropOperation(String sign, String sn, String timestamp, Integer option, Integer zone_id, String clientId, int force) throws MqttException {
        // 判断有没有车位地锁
        JSONObject msgObj = new JSONObject();
        msgObj.put("sign", sign);
        msgObj.put("sn", sn);
        msgObj.put("timestamp", timestamp);
        msgObj.put("msg_id", "GO2021070112000101");
        msgObj.put("msg_type", "GroundlockOption");
        // 发布信息操作
        JSONObject msgData = new JSONObject();
        // 1:升锁;  3:降锁后不⾃动升锁（D类型地锁）
        msgData.put("option", option);
        // 0,  强制操作0：否  1：是
        msgData.put("force", force);
        // 1   ⻋位id  0是左车位，1是右车位
        msgData.put("zone_id", zone_id);

        msgObj.put("msg_data", msgData);
        String sendMsg = msgObj.toJSONString();
        System.out.println(sendMsg);
//        String topc = "/pushzs/" + sn;
        String topc = "/pushzs/75a971b6-25878aa7";

        logger.debug("地锁-执行地锁操作信息: {}", sendMsg);
        if (Strings.isNotEmpty(clientId)) {
            MqttClientConnect.mqttClients.get(clientId).pub(topc, sendMsg);
        } else {
            for (Map.Entry<String, MqttClientConnect> item : MqttClientConnect.mqttClients.entrySet()) {
                item.getValue().pub(topc, sendMsg);
            }
        }

    }

    public static void sendVoice(String sign, String sn, String timestamp, int option){
        String content = "";
        switch (option){
            case 1 :
                // 升起
                content = "6L2m5L2N6ZSB5Y2z5bCG5LiK5Y2H77yM6K+35rOo5oSPLndhdg==";
                break;
            case 2:
                content = "56aB5q2i6L+b5YWl6K+l6L2m5L2N77yM6K+35bC95b+r56a75byALndhdg==";
                break;
            case 3:
                // 下降
                content = "6L2m5L2N6ZSB5bey5LiL6ZmNLndhdg==";
                break;
            default:
                logger.error("语音播报异常: 无法识别的操作类型");
                return;
        }
        // 语音播报
        logger.info("语音播报start: 设备sn{}" , sn);
        try {
            JSONObject msgObj = new JSONObject();
            msgObj.put("sign", sign);
            msgObj.put("sn", sn);
            msgObj.put("timestamp", timestamp);
            msgObj.put("msg_id", "PV2021070112000101");
            msgObj.put("msg_type", "PlayVoice");
            // 发布信息操作
            JSONObject msgData = new JSONObject();
            msgData.put("voice_speed", 100);
            msgData.put("voice_male", 1);
            msgData.put("voice_data", content);
            msgData.put("data_len", 52);
            msgData.put("voice_volume", 100);

            msgObj.put("msg_data", msgData);
            String sendMsg = msgObj.toJSONString();
            logger.info("发送的报文信息: {}", sendMsg);

            String topc = "/pushzs/75a971b6-25878aa7";
            for (Map.Entry<String, MqttClientConnect> item : MqttClientConnect.mqttClients.entrySet()) {
                item.getValue().pub(topc, sendMsg);
            }
            logger.info("语音播报end: 设备sn{}" , sn);
        } catch (MqttException e) {
            logger.error("语音播报异常: {}" , e.getMessage());
        }
    }


}
