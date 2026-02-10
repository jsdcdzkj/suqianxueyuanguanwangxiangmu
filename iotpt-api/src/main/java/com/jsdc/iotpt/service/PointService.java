package com.jsdc.iotpt.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.mapper.PointMapper;
import com.jsdc.iotpt.model.Point;
import com.jsdc.iotpt.model.sys.SysBuildArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;


@Service
@Transactional
public class PointService extends BaseService<Point> {
    @Autowired
    private PointMapper pointMapper;

    public Page<Point> getList(Integer pageIndex, Integer pageSize, String name, Integer logicalBuildId, Integer logicalFloorId, Integer logicalAreaId) {
        return pointMapper.getList(new Page<>(pageIndex, pageSize), name, logicalBuildId, logicalFloorId, logicalAreaId);
    }
}
