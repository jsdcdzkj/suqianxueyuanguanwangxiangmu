package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.model.ConfigSignalField;
import com.jsdc.iotpt.model.ConfigSignalTypeItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConfigSignalTypeItemMapper extends BaseMapper<ConfigSignalTypeItem> {
}
