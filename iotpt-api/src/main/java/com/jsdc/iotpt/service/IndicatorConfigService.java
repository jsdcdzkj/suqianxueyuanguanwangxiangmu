package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.IndicatorConfigMapper;
import com.jsdc.iotpt.model.IndicatorConfig;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.IndicatorConfigVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class IndicatorConfigService extends BaseService<IndicatorConfig> {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private IndicatorConfigMapper indicatorConfigMapper;

    public void edit(IndicatorConfig bean) {
        SysUser user = sysUserService.getUser();
        Date now = new Date();
        bean.setUpdateUser(user.getId());
        bean.setUpdateTime(now);
        if (StringUtils.isNotNull(bean.getId())) {
            bean.updateById();
        }else {
            bean.setIsDel(0);
            bean.setCreateUser(user.getId());
            bean.setCreateTime(now);
            bean.insert();
        }
    }

    public Page<IndicatorConfig> pageList(IndicatorConfigVo vo) {
        Page<IndicatorConfig> page = new Page<>(vo.getPageNo(), vo.getPageSize());
        LambdaQueryWrapper<IndicatorConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IndicatorConfig::getIsDel, 0);
        queryWrapper.eq(StringUtils.isNotNull(vo.getBizType()), IndicatorConfig::getBizType, vo.getBizType());
        queryWrapper.eq(StringUtils.isNotBlank(vo.getSubCode()), IndicatorConfig::getSubCode, vo.getSubCode());
        return indicatorConfigMapper.selectPage(page, queryWrapper);
    }

    public void del(Integer id) {
        IndicatorConfig bean = new IndicatorConfig();
        bean.setId(id);
        bean.setIsDel(1);
        bean.updateById();
    }
}
