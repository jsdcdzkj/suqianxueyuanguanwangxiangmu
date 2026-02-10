package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.MsgMessageInfoDao;
import com.jsdc.iotpt.dao.WarningDisposeDao;
import com.jsdc.iotpt.model.MsgMessageInfo;
import com.jsdc.iotpt.model.WarningDispose;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface MsgMessageInfoMapper extends BaseMapper<MsgMessageInfo> {


    @SelectProvider(type = MsgMessageInfoDao.class, method = "updateOne")
    Integer updateOne(MsgMessageInfo bean);
}
