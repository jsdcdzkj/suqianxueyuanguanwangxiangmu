package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 车辆布控
 */
@Entity
@TableName("VEHICLE")
@Table(name = "VEHICLE")
@KeySequence(value = "VEHICLE_ID")
@Data
@ApiModel(value = "黑/白名单")
public class Vehicle implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "VEHICLE_ID", sequenceName = "VEHICLE_ID", allocationSize = 1)
    @GeneratedValue(generator = "VEHICLE_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "车辆id")
    private Integer id;
    @ApiModelProperty(value = "车牌类型-数据字典(vehicleType)")
    private Integer type;
    @ApiModelProperty(value = "名单类型 1.黑名单 2.白名单")
    private Integer nameListType;
    @ApiModelProperty(value = "车辆号码")
    private String code;
    @ApiModelProperty(value = "门禁ID")
    private String doorIds;
    @ApiModelProperty(value = "备注")
    private String memo;

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


    @ApiModelProperty(value = "是否启用")
    private Integer isEnable;

    @ApiModelProperty(value = "是否删除")
    private Integer isDel;

    @Transient
    @TableField(exist = false)
    private String typeName;
    @Transient
    @TableField(exist = false)
    private String doorName;
    @Transient
    @TableField(exist = false)
    private Integer pageIndex;
    @Transient
    @TableField(exist = false)
    private Integer pageSize;
    @Transient
    @TableField(exist = false)
    private List<Integer> fileIds;
    @Transient
    @TableField(exist = false)
    private String fileUrl;
    @Transient
    @TableField(exist = false)
    private String timeStart;
    @Transient
    @TableField(exist = false)
    private String timeEnd;
}
