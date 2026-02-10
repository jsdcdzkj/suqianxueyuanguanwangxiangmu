package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.DeviceAccessControlDao;
import com.jsdc.iotpt.model.DeviceAccessControl;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface DeviceAccessControlMapper extends BaseMapper<DeviceAccessControl> {

    @SelectProvider(type = DeviceAccessControlDao.class, method = "getEntityList")
    Page<DeviceAccessControl> getEntityList(Page page, DeviceAccessControl bean);
}
