package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.ConfigTopicDao;
import com.jsdc.iotpt.model.ConfigTopic;
import com.jsdc.iotpt.vo.ConfigTopicVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * @authon thr
 * @describe 主题管理
 */
@Mapper
public interface ConfigTopicMapper extends BaseMapper<ConfigTopic> {

    @SelectProvider(type = ConfigTopicDao.class, method = "getEntityList")
    Page<ConfigTopicVo> getEntityList(Page page, ConfigTopicVo bean);
    @SelectProvider(type = ConfigTopicDao.class, method = "cacheTopic")
    List<ConfigTopic> cacheTopic(ConfigTopic configTopic);
}
