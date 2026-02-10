package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.ConfigSignalFieldDao;
import com.jsdc.iotpt.model.ConfigSignalField;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

/**
 * @authon thr
 * @describe 信号字段管理
 */
@Mapper
public interface ConfigSignalFieldMapper extends BaseMapper<ConfigSignalField> {

    @SelectProvider(type = ConfigSignalFieldDao.class, method = "getEntityList")
    Page<ConfigSignalField> getEntityList(Page page, ConfigSignalField bean);
}
