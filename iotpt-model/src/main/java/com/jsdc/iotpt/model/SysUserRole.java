package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @projectName: IOT
 * @className: SysUserRole
 * @author: wp
 * @description: 用户角色关联表
 * @date: 2023/5/8 16:51
 */
@Entity
@TableName("SYS_USER_ROLE")
@Table(name = "SYS_USER_ROLE")
@KeySequence(value = "SEQ_SYS_USER_ROLE_ID")
@Data
public class SysUserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_SYS_USER_ROLE_ID", sequenceName = "SEQ_SYS_USER_ROLE_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_SYS_USER_ROLE_ID", strategy = GenerationType.SEQUENCE)
    private Integer id;

    private Integer userId;

    private Integer RoleId;

    private Integer isDel;
}
