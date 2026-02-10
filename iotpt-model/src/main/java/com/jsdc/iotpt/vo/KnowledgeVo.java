package com.jsdc.iotpt.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Transient;

@Data
public class KnowledgeVo {
    private Integer id;

    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "文档类型")
    private String type;
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "文档类型名称")
    private String typeName;
    @ApiModelProperty(value = "内容描述")
    private String count;
    @ApiModelProperty(value = "文档正文")
    private String text;
    @ApiModelProperty(value = "是否删除 0:正常  1：删除")
    private Integer isDel;
}
