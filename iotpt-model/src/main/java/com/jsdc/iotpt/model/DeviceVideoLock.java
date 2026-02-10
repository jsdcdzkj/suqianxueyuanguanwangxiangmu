package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @projectName: IOT
 * @author: wp
 * @description: 视频设备表
 * @date: 2023/5/9 9:50
 */
@Entity
@TableName("DEVICE_VIDEO_LOCK")
@Table(name = "DEVICE_VIDEO_LOCK")
@Data
@ApiModel(value = "地锁设备")
public class DeviceVideoLock implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @ApiModelProperty(value = "视频设备id")
    private Integer videoId;

    @ApiModelProperty(value = "地锁设备编码")
    private String lockCode;

//    @ApiModelProperty(value = "车位id")
//    private Integer parkingId;
    //1普通车位 2指定车位 3新能源车位
    private String stallType;
    @Transient
    @TableField(exist = false)
    private String stallTypeName;

    @ApiModelProperty(value = "车位编号")
    private String parkingCode;

    @ApiModelProperty(value = "地锁状态 0:未知 1:升起 2:降下")
    private Integer lockStatus;

    @ApiModelProperty(value = "车位标识0,左车位 1,右车位")
    private Integer parkingFlag;

    @ApiModelProperty(value = "是否启用, 1启用,2禁用")
    private Integer isEnable;


    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "创建人id")
    private Integer createUser;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "更新人id")
    private Integer updateUser;

    /**
     * 是否删除 0:正常  1：删除
     */
    @ApiModelProperty(value = "是否删除")
    private Integer isDel;


    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "视频设备名称")
    private String videoName;


    @Transient
    @TableField(exist = false)
    private DeviceVideoLock param1;

    @Transient
    @TableField(exist = false)
    private DeviceVideoLock param2;

}
