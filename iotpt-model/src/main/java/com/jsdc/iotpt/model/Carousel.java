package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * ClassName: AlertSheet
 * Description: 设备告警记录表
 * date: 2023/5/19 11:48
 *
 * @author bn
 */
@Entity
@TableName("CAROUSEL")
@Table(name = "CAROUSEL")
@KeySequence(value = "SEQ_CAROUSEL_ID")
@Data
public class Carousel extends Model<Carousel> {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @ApiModelProperty(value = "轮播标题")
    private String title;

    @ApiModelProperty(value = "轮播位置")
    private Integer position;

    @ApiModelProperty(value = "轮播顺序")
    private Integer rotationOrder;//order是关键字

    // 查询开始时间
    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
    // 查询结束时间
    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    @ApiModelProperty(value = "轮播图片")
    private String image;

    @ApiModelProperty(value = "轮播时间 单位:秒")
    private Integer time;

    @ApiModelProperty(value = "链接选项(1.无, 2.站内文档, 3.url)")
    private String linkOption;

    @ApiModelProperty(value = "链接内容")
    private String linkOptionContent;

    @ApiModelProperty(value = "状态(1.未开始 2.展示中 3.已结束)")
    private Integer state;


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


    // 组装图片地址
    @Transient
    @TableField(exist = false)
    private String imageUrl;
    // 下载图片地址
    @Transient
    @TableField(exist = false)
    private String imageUrlDown;

    // 查询开始时间
    @Transient
    @TableField(exist = false)
    private String startTimeStr;
    // 查询结束时间
    @Transient
    @TableField(exist = false)
    private String endTimeStr;
    // 位置名称
    @Transient
    @TableField(exist = false)
    private String positionName;
    // 状态名称
    @Transient
    @TableField(exist = false)
    private String stateName;

    // 分页
    @Transient
    @TableField(exist = false)
    private Integer pageIndex;

    // 分页
    @Transient
    @TableField(exist = false)
    private Integer pageSize;

}
