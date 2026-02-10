package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 报告与商家关联关系表
 *
 */
@Entity
@TableName("REPORT_BUSINESS")
@Table(name = "REPORT_BUSINESS")
@org.hibernate.annotations.Table(appliesTo = "REPORT_BUSINESS", comment = "报告与商家关联关系表")
@KeySequence(value = "REPORT_BUSINESS_ID")
@Data
@ApiModel(value = "报告与商家关联关系表")
public class ReportBusiness implements Serializable {


    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "REPORT_BUSINESS_ID", sequenceName = "REPORT_BUSINESS_ID", allocationSize = 1)
    @GeneratedValue(generator = "REPORT_BUSINESS_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "ID")
    private Integer id;

    /**
     * 报告ID
     */
    @Comment("报告ID")
    @ApiModelProperty(value = "报告ID")
    private Integer reportId;

    /**
     * 商户ID
     */
    @Comment("商户ID")
    @ApiModelProperty(value = "商户ID")
    private Integer areaId;



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
