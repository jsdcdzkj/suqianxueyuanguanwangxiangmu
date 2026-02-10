package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.model.ConfigDeviceType;
import com.jsdc.iotpt.model.DeviceCollect;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.sys.SysConfig;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.SysConfigVo;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 系统配置(SysConfig)表服务接口
 *
 * @author wangYan
 * @since 2023-07-20
 */
@Service
public class SysConfigService extends BaseService<SysConfig> {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ConfigDeviceTypeService configDeviceTypeService;
    @Autowired
    private DeviceCollectService deviceCollectService;

    public Page<SysConfig> getPage(SysConfigVo sysConfig, Integer pageIndex, Integer pageSize) {
        Page<SysConfig> p = baseMapper.selectPage(new Page<>(pageIndex, pageSize), getWrapper(sysConfig));

        Map<Integer, SysUser> userMap = (Map<Integer, SysUser>) RedisUtils.getBeanValue("userDict");
        Map<Integer, ConfigDeviceType> configDeviceTypeMap = configDeviceTypeService.list(Wrappers.<ConfigDeviceType>lambdaQuery().eq(ConfigDeviceType::getIsDel, 0)).stream().collect(Collectors.toMap(ConfigDeviceType::getId, Function.identity()));
        Map<Integer, DeviceCollect> deviceCollectMap = deviceCollectService.list(Wrappers.<DeviceCollect>lambdaQuery().eq(DeviceCollect::getIsDel, 0)).stream().collect(Collectors.toMap(DeviceCollect::getId, Function.identity()));
        p.getRecords().forEach(item -> {
            SysUser user = userMap.get(item.getCreateUser());
            if (null != user) {
                item.setCreateUserName(user.getRealName());
            }
            if (null != item.getDeviceTypeId()) {
                ConfigDeviceType configDeviceType = configDeviceTypeMap.get(item.getDeviceTypeId());
                if (null != configDeviceType) {
                    item.setDeviceTypeName(configDeviceType.getDeviceTypeName());
                }
            }
            if (null != item.getDeviceId()) {
                String[] deviceIds = item.getDeviceId().split(",");
                List deviceIdList = new ArrayList<>();
                String[] deviceName = new String[deviceIds.length];
                for (int i = 0; i < deviceIds.length; i++) {
                    DeviceCollect deviceCollect = deviceCollectMap.get(Integer.parseInt(deviceIds[i]));
                    if (null != deviceCollect) {
                        deviceIdList.add(deviceCollect.getId());
                        deviceName[i] = deviceCollect.getName();
                    }
                }
                item.setDeviceIdList(deviceIdList);
                item.setDeviceName(StringUtils.join(deviceName, ","));
            }
        });
        return p;
    }

    /**
     * 封装查询条件
     *
     * @param sysConfig
     * @return
     */
    private LambdaQueryWrapper<SysConfig> getWrapper(SysConfigVo sysConfig) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        if (null != sysConfig) {
            if (StringUtils.isNotBlank(sysConfig.getConfigName())) {
                wrapper.like(SysConfig::getConfigName, sysConfig.getConfigName());
            }
            if (StringUtils.isNotBlank(sysConfig.getConfigValue())) {
                wrapper.like(SysConfig::getConfigValue, sysConfig.getConfigValue());
            }
            if (StringUtils.isNotBlank(sysConfig.getConfigSign())) {
                wrapper.like(SysConfig::getConfigSign, sysConfig.getConfigSign());
            }
            if (StringUtils.isNotBlank(sysConfig.getMemo())) {
                wrapper.like(SysConfig::getMemo, sysConfig.getMemo());
            }
            if (null != sysConfig.getDeviceTypeId()) {
                wrapper.eq(SysConfig::getDeviceTypeId, sysConfig.getDeviceTypeId());
            }
        }
        wrapper.orderByDesc(SysConfig::getCreateTime);
        wrapper.eq(SysConfig::getIsDel, G.ISDEL_NO);
        return wrapper;
    }

    /**
     * 保存系统配置
     *
     * @param sysConfig
     * @return
     */
    public Object saveConfig(SysConfigVo sysConfig) {
        //校验
        list(Wrappers.<SysConfig>lambdaQuery()
                .eq(SysConfig::getConfigName, sysConfig.getConfigName())
                .eq(SysConfig::getIsDel, G.ISDEL_NO)).stream().findFirst().ifPresent(config -> {
            if (null == sysConfig.getId() || !sysConfig.getId().equals(config.getId())) {
                throw new RuntimeException("配置名称已存在");
            }
        });
        list(Wrappers.<SysConfig>lambdaQuery()
                .eq(SysConfig::getConfigSign, sysConfig.getConfigSign())
                .eq(SysConfig::getIsDel, G.ISDEL_NO)).stream().findFirst().ifPresent(config -> {
            if (null == sysConfig.getId() || !sysConfig.getId().equals(config.getId())) {
                throw new RuntimeException("配置标识已存在");
            }
        });
        // 数据组装
        sysConfig.setIsDel(G.ISDEL_NO);
        if (null != sysConfig && null != sysConfig.getId() && null != sysConfig.selectById(sysConfig.getId())) {
            sysConfig.setUpdateTime(new Date());
            sysConfig.setUpdateUser(sysUserService.getUser().getId());
        } else {
            sysConfig.setCreateTime(new Date());
            sysConfig.setCreateUser(sysUserService.getUser().getId());
        }
        sysConfig.setDeviceId(StringUtils.join(sysConfig.getDeviceIdList(), ","));
        saveOrUpdate(sysConfig);
        return sysConfig;
    }
}

