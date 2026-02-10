package com.jsdc.iotpt.common.tcpClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BootNettyClientChannelCache {
    public static volatile Map<String, BootNettyClientChannel> channelMapCache = new ConcurrentHashMap<String, BootNettyClientChannel>();

    public static void add(String code, BootNettyClientChannel channel){
        channelMapCache.put(code,channel);
    }

    public static BootNettyClientChannel get(String code){
        return channelMapCache.get(code);
    }

    public static void remove(String code){
        channelMapCache.remove(code);
    }

    public static void save(String code, BootNettyClientChannel channel) {
        if(channelMapCache.get(code) == null) {
            add(code,channel);
        }
    }
}
