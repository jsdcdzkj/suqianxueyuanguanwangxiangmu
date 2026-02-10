package com.jsdc.iotpt.vo;


import lombok.Data;

@Data
public class MetaVo {

    private String title;

    private String icon;

    public MetaVo(String title, String icon) {
        this.title = title;
        this.icon = icon;
    }
}
