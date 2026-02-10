package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.ConfigProtocolDao;
import com.jsdc.iotpt.model.ConfigProtocol;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

/**
 * @authon thr
 * @describe 协议管理
 */
@Mapper
public interface ConfigProtocolMapper extends BaseMapper<ConfigProtocol> {

    @SelectProvider(type = ConfigProtocolDao.class, method = "getEntityList")
    Page<ConfigProtocol> getEntityList(Page page, ConfigProtocol bean);
}
