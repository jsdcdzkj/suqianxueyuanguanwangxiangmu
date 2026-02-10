package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.WarningDeviceMapDao;
import com.jsdc.iotpt.model.WarningDeviceMap;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface WarningDeviceMapMapper extends BaseMapper<WarningDeviceMap> {

    @SelectProvider(type = WarningDeviceMapDao.class, method = "getEntityList")
    Page<WarningDeviceMap> getEntityList(Page page, WarningDeviceMap bean);
}
