package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.AbnormalPatrolDao;
import com.jsdc.iotpt.dao.ConfigLinkDao;
import com.jsdc.iotpt.model.AlertSheet;
import com.jsdc.iotpt.model.operate.AbnormalPatrol;
import com.jsdc.iotpt.vo.ConfigLinkVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface AbnormalPatrolMapper extends BaseMapper<AbnormalPatrol> {
    @SelectProvider(type = AbnormalPatrolDao.class, method = "getPageList")
    Page<AbnormalPatrol> getPageList(Page page, AbnormalPatrol bean);
}
