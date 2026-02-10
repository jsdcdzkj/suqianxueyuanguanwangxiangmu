package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.ConfigDeviceSignalMapDao;
import com.jsdc.iotpt.dao.ConfigDeviceSubitemDao;
import com.jsdc.iotpt.model.ConfigDeviceSignalMap;
import com.jsdc.iotpt.model.ConfigDeviceSubitem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface ConfigDeviceSubitemMapper extends BaseMapper<ConfigDeviceSubitem> {

    @SelectProvider(type = ConfigDeviceSubitemDao.class, method = "getSubValue")
    List<ConfigDeviceSubitem> getSubValue(String year);

    @SelectProvider(type = ConfigDeviceSubitemDao.class, method = "existenceOrNot")
    List<ConfigDeviceSubitem> existenceOrNot(String year);


}
