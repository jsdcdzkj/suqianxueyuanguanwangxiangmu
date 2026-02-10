package com.jsdc.iotpt.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.init.RedisDataInit;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.service.SysDictService;
import com.jsdc.iotpt.service.SysUserService;
import com.jsdc.iotpt.util.RedisDictDataUtil;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.LogVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * (SysDict)表控制层
 *
 * @author wangYan
 * @since 2023-05-10
 */
@RestController
@RequestMapping("sysDict")
@Api(tags = "数据字典")
public class SysDictController {
    /**
     * 服务对象
     */
    @Resource
    private SysDictService sysDictService;
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private RedisDataInit redisDataInit;

    /**
     * 分页查询所有数据
     *
     * @param sysDict 查询实体
     * @return 所有数据
     */
    @PostMapping("/getPage")
    @ApiOperation("数据字典分页查询")
    public ResultInfo getPage(@RequestParam(required = false, defaultValue = "1") Integer pageNo,
                              @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                              SysDict sysDict) {
        LambdaQueryWrapper<SysDict> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(null != sysDict.getParentId(), SysDict::getParentId, sysDict.getParentId());
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(sysDict.getDictLabel()), SysDict::getDictLabel, sysDict.getDictLabel());
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(sysDict.getDictTypeName()), SysDict::getDictTypeName, sysDict.getDictTypeName());
        lambdaQueryWrapper.eq(StringUtils.isNotEmpty(sysDict.getDictValue()), SysDict::getDictValue, sysDict.getDictValue());
        lambdaQueryWrapper.eq(StringUtils.isNotEmpty(sysDict.getDictType()), SysDict::getDictType, sysDict.getDictType());
        lambdaQueryWrapper.eq(SysDict::getIsDel, "0");
        lambdaQueryWrapper.orderByDesc(SysDict::getDictType).orderByAsc(SysDict::getSort);
//        lambdaQueryWrapper.orderByAsc(SysDict::getDictValue);
        Page<SysDict> dictPage = sysDictService.page(new Page<>(pageNo, pageSize), lambdaQueryWrapper);
        for (SysDict dict : dictPage.getRecords()) {
            if (null != dict.getParentId() && dict.getParentId() != -1) {
                SysDict parentDict = sysDictService.getById(dict.getParentId());
                dict.setParentName(parentDict.getDictLabel());
            }
        }
        return ResultInfo.success(dictPage);
    }
    /**
     * 树形结构查询
     */
    @PostMapping("/getTree")
    @ApiOperation("数据字典树形结构查询")
    public ResultInfo getTree(SysDict sysDict) {
        LambdaQueryWrapper<SysDict> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(null != sysDict.getParentId(), SysDict::getParentId, sysDict.getParentId());
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(sysDict.getDictLabel()), SysDict::getDictLabel, sysDict.getDictLabel());
        lambdaQueryWrapper.eq(StringUtils.isNotEmpty(sysDict.getDictValue()), SysDict::getDictValue, sysDict.getDictValue());
        lambdaQueryWrapper.eq(StringUtils.isNotEmpty(sysDict.getDictType()), SysDict::getDictType, sysDict.getDictType());
        lambdaQueryWrapper.eq(SysDict::getIsDel, "0");
        lambdaQueryWrapper.orderByDesc(SysDict::getDictType);
        List<SysDict> list = sysDictService.list(lambdaQueryWrapper);

        for (SysDict dict : list) {
            if (null != dict.getParentId() && dict.getParentId() != -1) {
                SysDict parentDict = sysDictService.getById(dict.getParentId());
                dict.setParentName(parentDict.getDictLabel());
            }
        }
        // 组装树形结构递归数据到children
        List<SysDict> treeList = RedisDictDataUtil.getTreeList(list, -1);
        return ResultInfo.success(treeList);
    }

    /**
     * 查询所有类型数据
     */
    @PostMapping("/getDictTypeList")
    @ApiOperation("数据字典查询")
    public ResultInfo getDictTypeList(SysDict sysDict) {
        LambdaQueryWrapper<SysDict> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysDict::getIsDel, "0");
        List<SysDict> list = sysDictService.list(lambdaQueryWrapper);
        // 根据字典类型去重
        List<String> types = list.stream().map(SysDict::getDictType).distinct().collect(Collectors.toList());
        return ResultInfo.success(types);
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @RequestMapping("/selectOne/{id}")
    @ApiOperation("数据字典获取实体类")
    public ResultInfo selectOne(@PathVariable Serializable id) {
        return ResultInfo.success(this.sysDictService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param sysDict 实体对象
     * @return 新增结果
     */
    @PostMapping("/saveOrUpdate")
    @ApiOperation("保存字典数据")
    @LogInfo(LogEnums.LOG_SYSTEM_DICT)
    public ResultInfo saveOrUpdate(@RequestBody SysDict sysDict) {
        try {
            sysDictService.saveDict(sysDict);
        } catch (Exception e) {
            return ResultInfo.error(e.getMessage());
        }
        return ResultInfo.success(new LogVo("保存【字典数据】" + sysDict.getDictLabel()));
    }

    /**
     * 得到所有数据list集合
     */
    @PostMapping("/getList")
    @ApiOperation("数据字典查询")
    public ResultInfo getList(SysDict sysDict) {
        LambdaQueryWrapper<SysDict> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysDict::getIsDel, "0");
        lambdaQueryWrapper.eq(StringUtils.isNotEmpty(sysDict.getDictType()), SysDict::getDictType, sysDict.getDictType());
        lambdaQueryWrapper.orderByDesc(SysDict::getCreateTime);
        return ResultInfo.success(sysDictService.list(lambdaQueryWrapper));
    }

    /**
     * 删除数据
     *
     * @return 删除结果
     */
    @PostMapping("/delete")
    @LogInfo(LogEnums.LOG_SYSTEM_DICT)
    public ResultInfo delete(Integer id) {
        if (null == id) {
            return ResultInfo.error("id不能为空");
        }
        LambdaUpdateWrapper<SysDict> wrapper = Wrappers.<SysDict>lambdaUpdate();
        wrapper.eq(SysDict::getId, id);
        wrapper.set(SysDict::getIsDel, G.ISDEL_YES);
        sysDictService.update(null, wrapper);
        SysDict sysDict = sysDictService.getById(id);
        redisDataInit.updateDictCache(sysDict);
        return ResultInfo.success(new LogVo("删除【字典数据】" + sysDict.getDictLabel()));
    }

    /**
     * 修改数据
     *
     * @param sysDict 实体对象
     * @return 修改结果
     */
    @RequestMapping("/update")
    @LogInfo(LogEnums.LOG_SYSTEM_DICT)
    public ResultInfo update(@RequestBody SysDict sysDict) {
        return ResultInfo.success(this.sysDictService.updateById(sysDict),new LogVo("修改【字典数据】" + sysDict.getDictLabel()));
    }



    /**
     * 新增修改数据
     *
     * @param sysDict 实体对象
     * @return 修改结果
     */
    @RequestMapping("/insertOrUpdate")
    @ApiOperation("数据字典添加/编辑")
    public ResultInfo insertOrUpdate(@RequestBody SysDict sysDict) {
        SysUser sysUser = sysUserService.getUser();
        if (sysDict.getId() == null) {
            sysDict.setCreateUser(sysUser.getId());
            sysDict.setCreateTime(new Date());
            sysDict.setIsDel(0);
        } else {
            sysDict.setUpdateUser(sysUser.getId());
            sysDict.setUpdateTime(new Date());
        }
        sysDictService.saveOrUpdate(sysDict);
        redisDataInit.updateDictCache(sysDict);
        return ResultInfo.success(new LogVo("保存【字典数据】" + sysDict.getDictLabel()));
    }

    /**
     * 协议管理列表 todo
     *
     * @param bean
     * @return
     */
    @PostMapping("/getRedisDictList")
    @ApiOperation("数据字典列表")
    public ResultInfo getRedisDictList(SysDict bean) {
        return ResultInfo.success(RedisDictDataUtil.getDictByType(bean.getDictType()));
    }

    /**
     * 得到redis数据字典返回map
     */
    @PostMapping("/getRedisDictMap")
    @ApiOperation("数据字典列表")
    public ResultInfo getRedisDictMap() {
        return ResultInfo.success(RedisUtils.getBeanValue("dictData"));
    }
    @PostMapping("/selectDictList")
    @ApiOperation("字典数据返回list")
    public ResultInfo selectDictList(SysDict bean) {
        Map<String, SysDict> dictByType = RedisDictDataUtil.getDictByType(bean.getDictType());
        return ResultInfo.success( dictByType.values());
    }

}

