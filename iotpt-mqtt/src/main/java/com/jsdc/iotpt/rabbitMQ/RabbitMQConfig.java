package com.jsdc.iotpt.rabbitMQ;

import org.springframework.context.annotation.Configuration;

/**
 * ClassName: RabbitMQConfig
 * Description:
 * date: 2024/5/22 10:01
 *
 * @author bn
 */
@Configuration
public class RabbitMQConfig {

//    //定义队列名
//    private static final String QUEUE = "dingchi_iot_data_event";
//
//    //创建队列
//    /**
//     * 1. 配置队列
//     * 2. 队列名为 queue
//     * 3. true 表示: 持久化 (不填，默认为true,默认持久化)
//     * durable： 队列是否持久化。 队列默认是存放到内存中的，rabbitmq 重启则丢失，
//     * 若想重启之后还存在则队列要持久化，
//     * 保存到 Erlang 自带的 Mnesia 数据库中，当 rabbitmq 重启之后会读取该数据库
//     * @return
//     */
//    @Bean
//    public Queue queue(){
//        return new Queue(QUEUE,true);
//    }

}
