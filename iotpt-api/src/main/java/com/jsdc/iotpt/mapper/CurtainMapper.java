package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.dao.CurtainDao;
import com.jsdc.iotpt.model.curtain.Curtain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * @program: 园区物联网管控平台
 * @author: jxl
 * @create: 2024-11-26 08:53
 **/
@Mapper
public interface CurtainMapper extends BaseMapper<Curtain> {
    @SelectProvider(type = CurtainDao.class, method = "selectByMeetingId")
    List<Curtain> selectByMeetingId(Integer meetingId);
}
