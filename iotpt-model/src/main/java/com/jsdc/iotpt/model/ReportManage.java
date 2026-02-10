package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 报告管理
 *
 */
@Entity
@TableName("REPORT_MANAGE")
@Table(name = "REPORT_MANAGE")
@org.hibernate.annotations.Table(appliesTo = "REPORT_MANAGE", comment = "报告管理")
@KeySequence(value = "REPORT_MANAGE_ID")
@Data
@ApiModel(value = "报告管理")
public class ReportManage  implements Serializable {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "REPORT_MANAGE_ID", sequenceName = "REPORT_MANAGE_ID", allocationSize = 1)
    @GeneratedValue(generator = "REPORT_MANAGE_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "报告ID")
    private Integer id;


    /**
     * 报告名称
     */
    @Comment("报告名称")
    @ApiModelProperty(value = "报告名称")
    private String reportName;


    /**
     * 开始日期
     */
    @Comment("开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty(value = "开始日期")
    private Date reportStarTime;


    /**
     * 结束日期
     */
    @Comment("结束日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty(value = "结束日期")
    private Date reportEndTime;

    /**
     * 选择类型（年，月，日）
     */
    @Comment("类型")
    @ApiModelProperty(value = "0 年 1月 2日")
    private Integer reportType;

    /**
     * 选择的模板
     */
    @Comment("选择的模板")
    @ApiModelProperty(value = "选择的模板")
    private Integer chooseTemple;

    /**
     * 对应的文件路径
     */
    @Comment("对应的文件路径")
    @ApiModelProperty(value = "对应的文件路径")
    private String minioFile;


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


    /**
     * 查询条件创建人
     */
    @Transient
    @TableField(exist = false)
    private String createName;

    //选择模板的类型的名字
    @Transient
    @TableField(exist = false)
    private String chooseTempleName;

    /**
     * 选择的商户Id集合
     */
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "商户ids")
    private List<Integer> areaIds = new ArrayList<>();

    @Transient
    @TableField(exist = false)
    private String startTime;


    @Transient
    @TableField(exist = false)
    private String endTime;

    @Transient
    @TableField(exist = false)
    private String redioVal;//统计报表用 日月周年


}
