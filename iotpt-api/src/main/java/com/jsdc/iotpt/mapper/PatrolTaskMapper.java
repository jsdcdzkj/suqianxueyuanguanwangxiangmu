package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.dao.PatrolTaskDao;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.PatrolTask;
import com.jsdc.iotpt.vo.PatrolTaskVideoVo;
import com.jsdc.iotpt.vo.PatrolTaskVo;
import com.jsdc.iotpt.vo.PointVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface PatrolTaskMapper extends BaseMapper<PatrolTask> {
    @SelectProvider(type = PatrolTaskDao.class, method = "getPatrolTaskList")
    Page<PatrolTask> getPatrolTaskList(Page page, PatrolTaskVo patrolTaskVo);

    @SelectProvider(type = PatrolTaskDao.class, method = "getVideo")
    List<PatrolTaskVideoVo> getVideo(String id);

    @SelectProvider(type = PatrolTaskDao.class, method = "getPoint")
    List<PointVo> getPoint(String id);
}
