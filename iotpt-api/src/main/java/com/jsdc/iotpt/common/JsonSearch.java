package com.jsdc.iotpt.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;


public class JsonSearch {
    /**
     * 从json字符串中查询含有searchKey的JSON对象
     *
     * @param jsonObject   待查找的json对象
     * @param searchKey    要查找的key
     * @return 返回含有searchKey的json对象
     */
    public static JSONObject findKey(JSONObject jsonObject, String searchKey) {
        JSONObject result = null;
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof JSONArray) {
                result = handleArray((JSONArray) value, searchKey);
            }
            if (value instanceof JSONObject) {
                result = handleJsonObject((JSONObject) value, searchKey);
            }
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * 从json字符串中查询含有searchKey的JSON对象
     *
     * @param jsonStr    json字符串
     * @param searchKey  要查找的key
     * @return 返回含有searchKey的json对象
     */
    public static JSONObject findKey(String jsonStr, String searchKey) {
        return findKey(JSONObject.parseObject(jsonStr), searchKey);
    }

    public static JSONObject handleArray(JSONArray array, String searchKey) {
        JSONObject result = null;
        for (int i = 0; i < array.size(); i++) {
            Object object = array.get(i);
            if (object instanceof JSONArray) {
                //递归查询
                result = handleArray((JSONArray) object, searchKey);
            }
            if (object instanceof JSONObject) {
                result = handleJsonObject((JSONObject) object, searchKey);
            }
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public static JSONObject handleJsonObject(JSONObject jsonObject, String searchKey) {
        JSONObject result = null;
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof JSONArray) {
                result = handleArray((JSONArray) value, searchKey);
            }
            if (value instanceof JSONObject) {
                //递归查询
                result = handleJsonObject((JSONObject) value, searchKey);
            }
            if (result != null) {
                return result;
            }
            if (value instanceof String) {
                if (searchKey != null && searchKey.equals(key)) {
                    return jsonObject;
                }
            }
        }
        return null;
    }
}
