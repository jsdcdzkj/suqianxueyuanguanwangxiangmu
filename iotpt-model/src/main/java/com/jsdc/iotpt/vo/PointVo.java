package com.jsdc.iotpt.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import springfox.documentation.annotations.ApiIgnore;

import javax.persistence.*;
import java.util.Date;


/**
 * 点位配置
 */
@Data
@ApiModel(value = "点位配置")
public class PointVo extends Model<PointVo> {


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
    @ApiModelProperty(value = "时长")
    private String duration;

    @ApiModelProperty(value = "真实姓名")
    private String realName;


    // 计划名称
    @ApiModelProperty(value = "计划名称")
    private String planName;

    // 计划类型
    @ApiModelProperty(value = "计划类型 dictType:planType")
    private String planType;

    // 计划状态 0:开启 1 停用
    @ApiModelProperty(value = "计划状态 0:开启 1 停用")
    private Integer planStatus;

    // 表达式
    @ApiModelProperty(value = "表达式")
    private String cronExpression;

    // 执行类型 0:单次 1:循环
    @ApiModelProperty(value = "执行类型 0:单次 1:循环")
    private Integer executeType;

    // 执行周期 0:日 1:月 2:周
    @ApiModelProperty(value = "执行周期 0:日 1:月 2:周")
    private Integer planCycle;

    // 执行开始时间 单次使用yyyy-MM-dd HH:mm:ss
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "执行开始时间 单次使用yyyy-MM-dd HH:mm:ss")
    private Date cycleStartTime;

    //执行开始时间 单次使用yyyy-MM-dd HH:mm:ss
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "执行结束时间 单次使用yyyy-MM-dd HH:mm:ss")
    private Date cycleEndTime;

    // 执行日期 单次 空  多次 1,2,3.。。。。。
    @ApiModelProperty(value = "执行日期 单次 空  多次 1,2,3.。。。。。")
    private String cycleDate;

    // 计划描述
    @ApiModelProperty(value = "计划描述")
    private String planRemark;

    // 打卡次数
    @ApiModelProperty(value = "打卡次数")
    private Integer clockInCount;
}
