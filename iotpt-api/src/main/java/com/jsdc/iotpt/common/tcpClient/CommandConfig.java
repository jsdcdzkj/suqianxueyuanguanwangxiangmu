package com.jsdc.iotpt.common.tcpClient;


import io.netty.channel.Channel;

import java.util.HashMap;

import java.util.Map;

public class CommandConfig {

    private static CommandConfig instance;

    private Map<String,Channel> channels;

    private CommandConfig() {
        channels=new HashMap<>();
    }

    public static CommandConfig getInstance(){
        if (instance==null){
            instance =new CommandConfig();
        }
        return instance;
    }

    public Map<String, Channel> getChannels() {
        return channels;
    }

    public void setChannels(Map<String, Channel> channels) {
        this.channels = channels;
    }
}
