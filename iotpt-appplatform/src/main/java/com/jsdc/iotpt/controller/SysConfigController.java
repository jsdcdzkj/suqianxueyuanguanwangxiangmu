package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.model.sys.SysConfig;
import com.jsdc.iotpt.service.SysConfigService;
import com.jsdc.iotpt.vo.LogVo;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.SysConfigVo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;

/**
 * 系统配置(SysConfig)表控制层
 *
 * @author wangYan
 * @since 2023-07-20
 */
@RestController
@RequestMapping("/sysConfig")
public class SysConfigController {
    /**
     * 服务对象
     */
    @Resource
    private SysConfigService sysConfigService;

    /**
     * 分页查询所有数据
     *
     * @param sysConfig 查询实体
     * @return 所有数据
     */
    @RequestMapping("/getPage")
    public ResultInfo getPage(@RequestBody SysConfigVo sysConfig, @RequestParam(name = "pageNo", required = false, defaultValue = "1") Integer pageIndex,
                              @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return ResultInfo.success(this.sysConfigService.getPage(sysConfig, pageIndex, pageSize));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @RequestMapping("{id}")
    public ResultInfo selectOne(@PathVariable Serializable id) {
        return ResultInfo.success(this.sysConfigService.getById(id));
    }

    /**
     * 新增编辑数据
     *
     * @param sysConfig 实体对象
     * @return 结果
     */
    @RequestMapping("/saveOrUpdate")
    @LogInfo(LogEnums.LOG_SYSTEM_CONFIG)
    public ResultInfo saveOrUpdate(@RequestBody SysConfigVo sysConfig) {
        if (null == sysConfig.getId()) {
            return ResultInfo.success(this.sysConfigService.saveConfig(sysConfig), new LogVo("新增系统配置:" + sysConfig.getConfigName()));
        } else {
            return ResultInfo.success(this.sysConfigService.saveConfig(sysConfig), new LogVo("编辑系统配置:" + sysConfig.getConfigName()));
        }
    }

    /**
     * 删除数据
     *
     * @param sysConfig 实体对象
     * @return 删除结果
     */
    @RequestMapping("/deleteConfig")
    @LogInfo(LogEnums.LOG_SYSTEM_CONFIG)
    public ResultInfo deleteConfig(@RequestBody SysConfig sysConfig) {
        return ResultInfo.success(sysConfig.updateById(), new LogVo("删除系统配置:" + sysConfig.getConfigName()));
    }
}

