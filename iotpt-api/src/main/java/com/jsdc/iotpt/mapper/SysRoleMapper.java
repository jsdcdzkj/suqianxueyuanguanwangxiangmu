package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.dao.SysRoleDao;
import com.jsdc.iotpt.model.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    //获取角色标识
    @SelectProvider(type = SysRoleDao.class, method = "selectByRoleFlag")
    List<String> selectByRoleFlag(Integer userId);
}
