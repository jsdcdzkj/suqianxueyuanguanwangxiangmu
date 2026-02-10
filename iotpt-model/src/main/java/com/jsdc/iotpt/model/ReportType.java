package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@TableName("REPORT_TYPE")
@Table(name = "REPORT_TYPE")
@KeySequence(value = "SEQ_REPORT_TYPE_ID")
@Data
@ApiModel(value = "报告类型")
public class ReportType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_REPORT_TYPE_ID", sequenceName = "SEQ_REPORT_TYPE_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_REPORT_TYPE_ID", strategy = GenerationType.SEQUENCE)
    private Integer id;
    @ApiModelProperty(value = "父类id")
    private Integer parentId;
    @ApiModelProperty(value = "名称")
    private String typeName;
    @ApiModelProperty(value = "模型类型 1、单  2、双")
    private Integer modelType;
    @ApiModelProperty(value = "是否删除 0:正常  1：删除")
    private Integer isDel;
    @ApiModelProperty(value = "排序")
    private Integer sort;
    @Transient
    @TableField(exist = false)
    private String appendId;

    @Transient
    @TableField(exist = false)
    private String appendParentId;

}
