package com.jsdc.iotpt.vo;

import lombok.Data;

import java.util.Date;

@Data
public class LogVo {
    //内容
    private String content;

    //操作时间
    private Date operateTime;
    //是否删除 0:正常  1：删除
    private Integer isDel;

    public LogVo(String content) {
        this.content = content;
        this.operateTime = new Date();
        this.isDel = 0;
    }
}
