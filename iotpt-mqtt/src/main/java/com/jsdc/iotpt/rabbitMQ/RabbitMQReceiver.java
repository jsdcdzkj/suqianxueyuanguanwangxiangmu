package com.jsdc.iotpt.rabbitMQ;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.config.InfluxDBConfig;
import com.jsdc.iotpt.mapper.AlarmContentConfigMapper;
import com.jsdc.iotpt.service.AlarmRecordsService;
import com.jsdc.iotpt.service.influxDB.InfluxdbService;
import com.jsdc.iotpt.websocket.WsClientPool;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.EncodeException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClassName: RabbitMQReceiver
 * Description:
 * date: 2024/5/22 9:52
 *
 * @author bn
 */
@Service
public class RabbitMQReceiver {


    private static final ConcurrentHashMap<String, Object> sessionLocks = new ConcurrentHashMap<>();
    @Autowired
    private InfluxDBConfig influxDBConfig;
    private WsClientPool wsClientPool = WsClientPool.getInstance();
    @Autowired
    private InfluxdbService influxdbService;

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private AlarmContentConfigMapper alarmContentConfigMapper;
    @Autowired
    private AlarmRecordsService alarmRecordsService;



    //方法：接收消息
    @RabbitListener(queues = "dingchi_iot_data_event")
    public void mqEvent(byte[] msg) {
        JSONObject jsonObject=JSON.parseObject(new String(msg));

    }

    private SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Map<String,String> getTages(JSONObject jsonObject){
        Map<String,String> tagsMap=new HashMap<>();
        if (null!=jsonObject.getString("deviceCode"))
            tagsMap.put("deviceCode",jsonObject.getString("deviceCode"));
        if (null!=jsonObject.getString("deviceSn"))
            tagsMap.put("deviceSn",jsonObject.getString("deviceSn"));
        if (null!=jsonObject.getString("status"))
            tagsMap.put("status",jsonObject.getString("status"));
        if (null!=jsonObject.getString("uploadTime"))
            tagsMap.put("uploadTime",format.format(new Date(jsonObject.getLong("uploadTime"))));
        if (null!=jsonObject.getString("systemTime"))
            tagsMap.put("systemTime",format.format(new Date()));
        if (null!=jsonObject.getString("door"))
            tagsMap.put("door",jsonObject.getString("door"));
        if (null!=jsonObject.getString("doorLock"))
            tagsMap.put("doorLock",jsonObject.getString("doorLock"));
        if (null!=jsonObject.getString("passengerIn"))
            tagsMap.put("passengerIn",jsonObject.getString("passengerIn"));
        if (null!=jsonObject.getString("workMode"))
            tagsMap.put("workMode",jsonObject.getString("workMode"));
        if (null!=jsonObject.getString("level"))
            tagsMap.put("level",jsonObject.getString("level"));
        if (null!=jsonObject.getString("motion"))
            tagsMap.put("motion",jsonObject.getString("motion"));
        return tagsMap;
    }

    public Map<String, Object> getFields(JSONObject jsonObject){
        Map<String, Object> fieldsMap = new HashMap<>();

        fieldsMap.put("opencloseDoorNumVal", jsonObject.getFloat("openCloseDoorNum"));
        fieldsMap.put("cumulativeRunTimeVal", jsonObject.getFloat("cumulativeRunTime"));
        fieldsMap.put("cumulativeRunNumVal", jsonObject.getFloat("cumulativeRunNum"));
        fieldsMap.put("workModeVal", jsonObject.getFloat("workMode"));
        fieldsMap.put("mileageVal", jsonObject.getFloat("mileage"));
        fieldsMap.put("floorVal", jsonObject.getFloat("floor"));
        fieldsMap.put("levelVal", jsonObject.getFloat("level"));
        fieldsMap.put("motionVal", jsonObject.getFloat("motion"));
        fieldsMap.put("velocityVal", jsonObject.getFloat("velocity"));
        fieldsMap.put("passengerInVal", jsonObject.getFloat("passengerIn"));
        fieldsMap.put("doorVal", jsonObject.getFloat("door"));
        fieldsMap.put("statusVal", "online".equals(jsonObject.getString("status"))?0f:1f);
        Date date = new Date();
        long time = date.getTime() + 28800000;
        // 采集时间
        fieldsMap.put("dataTime", DateUtil.format(date, "yyyy-MM-dd HH:mm:ss"));

        return fieldsMap;
    }

    //方法：接收消息
    @RabbitListener(queues = "dingchi_iot_data_status")
    public void mqStatus(byte[] msg) throws EncodeException, IOException {
//        JSONObject jsonObject= JSON.parseObject(new String(msg));
//        long time = new Date().getTime() + 28800000;
//        if(!redisUtils.existsKey("lift"+jsonObject.get("deviceCode"))){
//            redisUtils.setBeanValue("lift"+jsonObject.get("deviceCode"),jsonObject,60);
//            influxdbService.insert("devicelift", time, getTages(jsonObject), getFields(jsonObject));
//        }
//        if(redisUtils.existsKey(jsonObject.get("deviceCode")+"")){
//            if (redisUtils.getKeyExpire(jsonObject.get("deviceCode")+"",TimeUnit.SECONDS)<5){
//                redisUtils.setBeanValue(jsonObject.get("deviceCode")+"",jsonObject);
//                redisUtils.expireKey(jsonObject.get("deviceCode")+"",15,TimeUnit.SECONDS);
//            }
//        }else {
//            redisUtils.setBeanValue(jsonObject.get("deviceCode")+"",jsonObject);
//            redisUtils.expireKey(jsonObject.get("deviceCode")+"",15,TimeUnit.SECONDS);
//        }
//        Map<String, Session> onlineClientMap = wsClientPool.getOnlineClientMap();
//        List<Object> list=new ArrayList<>();
//        Integer online=5;
//        for (String s : onlineClientMap.keySet()) {
//                if (s.contains("deviceLift")){
//                    List<DeviceLift> deviceLifts = liftMapper.selectList(new LambdaQueryWrapper<DeviceLift>().eq(DeviceLift::getIsDel, 0));
//                    for (DeviceLift deviceLift : deviceLifts) {
//                        JSONObject json= new JSONObject();
//                        if(redisUtils.existsKey(deviceLift.getDeviceCode())){
//                            json=(JSONObject) redisUtils.getBeanValue(deviceLift.getDeviceCode());
//                            json.put("isonline",true);
//                        }else {
//                            json.put("deviceCode",deviceLift.getDeviceCode());
//                            json.put("isonline",false);
//                        }
//                        list.add(json);
//                    }
//                    String jsonString = JSON.toJSONString(list);
////                    onlineClientMap.get(s).getAsyncRemote().sendText(jsonString);
//                    Object lock = sessionLocks.computeIfAbsent( onlineClientMap.get(s).getId(), k -> new Object());
//                    synchronized (lock) {
//                        try {
//                            onlineClientMap.get(s).getBasicRemote().sendText(jsonString);
//                        }catch (IOException e) {
//                            // 处理IO异常
//                        }
//                    }
//                }
//               if (s.equals("jscLift")){
//                List<DeviceLift> deviceLifts = liftMapper.selectList(new LambdaQueryWrapper<DeviceLift>().eq(DeviceLift::getIsDel, 0));
//                for (DeviceLift deviceLift : deviceLifts) {
//                    if(redisUtils.existsKey(deviceLift.getDeviceCode())){
//                        online--;
//                    }
//                }
////                onlineClientMap.get(s).getAsyncRemote().sendText(online+"");
//                   Object lock = sessionLocks.computeIfAbsent( onlineClientMap.get(s).getId(), k -> new Object());
//                   synchronized (lock) {
//                       try {
//                           onlineClientMap.get(s).getBasicRemote().sendText(online+"");
//                       }catch (IOException e) {
//                           // 处理IO异常
//                       }
//                   }
//               }
//            }
    }




}
