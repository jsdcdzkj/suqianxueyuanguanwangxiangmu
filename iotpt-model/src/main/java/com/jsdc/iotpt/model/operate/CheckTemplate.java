package com.jsdc.iotpt.model.operate;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @projectName: IOT
 * @className: CheckTemplate
 * @author: wp
 * @description: 检查项主表
 * @date: 2023/5/8 13:55
 */
@Entity
@TableName("CHECK_TEMPLATE")
@Table(name = "CHECK_TEMPLATE")
@KeySequence(value = "SEQ_CHECK_TEMPLATE_ID")
@Data
@ApiModel(value = "检查项主表")
public class CheckTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_CHECK_TEMPLATE_ID", sequenceName = "SEQ_CHECK_TEMPLATE_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_CHECK_TEMPLATE_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "模板id")
    private Integer id;

    /**
     * 检查项模板名称
     */
    @ApiModelProperty(value = "检查项模板名称")
    private String checkName;

    /**
     * 所属类型（字典表获取）
     */
    @ApiModelProperty(value = "所属类型")
    private String type;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String checkDesc;

    /**
     * 状态  0:否（禁用） 1:是（启用）
     */
    @ApiModelProperty(value = "状态")
    private Integer status;

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



    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<CheckTemplateSub> list = new ArrayList<>();

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String typeName;


}
