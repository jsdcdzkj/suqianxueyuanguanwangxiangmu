package com.jsdc.iotpt.controller;


import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsdc.common.minio.service.MinioTemplate;
import com.jsdc.iotpt.common.DateUtils;
import com.jsdc.iotpt.common.SysConfig;
import com.jsdc.iotpt.model.*;
import com.jsdc.iotpt.model.sys.SysBuildArea;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.mqtt.MqttClientCallback;
import com.jsdc.iotpt.mqtt.MqttClientConnect;
import com.jsdc.iotpt.service.*;
import com.jsdc.iotpt.util.FileUtils;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.*;
import com.jsdc.iotpt.websocket.WsClientPool;
import lombok.SneakyThrows;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.util.Strings;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.websocket.Session;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ClassName: MqttController
 * Description:
 * date: 2023/5/25 17:23
 *
 * @author bn
 */
@Controller
@RequestMapping("mqtt")
public class MqttController {
    private static final Logger log = LoggerFactory.getLogger(MqttController.class);


//    @Autowired
//    private MessageProducer mqttSubscriber;

    //    @Autowired
//    private MqttGateway mqttGateway;
    @Autowired
    private ConfigLinkService configLinkService;
    @Autowired
    private ConfigTopicService configTopicService;
    @Autowired
    private SysFileService sysFileService;
    @Autowired
    private SysDictService sysDictService;
    @Autowired
    private SysBuildAreaService sysBuildAreaService;
    @Autowired
    private MinioTemplate minioTemplate;


    JSONObject startRtspJSON=JSONObject.parseObject("{\"deviceFlag\": \"RTSPSTART\",\"rtspList\": [{\"pushSrcUrl\": \"Y2FtZXJh\",\"channlNo\": 2,\"flag\": \"0\"}]}");
    JSONObject stopRtspJSON=JSONObject.parseObject("{\"deviceFlag\": \"RTSPSTOP\",\"pushId\": [2]}");


    @Value("${warnImgPath}")
    private String warnImgPath;

//    @Value("${linkzhyy.ip}")
//    private String zhyyIp;



    @PostMapping("/startVideo")
    @ResponseBody
    public ResultInfo startVideo(String deviceCode){

        startRtspJSON.put("deviceId",deviceCode);
        for (Map.Entry<String,MqttClientConnect> item:MqttClientConnect.mqttClients.entrySet()){
            try {
                item.getValue().pub("/camera/sub",startRtspJSON.toJSONString());
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }


        return ResultInfo.success();
    }

    @PostMapping("/stopVideo")
    @ResponseBody
    public ResultInfo stopVideo(String deviceCode){

        stopRtspJSON.put("deviceId",deviceCode);
        for (Map.Entry<String,MqttClientConnect> item:MqttClientConnect.mqttClients.entrySet()){
            try {
                item.getValue().pub("/camera/sub",stopRtspJSON.toJSONString());
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        return ResultInfo.success();
    }


    @PostMapping("/message")
    @ResponseBody
    public String message(@RequestBody String message) throws IOException {


        /** 将String转换为JSON **/
        JSONObject result= JSONObject.parseObject(message);

        /** 接收成功返回 **/
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("code",200);
        jsonObject.put("message","数据接收成功");


        if (!result.containsKey("deviceId")){
            return jsonObject.toJSONString();
        }

        String deviceId=result.getString("deviceId");






        Integer type=result.getInteger("type");

        String base64=result.getString("signBigAvatar");

        String fileName=System.currentTimeMillis()+".jpg";
        FileUtils.base64ToFile(warnImgPath+"/videowarn",base64,fileName);

        String signImg=result.getString("signAvatar");
        String fileName1="xiao"+System.currentTimeMillis()+".jpg";
        FileUtils.base64ToFile(warnImgPath+"/videowarn",signImg,fileName1);

//        String param = "base64='"+base64+"'&fileName='"+fileName+"'";

//        HttpUtils.sendPost(zhyyIp + "/mission/imgFileBase", param);

        SysDict sysDict=sysDictService.getBaseMapper().
                selectOne(Wrappers.<SysDict>lambdaQuery().eq(SysDict::getDictType,"videoWarn").eq(SysDict::getDictValue,String.valueOf(type)));


        SysFile sysFile=new SysFile();
        sysFile.setBizType("7");
        sysFile.setFileName(fileName);
        sysFile.setFileUrl(fileName);
        sysFile.setCreateTime(new Date());
        sysFile.setFileType("image/jpeg");


        sysFileService.getBaseMapper().insert(sysFile);





        return jsonObject.toJSONString();
    }

    /**
     * 建立mqtt连接
     *
     * @param id 连接id
     * @return
     */
    @RequestMapping("addMqttConnect")
    @ResponseBody
    public ResultInfo addMqttConnect(Integer id) {


        // 已经建立连接
        if (null != SysConfig.getInstance().getLinkClientId().get(id)) {
            return ResultInfo.success();
        }
        ConfigLink configLinkData = configLinkService.getById(id);
        // 随机建立clientId
        String clientId = String.valueOf(System.currentTimeMillis());
        SysConfig.getInstance().getLinkClientId().put(id,clientId);
        MqttClientConnect mqttClientConnect = new MqttClientConnect();
        mqttClientConnect.setMqttClientId(clientId);
        mqttClientConnect.setLinkId(id);
        try {
            mqttClientConnect.setMqttClient(
                    String.format("%s%s","tcp://",configLinkData.getServiceAddress()),
                    clientId,
                    configLinkData.getUsername(),
                    configLinkData.getPassword(),
                    id,
                    false,
                    new MqttClientCallback(clientId,id));
        } catch (MqttException e) {
            return ResultInfo.error("mqtt连接建立失败！");
        }
        MqttClientConnect.mqttClients.put(clientId, mqttClientConnect);
        return ResultInfo.success();
    }

    /**
     * 移除mqtt连接
     *
     * @param id 连接id
     * @return
     */
    @RequestMapping("removeMqttConnect")
    @ResponseBody
    public ResultInfo removeMqttConnect(Integer id) {

        //
        String clientId=SysConfig.getInstance().getLinkClientId().get(id);
        if (null == clientId) {
            return ResultInfo.success();
        }

        ConfigLink configLinkData = configLinkService.getById(id);

        MqttClientConnect mqttClientConnect = MqttClientConnect.mqttClients.get(clientId);
        if (null != mqttClientConnect) {
            try {
                mqttClientConnect.close();
                SysConfig.getInstance().getLinkClientId().put(id,null);
                MqttClientConnect.mqttClients.remove(clientId);

                ConfigTopic configTopic=new ConfigTopic();
                configTopic.setTopicType(1);
                configTopic.setTopicStatus(1);
                configTopic.setLinkId(id);
                List<ConfigTopic> configTopics=configTopicService.getList(configTopic);

                configTopics.forEach(x->{
                    x.setTopicStatus(0);
                    configTopicService.updateById(x);
                });


                configLinkData.setConnectionStatus(1);
                configLinkService.updateById(configLinkData);

            } catch (MqttException e) {
                return ResultInfo.error("mqtt连接移除失败！");
            }
        }else{
            configLinkData.setConnectionStatus(1);
            configLinkService.updateById(configLinkData);
            return ResultInfo.error("mqtt连接已被切换！");
        }
        return ResultInfo.success();
    }

    /**
     * 测试连接
     */
    @RequestMapping("testMqttConnect")
    @ResponseBody
    public ResultInfo testMqttConnect(ConfigLink configLink) {
        if(StringUtils.isEmpty(configLink.getServiceAddress())||
                StringUtils.isEmpty(configLink.getClientId())||
                StringUtils.isEmpty(configLink.getUsername())||
                StringUtils.isEmpty(configLink.getPassword())){
            return ResultInfo.error("参数异常，请检查参数格式！");
        }



        MqttClientConnect mqttClientConnect = new MqttClientConnect();
        boolean isCon = false;
        try {
            mqttClientConnect.setMqttClient(String.format("%s%s","tcp://",configLink.getServiceAddress()), configLink.getClientId(), configLink.getUsername(), configLink.getPassword(),null, false, new MqttClientCallback(configLink.getClientId(),null));
            isCon = mqttClientConnect.getMqttClient().isConnected();
        } catch (MqttException e) {
            return ResultInfo.error("连接错误，请检查连接地址，例：tcp://127.0.0.1:1883");
        }
        return ResultInfo.success("测试连接成功", isCon);
    }


    /**
     * 动态订阅主题
     *
     * @return
     * @paaram linkId 连接id
     * @paaram topicId 主题id
     */
    @RequestMapping("addTopic")
    @ResponseBody
    public ResultInfo addTopic(Integer linkId, Integer topicId) throws InterruptedException {

//        Thread.sleep(1000000000);

        String clientId=SysConfig.getInstance().getLinkClientId().get(linkId);
        if (null == clientId) {
            return ResultInfo.error("mqtt服务器连接未建立！");
        }

        // 主题
        ConfigTopic configTopic = configTopicService.getById(topicId);

        if (null != MqttClientConnect.mqttClients.get(clientId)) {
            try {
                MqttClientConnect.mqttClients.get(clientId).unsub(clientId, topicId,configTopic.getTopicName());
                // 针对主题的监听
                MqttClientConnect.mqttClients.get(clientId).
                        sub(clientId,linkId, topicId, configTopic.getTopicName(), 0);
            } catch (MqttException e) {
                return ResultInfo.error(e.getMessage());
            }
        } else {
            return ResultInfo.error("mqtt服务器连接未建立！");
        }

//        ((MqttPahoMessageDrivenChannelAdapter) mqttSubscriber).addTopic(topic,0);
        return ResultInfo.success();
    }

    /**
     * 批量订阅主题
     */
    @RequestMapping("batchAddTopic")
    @ResponseBody
    public ResultInfo batchAddTopic(ConfigTopicVo bean) {

        List<String> errorTopic = new ArrayList<>();

        for (int i = 0; i < bean.getLinkIdList().size(); i++) {
            // 连接
            String clientId=SysConfig.getInstance().getLinkClientId().get(bean.getLinkIdList().get(i));
            if (null == clientId) {
                continue;
            }
//            ConfigLink configLink = configLinkService.getById(bean.getLinkIdList().get(i));

            // 主题
            ConfigTopic configTopic = configTopicService.getById(bean.getIdList().get(i));

            if (null != MqttClientConnect.mqttClients.get(clientId)) {
                try {
                    MqttClientConnect.mqttClients.get(clientId).unsub(clientId,configTopic.getId(),configTopic.getTopicName());
                    MqttClientConnect.mqttClients.get(clientId).sub(clientId,bean.getLinkIdList().get(i), configTopic.getId(),configTopic.getTopicName(), 0);
                    //
                    configTopic.setTopicStatus(1);
                    configTopicService.updateById(configTopic);
                } catch (MqttException e) {

                    errorTopic.add(configTopic.getTopicName());
                }
            } else {
                errorTopic.add(configTopic.getTopicName());
            }

        }

        if (!errorTopic.isEmpty()) {
            return ResultInfo.error(errorTopic, "存在主题未订阅成功！");
        }

        return ResultInfo.success("批量订阅主题成功！");

    }

    /**
     * 批量移除主题
     */
    @RequestMapping("batchRemoveTopic")
    @ResponseBody
    public ResultInfo batchRemoveTopic(ConfigTopicVo bean) {

        List<String> errorTopic = new ArrayList<>();

//        Map<Integer,List<ConfigTopic>> map=new HashMap<>();
//
//        for (int i = 0; i < bean.getLinkIdList().size(); i++) {
//            if (map.containsKey(bean.getLinkIdList().get(i))){
//                // 主题
//                ConfigTopic configTopic = configTopicService.getById(bean.getIdList().get(i));
//                map.get(bean.getLinkIdList().get(i)).add(configTopic);
//            }else {
//                map.put(bean.getLinkIdList().get(i),new ArrayList<>());
//            }
//        }
//
//        for (Map.Entry<Integer, List<ConfigTopic>> item: map.entrySet()) {
//            List<String> topicNames=item.getValue().stream().map(x-> x.getTopicName()).collect(Collectors.toList());
//            String[] msgtopic=topicNames.toArray(new String[topicNames.size()]);
//            try {
//                MqttClientConnect.mqttClients.get(item.getKey()).unsubs(msgtopic);
//            } catch (MqttException e) {
//                e.printStackTrace();
//            }
//        }

        for (int i = 0; i < bean.getLinkIdList().size(); i++) {
            // 连接
            String clientId=SysConfig.getInstance().getLinkClientId().get(bean.getLinkIdList().get(i));
            if (null == clientId) {
                continue;
            }
//            // 连接
//            ConfigLink configLink = configLinkService.getById(bean.getLinkIdList().get(i));

            // 主题
            ConfigTopic configTopic = configTopicService.getById(bean.getIdList().get(i));

            if (null != MqttClientConnect.mqttClients.get(clientId)) {
                try {
                    MqttClientConnect.mqttClients.get(clientId).unsub(clientId,configTopic.getId(),configTopic.getTopicName());
                    configTopic.setTopicStatus(0);
                    configTopicService.updateById(configTopic);
                } catch (MqttException e) {
                    errorTopic.add(configTopic.getTopicName());
                }
            } else {
                errorTopic.add(configTopic.getTopicName());
            }

        }

        if (!errorTopic.isEmpty()) {
            return ResultInfo.error(errorTopic, "存在主题未取消订阅成功！");
        }

        return ResultInfo.success("批量取消订阅主题成功！");

    }

    /**
     * 动态移除主题
     *
     * @return
     * @paaram linkId 连接id
     * @paaram topicId 主题id
     */
    @RequestMapping("removeTopic")
    @ResponseBody
    public ResultInfo removeTopic(Integer linkId, Integer topicId) {

        String clientId=SysConfig.getInstance().getLinkClientId().get(linkId);
        if (null == clientId) {
            return ResultInfo.success();
        }
        // 连接
//        ConfigLink configLink = configLinkService.getById(linkId);

//        String url=configLink.getServiceAddress().
//                replaceFirst("tcp","http").
//                substring(0,configLink.getServiceAddress().lastIndexOf(":")+1);

//        OkHttpClient client = new OkHttpClient();
//
//        JSONObject paramObject = new JSONObject();
//        paramObject.put("topic", "/Topic/likong");
//
//        Request request = new Request.Builder()
//                //这里必须手动设置为json内容类型
//                .addHeader("content-type", "application/json")
//                //设置token
//                .header("Authorization", Credentials.basic(configLink.getApiKey(), configLink.getSecretKey()))
//                //参数放到链接后面
//                .url(url+":18083/api/v5/clients/"+configLink.getClientId()+"/unsubscribe")
//                .post(okhttp3.RequestBody.create(MediaType.parse("application/json; charset=utf-8"), paramObject.toString()))
//                        .build();


//        //设置请求头
//        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
//
//        RequestBody requestBody = RequestBody.create(mediaType,kkString);
//
//        Request request = new Request.Builder()
//                .url(url+":18083/api/v5/clients/"+configLink.getClientId()+"/unsubscribe")
//                .header("Authorization", Credentials.basic(configLink.getApiKey(), configLink.getSecretKey()))
//                .post(requestBody)
//                .build();

//        Response response = null;
//        try {
//            response = client.newCall(request).execute();
//
//            System.out.println(response.body().string());
//            System.out.println();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        // 主题
        ConfigTopic configTopic = configTopicService.getById(topicId);

        if (null != MqttClientConnect.mqttClients.get(clientId)) {
            try {
                MqttClientConnect.mqttClients.get(clientId).unsub(clientId,topicId,configTopic.getTopicName());
            } catch (MqttException e) {
                return ResultInfo.error(e.getMessage());
            }
        }

        return ResultInfo.success();
    }


    /**
     *  获取设备状态
     * @param
     * @param topic
     * @param type
     * @param sn
     * @return
     */
    @RequestMapping("getDeviceState")
    @ResponseBody
    public ResultInfo getDeviceState(Integer linkId,String topic,String type,String sn) {
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("type",type);
                jsonObject.put("sn",sn);

                String clientId=SysConfig.getInstance().getLinkClientId().get(linkId);

                if (null!=clientId){
                    MqttClientConnect.mqttClients.get(clientId).pub(topic, jsonObject.toJSONString());
                }else {
                    for (Map.Entry<String,MqttClientConnect> item:MqttClientConnect.mqttClients.entrySet()){
                        item.getValue().pub(topic,jsonObject.toJSONString());
                    }
                }
            }
        }).start();
        return ResultInfo.success("成了！");
    }


    /**
     *  设备控制
     * @param gateWayId
     * @param PublicAddr
     * @param tagid
     * @param value
     * @param type
     * @return
     */
    @RequestMapping("writeControl")
    @ResponseBody
    public ResultInfo writeControl(Integer linkId,String topic,String gateWayId,String PublicAddr,String tagid,String value,String type){
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("sn",gateWayId);
                jsonObject.put("PublicAddr",PublicAddr);
                jsonObject.put("tagid",tagid);
                jsonObject.put("value",value);
                jsonObject.put("type",type);

                String clientId=SysConfig.getInstance().getLinkClientId().get(linkId);
                if (null!=clientId){
                   MqttClientConnect.mqttClients.get(clientId).pub(topic, jsonObject.toJSONString());
                }else {
                    for (Map.Entry<String,MqttClientConnect> item:MqttClientConnect.mqttClients.entrySet()){
                        item.getValue().pub(topic,jsonObject.toJSONString());
                    }
                }
            }
        }).start();
        return ResultInfo.success();
    }


    /**
     * 推送数据到mqtt
     *
     * @return
     * @throws MqttException
     */
    @RequestMapping("isLightControl")
    @ResponseBody
    public ResultInfo isLightControl(String value) throws MqttException {

        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("sn","202401314060");
                jsonObject.put("PublicAddr","LightControl_random");
                jsonObject.put("tagid","Open");
                jsonObject.put("value",value);
                jsonObject.put("type","write");
                jsonObject.put("time",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                for (Map.Entry<String,MqttClientConnect> item:MqttClientConnect.mqttClients.entrySet()){
                    item.getValue().pub("/linkqi/Lightsub",jsonObject.toJSONString());
                }

            }
        }).start();
        return ResultInfo.success();
    }

    /**
     * 推送数据到mqtt
     *
     * @param clientId
     * @param topic
     * @param message
     * @return
     * @throws MqttException
     */
    @RequestMapping("sendToMqtt")
    @ResponseBody
    public ResultInfo sendToMqtt(String clientId, String topic, String message) throws MqttException {

        System.out.println(message);

//        mqttGateway.sendToMqtt(topic, message);
        if (Strings.isNotEmpty(clientId)){
            MqttClientConnect.mqttClients.get(clientId).pub(topic, message);
        }else {
            for (Map.Entry<String,MqttClientConnect> item:MqttClientConnect.mqttClients.entrySet()){
                item.getValue().pub(topic,message);
            }
        }


        return ResultInfo.success("成了！");
    }

    @RequestMapping("remoteControl")
    @ResponseBody
    public ResultInfo remoteControl(@RequestBody RemoteControl remoteControl) throws MqttException {
        RemoteControl.Commend commend = remoteControl.getCommend();
        String jsonString = JSONObject.toJSONString(commend);
        String replace = jsonString.replace("com", "COM")
                                   .replace("devName", "DevName")
                                   .replace("channelName", "ChannelName")
                                   .replace("values", "Values");

        List<ConfigTopic> topicList = remoteControl.getTopicList();
        for (ConfigTopic configTopic : topicList) {
            Collection<MqttClientConnect> values = MqttClientConnect.mqttClients.values();
            for (MqttClientConnect value : values) {
                value.pub(configTopic.getTopicName(), replace, 2);
            }
        }
        return ResultInfo.success("成了！");
    }


//    @RequestMapping("sendMsg")
//    @ResponseBody
//    public String sendMsg(WarningInfoVo vo){
//        Map<String, Session> onlineClientMap = wsClientPool.getOnlineClientMap();
//
//        // 使用Jackson库将对象转换为JSON
//        ObjectMapper objectMapper = new ObjectMapper();
//        String json = "";
//        try {
//            json = objectMapper.writeValueAsString(vo);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//            return "error";
//        }
//        for (Session wsSession : onlineClientMap.values()) {
//            wsSession.getAsyncRemote().sendText(json);
//        }
//        return "success";
//    }


    /**
     * controlGroundLock
     */
    @RequestMapping("controlGroundLock")
    @ResponseBody
    public String controlGroundLock(@RequestBody Map<String, Object> param){
        Integer option = MapUtils.getInteger(param, "option");
        JSONObject msgObj = new JSONObject();
        msgObj.put("sign", MapUtils.getString(param, "sign"));
        msgObj.put("sn", MapUtils.getString(param, "sn"));
        msgObj.put("timestamp", MapUtils.getString(param, "timestamp"));
        msgObj.put("msg_id", "GO2021070112000101");
        msgObj.put("msg_type", "GroundlockOption");
        // 发布信息操作
        JSONObject msgData = new JSONObject();
        // 1:升锁;  3:降锁后不⾃动升锁（D类型地锁）
        msgData.put("option", option);
        // 0,  强制操作0：否  1：是
        msgData.put("force", 1);
        // 1   ⻋位id  0是左车位，1是右车位
        msgData.put("zone_id", MapUtils.getIntValue(param, "zone_id"));

        msgObj.put("msg_data", msgData);
        String sendMsg = msgObj.toJSONString();
        MqttController.log.info("控制升降地锁的命令: {}",sendMsg);
        String topc = "/pushzs/75a971b6-25878aa7";
        for (Map.Entry<String, MqttClientConnect> item : MqttClientConnect.mqttClients.entrySet()) {
            try {
                item.getValue().pub(topc, sendMsg);
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        }
        return "success";
    }

    public static void main(String[] args) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("deviceName", "123123");

        String result= HttpUtil.post("http://192.168.20.200:8086/mqtt/sendMsg", paramMap);
        System.out.printf(result);
    }




    @PostMapping("work_Air")
    @ResponseBody
    public  String work_Air(@RequestBody String body){
        MqttController.log.info("17空调插座发布指令开始====: {}", body);
        JSONObject jsonObject = JSONObject.parseObject(body);
        JSONObject msgObj = new JSONObject();
        msgObj.put("read","workInfo");
        msgObj.put("slotNum",1);
        sendWorkInfo(msgObj,jsonObject.getString("topic"));
        MqttController.log.info("17空调发布指令成功====: {}", msgObj.toString());
        return "success";
    }


    public void sendWorkInfo(JSONObject msgObj,String topic){
        String sendMsg = msgObj.toJSONString();
        MqttController.log.info("1702空调====发送: {}", sendMsg);
        for (Map.Entry<String, MqttClientConnect> item : MqttClientConnect.mqttClients.entrySet()) {
            try {
                item.getValue().pub(topic, sendMsg);
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @PostMapping("action_Air")
    @ResponseBody
    public  String action_Air(@RequestBody String body){
        MqttController.log.info("17空调插座发布指令开始====: {}", body);
        JSONObject jsonObject = JSONObject.parseObject(body);
        JSONObject msgObj = new JSONObject();
        if (("0.00").equals(jsonObject.getString("isOnOff"))){
            msgObj.put("action","on");
        }else {
            msgObj.put("action","off");
        }
        msgObj.put("slotNum",1);
        msgObj.put("id","123456789111");
        sendWorkInfo(msgObj,jsonObject.getString("topic"));
        MqttController.log.info("17空调发布指令成功====: {}", msgObj.toString());
        return "success";
    }

    @PostMapping("action_Air_All")
    @ResponseBody
    public  String action_Air_All(@RequestBody String body){
        MqttController.log.info("17空调插座发布指令开始====: {}", body);
        JSONObject jsonObject = JSONObject.parseObject(body);
        JSONObject msgObj = new JSONObject();
        if (("0.00").equals(jsonObject.getString("isOnOff"))){
            msgObj.put("action","off");
        }else {
            msgObj.put("action","on");
        }
        msgObj.put("slotNum",1);
        msgObj.put("id","123456789111");
        sendWorkInfo(msgObj,jsonObject.getString("topic"));
        MqttController.log.info("17空调发布指令成功====: {}", msgObj.toString());
        return "success";
    }



    @PostMapping("action_Air_Syn")
    @ResponseBody
    public  String action_Air_Syn(@RequestBody String body){
        MqttController.log.info("17空调插座发布指令开始空开====: {}", body);
        JSONObject jsonObject = JSONObject.parseObject(body);
        JSONObject msgObj = new JSONObject();
        //解锁
        msgObj.put("type","write");
//        msgObj.put("sn","202401304039");
        msgObj.put("sn",jsonObject.get("iccId"));
        msgObj.put("time", DateUtils.dateToStr(new Date()));
        msgObj.put("PublicAddr",jsonObject.get("deviceCode")+"_random");
        msgObj.put("tagid","Open");
        msgObj.put("value","2");
        sendWorkInfo(msgObj,"/linkqi/Airsub");
        if (("0.00").equals(jsonObject.getString("isOnOff"))){
            msgObj.put("value","6");
        }else {
            msgObj.put("value","7");
        }
        sendWorkInfo(msgObj,"/linkqi/Airsub");
        MqttController.log.info("17空调发布指令成功空开====: {}", msgObj.toString());
        return "success";
    }


    @PostMapping("work_TenantInfo")
    @ResponseBody
    public  String work_TenantInfo(@RequestBody String body){
        MqttController.log.info("租户电闸发布指令开始====: {}", body);
        JSONObject jsonObject = JSONObject.parseObject(body);
        JSONObject msgObj = new JSONObject();
        //解锁
        msgObj.put("type","write");
        msgObj.put("sn",jsonObject.get("gteway"));
        msgObj.put("time", DateUtils.dateToStr(new Date()));
        msgObj.put("PublicAddr",jsonObject.get("deviceCode")+"_random");
        msgObj.put("tagid","Open");
        msgObj.put("value","2");
        sendWorkInfo(msgObj,jsonObject.get("topic").toString());
        if (("0.00").equals(jsonObject.getString("isOnOff"))){
            msgObj.put("value","0");
        }else {
            msgObj.put("value","7");
        }
        sendWorkInfo(msgObj,jsonObject.get("topic").toString());
        MqttController.log.info("租户电闸发布指令开始====: {}", msgObj.toString());
        return "success";
    }


    @PostMapping("/merchant/electricity/switch")
    @ResponseBody
    public  String merchantElectricitySwitch(@RequestBody String body){
        MqttController.log.info("租户电闸发布指令开始====: {}", body);
        JSONObject jsonObject = JSONObject.parseObject(body);
        JSONObject msgObj = new JSONObject();
        //解锁
        msgObj.put("type","write");
        msgObj.put("sn",jsonObject.get("gteway"));
        msgObj.put("time", DateUtils.dateToStr(new Date()));
        msgObj.put("PublicAddr",jsonObject.get("deviceCode")+"_random");
        msgObj.put("tagid","Open");
        msgObj.put("value","2");
        sendWorkInfo(msgObj,jsonObject.get("topic").toString());
        if (("0.00").equals(jsonObject.getString("isOnOff"))){ // 开
            msgObj.put("value","21845");
        }else {
            msgObj.put("value","43690"); // 关
        }
        sendWorkInfo(msgObj,jsonObject.get("topic").toString());
        MqttController.log.info("租户电闸发布指令开始====: {}", msgObj.toString());
        return "success";
    }

}
