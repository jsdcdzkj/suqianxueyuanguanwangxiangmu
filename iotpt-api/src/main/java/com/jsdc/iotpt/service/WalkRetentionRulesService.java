package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.WalkRetentionRulesMapper;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.WalkRetentionRules;
import com.jsdc.iotpt.vo.WalkRetentionRulesVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WalkRetentionRulesService extends BaseService<WalkRetentionRules> {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private WalkRetentionRulesMapper retentionRulesMapper;


    public Page<WalkRetentionRulesVo> findAll(WalkRetentionRulesVo vo) {
        if (vo.getDeptId() == null) {
            SysUser sysUser = sysUserService.getUser();
            vo.setDeptId(sysUser.getDeptId());
        }
        Page<WalkRetentionRulesVo> page = new Page<>(vo.getPageNo(), vo.getPageSize());
        Page<WalkRetentionRulesVo> pageList = retentionRulesMapper.findAll(page, vo);
        return pageList;
    }


    public List<WalkRetentionRules> getList(WalkRetentionRules retentionRules) {
        QueryWrapper<WalkRetentionRules> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDelete", 0);
        if (retentionRules.getDeptId() != null) {
            queryWrapper.eq("deptId", retentionRules.getDeptId());
        }
        return list(queryWrapper);
    }
}
