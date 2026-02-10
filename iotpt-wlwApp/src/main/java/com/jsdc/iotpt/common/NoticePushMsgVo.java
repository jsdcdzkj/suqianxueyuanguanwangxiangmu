package com.jsdc.iotpt.common;

import lombok.Data;

import java.util.Date;

@Data
public class NoticePushMsgVo {

    private Integer id;
    // 是否发送 0,否 1 是
    private Integer is_send;
    // 40.耗材库存
    private String menu_code;
    // 3.固定资产,默认
    private String msgType;

    // 发送的地址
    private String url;
    // 名称
    private String name;
    // 发谁的电话
    private String sending_phone;

    private String type;

    private String resons;

    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 发给谁
     */
    private String to_user;
    /**
     * 发送时间
     */
    private Date send_time;
    /**
     * 发送人
     */
    private String send_user;
    /**
     * 0,未删除 1 已删除
     */
    private String is_del;
}
