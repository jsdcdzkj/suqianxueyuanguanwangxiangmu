package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.dao.SysUserDao;
import com.jsdc.iotpt.model.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    @SelectProvider(type = SysUserDao.class, method = "selectIsDirector")
    SysUser selectIsDirector(Integer userId);

    @SelectProvider(type = SysUserDao.class, method = "selectUserPhone")
    List<SysUser> selectUserPhone();

    @SelectProvider(type = SysUserDao.class, method = "getUsersByRole")
    List<SysUser> getUsersByRole(String administration);

    @SelectProvider(type = SysUserDao.class, method = "getApproveUser")
    List<SysUser> getApproveUser(String componyId,String roleFlag);

}
