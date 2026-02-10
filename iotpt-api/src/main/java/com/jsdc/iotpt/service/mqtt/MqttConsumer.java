//package com.jsdc.iotpt.service.mqtt;
//
//
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.baomidou.mybatisplus.core.toolkit.StringUtils;
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import com.jsdc.iotpt.common.G;
//import com.jsdc.iotpt.model.AlertSheet;
//import com.jsdc.iotpt.model.ConfigTopic;
//import com.jsdc.iotpt.model.DataSheet;
//import com.jsdc.iotpt.model.HeartSheet;
//import com.jsdc.iotpt.service.ConfigTopicService;
//import com.jsdc.iotpt.service.Kafka.KafkaProducer;
//import lombok.SneakyThrows;
//import org.joda.time.DateTime;
//import jdk.nashorn.internal.scripts.JS;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.integration.mqtt.support.MqttHeaders;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageHandler;
//import org.springframework.messaging.MessagingException;
//import org.springframework.stereotype.Component;
//import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.Method;
//import java.util.*;
//
//
//
///**
// * ClassName: MqttConsumer
// * Description:
// * date: 2023/5/10 16:43
// *
// * @author bn
// */
//@Component
//@ConditionalOnProperty(value = "spring.mqtt.enable", havingValue = "true")
//public class MqttConsumer implements MessageHandler {
//
//
//    @Autowired
//    private ConfigTopicService configTopicService;
//
//    @Autowired
//    private KafkaProducer kafkaProducer;
//
//    // topic数据类型 结构
//    private static Map<String,String> topicType;
//
//    // mqtt转发kafka数据结构
//    private static Map<String,String> mqttKafka;
//
//
//    /**
//     * A:{type:111,value:222}
//     * B:{type:222,clientId:3333,value:3333}
//     * 模板：{type(A):type(System),clientId(B):type(System),value(A,B):val(System)}
//     * 针对上述信息解析时,B包含了type字段，取出type的value，但实际上 client才与系统内type对应，所以解析时，应该判断一下
//     */
//    //网关信息获取
////    static JSONObject wabc = JSONObject.parseObject("{\"clientId\":\"gateWayId\",\"time\":\"time\"}");
////    //设备信息获取
////    static JSONObject sabc = JSONObject.parseObject("{\"quality\":\"type\",\"random\":\"deviceId\",\"deviceId\":\"deviceId\"}");
////    //信号获取
////    static JSONObject xabc = JSONObject.parseObject("{\"value\":\"val\",\"quality\":\"type\",\"id\":\"channelId\",\"channelId\":\"channelId\",\"desc\":\"point\",\"point\":\"point\",\"timestamp\":\"time\"}");
//
//
//
//    // 网关信息解析模板
//    private static Map<String, JSONObject> wgjx;
//
//    // 设备信息解析模板
//    private static Map<String,JSONObject> sbjx;
//
//    // 信号解析模板
//    private static Map<String,JSONObject> xhjx;
//
//    // 心跳解析
//    private static Map<String,JSONObject> xtjx;
//
//    // 报警解析
//    private static Map<String,JSONObject> bjjx;
//
//
//    public MqttConsumer() {
//        topicType=new HashMap<>();
//        /**
//         * 领器 根据type解析数据
//         * 例如力控的数据 无type类型 字典里不需要拼接 默认全部已数据解析
//         */
//        topicType.put("/Topic/device","type");
//        mqttKafka=new HashMap<>();
//        /**
//         *  例如领器的数据 上报方式为三种 一是全部数据上报 real，二是变化数据上报 change，三是断线缓存数据上报 his
//         *  到时候用，分割，拼接之后全部指向kafka数据通道
//         */
//        mqttKafka.put("/Topic/devicetypereal", G.KAFKA_DATA_TOPIC);
//        mqttKafka.put("/Topic/devicetypehis", G.KAFKA_DATA_TOPIC);
//        //
//        mqttKafka.put("/Topic/devicetypeheart", G.KAFKA_HEARTBEAT_TOPIC);
//        mqttKafka.put("/Topic/devicetypealarm", G.KAFKA_ALERTING_TOPIC);
//        //力控的数据，无type类型，已数据解析为模板，解析后发送到数据kafka内
//        mqttKafka.put("/Topic/likong",G.KAFKA_DATA_TOPIC);
//
//        /**
//         * 网关解析模板
//         */
//        wgjx=new HashMap<>();
//        wgjx.put("/Topic/device",JSONObject.parseObject("{\"clientId\":\"gateWayId\",\"time\":\"time\"}"));
//        wgjx.put("/Topic/likong",JSONObject.parseObject("{\"gateWayId\":\"gateWayId\",\"sendTime\":\"time\"}"));
//        /**
//         * 设备解析模板
//         */
//        sbjx=new HashMap<>();
//        sbjx.put("/Topic/device",JSONObject.parseObject("{\"quality\":\"type\",\"random\":\"deviceId\"}"));
//        sbjx.put("/Topic/likong",JSONObject.parseObject("{\"deviceId\":\"deviceId\"}"));
//
//        /**
//         * 信号解析模板
//         */
//        xhjx=new HashMap<>();
//        xhjx.put("/Topic/device",JSONObject.parseObject("{\"value\":\"val\",\"quality\":\"type\",\"id\":\"channelId\",\"desc\":\"point\"}"));
//        xhjx.put("/Topic/likong",JSONObject.parseObject("{\"value\":\"val\",\"channelId\":\"channelId\",\"point\":\"point\",\"timestamp\":\"time\"}"));
//
//        /**
//         *  xinti
//         */
//        xtjx=new HashMap<>();
//        xtjx.put("/Topic/device",JSONObject.parseObject("{\"clientId\":\"gateWayId\",\"time\":\"time\",\"csq\":\"val\"}"));
//
//        bjjx=new HashMap<>();
//        bjjx.put("/Topic/device",JSONObject.parseObject("{\"clientId\":\"gateWayId\",\"time\":\"time\",\"s\":\"val\",\"t\":\"type\"}"));
//
//
//
//    }
//
//    private void init(){
//
//
//
//    }
//
//
//
//
//    private DataSheet setVal(String key, String value, DataSheet dataSheet) throws Exception {
//        Field nameField = dataSheet.getClass().getField(key);
//        char[] cs = key.toCharArray();
//        cs[0] -= 32;
//        Method set = dataSheet.getClass().getMethod("set" + String.valueOf(cs), nameField.getType());
//        if (value != null && !value.equals("")) {
//            if (nameField.getType().getName().contains("String")) {
//                set.invoke(dataSheet, value);
//            } else if (nameField.getType().getName().contains("Integer")) {
//                set.invoke(dataSheet, Integer.parseInt(value));
//            } else if (nameField.getType().getName().contains("Date")) {
//                if (value.length() > 10 && value.substring(0, 10).matches("[\\d]{4}-[\\d]{2}-[\\d]{2}")) {
//                    Date date = new DateTime(value.replaceFirst(" ", "T")).toDate();
//                    set.invoke(dataSheet, date);
//                }else if(value.matches("[\\d]{10}|[\\d]{13}")){ // 有可能是10位或者13位时间戳
//                    if (value.matches("[\\d]{10}")){
//                        value=value+"000";
//                    }
//                    Date date=new DateTime(Long.valueOf(value)).toDate();
//                    set.invoke(dataSheet, date);
//                }
//            }
//        }
//
//        return dataSheet;
//    }
//
//    private HeartSheet setVal(String key, String value, HeartSheet heartSheet) throws Exception {
//        Field nameField = heartSheet.getClass().getField(key);
//        char[] cs = key.toCharArray();
//        cs[0] -= 32;
//        Method set = heartSheet.getClass().getMethod("set" + String.valueOf(cs), nameField.getType());
//        if (value != null && !value.equals("")) {
//            if (nameField.getType().getName().contains("String")) {
//                set.invoke(heartSheet, value);
//            } else if (nameField.getType().getName().contains("Integer")) {
//                set.invoke(heartSheet, Integer.parseInt(value));
//            } else if (nameField.getType().getName().contains("Date")) {
//                if (value.length() > 10 && value.substring(0, 10).matches("[\\d]{4}-[\\d]{2}-[\\d]{2}")) {
//                    Date date = new DateTime(value.replaceFirst(" ", "T")).toDate();
//                    set.invoke(heartSheet, date);
//                }else if(value.matches("[\\d]{10}|[\\d]{13}")){ // 有可能是10位或者13位时间戳
//                    if (value.matches("[\\d]{10}")){
//                        value=value+"000";
//                    }
//                    Date date=new DateTime(Long.valueOf(value)).toDate();
//                    set.invoke(heartSheet, date);
//                }
//            }
//        }
//
//        return heartSheet;
//    }
//
//    private AlertSheet setVal(String key, String value, AlertSheet alertSheet) throws Exception {
//        Field nameField = alertSheet.getClass().getField(key);
//        char[] cs = key.toCharArray();
//        cs[0] -= 32;
//        Method set = alertSheet.getClass().getMethod("set" + String.valueOf(cs), nameField.getType());
//        if (value != null && !value.equals("")) {
//            if (nameField.getType().getName().contains("String")) {
//                set.invoke(alertSheet, value);
//            } else if (nameField.getType().getName().contains("Integer")) {
//                set.invoke(alertSheet, Integer.parseInt(value));
//            } else if (nameField.getType().getName().contains("Date")) {
//                if (value.length() > 10 && value.substring(0, 10).matches("[\\d]{4}-[\\d]{2}-[\\d]{2}")) {
//                    Date date = new DateTime(value.replaceFirst(" ", "T")).toDate();
//                    set.invoke(alertSheet, date);
//                }else if(value.matches("[\\d]{10}|[\\d]{13}")){ // 有可能是10位或者13位时间戳
//                    if (value.matches("[\\d]{10}")){
//                        value=value+"000";
//                    }
//                    Date date=new DateTime(Long.valueOf(value)).toDate();
//                    set.invoke(alertSheet, date);
//                }
//            }
//        }
//
//        return alertSheet;
//    }
//
//    public boolean iteraJson(String str, Map res, DataSheet dataSheet, JSONObject wabc) throws Exception {
//        //因为json串中不一定有逗号，但是一定有：号，所以这里判断没有则已经value了
//        if (str.toString().indexOf(":") == -1) {
//            return true;
//        }
//        JSONObject fromObject = JSON.parseObject(str);
//        Iterator keys = fromObject.keySet().iterator();
//        while (keys.hasNext()) {
//            String key = keys.next().toString();
//            Object value = fromObject.get(key);
//            if (iteraJson(value.toString(), res, dataSheet,wabc)) {
//                res.put(key, value);
//                if (wabc.get(key) != null) {
//                    setVal(wabc.get(key).toString(), value.toString(), dataSheet);
//                }
//            }
//        }
//        return false;
//    }
//
//    public boolean iteraJson(String str, Map res, HeartSheet dataSheet, JSONObject wabc) throws Exception {
//        //因为json串中不一定有逗号，但是一定有：号，所以这里判断没有则已经value了
//        if (str.toString().indexOf(":") == -1) {
//            return true;
//        }
//        JSONObject fromObject = JSON.parseObject(str);
//        Iterator keys = fromObject.keySet().iterator();
//        while (keys.hasNext()) {
//            String key = keys.next().toString();
//            Object value = fromObject.get(key);
//            if (iteraJson(value.toString(), res, dataSheet,wabc)) {
//                res.put(key, value);
//                if (wabc.get(key) != null) {
//                    setVal(wabc.get(key).toString(), value.toString(), dataSheet);
//                }
//            }
//        }
//        return false;
//    }
//
//    public boolean iteraJson(String str, Map res, AlertSheet dataSheet, JSONObject jabc) throws Exception {
//        //因为json串中不一定有逗号，但是一定有：号，所以这里判断没有则已经value了
//        if (str.toString().indexOf(":") == -1) {
//            return true;
//        }
//        JSONObject fromObject = JSON.parseObject(str);
//        Iterator keys = fromObject.keySet().iterator();
//        while (keys.hasNext()) {
//            String key = keys.next().toString();
//            Object value = fromObject.get(key);
//            if (iteraJson(value.toString(), res, dataSheet,jabc)) {
//                res.put(key, value);
//                if (jabc.get(key) != null) {
//                    setVal(jabc.get(key).toString(), value.toString(), dataSheet);
//                }
//            }
//        }
//        return false;
//    }
//
//    /**
//     *  数据解析
//     */
//    public boolean testData(JSONObject fromObject, Map map, DataSheet dataSheet, List<DataSheet> list, JSONObject wabc, JSONObject sabc,JSONObject xabc) throws Exception{
//        Set<String> keys = fromObject.keySet();
//        Iterator<String> iterator = keys.iterator();
//        while (iterator.hasNext()) {
//            String key = iterator.next();
//            Object value = fromObject.get(key);
//            String val = value.toString();
//            if (val.indexOf("[{") == -1) {
//                //说明不存在数组json即格式为："[{" 开头的数据。可以允许是[10,11,12]的非json数组
//                if (val.indexOf(":") == -1 || (val.length() > 10 && val.substring(0, 10).matches("[\\d]{4}-[\\d]{2}-[\\d]{2}"))) {
//                    //当字符串中不存在：说明已经是值了，如果存在：也可能是日期类型的数据，所以用正则表达式匹配，如果是日期，就直接放入Map中
//                    map.put(key.replaceAll("\"", ""), val);
//                    if (wabc.getString(key) != null) {
//                        dataSheet = setVal(wabc.getString(key), val, dataSheet);
//                    }
//                    if (sabc.getString(key) != null) {
//                        dataSheet = setVal(sabc.getString(key), val, dataSheet);
//                    }
//                } else {
//                    iteraJson(val, map, dataSheet,wabc);
//                }
//            }
//        }
//
//        Iterator<String> iterator2 = keys.iterator();
//        while (iterator2.hasNext()) {
//            String key = iterator2.next();
//            Object value = fromObject.get(key);
//            String val = value.toString();
//            if (val.indexOf("[{") != -1) {
//                //包含随机ID
//                if (key.contains("random")) {
//                    if (wabc.getString("random") != null) {
//                        setVal(wabc.getString("random"), key.split("_")[1], dataSheet);
//                    }
//                    if (sabc.getString("random") != null) {
//                        setVal(sabc.getString("random"), key.split("_")[1], dataSheet);
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
//                            setVal(xkey, jsonObject.getString(next), dataSheet);
//                        }
//                        DataSheet sheet = new DataSheet();
//                        BeanUtils.copyProperties(dataSheet, sheet);
//                        list.add(sheet);
//                    }
//                    map.put(key.replaceAll("\"", ""), jsonArray);
//                } else {
//                    //说明value可能是一个json，这个json中任然包含数组。例如：{inner:[{a:1,b:2,c:3}]}
//                    if (val.indexOf(":") == -1) {
//                        return true;
//                    }
//                    JSONObject valObject = JSONObject.parseObject(val);
//                    testData(valObject, map, dataSheet, list,wabc,sabc,xabc);//符合当前递归条件
//                }
//            }
//        }
//        return false;
//    }
//
//    /**
//     *  告警解析
//     * @param fromObject
//     * @param map
//     * @param alertSheet
//     * @param list
//     * @param jabc
//     */
//    public boolean testAlert(JSONObject fromObject, Map map, AlertSheet alertSheet, ArrayList<AlertSheet> list, JSONObject jabc)throws Exception{
//
//        Set<String> keys = fromObject.keySet();
//        Iterator<String> iterator = keys.iterator();
//        while (iterator.hasNext()) {
//            String key = iterator.next();
//            Object value = fromObject.get(key);
//            String val = value.toString();
//            if (val.indexOf("[{") == -1) {
//                //说明不存在数组json即格式为："[{" 开头的数据。可以允许是[10,11,12]的非json数组
//                if (val.indexOf(":") == -1 || (val.length() > 10 && val.substring(0, 10).matches("[\\d]{4}-[\\d]{2}-[\\d]{2}"))) {
//                    //当字符串中不存在：说明已经是值了，如果存在：也可能是日期类型的数据，所以用正则表达式匹配，如果是日期，就直接放入Map中
//                    map.put(key.replaceAll("\"", ""), val);
//                    if (jabc.getString(key) != null) {
//                        alertSheet = setVal(jabc.getString(key), val, alertSheet);
//                    }
//                } else {
//                    iteraJson(val, map, alertSheet,jabc);
//                }
//            }
//        }
//
//        Iterator<String> iterator2 = keys.iterator();
//        while (iterator2.hasNext()) {
//            String key = iterator2.next();
//            Object value = fromObject.get(key);
//            String val = value.toString();
//            if (val.indexOf("[{") != -1) {
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
//        return false;
//
//
//    }
//
//    /**
//     *  心跳解析
//     * @param fromObject
//     * @param map
//     * @param heartSheet
//     * @param list
//     * @param xabc
//     */
//    public boolean testHeart(JSONObject fromObject, Map map, HeartSheet heartSheet, JSONObject xabc) throws Exception{
//        Set<String> keys = fromObject.keySet();
//        Iterator<String> iterator = keys.iterator();
//        while (iterator.hasNext()) {
//            String key = iterator.next();
//            Object value = fromObject.get(key);
//            String val = value.toString();
//            if (val.indexOf("[{") == -1) {
//                //说明不存在数组json即格式为："[{" 开头的数据。可以允许是[10,11,12]的非json数组
//                if (val.indexOf(":") == -1 || (val.length() > 10 && val.substring(0, 10).matches("[\\d]{4}-[\\d]{2}-[\\d]{2}"))) {
//                    //当字符串中不存在：说明已经是值了，如果存在：也可能是日期类型的数据，所以用正则表达式匹配，如果是日期，就直接放入Map中
//                    map.put(key.replaceAll("\"", ""), val);
//                    if (xabc.getString(key) != null) {
//                        heartSheet = setVal(xabc.getString(key), val, heartSheet);
//                    }
//                } else {
//                    iteraJson(val, map, heartSheet,xabc);
//                }
//            }
//        }
//
//        // 正常心跳只有一层 如果有多层 将此处注释释放
////        Iterator<String> iterator2 = keys.iterator();
////        while (iterator2.hasNext()) {
////            String key = iterator2.next();
////            Object value = fromObject.get(key);
////            String val = value.toString();
////            if (val.indexOf("[{") != -1) {
////                //包含随机ID
////                if (key.contains("random")) {
////                    if (xabc.getString("random") != null) {
////                        setVal(xabc.getString("random"), key.split("_")[1], heartSheet);
////                    }
////                    if (xabc.getString("random") != null) {
////                        setVal(xabc.getString("random"), key.split("_")[1], heartSheet);
////                    }
////                }
////                //说明存在数组json即格式为：[{开头的json数组
////                if (val.indexOf("[{") == 0) {
////                    //说明当前value就是一个json数组
////                    //去除[括号
////                    JSONArray jsonArray = JSONArray.parseArray(val);
////                    for (Object o : jsonArray) {
////                        JSONObject jsonObject = JSONObject.parseObject(o.toString());
////                        Iterator<String> iterator1 = xabc.keySet().iterator();
////                        while (iterator1.hasNext()) {
////                            //厂商参数key
////                            String next = iterator1.next();
////                            //对应数据值参数
////                            String xkey = xabc.getString(next);
////                            setVal(xkey, jsonObject.getString(next), heartSheet);
////                        }
////                        HeartSheet sheet = new HeartSheet();
////                        BeanUtils.copyProperties(sheet, heartSheet);
////                        list.add(sheet);
////                    }
////                    map.put(key.replaceAll("\"", ""), jsonArray);
////                } else {
////                    //说明value可能是一个json，这个json中任然包含数组。例如：{inner:[{a:1,b:2,c:3}]}
////                    if (val.indexOf(":") == -1) {
////                        return true;
////                    }
////                    JSONObject valObject = JSONObject.parseObject(val);
////                    testHeart(valObject, map, heartSheet, list,xabc);//符合当前递归条件
////                }
////            }
////        }
//
//
//
//
//
//
//        return false;
//
//    }
//
//
//
//    /**
//     *  mqtt数据解析
//     *  $目前思路是多个数据模板，对于解析来说，将模板拆分的越清晰，解析出来效果越好，
//     *  之所以拆出多个模板，是为了防止网关多的时候，可能使用的字段一致，含义不一致，代码指向就很难区分这些问题
//     *  数据类型模板：Map<topic,type> topicType 通过mqtt主题进行索引data是否需要区分类型（数据，报警，心跳）
//     *  网关解析模板：Map<topic,json>  wgjx 通过mqtt主题进行索引
//     *  设备解析模板：Map<topic,json> sbjx 通过mqtt主题进行索引
//     *  信号解析模板：Map<topic,json> xhjx 通过mqtt主题进行索引
//     *  mqtt发送至kafka模板：Map<topic+type+value,kafka> 通过拼接字符串进行索引 主要是为了防止key值重复
//     *
//     *
//     *  1.根据topic从数据类型模板里去值；有：表示当前网关的数据需进行分类，无：表示当前网关无报警与心跳
//     *
//     *
//     *
//     * @param message
//     * @throws Exception
//     */
//    @SneakyThrows
//    @Override
//    public void handleMessage(Message<?> message) throws MessagingException {
//        String topic = String.valueOf(message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC));
//        String payload = String.valueOf(message.getPayload());
//        System.out.println(topic);
//        System.out.println(payload);
//
//        if (payload.indexOf(":")==-1){
//            return;
//        }
//        // 第一步 将消息解析成JSONObject
//        JSONObject fromObject = JSONObject.parseObject(payload);
//
//        // 第二步 根据topic决定数据是否需要区分
//        if (topicType.containsKey(topic)){
//            String type=topicType.get(topic);
//            Object value=fromObject.get(type);
//            String val=value.toString();
//
//            String key=topic+type+val;
//            // 根据type值决定走向的模板，有数据走对应模板 无数据 说明数据未配置 ，可丢弃 也可保存至无法解析的数据库内
//            if (mqttKafka.containsKey(key)){
//                // 走报警解析
//                if (mqttKafka.get(key).equals(G.KAFKA_ALERTING_TOPIC)){
//                    Map map = new HashMap();
//                    AlertSheet alertSheet = new AlertSheet();
//                    ArrayList<AlertSheet> alertSheets = new ArrayList<>();
//                    testAlert(fromObject,map,alertSheet,alertSheets,bjjx.get(topic));
//
//                    System.out.println(JSON.toJSONString(alertSheets));
//                    System.out.println();
//
////                    kafkaProducer.sendMsg(G.KAFKA_ALERTING_TOPIC,);
//
//                }else if (mqttKafka.get(key).equals(G.KAFKA_HEARTBEAT_TOPIC)){ // 走心跳解析
//                    Map map = new HashMap();
//                    HeartSheet heartSheet = new HeartSheet();
////                    ArrayList<HeartSheet> heartSheets = new ArrayList<>();
//                    testHeart(fromObject,map,heartSheet,xtjx.get(topic));
//
//                    System.out.println(JSON.toJSONString(heartSheet));
//                    System.out.println(heartSheet.toString());
//
//                }else {// 数据解析
//                    Map map = new HashMap();
//                    DataSheet dataSheet = new DataSheet();
//                    ArrayList<DataSheet> dataSheets = new ArrayList<>();
//                    testData(fromObject,map,dataSheet,dataSheets,wgjx.get(topic),sbjx.get(topic),xhjx.get(topic));
//
//                    System.out.println(JSON.toJSONString(dataSheets));
//                    System.out.println(dataSheets.toString());
//                    kafkaProducer.sendMsg(G.KAFKA_DATA_TOPIC,JSON.toJSONString(dataSheets));
//                }
//
//            }else {
//                // 是直接丢弃，还是单独入库，后续讨论一下
//            }
//        }else {
//            // 当前topic不需要区分数据 直接走数据解析
//            Map map = new HashMap();
//            DataSheet dataSheet = new DataSheet();
//            ArrayList<DataSheet> dataSheets = new ArrayList<>();
//            testData(fromObject,map,dataSheet,dataSheets,wgjx.get(topic),sbjx.get(topic),xhjx.get(topic));
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
//
//
//
//
//    }
//
//
//    public static void main(String[] args) {
//        MqttConsumer mqttConsumer=new MqttConsumer();
//
//        System.out.println(mqttConsumer.mqttKafka.get("/Topic/devicetypere").equals(G.KAFKA_HEARTBEAT_TOPIC));
//    }
//
////    class AlertSheet{
////        public Integer id;
////        //网关ID
////        public String gateWayId;
////        //设备类型
////        public Integer type;
////        //设备ID
////        public String deviceId;
////        //设备通道
////        public String channelId;
////
////        public String point;
////        //采集时间
////        public Date time;
////        //数值
////        public String val;
////
////        public Integer getId() {
////            return id;
////        }
////
////        public void setId(Integer id) {
////            this.id = id;
////        }
////
////        public String getGateWayId() {
////            return gateWayId;
////        }
////
////        public void setGateWayId(String gateWayId) {
////            this.gateWayId = gateWayId;
////        }
////
////        public Integer getType() {
////            return type;
////        }
////
////        public void setType(Integer type) {
////            this.type = type;
////        }
////
////        public String getDeviceId() {
////            return deviceId;
////        }
////
////        public void setDeviceId(String deviceId) {
////            this.deviceId = deviceId;
////        }
////
////        public String getChannelId() {
////            return channelId;
////        }
////
////        public void setChannelId(String channelId) {
////            this.channelId = channelId;
////        }
////
////        public String getPoint() {
////            return point;
////        }
////
////        public void setPoint(String point) {
////            this.point = point;
////        }
////
////        public Date getTime() {
////            return time;
////        }
////
////        public void setTime(Date time) {
////            this.time = time;
////        }
////
////        public String getVal() {
////            return val;
////        }
////
////        public void setVal(String val) {
////            this.val = val;
////        }
////    }
////
////
////    class HeartSheet{
////        public Integer id;
////
////        //网关ID
////        public String gateWayId;
////        //设备类型
////        public Integer type;
////        //设备ID
////        public String deviceId;
////        //设备通道
////        public String channelId;
////
////        public String point;
////        //采集时间
////        public Date time;
////        //数值
////        public String val;
////
////        public Integer getId() {
////            return id;
////        }
////
////        public void setId(Integer id) {
////            this.id = id;
////        }
////
////        public String getGateWayId() {
////            return gateWayId;
////        }
////
////        public void setGateWayId(String gateWayId) {
////            this.gateWayId = gateWayId;
////        }
////
////        public Integer getType() {
////            return type;
////        }
////
////        public void setType(Integer type) {
////            this.type = type;
////        }
////
////        public String getDeviceId() {
////            return deviceId;
////        }
////
////        public void setDeviceId(String deviceId) {
////            this.deviceId = deviceId;
////        }
////
////        public String getChannelId() {
////            return channelId;
////        }
////
////        public void setChannelId(String channelId) {
////            this.channelId = channelId;
////        }
////
////        public String getPoint() {
////            return point;
////        }
////
////        public void setPoint(String point) {
////            this.point = point;
////        }
////
////        public Date getTime() {
////            return time;
////        }
////
////        public void setTime(Date time) {
////            this.time = time;
////        }
////
////        public String getVal() {
////            return val;
////        }
////
////        public void setVal(String val) {
////            this.val = val;
////        }
////    }
////
////    class DataSheet{
////        public Integer id;
////        //网关ID
////        public String gateWayId;
////        //设备类型
////        public Integer type;
////        //设备ID
////        public String deviceId;
////        //设备通道
////        public String channelId;
////
////        public String point;
////        //采集时间
////        public Date time;
////        //数值
////        public String val;
////
////
////        public Integer getId() {
////            return id;
////        }
////
////        public void setId(Integer id) {
////            this.id = id;
////        }
////
////        public String getGateWayId() {
////            return gateWayId;
////        }
////
////        public void setGateWayId(String gateWayId) {
////            this.gateWayId = gateWayId;
////        }
////
////        public Integer getType() {
////            return type;
////        }
////
////        public void setType(Integer type) {
////            this.type = type;
////        }
////
////        public String getDeviceId() {
////            return deviceId;
////        }
////
////        public void setDeviceId(String deviceId) {
////            this.deviceId = deviceId;
////        }
////
////        public String getChannelId() {
////            return channelId;
////        }
////
////        public void setChannelId(String channelId) {
////            this.channelId = channelId;
////        }
////
////        public String getPoint() {
////            return point;
////        }
////
////        public void setPoint(String point) {
////            this.point = point;
////        }
////
////        public Date getTime() {
////            return time;
////        }
////
////        public void setTime(Date time) {
////            this.time = time;
////        }
////
////        public String getVal() {
////            return val;
////        }
////
////        public void setVal(String val) {
////            this.val = val;
////        }
////    }
//
//}
