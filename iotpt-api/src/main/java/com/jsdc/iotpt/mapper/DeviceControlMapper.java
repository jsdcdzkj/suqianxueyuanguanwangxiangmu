package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.dao.DeviceControlDao;
import com.jsdc.iotpt.model.ConfigSignalType;
import com.jsdc.iotpt.model.DeviceCollect;
import com.jsdc.iotpt.model.DeviceControl;
import com.jsdc.iotpt.vo.DeviceControlVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface DeviceControlMapper extends BaseMapper<DeviceControl> {

    @SelectProvider(type = DeviceControlDao.class,method = "getSignalByDeviceTypeId")
    List<ConfigSignalType> getSignalByDeviceTypeId(Integer deviceTypeId);

    @SelectProvider(type = DeviceControlDao.class,method = "getList")
    List<DeviceControlVo> getList(DeviceControl control);
}
