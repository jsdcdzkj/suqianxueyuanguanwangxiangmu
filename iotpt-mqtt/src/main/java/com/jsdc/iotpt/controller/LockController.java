package com.jsdc.iotpt.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.common.minio.service.MinioTemplate;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.entity.*;
import com.jsdc.iotpt.mapper.GroundLocksImgMapper;
import com.jsdc.iotpt.model.GroundLocksImg;
import com.jsdc.iotpt.mqtt.MqttClientCallback;
import com.jsdc.iotpt.mqtt.MqttClientConnect;
import com.jsdc.iotpt.util.Base64Utils;
import com.jsdc.iotpt.util.uuid.IdUtils;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;


@Slf4j
@RestController
@RequestMapping("/")
@Api(tags = "数据转换模板管理")
public class LockController {

    @Autowired
    private GroundLocksImgMapper groundLocksImgMapper;

    @Autowired
    private MinioTemplate minioTemplate;


    public static LinkedHashMap<String, Object> convertMapToLinkedHashMap(Map<String, Object> map) {
        LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            linkedHashMap.put(entry.getKey(), entry.getValue());
        }
        return linkedHashMap;
    }

    public static <T> T convertLinkedHashMapToObject(LinkedHashMap<String, Object> linkedHashMap, Class<T> clazz) {
        try {
            T obj = clazz.getDeclaredConstructor().newInstance();
            for (Map.Entry<String, Object> entry : linkedHashMap.entrySet()) {
                String propertyName = entry.getKey();
                Object propertyValue = entry.getValue();

                clazz.getDeclaredField(propertyName).set(obj, propertyValue);
            }
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T convertMapToObject(Map<String, Object> map, Class<T> clazz) {
        try {
            T obj = clazz.getDeclaredConstructor().newInstance();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String propertyName = entry.getKey();
                Object propertyValue = entry.getValue();

                clazz.getDeclaredField(propertyName).set(obj, propertyValue);
            }
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 心跳地址
     */
    @PostMapping("devicemanagement/php/receivedeviceinfo.php")
    @ApiOperation("心跳地址")
    public void receivedeviceinfo(@RequestBody Map<String, Object> param) {
        log.info("心跳地址start-param:{}", JSONObject.toJSONString(param));
        // 如果是1，车辆要入场，再用车牌号跟数据库对比，如果是我们公司的车，则发送落锁指令
        // sn 设备号
        String sn = MapUtils.getString(param, "sn");
        // timestamp 时间戳
        String timestamp = System.currentTimeMillis() + "";
        //1. 按照“key=value”格式，使⽤“&”符号，按顺序拼接为字符串，格式如下： sn=xxx&timestamp=xxx&msg_id=xxx&msg_type=xxx
        String snTemp = "sn=" + sn + "&timestamp=" + timestamp + "&msg_id=GO2021070112000101&msg_type=GroundlockOption";
        //2. 将拼接字符串进⾏32位MD5计算，得到⼤写的数字签名
        String sign = DigestUtils.md5DigestAsHex(snTemp.getBytes()).toUpperCase();
        JSONObject msgObj = new JSONObject();
        msgObj.put("sign", sign);
        msgObj.put("sn", sn);
        // 得到当前日期字符串 如:  "2021-07-01 12:00:01"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        msgObj.put("timestamp", sdf.format(new Date()));
        msgObj.put("msg_id", "GS2021070112000101");
        msgObj.put("msg_type", "GroundlockStatus");

        msgObj.put("msg_data", new JSONObject());
        String sendMsg = msgObj.toJSONString();
        System.out.println(sendMsg);
        String topc = "/pushzs/75a971b6-25878aa7";
        try {
            for (Map.Entry<String, MqttClientConnect> item : MqttClientConnect.mqttClients.entrySet()) {
                item.getValue().pub(topc, sendMsg);
            }
        } catch (MqttException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 推送业务识别结果
     */
    @PostMapping("zhenshi/upload.action")
    @ApiOperation("推送业务识别结果")
    public ResultInfo upload(@RequestBody RequestEntity param) {
        log.info("start 推送业务识别结果 param:{}", JSONObject.toJSONString(param));
        // 判断是否有图片
        List<FetureImg> fetureImgs = param.getFeture_img();
        if (CollectionUtils.isEmpty(fetureImgs)) {
            log.info("图片为空");
            return ResultInfo.success();
        }
        // 判断是否有设备
        DeviceInfo deviceInfo = param.getDevice_info();
        if (null == deviceInfo) {
            log.info("设备为空");
            return ResultInfo.success();
        }
        Product_h product = param.getProduct_h();
        if (null == product) {
            return ResultInfo.success();
        }
        // 判断是否有车牌
        Plate plateObj = product.getPlate();
        if (null == plateObj) {
            log.info("车牌为空");
            return ResultInfo.success();
        }
        String plate = new String(Base64.getDecoder().decode(plateObj.getPlate()), StandardCharsets.UTF_8);
//        Base64Utils.decode(plateObj.getPlate());

        // 车位信息
        Parking parking = product.getParking();
        if (null == parking) {
            log.info("车位为空");
            return ResultInfo.success();
        }
        String stall_code = Base64Utils.decode(parking.getZone_name());

        // 识别信息对象
        Reco reco = product.getReco();
        if (null == reco) {
            log.info("识别信息为空");
            return ResultInfo.success();
        }
        // 识别时间
        String reco_time = reco.getReco_time();

        for (FetureImg temp : fetureImgs) {
            // base64 图片 转 inputstream
            // 解码Base64图片
            byte[] imageBytes = Base64.getDecoder().decode(temp.getImage());

            // 创建ByteArrayInputStream对象
            InputStream inputStream = new ByteArrayInputStream(imageBytes);

            String fileName = IdUtils.fastSimpleUUID() + ".jpg";

            try {
                // 如果设备编码一样, 车牌号一样,停车场名称一样, 文件大小一样,数据间隔少于1分钟,type一样,则不进行上传
                Long groundLocksCount = groundLocksImgMapper.selectCount(Wrappers.<GroundLocksImg>lambdaQuery()
                        .eq(GroundLocksImg::getSn, deviceInfo.getSn())
                        .eq(GroundLocksImg::getSerial, param.getSerial() + "")
                        .eq(GroundLocksImg::getReco_id, param.getReco_id())
                        .eq(GroundLocksImg::getType, temp.getType())
                        .eq(GroundLocksImg::getImage_length, temp.getImage_length())
                        .ge(GroundLocksImg::getCreateTime, LocalDateTime.now().minusMinutes(1))
                );
                if (groundLocksCount > 0) {
                    log.info("图片已存在,不进行上传");
                    continue;
                }
                log.info("图片上传到minio: fileName:{}", fileName);
                minioTemplate.putObject(G.MINIO_BUCKET_LOCK, fileName, inputStream);
                // 地锁 图片信息入库操作
                groundLocksImgMapper.insert(GroundLocksImg
                        .builder()
                        .id(IdUtils.fastSimpleUUID())
                        // serial 序列号
                        .serial(param.getSerial() + "")
                        // sn 设备号
                        .sn(deviceInfo.getSn())
                        // ip
                        .ip(deviceInfo.getIp())
                        // dev_name
                        .dev_name(deviceInfo.getDev_name())
                        // image 图片base64
                        .image(fileName)
                        // image_length
                        .image_length(temp.getImage_length())
                        // image_send_flag
                        .image_send_flag(temp.getImage_send_flag())
                        // key
                        .img_key(temp.getKey())
                        // type
                        .type(temp.getType())
                        // reco_id
                        .reco_id(param.getReco_id())
                        // 时间戳
                        .time_s(param.getTime_s() + "")
                        // 车牌号
                        .plate(plate)
                        // 车位编号
                        .stall_code(stall_code)
                        // 识别时间
                        .reco_time(reco_time)
                        // 车位状态
                        .parking_state(parking.getParking_state())
                        // 左车位,右车位
                        .zone_id(parking.getZone_id())
                        .createTime(new Date())
                        .build()
                );
                // 如果是1，车辆要入场，再用车牌号跟数据库对比，如果是我们公司的车，则发送落锁指令
                // sn 设备号
                String sn = param.getDevice_info().getSn();
                // timestamp 时间戳
                String timestamp = String.valueOf(param.getTime_s());
                String msg_type = "IVS";
                //1. 按照“key=value”格式，使⽤“&”符号，按顺序拼接为字符串，格式如下： sn=xxx&timestamp=xxx&msg_id=xxx&msg_type=xxx
                String snTemp = "sn=" + sn + "&timestamp=" + timestamp + "&msg_id=GO2021070112000101&msg_type=GroundlockOption";
                //2. 将拼接字符串进⾏32位MD5计算，得到⼤写的数字签名
                String sign = DigestUtils.md5DigestAsHex(snTemp.getBytes()).toUpperCase();


                if (8 == parking.getParking_state()){
                    // 离场信息 mqtt双向绑定
                    // 空场
                    log.info("-----------------------LockController 空场 start-----------------------");
                    // 空场逻辑
                    // 升起
                    int option = 1;
                    // 语音播报
                    MqttClientCallback.sendVoice(sign, sn, timestamp, option);
                    // 操作
                    MqttClientCallback.liftAndDropOperation(sign, param.getDevice_info().getSn(), timestamp, option, parking.getZone_id(), "", 1);

                    log.info("-----------------------LockController 空场 end-----------------------");
                } else if (4 == parking.getParking_state()) {
                    // 出场
                    log.info("-----------------------LockController 出场 start-----------------------");
                    // 出场逻辑
                    // 升起
                    int option = 1;
                    // 操作
                    MqttClientCallback.liftAndDropOperation(sign, param.getDevice_info().getSn(), timestamp, option, parking.getZone_id(), "");
                    log.info("-----------------------LockController 出场 end-----------------------");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

        }

        log.info("end推送业务识别结果");
        return ResultInfo.success();
    }

    /**
     *
     *地下车库 停车记录接口
     * @author wzn
     * @date 2024/11/08 08:59
     */
    @PostMapping("videoStall")
    public ResultInfo videoStall(@RequestBody String body) {
        return ResultInfo.success() ;
    }


}
