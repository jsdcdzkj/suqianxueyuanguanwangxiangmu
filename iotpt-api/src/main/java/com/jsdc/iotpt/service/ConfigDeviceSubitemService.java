package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.mapper.ConfigDeviceSubitemMapper;
import com.jsdc.iotpt.model.ConfigDeviceSubitem;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.ConfigDeviceSubitemVo;
import com.jsdc.iotpt.vo.LogVo;
import com.jsdc.iotpt.vo.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @projectName: IOT
 * @className: ConfigDeviceSubitemService
 * @author: wp
 * @description:
 * @date: 2023/5/9 8:52
 */
@Service
@Transactional
public class ConfigDeviceSubitemService extends BaseService<ConfigDeviceSubitem> {

    @Autowired
    private ConfigDeviceSubitemMapper ConfigDeviceSubitemMapper;
    @Autowired
    private SysUserService userService;
    @Autowired
    private SysDictService dictService;
    @Autowired
    private MemoryCacheService memoryCacheService;
    /**
     * 分页查询 todo
     *
     * @return
     */
    public Page<ConfigDeviceSubitem> getPageList(ConfigDeviceSubitemVo vo) {
        LambdaQueryWrapper wrapper = Wrappers.<ConfigDeviceSubitem>lambdaQuery()
                .like(StringUtils.isNotEmpty(vo.getSubitemName()), ConfigDeviceSubitem::getSubitemName, vo.getSubitemName())
                .like(StringUtils.isNotEmpty(vo.getSubitemCode()), ConfigDeviceSubitem::getSubitemCode, vo.getSubitemCode())
                .eq(ConfigDeviceSubitem::getIsDel, G.ISDEL_NO);
        Page<ConfigDeviceSubitem> page = ConfigDeviceSubitemMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), wrapper);
        page.getRecords().forEach(a -> {
            if (StringUtils.isNotEmpty(a.getEnergy_type())) {
                List<SysDict> dicts = dictService.list(new LambdaQueryWrapper<SysDict>().eq(SysDict::getDictType, "device_energy_type")
                        .eq(SysDict::getDictValue, a.getEnergy_type()).eq(SysDict::getIsDel, 0));
                if (!dicts.isEmpty() && dicts.size() > 0) {
                    a.setEnergy_type(dicts.get(0).getDictLabel());
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
    public List<ConfigDeviceSubitem> getList(ConfigDeviceSubitemVo vo) {
        QueryWrapper<ConfigDeviceSubitem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        if (StringUtils.isNotEmpty(vo.getEnergy_type())) {
            queryWrapper.eq("energy_type", vo.getEnergy_type());
        }
        return ConfigDeviceSubitemMapper.selectList(queryWrapper);
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    public ResultInfo saveOrUpdateConfigDeviceSubitem(ConfigDeviceSubitemVo bean) {
        // 验证唯一
        Long num = count(Wrappers.<ConfigDeviceSubitem>lambdaQuery()
                .eq(ConfigDeviceSubitem::getIsDel, G.ISDEL_NO)
                .eq(ConfigDeviceSubitem::getSubitemCode, bean.getSubitemCode())
                .ne(null != bean.getId(), ConfigDeviceSubitem::getId, bean.getId())
        );
        if (num > 0) {
            return ResultInfo.error("设备分项编码已存在");
        }
        num = count(Wrappers.<ConfigDeviceSubitem>lambdaQuery()
                .eq(ConfigDeviceSubitem::getIsDel, G.ISDEL_NO)
                .eq(ConfigDeviceSubitem::getSubitemName, bean.getSubitemName())
                .ne(null != bean.getId(), ConfigDeviceSubitem::getId, bean.getId())
        );
        if (num > 0) {
            return ResultInfo.error("设备分项名称已存在");
        }
        if (StringUtils.isNotNull(bean.getId())) {
            bean.setUpdateTime(new Date());
            bean.setUpdateUser(userService.getUser().getId());
        } else {
            bean.setCreateTime(new Date());
            bean.setCreateUser(userService.getUser().getId());
            bean.setUpdateTime(new Date());
            bean.setUpdateUser(userService.getUser().getId());
            bean.setIsDel(G.ISDEL_NO);
        }
        saveOrUpdate(bean);


        memoryCacheService.putAllSubitem();
        return ResultInfo.success(new LogVo("设备分项添加/编辑成功"));
    }

    /**
     * 根据id获取类 todo
     *
     * @param id
     * @return
     */
    public ConfigDeviceSubitem getEntityById(Integer id) {
        return getById(id);
    }

    /**
     * 根据id删除 todo
     *
     * @param id
     * @return
     */
    public ResultInfo delConfigDeviceSubitem(Integer id) {
        ConfigDeviceSubitem ConfigDeviceSubitem = new ConfigDeviceSubitem();
        ConfigDeviceSubitem.setId(id);
        ConfigDeviceSubitem.setIsDel(G.ISDEL_YES);
        updateById(ConfigDeviceSubitem);
        return ResultInfo.success();
    }

}
