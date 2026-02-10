package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.model.sys.SysConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统配置(SysConfig)表数据库访问层
 *
 * @author wangYan
 * @since 2023-07-20
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {

}

