package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.mapper.ConfigModelMapper;
import com.jsdc.iotpt.mapper.ConfigSupplierMapper;
import com.jsdc.iotpt.model.ConfigModel;
import com.jsdc.iotpt.model.ConfigSupplier;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.ConfigModelVo;
import com.jsdc.iotpt.vo.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @projectName: IOT
 * @className: ConfigModelService
 * @author: wp
 * @description:
 * @date: 2023/5/9 9:33
 */
@Service
@Transactional
public class ConfigModelService extends BaseService<ConfigModel> {
    @Autowired
    private ConfigModelMapper configModelMapper;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private ConfigSupplierMapper setSupplierMapper;

    /**
     * 分页查询 todo
     *
     * @return
     */
    public Page<ConfigModel> getPageList(ConfigModelVo vo) {
        QueryWrapper<ConfigModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        //testMapper.getEntityList(new Page<>(vo.getPageNo(), vo.getPageSize()),vo);
        Page<ConfigModel> configModelPage = configModelMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);
        for (ConfigModel configModel : configModelPage.getRecords()) {
            ConfigSupplier supplier = setSupplierMapper.selectById(configModel.getSupplierId());
            if (null != supplier){
                configModel.setSupplier(supplier);
                configModel.setSupplierName(supplier.getSupplierName());
            }
        }
        return configModelPage;

    }

    /**
     * 查询 todo
     *
     * @return
     */
    public List<ConfigModel> getList(ConfigModel vo) {
        QueryWrapper<ConfigModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        if (StringUtils.isNotNull(vo.getSupplierId())) {
            queryWrapper.eq("supplierId", vo.getSupplierId());
        }
        return configModelMapper.selectList(queryWrapper);
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    public void saveOrUpdateConfigModel(ConfigModel bean) {
        Long num = count(Wrappers.<ConfigModel>lambdaQuery()
                .eq(ConfigModel::getIsDel, G.ISDEL_NO)
                .eq(ConfigModel::getModelName, bean.getModelName())
                .ne(null != bean.getId(), ConfigModel::getId, bean.getId())
        );
        if (num > 0) {
            throw new RuntimeException("型号名称已存在");
        }
        SysUser user = sysUserService.getUser();
        if (null == bean.getId()) {
            bean.setIsDel(G.ISDEL_NO);
            bean.setCreateTime(new Date());
            bean.setCreateUser(user.getId());
        } else {
            bean.setUpdateTime(new Date());
            bean.setUpdateUser(user.getId());
        }
        saveOrUpdate(bean);
    }

    /**
     * 根据id获取类 todo
     *
     * @param id
     * @return
     */
    public ConfigModel getEntityById(Integer id) {
        return getById(id);
    }

    /**
     * 根据id删除 todo
     *
     * @param id
     * @return
     */
    public ResultInfo delConfigModel(Integer id) {
        if (null == id) {
            return ResultInfo.error("id不能为空");
        }
        ConfigModel configModel = new ConfigModel();
        configModel.setId(id);
        configModel.setIsDel(1);
        updateById(configModel);
        return ResultInfo.success();
    }
}
