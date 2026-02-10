package com.jsdc.iotpt.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.common.Constants;
import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.model.SysMenu;
import com.jsdc.iotpt.model.sys.SysBuild;
import com.jsdc.iotpt.model.sys.SysBuildArea;
import com.jsdc.iotpt.model.sys.SysBuildFloor;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.service.*;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.SysBuildAreaVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/sys_build_area")
public class SysBuildAreaController {
    @Autowired
    private SysBuildAreaService sysBuildAreaService;

    @Autowired
    private SysBuildFloorService sysBuildFloorService;

    @Autowired
    private SysBuildService sysBuildService;

    @Autowired
    private SysDictService sysDictService ;


    /**
     * 区域新增
     * Author wzn
     * Date 2023/5/9 9:14
     */
    @PostMapping("/addBuildArea")
    @LogInfo(value = LogEnums.LOG_AREA_ADD, model = Constants.MODEL_YYZT)
    public ResultInfo addBuildArea(@RequestBody SysBuildArea sysBuildArea) {
        sysBuildAreaService.addSysBuildArea(sysBuildArea);
        return ResultInfo.success();
    }


    /**
     * 区域修改
     * Author wzn
     * Date 2023/5/9 9:15
     */
    @PostMapping("/updateBuildArea")
    @LogInfo(value = LogEnums.LOG_AREA_UPDATE, model = Constants.MODEL_YYZT)
    public ResultInfo updateBuildFloor(@RequestBody SysBuildArea sysBuildArea) {
        sysBuildAreaService.updateSysBuildArea(sysBuildArea);
        return ResultInfo.success();
    }

    /**
     * 区域删除
     * Author wzn
     * Date 2023/5/9 9:17
     */
    @PostMapping("/delBuildArea")
    @LogInfo(value = LogEnums.LOG_AREA_DELETE, model = Constants.MODEL_YYZT)
    public ResultInfo delBuildArea(Integer id) {
        if(sysBuildAreaService.exist(id+"")){
            throw new CustomException("该区域已被部门使用,禁止删除！") ;
        }
        if(sysBuildAreaService.existDevice(id+"")){
            throw new CustomException("该区域已被采集终端使用,禁止删除！") ;
        }
        if(sysBuildAreaService.existDeviceAccess(id+"")){
            throw new CustomException("该区域已被门禁设备使用,禁止删除！") ;
        }
        if(sysBuildAreaService.existDeviceGateway(id+"")){
            throw new CustomException("该区域已被网关设备使用,禁止删除！") ;
        }

        SysBuildArea sysBuildArea1 = new SysBuildArea();
        sysBuildArea1.setId(id);
        sysBuildArea1.setIsDel(1);
        sysBuildAreaService.updateById(sysBuildArea1);
        return ResultInfo.success();
    }

    /**
     * 分页查询
     * 区域列表
     * Author wzn
     * Date 2023/5/9 9:17
     */
    @PostMapping("/selectBuildAreaList")
    public ResultInfo selectBuildAreaList(@RequestBody SysBuildAreaVo sysBuildArea) {
        Page<SysBuildArea> sysBuildAreaPage = sysBuildAreaService.selectBuildAreaList(sysBuildArea);
        if (!CollectionUtils.isEmpty(sysBuildAreaPage.getRecords())) {
            for (SysBuildArea s : sysBuildAreaPage.getRecords()) {
                SysBuildFloor sysBuildFloor = sysBuildFloorService.getById(s.getFloorId());
                if (null != sysBuildFloor) {
                    s.setFloorName(sysBuildFloor.getFloorName());
                    SysBuild sysBuild = sysBuildService.getById(sysBuildFloor.getDictBuilding()) ;
                    if(null != sysBuild){
                       s.setBuildName(sysBuild.getBuildName());
                    }

                }
                //区域类型转义
                if(StringUtils.isNotEmpty(s.getAreaType())){
                    SysDict sysDict = sysDictService.selectByValueAndType("area_type",s.getAreaType()) ;
                    if(null != sysDict){
                        s.setAreaType(sysDict.getDictLabel());
                    }
                }

            }
        }
        return ResultInfo.success(sysBuildAreaPage);
    }

    /**
     * 区域列表
     * Author wzn
     * Date 2023/5/9 9:17
     */
    @PostMapping("/getList")
    public ResultInfo getList(SysBuildAreaVo vo) {
        return ResultInfo.success(sysBuildAreaService.getList(vo));
    }


    /**
     * 区域列表
     */
    @PostMapping("/getAllList")
    public ResultInfo getAllList(SysBuildAreaVo vo) {
        return ResultInfo.success(sysBuildAreaService.getAllList(vo));
    }


    /**
     * 区域详情
     * Author wzn
     * Date 2023/5/9 9:18
     */
    @PostMapping("/info")
    public ResultInfo info(String id) {
        SysBuildArea sysBuildArea = sysBuildAreaService.info(id);
        if (null != sysBuildArea) {
            SysBuildFloor sysBuildFloor = sysBuildFloorService.getById(sysBuildArea.getFloorId());
            if (null != sysBuildFloor) {
                SysBuild sysBuild = sysBuildService.getById(sysBuildFloor.getDictBuilding());
                if (null != sysBuild) {
                    sysBuildArea.setBuildFloorId(sysBuild.getId());

                    //楼宇下  所有楼层数据
                    QueryWrapper<SysBuildFloor> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("dictBuilding", sysBuild.getId());
                    List<SysBuildFloor> sysBuildFloorList = sysBuildFloorService.list(queryWrapper);
                    sysBuildArea.setSysBuildFloorList(sysBuildFloorList);
                }
            }
        }


        return ResultInfo.success(sysBuildArea);
    }


    /**
     * 所有的楼层
     * Author wzn
     * Date 2023/5/23 14:29
     */
    @PostMapping("/allBuildFloorList")
    public ResultInfo allBuildFloorList(Integer buildFloorId) {
        QueryWrapper<SysBuildFloor> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        queryWrapper.eq("dictBuilding", buildFloorId);
        queryWrapper.orderByAsc("sort");
        List<SysBuildFloor> sysBuildFloorList = sysBuildFloorService.list(queryWrapper);
        return ResultInfo.success(sysBuildFloorList);
    }
    /**
     * 组装菜单树
     */
    @PostMapping(value = "getAreaTypeTree.do")
    @ApiOperation("查询菜单树")
    public ResultInfo getMenuTypeTree(@RequestBody SysBuildArea buildArea){
        return ResultInfo.success(sysBuildAreaService.getAreaTypeTree(buildArea));
    }

}
