package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.model.sys.SysDict;
import org.apache.ibatis.annotations.Mapper;

/**
 * (SysDict)表数据库访问层
 *
 * @author wangYan
 * @since 2023-05-09
 */
@Mapper
public interface SysDictMapper extends BaseMapper<SysDict> {

}

