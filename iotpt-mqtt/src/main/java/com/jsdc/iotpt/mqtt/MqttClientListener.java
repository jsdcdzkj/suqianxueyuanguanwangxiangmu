package com.jsdc.iotpt.mqtt;

import com.jsdc.iotpt.common.SysConfig;
import com.jsdc.iotpt.model.ConfigLink;
import com.jsdc.iotpt.model.ConfigTopic;
import com.jsdc.iotpt.service.ConfigLinkService;
import com.jsdc.iotpt.service.ConfigTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * ClassName: MqttClientListener
 * Description: 项目启动 监听主题
 * date: 2023/6/30 10:52
 *
 * @author bn
 */
@Component
public class MqttClientListener implements ApplicationListener<ContextRefreshedEvent> {


    private volatile AtomicBoolean isInit=new AtomicBoolean(false);

    @Autowired
    private ConfigLinkService configLinkService;

    @Autowired
    private ConfigTopicService configTopicService;

    @Value("${sysMqtt.prefix}")
    private String prefix;

    /**
     *  系统启动时建立连接
     * @param contextRefreshedEvent
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        try {
            //防止重复触发
            if(!isInit.compareAndSet(false,true)) {
                return;
            }

            ConfigLink configLink=new ConfigLink();
            configLink.setConnectionStatus(2);
            configLink.setProtocolId("1");
            List<ConfigLink> configLinks=configLinkService.getList(configLink);
            for (ConfigLink item:configLinks) {

                // 随机建立clientId
//                if (item.getClientId().isEmpty()){
                String clientId =String.valueOf(System.currentTimeMillis());

                SysConfig.getInstance().getLinkClientId().put(item.getId(),clientId);
//                    item.setClientId(String.valueOf(clientId));


//                    configLinkService.updateById(item);
//                }

//                String clientId=prefix+item.getClientId();
//                String clientId=String.valueOf(System.currentTimeMillis());

                // 建立连接
                MqttClientConnect mqttClientConnect = new MqttClientConnect();
                mqttClientConnect.setMqttClientId(clientId);
                mqttClientConnect.setLinkId(item.getId());
                try{
                    mqttClientConnect.setMqttClient(
                            String.format("%s%s","tcp://",item.getServiceAddress()),
                            clientId,
                            item.getUsername(),
                            item.getPassword(),
                            item.getId(),
                            false,
                            new MqttClientCallback(clientId,item.getId()));
                }catch (Exception ee){
                    continue;
                }

                MqttClientConnect.mqttClients.put(clientId, mqttClientConnect);

                // 查询主题
                ConfigTopic configTopic=new ConfigTopic();
                configTopic.setTopicType(1);
                configTopic.setTopicStatus(1);
                configTopic.setLinkId(item.getId());
                List<ConfigTopic> configTopics=configTopicService.getList(configTopic);
                System.out.println(configTopics.toString());
                List<String> topicNames=configTopics.stream().map(x-> "$share/bn/"+x.getTopicName()).collect(Collectors.toList());
                List<Integer> topicIds=configTopics.stream().map(x-> x.getId()).collect(Collectors.toList());

                String[] msgtopic=topicNames.toArray(new String[topicNames.size()]);
                MqttClientConnect.mqttClients.get(clientId).sub(clientId,item.getId(),topicIds,msgtopic,3000);
//                for (ConfigTopic x:configTopics) {
//                    MqttClientConnect.mqttClients.get(configLink.getClientId()).unsub(x.getTopicName());
//                    MqttClientConnect.mqttClients.get(configLink.getClientId()).sub(x.getTopicName(),0);
//                }
            }


        } catch (Exception e) {

        }
    }

}
