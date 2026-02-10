package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.WalkRetentionRulesDao;
import com.jsdc.iotpt.model.WalkRetentionRules;
import com.jsdc.iotpt.vo.WalkRetentionRulesVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;


@Mapper
public interface WalkRetentionRulesMapper extends BaseMapper<WalkRetentionRules> {


    @SelectProvider(type = WalkRetentionRulesDao.class, method = "findAll")
    Page<WalkRetentionRulesVo> findAll(Page page, WalkRetentionRulesVo retentionRules);
}
