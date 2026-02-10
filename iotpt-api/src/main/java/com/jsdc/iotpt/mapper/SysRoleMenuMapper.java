package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.dao.SysRoleMenuDao;
import com.jsdc.iotpt.dto.SysRoleMenuDto;
import com.jsdc.iotpt.model.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    @SelectProvider(type = SysRoleMenuDao.class, method = "getRoleMenus")
    List<SysRoleMenuDto> getRoleMenus(Integer roleId);
}
