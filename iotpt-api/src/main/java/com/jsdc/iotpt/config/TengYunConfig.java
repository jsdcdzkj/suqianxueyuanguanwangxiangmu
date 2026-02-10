package com.jsdc.iotpt.config;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * ClassName: TengYunConfig
 * Description:
 * date: 2024/5/27 14:58
 *
 * @author bn
 */
@Slf4j
public class TengYunConfig {


    // 测试地址
    private static String hostUrl="http://172.168.80.1:58100/tengyun-api";

    //应用ID（平台提供）
    private static String APPID="319291869861253120";

    //
    private static String APPSECRET="aed79be7c2f74b4c9eaed53649b9bcc6";

    public static void main(String[] args) {
        /**
         * appid          应用ID（平台提供）
         * appsecret      应用秘钥（平台提供）
         * version        版本号，默认1.0.0
         * signtype       加密方法，md5 / sha256
         * timestamp      当前时间戳
         * nonce          随机数，不小于10位
         * content        请求内容
         * sign           加签字符串
         */

        //应用秘钥
        String appsecret = APPSECRET;

        //传参
        HashMap<Object, Object> bodyMap = new HashMap<>(3);
//        bodyMap.put("canteenId", "313921594210652160");
//        bodyMap.put("pageNum", 1);
//        bodyMap.put("startDate", "2024-05-29");
//        bodyMap.put("endDate", "2024-05-29");

        //请求内容字典排序，转换为String
        String content = JSON.toJSONString(bodyMap, SerializerFeature.MapSortField, SerializerFeature.WriteMapNullValue);
        log.info("请求内容转换为JSON字符串，字典排序：{}", content);


        //构造请求验签参数
        Map<String, Object> map = new TreeMap<>();
        map.put("appid", APPID);
        map.put("version", "1.0.0");
        map.put("signtype", "md5");
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("nonce", RandomUtil.randomNumbers(10));
        map.put("content", content);
        //生成sign，防篡改
        String toSignStr = Joiner.on("&")
                .useForNull("")
                .withKeyValueSeparator("=")
                .join(map) + "&appsecret=" + appsecret;
        log.info("所有参数字典排序，待加签String：{}", toSignStr);

        // md5/sha256 加签（其他方式待提供）
        String sign = DigestUtils.md5Hex(toSignStr).toUpperCase();
        log.info("加签String：{}", sign);
        map.put("sign", sign);

        //post请求
        map.replace("content", bodyMap);
        HttpRequest request = HttpUtil.createPost("http://172.168.80.1:58100/tengyun-api/leopen/dish/query");
        log.info("request="+ JSONUtil.toJsonStr(map));
        request.body(JSONUtil.toJsonStr(map), ContentType.JSON.toString());
        HttpResponse response = request.execute();
        log.info("response={}", response.body());
    }

}
