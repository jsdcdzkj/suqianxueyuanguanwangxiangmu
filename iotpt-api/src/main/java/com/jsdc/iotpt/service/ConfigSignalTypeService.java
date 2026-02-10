package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.mapper.ConfigDeviceSignalMapMapper;
import com.jsdc.iotpt.mapper.ConfigSignalTypeItemMapper;
import com.jsdc.iotpt.mapper.ConfigSignalTypeMapper;
import com.jsdc.iotpt.model.ConfigDeviceSignalMap;
import com.jsdc.iotpt.model.ConfigDeviceType;
import com.jsdc.iotpt.model.ConfigSignalType;
import com.jsdc.iotpt.model.ConfigSignalTypeItem;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.ConfigSignalTypeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @projectName: IOT
 * @className: ConfigSignalTypeService
 * @author: wp
 * @description:
 * @date: 2023/5/9 8:38
 */
@Service
@Transactional
public class ConfigSignalTypeService extends BaseService<ConfigSignalType> {

    @Autowired
    private ConfigSignalTypeMapper  configSignalTypeMapper;
    @Autowired
    private ConfigDeviceSignalMapMapper configDeviceSignalMapMapper;
    @Autowired
    private ConfigSignalTypeItemMapper itemMapper;
    @Autowired
    private SysUserService userService;


    /**
     * 条件查询信号类型 todo
     *
     * @param signalTypeName
     * @return
     */
    public List<ConfigSignalType> getSignalType(String signalTypeName,Integer id) {
        LambdaQueryWrapper<ConfigDeviceSignalMap> cdsWrapper=new LambdaQueryWrapper<>();
        cdsWrapper.eq(ConfigDeviceSignalMap::getDeviceTypeId,id).eq(ConfigDeviceSignalMap::getIsDel,0);
        List<ConfigDeviceSignalMap>list = configDeviceSignalMapMapper.selectList(cdsWrapper);
        List<Integer> ids=new ArrayList<>();
        for (ConfigDeviceSignalMap configDeviceSignalMap : list) {
            ids.add(configDeviceSignalMap.getSignalTypeId());
        }
        LambdaQueryWrapper<ConfigSignalType> wrapper=new LambdaQueryWrapper();
        wrapper.like(StringUtils.isNotEmpty(signalTypeName),ConfigSignalType::getSignalTypeName,signalTypeName)
                .eq(ConfigSignalType::getIsDel,0).notIn(StringUtils.isNotEmpty(ids),ConfigSignalType::getId,ids);
        return configSignalTypeMapper.selectList(wrapper);
    }

    /**
     * 根据设备类型编码获取信号类型
     */
    public List<ConfigSignalType> getEntityByTCode(ConfigDeviceType bean) {
        return configDeviceSignalMapMapper.getEntityByTCode(bean);
    }



    /**
     * 分页查询
     * @param pageNo
     * @param pageSize
     * @param configSignalType
     * @return
     */
    public Page<ConfigSignalType> getPage(Integer pageNo, Integer pageSize, ConfigSignalType configSignalType){
        Page page = new Page<>(pageNo, pageSize);
        Page<ConfigSignalType> p = baseMapper.selectPage(page, getWrapper(configSignalType));

        return p;
    }

    /**
     * 列表查询
     * @param type
     * @return
     */
    public List<ConfigSignalType> getList(ConfigSignalType type){
        List<ConfigSignalType> list = baseMapper.selectList(getWrapper(type));
        return list;
    }

    /**
     * 对象查询
     * @param type
     * @return
     */
    public ConfigSignalType getEntity(ConfigSignalType type){
        ConfigSignalType configSignalType = getById(type.getId());
        List<ConfigSignalTypeItem> configSignalTypeItems=itemMapper.
                selectList(Wrappers.<ConfigSignalTypeItem>lambdaQuery().
                        eq(ConfigSignalTypeItem::getTypeId,type.getId()).
                        eq(ConfigSignalTypeItem::getIsDel,0));
        configSignalType.setItems(configSignalTypeItems);
        return configSignalType;
    }

    /**
     * 新增/编辑
     * @param configSignalType
     * @return
     */
    public Boolean saveOrUpdateSignType(ConfigSignalType configSignalType){
        if(null == configSignalType.getId()){
            LambdaQueryWrapper<ConfigSignalType> wrapper=new LambdaQueryWrapper<>();
            wrapper.eq(ConfigSignalType::getSignalTypeName,configSignalType.getSignalTypeName()).or().eq(ConfigSignalType::getSignalTypeCode,configSignalType.getSignalTypeCode());
            wrapper.eq(ConfigSignalType::getIsDel,0);
            if (this.configSignalTypeMapper.selectCount(wrapper)>0){
                return false;
            }
            configSignalType.setIsDel(G.ISDEL_NO);
            configSignalType.setCreateTime(new Date());
            configSignalType.setUpdateTime(new Date());
            configSignalType.setCreateUser(userService.getUser().getId());
            configSignalType.setUpdateUser(userService.getUser().getId());
        }else{
            LambdaQueryWrapper<ConfigSignalType> wrapper=new LambdaQueryWrapper<>();
            wrapper.and(i->i.eq(ConfigSignalType::getSignalTypeName,configSignalType.getSignalTypeName()).or().eq(ConfigSignalType::getSignalTypeCode,configSignalType.getSignalTypeCode()));
            wrapper.eq(ConfigSignalType::getIsDel,0);
            wrapper.ne(ConfigSignalType::getId,configSignalType.getId());
            if (this.configSignalTypeMapper.selectCount(wrapper)>0){
                return false;
            }
            configSignalType.setUpdateTime(new Date());
            configSignalType.setUpdateUser(userService.getUser().getId());
        }

        boolean flag=saveOrUpdate(configSignalType);


        itemMapper.update(null,Wrappers.<ConfigSignalTypeItem>lambdaUpdate().set(ConfigSignalTypeItem::getIsDel,1).
                eq(ConfigSignalTypeItem::getTypeId,configSignalType.getId()));


        configSignalType.getItems().forEach(x->{
            x.setTypeId(configSignalType.getId());
            itemMapper.insert(x);
        });

        return flag;
    }

    /**
     * 删除
     * @param type
     * @return
     */
    public Boolean delete(ConfigSignalType type){
        ConfigSignalType configSignalType = getById(type.getId());
        configSignalType.setIsDel(G.ISDEL_YES);
        configSignalType.setUpdateUser(userService.getUser().getId());
        configSignalType.setUpdateTime(new Date());
        return updateById(configSignalType);
    }

    /**
     * 封装查询条件
     * @param type
     * @return
     */
    private LambdaQueryWrapper<ConfigSignalType> getWrapper(ConfigSignalType type){
        LambdaQueryWrapper<ConfigSignalType> wrapper = new LambdaQueryWrapper<>();
        if(null != type){
            wrapper.like(StringUtils.isNotEmpty(type.getSignalTypeName()), ConfigSignalType::getSignalTypeName, type.getSignalTypeName());
            wrapper.like(StringUtils.isNotEmpty(type.getSignalTypeCode()), ConfigSignalType::getSignalTypeCode, type.getSignalTypeCode());
        }
        wrapper.eq(ConfigSignalType::getIsDel, G.ISDEL_NO);
        return wrapper;
    }
}
