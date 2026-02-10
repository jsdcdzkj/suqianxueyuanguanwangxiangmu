//package com.jsdc.iotpt.service.Kafka;
//
//import com.alibaba.fastjson.JSON;
//import com.jsdc.iotpt.mapper.AlertSheetMapper;
//import com.jsdc.iotpt.mapper.DataSheetMapper;
//import com.jsdc.iotpt.mapper.HeartSheetMapper;
//import com.jsdc.iotpt.model.AlertSheet;
//import com.jsdc.iotpt.model.DataSheet;
//import com.jsdc.iotpt.service.mqtt.AlertSheetService;
//import com.jsdc.iotpt.service.mqtt.DataSheetService;
//import com.jsdc.iotpt.service.mqtt.HeartSheetService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//import org.w3c.dom.stylesheets.LinkStyle;
//
//import java.util.List;
//
///**
// * @projectName: IOT
// * @className: KafkaConsumer
// * @author: wp
// * @description: kafka消费者
// * @date: 2023/5/11 9:40
// */
//@Service
//@Slf4j
//public class KafkaConsumer {
//    Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    @Autowired
//    private AlertSheetMapper alertSheetMapper;
//
//    @Autowired
//    private DataSheetMapper dataSheetMapper;
//
//
//    @Autowired
//    private HeartSheetMapper heartSheetMapper;
//
//    /**
//     *  数据
//     * @param record
//     */
//    @KafkaListener(topics = {"DATA_TOPIC"})
//    public void receiveMsg(ConsumerRecord<String, Object> record){
//        //topic
//        String topic = record.topic();
//        //消息内容
//        String msg = String.valueOf(record.value());
//
//        List<DataSheet> dataSheets= JSON.parseArray(msg,DataSheet.class);
//
//        dataSheets.forEach(x->{
//            dataSheetMapper.insert(x);
//        });
//
//        logger.info("kafka接收消息： topic: "+record.topic()+"-消息内容: "+record.value());
//    }
//
//
//    @KafkaListener(topics = {"ALERT_TOPIC"})
//    public void alertMsg(ConsumerRecord<String, Object> record){
//        //topic
//        String topic = record.topic();
//        //消息内容
//        String msg = String.valueOf(record.value());
//
//        List<AlertSheet> alertSheets=JSON.parseArray(msg,AlertSheet.class);
//
//        alertSheets.forEach(x->{
//            alertSheetMapper.insert(x);
//        });
//
//        logger.info("kafka接收消息： topic: "+record.topic()+"-消息内容: "+record.value());
//    }
//
//    @KafkaListener(topics = {"HEART_TOPIC"})
//    public void heartMsg(ConsumerRecord<String, Object> record){
//        //topic
//        String topic = record.topic();
//        //消息内容
//        String msg = String.valueOf(record.value());
//
//
//
//        logger.info("kafka接收消息： topic: "+record.topic()+"-消息内容: "+record.value());
//    }
//
//
//}
