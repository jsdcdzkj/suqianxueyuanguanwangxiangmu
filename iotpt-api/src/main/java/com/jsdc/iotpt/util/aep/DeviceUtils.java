package com.jsdc.iotpt.util.aep;

public class DeviceUtils {


    public static String buileWei(String data,Integer starl,Integer endl) {
        String rs="";
        if (endl==0) {
            rs=data.substring(starl*2-2, starl*2);
            return rs;
        }
        rs=data.substring(starl*2-2, endl*2);
        return rs;

    }

}