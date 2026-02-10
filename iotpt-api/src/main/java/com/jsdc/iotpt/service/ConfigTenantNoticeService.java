package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;

import com.jsdc.iotpt.mapper.ConfigTenantNoticeMapper;
import com.jsdc.iotpt.model.ConfigTenantNotice;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.ConfigTenantNoticeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ConfigTenantNoticeService extends BaseService<ConfigTenantNotice> {

    @Autowired
    private ConfigTenantNoticeMapper configTenantNoticeMapper;

    /**
     * 分页查询 todo
     *
     * @return
     */
    public Page<ConfigTenantNotice> getPageList(ConfigTenantNoticeVo vo) {
        QueryWrapper<ConfigTenantNotice> queryWrapper = new QueryWrapper<>();

        //testMapper.getEntityList(new Page<>(vo.getPageNo(), vo.getPageSize()),vo);
        return configTenantNoticeMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);

    }

    /**
     * 查询 todo
     *
     * @return
     */
    public List<ConfigTenantNotice> getList(ConfigTenantNotice entity) {
        QueryWrapper<ConfigTenantNotice> queryWrapper = new QueryWrapper<>();
        return configTenantNoticeMapper.selectList(queryWrapper);
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    public ResultInfo saveOrUpdateConfigTenantNotice(ConfigTenantNotice bean) {
       if(bean.getId() == null){
           save(bean);
       }else {
           updateById(bean);
       }
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



    /**
     * 根据租户id获取类 todo
     *
     * @param id
     * @return
     */
    public ConfigTenantNotice getEntityByTId(Integer id) {
        LambdaQueryWrapper<ConfigTenantNotice> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(ConfigTenantNotice::getTenantId, id);
        return getOne(wrapper);
    }

}


