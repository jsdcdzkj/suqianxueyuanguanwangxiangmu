package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.dto.SysRoleMenuDto;
import com.jsdc.iotpt.mapper.SysMenuMapper;
import com.jsdc.iotpt.mapper.SysRoleMenuMapper;
import com.jsdc.iotpt.model.SysMenu;
import com.jsdc.iotpt.model.SysRole;
import com.jsdc.iotpt.model.SysRoleMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @projectName: IOT
 * @className: SysRoleMenuService
 * @author: wp
 * @description:
 * @date: 2023/5/10 9:43
 */
@Service
@Transactional
public class SysRoleMenuService extends BaseService<SysRoleMenu> {

    @Autowired
    private SysRoleMenuMapper mapper;

    @Autowired
    private SysMenuMapper menuMapper;

    public List<SysRoleMenuDto> getRoleMenus(Integer roleId){
        return mapper.getRoleMenus(roleId);
    }

    public Boolean updateRoleMenu(SysRole role){
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, role.getId());
        mapper.delete(wrapper);
        if (role.getMenuIds() == null || role.getMenuIds().size() == 0){
            return true;
        }
        List<Integer> menuIds = role.getMenuIds();
        for(Integer menuId : menuIds){
            SysMenu a = menuMapper.selectById(menuId);
            if (null == a){
                continue;
            }
            if (null != a.getParentId() && !menuIds.contains(a.getParentId())){
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setMenuId(a.getParentId());
                roleMenu.setRoleId(role.getId());
                roleMenu.setIsDel(G.ISDEL_NO);
                roleMenu.insert();
            }
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setMenuId(menuId);
            roleMenu.setRoleId(role.getId());
            roleMenu.setIsDel(G.ISDEL_NO);
            roleMenu.insert();
        }
        return true;
    }
}
