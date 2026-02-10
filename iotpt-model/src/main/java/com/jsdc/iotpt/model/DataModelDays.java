package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 大数据模型数据
 * 每日生成数据
 */
@Entity
@TableName("DATA_MODEL_DAYS")
@Table(name = "DATA_MODEL_DAYS")
@KeySequence(value = "SEQ_DATA_MODEL_DAYS_ID")
@Data
@ApiModel(value = "大数据模型数据")
public class DataModelDays implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_DATA_MODEL_DAYS_ID", sequenceName = "SEQ_DATA_MODEL_DAYS_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_DATA_MODEL_DAYS_ID", strategy = GenerationType.SEQUENCE)
    private Integer id;

    /**
     * 日期
     */
    @ApiModelProperty(value = "日期")
    private String time;

    /**
     * 电量
     */
    @ApiModelProperty(value = "电量")
    public String powerVal;

    /**
     * 季节
     */
    @ApiModelProperty(value = "季节")
    private String season;

    /**
     * 室内温度
     */
    private String inTemp;

    /**
     * 室外温度
     */
    private String outTemp;

    /**
     * 工作时长 小时
     */
    private String workingHours;

    private String userCounts;

    /**
     * 工作日 0:非工作日 1:工作日
     */
    private String isWeekday;

    /**
     * 会议预约总时长
     */
    private String meeting_reservation_duration;

    /**
     * 包间预定次数
     */
    private Integer room_reserve_count;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 是否删除 0:正常  1：删除
     */
    private Integer isDel;
}
