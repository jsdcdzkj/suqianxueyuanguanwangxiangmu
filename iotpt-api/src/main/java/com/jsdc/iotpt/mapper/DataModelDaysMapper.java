package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.model.DataModelDays;
import org.apache.ibatis.annotations.Mapper;

/**
 * 大数据模型数据
 * 每日生成数据
 */
@Mapper
public interface DataModelDaysMapper extends BaseMapper<DataModelDays> {

}
