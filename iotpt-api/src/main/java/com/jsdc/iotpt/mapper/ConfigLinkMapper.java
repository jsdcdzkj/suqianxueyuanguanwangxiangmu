package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.ConfigLinkDao;
import com.jsdc.iotpt.model.ConfigLink;
import com.jsdc.iotpt.vo.ConfigLinkVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

/**
 * @authon thr
 * @describe 连接管理
 */
@Mapper
public interface ConfigLinkMapper extends BaseMapper<ConfigLink> {

    @SelectProvider(type = ConfigLinkDao.class, method = "getEntityList")
    Page<ConfigLinkVo> getEntityList(Page page, ConfigLinkVo bean);
}
