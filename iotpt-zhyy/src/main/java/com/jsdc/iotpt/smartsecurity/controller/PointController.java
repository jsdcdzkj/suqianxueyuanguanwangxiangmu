package com.jsdc.iotpt.smartsecurity.controller;

import com.jsdc.iotpt.common.Constants;
import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.mapper.SysBuildAreaMapper;
import com.jsdc.iotpt.mapper.SysBuildFloorMapper;
import com.jsdc.iotpt.mapper.SysBuildMapper;
import com.jsdc.iotpt.model.*;
import com.jsdc.iotpt.model.sys.SysBuild;
import com.jsdc.iotpt.model.sys.SysBuildArea;
import com.jsdc.iotpt.model.sys.SysBuildFloor;
import com.jsdc.iotpt.service.*;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.DeviceCollectVo;
import com.jsdc.iotpt.vo.PointVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


@RestController
@RequestMapping("/point")
@Api(tags = "点位配置")
public class PointController {

    @Autowired
    private PointService pointService;
    @Autowired
    private SysOrgManageService sysOrgManageService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysBuildAreaMapper sysBuildAreaMapper;
    @Autowired
    private SysBuildFloorMapper sysBuildFloorMapper;
    @Autowired
    private SysBuildMapper sysBuildMapper;

    @RequestMapping("getList")
    @ApiOperation(value = "点位列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "点位名称", dataType = "String", required = true),
            @ApiImplicitParam(name = "logicalBuildId", value = "所属楼宇", dataType = "Integer", required = true),
            @ApiImplicitParam(name = "logicalFloorId", value = "所属楼层", dataType = "Integer", required = true),
            @ApiImplicitParam(name = "logicalAreaId", value = "所属区域", dataType = "Integer", required = true),
            @ApiImplicitParam(name = "pageIndex", value = "当前页数", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "显示条数", dataType = "Integer")
    })
    public ResultInfo getList(Integer pageIndex, Integer pageSize, String name, Integer logicalBuildId, Integer logicalFloorId, Integer logicalAreaId) {
        return ResultInfo.success(pointService.getList(pageIndex, pageSize, name, logicalBuildId, logicalFloorId, logicalAreaId));
    }

    @PostMapping("/areaTreeList")
    @ApiOperation(value = "区域三级树形图")
    public ResultInfo areaTreeList() {
        return ResultInfo.success(sysOrgManageService.areaTreeList());
    }

    @RequestMapping("getById")
    @ApiOperation(value = "根据id获取点位")
    @ApiImplicitParam(name = "id", value = "点位主键", dataType = "Integer")
    public ResultInfo getById(Integer id) {
        return ResultInfo.success(pointService.getById(id));
    }

    @RequestMapping("add")
    @ApiOperation(value = "新增点位")
    @LogInfo(value = LogEnums.LOG_SYSTEM_POINT_ADD,model = Constants.MODEL_YYZT)
    public ResultInfo add(@RequestBody PointVo pointVo) {
        validatePointVo(pointVo, true);  // 设置参数为true，因为是新增操作
        return savePoint(pointVo, true); // 设置参数为true，因为是新增操作
    }

    @RequestMapping("edit")
    @ApiOperation(value = "编辑点位")
    @LogInfo(value = LogEnums.LOG_SYSTEM_POINT_EDIT,model = Constants.MODEL_YYZT)
    public ResultInfo edit(@RequestBody PointVo pointVo) {
        validatePointVo(pointVo, false); // 设置参数为false，因为是编辑操作
        return savePoint(pointVo, false); // 设置参数为false，因为是编辑操作
    }

    @RequestMapping("del")
    @ApiOperation(value = "删除点位")
    @ApiImplicitParam(name = "id", value = "点位主键", dataType = "Integer")
    @LogInfo(value = LogEnums.LOG_SYSTEM_POINT_DEL,model = Constants.MODEL_YYZT)
    public ResultInfo del(Integer id) {
        if (null == id) {
            return ResultInfo.error("点位ID不能为空");
        }
        Point point = pointService.getById(id);
        if (point == null) {
            return ResultInfo.error("点位ID不存在");
        }
        point.setIsDel(1);
        return ResultInfo.success(pointService.updateById(point));
    }

    private void validatePointVo(PointVo pointVo, boolean isNew) {
        if (StringUtils.isEmpty(pointVo.getName())) {
            throw new CustomException("点位名称不能为空！");
        }
        if (!isNew && null == pointVo.getId()) {
            throw new CustomException("点位ID不能为空！");
        }
    }

    private ResultInfo savePoint(PointVo pointVo, boolean isNew) {
        SysUser sysUser = sysUserService.getUser();
        Point point = new Point();
        BeanUtils.copyProperties(pointVo, point);


        //用区域查楼层 物理位置
        if (StringUtils.isNotNull(pointVo.getLogicalAreaId())) {
            SysBuildArea sysBuildArea = sysBuildAreaMapper.selectById(pointVo.getLogicalAreaId());
            if (StringUtils.isNotNull(sysBuildArea)) {
                //查楼宇
                SysBuildFloor sysBuildFloor = sysBuildFloorMapper.selectById(sysBuildArea.getFloorId());
                if (StringUtils.isNotNull(sysBuildFloor)) {
                    SysBuild sysBuild = sysBuildMapper.selectById(sysBuildFloor.getDictBuilding());
                    point.setAreaNames(sysBuild.getBuildName() + " / " + sysBuildFloor.getFloorName() + " / " + sysBuildArea.getAreaName());
                }
            }
        }

        //用区域查楼层 逻辑位置
        if (StringUtils.isNotNull(pointVo.getLogicalAreaId()) && pointVo.getLogicalAreaId() != 0) {
            SysBuildArea sysBuildArea2 = sysBuildAreaMapper.selectById(pointVo.getLogicalAreaId());
            if (StringUtils.isNotNull(sysBuildArea2)) {
                //查楼层
                SysBuildFloor sysBuildFloor = sysBuildFloorMapper.selectById(sysBuildArea2.getFloorId());
                if (StringUtils.isNotNull(sysBuildFloor)) {
                    //查楼宇
                    SysBuild sysBuild = sysBuildMapper.selectById(sysBuildFloor.getDictBuilding());
                    point.setLogicalAreaNames(sysBuild.getBuildName() + " / " + sysBuildFloor.getFloorName() + " / " + sysBuildArea2.getAreaName());
                }
            }
        } else if (StringUtils.isNotNull(pointVo.getLogicalFloorId()) && pointVo.getLogicalFloorId() != 0) {
            //查楼层
            SysBuildFloor sysBuildFloor = sysBuildFloorMapper.selectById(pointVo.getLogicalFloorId());
            if (StringUtils.isNotNull(sysBuildFloor)) {
                SysBuild sysBuild = sysBuildMapper.selectById(sysBuildFloor.getDictBuilding());
                point.setLogicalAreaNames(sysBuild.getBuildName() + " / " + sysBuildFloor.getFloorName());
            }
        } else if (StringUtils.isNotNull(pointVo.getLogicalBuildId()) && pointVo.getLogicalBuildId() != 0) {
            //查楼宇
            SysBuild sysBuild = sysBuildMapper.selectById(pointVo.getLogicalBuildId());
            point.setLogicalAreaNames(sysBuild.getBuildName());
        }


        if (isNew) {
            point.setCreateUser(sysUser.getId());
            point.setCreateTime(new Date());
            point.setIsDel(0);
            return ResultInfo.success(pointService.save(point));
        } else {
            point.setUpdateUser(sysUser.getId());
            point.setUpdateTime(new Date());
            return ResultInfo.success(pointService.updateById(point));
        }
    }

}
