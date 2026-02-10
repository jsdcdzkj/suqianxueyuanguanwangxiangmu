package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.ConfigDeviceSignalMapDao;
import com.jsdc.iotpt.model.ConfigDeviceSignalMap;
import com.jsdc.iotpt.model.ConfigDeviceType;
import com.jsdc.iotpt.model.ConfigSignalType;
import com.jsdc.iotpt.vo.ConfigDeviceSignalMapVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface ConfigDeviceSignalMapMapper extends BaseMapper<ConfigDeviceSignalMap> {

    @SelectProvider(type = ConfigDeviceSignalMapDao.class, method = "getEntityList")
    Page<ConfigDeviceSignalMap> getEntityList(Page page, ConfigDeviceSignalMap bean);

    @SelectProvider(type = ConfigDeviceSignalMapDao.class, method = "getEntityByTId")
    List<ConfigDeviceSignalMapVo> getEntityByTId(Integer id);

    @SelectProvider(type = ConfigDeviceSignalMapDao.class, method = "getEntityByTIdGroup")
    List<ConfigDeviceSignalMapVo> getEntityByTIdGroup(Integer id);


    @SelectProvider(type = ConfigDeviceSignalMapDao.class, method = "getEntityByTCode")
    List<ConfigSignalType> getEntityByTCode(ConfigDeviceType bean);

    @SelectProvider(type = ConfigDeviceSignalMapDao.class, method = "getSignalListByDeviceCode")
    List<ConfigSignalType> getSignalListByDeviceCode(Integer deviceCode);
}
