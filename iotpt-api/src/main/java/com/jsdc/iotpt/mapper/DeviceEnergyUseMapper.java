package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.AbnormalPatrolDao;
import com.jsdc.iotpt.model.DeviceEnergyUse;
import com.jsdc.iotpt.model.operate.AbnormalPatrol;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface DeviceEnergyUseMapper extends BaseMapper<DeviceEnergyUse> {

}
