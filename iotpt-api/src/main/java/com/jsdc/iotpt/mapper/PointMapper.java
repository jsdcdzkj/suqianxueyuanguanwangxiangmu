package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.PointDao;
import com.jsdc.iotpt.model.Point;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface PointMapper extends BaseMapper<Point> {

    @SelectProvider(type = PointDao.class, method = "getList")
    Page<Point> getList(Page<Point> p, String name, Integer logicalBuildId, Integer logicalFloorId, Integer logicalAreaId);
}
