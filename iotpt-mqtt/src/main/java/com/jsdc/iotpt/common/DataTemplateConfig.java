package com.jsdc.iotpt.common;

import com.alibaba.fastjson.JSONObject;
import com.jsdc.iotpt.model.ConfigSignalField;
import com.jsdc.iotpt.model.ConfigTopic;
import com.jsdc.iotpt.service.ConfigSignalFieldService;
import com.jsdc.iotpt.service.ConfigTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ClassName: DataTemplateConfig
 * Description:
 * date: 2023/6/3 8:52
 *
 * @author bn
 */
@Component
public class DataTemplateConfig {


    @Autowired
    private ConfigTopicService configTopicService;

    @Autowired
    private ConfigSignalFieldService configSignalFieldService;


    /**
     * 1.通过反射获取数据，报警，心跳model字段名
     * 2.获取mqtt 订阅主题
     */
    public void test(){
        // 获取系统内所有mqtt订阅主题
        ConfigTopic configTopic=new ConfigTopic();
        configTopic.setTopicType(1);
        List<ConfigTopic> configTopics=configTopicService.cacheTopic(configTopic);
        configTopics.forEach(x->{
            ConfigSignalField configSignalField=new ConfigSignalField();
            configSignalField.setModelId(x.getTransferId());
            // 系统字段对比表
            List<ConfigSignalField> configSignalFields=configSignalFieldService.getList(configSignalField);
            // 按类型分组 报警，心跳，数据
            Map<String,List<ConfigSignalField>> groups=configSignalFields.stream().collect(Collectors.groupingBy(ConfigSignalField::getType));

            for(Map.Entry<String, List<ConfigSignalField>> item:groups.entrySet()){
                // 数据模板拆分成网关，设备，信号
                if (item.getKey().equals("1")){
                    item.getValue().forEach(y->{
                        SystemKey systemKey=SystemKey.getSeemingKey(y.getSystemKey());
                        switch (systemKey){
                            case GATEWAY_INFO:
                                JSONObject wg=SysConfig.getInstance().getWgjx().get(x.getTopicName());
                                if (null == wg)
                                {
                                    wg=new JSONObject();
                                }
                                wg.put(y.getGatewayKey(),y.getSystemKey());
                                SysConfig.getInstance().getWgjx().put(x.getTopicName(),wg);
                                break;
                            case DEVICE_INFO:
                                JSONObject sb=SysConfig.getInstance().getSbjx().get(x.getTopicName());
                                if (null == sb)
                                {
                                    sb=new JSONObject();
                                }
                                sb.put(y.getGatewayKey(),y.getSystemKey());
                                SysConfig.getInstance().getSbjx().put(x.getTopicName(),sb);
                                break;
                            case SIGNAL_INFO:
                                JSONObject xh=SysConfig.getInstance().getXhjx().get(x.getTopicName());
                                if (null == xh)
                                {
                                    xh=new JSONObject();
                                }
                                xh.put(y.getGatewayKey(),y.getSystemKey());
                                SysConfig.getInstance().getXhjx().put(x.getTopicName(),xh);
                                break;
                            default:
                                break;
                        }
                    });

                }else if(item.getKey().equals("2")){// 告警
                    JSONObject jsonObject=new JSONObject();
                    item.getValue().forEach(y->{
                        jsonObject.put(y.getGatewayKey(),y.getSystemKey());
                    });
                    SysConfig.getInstance().getXtjx().put(x.getTopicName(),jsonObject);
                }else if (item.getKey().equals("3")){// 心跳
                    JSONObject jsonObject=new JSONObject();
                    item.getValue().forEach(y->{
                        jsonObject.put(y.getGatewayKey(),y.getSystemKey());
                    });
                    SysConfig.getInstance().getBjjx().put(x.getTopicName(),jsonObject);
                }

            }
        });
    }


    public static void main(String[] args) {
        new DataTemplateConfig().test();
    }




}
