package com.jsdc.iotpt.vo;

import io.swagger.models.auth.In;
import lombok.Data;

import java.util.List;

@Data
public class FaceVo {


    private String id ;
    private String detail ;
    private String groupName ;

    private Integer grouptype ;
    private List<String> deviceCodeList ;


    private List<String> types ;
    private List<String> nodeCodes ;
    private String url ;
    private String category ;
    private String name ;

    private String receiveIp;
    private  String receivePort;

    private  Integer sex;
    private  String cardtype;
    private  String cardid;
    private  Long groupid;
    private  String birthday;
    private  String province;
    private  String city;
    private  String bankAccount;
    private  String facepath;
    private  Integer personId;


    private  List<Integer> groupids;
    private  String deptName;

    private  String channelCode;
    private  String begainTime;
    private  String stopTime;
    private  String operateType;
    private  String direct;
    private  String step;
    private  String command;
    private  String stepX;
    private  String tepY;
    private  String target;





}
