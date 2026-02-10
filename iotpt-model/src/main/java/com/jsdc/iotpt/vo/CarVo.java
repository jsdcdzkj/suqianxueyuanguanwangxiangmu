package com.jsdc.iotpt.vo;

import lombok.Data;
@Data
public class CarVo {


    private String id ;
    //姓名
    private String realName ;
    //单位
    private String orgName ;
    //部门
    private String deptName ;
    //手机号
    private String phone ;

    private Integer pageNo ;

    private Integer pageSize ;

}
