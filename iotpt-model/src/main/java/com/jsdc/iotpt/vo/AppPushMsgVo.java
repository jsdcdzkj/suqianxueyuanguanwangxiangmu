package com.jsdc.iotpt.vo;

import lombok.Data;

import java.util.List;

@Data
public class AppPushMsgVo {
    private String title;
    private String name;
    private String resons;
    private String num;
    private String num1;
    private String boxName;
    private String username;
    private String money;
    private String time;
    private String time1;
    private String reasons;
    private String plate;
    private String startTime;
    private String endTime;
    private String projectName;
    private String content;
    private String day;

    private String sending_phone;

    private String type;

    private String appConfigCode;
    private String AppTitle;
    private String AppContent;
    private List<String> phoneList;
    private List<Integer> userIdList;
    private String customParameters;// 自定义参数 json格式
    private String region;
    private String orgName;
    private String realname;
    private String reason;
    private String departureTimeString;
    private String companyApprover;
    private String companyApproverReason;
}
