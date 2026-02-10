package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.FalseAlarmRecordsDao;
import com.jsdc.iotpt.model.new_alarm.FalseAlarmRecords;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface FalseAlarmRecordsMapper extends BaseMapper<FalseAlarmRecords> {

    @SelectProvider(type = FalseAlarmRecordsDao.class, method = "getEntityList")
    Page<FalseAlarmRecords> getEntityList(Page page, FalseAlarmRecords bean);
}
