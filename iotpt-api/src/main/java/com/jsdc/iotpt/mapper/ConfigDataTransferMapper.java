package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.ConfigDataTransferDao;
import com.jsdc.iotpt.model.ConfigDataTransfer;
import com.jsdc.iotpt.vo.ConfigDataTransferVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

/**
 * @authon thr
 * @describe 数据转换模板管理
 */
@Mapper
public interface ConfigDataTransferMapper extends BaseMapper<ConfigDataTransfer> {

    @SelectProvider(type = ConfigDataTransferDao.class, method = "getEntityList")
    Page<ConfigDataTransferVo> getEntityList(Page page, ConfigDataTransferVo bean);
}
