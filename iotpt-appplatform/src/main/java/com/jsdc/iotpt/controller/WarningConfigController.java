package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.common.Constants;
import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.model.WarningConfig;
import com.jsdc.iotpt.service.WarningConfigService;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.WarningConfigVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 告警配置 controller控制器
 *
 * @author lb
 */
@RestController
@RequestMapping("/warningConfig")
@Api(tags = "告警配置")
public class WarningConfigController {

    @Autowired
    WarningConfigService warningConfigService;

    /**
     * 告警配置分页查询
     *
     * @return
     */
    @RequestMapping("/getPageList")
    @ApiOperation("告警配置分页查询")
    public ResultInfo getPageList(WarningConfigVo bean) {
        return ResultInfo.success(warningConfigService.getPageList(bean));
    }

    @RequestMapping("/getWarningConfig")
    @ApiOperation("告警配置编辑页面")
    public ResultInfo getWarningConfig(WarningConfigVo bean) {
        ResultInfo resultInfo = warningConfigService.getWarningConfigEdit(bean);
        return ResultInfo.success(resultInfo);
    }

    /**
     * 添加/编辑
     *
     * @param bean
     * @return
     */
    @LogInfo(value = LogEnums.LOG_WARNING_CONFIG_UPDATE, model = Constants.MODEL_YYZT)
    @RequestMapping("/saveOrUpdate")
    @ApiOperation("告警配置添加/编辑")
    public ResultInfo saveOrUpdateWarningConfig(@RequestBody WarningConfigVo bean) {
        if (null != bean && null == bean.getId()) {
            return warningConfigService.saveWarningConfig(bean);
        } else if (null != bean && null != bean.getId()) {
            return warningConfigService.editWarningConfig(bean);
        }
        return ResultInfo.error("数据错误");
    }

    /**
     * 获取实体类
     *
     * @param bean
     * @return
     */
    @RequestMapping("/getEntity")
    @ApiOperation("告警配置获取实体类")
    public ResultInfo getEntity(WarningConfigVo bean) {
        return warningConfigService.getEntityById(bean.getId());
    }


    @LogInfo(value = LogEnums.LOG_WARNING_CONFIG_DELETE, model = Constants.MODEL_YYZT)
    @RequestMapping("/delEntity")
    @ApiOperation("告警配置删除")
    public ResultInfo delEntity(Integer id) {
        return warningConfigService.delEntity(id);
    }

}
