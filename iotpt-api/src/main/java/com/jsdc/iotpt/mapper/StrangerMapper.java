package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.StrangerDao;
import com.jsdc.iotpt.model.Stranger;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface StrangerMapper extends BaseMapper<Stranger> {

    @SelectProvider(type = StrangerDao.class, method = "selectStrangerPage")
    Page<Stranger> selectStrangerPage(Page page, Stranger bean);
}
