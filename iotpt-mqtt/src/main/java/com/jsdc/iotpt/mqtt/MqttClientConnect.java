package com.jsdc.iotpt.mqtt;



import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClassName: MqttClientConnect
 * Description:
 * date: 2023/6/30 10:47
 *
 * @author bn
 */
@Component
public class MqttClientConnect {

    private MqttClient mqttClient;

    private MqttConnectOptions options;

    /**
     * 系统的mqtt客户端id
     */
    private String mqttClientId;

    /**
     * 连接id
      */
    private Integer linkId;

    /**
     * 客户端
     */
    public static ConcurrentHashMap<String, MqttClientConnect> mqttClients = new ConcurrentHashMap();

    /**
     *  当前连接
     */
    public ConcurrentHashMap<Integer,MqttMessageListener> clientTopics=new ConcurrentHashMap<>();

    /**
     * 客户端connect连接mqtt服务器
     *
     * @param userName     用户名
     * @param passWord     密码
     * @param cleanSession     会话
     * @param connectionTimeout     超时时间
     * @param keepAliveInterval     心跳
     * @param automaticReconnect     自动重连
     * @param mqttCallback 回调函数
     **/
    public void setMqttClient(String host, String clientId, String userName, String passWord,Integer linkId, boolean cleanSession,  MqttCallback mqttCallback) throws MqttException {
        options = mqttConnectOptions(host, clientId, userName, passWord, cleanSession);
        if (mqttCallback == null) {
            mqttClient.setCallback(new MqttClientCallback(mqttClientId,linkId));
        } else {
            mqttClient.setCallback(mqttCallback);
        }
        mqttClient.connect(options);
    }

    /**
     * MQTT连接参数设置
     */
    private MqttConnectOptions mqttConnectOptions(String host, String clientId, String userName, String passWord, boolean cleanSession) throws MqttException {
        mqttClient = new MqttClient(host, clientId, new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        if (null!=userName){
            options.setUserName(userName);
        }
        if (null!=passWord){
            options.setPassword(passWord.toCharArray());
        }

        options.setConnectionTimeout(30);///默认：30
        options.setAutomaticReconnect(false);//默认：false
        options.setCleanSession(true);//默认：true
        options.setKeepAliveInterval(60);//默认：60
        return options;
    }

    /**
     * 关闭MQTT连接
     */
    public void close() throws MqttException {
        if (mqttClient.isConnected()){
            mqttClient.disconnect();
        }
        mqttClient.close();

    }

    /**
     * 向某个主题发布消息 默认qos：1
     *
     * @param topic:发布的主题
     * @param msg：发布的消息
     */
    public void pub(String topic, String msg) throws MqttException {
        MqttMessage mqttMessage = new MqttMessage();
        //mqttMessage.setQos(2);
        mqttMessage.setPayload(msg.getBytes());
        MqttTopic mqttTopic = mqttClient.getTopic(topic);
        MqttDeliveryToken token = mqttTopic.publish(mqttMessage);
        token.waitForCompletion();
    }

    /**
     * 向某个主题发布消息
     *
     * @param topic: 发布的主题
     * @param msg:   发布的消息
     * @param qos:   消息质量    Qos：0、1、2
     */
    public void pub(String topic, String msg, int qos) throws MqttException {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(qos);
        mqttMessage.setPayload(msg.getBytes());
        MqttTopic mqttTopic = mqttClient.getTopic(topic);
        MqttDeliveryToken token = mqttTopic.publish(mqttMessage);
        token.waitForCompletion();
    }

    /**
     * 订阅多个主题 ，此方法默认的的Qos等级为：1
     *
     * @param topic 主题
     */
    public void sub(String clientId,Integer linkId,List<Integer> topicIds, String[] topic, Integer timeout) throws MqttException {
        if (topic.length==0){
            return;
        }
        int[] qos = new int[topic.length];
        MqttMessageListener[] mqttMessageListeners=new MqttMessageListener[topic.length];
        for (int i = 0; i < topic.length; i++) {
            qos[i] = 0;
            MqttMessageListener mqttMessageListener=new MqttMessageListener(clientId,linkId);
            mqttMessageListener.setTopicId(topicIds.get(i));
            mqttMessageListeners[i]=mqttMessageListener;
        }
        mqttClient.subscribe(topic,qos,mqttMessageListeners);
        for (int i = 0; i < topic.length; i++) {
            MqttClientConnect.mqttClients.get(clientId).getClientTopics().put(topicIds.get(i),mqttMessageListeners[i]);
        }
        mqttClient.setTimeToWait(timeout);




    }

    /**
     * 订阅某一个主题，可携带Qos
     *
     * @param topic 所要订阅的主题
     * @param qos   消息质量：0、1、2
     */
    public void sub(String topic, int qos) throws MqttException {
        mqttClient.subscribe(topic, qos);
    }

    /**
     * 订阅主题，每个主题处理自己的消息
     * @param linkId
     * @param topicId
     * @param topic
     * @param qos
     * @throws MqttException
     */
    public void sub(String clientId,Integer linkId,Integer topicId,String topic,int qos) throws MqttException {
        MqttMessageListener mqttMessageListener=new MqttMessageListener(clientId,linkId);
        mqttMessageListener.setTopicId(topicId);
        mqttClient.subscribe(topic,qos,mqttMessageListener);
        MqttClientConnect.mqttClients.get(clientId).getClientTopics().put(topicId,mqttMessageListener);
    }

    /**
     *  移除订阅的某个主题
     * @param topic
     * @throws MqttException
     */
    public void unsub(String clientId,Integer topicId,String topic) throws MqttException {
        mqttClient.unsubscribe(topic);
        MqttClientConnect.mqttClients.get(clientId).getClientTopics().remove(topicId);
    }

    /**
     *  批量移除主题
     * @param topic
     * @throws MqttException
     */
    public void unsubs(String[] topic) throws MqttException {
        mqttClient.unsubscribe(topic);
    }

    public String getMqttClientId() {
        return mqttClientId;
    }

    public void setMqttClientId(String mqttClientId) {
        this.mqttClientId = mqttClientId;
    }

    public MqttClient getMqttClient() {
        return mqttClient;
    }

    public Integer getLinkId() {
        return linkId;
    }

    public void setLinkId(Integer linkId) {
        this.linkId = linkId;
    }

    public MqttConnectOptions getOptions() {
        return options;
    }

    public void setOptions(MqttConnectOptions options) {
        this.options = options;
    }

    public ConcurrentHashMap<Integer, MqttMessageListener> getClientTopics() {
        return clientTopics;
    }

}
