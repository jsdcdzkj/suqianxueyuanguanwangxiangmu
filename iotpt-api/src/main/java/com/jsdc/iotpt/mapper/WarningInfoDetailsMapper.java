package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.WarningInfoDetailsDao;
import com.jsdc.iotpt.model.WarningInfoDetails;
import com.jsdc.iotpt.vo.WarningInfoDetailsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface WarningInfoDetailsMapper extends BaseMapper<WarningInfoDetails> {

    @SelectProvider(type = WarningInfoDetailsDao.class, method = "getEntityList")
    Page<WarningInfoDetails> getEntityList(Page page, WarningInfoDetails bean);

    @SelectProvider(type = WarningInfoDetailsDao.class, method = "getList")
    List<WarningInfoDetailsVo> getList(WarningInfoDetails bean);
}
