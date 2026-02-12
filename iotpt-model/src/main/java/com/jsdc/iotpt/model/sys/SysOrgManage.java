package com.jsdc.iotpt.model.sys;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 组织机构表(sys_org_manage)
 */
@Entity
@TableName("sys_org_manage")
@Table(name = "sys_org_manage")
@KeySequence(value = "SEQ_SYS_ORG_MANAGE_ID")
@Data
public class SysOrgManage extends Model<SysOrgManage> {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "orgManageId", sequenceName = "SEQ_SYS_ORG_MANAGE_ID", allocationSize = 1)
    @GeneratedValue(generator = "orgManageId", strategy = GenerationType.SEQUENCE)
    private Integer id;

    //组织名称
    private String orgName;


    //父级主键 父级0
    private Integer parentId;
    //地址
    private String address;
    //负责人
    private String manager;
    //联系电话
    private String phone;
    // 手机电话
    private String telephone;
    //描述
    private String memo;

    // 邮箱
    private String email;

    //大华单位id
    private String iccId;

    //部门所属区域
    @Transient
    @TableField(exist = false)
    private String areaName;

    //是否是租户 0否1是
    private String isItATenant;


    @Transient
    @TableField(exist = false)
    private List<SysOrgManage> children;


    @Transient
    @TableField(exist = false)
    private List<SysOrgDept> deptList;

    @Transient
    @TableField(exist = false)
    private Integer pageNo;

    @Transient
    @TableField(exist = false)
    private Integer pageSize;

    @Transient
    @TableField(exist = false)
    private String label;

    /**
     * 创建人id
     */
    private Integer createUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;


    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 更新人id
     */
    private Integer updateUser;

    /**
     * 是否删除 0:正常  1：删除
     */
    private Integer isDel;
}
