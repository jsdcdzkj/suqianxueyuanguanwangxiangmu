package com.jsdc.iotpt.common.enums;

public enum CalenderEnum {
    DATE_TYPE("天", 1),
    WEEK_TYPE("星期", 2),
    MONTH_TYPE("月", 3),
    YEAR_TYPE("年", 4);


    private String name;
    private Integer code;


    CalenderEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }


}

