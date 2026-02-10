package com.jsdc.iotpt.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.dto.SysRoleMenuDto;
import com.jsdc.iotpt.mapper.SysRoleMapper;
import com.jsdc.iotpt.mapper.SysRoleMenuMapper;
import com.jsdc.iotpt.mapper.SysUserRoleMapper;
import com.jsdc.iotpt.model.SysRole;
import com.jsdc.iotpt.model.SysRoleMenu;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.SysUserRole;
import com.jsdc.iotpt.util.StringUtils;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @projectName: IOT
 * @className: SysRoleService
 * @author: wp
 * @description:
 * @date: 2023/5/8 16:51
 */
@Service
@Transactional
public class SysRoleService extends BaseService<SysRole> {

    @Autowired
    SysRoleMenuMapper roleMenuMapper;
    @Autowired
    SysRoleMenuService roleMenuService;
    @Autowired
    SysUserService userService;
    @Autowired
    SysRoleMapper roleMapper;
    @Autowired
    SysUserRoleMapper userRoleMapper;
    /**
     * 列表查询
     * @param role
     * @return
     */
    public List<SysRole> getRoleList(SysRole role){
        List<SysRole> roles = baseMapper.selectList(getWrapper(role));
        return roles;
    }

    /**
     * 分页查询
     * @param role
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public Page<SysRole> getRolePage(SysRole role, Integer pageIndex, Integer pageSize){
        Page<SysRole> p = baseMapper.selectPage(new Page<>(pageIndex, pageSize), getWrapper(role).orderByAsc(SysRole::getCreateTime));
        Map<Integer, SysUser> userMap = (Map<Integer, SysUser>) RedisUtils.getBeanValue("userDict");
        p.getRecords().forEach(x -> {
            int roleId = x.getId();
            List<SysRoleMenuDto> dtos = roleMenuMapper.getRoleMenus(roleId);
            List<String> menus = dtos.stream().filter(y -> (StringUtils.isNotNull(y))).map(SysRoleMenuDto::getTitle).collect(Collectors.toList());
            List<SysRoleMenu> sysRoleMenus = roleMenuMapper.selectList(Wrappers.<SysRoleMenu>lambdaQuery()
                    .eq(SysRoleMenu::getRoleId, roleId).eq(SysRoleMenu::getIsDel, G.ISDEL_NO));
            x.setMenus(menus);
            x.setMenuIds(sysRoleMenus.stream().distinct().map(SysRoleMenu::getMenuId).collect(Collectors.toList()));

            if (null != userMap){
                x.setCreateUserName(userMap.get(x.getCreateUser()) == null ? "" : userMap.get(x.getCreateUser()).getRealName());
                x.setUpdateUserName(userMap.get(x.getUpdateUser()) == null ? "" : userMap.get(x.getUpdateUser()).getRealName());
            }
        });
        return p;
    }

    /**
     * 查询角色信息
     * @param role
     * @return
     */
    public SysRole getRole(SysRole role){
        SysRole r = getOne(getWrapper(role));
        return r;
    }

    /**
     * 保存角色
     * @param role
     * @return
     */
    public Integer saveRole(SysRole role) {
        if(null != role && null != role.getId() && null != getById(role.getId())){
            return editRole(role);
        }else {
            List<SysRole> list = roleMapper.selectList(getWrapper(role));
            if(CollectionUtils.isEmpty(list)){
                return addRole(role);
            }else{
                return -2;
            }
        }
    }
    /**
     * 验证角色名是否重复
     */
    public void checkName(SysRole role){
        Long nameCount = roleMapper.selectCount(Wrappers.<SysRole>lambdaQuery()
                .eq(SysRole::getRoleName, role.getRoleName())
                .ne(null != role.getId(), SysRole::getId, role.getId())
                .eq(SysRole::getSystemId, role.getSystemId())
                .eq(SysRole::getIsDel,G.ISDEL_NO)
        );
        if (nameCount > 0){
            throw new RuntimeException("角色名重复");
        }
        Long flagCount = roleMapper.selectCount(Wrappers.<SysRole>lambdaQuery()
                .eq(SysRole::getRoleFlag, role.getRoleFlag())
                .ne(null != role.getId(), SysRole::getId, role.getId())
                .eq(SysRole::getSystemId, role.getSystemId())
                .eq(SysRole::getIsDel,G.ISDEL_NO)
        );
        if (flagCount > 0){
            throw new RuntimeException("标识名重复");
        }
    }

    /**
     * 新增角色
     * @param role
     * @return
     */
    public Integer addRole(@NonNull SysRole role){
        role.setCreateUser(userService.getUser().getId());
        role.setUpdateUser(userService.getUser().getId());
        role.setCreateTime(new Date());
        role.setUpdateTime(new Date());
        role.setIsDel(G.ISDEL_NO);
        if(save(role)){
            return roleMenuService.updateRoleMenu(role) ? 1 : -1;
        }
        return -1;
    }

    /**
     * 编辑角色
     * @param role
     * @return
     */
    public Integer editRole(@NonNull SysRole role){
        int id = role.getId();
        SysRole original = getById(id);
        BeanUtil.copyProperties(role, original, CopyOptions.create().ignoreNullValue().setIgnoreCase(true));
        original.setUpdateTime(new Date());
        original.setUpdateUser(userService.getUser().getId());
        if(updateById(original)){
            return roleMenuService.updateRoleMenu(role) ? 1 : -1;
        }
        return -1;
    }

    /**
     * 删除角色
     * @param role
     * @return
     */
    public Boolean delRole(SysRole role){
        int id = role.getId();
        SysRole original = getById(id);
        original.setIsDel(G.ISDEL_YES);
        original.setUpdateUser(userService.getUser().getId());
        original.setUpdateTime(new Date());
        return updateById(original);
    }
    /**
     * 封装查询条件
     * @param role
     * @return
     */
    public LambdaUpdateWrapper<SysRole> getWrapper(SysRole role){
        LambdaUpdateWrapper<SysRole> wrapper = new LambdaUpdateWrapper<>();
        if(null != role){
            wrapper.eq(null != role.getId(), SysRole::getId, role.getId());
            wrapper.like(StringUtils.isNotBlank(role.getRoleFlag()), SysRole::getRoleFlag, role.getRoleFlag());
            wrapper.eq(null != role.getSystemId(), SysRole::getSystemId, role.getSystemId());
            wrapper.like(StringUtils.isNotBlank(role.getRoleName()), SysRole::getRoleName, role.getRoleName());
        }
        wrapper.eq(SysRole::getIsDel, G.ISDEL_NO);
        return wrapper;
    }

    /**
     * 获取角色菜单
     * @param roleId
     * @return
     */
    public List<SysRoleMenu> getRoleMenu(Integer roleId) {
        SysRole role = getById(roleId);
        if (null == role){
            return null;
        }
        List<SysRoleMenu> dtos = roleMenuMapper.selectList(Wrappers.<SysRoleMenu>lambdaQuery()
                .eq(SysRoleMenu::getId, roleId).eq(SysRoleMenu::getIsDel, G.ISDEL_NO));
        return dtos;
    }

    /**
     * 根据角色获取用户
     * @param roleId
     * @return
     */
    public List<SysUser> getRoleUser(Integer roleId) {
        SysRole role = getById(roleId);
        if (null == role){
            return Collections.emptyList();
        }
        List<SysUserRole> users = userRoleMapper.selectList(Wrappers.<SysUserRole>lambdaQuery()
                .eq(SysUserRole::getRoleId, roleId).eq(SysUserRole::getIsDel, G.ISDEL_NO));
        if (CollectionUtils.isEmpty(users)){
            return Collections.emptyList();
        }
        List<Integer> userIds = users.stream().map(SysUserRole::getUserId).collect(Collectors.toList());
        List<SysUser> userList = userService.getBaseMapper().selectList(Wrappers.<SysUser>lambdaQuery()
                .in(SysUser::getId, userIds).eq(SysUser::getIsDel, G.ISDEL_NO));
        return userList;
    }

    /**
     * 根据角色code获取用户
     * @return
     */
    public List<SysUser> getRoleUserByCode(String roleCode) {
        SysRole role =
                this.getOne(Wrappers.lambdaQuery(SysRole.class).eq(SysRole::getRoleFlag, roleCode).eq(SysRole::getIsDel, G.ISDEL_NO));
        if (null == role){
            return Collections.emptyList();
        }
        List<SysUserRole> users = userRoleMapper.selectList(Wrappers.<SysUserRole>lambdaQuery()
                .eq(SysUserRole::getRoleId, role.getId()).eq(SysUserRole::getIsDel, G.ISDEL_NO));
        if (CollectionUtils.isEmpty(users)){
            return Collections.emptyList();
        }
        List<Integer> userIds = users.stream().map(SysUserRole::getUserId).collect(Collectors.toList());
        List<SysUser> userList = userService.getBaseMapper().selectList(Wrappers.<SysUser>lambdaQuery()
                .in(SysUser::getId, userIds).eq(SysUser::getIsDel, G.ISDEL_NO));
        return userList;
    }

    //获取角色标识
    public List<String> selectByRoleFlag(Integer userId) {
        return roleMapper.selectByRoleFlag(userId);
    }
}
