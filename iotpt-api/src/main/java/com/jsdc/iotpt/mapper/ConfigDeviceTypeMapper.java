package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.ConfigDeviceTypeDao;
import com.jsdc.iotpt.model.ConfigDeviceType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface ConfigDeviceTypeMapper extends BaseMapper<ConfigDeviceType> {

    @SelectProvider(type = ConfigDeviceTypeDao.class, method = "getEntityList")
    Page<ConfigDeviceType> getEntityList(Page page, ConfigDeviceType bean);
}
