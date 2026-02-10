package com.jsdc.iotpt.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.sys.SysBuild;
import com.jsdc.iotpt.model.sys.SysBuildArea;
import com.jsdc.iotpt.model.sys.SysBuildFloor;
import com.jsdc.iotpt.model.sys.SysOrgDept;
import com.jsdc.iotpt.service.*;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/sys_org_dept")
public class SysOrgDeptController {
    @Autowired
    private SysOrgDeptService sysOrgDeptService;

    @Autowired
    private SysBuildAreaService sysBuildAreaService;
    @Autowired
    private SysBuildFloorService sysBuildFloorService;

    @Autowired
    private SysBuildService sysBuildService ;


    /**
     * 组织单位新增
     * Author wzn
     * Date 2023/5/9 9:14
     */
    @PostMapping("/addOrgDept")
    public ResultInfo addOrgDept(@RequestBody SysOrgDept sysOrgDept) {
        ResultInfo resultInfo = sysOrgDeptService.addOrgDept(sysOrgDept);
        return resultInfo;
    }


    /**
     * 组织单位修改
     * Author wzn
     * Date 2023/5/9 9:15
     */
    @PostMapping("/updateOrgDept")
    public ResultInfo updateBuild(@RequestBody SysOrgDept sysOrgDept) {
        ResultInfo resultInfo =  sysOrgDeptService.updateOrgDept(sysOrgDept);
        return resultInfo;
    }

    /**
     * 组织单位删除
     * Author wzn
     * Date 2023/5/9 9:17
     */
    @PostMapping("/delOrgDept")
    public ResultInfo delBuild(Integer id) {
        if (sysOrgDeptService.exist(id)) {
            throw new CustomException("该部门已被使用,禁止删除！");
        }

        SysOrgDept sysOrgDept1 = new SysOrgDept();
        sysOrgDept1.setId(id);
        sysOrgDept1.setIsDel(1);
        sysOrgDeptService.updateById(sysOrgDept1);
        return ResultInfo.success();
    }




    /**
     * 组织单位列表
     * Author wzn
     * Date 2023/5/9 9:17
     */
    @PostMapping("/selectOrgDeptList")
    public ResultInfo selectBuildList(@RequestBody SysOrgDept sysOrgDept) {
        Page<SysOrgDept> sysOrgDeptPage = null;
        try {
            sysOrgDeptPage = sysOrgDeptService.selectOrgDeptList(sysOrgDept);
            if (CollectionUtil.isNotEmpty(sysOrgDeptPage.getRecords())) {
                for (SysOrgDept s : sysOrgDeptPage.getRecords()) {
                    if (StringUtils.isNotEmpty(s.getIds())) {
                        List<String> split = Arrays.asList(s.getIds().split(","));
                        StringBuffer areaName = new StringBuffer();
                        for (String st : split) {
                            SysBuildArea sysBuildArea = sysBuildAreaService.getById(st);
                            if (null != sysBuildArea) {
                                //查楼层
                                SysBuildFloor sysBuildFloor = sysBuildFloorService.getById(sysBuildArea.getFloorId());
                                if (null != sysBuildFloor) {
                                    SysBuild sysBuild = sysBuildService.getById(sysBuildFloor.getDictBuilding()) ;
                                    if(null != sysBuild){
                                        areaName.append(sysBuild.getBuildName()+"/"+sysBuildFloor.getFloorName()+"/"+sysBuildArea.getAreaName()).append(" , ") ;
                                        String orgName = areaName.toString().substring(0, areaName.length() - 2);
                                        s.setOrgName(orgName);
                                    }
                                }

                            }

                        }

                    }


                }
            }
        } catch (Exception e) {
            return ResultInfo.error(e.getMessage());
        }
        return ResultInfo.success(sysOrgDeptPage);
    }


    /**
     * 组织单位详情
     * Author wzn
     * Date 2023/5/9 9:18
     */
    @PostMapping("/info")
    public ResultInfo info(String id) {
        SysOrgDept sysOrgDept = sysOrgDeptService.info(id);
        List<List<Object>> list = new ArrayList<>();
        if (null != sysOrgDept) {
            if(StringUtils.isNotEmpty(sysOrgDept.getIds())){
                List<String> result = Arrays.asList(sysOrgDept.getIds().split(","));
                for (String s : result) {
                    ArrayList arrayList = new ArrayList();
                    //用区域查楼层'
                    SysBuildArea sysBuildArea = sysBuildAreaService.getById(s);
                    if (sysBuildArea != null) {
                        //查楼宇
                        SysBuildFloor sysBuildFloor = sysBuildFloorService.getById(sysBuildArea.getFloorId());
                        if (null != sysBuildFloor) {

                            arrayList.add(sysBuildFloor.getDictBuilding());
                            arrayList.add(sysBuildArea.getFloorId());
                            arrayList.add(s);
                            list.add(arrayList);
                        }
                    }
                }
            }

            sysOrgDept.setShowData(list);
        }
        return ResultInfo.success(sysOrgDept);
    }

    /**
    *部门无限极
    * Author wzn
    * Date 2023/7/17 10:46
    */
    @PostMapping("/findDept")
    public ResultInfo findDept() {
        List<SysOrgDept> sysOrgDepts = sysOrgDeptService.findDept();
        return ResultInfo.success(sysOrgDepts);
    }



    /**
     * 无结构
     * 查询所有部门 查询列表无结构
     * @return
     */
    @PostMapping("/findAllDept")
    public ResultInfo findAllDept() {
        List<SysOrgDept> sysOrgDepts = sysOrgDeptService.findAllDept(new SysOrgDept());
        return ResultInfo.success(sysOrgDepts);
    }

    /**
     * 无结构
     * 根据组织单位查询所有部门
     * @return
     */
    @PostMapping("/findAllDeptByUnit")
    public ResultInfo findAllDeptByUnit(@RequestBody  SysOrgDept dept) {
        List<SysOrgDept> sysOrgDepts = sysOrgDeptService.findAllDept(dept);
        return ResultInfo.success(sysOrgDepts);
    }
}
