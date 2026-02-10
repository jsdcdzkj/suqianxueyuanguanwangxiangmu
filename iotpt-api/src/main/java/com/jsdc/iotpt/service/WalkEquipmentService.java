package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.WalkEquipmentMapper;
import com.jsdc.iotpt.model.ConfigDeviceSubitem;
import com.jsdc.iotpt.model.WalkEquipment;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.WalkEquipmentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WalkEquipmentService extends BaseService<WalkEquipment> {

    @Autowired
    private SysDictService sysDictService;
    @Autowired
    private WalkEquipmentMapper walkEquipmentMapper;

    /**
     * 查询设备列表
     *
     * @param equipment
     * @return
     */
    public List<WalkEquipment> getList(WalkEquipment equipment) {
        QueryWrapper<WalkEquipment> queryWrapper = new QueryWrapper<>();
        if (null != equipment.getAreaId()) {
            queryWrapper.eq("areaId", equipment.getAreaId());
        }
        if (null != equipment.getDeptId()) {
            queryWrapper.eq("deptId", equipment.getDeptId());
        }
        if (StringUtils.isNotEmpty(equipment.getEquipmentCode())) {
            queryWrapper.like("equipmentCode", equipment.getEquipmentCode());
        }
        if (StringUtils.isNotEmpty(equipment.getEquipmentModel())) {
            queryWrapper.like("equipmentModel", equipment.getEquipmentModel());
        }
        if (StringUtils.isNotEmpty(equipment.getEquipmentType())) {
            queryWrapper.eq("equipmentType", equipment.getEquipmentType());
        }
        if (StringUtils.isNotEmpty(equipment.getManufactor())) {
            queryWrapper.like("manufactor", equipment.getManufactor());
        }
        if (StringUtils.isNotEmpty(equipment.getIp())) {
            queryWrapper.eq("ip", equipment.getIp());
        }
        queryWrapper.eq("isDelete", 0);
        queryWrapper.ne("useway", 4);
        List<WalkEquipment> equipments = list(queryWrapper);
        return equipments;
    }

    public Page<WalkEquipment> getEquipmentList(WalkEquipmentVo vo, Integer pageIndex, Integer pageSize) {
        LambdaQueryWrapper wrapper = Wrappers.<ConfigDeviceSubitem>lambdaQuery().eq(ConfigDeviceSubitem::getIsDel, 0);
        Page<WalkEquipment> page = walkEquipmentMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), wrapper);
        SysDict sysDict = new SysDict();
        sysDict.setDictType("equipmentType");
        return page;
    }

    public List<WalkEquipment> getListByFloor(WalkEquipment equipment) {
        return list(new LambdaQueryWrapper<WalkEquipment>().eq(WalkEquipment::getIsDelete, 0));
    }

    public List<WalkEquipment> getListByAreaIds(List<Integer> areaIds) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.in("areaId", areaIds);
        wrapper.eq("isDelete", 0);
        return list(wrapper);
    }

    public List<WalkEquipment> getListByNvrIds(List<Integer> nvrIds) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.in("nvrId", nvrIds);
        wrapper.eq("isDelete", 0);
        return list(wrapper);
    }

    public WalkEquipment getEquipmentByIp(String ip) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("ip", ip);
        wrapper.eq("isDelete", 0);
        return getOne(wrapper);
    }
}
