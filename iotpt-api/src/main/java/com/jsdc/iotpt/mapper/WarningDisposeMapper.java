package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.WarningDisposeDao;
import com.jsdc.iotpt.model.WarningDispose;
import com.jsdc.iotpt.vo.WarningDisposeVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface WarningDisposeMapper extends BaseMapper<WarningDispose> {

    @SelectProvider(type = WarningDisposeDao.class, method = "getEntityList")
    Page<WarningDispose> getEntityList(Page page, WarningDispose bean);


    @SelectProvider(type = WarningDisposeDao.class, method = "getAllList")
    List<WarningDisposeVo> getAllList(WarningDisposeVo bean);
}
