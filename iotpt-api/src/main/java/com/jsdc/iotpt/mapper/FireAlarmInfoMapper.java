package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.FireAlarmInfoDao;
import com.jsdc.iotpt.model.new_alarm.FireAlarmInfo;
import com.jsdc.iotpt.vo.FireAlarmInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;

@Mapper
public interface FireAlarmInfoMapper extends BaseMapper<FireAlarmInfo> {

    @SelectProvider(type = FireAlarmInfoDao.class, method = "getEntityList")
    Page<FireAlarmInfoVo> getEntityList(Page page, FireAlarmInfoVo vo);

    @SelectProvider(type = FireAlarmInfoDao.class, method = "export")
    List<FireAlarmInfoVo> export(FireAlarmInfoVo vo);

    @SelectProvider(type = FireAlarmInfoDao.class, method = "lineM")
    List<Map<String,Object>> lineM(FireAlarmInfoVo vo);

    @SelectProvider(type = FireAlarmInfoDao.class, method = "lineY")
    List<Map<String,Object>> lineY(FireAlarmInfoVo vo);

    @SelectProvider(type = FireAlarmInfoDao.class, method = "column")
    Integer column(FireAlarmInfoVo vo,Integer type,Integer isAll);

//    @SelectProvider(type = FireAlarmInfoDao.class, method = "columnF")
//    Integer columnF(FireAlarmInfoVo vo,Integer type);
}
