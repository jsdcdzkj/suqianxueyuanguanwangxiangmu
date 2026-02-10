package com.jsdc.iotpt.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.sys.SysOrgManage;
import com.jsdc.iotpt.service.SysOrgManageService;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 单位
 * Description:
 * date: 2023/11/28 11:29
 *
 * @author bn
 */
@RestController
@RequestMapping("/app/sysOrgManage")
public class SysOrgManageController {

    @Autowired
    private SysOrgManageService sysOrgManageService;

    /**
     *  单位列表接口
     * @param sysOrgManage
     * @return
     */
    @PostMapping("/getList")
    @ApiOperation(value = "单位列表", httpMethod = "POST")
    public ResultInfo getList(SysOrgManage sysOrgManage){

        try {
            return ResultInfo.success( sysOrgManageService.getList(sysOrgManage));
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("获取列表失败");
        }
    }



    /**
     * 多维分析树状图专用
     * 区域三级树形图
     */
    @PostMapping("/areaTreeList")
    @ApiOperation("多维分析树状图专用")
    public ResultInfo getAreaTree(){
        try {
            return ResultInfo.success(sysOrgManageService.areaTreeList3(null));
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("获取列表失败");
        }
    }

}
