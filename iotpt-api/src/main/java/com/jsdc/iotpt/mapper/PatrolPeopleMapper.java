package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.dao.PatrolPeopleDao;
import com.jsdc.iotpt.model.PatrolPeople;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface PatrolPeopleMapper extends BaseMapper<PatrolPeople> {
    @SelectProvider(method = "getList",type = PatrolPeopleDao.class)
    List<PatrolPeople> getList(PatrolPeople patrolPeople);
}
