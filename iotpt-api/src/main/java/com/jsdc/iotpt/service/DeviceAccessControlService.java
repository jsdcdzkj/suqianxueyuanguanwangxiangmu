package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.mapper.*;
import com.jsdc.iotpt.model.ConfigModel;
import com.jsdc.iotpt.model.ConfigSupplier;
import com.jsdc.iotpt.model.DeviceAccessControl;
import com.jsdc.iotpt.model.sys.SysBuild;
import com.jsdc.iotpt.model.sys.SysBuildArea;
import com.jsdc.iotpt.model.sys.SysBuildFloor;
import com.jsdc.iotpt.vo.DeviceAccessControlVo;
import com.jsdc.iotpt.vo.ResultInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class DeviceAccessControlService extends BaseService<DeviceAccessControl> {

    @Autowired
    DeviceAccessControlMapper deviceAccessControlMapper;
    @Autowired
    SysBuildAreaMapper areaMapper;
    @Autowired
    SysBuildFloorMapper floorMapper;
    @Autowired
    SysBuildMapper buildMapper;
    @Autowired
    ConfigSupplierMapper configSupplierMapper;
    @Autowired
    ConfigModelMapper modelMapper;
    @Autowired
    SysUserService userService;

    /**
     * 分页查询 todo
     *
     * @return
     */
    public Page<DeviceAccessControl> getPageList(DeviceAccessControlVo vo) {
        LambdaQueryWrapper<DeviceAccessControl> wrapper = Wrappers.<DeviceAccessControl>lambdaQuery()
                .like(StringUtils.isNotEmpty(vo.getName()), DeviceAccessControl::getName, vo.getName())
                .eq(null != vo.getAreaId(), DeviceAccessControl::getAreaId, vo.getAreaId())
                .eq(null != vo.getSupplierId(), DeviceAccessControl::getSupplierId, vo.getSupplierId())
                .eq(DeviceAccessControl::getIsDel, G.ISDEL_NO)
                .orderByDesc(DeviceAccessControl::getStatus,DeviceAccessControl::getCreateTime);
        Page<DeviceAccessControl> page = deviceAccessControlMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), wrapper);
        page.getRecords().forEach(x -> {
            if (null != x) {
                if (null != x.getAreaId()) {
                    SysBuildArea area = areaMapper.selectById(x.getAreaId());
                    SysBuild build = buildMapper.selectById(x.getBuildId());
                    SysBuildFloor floor = floorMapper.selectById(x.getFloorId());
                    String area_name = null == area ? "" : area.getAreaName();
                    String build_name = null == build ? "" : build.getBuildName();
                    String floor_name = null == floor ? "" : floor.getFloorName();
                    if(StringUtils.isNotEmpty(build_name)&&StringUtils.isNotEmpty(floor_name)&&StringUtils.isNotEmpty(area_name)){
                        x.setAreaName(build_name + " / " + floor_name + " / " + area_name);
                    }
                }
                if (null != x.getModelNum()) {
                    ConfigModel model = modelMapper.selectById(x.getModelNum());
                    x.setModelName(null == model ? "" : model.getModelName());
                }
                if (null != x.getSupplierId()) {
                    ConfigSupplier supplier = configSupplierMapper.selectById(x.getSupplierId());
                    x.setSupplierName(null == supplier ? "" : supplier.getSupplierName());
                }
            }
        });
        return page;
    }

    /**
     * 查询 todo
     *
     * @return
     */
    public List<DeviceAccessControl> getList(DeviceAccessControlVo vo) {
        LambdaQueryWrapper<DeviceAccessControl> wrapper = Wrappers.<DeviceAccessControl>lambdaQuery()
                .like(StringUtils.isNotEmpty(vo.getName()), DeviceAccessControl::getName, vo.getName())
                .eq(null != vo.getAreaId(), DeviceAccessControl::getAreaId, vo.getAreaId())
                .eq(null != vo.getSupplierId(), DeviceAccessControl::getSupplierId, vo.getSupplierId())
                .eq(DeviceAccessControl::getIsDel, G.ISDEL_NO);
        List<DeviceAccessControl> list = deviceAccessControlMapper.selectList(wrapper);
        list.forEach(x -> {
            if (null != x) {
                if (null != x.getAreaId()) {
                    SysBuildArea area = areaMapper.selectById(x.getAreaId());
                    SysBuild build = buildMapper.selectById(x.getBuildId());
                    SysBuildFloor floor = floorMapper.selectById(x.getFloorId());
                    String area_name = null == area ? "" : area.getAreaName();
                    String build_name = null == build ? "" : build.getBuildName();
                    String floor_name = null == floor ? "" : floor.getFloorName();
                    if(StringUtils.isNotEmpty(build_name)&&StringUtils.isNotEmpty(floor_name)&&StringUtils.isNotEmpty(area_name)){
                        x.setAreaName(build_name + " / " + floor_name + " / " + area_name);
                    }
                }
                if (null != x.getModelNum()) {
                    ConfigModel model = modelMapper.selectById(x.getModelNum());
                    x.setModelName(null == model ? "" : model.getModelName());
                }
                if (null != x.getSupplierId()) {
                    ConfigSupplier supplier = configSupplierMapper.selectById(x.getSupplierId());
                    x.setSupplierName(null == supplier ? "" : supplier.getSupplierName());
                }
            }
        });
        return list;
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    public ResultInfo saveOrUpdateDeviceAccessControl(DeviceAccessControl bean) {
        bean.setIsDel(G.ISDEL_NO);
        bean.setCreateTime(new Date());
        bean.setUpdateTime(new Date());
        bean.setCreateUser(userService.getUser().getId());
        bean.setUpdateUser(userService.getUser().getId());
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
        DeviceAccessControl deviceAccessControl = getById(id);
        if (null != deviceAccessControl) {
            if (null != deviceAccessControl.getAreaId()) {
                SysBuildArea area = areaMapper.selectById(deviceAccessControl.getAreaId());
                deviceAccessControl.setAreaName(null == area ? "" : area.getAreaName());
            }
            if (null != deviceAccessControl.getModelNum()) {
                ConfigModel model = modelMapper.selectById(deviceAccessControl.getModelNum());
                deviceAccessControl.setModelName(null == model ? "" : model.getModelName());
            }
            if (null != deviceAccessControl.getSupplierId()) {
                ConfigSupplier supplier = configSupplierMapper.selectById(deviceAccessControl.getSupplierId());
                deviceAccessControl.setSupplierName(null == supplier ? "" : supplier.getSupplierName());
            }
            if (null != deviceAccessControl.getAreaId()) {
                SysBuildArea area = areaMapper.selectById(deviceAccessControl.getAreaId());
                deviceAccessControl.setAreaName(null == area ? "" : area.getAreaName());
                if (null != area) {
                    SysBuildFloor floor = floorMapper.selectById(area.getFloorId());
                    deviceAccessControl.setFloorName(null == floor ? "" : floor.getFloorName());
                    if (null != floor) {
                        SysBuild build = buildMapper.selectById(floor.getDictBuilding());
                        deviceAccessControl.setBuildName(null == build ? "" : build.getBuildName());
                    }
                }

            }
        }
        return ResultInfo.success(deviceAccessControl);
    }

    /**
     * 删除 todo
     *
     * @param id
     * @return
     */
    public ResultInfo delete(Integer id) {
        DeviceAccessControl entity = getById(id);
        entity.setIsDel(G.ISDEL_YES);
        entity.setUpdateUser(userService.getUser().getId());
        entity.setUpdateTime(new Date());
        updateById(entity);
        return ResultInfo.success();
    }
}


