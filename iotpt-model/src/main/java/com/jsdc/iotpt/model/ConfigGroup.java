package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jsdc.iotpt.model.sys.SysOrgDept;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * ClassName: ConfigGroup
 * Description: 分组管理
 * date: 2024/2/8 11:46
 *
 * @author bn
 */
@Entity
@TableName("config_group")
@Table(name = "config_group")
@KeySequence(value = "SEQ_CONFIG_GROUP_ID")
@Data
@ApiModel(value = "连接管理")
public class ConfigGroup {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "configGroupId", sequenceName = "SEQ_CONFIG_GROUP_ID", allocationSize = 1)
    @GeneratedValue(generator = "configGroupId", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "分组管理id")
    private Integer id;

    // 分组名称
    private String groupName;

    // 分组方式 0.人员 1.部门 2.人员+部门
    private Integer groupWay;

    // 备注
    private String remark;

    // 删除标志
    private Integer isDel;


    @Transient
    @TableField(exist = false)
    private List<ConfigGroupItem> itemList;


    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;


    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private Integer updateUser;

    private Integer createUser;



}
