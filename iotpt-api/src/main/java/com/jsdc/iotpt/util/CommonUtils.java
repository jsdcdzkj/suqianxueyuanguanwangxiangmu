package com.jsdc.iotpt.util;

import java.util.Map;

public class CommonUtils {

    /**
     * List 数据字典判断
     * 验证map是否为空,传入的key是否为空,map.get(key)是否为空
     */
    public static boolean isMapNotEmpty(Map map, Integer key) {
        if (map == null) {
            return false;
        }
        if (key == null) {
            return false;
        }
        if (map.get(key) == null) {
            return false;
        }
        return true;
    }
}
