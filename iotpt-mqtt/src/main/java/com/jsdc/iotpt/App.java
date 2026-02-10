package com.jsdc.iotpt;


import com.alibaba.fastjson.JSONObject;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {



    public static void main(String[] args) {
        String name="tcp://192.168.0.93:1883";


        int last=name.lastIndexOf(":");

        System.out.println(name.replaceFirst("tcp","http").substring(0,name.lastIndexOf(":")+1));
    }
}