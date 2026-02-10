package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.common.Constants;
import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.service.ConfigSupplierService;
import com.jsdc.iotpt.vo.ConfigModelVo;
import com.jsdc.iotpt.vo.ConfigSupplierVo;
import com.jsdc.iotpt.vo.LogVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * todo
 * controller控制器
 */
@RestController
@RequestMapping("/configSupplier")
@Api(tags = "供应商管理")
public class ConfigSupplierController {

    @Autowired
    private ConfigSupplierService configSupplierService;

    /**
     * 分页查询 todo
     *
     * @return
     */
    @RequestMapping("/getPageList")
    @ApiOperation("供应商-分页查询")
    public ResultInfo getPageList(ConfigSupplierVo bean) {
        return ResultInfo.success(configSupplierService.getPageList(bean));
    }

    @PostMapping("/getList")
    @ApiOperation("供应商列表")
    public ResultInfo getList(ConfigSupplierVo bean) {
        return ResultInfo.success(configSupplierService.getList(bean));
    }

    /**
     * 根据id查看
     */
    @RequestMapping("/getById")
    @ApiOperation("根据id查看供应商")
    public ResultInfo getById(ConfigSupplierVo bean) {
        return ResultInfo.success(configSupplierService.getEntityById(bean.getId()));
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    @RequestMapping("/saveOrUpdate")
    @ApiOperation("添加/编辑供应商")
    @LogInfo(value = LogEnums.LOG_CONFIG_SUPPLIER_SAVE, model = Constants.MODEL_YYZT)
    public ResultInfo saveOrUpdateDeviceCollect(@RequestBody ConfigSupplierVo bean) {
        try {
            configSupplierService.saveOrUpdateConfigSupplier(bean);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResultInfo.error(e.getMessage());
        }
        return ResultInfo.success(new LogVo("添加/编辑供应商"));
    }

    /**
     * 删除 todo
     */
    @RequestMapping("/delete")
    @ApiOperation("删除供应商")
    @LogInfo(value = LogEnums.LOG_CONFIG_SUPPLIER_DEL, model = Constants.MODEL_YYZT)
    public ResultInfo deleteDeviceCollect(ConfigSupplierVo bean) {
        configSupplierService.delConfigSupplier(bean.getId());
        return ResultInfo.success(new LogVo("删除供应商"));
    }

    // ------------------------------- 型号管理 -------------------------------
    /**
     * 得到供应商下面的型号管理列表 ,根据供应商id
     *
     * @return
     */
    @RequestMapping("/getModelList")
    @ApiOperation("供应商下面的型号管理列表")
    public ResultInfo getModelList(ConfigModelVo bean) {
        return configSupplierService.getModelList(bean);
    }

    /**
     * 得到供应商下面的设备管理
     */
    @RequestMapping("/getDeviceList")
    @ApiOperation("供应商的设备信息")
    public ResultInfo getDeviceList(ConfigSupplierVo bean) {
        return configSupplierService.getDeviceList(bean);
    }

}
