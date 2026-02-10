package com.jsdc.iotpt.util;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author lb
 * @Date 2023/7/27 8:37
 * @Description 类描述
 **/
@Data
public class AliSmsVo implements Serializable {

    private String title;//标题内容|物品内容
    private String name;//申请人|驳回节点
    private String time;//时间
    private String resons;//驳回理由
    private String content;//链接内容
    private String code;//短信模版编码
    private String phone;//发送人手机号

    private String plate;//车牌号


    private String dest;//目的地
    private String start;//起始里程
    private String end;//结束里程
    private String smsCode;//验证码
}
