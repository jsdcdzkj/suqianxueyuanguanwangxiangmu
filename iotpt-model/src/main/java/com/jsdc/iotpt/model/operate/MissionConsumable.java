package com.jsdc.iotpt.model.operate;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: 园区物联网管控平台 报事报修 耗材
 * @author: jxl
 * @create: 2025-01-08 14:54
 **/
@Entity
@TableName("MISSION_CONSUMABLE")
@Table(name = "MISSION_CONSUMABLE")
@KeySequence(value = "SEQ_MISSION_CONSUMABLE_ID")
@Data
@ApiModel(value = "任务表")
public class MissionConsumable implements Serializable {

    private static final long serialVersionUID = 3234235552261L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "missionConsumableId", sequenceName = "SEQ_MISSION_CONSUMABLE_ID", allocationSize = 1)
    @GeneratedValue(generator = "missionConsumableId", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "任务表ID")
    private Integer id;
    //关联任务id
    @ApiModelProperty(value = "任务ID")
    private Integer missionId;
    @ApiModelProperty(value = "是否暂存 1是0否")
    private Integer temporary;
    @ApiModelProperty(value = "耗材名称")
    private String consumableName;
    @ApiModelProperty(value = "耗材价格")
    private BigDecimal money;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    private Integer createUser;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    private Integer updateUser;
    @ApiModelProperty(value = "是否删除 1是0否")
    private Integer isDel;

    @TableField(exist = false)
    @Transient
    private String username;
}
