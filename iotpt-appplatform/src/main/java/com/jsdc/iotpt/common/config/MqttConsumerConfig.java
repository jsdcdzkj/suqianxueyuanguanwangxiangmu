package com.jsdc.iotpt.common.config;//package com.jsdc.iotpt.common.config;
//
//import com.jsdc.iotpt.model.ConfigTopic;
//import com.jsdc.iotpt.service.ConfigTopicService;
//import org.eclipse.paho.client.mqttv3.*;
//import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.annotation.PostConstruct;
//import java.io.ByteArrayOutputStream;
//import java.io.ObjectOutputStream;
//import java.io.Serializable;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * ClassName: MqttProviderConfig
// * Description:
// * date: 2023/5/10 10:10
// *
// * @author bn
// */
////@Configuration
//public class MqttConsumerConfig {
//
//
//    @Value("${spring.mqtt.username}")
//    private String username;
//
//    @Value("${spring.mqtt.password}")
//    private String password;
//
//    @Value("${spring.mqtt.url}")
//    private String hostUrl;
//
//    @Value("${spring.mqtt.consumerclientid}")
//    private String clientId;
//
//    @Autowired
//    private ConfigTopicService configTopicService;
//
//
//    @Bean
//    public ConfigTopicService getConfigTopicService(){
//        return configTopicService;
//    }
//
//    /**
//     * 客户端对象
//     */
//    private MqttClient client;
//
//    @PostConstruct
//    public void init(){
//        connect();
//    }
//
//
//    /**
//     * 客户端连接服务端
//     */
//    public void connect(){
//        try{
//            client = new MqttClient(hostUrl, clientId, new MemoryPersistence());
//            MqttConnectOptions options = new MqttConnectOptions();
//            options.setCleanSession(false);
//            options.setUserName(username);
//            options.setPassword(password.toCharArray());
//            options.setCleanSession(false);   //是否清除session
//            // 设置超时时间
//            options.setConnectionTimeout(3000);
//            // 设置会话心跳时间
//            options.setKeepAliveInterval(20);
//
//            ConfigTopic configTopic =new ConfigTopic();
//            configTopic.setTopicType(1);
//            List<ConfigTopic> configTopics=configTopicService.getList(configTopic);
//
//            List<String> topicNames=configTopics.stream().map(item-> item.getTopicName()).collect(Collectors.toList());
//
//            String[] msgtopic=topicNames.toArray(new String[topicNames.size()]);
//
//
//
//
//            //订阅消息
//            int[] qos = new int[configTopics.size()];
//            for (int i = 0; i < configTopics.size(); i++) {
//                qos[i] = 0;
//            }
//
//            client.setCallback(new MsgCallback(client, options, msgtopic, qos){});
//            client.connect(options);
//            client.subscribe(msgtopic, qos);
//
//            System.out.println("MQTT连接成功:" + clientId + ":" + client);
//        } catch(Exception e){
//            System.out.println("MQTT连接异常：" + e);
//        }
//    }
//
//    //实现MqttCallback，内部函数可回调
//    class MsgCallback implements MqttCallback {
//        private MqttClient client;
//        private MqttConnectOptions options;
//        private String[] topic;
//        private int[] qos;
//
//        public MsgCallback() {
//        }
//
//        public MsgCallback(MqttClient client, MqttConnectOptions options, String[] topic, int[] qos) {
//            this.client = client;
//            this.options = options;
//            this.topic = topic;
//            this.qos = qos;
//        }
//        //连接失败回调该函数
//        @Override
//        public void connectionLost(Throwable throwable) {
//            System.out.println("MQTT连接断开，发起重连");
//            while (true) {
//                try {
//                    Thread.sleep(1000);
//                    connect();
//                    client.connect(options);
//                    //订阅消息
//                    client.subscribe(topic, qos);
//                    System.out.println("MQTT重新连接成功:" + client);
//                    break;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    continue;
//                }
//            }
//        }
//        //收到消息回调该函数
//        @Override
//        public void messageArrived(String s, MqttMessage message) throws Exception {
//            System.out.println();
//            //订阅消息字符
//            String msg = new String(message.getPayload());
//            byte[] bymsg = getBytesFromObject(msg);
////            System.out.println("topic:" + topic);
////            queue.put(msg);
//
//            System.out.println(msg);
//        }
//        //对象转化为字节码
//        public byte[] getBytesFromObject(Serializable obj) throws Exception {
//            if (obj == null) {
//                return null;
//            }
//            ByteArrayOutputStream bo = new ByteArrayOutputStream();
//            ObjectOutputStream oo = new ObjectOutputStream(bo);
//            oo.writeObject(obj);
//            return bo.toByteArray();
//        }
//
//        @Override
//        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
//
//        }
//    }
//
//
//
//}
