package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.model.SysMenu;
import com.jsdc.iotpt.model.sys.SysMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysMessageMapper  extends BaseMapper<SysMessage> {
}
