package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.ConfigSignalFieldMapper;
import com.jsdc.iotpt.model.ConfigSignalField;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.ConfigSignalFieldVo;
import com.jsdc.iotpt.vo.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @authon thr
 * @describe 信号字段管理
 */
@Service
@Transactional
public class ConfigSignalFieldService extends BaseService<ConfigSignalField> {

    @Autowired
    private ConfigSignalFieldMapper configSignalFieldMapper;

    /**
     * 分页查询 todo
     *
     * @return
     */
    public Page<ConfigSignalField> getPageList(ConfigSignalFieldVo vo) {
        QueryWrapper<ConfigSignalField> queryWrapper = new QueryWrapper<>();

        //testMapper.getEntityList(new Page<>(vo.getPageNo(), vo.getPageSize()),vo);
        return configSignalFieldMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);

    }

    /**
     * 查询 todo
     *
     * @return
     */
    public List<ConfigSignalField> getList(ConfigSignalField bean) {
        QueryWrapper<ConfigSignalField> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotNull(bean)) {
            if (StringUtils.isNotNull(bean.getModelId())) {
                queryWrapper.eq("modelId", bean.getModelId());
            }
            if (StringUtils.isNotEmpty(bean.getType())) {
                queryWrapper.eq("type", bean.getType());
            }
        }
        queryWrapper.eq("isDel", 0);
        return configSignalFieldMapper.selectList(queryWrapper);
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    public ResultInfo saveOrUpdateConfigSignalField(ConfigSignalField bean) {
        if (StringUtils.isNull(bean.getId())) {
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


