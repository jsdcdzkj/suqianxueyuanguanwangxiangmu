package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.WarningConfigDao;
import com.jsdc.iotpt.model.WarningConfig;
import com.jsdc.iotpt.vo.WarningConfigVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface WarningConfigMapper extends BaseMapper<WarningConfig> {

    @SelectProvider(type = WarningConfigDao.class, method = "getEntityList")
    Page<WarningConfigVo> getEntityList(Page page, WarningConfig bean);
    @SelectProvider(type = WarningConfigDao.class, method = "getWarningType")
    List<WarningConfig> getWarningType(WarningConfigVo vo);

    @SelectProvider(type = WarningConfigDao.class, method = "getAllWarningConfig")
    List<WarningConfigVo> getAllWarningConfig();


}
