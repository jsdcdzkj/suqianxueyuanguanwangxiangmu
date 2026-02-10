package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 知识库
 */
@Entity
@TableName("KNOWLEDGE")
@Table(name = "KNOWLEDGE")
@KeySequence(value = "KNOWLEDGE_ID")
@Data
public class Knowledge {
    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "KNOWLEDGE_ID", sequenceName = "KNOWLEDGE_ID", allocationSize = 1)
    @GeneratedValue(generator = "KNOWLEDGE_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "知识库id")
    private Integer id;

    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "文档类型")
    private String type;
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "文档类型名称")
    private String typeName;
    @Column(columnDefinition="VARCHAR2(4000)")
    @ApiModelProperty(value = "内容描述")
    private String count;
    @Column(columnDefinition="VARCHAR2(4000)")
    @ApiModelProperty(value = "文档正文")
    private String text;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @ApiModelProperty(value = "创建人id")
    private Integer createUser;
    @ApiModelProperty(value = "创建人姓名")
    private String createUserName;
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    @ApiModelProperty(value = "更新人id")
    private Integer updateUser;
    @ApiModelProperty(value = "更新人姓名")
    private String updateUserName;
    @ApiModelProperty(value = "是否删除 0:正常  1：删除")
    private Integer isDel;
}
