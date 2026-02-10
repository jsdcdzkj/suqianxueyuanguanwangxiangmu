//package com.jsdc.iotpt.mqtt;
//
//import com.jsdc.iotpt.kafka.KafkaProducer;
//import com.jsdc.iotpt.model.ConfigTopic;
//import com.jsdc.iotpt.service.ConfigTopicService;
//import com.jsdc.iotpt.service.mqtt.AlertSheetService;
//import com.jsdc.iotpt.service.mqtt.DataSheetService;
//import com.jsdc.iotpt.service.mqtt.HeartSheetService;
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.integration.annotation.ServiceActivator;
//import org.springframework.integration.channel.DirectChannel;
//import org.springframework.integration.core.MessageProducer;
//import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
//import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
//import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
//import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
//import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.MessageHandler;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * ClassName: MqttConfig
// * Description:
// * date: 2023/5/10 16:17
// *
// * @author bn
// */
//@Configuration
//@ConditionalOnProperty(value = "spring.mqtt.enable", havingValue = "true")
//public class MqttConfig {
//
//
//    @Value("${spring.mqtt.username}")
//    private String username;
//
//
//    @Value("${spring.mqtt.password}")
//    private String password;
//
//    @Value("${spring.mqtt.url}")
//    private String hostUrl;
//
//    @Value("${spring.mqtt.producerclientid}")
//    private String producerClientId;
//
//    @Value("${spring.mqtt.producertopic}")
//    private String producerTopic;
//
//    //生产者和消费者是单独连接服务器会使用到一个clientid（客户端id），如果是同一个clientid的话会出现Lost connection: 已断开连接; retrying...
//    @Value("${spring.mqtt.consumerclientid}")
//    private String consumerClientId;
//
////    @Value("${spring.mqtt.consumertopic}")
////    private String[] consumerTopic;
//
//
//    @Value("${spring.mqtt.timeout}")
//    private int timeout;   //连接超时
//
//    @Value("${spring.mqtt.keepalive}")
//    private int keepalive;  //连接超时
//
//    //入站通道名（消费者）订阅的bean名称
//    public static final String CHANNEL_NAME_IN = "mqttInboundChannel";
//    //出站通道名（生产者）发布的bean名称
//    public static final String CHANNEL_NAME_OUT = "mqttOutboundChannel";
//
//    @Autowired
//    private ConfigTopicService configTopicService;
//
//    @Autowired
//    private KafkaProducer kafkaProducer;
//
//    @Autowired
//    private AlertSheetService alertSheetService;
//
//    @Autowired
//    private DataSheetService dataSheetService;
//
//    @Autowired
//    private HeartSheetService heartSheetService;
//
//    @Bean
//    public HeartSheetService getHeartSheetService(){
//        return heartSheetService;
//    }
//
//    @Bean
//    public DataSheetService getDataSheetService(){
//        return dataSheetService;
//    }
//
//    @Bean
//    public AlertSheetService getAlertSheetService(){
//        return alertSheetService;
//    }
//
//    @Bean
//    public KafkaProducer getMqttConsumer(){
//        return kafkaProducer;
//    }
//
//    @Bean
//    public ConfigTopicService getConfigTopicService(){
//        return configTopicService;
//    }
//
//    /**
//     * MQTT连接器选项
//     *
//     * @return {@link MqttConnectOptions}
//     */
//    @Bean
//    public MqttConnectOptions getMqttConnectOptions() {
//        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
//        mqttConnectOptions.setCleanSession(false);
//        mqttConnectOptions.setConnectionTimeout(30);
//        mqttConnectOptions.setKeepAliveInterval(60);
//        mqttConnectOptions.setAutomaticReconnect(true);
//        mqttConnectOptions.setUserName(username);
//        mqttConnectOptions.setPassword(password.toCharArray());
//        mqttConnectOptions.setServerURIs(new String[]{hostUrl});
//
//        return mqttConnectOptions;
//    }
//
//
//    /**
//     * MQTT客户端
//     *
//     * @return {@link MqttPahoClientFactory}
//     */
//    @Bean
//    public MqttPahoClientFactory mqttClientFactory() {
//        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
//        factory.setConnectionOptions(getMqttConnectOptions());
//        return factory;
//    }
//
//
//    /**
//     * MQTT信息通道（生产者）
//     *
//     * @return {@link MessageChannel}
//     */
//    @Bean(name = CHANNEL_NAME_OUT)
//    public MessageChannel mqttOutboundChannel() {
//        return new DirectChannel();
//    }
//
//
//    /**
//     * MQTT消息处理器（生产者）
//     *
//     * @return
//     */
//    @Bean
//    @ServiceActivator(inputChannel = CHANNEL_NAME_OUT)
//    public MessageHandler mqttOutbound() {
//        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(producerClientId, mqttClientFactory());
//        messageHandler.setAsync(true);
//        messageHandler.setDefaultTopic(producerTopic);
//        return messageHandler;
//    }
//
//
//    /**
//     * MQTT信息通道（消费者）
//     *
//     * @return {@link MessageChannel}
//     */
//    @Bean(name = CHANNEL_NAME_IN)
//    public MessageChannel mqttInboundChannel() {
//        return new DirectChannel();
//    }
//
//
//    /**
//     * MQTT消息订阅绑定（消费者）
//     *
//     * @return {@link MessageProducer}
//     */
//    @Bean
//    public MessageProducer inbound() {
//
//        ConfigTopic configTopic =new ConfigTopic();
//        configTopic.setTopicType(1);
//        List<ConfigTopic> configTopics=configTopicService.getList(configTopic);
//
//        List<String> topicNames=configTopics.stream().map(item-> item.getTopicName()).collect(Collectors.toList());
//
//        String[] msgtopic=topicNames.toArray(new String[topicNames.size()]);
//
//
//        // 可以同时消费（订阅）多个Topic
//        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(consumerClientId, mqttClientFactory(), msgtopic);
//        adapter.setCompletionTimeout(timeout);
//        adapter.setConverter(new DefaultPahoMessageConverter());
//        adapter.setQos(0);
//        // 设置订阅通道
//        adapter.setOutputChannel(mqttInboundChannel());
//        return adapter;
//    }
//
//    /**
//     * MQTT消息处理器（消费者）
//     *
//     * @return {@link MessageHandler}
//     */
//    @Bean
//    @ServiceActivator(inputChannel = CHANNEL_NAME_IN)
//    public MessageHandler handler() {
//        //方法1
////        return new MessageHandler() {
////            @Override
////            public void handleMessage(Message<?> message) throws MessagingException {
////                String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC).toString();
////                String msg = message.getPayload().toString();
////                logger.info("接收到订阅消息:
////        topic:" + topic + "
////        message:" + msg);
////            }
////        };
//        //方法2
//        return new MqttConsumer();
//    }
//
//
//    //如果我要配置多个client，只要配置多个通道即可
//    //通道2
////    @Bean
////    public MessageChannel mqttInputChannelTwo() {
////        return new DirectChannel();
////    }
////    //配置client2，监听的topic:hell2,hello3
////    @Bean
////    public MessageProducer inbound1() {
////        MqttPahoMessageDrivenChannelAdapter adapter =
////                new MqttPahoMessageDrivenChannelAdapter(clientId, mqttClientFactory(),
////                        "hello2","hello3");
////        adapter.setCompletionTimeout(timeout);
////        adapter.setConverter(new DefaultPahoMessageConverter());
////        adapter.setQos(1);
////        adapter.setOutputChannel(mqttInputChannelTwo());
////        return adapter;
////    }
////
////    //通过通道2获取数据
////    @Bean
////    @ServiceActivator(inputChannel = "mqttInputChannelTwo")
////    public MessageHandler handlerTwo() {
////        return new MqttConsumer();
////    }
//
//
//
//
//}
