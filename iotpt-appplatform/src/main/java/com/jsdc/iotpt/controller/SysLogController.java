package com.jsdc.iotpt.controller;


import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.sys.SysLog;
import com.jsdc.iotpt.service.SysLogService;
import com.jsdc.iotpt.service.SysUserService;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.SysLogVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;

/**
 * (SysLog)表控制层
 *
 * @author wangYan
 * @since 2023-05-10
 */
@RestController
@RequestMapping("sysLog")
@Api(tags = "系统日志")
public class SysLogController {
    /**
     * 服务对象
     */
    @Resource
    private SysLogService sysLogService;
    @Autowired
    private SysUserService sysUserService;

    /**
     * 分页查询所有数据
     *
     * @param sysLog 查询实体
     * @return 所有数据
     */
    @RequestMapping("/getPage")
    @ApiOperation("系统日志分页查询")
    public ResultInfo getPage(SysLogVo sysLog, @RequestParam(name = "pageNo", required = false, defaultValue = "1") Integer pageIndex,
                              @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return ResultInfo.success(sysLogService.getPage(sysLog, pageIndex, pageSize));
    }

    /**
     * 查询所有日志类型数据
     *
     * @return 所有数据
     */
    @RequestMapping("/getTypeAll")
    @ApiOperation("查询所有日志类型数据")
    public ResultInfo getTypeAll() {
        List list = new ArrayList<>();
        for (LogEnums l : LogEnums.values()) {
            Map map = new HashMap<>();
            map.put("dictValue",l.getValue());
            map.put("dictLabel",l.getDesc());
            list.add(map);
        }
        return ResultInfo.success(list);
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @RequestMapping("/selectOne/{id}")
    @ApiOperation("系统日志获取实体类")
    public ResultInfo selectOne(@PathVariable Serializable id) {
        return ResultInfo.success(this.sysLogService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param sysLog 实体对象
     * @return 新增结果
     */
    @RequestMapping("/insert")
    public ResultInfo insert(@RequestBody SysLog sysLog) {
        return ResultInfo.success(this.sysLogService.save(sysLog));
    }

    /**
     * 修改数据
     *
     * @param sysLog 实体对象
     * @return 修改结果
     */
    @RequestMapping("/update")
    public ResultInfo update(@RequestBody SysLog sysLog) {
        return ResultInfo.success(this.sysLogService.updateById(sysLog));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @RequestMapping("/delete")
    public ResultInfo delete(@RequestParam("idList") List<Long> idList) {
        return ResultInfo.success(this.sysLogService.removeByIds(idList));
    }

    /**
     * 新增修改数据
     *
     * @param sysLog 实体对象
     * @return 修改结果
     */
    @RequestMapping("/insertOrUpdate")
    @ApiOperation("系统日志添加/编辑")
    public ResultInfo insertOrUpdate(@RequestBody SysLog sysLog) {
        SysUser sysUser = sysUserService.getUser();
        if (sysLog.getId() == null) {
            sysLog.setCreateUser(sysUser.getId());
            sysLog.setCreateTime(new Date());
            sysLog.setIsDel(0);
        } else {
            sysLog.setUpdateUser(sysUser.getId());
            sysLog.setUpdateTime(new Date());
        }
        return ResultInfo.success(this.sysLogService.saveOrUpdate(sysLog));
    }
}

