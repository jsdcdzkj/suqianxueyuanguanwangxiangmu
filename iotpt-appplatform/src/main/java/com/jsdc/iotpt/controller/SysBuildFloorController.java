package com.jsdc.iotpt.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.common.Constants;
import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.model.sys.SysBuild;
import com.jsdc.iotpt.model.sys.SysBuildFloor;
import com.jsdc.iotpt.service.SysBuildFloorService;
import com.jsdc.iotpt.service.SysBuildService;
import com.jsdc.iotpt.service.SysLogService;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/sys_build_floor")
public class SysBuildFloorController {
    @Autowired
    private SysBuildFloorService sysBuildFloorService;

    @Autowired
    private SysBuildService sysBuildService;

    /**
     * 楼层新增
     * Author wzn
     * Date 2023/5/9 9:14
     */
    @PostMapping("/addBuildFloor")
    @LogInfo(value = LogEnums.LOG_FLOOR_ADD, model = Constants.MODEL_YYZT)
    public ResultInfo addBuildFloor(SysBuildFloor sysBuildFloor) {
        sysBuildFloorService.addSysBuildFloor(sysBuildFloor);
        return ResultInfo.success();
    }


    /**
     * 楼层修改
     * Author wzn
     * Date 2023/5/9 9:15
     */
    @PostMapping("/updateBuildFloor")
    @LogInfo(value = LogEnums.LOG_FLOOR_UPDATE, model = Constants.MODEL_YYZT)
    public ResultInfo updateBuildFloor(SysBuildFloor sysBuildFloor) {
        sysBuildFloorService.updateSysBuildFloor(sysBuildFloor);
        return ResultInfo.success();
    }

    /**
     * 楼层删除
     * Author wzn
     * Date 2023/5/9 9:17
     */
    @PostMapping("/delBuildFloor")
    @LogInfo(value = LogEnums.LOG_FLOOR_DELETE, model = Constants.MODEL_YYZT)
    public ResultInfo delBuildFloor(Integer id) {

        if (sysBuildFloorService.exist(id + "")) {
            throw new CustomException("请先删除区域！");
        }
        SysBuildFloor sysBuildFloor1 = new SysBuildFloor();
        sysBuildFloor1.setId(id);
        sysBuildFloor1.setIsDel(1);
        sysBuildFloorService.updateById(sysBuildFloor1);
        return ResultInfo.success();
    }

    /**
     * 楼层列表
     * Author wzn
     * Date 2023/5/9 9:17
     */
    @PostMapping("/selectBuildFloorList")
    public ResultInfo selectBuildFloorList(@RequestBody SysBuildFloor sysBuildFloor) {
        Page<SysBuildFloor> sysBuildFloorPage = sysBuildFloorService.selectBuildFloorList(sysBuildFloor);
        if (!CollectionUtils.isEmpty(sysBuildFloorPage.getRecords())) {
            for (SysBuildFloor s : sysBuildFloorPage.getRecords()) {
                SysBuild sysBuild1 = sysBuildService.getById(s.getDictBuilding());
                if (null != sysBuild1) {
                    s.setParentName(sysBuild1.getBuildName());
                }
            }
        }
        return ResultInfo.success(sysBuildFloorPage);
    }


    /**
     * 楼层详情
     * Author wzn
     * Date 2023/5/9 9:18
     */
    @PostMapping("/info")
    public ResultInfo info(String id) {
        return ResultInfo.success(sysBuildFloorService.info(id));
    }


    @PostMapping("/allBuildList")
    public ResultInfo allBuildList() {
        QueryWrapper<SysBuild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        queryWrapper.orderByDesc("createTime");
        List<SysBuild> sysBuildList = sysBuildService.list(queryWrapper);
        return ResultInfo.success(sysBuildList);
    }

    @PostMapping("/getList")
    public ResultInfo getList(SysBuildFloor bean) {
        QueryWrapper<SysBuildFloor> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        queryWrapper.orderByAsc("sort");
        if (StringUtils.isNotNull(bean)) {
            if (StringUtils.isNotNull(bean.getDictBuilding())) {
                queryWrapper.eq("dictBuilding", bean.getDictBuilding());
            }
        }
        List<SysBuildFloor> sysBuildList = sysBuildFloorService.list(queryWrapper);
        return ResultInfo.success(sysBuildList);
    }

    /**
     * 查询指定楼宇(或所有楼宇)所有楼层
     *
     * @param bean
     * @return
     */
    @PostMapping("/getAllList")
    public ResultInfo getAllList(@RequestBody SysBuildFloor bean) {
        return getList(bean);
    }


}
