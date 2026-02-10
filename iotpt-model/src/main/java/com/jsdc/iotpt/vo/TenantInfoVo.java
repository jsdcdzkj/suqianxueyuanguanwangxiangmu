package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.TenantInfo;
import lombok.Data;

import java.util.List;


@Data
public class TenantInfoVo extends TenantInfo{

    private String deviceCode;

    private String deviceId;

    private String isOnOff;//0.00开   1.00关

    private String pwd;

    private String topic;

    private String gteway;

}
