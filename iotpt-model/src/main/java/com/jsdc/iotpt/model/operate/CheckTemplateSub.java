package com.jsdc.iotpt.model.operate;

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
 * @projectName: IOT
 * @className: CheckTemplateSub
 * @author: wp
 * @description: 检查项子表
 * @date: 2023/5/8 13:55
 */
@Entity
@TableName("CHECK_TEMPLATE_SUB")
@Table(name = "CHECK_TEMPLATE_SUB")
@KeySequence(value = "SEQ_CHECK_TEMPLATE_SUB_ID")
@Data
@ApiModel(value = "检查项子表")
public class CheckTemplateSub implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_CHECK_TEMPLATE_SUB_ID", sequenceName = "SEQ_CHECK_TEMPLATE_SUB_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_CHECK_TEMPLATE_SUB_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "检查项子表id")
    private Integer id;

    @ApiModelProperty(value = "检查项模板ID")
    private Integer checkId;

    /**
     * 不用了 别用这个字段
     */
    @ApiModelProperty(value = "分项检查项名称")
    private String subName;


    /**
     * 设备类型
     * 关联设备类型管理表（config_device_type）
     */
    @ApiModelProperty(value = "设备类型")
    private Integer deviceType;

    @ApiModelProperty(value = "分项检查项内容")
    private String subContent;


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


}
