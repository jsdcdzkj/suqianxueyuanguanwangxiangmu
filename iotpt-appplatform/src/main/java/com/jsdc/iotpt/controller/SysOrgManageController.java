package com.jsdc.iotpt.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.sys.SysOrgManage;
import com.jsdc.iotpt.service.SysOrgManageService;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/sys_org_manage")
public class SysOrgManageController {
    @Autowired
    private SysOrgManageService sysOrgManageService;


    /**
     * 组织单位新增
     * Author wzn
     * Date 2023/5/9 9:14
     */
    @PostMapping("/addOrgManage")
    public ResultInfo addOrgManage(@RequestBody SysOrgManage sysOrgManage) {
        boolean check = sysOrgManageService.checkOnly(sysOrgManage);
        if (!check) {
            throw new CustomException("单位已存在,禁止重复添加！");
        }
        return ResultInfo.success(sysOrgManageService.addOrgManage(sysOrgManage));
    }


    /**
     * 组织单位修改
     * Author wzn
     * Date 2023/5/9 9:15
     */
    @PostMapping("/updateOrgManage")
    public ResultInfo updateBuild(@RequestBody SysOrgManage sysOrgManage) {
        boolean check = sysOrgManageService.checkOnly(sysOrgManage);
        if (!check) {
            throw new CustomException("单位已存在,禁止重复添加！");
        }
        sysOrgManageService.updateOrgManage(sysOrgManage);
        return ResultInfo.success();
    }

    /**
     * 组织单位删除
     * Author wzn
     * Date 2023/5/9 9:17
     */
    @PostMapping("/delOrgManage")
    public ResultInfo delBuild(Integer id) {
        if (sysOrgManageService.exist(id + "")) {
            throw new CustomException("请先删除子公司/部门！");
        }

        SysOrgManage sysOrgManage1 = new SysOrgManage();
        sysOrgManage1.setId(id);
        sysOrgManage1.setIsDel(1);
        sysOrgManageService.updateById(sysOrgManage1);
        return ResultInfo.success();
    }

    /**
     * 组织单位列表
     * Author wzn
     * Date 2023/5/9 9:17
     */
    @PostMapping("/selectBuildList")
    public ResultInfo selectBuildList(@RequestBody SysOrgManage sysOrgManage) {
        Page<SysOrgManage> sysOrgManagePage = sysOrgManageService.selectOrgManageList(sysOrgManage);
        return ResultInfo.success(sysOrgManagePage);
    }


    /**
     * 组织单位详情
     * Author wzn
     * Date 2023/5/9 9:18
     */
    @PostMapping("/info")
    public ResultInfo info(String id) {
        SysOrgManage sysOrgManage = sysOrgManageService.info(id);
        return ResultInfo.success(sysOrgManage);
    }


    /**
     * 单位树形图结构
     * Author wzn
     * Date 2023/6/30 8:58
     */
    @PostMapping("/orgTreeList")
    public ResultInfo orgTreeList() {
        return ResultInfo.success(sysOrgManageService.orgTreeList());
    }

    /**
     * 区域三级树形图
     * Author wzn
     * Date 2023/6/30 10:11
     */
    @PostMapping("/areaTreeList")
    public ResultInfo areaTreeList() {
        return ResultInfo.success(sysOrgManageService.areaTreeList());
    }

    /**
     * 区域三级树形图
     * Author thr
     * Date 2023/6/30 10:11
     */
    @PostMapping("/areaTreeList2")
    public ResultInfo areaTreeList2() {
        return ResultInfo.success(sysOrgManageService.areaTreeList2());
    }

    /**
     *公司无限极
     * Author wzn
     * Date 2023/7/17 10:46
     */
    @PostMapping("/findOrg")
    public ResultInfo findOrg() {
        List<SysOrgManage> sysOrgManages = sysOrgManageService.findOrg();
        return ResultInfo.success(sysOrgManages);
    }


    /**
     *
     *商户
     * @author wzn
     * @date 2025/10/13 11:14
     */
    @GetMapping("/findMerchant")
    public ResultInfo findMerchant(String orgName) {
        List<SysOrgManage> sysOrgManages = sysOrgManageService.findOrg2(orgName);
        return ResultInfo.success(sysOrgManages);
    }
}
