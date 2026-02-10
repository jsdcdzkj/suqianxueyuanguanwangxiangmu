package com.jsdc.iotpt.common;

import org.springframework.util.Base64Utils;

import java.util.HashMap;
import java.util.Map;

public enum SystemKey {
    UNKNOWN(""),
    GATEWAY_INFO("gateWayId"),
    DEVICE_INFO("deviceId"),
    SIGNAL_INFO("channelId|val|dataTime");


    // 类型对应后缀
    private Map<String, Boolean> mSuffixMap;


    // suffix, 类型对应的后缀，用;分割
    private SystemKey(String keys) {
        this.mSuffixMap = new HashMap<String, Boolean>();
        String[] splits = keys.split("\\|");
        for (String suffix : splits) {
            if (suffix.isEmpty())
                continue;
            this.mSuffixMap.put(suffix, true);
        }
        return;
    }


    public boolean isMatch(String suffix) {
        if (this.mSuffixMap.containsKey(suffix))
            return true;
        return false;
    }

    public static SystemKey getSeemingKey(String suffix) {
        if (null == suffix){
            return SystemKey.UNKNOWN;
        }
        // 逐一判断
        if (SystemKey.GATEWAY_INFO.isMatch(suffix))
            return SystemKey.GATEWAY_INFO;
        if (SystemKey.DEVICE_INFO.isMatch(suffix))
            return SystemKey.DEVICE_INFO;
        if (SystemKey.SIGNAL_INFO.isMatch(suffix))
            return SystemKey.SIGNAL_INFO;
        return SystemKey.UNKNOWN;
    }




}
