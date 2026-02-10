package com.jsdc.iotpt.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 强密码校验工具类
 */
public class StrongPasswordValidatorUtils {

    public static Map<String,String> isValidPassword(String password) {
        Map<String,String> map = new HashMap<>();

        if (null == password || "".equals(password.trim())) {
            map.put("code","-1");
            map.put("msg","密码不能为空");
            return map;
        }

        // 最小长度8个字符
        if (password.length() < 8) {
            map.put("code","-1");
            map.put("msg","密码最小长度8个字符");
            return map;
        }

        // 至少一个大写字母
        Pattern upperCasePattern = Pattern.compile("[A-Z]");
        if (!upperCasePattern.matcher(password).find()) {
            map.put("code","-1");
            map.put("msg","密码至少包含一个大写字母");
            return map;
        }

        // 至少一个小写字母
        Pattern lowerCasePattern = Pattern.compile("[a-z]");
        if (!lowerCasePattern.matcher(password).find()) {
            map.put("code","-1");
            map.put("msg","密码至少包含一个小写字母");
            return map;
        }

        // 至少一个数字
        Pattern digitPattern = Pattern.compile("[0-9]");
        if (!digitPattern.matcher(password).find()) {
            map.put("code","-1");
            map.put("msg","密码至少包含一个数字");
            return map;
        }

        // 至少一个特殊字符
        Pattern specialCharPattern = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");
        if (!specialCharPattern.matcher(password).find()) {
            map.put("code","-1");
            map.put("msg","密码至少包含一个特殊字符");
            return map;
        }


        map.put("code","0");
        map.put("msg","成功");
        return map;
    }
}
