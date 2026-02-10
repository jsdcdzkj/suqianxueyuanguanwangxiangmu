package com.jsdc.iotpt.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.model.AlertSheet;
import com.jsdc.iotpt.model.ConfigDataTransfer;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface AlertSheetMapper extends BaseMapper<AlertSheet> {
}
