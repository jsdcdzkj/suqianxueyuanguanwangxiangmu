package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;

import com.jsdc.iotpt.mapper.ConfigTemplateFieldMapper;
import com.jsdc.iotpt.model.ConfigTemplateField;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.ConfigTemplateFieldVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ConfigTemplateFieldService extends BaseService<ConfigTemplateField> {

    @Autowired
    private ConfigTemplateFieldMapper configTemplateFieldMapper;

    /**
     * 分页查询 todo
     *
     * @return
     */
    public Page<ConfigTemplateField> getPageList(ConfigTemplateFieldVo vo) {
        QueryWrapper<ConfigTemplateField> queryWrapper = new QueryWrapper<>();

        //testMapper.getEntityList(new Page<>(vo.getPageNo(), vo.getPageSize()),vo);
        return configTemplateFieldMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);

    }

    /**
     * 查询 todo
     *
     * @return
     */
    public List<ConfigTemplateField> getList(ConfigTemplateField bean) {
        QueryWrapper<ConfigTemplateField> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotNull(bean)) {
            if (StringUtils.isNotNull(bean.getModelId())) {
                queryWrapper.eq("modelId", bean.getModelId());
            }
            if (StringUtils.isNotEmpty(bean.getType())) {
                queryWrapper.eq("type", bean.getType());
            }
        }
        queryWrapper.eq("isDel", 0);
        return configTemplateFieldMapper.selectList(queryWrapper);
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    public ResultInfo saveOrUpdateConfigTemplateField(ConfigTemplateField bean) {
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

    /**
     * 根据modelId获取类 todo
     */
    public ConfigTemplateField getEntityByModelId(ConfigTemplateField bean) {
        QueryWrapper<ConfigTemplateField> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotNull(bean)) {
            if (StringUtils.isNotNull(bean.getModelId())) {
                queryWrapper.eq("modelId", bean.getModelId());
            }
            if (StringUtils.isNotEmpty(bean.getType())) {
                queryWrapper.eq("type", bean.getType());
            }
        }
        queryWrapper.eq("isDel", 0);
        return configTemplateFieldMapper.selectOne(queryWrapper);
    }

}


