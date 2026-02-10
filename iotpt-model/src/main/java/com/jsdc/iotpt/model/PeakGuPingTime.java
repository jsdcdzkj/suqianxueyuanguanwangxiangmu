package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.util.Date;

/**
 * 峰谷平-电费配置-子表
 *
 */
@Entity
@TableName("PEAK_GU_PING_Time")
@Table(name = "PEAK_GU_PING_Time")
@org.hibernate.annotations.Table(appliesTo = "PEAK_GU_PING_Time", comment = "峰谷平-电费配置")
@KeySequence(value = "PEAK_GU_PING_Time_ID")
@Data
@ApiModel(value = "峰谷平-电费配置")
public class PeakGuPingTime extends Model<PeakGuPingTime> {
    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "PEAK_GU_PING_Time_ID", sequenceName = "PEAK_GU_PING_Time_ID", allocationSize = 1)
    @GeneratedValue(generator = "PEAK_GU_PING_Time_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "电费配置id")
    private Integer id;
    /**
     * 主表ID
     */
    @Comment("主表ID")
    @ApiModelProperty(value = "主表ID")
    private String peakGuPingId;

    @Comment("起时点")
    @ApiModelProperty(value = "起时点")
    private String startTime;

    @Comment("止时点")
    @ApiModelProperty(value = "止时点")
    private String endTime;

    @Comment("类型")
    @ApiModelProperty(value = "0 高峰 1低估")
    private String type;

    /**
     * 基础电价
     */
    @Comment("基础电价")
    @ApiModelProperty(value = "基础电价")
    private String num;

    @Comment("创建人")
    private Integer createUser;
    @Comment("更新人")
    private Integer updateUser;
    @Comment("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @Comment("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    @Comment("是否删除 0:正常  1：删除")
    private Integer isDel;
}
