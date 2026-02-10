package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;


/**
 * 点位管理
 */
@Entity
@TableName("POINT")
@Table(name = "POINT")
@KeySequence(value = "POINT_ID")
@Data
public class Point extends Model<Point> {


    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "pointId", sequenceName = "POINT_ID", allocationSize = 1)
    @GeneratedValue(generator = "pointId", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "主键ID")
    private Integer id;

    /**
     * 逻辑位置
     * 所属区域
     * 关联区域表id
     */
    @ApiModelProperty(value = "逻辑位置-所属楼宇")
    private Integer logicalBuildId;
    @ApiModelProperty(value = "逻辑位置-所属楼层")
    private Integer logicalFloorId;
    @ApiModelProperty(value = "逻辑位置-所属区域")
    private Integer logicalAreaId;

    @ApiModelProperty(value = "物理位置")
    private String areaNames;
    @ApiModelProperty(value = "逻辑位置")
    private String logicalAreaNames;


    @ApiModelProperty(value = "点位名称")
    private String name;
    @ApiModelProperty(value = "排序")
    private Integer sort;
    @ApiModelProperty(value = "说明")
    private String remarks;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 创建人id
     */
    private Integer createUser;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 更新人id
     */
    private Integer updateUser;

    /**
     * 是否删除 0:正常  1：删除
     */
    private Integer isDel;
}
