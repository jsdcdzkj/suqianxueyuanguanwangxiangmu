package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.init.RedisDataInit;
import com.jsdc.iotpt.model.sys.SysDict;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * (SysDict)表服务接口
 *
 * @author wangYan
 * @since 2023-05-09
 */
@Service
@Transactional
public class SysDictService extends BaseService<SysDict> {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private RedisDataInit redisDataInit;

    @Autowired
    private MemoryCacheService memoryCacheService;
    /**
     * @description 查询所有字典数据
     */
    public List<SysDict> getAllDicts() {
        LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDict::getIsDel, "0");
        return list(wrapper);
    }

    /**
     * @description 通过类型和值
     */
    public SysDict selectByValueAndType(String dict_type, String value) {
        QueryWrapper<SysDict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dictType", dict_type);
        queryWrapper.eq("dictValue", value);
        queryWrapper.eq("isDel", "0");
        return getOne(queryWrapper);
    }

    /**
     * @description 通过类型
     */
    public List<SysDict> selectByType(String dict_type) {
        QueryWrapper<SysDict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dictType", dict_type);
        queryWrapper.eq("isDel", "0");
        return list(queryWrapper);
    }

    /**
     * @description 通过类型 匹配之一
     */
    public boolean compareDictValueIsContains(String dict_type,String value) {
        QueryWrapper<SysDict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dictType", dict_type);
        queryWrapper.eq("isDel", "0");
        List<SysDict> list = list(queryWrapper);
        SysDict sysDict = list.get(0);
        List<String> collect = Arrays.stream(sysDict.getDictValue().split(",")).collect(Collectors.toList());
        return collect.contains(value);
    }

    /**
     * @description 通过值
     */
    public SysDict selectByValue(String value) {
        QueryWrapper<SysDict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("value", value);
        queryWrapper.eq("isDel", "0");
        return getOne(queryWrapper);
    }

    /**
     * 保存数据字典
     * @param sysDict
     * @return
     */
    public Object saveDict(SysDict sysDict) {
        // 校验
        list(Wrappers.<SysDict>lambdaQuery().eq(SysDict::getDictValue, sysDict.getDictValue())
                .eq(SysDict::getDictType, sysDict.getDictType()).eq(SysDict::getIsDel, G.ISDEL_NO)).stream().findFirst().ifPresent(dict -> {
            if (null == sysDict.getId() || !sysDict.getId().equals(dict.getId())) {
                throw new RuntimeException("字典值已存在");
            }
        });
        list(Wrappers.<SysDict>lambdaQuery().eq(SysDict::getDictLabel, sysDict.getDictLabel())
                .eq(SysDict::getDictType, sysDict.getDictType()).eq(SysDict::getIsDel, G.ISDEL_NO)).stream().findFirst().ifPresent(dict -> {
            if (null == sysDict.getId() || !sysDict.getId().equals(dict.getId())) {
                throw new RuntimeException("字典标签已存在");
            }
        });
        // 数据组装
        sysDict.setIsDel(G.ISDEL_NO);
        sysDict.setParentId(null == sysDict.getParentId() ? -1 : sysDict.getParentId());
        if (null != sysDict && null != sysDict.getId() && null != sysDict.selectById(sysDict.getId())){
            sysDict.setUpdateTime(new Date());
            sysDict.setUpdateUser(sysUserService.getUser().getId());
        }else {
            sysDict.setCreateTime(new Date());
            sysDict.setCreateUser(sysUserService.getUser().getId());
        }
        saveOrUpdate(sysDict);
        redisDataInit.updateDictCache(sysDict);


        memoryCacheService.putAllDict();
        return sysDict;
    }


    public void delete(SysDict sysDict) {
        sysDict.setIsDel(G.ISDEL_YES);
        saveOrUpdate(sysDict);
        redisDataInit.updateDictCache(sysDict);
        memoryCacheService.putAllDict();
    }

    public List<SysDict> getList(SysDict sysDict) {
        QueryWrapper queryWrapper=new QueryWrapper();

        queryWrapper.eq("isDel",0);

        if (Strings.isNotEmpty(sysDict.getDictType())){
            queryWrapper.eq("dictType",sysDict.getDictType());
        }

        return getBaseMapper().selectList(queryWrapper);
    }
}

