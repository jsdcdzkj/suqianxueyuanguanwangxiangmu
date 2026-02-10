package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.mapper.ConfigSupplierMapper;
import com.jsdc.iotpt.model.ConfigModel;
import com.jsdc.iotpt.model.ConfigSupplier;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.ConfigModelVo;
import com.jsdc.iotpt.vo.ConfigSupplierVo;
import com.jsdc.iotpt.vo.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @projectName: IOT
 * @className: ConfigSupplierService
 * @author: wp
 * @description:
 * @date: 2023/5/9 9:24
 */
@Service
@Transactional
public class ConfigSupplierService extends BaseService<ConfigSupplier> {


    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private ConfigModelService configModelService;

    @Autowired
    private ConfigSupplierMapper supplierMapper;

    /**
     * 分页查询 todo
     *
     * @return
     */
    public Page<ConfigSupplier> getPageList(ConfigSupplierVo vo) {
        LambdaQueryWrapper<ConfigSupplier> queryWrapper = Wrappers.<ConfigSupplier>lambdaQuery().eq(ConfigSupplier::getIsDel, G.ISDEL_NO);
        queryWrapper.like(StringUtils.isNotBlank(vo.getSupplierName()), ConfigSupplier::getSupplierName, vo.getSupplierName());
        queryWrapper.eq(StringUtils.isNotBlank(vo.getSupplierType()), ConfigSupplier::getSupplierType, vo.getSupplierType());
        //testMapper.getEntityList(new Page<>(vo.getPageNo(), vo.getPageSize()),vo);
        return supplierMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);

    }

    /**
     * 查询 todo
     *
     * @return
     */
    public List<ConfigSupplier> getList(ConfigSupplierVo vo) {
        QueryWrapper<ConfigSupplier> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        return supplierMapper.selectList(queryWrapper);
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    public void saveOrUpdateConfigSupplier(ConfigSupplier bean) {
        Long num = supplierMapper.selectCount(Wrappers.<ConfigSupplier>lambdaQuery()
                .eq(ConfigSupplier::getIsDel, G.ISDEL_NO)
                .eq(ConfigSupplier::getSupplierName, bean.getSupplierName())
                .ne(bean.getId() != null, ConfigSupplier::getId, bean.getId())
                .eq(ConfigSupplier::getSupplierType, bean.getSupplierType())
        );
        if (num > 0) {
            throw new RuntimeException("供应商名称已存在");
        }
        // 当前登陆人
        SysUser sysUser = sysUserService.getUser();
        if (bean.getId() == null) {
            //新增
            bean.setIsDel(0);
            bean.setCreateTime(new Date());
            bean.setCreateUser(sysUser.getId());
        }else{
            //编辑
            bean.setUpdateTime(new Date());
            bean.setUpdateUser(sysUser.getId());
        }
        saveOrUpdate(bean);
    }

    /**
     * 根据id获取类 todo
     *
     * @param id
     * @return
     */
    public ConfigSupplier getEntityById(Integer id) {
        return getById(id);
    }

    /**
     * 根据id删除 todo
     *
     * @param id
     * @return
     */
    public ResultInfo delConfigSupplier(Integer id) {
        ConfigSupplier ConfigSupplier = new ConfigSupplier();
        ConfigSupplier.setId(id);
        ConfigSupplier.setIsDel(1);
        updateById(ConfigSupplier);
        return ResultInfo.success();
    }

    /**
     * 获取供应商下的型号列表
     *
     * @param bean
     * @return
     */
    public ResultInfo getModelList(ConfigModelVo bean) {
        if (bean.getConfigSupplierId() == null) {
            return ResultInfo.error("供应商id不能为空");
        }
        Page<ConfigModel> configModels = configModelService.page(new Page<>(bean.getPageNo(), bean.getPageSize()), Wrappers.<ConfigModel>lambdaQuery()
                .eq(ConfigModel::getIsDel, G.ISDEL_NO)
                .eq(ConfigModel::getSupplierId, bean.getConfigSupplierId())
        );
        for (ConfigModel configModel : configModels.getRecords()) {
            ConfigSupplier supplier = supplierMapper.selectById(configModel.getSupplierId());
            if (null != supplier){
                configModel.setSupplier(supplier);
                configModel.setSupplierName(supplier.getSupplierName());
            }
        }

        return ResultInfo.success(configModels);
    }

    /**
     * 获取供应商的设备列表
     * @param bean
     * @return
     */
    public ResultInfo getDeviceList(ConfigSupplierVo bean) {
        Integer supplierId = bean.getId();
        if (supplierId == null) {
            return ResultInfo.error("供应商id不能为空");
        }
        // 得到门禁设备列表
        return null;

    }
}
