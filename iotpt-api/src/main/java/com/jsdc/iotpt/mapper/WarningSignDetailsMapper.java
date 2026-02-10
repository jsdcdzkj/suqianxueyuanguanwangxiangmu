package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.WarningSignDetailsDao;
import com.jsdc.iotpt.model.WarningSignDetails;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface WarningSignDetailsMapper extends BaseMapper<WarningSignDetails> {

    @SelectProvider(type = WarningSignDetailsDao.class, method = "getEntityList")
    Page<WarningSignDetails> getEntityList(Page page, WarningSignDetails bean);
}
