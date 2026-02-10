package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.ConfigTenantNoticeDao;
import com.jsdc.iotpt.model.ConfigTenantNotice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface ConfigTenantNoticeMapper extends BaseMapper<ConfigTenantNotice> {

    @SelectProvider(type = ConfigTenantNoticeDao.class, method = "getEntityList")
    Page<ConfigTenantNotice> getEntityList(Page page, ConfigTenantNotice bean);
}
