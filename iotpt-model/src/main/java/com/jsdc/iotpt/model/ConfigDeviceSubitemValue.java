package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@TableName("CONFIG_DEVICE_SUBITEM_VALUE")
@Table(name = "CONFIG_DEVICE_SUBITEM_VALUE")
@KeySequence(value = "CONFIG_DEVICE_SUBITEM_VALUE_ID")
@Data
public class ConfigDeviceSubitemValue implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "configdevicesubitemvalueId", sequenceName = "CONFIG_DEVICE_SUBITEM_VALUE_ID", allocationSize = 1)
    @GeneratedValue(generator = "configdevicesubitemvalueId", strategy = GenerationType.SEQUENCE)
    private Integer id;


    //设备分项id
    private String subitemId;

    //定额值
    private String ratedValue;

    //年份
    private String year;


}
