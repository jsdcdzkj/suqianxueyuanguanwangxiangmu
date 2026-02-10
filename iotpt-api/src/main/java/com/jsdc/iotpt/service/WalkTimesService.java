package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.WalkTimesMapper;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.WalkTimes;
import com.jsdc.iotpt.vo.WalkTimesVo;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WalkTimesService extends BaseService<WalkTimes> {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private WalkTimesMapper walkTimesMapper;


    public Page<WalkTimes> findAll(WalkTimesVo vo) {
        QueryWrapper<WalkTimes> queryWrapper = new QueryWrapper<>();
        if (vo.getDeptId() == null) {
            SysUser user = sysUserService.getUser();
            vo.setDeptId(user.getDeptId());
        }
        if (vo != null) {
            if (vo.getId() != null) {
                queryWrapper.eq("id", vo.getId());
            }
            if (vo.getDeptId() != null) {
                queryWrapper.eq("deptId", vo.getDeptId());
            }
            if (Strings.isNotEmpty(vo.getTimesName())) {
                queryWrapper.like("timesName", vo.getTimesName());
            }
            if (vo.getIsFlag() != null) {
                queryWrapper.eq("isFlag", vo.getIsFlag());
            }
        }
        queryWrapper.eq("isDelete", 0);
        Page<WalkTimes> page = walkTimesMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);
        return page;
    }


    public List<WalkTimes> getList(WalkTimes times) {
        QueryWrapper<WalkTimes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isFlag", 0);
        if (null != times.getDeptId()) {
            queryWrapper.eq("deptId", times.getDeptId());
        }
        queryWrapper.eq("isDelete", 0);
        return list(queryWrapper);
    }

    /**
     * 获取所有有效的时间组
     *
     * @return
     */
    public List<WalkTimes> getAllList() {
        QueryWrapper<WalkTimes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isFlag", 0);
        queryWrapper.eq("isDelete", 0);
        return list(queryWrapper);
    }
}
