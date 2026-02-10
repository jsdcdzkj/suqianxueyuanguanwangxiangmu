package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;

import com.jsdc.iotpt.mapper.AppVersionInfoMapper;
import com.jsdc.iotpt.model.AppVersionInfo;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.AppVersionInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class AppVersionInfoService extends BaseService<AppVersionInfo> {

    @Autowired
    private AppVersionInfoMapper appVersionInfoMapper;
    @Autowired
    private SysUserService sysUserService;

    /**
     * 分页查询 todo
     *
     * @return
     */
    public Page<AppVersionInfo> getPageList(AppVersionInfoVo vo) {
        QueryWrapper<AppVersionInfo> queryWrapper = new QueryWrapper<>();
        //testMapper.getEntityList(new Page<>(vo.getPageNo(), vo.getPageSize()),vo);
        return appVersionInfoMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()),
                Wrappers.<AppVersionInfo>lambdaQuery().eq(AppVersionInfo::getIsDel,0).orderByDesc(AppVersionInfo::getVersion));

    }

    /**
     * 查询 todo
     *
     * @return
     */
    public List<AppVersionInfo> getList(AppVersionInfo entity) {
        QueryWrapper<AppVersionInfo> queryWrapper = new QueryWrapper<>();
        return appVersionInfoMapper.selectList(queryWrapper);
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    public ResultInfo saveOrUpdateAppVersionInfo(AppVersionInfo bean) {
        if (Objects.isNull(bean.getId())) {
            bean.setIsDel(0);
            bean.setCreateTime(new Date());
            bean.setCreateUser(sysUserService.getUser().getId());
        }else{
            bean.setUpdateTime(new Date());
            bean.setUpdateUser(sysUserService.getUser().getId());
        }
        saveOrUpdate(bean);
        return ResultInfo.success();
    }

    /**
     * 根据id获取类 todo
     *
     * @param id
     * @return
     */
    public ResultInfo getEntityById(Integer id) {
        return ResultInfo.success(getById(id));
    }

    public ResultInfo getLastOne() {
        return ResultInfo.success(appVersionInfoMapper.getLastOne());
    }
}


