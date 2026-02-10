package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.ConfigDeviceSignalMap;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value ="设备信号关联")
public class ConfigDeviceSignalMapVo extends ConfigDeviceSignalMap implements Serializable {
    private Integer pageNo = 1;
    private Integer pageSize = 10;
    //信号类型串
    @ApiModelProperty(value ="信号类型串")
    private String sIds;
    //设备类型串
    @ApiModelProperty(value ="设备类型")
    private Integer deviceTypeId;

    //信号类型串
    @ApiModelProperty(value ="信号类型名称")
    private String signalTypeName;
    //设备类型串
    @ApiModelProperty(value ="信号类型编码")
    private String signalTypeCode;
    //设备类型串
    @ApiModelProperty(value ="描述")
    private String signalTypeDesc;
    //设备类型串
    @ApiModelProperty(value ="信号值")
    private String signValue;

    //单位
    @ApiModelProperty(value ="单位")
    private String unit;
    //发送时间
    private String time;

    //信号类型分类
    @ApiModelProperty(value ="信号类型分类")
    private String signalType;
    private String dictLabel;
}
