package com.jsdc.iotpt.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.dao.SysBuildAreaDao;
import com.jsdc.iotpt.model.sys.SysBuildArea;
import com.jsdc.iotpt.vo.SysBuildAreaVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface SysBuildAreaMapper extends BaseMapper<SysBuildArea> {

    @SelectProvider(type = SysBuildAreaDao.class, method = "getAllList")
    List<SysBuildArea> getAllList(SysBuildAreaVo vo);

    @SelectProvider(type = SysBuildAreaDao.class, method = "selectByIds")
    List<SysBuildArea> selectByIds(List<String> areaIds);
}
