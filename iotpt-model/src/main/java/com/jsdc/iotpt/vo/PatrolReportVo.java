package com.jsdc.iotpt.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 巡更报告
 */
@Data
public class PatrolReportVo extends Model<PatrolReportVo> {

    @ApiModelProperty(value = "主键ID")
    private Integer id;
    @ApiModelProperty(value = "事件类型 数据字典（patrolReportType）")
    private String patrolReportType;
    @ApiModelProperty(value = "打卡时间")
    private String signTime;
    @ApiModelProperty(value = "说明")
    private String remarks;
    @ApiModelProperty(value = "是否删除 0:正常  1：删除")
    private Integer isDel;
}
