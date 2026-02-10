package com.jsdc.iotpt.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.dao.ConfigDeviceSignalMapDao;
import com.jsdc.iotpt.dao.DataSheetDao;
import com.jsdc.iotpt.model.AlertSheet;
import com.jsdc.iotpt.model.DataSheet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;


@Mapper
public interface DataSheetMapper extends BaseMapper<DataSheet> {


    //用电量评价
    @SelectProvider(type = DataSheetDao.class, method = "energyEvaluate")
     String energyEvaluate(Integer type);

    //用电量评价分楼层
    @SelectProvider(type = DataSheetDao.class, method = "energyEvaluateByFloor")
    List<Map<String,Object>> energyEvaluateByFloor(Integer type);

    //用电量评价分楼层
    @SelectProvider(type = DataSheetDao.class, method = "getEnergyCount")
    List<String> getEnergyCount();

    //各楼层水电对比
    @SelectProvider(type = DataSheetDao.class, method = "waterElectricityByFloor")
    List<Map<String,Object>> waterElectricityByFloor(Integer timeType,String type);
}
