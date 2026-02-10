package com.jsdc.iotpt.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsdc.iotpt.mapper.SysMessageMapper;
import com.jsdc.iotpt.mapper.SysMsgSettingMapper;
import com.jsdc.iotpt.mapper.SysUserMapper;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.sys.SysMessage;
import com.jsdc.iotpt.model.sys.SysMsgSetting;
import com.jsdc.iotpt.service.SysUserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * ClassName: GetTuiConfig
 * Description:
 * date: 2024/11/14 13:57
 *
 * @author bn
 */
@Component
@Slf4j
public class GetTuiConfig {

    private static String AppID="dGJoFH8ENg942g2T378we3";

    private static String AppKey="d3MLEcDsSV6Hp3MjUaSLl8";

    private static String AppSecret="LTuoUDDZi18M6GYxM2Ecv5";

    private static String MasterSecret="VBnydPY9nuA5VfdcTv8C02";

    private static String BaseUrl="https://restapi.getui.com/v2/";


    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private SysMsgSettingMapper sysMsgSettingMapper;
    @Autowired
    private SysMessageMapper sysMessageMapper;


    private String getSHA256(String content) throws Exception {
        // 将字节转换为十六进制字符串
        StringBuilder sb = new StringBuilder();

        // 创建一个MessageDigest对象
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        // 使用digest()方法处理输入的字节
        byte[] hashBytes = digest.digest(content.getBytes("UTF-8"));
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b & 0xff));
        }

        return sb.toString();
    }

    /**
     *  获取sign
     * @return
     */
    private String getSign(long currentTime) throws Exception{

        return getSHA256(AppKey+currentTime+MasterSecret);
    }

    /**
     *  获取token
     * @return
     */
    public String getToken() throws Exception{
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        TokenParams tokenParams=new TokenParams();

        long currentTime=System.currentTimeMillis();
        tokenParams.setSign(getSign(currentTime));
        tokenParams.setTimestamp(String.valueOf(currentTime));
        tokenParams.setAppkey(AppKey);

        String jsonParams= JSON.toJSONString(tokenParams);

        HttpEntity<String> httpEntity=new HttpEntity<String>(jsonParams,httpHeaders);

        JSONObject result = restTemplate.postForObject(BaseUrl+AppID+"/auth", httpEntity, JSONObject.class);

        System.out.println(result);

        if (result.containsKey("code")&&0==result.getInteger("code")){
            return result.getJSONObject("data").getString("token");
        }

        return null;
    }
    /**
     *  单推
     * @return
     */
    public String getSingleCid() throws Exception{
        String token=getToken();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("token",token);

        String jsonParams="{\n" +
                "  \"request_id\": \""+System.currentTimeMillis()+"\",\n" +
                "  \"settings\": {\n" +
                "    \"ttl\": 7200000\n" +
                "  },\n" +
                "  \"audience\": {\n" +
                "    \"cid\": [\n" +
                "      \"558a2b85ac706a75b0527c70fc41417e\"\n" +
                "    ]\n" +
                "  },\n" +
                "  \"push_message\": {\n" +
                "    \"notification\": {\n" +
                "      \"title\": \"请填写通知标题\",\n" +
                "      \"body\": \"请填写通知内容\",\n" +
                "      \"click_type\": \"url\",\n" +
                "      \"url\": \"https//:xxx\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
        System.out.println(jsonParams);

        HttpEntity<String> httpEntity=new HttpEntity<String>(jsonParams,httpHeaders);

        JSONObject result = restTemplate.postForObject(BaseUrl+AppID+"/push/single/cid", httpEntity, JSONObject.class);

        System.out.println(result);

        return null;
    }

    /**
     *  批量推，单推合并
     * @return List<Integer> userIds,String title,String context
     * @throws Exception
     */
    public String getListCid(List<Integer> userIds,String viewId,Integer menuId,String msgType,String title,String context) throws Exception{


        List<Integer> pushUserIds=new ArrayList<>();

        userIds.forEach(x->{
            SysMessage sysMessage=new SysMessage();
            sysMessage.setIsRead(0);
            sysMessage.setIsDel(0);
            sysMessage.setContext(context);
            sysMessage.setTitle(title);
            if (StringUtils.isNotEmpty(viewId)){
                sysMessage.setViewId(viewId);
            }
            sysMessage.setMenuId(menuId);
            sysMessage.setMsgType(msgType);
            sysMessage.setPushUser(x);
            sysMessage.setCreateTime(new Date());
            sysMessageMapper.insert(sysMessage);

            SysMsgSetting sysMsgSetting=sysMsgSettingMapper.
                    selectOne(Wrappers.<SysMsgSetting>lambdaQuery().
                            eq(SysMsgSetting::getUserId,x));
            if (sysMsgSetting!=null){
                if (sysMsgSetting.getIsMsg()==1){
                    switch (msgType){
                        case "1":
                            if (sysMsgSetting.getIsSys()==1)
                                pushUserIds.add(x);
                            break;
                        case "2":
                            if (sysMsgSetting.getIsItss()==1)
                                pushUserIds.add(x);
                            break;
                        case "3":
                            if (sysMsgSetting.getIsRfid()==1)
                                pushUserIds.add(x);
                            break;
                        case "4":
                            if (sysMsgSetting.getIsOff()==1)
                                pushUserIds.add(x);
                            break;
                        case "5":
                            if (sysMsgSetting.getIsCar()==1)
                                pushUserIds.add(x);
                            break;
                        default:
                            break;
                    }
                }
            }else {
                pushUserIds.add(x);
            }
        });

        if (pushUserIds.isEmpty()){
            return "不符合推送条件";
        }

        List<SysUser> sysUsers=userMapper.
                selectList(Wrappers.<SysUser>lambdaQuery().
                        eq(SysUser::getIsDel,0).
                        isNotNull(SysUser::getCId).
                        in(SysUser::getId,pushUserIds).
                        select(SysUser::getCId));

        List<String> cids=sysUsers.stream().map(x->"\""+x.getCId()+"\"").collect(Collectors.toList());

        log.error(JSON.toJSONString(cids));


        String token=getToken();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("token",token);
        String taskid=pushListMessage(title,context);
        String jsonParams="{\n" +
                "  \"audience\": {\n" +
                "    \"cid\": "+cids+
                "  },\n" +
                "  \"taskid\": \""+taskid+"\",\n" +
                "  \"is_async\": true\n" +
                "}";

        log.error(jsonParams);

        HttpEntity<String> httpEntity=new HttpEntity<String>(jsonParams,httpHeaders);

        JSONObject result = restTemplate.postForObject(BaseUrl+AppID+"/push/list/cid", httpEntity, JSONObject.class);

        log.error(result.toJSONString());

        return result.toJSONString();
    }


    /**
     *  批量推送消息体
     * @return
     * @throws Exception
     */
    public String pushListMessage(String title,String context) throws Exception{
        String token=getToken();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("token",token);
        // 在线消息主体
        Notification message=new Notification();
        message.setTitle(title);
        message.setBody(context);
        // 离线
        OffNotification offNotification=new OffNotification();
        offNotification.setTitle(title);
        offNotification.setBody(context);
        //PushMessage
        PushMessage pushMessage=new PushMessage();
        pushMessage.setNotification(message);
        // push_channel_android
        Ups ups=new Ups();
        ups.setNotification(offNotification);
        Android android=new Android();
        android.setUps(ups);
        // push_channel_ios
        Alert alert=new Alert();
        alert.setTitle(title);
        alert.setBody(context);
        Aps aps=new Aps();
        aps.setAlert(alert);
        Ios ios=new Ios();
        ios.setAps(aps);
        // push_channel_harmony
        HarmonyNotification harmonyNotification=new HarmonyNotification();
        harmonyNotification.setTitle(title);
        harmonyNotification.setBody(context);
        Harmony harmony=new Harmony();
        harmony.setNotification(harmonyNotification);
        // push_channel
        PushChannel pushChannel=new PushChannel();
        pushChannel.setAndroid(android);
        pushChannel.setIos(ios);
        pushChannel.setHarmony(harmony);

        // 主体
        PushNotification pushNotification=new PushNotification();
        pushNotification.setRequestId(String.valueOf(System.currentTimeMillis()));
        pushNotification.setPushMessage(pushMessage);
        pushNotification.setPushChannel(pushChannel);

        ObjectMapper objectMapper = new ObjectMapper();

        log.error(objectMapper.writeValueAsString(pushNotification));

        HttpEntity<String> httpEntity=new HttpEntity<String>(objectMapper.writeValueAsString(pushNotification),httpHeaders);

        JSONObject result = restTemplate.postForObject(BaseUrl+AppID+"/push/list/message", httpEntity, JSONObject.class);

        log.error(result.toJSONString());

        if (result.containsKey("code")&&0==result.getInteger("code")){
            return result.getJSONObject("data").getString("taskid");
        }

        return null;

    }



    @Data
    class TokenParams{
        private String sign;
        private String timestamp;
        private String appkey;
    }

    @Data
    class SingleCid{

        private String request_id;

        private String audience="all";

        private JSONObject settings = JSON.parseObject("\"settings\":{\"ttl\":7200000,\"strategy\":{\"default\":1,\"ios\":4,\"st\":4,\"hw\":1,\"ho\":1,\"hoshw\":1}}");




    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Data
    static class PushNotification{
        @JsonProperty("request_id")
        private String requestId;

        @JsonProperty("audience")
        private String audience="all";

        @JsonProperty("settings")
        private Settings settings=new Settings();

        @JsonProperty("push_message")
        private PushMessage pushMessage;

        @JsonProperty("push_channel")
        private PushChannel pushChannel;
    }

    @Data
    static class Settings{
        @JsonProperty("ttl")
        private long ttl;

        @JsonProperty("strategy")
        private Map<String, Integer> strategy = new HashMap<>();

        {
            ttl=7200000;
            strategy.put("default",1);
            strategy.put("ios",1);
            strategy.put("st",1);
            strategy.put("hw",1);
            strategy.put("ho",1);
            strategy.put("hoshw",1);
            strategy.put("vv",1);
            strategy.put("xm",1);
            strategy.put("op",1);
        }
    }

    @Data
    class PushMessage{
        @JsonProperty("notification")
        private Notification notification;
    }

    @Data
    static class OffNotification{
        @JsonProperty("title")
        private String title;

        @JsonProperty("body")
        private String body;

        @JsonProperty("click_type")
        private String clickType="startapp";

//        @JsonProperty("payload")
//        private String url="提醒";

//        @JsonProperty("channel_level")
//        private Integer channelLevel=4;
    }

    @Data
    static class Notification{
        @JsonProperty("title")
        private String title;

        @JsonProperty("body")
        private String body;

        @JsonProperty("click_type")
        private String clickType="payload";

        @JsonProperty("payload")
        private String url="提醒";

        @JsonProperty("channel_level")
        private Integer channelLevel=4;
    }

    @Data
    static class PushChannel{
        @JsonProperty("android")
        private Android android;

        @JsonProperty("ios")
        private Ios ios;

        @JsonProperty("harmony")
        private Harmony harmony;
    }

    @Data
    static class Android{
        @JsonProperty("ups")
        private Ups ups;
    }

    @Data
    static class Ups{
        @JsonProperty("notification")
        private OffNotification notification;

        @JsonProperty("options")
        private Map<String, Map<String, Object>> options = new TreeMap<>();

        {
            Map<String,Object> hw=new HashMap<>();
            hw.put("/message/android/category","WORK");
            options.put("HW",hw);
            Map<String,Object> xm=new HashMap<>();
            xm.put("/extra.channel_id","130717");
            options.put("XM",xm);
            Map<String,Object> op=new HashMap<>();
            op.put("/channel_id","130717");
            op.put("/category","TODO");
            op.put("/notify_level",16);
            options.put("OP",op);
            Map<String,Object> vv=new HashMap<>();
            vv.put("/category","TODO");
            options.put("VV",vv);
        }
    }

    @Data
    static class Ios{
        @JsonProperty("type")
        private String type="notify";

        @JsonProperty("payload")
        private String payload="自定义消息";

        @JsonProperty("aps")
        private Aps aps;

        @JsonProperty("auto_badge")
        private String autoBadge="+1";
    }

    @Data
    static class Aps{
        @JsonProperty("alert")
        private Alert alert;

        @JsonProperty("content-available")
        private int contentAvailable=0;

        @JsonProperty("sound")
        private String sound="default";
    }

    @Data
    static class Alert{
        @JsonProperty("title")
        private String title;

        @JsonProperty("body")
        private String body;

        @JsonProperty("click_type")
        private String clickType="payload";

        @JsonProperty("payload")
        private String payload="提醒";
    }

    @Data
    static class Harmony{
        @JsonProperty("notification")
        private HarmonyNotification notification;

        @JsonProperty("options")
        private Map<String, Map<String, Object>> options = new HashMap<>();

        {
            Map<String,Object> hw=new HashMap<>();
            hw.put("/message/android/category","WORK");
            options.put("HW",hw);
        }
    }

    @Data
    static class HarmonyNotification{
        @JsonProperty("title")
        private String title;

        @JsonProperty("body")
        private String body;

        @JsonProperty("click_type")
        private String clickType="startapp";

        @JsonProperty("category")
        private String category="WORK";
    }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        PushNotification person=new PushNotification();
        System.out.println(objectMapper.writeValueAsString(person));
    }
}
