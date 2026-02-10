package com.jsdc.iotpt.util;

import com.alibaba.fastjson.JSONObject;
import com.jsdc.iotpt.util.exception.CustomException;
import lombok.extern.slf4j.Slf4j;

import java.util.*;


/**
 * 统一门户工具类

 */

@Slf4j
public class UnifiedPortalUtils {

    /**
     * 通过应用ID和公钥私钥获取授权token，后续接口的调用需要header中携带此token请求，该token有效期为24小时，请不要循环调用
     */
    public static String getToken(String url, String appid, String privateKey){
        url = url + "/thirdAccountApi/getAccessToken";
        Map<String, String> param = new HashMap<>();
        param.put("url", url);
        JSONObject param1 = new JSONObject();
        param1.put("appId", appid);
        param1.put("privateKey", privateKey);
        param.put("param", param1.toString());
        param.put("contentType", "application/json; charset=UTF-8");
        String result = HttpUtils.sendPost(param);
        JSONObject res = JSONObject.parseObject(result);
        Integer code = res.getInteger("code");
        if (code == 200){
            return res.getString("data");
        }else {
            return res.getString("code");
        }
    }

    /**
     * 第三方用户同步
     */
    public static void syncAccountInfo(String url,String accessToken, List<JSONObject> params) {
        log.info(url + "/thirdAccountApi/syncAccountInfo");
        Map<String, String> obj = new HashMap<>();
        obj.put("url", url + "/thirdAccountApi/syncAccountInfo");
        obj.put("accept", accessToken);
        obj.put("param", params.toString());
        obj.put("contentType", "application/json; charset=UTF-8");

        log.info("开始同步用户信息=tbyhxx===>" + JSONObject.toJSONString(params));
        log.warn("开始同步用户信息=tbyhxx===>" + JSONObject.toJSONString(params));
        log.error("开始同步用户信息=tbyhxx==>" + JSONObject.toJSONString(params));


        String result = HttpUtils.sendPost(obj);
        JSONObject res = JSONObject.parseObject(result);
        Integer code = res.getInteger("code");
        if (code == 200){
            log.info("同步用户信息成功");
        }else {
            String errMsg = res.getString("msg");
            log.error("同步用户信息失败:  " + errMsg);
            throw new CustomException("同步用户信息失败:  " + errMsg);
        }

        log.info(code + "同步用户信息=tbyhxx=js==>结束" + JSONObject.toJSONString(params));
        log.warn(code + "同步用户信息=tbyhxx=js==>结束" + JSONObject.toJSONString(params));
        log.error(code + "同步用户信息=tbyhxx=js=>结束" + JSONObject.toJSONString(params));

    }

    /**
     * 比对两个日期是否是同一天
     */
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }
}
