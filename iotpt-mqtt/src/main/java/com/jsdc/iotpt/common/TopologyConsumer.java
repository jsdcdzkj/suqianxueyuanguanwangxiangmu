package com.jsdc.iotpt.common;

import com.alibaba.fastjson.JSONObject;
import com.jsdc.iotpt.model.ConfigLink;
import com.jsdc.iotpt.model.ConfigTopic;
import com.jsdc.iotpt.mqtt.MqttClientCallback;
import com.jsdc.iotpt.mqtt.MqttClientConnect;
import com.jsdc.iotpt.service.ConfigLinkService;
import com.jsdc.iotpt.service.ConfigTopicService;
import com.jsdc.iotpt.vo.ResultInfo;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * ClassName: TopologyConsumer
 * Description: 守护线程
 * date: 2023/7/13 9:00
 *
 * @author bn
 */
//@Component
public class TopologyConsumer  implements InitializingBean, DisposableBean, Runnable {


    private volatile boolean condition = true;

    @Autowired
    public ConfigLinkService configLinkService;

    @Autowired
    public ConfigTopicService configTopicService;

    @Value("${sysMqtt.prefix}")
    private String prefix;


    Logger logger = LoggerFactory.getLogger(MqttClientCallback.class);

    @Override
    public void run() {
        while (condition) {
            try {
                //守护线程，半小时执行一次，当mqtt连接因系统异常导致了断线，通过进程进行唤醒
                Thread.sleep(60000);

                MqttClientConnect.mqttClients.forEach((key,item)->{
                    String msg="1";
                    MqttMessage mqttMessage = new MqttMessage();
                    mqttMessage.setQos(0);
                    mqttMessage.setPayload(msg.getBytes());
                    MqttTopic mqttTopic = item.getMqttClient().getTopic("heart");
                    MqttDeliveryToken token = null;
                    try {
                        token = mqttTopic.publish(mqttMessage);
                        token.waitForCompletion();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }

//                    try {
//                        ConfigLink configLink= configLinkService.getById(item.getLinkId());
//                        OkHttpClient client = new OkHttpClient();
//
//                        String url="http://"+configLink.getServiceAddress().
//                                substring(0,configLink.getServiceAddress().lastIndexOf(":"));
//
//                        Request request = new Request.Builder()
//                                .url(url+":18083/api/v5/clients/"+prefix+item.getMqttClientId())
//                                .header("Content-Type", "application/json")
//                                .header("Authorization", Credentials.basic(configLink.getApiKey(), configLink.getSecretKey()))
//                                .build();
//
//                        Response response = client.newCall(request).execute();
//                        JSONObject fromObject = JSONObject.parseObject(response.body().string());
//
//                        // 接口分404情况与200 具体参照api文档，返回结果包含了code的情况，说明404
//                        //https://www.emqx.io/docs/zh/v5.0/admin/api-docs.html#tag/Clients/paths/~1clients~1%7Bclientid%7D/delete
//                        if (fromObject.containsKey("code")&&fromObject.getString("code").equals("CLIENTID_NOT_FOUND")){
////                            configLink.setClientId(item.getMqttClientId());
////                            configLinkService.updateById(configLink);
//
//                            MqttClientConnect mqttClientConnect = new MqttClientConnect();
//                            mqttClientConnect.setMqttClientId(prefix+configLink.getClientId());
//                            mqttClientConnect.setLinkId(configLink.getId());
//
//                            mqttClientConnect.setMqttClient(
//                                    String.format("%s%s","tcp://",configLink.getServiceAddress()),
//                                    prefix+configLink.getClientId(),
//                                    configLink.getUsername(),
//                                    configLink.getPassword(),
//                                    configLink.getId(),
//                                    false,
//                                    new MqttClientCallback(configLink.getClientId(),configLink.getId()));
//
//                            MqttClientConnect.mqttClients.put(prefix+configLink.getClientId(), mqttClientConnect);
//
//                            ConfigTopic configTopic=new ConfigTopic();
//                            configTopic.setTopicType(1);
//                            configTopic.setTopicStatus(1);
//                            configTopic.setLinkId(configLink.getId());
//                            List<ConfigTopic> configTopics=configTopicService.getList(configTopic);
//
//                            List<String> topicNames=configTopics.stream().map(x-> "$queue/"+x.getTopicName()).collect(Collectors.toList());
//                            List<Integer> topicIds=configTopics.stream().map(x-> x.getId()).collect(Collectors.toList());
//
//                            String[] msgtopic=topicNames.toArray(new String[topicNames.size()]);
//                            mqttClientConnect.sub(prefix+configLink.getClientId(),configLink.getId(),topicIds,msgtopic,3000);
//                        }
//
//
//                    } catch (Exception e) {
//                        logger.info("msg",e);
//                    }


                });
            } catch (Exception e) {
                continue;
            }
        }
    }

    /**
     * condition字段自定义的 随便取一个就行 主要是给while条件判断的依据 服务关闭前会调用destroy方法
     *
     */
    @Override
    public void destroy() throws Exception {
        condition = false;
    }


    /**
     * afterPropertiesSet() 方法在该Bean被实例化后属性注入完成之后执行里面的代码
     * 这里是启动该线程(也就是前面说的守护线程)
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        new Thread(this).start();
    }

    public static void main(String[] args) {
        String url="192.168.0.191:1883";
        System.out.println(url.substring(0,url.lastIndexOf(":")));
    }

}
