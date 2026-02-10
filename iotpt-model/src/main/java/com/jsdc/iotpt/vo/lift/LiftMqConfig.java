package com.jsdc.iotpt.vo.lift;


import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.List;

@Data
public class LiftMqConfig extends Model<LiftMqConfig> {

    private String deviceId;//设备id

    private String gatewayId;//网关ID

    private String deviceSecret;//设备密钥

    private String userName;//用户名

    private String passwd;//设备密码（设备ID+设备密码可用于设备登录）

    private String host="172.168.80.3";//平台连接地址

    private String port="1883";//端口

    private List<TopicInfo> publishTopic;//发布Topic列表（用于上传数据）

    private List<TopicInfo> subsrcibeTopic;//订阅Topic列表（用于订阅数据）

    private String clientId;//客户端ID（由平台生成，用于MQTT连接）

    private String certficateFile;//证书文件（BASE64加密）

    private String protocol="MQTT";//连接协议[MQTT,MQTTS,HTTP,HTTPS]


}
