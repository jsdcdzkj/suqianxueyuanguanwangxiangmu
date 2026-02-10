package com.jsdc.iotpt.common.tcpClient;

import io.netty.channel.Channel;
import lombok.Data;

@Data
public class BootNettyClientChannel {

    //	连接客户端唯一的code
    private String code;

    //	客户端最新发送的消息内容
    private String last_data;

    private transient volatile Channel channel;
}
