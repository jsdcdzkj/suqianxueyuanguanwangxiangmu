package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.AppVersionInfoDao;
import com.jsdc.iotpt.model.AppVersionInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface AppVersionInfoMapper extends BaseMapper<AppVersionInfo> {

    @SelectProvider(type = AppVersionInfoDao.class, method = "getEntityList")
    Page<AppVersionInfo> getEntityList(Page page, AppVersionInfo bean);

    @SelectProvider(type = AppVersionInfoDao.class, method = "getLastOne")
    AppVersionInfo getLastOne();
}
