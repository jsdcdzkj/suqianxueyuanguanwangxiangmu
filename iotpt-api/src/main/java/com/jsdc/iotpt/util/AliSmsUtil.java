package com.jsdc.iotpt.util;


import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 * @Author lb
 * @Date 2023/7/27 8:40
 * @Description 类描述
 **/
@Component
public class AliSmsUtil {

    private static final Logger log = LoggerFactory.getLogger(AliSmsUtil.class);
    /**
     * 建议：提到yml配置文件中
     * // private static String path = "http://localhost:8089/aliSms/meetingMgn";
     * //建议：提到yml配置文件中
     * //  private static String pathUrl = "pages/SignIn/qiandao/index";
     * <p>
     * // public static void main(String[] args) {
     * //        httpClientMethon(null, null, null);
     * // }
     **/


    private static String serverIp;

    @Value("${aliSMS.url}")
    private String server_Ip;

    private static String daiPaiChe;

    @PostConstruct
    public void getEnvironment() {
        serverIp = this.server_Ip;

    }

    /**
     * 验证码
     * 忘记密码验证码
     *
     * @return
     */
    public static String smsMethod(String verificationCode, String phoneNum) {
        String url = serverIp + "/aliSms/smsMethod";
        JSONObject params = new JSONObject();
        params.put("smsCode", verificationCode);
        params.put("sending_phone", phoneNum);
        log.info("忘记密码验证码" + params.toJSONString());
        return sendSMS(url, params);
    }

    /**
     * 会议 - 服务提醒
     *
     * @return
     */
    public static String MeetingServiceWarn(String cont, String phoneNum) {
        String url = serverIp + "/aliSms/smsMethod";
        JSONObject params = new JSONObject();
        params.put("smsCode", cont);
        params.put("sending_phone", phoneNum);
        return sendSMS(url, params);
    }

    /**
     * 预约提醒
     *
     * @return
     */
    public static String visitorReminder(String time, String phoneNum) {
        String url = serverIp + "/aliSms/visitorReminder";
        JSONObject params = new JSONObject();
        params.put("time", time);
        params.put("sending_phone", phoneNum);
        return sendSMS(url, params);
    }

    /**
     * 来访通知
     *
     * @return
     */
    public static String visitorNotice(String phone) {
        String url = serverIp + "/aliSms/visitNotice";
        JSONObject params = new JSONObject();
        params.put("sending_phone", phone);
        return sendSMS(url, params);
    }

    /**
     * 主动邀约
     *
     * @return
     */
    public static String visitorInitiativeReminder(String time, String phoneNum) {
        String url = serverIp + "/aliSms/visitorInitiativeReminder";
        JSONObject params = new JSONObject();
        params.put("time", time);
        params.put("sending_phone", phoneNum);
        return sendSMS(url, params);
    }

    /**
     * 访客到达通知
     *
     * @return
     */
    public static String noticeOfArrival(String phone) {
        String url = serverIp + "/aliSms/noticeOfArrival";
        JSONObject params = new JSONObject();
        params.put("sending_phone", phone);
        return sendSMS(url, params);
    }


    /**
     * @param url
     * @param params
     * @return 返回 code为1 成功 其它是失败
     * 调以方法要在yml中添加 模板。并调用refreshProps方法生效
     */
    private static String sendSMS(String url, JSONObject params) {
        log.error(url);
        log.error(params.toJSONString());
        // 创建HttpClient实例
        HttpClient httpclient = HttpClients.createDefault();
        // 创建POST请求对象，并设置URL
        //  HttpPost httppost = new HttpPost(serverIp + "/aliSms/dcycReject");
        HttpPost httppost = new HttpPost(url);
        try {
            // 创建要传递的对象
            // 将对象转换为JSON格式的字符串
//            JSONObject params = new JSONObject();
//            params.put("sending_phone", phone);
//            params.put("title", title);
//            params.put("name", name);
//            params.put("time", time);
//            params.put("resons", resons);
//            params.put("content", content);
//
//            params.put("content", reject);
            // 设置请求头
            httppost.setHeader("Content-Type", "application/json");
            // 设置请求体
            StringEntity s = new StringEntity(params.toString(), "UTF-8");
            s.setContentType("application/json");
            httppost.setEntity(s);
            // 执行请求
            HttpResponse response = httpclient.execute(httppost);
            // 获取响应内容
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            System.out.println(result);
            return result;
            // 处理响应结果

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("发送失败");
        return "-1"; //
    }

    /**
     * 订餐统计数据通知(新的)
     *
     * @param date
     * @param num
     * @return
     */
    public static String foodSMSNewUser(String phones, String date, long num, Integer peoNum, double moneyDay) {
        String url = serverIp + "/aliSms/foodSMSNewUser";
        JSONObject params = new JSONObject();
        params.put("sending_phone", phones);
        params.put("date", date);
        params.put("num1", peoNum);//今日消费人次
        params.put("num2", moneyDay);//今日消费金额
        params.put("num3", num);//明日消费人次
        return sendSMS(url, params);
    }

    /**
     * 订餐统计数据通知(旧的)
     *
     * @param date
     * @param num
     * @return
     */
    public static String foodSMSUser(String phones, String date, long num) {
        String url = serverIp + "/aliSms/foodSMSUser";
        JSONObject params = new JSONObject();
        params.put("sending_phone", phones);
        params.put("date", date);
        params.put("num", num);
        return sendSMS(url, params);
    }


    /**
     * 预定菜单通知
     *
     * @param phones
     * @return
     */
    public static String foodNotice(String phones, String time) {
        String url = serverIp + "/aliSms/foodNotice";
        JSONObject params = new JSONObject();
        params.put("sending_phone", phones);
        params.put("time", time);
        return sendSMS(url, params);
    }


    /**
     * 告警短信通知
     *
     * @param phones
     * @return
     */
    public static String alarmNotice(String phones, String region, String type, String content) {
        String url = serverIp + "/aliSms/alarmNotice";
        JSONObject params = new JSONObject();
        params.put("sending_phone", phones);
        params.put("alarmRegion", region);
        params.put("alarmType", type);
        params.put("alarmContent", content);
        return sendSMS(url, params);
    }


    /**
     * 会议室场景配置
     *
     * @param phones
     * @return
     */
    public static String meetingMrrange(String phones, Integer num) {
        String url = serverIp + "/aliSms/meetingMrrange";
        JSONObject params = new JSONObject();
        params.put("sending_phone", phones);
        params.put("num", num);
        return sendSMS(url, params);
    }

    /**
     * 会议室结束配置(废除)
     *
     * @param phones
     * @return
     */
    public static String meetingEnd(String phones, Integer num) {
        String url = serverIp + "/aliSms/meetingEnd";
        JSONObject params = new JSONObject();
        params.put("sending_phone", phones);
        params.put("num", num);
        return sendSMS(url, params);
    }

    /**
     * 会议室结束配置(使用)
     *
     * @param phones
     * @return
     */
    public static String meetingEndTime(String phones, String name) {
        String url = serverIp + "/aliSms/meetingEndTime";
        JSONObject params = new JSONObject();
        params.put("sending_phone", phones);
        params.put("name", name);
        return sendSMS(url, params);
    }


    /**
     * 包厢预约成功-通知审批人
     *
     * @param phones
     * @param money
     * @return
     */
    public static String approvalNotice(String phones, String money) {
        String url = serverIp + "/aliSms/approvalNotice";
        JSONObject params = new JSONObject();
        params.put("sending_phone", phones);
        params.put("money", money);
        return sendSMS(url, params);
    }


    /**
     * @Description: 证书到期提醒
     * @param: [name, evet, phone]
     * @return: java.lang.String
     * @author: 苹果
     * @date: 2024/8/13
     * @time: 14:58
     */
    public static String sendSms2Shop(String name, String evet, String phone) {
        String url = serverIp + "/aliSms/sendSms";
        JSONObject params = new JSONObject();
        //模板参数
        params.put("name", name);
        params.put("time", evet);

        //基础参数
        params.put("phoneNumbers", phone);
        params.put("signName", "鼎驰科技");
        params.put("code", "SMS_471730023");

        return sendSMS(url, params);
    }

    /**
     * 发送阿里短信 通用接口
     * String url = serverIp + "/aliSms/sendSms";
     * <p>
     * JSONObject params = new JSONObject();
     * //模板参数
     * params.put("name", name);
     * params.put("evet", evet);
     * <p>
     * //基础参数
     * params.put("phoneNumbers", phone);
     * params.put("signName", "鼎驰科技");
     * params.put("code", "SMS_469015420");
     *
     * @param params
     * @return
     */
    public static String sendSMS(JSONObject params) {
        String url = serverIp + "/aliSms/sendSms";
        return sendAliSMS(url, params);
    }

    public static String reserveSuccessNotice(String phone, Date time) {
//        String url = serverIp + "/aliSms/boxOrderSuccess";
//        JSONObject params = new JSONObject();
//        //基础参数
//        params.put("phoneNumbers", phone);
//        params.put("signName", "鼎驰科技");
//        params.put("code", "SMS_472110163");
//        params.put("time", DateUtil.format(time, "yyyy-MM-dd HH:mm"));
//        return sendSMS(params);
        String url = serverIp + "/aliSms/boxOrderSuccess";
        JSONObject params = new JSONObject();
        //基础参数
        params.put("sending_phone", phone);
        params.put("time", DateUtil.format(time, "yyyy-MM-dd HH:mm"));
        return sendSMS(url, params);
    }

    public static String rejectNotice(String phone, Date time) {
        String url = serverIp + "/aliSms/boxOrderError";
        JSONObject params = new JSONObject();
        //基础参数
        params.put("sending_phone", phone);
        params.put("time", DateUtil.format(time, "yyyy-MM-dd HH:mm"));
        return sendSMS(url, params);
//        JSONObject params = new JSONObject();
//        //基础参数
//        params.put("phoneNumbers", phone);
//        params.put("signName", "鼎驰科技");
//        params.put("code", "SMS_472395001");
//        params.put("time", DateUtil.format(time, "yyyy-MM-dd HH:mm"));
//        return sendSMS(params);
    }

    //备餐通知
    public static String prepareNotice(String phone, Date time) {
        String url = serverIp + "/aliSms/prepareNotice";
        JSONObject params = new JSONObject();
        //基础参数
        params.put("sending_phone", phone);
        params.put("time", DateUtil.format(time, "yyyy-MM-dd HH:mm"));
        return sendSMS(url, params);
//        JSONObject params = new JSONObject();
//        //基础参数
//        params.put("phoneNumbers", phone);
//        params.put("signName", "鼎驰科技");
//        params.put("code", "SMS_471945190");
//        params.put("time", DateUtil.format(time, "yyyy-MM-dd HH:mm"));
//        return sendSMS(params);
    }


    /**
     * 发送阿里语音短信 通用接口
     * String url = serverIp + "/aliSms/sendYuYinSms";
     * JSONObject params = new JSONObject();
     * JSONObject ttsParam = new JSONObject();
     * //模板参数
     * ttsParam.put("name", name);
     * ttsParam.put("evet", evet);
     * <p>
     * //基础参数
     * params.put("calledNumber", calledNumber);
     * params.put("ttsCode", "TTS_299450169");
     * params.put("ttsParam", ttsParam.toJSONString());
     *
     * @param params
     * @return
     */
    public static String sendSMSYuYin(JSONObject params) {
        String url = serverIp + "/aliSms/sendYuYinSms";
        return sendAliSMS(url, params);
    }

    private static String sendAliSMS(String url, JSONObject params) {
        log.error(url);
        log.error(params.toJSONString());
        // 创建HttpClient实例
        HttpClient httpclient = HttpClients.createDefault();
        // 创建POST请求对象，并设置URL
        HttpPost httppost = new HttpPost(url);
        try {
            // 创建要传递的对象
            // 将对象转换为JSON格式的字符串
            // 设置请求头
            httppost.setHeader("Content-Type", "application/json");
            // 设置请求体
            StringEntity s = new StringEntity(params.toString(), "UTF-8");
            s.setContentType("application/json");
            httppost.setEntity(s);
            // 执行请求
            HttpResponse response = httpclient.execute(httppost);
            // 获取响应内容
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            System.out.println(result);
            return result;
            // 处理响应结果

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        System.out.println("发送失败");
        return "{\"msg\":\"失败\",\"code\":\"F\"}";
    }
}
