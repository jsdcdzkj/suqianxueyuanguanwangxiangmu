package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *  时间组
 */
@TableName("walk_times")
@Entity
@Table(name = "walk_times")
@Data
public class WalkTimes implements Serializable {

    /**
     * 自增id
     */
    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_WALK_TIMES_ID", sequenceName = "SEQ_WALK_TIMES_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_WALK_TIMES_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "主键id")
    private Integer id;

    // 时间组名称
    private String timesName;

    // 时间组开始时间
    private String startTime;

    // 时间组结束时间
    private String endTime;

    // 自然日
    private String days;

    // 时间组
    private String timeAry;

    // 说明
    private String remarks;

    // 创建者部门id
    private Integer deptId;

    // 状态
    private Integer isFlag;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    // 修改时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    // 逻辑位
    private Integer isDelete;



}
