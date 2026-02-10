package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.AlarmTypeConfigDao;
import com.jsdc.iotpt.model.new_alarm.AlarmTypeConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface AlarmTypeConfigMapper extends BaseMapper<AlarmTypeConfig> {

    @SelectProvider(type = AlarmTypeConfigDao.class, method = "getEntityList")
    Page<AlarmTypeConfig> getEntityList(Page page, AlarmTypeConfig bean);
}
