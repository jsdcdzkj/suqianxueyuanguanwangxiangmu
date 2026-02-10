package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.dao.PatrolDeviceOrPointDao;
import com.jsdc.iotpt.dao.PatrolPeopleDao;
import com.jsdc.iotpt.model.PatrolDeviceOrPoint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface PatrolDeviceOrPointMapper extends BaseMapper<PatrolDeviceOrPoint> {


    @SelectProvider(method = "getList",type = PatrolDeviceOrPointDao.class)
    List<PatrolDeviceOrPoint> getList(PatrolDeviceOrPoint patrolDeviceOrPoint);
}
