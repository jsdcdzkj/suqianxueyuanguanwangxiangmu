package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.ConfigTemplateFieldDao;
import com.jsdc.iotpt.model.ConfigTemplateField;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface ConfigTemplateFieldMapper extends BaseMapper<ConfigTemplateField> {

    @SelectProvider(type = ConfigTemplateFieldDao.class, method = "getEntityList")
    Page<ConfigTemplateField> getEntityList(Page page, ConfigTemplateField bean);
}
