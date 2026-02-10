package com.jsdc.iotpt.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EmailVo {


    //邮件主题
    private String subject = "邮件发送测试";
    //邮件文本内容
    private String messageText = "这是一份测试邮件";
    //HTML格式的邮件内容
    private String htmlText ="<div>\n" +
            "    <p>这是一个HTML格式的邮件</p> <br>" +
            "    <img src=\"https://w.wallhaven.cc/full/p9/wallhaven-p9o51m.png\" width='500' height='300' /><br>\n" +
            "    <a href=\"https://www.yb2cc.cn\" title=\"码上就去学习\" target=\"_blank\">点我码上就去学习</a>\n" +
            "</div>";
    //附件（文件路径集合）
    private List<String> filePathList = new ArrayList<>();

//    //邮件发送方
//    private String mailSender;
//
//    //邮件接收方
//    private String mailRecipient;

}
