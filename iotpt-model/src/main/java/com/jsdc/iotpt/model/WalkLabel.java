package com.jsdc.iotpt.model;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 标签表
 */
@TableName("walk_label")
@Entity
@Table(name = "walk_label")
@Data
public class WalkLabel implements Serializable {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_WALK_LABEL_ID", sequenceName = "SEQ_WALK_LABEL_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_WALK_LABEL_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "主键id")
    private Integer id;

    // 标签名称
    private String labelName;

    // 标签代码
    private String labelCode;

    //是否预警 0否 1是
    private Integer isWarn;

    //所属机构
    private Integer deptId;

    // 逻辑删除
    private Integer isDelete;
}
