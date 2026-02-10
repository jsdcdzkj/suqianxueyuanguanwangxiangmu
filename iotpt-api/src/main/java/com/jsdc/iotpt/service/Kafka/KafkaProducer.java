//package com.jsdc.iotpt.service.Kafka;
//
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
///**
// * @projectName: IOT
// * @className: KafkaProducer
// * @author: wp
// * @description: kafka生产者
// * @date: 2023/5/11 9:58
// */
//@Service
//@Slf4j
//public class KafkaProducer {
//
//    Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    @Autowired
//    KafkaTemplate<String, Object> kafkaTemplate;
//
//    //消息推送至指定topic
//    public void sendMsg(String topic, String msg){
//        logger.info("推送主题：" + topic);
//        logger.info("消息内容：" + msg);
//        kafkaTemplate.send(topic, msg);
//    }
//}
