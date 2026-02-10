package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.ConfigProtocolMapper;
import com.jsdc.iotpt.model.ConfigProtocol;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.ConfigProtocolVo;
import com.jsdc.iotpt.vo.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @authon thr
 * @describe 协议管理
 */
@Service
@Transactional
public class ConfigProtocolService extends BaseService<ConfigProtocol> {

    @Autowired
    private ConfigProtocolMapper configProtocolMapper;

    /**
     * 分页查询 todo
     *
     * @return
     */
    public Page<ConfigProtocol> getPageList(ConfigProtocolVo vo) {
        QueryWrapper<ConfigProtocol> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotNull(vo)){
            if (StringUtils.isNotEmpty(vo.getProtocolName())){
                queryWrapper.like("protocolName", vo.getProtocolName());
            }
        }
        queryWrapper.eq("isDel", 0);
        queryWrapper.orderByDesc("id");
        //testMapper.getEntityList(new Page<>(vo.getPageNo(), vo.getPageSize()),vo);
        return configProtocolMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);

    }

    /**
     * 查询 todo
     *
     * @return
     */
    public List<ConfigProtocol> getList(ConfigProtocol entity) {
        QueryWrapper<ConfigProtocol> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        queryWrapper.orderByDesc("id");
        return configProtocolMapper.selectList(queryWrapper);
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    public ResultInfo saveOrUpdateConfigProtocol(ConfigProtocol bean) {
        if (StringUtils.isNotNull(bean.getId())) {
            bean.setUpdateTime(new Date());
        } else {
            bean.setCreateTime(new Date());
            bean.setIsDel(0);
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

}


