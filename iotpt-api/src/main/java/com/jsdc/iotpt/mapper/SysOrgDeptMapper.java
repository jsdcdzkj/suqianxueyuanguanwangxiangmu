package com.jsdc.iotpt.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.SysOrgDeptDao;
import com.jsdc.iotpt.model.sys.SysOrgDept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface SysOrgDeptMapper extends BaseMapper<SysOrgDept> {

    @SelectProvider(type = SysOrgDeptDao.class, method = "getList")
    Page<SysOrgDept> getList(Page<SysOrgDept> page, SysOrgDept sysOrgDept, String lettersCommaSeparated);

    @SelectProvider(type = SysOrgDeptDao.class, method = "getList2")
    List<SysOrgDept> getList2(SysOrgDept sysOrgDept, String lettersCommaSeparated);


    @SelectProvider(type = SysOrgDeptDao.class, method = "exist")
    Integer exist(String id);


}
