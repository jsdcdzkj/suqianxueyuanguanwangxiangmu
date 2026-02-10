package com.jsdc.iotpt.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.dao.SysBuildFloorDao;
import com.jsdc.iotpt.model.sys.SysBuildFloor;
import com.jsdc.iotpt.vo.SysBuildFloorVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface SysBuildFloorMapper extends BaseMapper<SysBuildFloor> {
    @SelectProvider(type = SysBuildFloorDao.class, method = "selectFloorOrder")
    List<SysBuildFloorVo> selectFloorOrder();
}
