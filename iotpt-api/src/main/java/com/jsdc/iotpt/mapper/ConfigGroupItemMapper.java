package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.dao.ConfigGroupDao;
import com.jsdc.iotpt.model.ConfigGroupItem;
import com.jsdc.iotpt.model.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface ConfigGroupItemMapper extends BaseMapper<ConfigGroupItem> {
}
