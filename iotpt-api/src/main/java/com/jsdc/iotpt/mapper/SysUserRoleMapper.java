package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.dao.SysUserRoleDao;
import com.jsdc.iotpt.dto.SysUserRoleDto;
import com.jsdc.iotpt.model.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    @SelectProvider(type = SysUserRoleDao.class, method = "getUserRole")
    List<SysUserRoleDto> getUserRole(Integer userId);

    @SelectProvider(type= SysUserRoleDao.class,method = "getRoleSymbolByUser")
    List<String> getRoleSymbolByUser(String userId);
}
