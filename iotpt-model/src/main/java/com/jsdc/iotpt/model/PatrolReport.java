package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 巡更报告
 */
@Entity
@TableName("PATROL_REPORT")
@Table(name = "PATROL_REPORT")
@KeySequence(value = "PATROL_REPORT_ID")
@Data
public class PatrolReport extends Model<PatrolReport> {


    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "patrolReportId", sequenceName = "PATROL_REPORT_ID", allocationSize = 1)
    @GeneratedValue(generator = "patrolReportId", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "任务ID")
    private Integer patrolTaskId;

    @ApiModelProperty(value = "设备ID")
    private Integer deviceId;

    @ApiModelProperty(value = "事件类型 数据字典（patrolReportType）")
    private String patrolReportType;
    @ApiModelProperty(value = "说明")
    private String remarks;

    @ApiModelProperty(value = "上报人员")
    private Integer userId;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "上报图片列表")
    private List<SysFile> fileList;

}
