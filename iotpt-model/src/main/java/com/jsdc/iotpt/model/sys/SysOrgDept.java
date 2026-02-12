package com.jsdc.iotpt.model.sys;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jsdc.iotpt.model.SysUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 组织部门表(sys_org_dept)
 */
@Entity
@TableName("sys_org_dept")
@Table(name = "sys_org_dept")
@KeySequence(value = "SEQ_SYS_ORG_DEPT_ID")
@Data
public class SysOrgDept extends Model<SysOrgDept> {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "orgDeptId", sequenceName = "SEQ_SYS_ORG_DEPT_ID", allocationSize = 1)
    @GeneratedValue(generator = "orgDeptId", strategy = GenerationType.SEQUENCE)
    private Integer id;

    //部门名称
    private String deptName;
    //联系人
    private String manager;
    //电话
    private String phone;
    // 手机电话
    private String telephone;
    //部门位置
    private String address;
    // 邮箱
    private String email;

    //备注信息
    private String memo;
    //父级主键 父级0
    private Integer parentId;

    //包含区域
    private String ids;
    //a华部门id
    private Long dahuaDeptId;


    @Transient
    @TableField(exist = false)
    private List<SysOrgDept> children;

    @Transient
    @TableField(exist = false)
    private List<SysUser> sysUserList;


    //所属组织
    @ApiModelProperty(value = "所属组织")
    private Integer orgId;

    @Transient
    @TableField(exist = false)
    private String orgName;


    @Transient
    @TableField(exist = false)
    private Integer pageNo;

    @Transient
    @TableField(exist = false)
    private Integer pageSize;


    @Transient
    @TableField(exist = false)
    private Integer areaId;


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

    @Transient
    @TableField(exist = false)
    private List<List<Object>> showData;

    /**
     * 部门人数
     */
    @Transient
    @TableField(exist = false)
    private Long peopleCount;

    @Transient
    @TableField(exist = false)
    private Integer anfang;
    @Transient
    @TableField(exist = false)
    private Integer xiaofang;
    @Transient
    @TableField(exist = false)
    private Integer shebei;

    @Transient
    @TableField(exist = false)
    private List<Integer> visitorAreaIds;

    @Transient
    @TableField(exist = false)
    private String participantsNum; // 问卷下发人数
    @Transient
    @TableField(exist = false)
    private String answerNum; // 问卷答题人数

    @Transient
    @TableField(exist = false)
    private String recyclingRate; // 问卷回收率


}
