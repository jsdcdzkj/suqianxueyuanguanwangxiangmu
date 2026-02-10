package com.jsdc.iotpt.util;

public class NumberUtil {

    public static boolean isInteger(String str){
        if(str == null){
            return false;
        }
        try{
            Integer.parseInt(str);
        }catch (Exception e){
            return false;
        }
        return true;
    }
}
