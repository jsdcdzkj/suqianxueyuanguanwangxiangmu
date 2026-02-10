package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.dao.MeetingSceneControlDao;
import com.jsdc.iotpt.model.MeetingSceneControl;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

/**
 * @Author：jxl
 * @Date：2024/6/13 13:44
 * @FileDesc：
 */
@Mapper
public interface MeetingSceneControlMapper extends BaseMapper<MeetingSceneControl> {

    @SelectProvider(type = MeetingSceneControlDao.class,method = "getByDeviceTypeId")
    MeetingSceneControl getByDeviceTypeId(Integer deviceType, Integer deviceId);
}
