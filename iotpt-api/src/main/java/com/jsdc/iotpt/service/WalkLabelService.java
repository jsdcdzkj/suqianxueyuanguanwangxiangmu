package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.WalkLabelMapper;
import com.jsdc.iotpt.model.WalkLabel;
import com.jsdc.iotpt.vo.WalkLabelVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WalkLabelService extends BaseService<WalkLabel> {

    @Autowired
    private WalkLabelMapper walkLabelMapper;

    public Page<WalkLabel> getPageList(WalkLabelVo vo) {
        LambdaQueryWrapper<WalkLabel> queryWrapper = new LambdaQueryWrapper<WalkLabel>().eq(WalkLabel::getIsDelete, 0);
        if (null != vo) {
            if (StringUtils.isNotEmpty(vo.getLabelName())) {
                queryWrapper.like(WalkLabel::getLabelName, vo.getLabelName());
            }
        }
        return walkLabelMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);
    }
}
