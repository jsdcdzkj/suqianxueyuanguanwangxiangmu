package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.AlarmPlanConfigDao;
import com.jsdc.iotpt.model.new_alarm.AlarmPlanConfig;
import com.jsdc.iotpt.vo.AlarmPlanConfigVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;


@Mapper
public interface AlarmPlanConfigMapper extends BaseMapper<AlarmPlanConfig> {

    @SelectProvider(type = AlarmPlanConfigDao.class, method = "getPage")
    Page<AlarmPlanConfig> getPage(Page<AlarmPlanConfig> page, AlarmPlanConfigVO vo);

    @SelectProvider(type = AlarmPlanConfigDao.class, method = "selectByContentId")
    AlarmPlanConfig selectByContentId(String contentId);

}
