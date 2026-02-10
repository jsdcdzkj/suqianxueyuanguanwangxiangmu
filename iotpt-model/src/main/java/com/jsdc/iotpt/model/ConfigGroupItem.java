package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.jsdc.iotpt.model.sys.SysOrgDept;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

/**
 * ClassName: ConfigGroupItem
 * Description:
 * date: 2024/2/8 14:11
 *
 * @author bn
 */
@Entity
@TableName("config_group_item")
@Table(name = "config_group_item")
@KeySequence(value = "SEQ_CONFIG_GROUP_ITEM_ID")
@Data
@ApiModel(value = "分组子项")
public class ConfigGroupItem {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "configGroupItemId", sequenceName = "SEQ_CONFIG_GROUP_ITEM_ID", allocationSize = 1)
    @GeneratedValue(generator = "configGroupItemId", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "分组管理id")
    private Integer id;


    private Integer groupId;

    // 单位id
    private Integer orgId;

    @Transient
    @TableField(exist = false)
    private SysOrgDept dept;

    private Integer deptId;

    @Transient
    @TableField(exist = false)
    private SysUser sysUser;

    private Integer userId;


    private Integer isDel;



}
