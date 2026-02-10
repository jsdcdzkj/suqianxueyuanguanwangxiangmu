package com.jsdc.iotpt.util;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;


public  class OkHttpClientUtil {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");








    /**
     * post 请求, 结算中心接口请求
     * @param json      请求数据
     * @return string
     */
    public static JSONObject  doPostJson(String url,String json) {
        return exectePost(url, json, JSON);
    }
    private static JSONObject exectePost(String url, String data, MediaType contentType) {
        RequestBody requestBody = RequestBody.create(contentType, data);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        return execute(request);
    }

    private static JSONObject execute(Request request) {
        Response response = null;
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return JSONObject.parseObject(response.body().string());
            }
        } catch (Exception e) {

        } finally {
            if (response != null) {
                response.close();
            }
        }
        JSONObject responseResult = new JSONObject();
        responseResult.put("code","0");
        responseResult.put("msg","接口调用失败");
        return responseResult;
    }



}
