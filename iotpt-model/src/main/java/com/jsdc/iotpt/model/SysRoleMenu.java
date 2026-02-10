package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @projectName: IOT
 * @className: SysRoleMenu
 * @author: wp
 * @description: 角色菜单表
 * @date: 2023/5/10 9:30
 */
@Entity
@TableName("SYS_ROLE_MENU")
@Table(name = "SYS_ROLE_MENU")
@KeySequence(value = "SEQ_SYS_ROLE_MENU_ID")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "角色菜单")
public class SysRoleMenu extends Model<SysRoleMenu> {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_SYS_ROLE_MENU_ID", sequenceName = "SEQ_SYS_ROLE_MENU_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_SYS_ROLE_MENU_ID", strategy = GenerationType.SEQUENCE)
    private Integer id;

    private Integer roleId;

    private Integer menuId;

    private Integer isDel;
}
