package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.ConfigDeviceSignalMapMapper;
import com.jsdc.iotpt.model.ConfigDeviceSignalMap;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.ConfigDeviceSignalMapVo;
import com.jsdc.iotpt.vo.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConfigDeviceSignalMapService extends BaseService<ConfigDeviceSignalMap> {

    @Autowired
    private ConfigDeviceSignalMapMapper configDeviceSignalMapMapper;

    @Autowired
    private DeviceControlService deviceControlService;


    /**
     * 分页查询 todo
     *
     * @return
     */
    public Page<ConfigDeviceSignalMap> getPageList(ConfigDeviceSignalMapVo vo) {
        QueryWrapper<ConfigDeviceSignalMap> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel",0);
        //testMapper.getEntityList(new Page<>(vo.getPageNo(), vo.getPageSize()),vo);
        return configDeviceSignalMapMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);

    }

    /**
     * 查询 todo
     *
     * @return
     */
    public List<ConfigDeviceSignalMap> getList(ConfigDeviceSignalMap entity) {
        QueryWrapper<ConfigDeviceSignalMap> queryWrapper = new QueryWrapper<>();
        return configDeviceSignalMapMapper.selectList(queryWrapper);
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    public ResultInfo saveOrUpdateConfigDeviceSignalMap(ConfigDeviceSignalMapVo bean) {

        List<ConfigDeviceSignalMap> configDeviceSignalMaps=configDeviceSignalMapMapper.
                selectList(Wrappers.<ConfigDeviceSignalMap>lambdaQuery().
                        eq(ConfigDeviceSignalMap::getDeviceTypeId,bean.getDeviceTypeId()).
                        eq(ConfigDeviceSignalMap::getIsDel,0));

        List<Integer> oldIds=configDeviceSignalMaps.stream().map(x->x.getId()).collect(Collectors.toList());


        //删除原来绑定的信号类型
        ConfigDeviceSignalMap deviceSignalMap=new ConfigDeviceSignalMap();
        deviceSignalMap.setIsDel(1);
        LambdaQueryWrapper<ConfigDeviceSignalMap> wrapper=new LambdaQueryWrapper();
        wrapper.eq(ConfigDeviceSignalMap::getDeviceTypeId,bean.getDeviceTypeId());
        update(deviceSignalMap,wrapper);
        //绑定新的信号类型
        List<String> ids= StringUtils.str2List(bean.getSIds(),",",false,false);
        for (String id : ids) {
            ConfigDeviceSignalMap configDeviceSignalMap=new ConfigDeviceSignalMap();
            configDeviceSignalMap.setDeviceTypeId(bean.getDeviceTypeId());
            configDeviceSignalMap.setSignalTypeId(Integer.parseInt(id));
            configDeviceSignalMap.setIsDel(0);
            configDeviceSignalMap.setCreateTime(new Date());
            saveOrUpdate(configDeviceSignalMap);
        }
        deviceControlService.editByDeviceTypeAndSignal(bean.getDeviceTypeId(),ids,oldIds);
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
     * 根据设备类型查询绑定的信号类型 todo
     *
     * @param id
     * @return
     */
    public ResultInfo getEntityByTId(Integer id) {
        return ResultInfo.success(this.configDeviceSignalMapMapper.getEntityByTId(id));
    }

}


