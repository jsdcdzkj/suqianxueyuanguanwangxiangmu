package com.jsdc.iotpt.common;

import com.alibaba.fastjson.JSONObject;
import com.jsdc.iotpt.websocket.WsClientPool;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: SysConfig
 * Description:
 * date: 2023/5/26 15:52
 *
 * @author bn
 */
public class SysConfig {

    private static SysConfig instance = new SysConfig();

    private Map<String,Boolean> topicOnline=new HashMap<>();

    // topic数据类型 结构
    private Map<String,String> topicType=new HashMap<>();

    // mqtt转发kafka数据结构
    private Map<String,String> mqttKafka=new HashMap<>();


    // 网关信息解析模板
    private Map<String, JSONObject> wgjx=new HashMap<>();

    // 设备信息解析模板
    private Map<String,JSONObject> sbjx=new HashMap<>();

    // 信号解析模板
    private Map<String,JSONObject> xhjx=new HashMap<>();

    // 心跳解析
    private Map<String,JSONObject> xtjx=new HashMap<>();

    // 报警解析
    private Map<String,JSONObject> bjjx=new HashMap<>();

    private Map<Integer,String> linkClientId=new HashMap<>();



    /**
     * 返回单例对象
     */
    public static SysConfig getInstance()
    {
        return instance;
    }


    public Map<String, Boolean> getTopicOnline() {
        return topicOnline;
    }

    public Map<String, String> getTopicType() {
        return topicType;
    }

    public Map<String, String> getMqttKafka() {
        return mqttKafka;
    }

    public Map<String, JSONObject> getWgjx() {
        return wgjx;
    }

    public Map<String, JSONObject> getSbjx() {
        return sbjx;
    }

    public Map<String, JSONObject> getXhjx() {
        return xhjx;
    }

    public Map<String, JSONObject> getXtjx() {
        return xtjx;
    }

    public Map<String, JSONObject> getBjjx() {
        return bjjx;
    }

    public Map<Integer, String> getLinkClientId() {
        return linkClientId;
    }

}
