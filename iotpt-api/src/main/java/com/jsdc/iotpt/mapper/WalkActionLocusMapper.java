package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.dao.WalkActionLocusDao;
import com.jsdc.iotpt.model.WalkActionLocus;
import com.jsdc.iotpt.vo.WalkActionLocusVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;


@Mapper
public interface WalkActionLocusMapper extends BaseMapper<WalkActionLocus> {


    @SelectProvider(type = WalkActionLocusDao.class, method = "getActionsById")
    List<WalkActionLocus> getActionsById(String faceId, String time, Integer floor);

    @SelectProvider(type = WalkActionLocusDao.class, method = "getActionsByTime")
    List<WalkActionLocus> getActionsByTime(String faceId, String startTime, String endTime);

    @SelectProvider(type = WalkActionLocusDao.class, method = "signInAndOut")
    List<WalkActionLocusVo> signInAndOut(String realName, String createTime, Integer isFlag, Integer deptId, String turnstileType);

    @SelectProvider(type = WalkActionLocusDao.class, method = "signInAndOut2")
    List<WalkActionLocusVo> signInAndOut2(String createTime, Integer isFlag, Integer deptId, String turnstileType, Integer floor);

    @SelectProvider(type = WalkActionLocusDao.class, method = "selectToDayById")
    List<WalkActionLocus> selectToDayById(Integer id);
}
