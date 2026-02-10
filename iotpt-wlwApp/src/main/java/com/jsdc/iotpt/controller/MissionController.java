package com.jsdc.iotpt.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.mapper.SysUserMapper;
import com.jsdc.iotpt.model.SysFile;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.operate.*;
import com.jsdc.iotpt.service.*;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.AppPushMsgVo;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.UploadParams;
import com.jsdc.iotpt.vo.operate.AssignedCountDTO;
import com.jsdc.iotpt.vo.operate.MissionItemRecordVo;
import com.jsdc.iotpt.vo.operate.MissionVo;
import com.jsdc.iotpt.vo.operate.WorkOrderVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务组功能
 *
 * @author zln
 */
@RestController
@RequestMapping("/app/mission")
@Api(tags = "任务组")
public class MissionController {

    @Autowired
    private MissionService missionService;
    @Autowired
    private MissionAssignService assignService;
    @Autowired
    private SysFileService fileService;
    @Autowired
    private MissionItemRecordService recordService;
    @Autowired
    private SysUserService userService;
    @Autowired
    private TeamGroupUserService groupUserService;
    @Autowired
    private TeamGroupsService teamGroupsService;
    @Autowired
    private MissionAssignUserService assignUserService;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private MissionCareService careService;
    @Autowired
    private AppPushMsgService appPushMsgService;
    @Value("${jsdc.nginxPath}")
    private String nginxPath;

    @PostMapping("pageListMission.do")
    @ApiOperation(value = "分页查询", position = 1)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageIndex", value = "页码", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", dataType = "int")
    })
    public ResultInfo pageListMission(MissionVo bean, @RequestParam(name = "pageNo") Integer pageIndex, @RequestParam(name = "pageSize") Integer pageSize) {
//        if (null != bean.getIsHandle() && (1 == bean.getIsHandle() || 2 == bean.getIsHandle())) {//我的上报和待处理

//        }
        try {
            return ResultInfo.success(missionService.appMissionPageList(bean, pageIndex, pageSize));
        } catch (Exception e) {
            throw new CustomException("获取列表失败");
        }
    }


    @PostMapping("pageListRepair.do")
    @ApiOperation(value = "分页查询", position = 1)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageIndex", value = "页码", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", dataType = "int")
    })
    public ResultInfo pageListRepair(MissionVo bean, @RequestParam(name = "pageNo") Integer pageIndex, @RequestParam(name = "pageSize") Integer pageSize) {
//        if (null != bean.getIsHandle() && (1 == bean.getIsHandle() || 2 == bean.getIsHandle())) {//我的上报和待处理

//        }
        try {
            return ResultInfo.success(missionService.appRepairPageList(bean, pageIndex, pageSize));
        } catch (Exception e) {
            throw new CustomException("获取列表失败");
        }
    }

    @PostMapping("pageList.do")
    @ApiOperation(value = "分页查询", position = 1)
    @ApiImplicitParams({@ApiImplicitParam(name = "pageIndex", value = "页码", dataType = "int"), @ApiImplicitParam(name = "pageSize", value = "每页条数", dataType = "int")})
    public ResultInfo missionPageList(MissionVo bean, @RequestParam(name = "pageNo") Integer pageIndex, @RequestParam(name = "pageSize") Integer pageSize) {
        try {
            return ResultInfo.success(missionService.missionPageList(bean, pageIndex, pageSize));
        } catch (Exception e) {
            throw new CustomException("获取列表失败");
        }
    }

    /**
     * 待处理
     * 已处理
     * 我的关注
     *
     * @param bean
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @PostMapping("pageHandleList.json")
    @ApiOperation(value = "分页查询", position = 1)
    @ApiImplicitParams({@ApiImplicitParam(name = "pageIndex", value = "页码", dataType = "int"), @ApiImplicitParam(name = "pageSize", value = "每页条数", dataType = "int")})
    public ResultInfo pageHandleList(MissionVo bean, @RequestParam(name = "page") Integer pageIndex, @RequestParam(name = "limit") Integer pageSize) {
        try {
            SysUser sysUser = userService.getById(bean.getUserId());
            Integer isAdmin = 0;
            if (sysUser.getLoginName().equals("admin")) {
                isAdmin = 1;
            }
            bean.setIsAdmin(isAdmin);
            return ResultInfo.success(missionService.pageListMissionApp(bean, pageIndex, pageSize));
        } catch (Exception e) {
            throw new CustomException("获取列表失败");
        }
    }


    @PostMapping("pageCareList.json")
    @ApiOperation(value = "我的关注分页查询", position = 1)
    @ApiImplicitParams({@ApiImplicitParam(name = "pageIndex", value = "页码", dataType = "int"), @ApiImplicitParam(name = "pageSize", value = "每页条数", dataType = "int")})
    public ResultInfo pageCareList(MissionVo bean, @RequestParam(name = "page") Integer pageIndex, @RequestParam(name = "limit") Integer pageSize) {
        try {
            SysUser sysUser = userService.getById(bean.getUserId());
            Integer isAdmin = 0;
            if (sysUser.getLoginName().equals("admin")) {
                isAdmin = 1;
            }
            bean.setIsAdmin(isAdmin);
            return ResultInfo.success(missionService.pageCareMissionApp(bean, pageIndex, pageSize));
        } catch (Exception e) {
            throw new CustomException("获取我的关注列表失败");
        }
    }


    @PostMapping("statusCount.do")
    @ApiOperation("根据不同状态进行统计数据")
    public ResultInfo statusCount(Integer userId, Integer publicity, Integer home_tab_type) {
        try {
            SysUser sysUser = userService.getById(userId);
            Integer isAdmin = 0;
            if (sysUser.getLoginName().equals("admin")) {
                isAdmin = 1;
            }
            return ResultInfo.success(missionService.statusCount(userId, isAdmin, publicity, home_tab_type));
        } catch (Exception e) {
            throw new CustomException("获取统计数据失败");
        }
    }

    @PostMapping("assignedCount.do")
    @ApiOperation("待指派根据不同状态统计")
    public ResultInfo assignedCount(AssignedCountDTO dto) {
        try {
            SysUser sysUser = userService.getById(dto.getUserId());
            Integer isAdmin = 0;
            if (sysUser.getLoginName().equals("admin")) {
                isAdmin = 1;
            }
            return ResultInfo.success(missionService.assignedCount(dto));
        } catch (Exception e) {
            throw new CustomException("获取统计数据失败");
        }
    }


    @PostMapping("inspectionData.do")
    @ApiOperation(value = "处理详情内容展示", notes = "前端传递missionId、teamGroupsId参数获取数据")
    public ResultInfo selectByMissionId(MissionItemRecordVo bean) {
        try {
            return ResultInfo.success(missionService.inspectionData(bean));
        } catch (Exception e) {
            throw new CustomException("获取处理详情失败");
        }
    }


    @PostMapping("waitHandle.do")
    @ApiOperation("处理功能")
    public ResultInfo waitHandle(@RequestBody MissionVo mission) {
        try {
            if (StrUtil.isNotEmpty(mission.getStartTime())) {
                try {
                    mission.setHandleDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(mission.getStartTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return ResultInfo.success(missionService.waitHandle(mission));
        } catch (Exception e) {
            throw new CustomException("处理失败");
        }
    }


    @PostMapping("storeHandle.do")
    @ApiOperation("暂存处理功能")
    public ResultInfo storeHandle(@RequestBody MissionVo mission) {

        try {
            return missionService.storeHandle(mission);
        } catch (Exception e) {
            throw new CustomException("暂存处理失败");
        }
    }

    @PostMapping("uploadFile.do")
    @ApiOperation("上传图片")
    public ResultInfo uploadFile(@RequestParam("file") MultipartFile files) {
        try {
            return ResultInfo.success(fileService.minioReturnFile(new UploadParams("/missionItemRecord", files)));
        } catch (Exception e) {
            throw new CustomException("上传失败");
        }
    }

    @PostMapping("isPending.do")
    @ApiOperation("挂起功能")
    public ResultInfo isPending(@RequestBody MissionVo mission) {
        try {
            mission.setPendingTime(new Date());
            return missionService.isPending(mission);
        } catch (Exception e) {
            throw new CustomException("挂起功能失败");
        }
    }

    @PostMapping("pendingList.do")
    @ApiOperation("挂起列表")
    public ResultInfo pendingList(@RequestBody MissionVo mission) {

        try {
            return ResultInfo.success(missionService.pendingList(mission));
        } catch (Exception e) {
            throw new CustomException("获取列表失败");
        }
    }

    @PostMapping("myPendingList.do")
    @ApiOperation("我的挂起列表")
    public ResultInfo myPendingList(@RequestBody MissionVo mission) {

        try {
            return ResultInfo.success(missionService.myPendingList(mission));
        } catch (Exception e) {
            throw new CustomException("获取列表失败");
        }
    }


    @PostMapping("editPending.do")
    @ApiOperation("修改挂起到正常状态")
    public ResultInfo editPending(@RequestBody MissionVo mission) {

        try {
            return missionService.isPending(mission);
        } catch (Exception e) {
            throw new CustomException("操作失败");
        }
    }


    @PostMapping("saveAppMission.do")
    @ApiOperation("新增编辑APP")
    public ResultInfo saveAppMission(@RequestBody Mission bean) {
        try {
            return ResultInfo.success(missionService.saveAppMission(bean));
        } catch (Exception e) {
            throw new CustomException("操作失败");
        }
    }


    @PostMapping("saveMission.do")
    @ApiOperation("新增编辑")
    public ResultInfo saveMission(@RequestBody Mission bean) {
        try {
            return ResultInfo.success(missionService.saveMission(bean));
        } catch (Exception e) {
            throw new CustomException("操作失败");
        }
    }


    @PostMapping("updPublicity.do")
    @ApiOperation(value = "关注功能", notes = "关注类型 1：我的任务关注  2：待指派关注")
    public ResultInfo updPublicity(Integer id, Integer publicity, Integer userId, Integer careType) {
        try {
            Mission mission = missionService.getById(id);
            if (0 == publicity) {//取消关注
                mission.setPublicity(0);
                List<MissionCare> list = careService.list(new LambdaQueryWrapper<MissionCare>().eq(MissionCare::getMissionId, id).eq(MissionCare::getCreateUser, userId));
                list.forEach(a -> {
                    careService.removeById(a.getId());
                });
            } else {//关注
                mission.setPublicity(1);
                MissionCare care = new MissionCare();
                care.setMissionId(id);
                care.setCreateUser(userId);
                care.setCreateTime(new Date());
                care.setCareType(careType);
                careService.save(care);
            }
            missionService.updateById(mission);
            return ResultInfo.success("操作成功");
        } catch (Exception e) {
            throw new CustomException("关注失败");
        }
    }

    @PostMapping("detailsMission.do")
    @ApiOperation("详情")
    public ResultInfo detailsMission(Mission bean) {
        try {
            return ResultInfo.success(missionService.detailsMission(bean));
        } catch (Exception e) {
            throw new CustomException("获取详情失败");
        }
    }


    @PostMapping("openStatus.do")
    @ApiOperation("开启处理")
    public ResultInfo openStatus(Integer id) {
        try {
            Mission mission = missionService.getById(id);
            mission.setStates(2);//待处理
            return ResultInfo.success(missionService.updateById(mission));
        } catch (Exception e) {
            throw new CustomException("开启处理失败");
        }
    }

    /**
     * app 指派
     * 4
     *
     * @param bean
     * @param deadlineTime
     * @return
     */
    @PostMapping("tobeAppAssigned.do")
    @ApiOperation("待指派")
    public ResultInfo tobeAppAssigned(MissionAssign bean, String deadlineTime) {
        try {
            bean.setDeadline(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(deadlineTime));
            return ResultInfo.success(assignService.tobeAppAssigned(bean));
        } catch (ParseException e) {
            e.printStackTrace();
            throw new CustomException("待指派失败");
        }
    }

    @ApiOperation(value = "工单驳回", notes = "id:工单Id,reason:原因)")
    @RequestMapping("/missionRejection")
    public ResultInfo missionRejection(@RequestBody Mission bean) {
        try {
            missionService.missionRejection(bean);
            return ResultInfo.success("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("工单驳回失败");
        }
    }

    @PostMapping("handleMission.do")
    @ApiOperation(value = "处理工单（同意或拒绝）耗材消费", notes = "id:工单Id,handleStatus:1：同意 2：拒绝)")
    public ResultInfo handleMission(@RequestBody MissionVo bean) {
        try {
            return missionService.handleMission(bean);
        } catch (Exception e) {
            throw new CustomException("处理工单失败");
        }
    }

    @PostMapping("addConsumables.do")
    @ApiOperation(value = "添加耗材信息", notes = "id 工单id consumables：耗材信息集合")
    public ResultInfo addConsumables(@RequestBody MissionVo bean) {
        try {
            return missionService.addConsumables(bean);
        } catch (Exception e) {
            throw new CustomException("添加耗材信息失败");
        }
    }

    @GetMapping("consumableTemporary.do")
    @ApiOperation(value = "耗材暂存信息", notes = "id 工单id")
    public ResultInfo consumableTemporary(Integer id) {
        try {
            return missionService.consumableTemporary(id);
        } catch (Exception e) {
            throw new CustomException("获取耗材暂存信息失败");
        }
    }

    @PostMapping("tobeAssigned.do")
    @ApiOperation("待指派")
    public ResultInfo tobeAssigned(MissionAssign bean, String deadlineTime) {
        try {
            bean.setDeadline(new SimpleDateFormat("yyyy-MM-dd").parse(deadlineTime));
            return ResultInfo.success(assignService.tobeAssigned(bean));
        } catch (ParseException e) {
            e.printStackTrace();
            throw new CustomException("待指派失败");
        }
    }


    @PostMapping("selectRegionByDevice.do")
    @ApiOperation(value = "根据所属区域获取所有设备", notes = "根据areaId获取设备信息")
    public ResultInfo selectRegionByDevice(Integer areaId, String name) {
        try {
            return ResultInfo.success(recordService.selectRegionByDevice(areaId, name));
        } catch (Exception e) {
            throw new CustomException("获取所有设备失败");
        }
    }


    @PostMapping("selectAreaList.do")
    @ApiOperation(value = "查询所有区域")
    public ResultInfo selectAreaList(String name) {
        try {
            return ResultInfo.success(recordService.selectAreaList(name));
        } catch (Exception e) {
            throw new CustomException("查询所有区域失败");
        }
    }


    @PostMapping("teamGroupsList.json")
    @ApiOperation("根据班组id获取班组人员")
    public ResultInfo teamGroupsList(Integer groupId) {
        try {
            List<TeamGroupUser> list = groupUserService.list(new LambdaQueryWrapper<TeamGroupUser>()
                    .eq(TeamGroupUser::getIsDel, 0).eq(TeamGroupUser::getGroupId, groupId));
            list.forEach(a -> {
                SysUser users = userService.getById(a.getUserId());
                a.setRealName(users.getRealName());
            });
            return ResultInfo.success(list);
        } catch (Exception e) {
            throw new CustomException("获取班组人员失败");
        }
    }
    @PostMapping("teamGroupsList1.json")
    @ApiOperation("获取所有班组人员")
    public ResultInfo teamGroupsList1() {
        try {
            List<TeamGroupUser> list = groupUserService.list(new LambdaQueryWrapper<TeamGroupUser>()
                    .eq(TeamGroupUser::getIsDel, 0));
            if (CollUtil.isNotEmpty(list)) {
                list.forEach(a -> {
                    SysUser user = userService.getById(a.getUserId());
                    TeamGroups teamGroups = teamGroupsService.getById(a.getGroupId());
                    a.setTeamGroupName(teamGroups.getName());
                    if (ObjUtil.isNotEmpty(user)) {
                        a.setRealName(user.getRealName());
                    }
                });
            }
            return ResultInfo.success(list.stream().collect(Collectors.groupingBy(TeamGroupUser::getTeamGroupName)));
        } catch (Exception e) {
            throw new CustomException("获取列表失败");
        }
    }


    @PostMapping("saveAssignUser.json")
    @ApiOperation("保存指派人员")
    public ResultInfo saveAssignUser(@RequestBody MissionAssignUser bean) {
        try {
            bean.getUsers().forEach(a -> {
                MissionAssignUser user = new MissionAssignUser();
                user.setMissionId(bean.getMissionId());
                user.setUserId(a);
                user.setGroupId(bean.getGroupId());
                assignUserService.save(user);
            });
            AppPushMsgVo msgVo = new AppPushMsgVo();
            msgVo.setAppConfigCode("SYSTEM_REPAIRS_NOTICE");
            msgVo.setUserIdList(bean.getUsers().stream().map(Integer::parseInt).collect(Collectors.toList()));
            appPushMsgService.pushMsg(msgVo);

            Mission mission = missionService.getById(bean.getMissionId());
            mission.setStates(2);//待处理
            mission.setOpenTime(new Date());
            return ResultInfo.success(missionService.updateById(mission));
        } catch (Exception e) {
            throw new CustomException("保存指派人员失败");
        }
    }


    @PostMapping("waitHandleApp.do")
    @ApiOperation("单个处理")
    public ResultInfo waitHandleApp(MissionItemRecordVo bean) {
        try {
            if (StringUtils.isNotEmpty(bean.getStartTime())) {
                try {
                    bean.setHandleDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(bean.getStartTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return ResultInfo.success(missionService.waitHandleApp(bean));
        } catch (Exception e) {
            throw new CustomException("处理失败");
        }
    }


    @PostMapping("missionItemRecordApp.do")
    @ApiOperation("处理详情")
    public ResultInfo missionItemRecordApp(Integer id) {
        try {
            return ResultInfo.success(recordService.missionItemRecordApp(id));
        } catch (Exception e) {
            throw new CustomException("获取处理详情失败");
        }
    }


    @PostMapping("backOut.do")
    @ApiOperation("撤销")
    public ResultInfo backOut(Integer id, String notes) {
        try {
            Mission mission = missionService.getById(id);
            mission.setPublicity(1);
            mission.setNotes(notes);
            mission.setStates(5);
            return ResultInfo.success(missionService.updateById(mission));
        } catch (Exception e) {
            throw new CustomException("撤销失败");
        }
    }


    @PostMapping("pageReportList.json")
    @ApiOperation(value = "上报分页查询", notes = "传递参数：page：页码(必填)，limit:每页条数(必填),userId：上报人id,title:任务标题,handleDay：处理时间 ")
    @ApiImplicitParams({@ApiImplicitParam(name = "pageIndex", value = "页码", dataType = "int"), @ApiImplicitParam(name = "pageSize", value = "每页条数", dataType = "int")})
    public Page<MissionVo> pageReportList(MissionVo bean, @RequestParam(name = "page") Integer pageIndex, @RequestParam(name = "limit") Integer pageSize) {
        try {
            return missionService.pageReportList(bean, pageIndex, pageSize);
        } catch (Exception e) {
            throw new CustomException("获取列表失败");
        }
    }

    @PostMapping("handleDetailsApp.do")
    @ApiOperation(value = "处理页面详情数据", notes = "传递参数：missionid：任务id(必填)，region:所属区域ID(必填),detailsType：详情类型 ")
    public ResultInfo handleDetailsApp(Integer missionid, Integer region, Integer detailsType) {
        try {
            return ResultInfo.success(recordService.handleDetailsApp(missionid, region, detailsType));
        } catch (Exception e) {
            throw new CustomException("获取数据失败");
        }
    }

    @PostMapping("inspectionAppData.do")
    @ApiOperation(value = "处理详情内容展示", notes = "前端传递missionId")
    public ResultInfo inspectionAppData(MissionItemRecordVo bean) {
        try {
            return ResultInfo.success(missionService.inspectionAppData(bean));
        } catch (Exception e) {
            throw new CustomException("获取数据失败");
        }
    }

    @PostMapping("edit.do")
    @ApiOperation(value = "编辑", notes = "Mission对象")
    public ResultInfo edit(Mission mission) {
        try {
            missionService.updateById(mission);
            return ResultInfo.success();
        } catch (Exception e) {
            throw new CustomException("编辑失败");
        }
    }

    @PostMapping("getHistoryMission.do")
    @ApiOperation(value = "根据任务ID查询相关设备的历史任务", notes = "传递参数：deviceId：设备id,deviceType:设备类型,id：任务id")
    public ResultInfo getHistoryMission(Mission bean) {
        try {
            List<Mission> list = missionService.getHistoryMission(bean);
            for (Mission temp : list) {
                List<SysFile> files = fileService.list(new LambdaQueryWrapper<SysFile>().eq(SysFile::getBizId, temp.getId() + "").eq(SysFile::getBizType, "4"));
                if (CollectionUtils.isNotEmpty(files)) {
                    for (int i = 0; i < files.size(); i++) {
                        files.get(i).setFileUrl(nginxPath + files.get(i).getFileUrl());
                    }
                }
                temp.setFileList(files);
                if (null != temp.getHandleId()) {
                    SysUser sysUser = sysUserMapper.selectById(temp.getHandleId());
                    if (null != sysUser) {
                        temp.setHandleName(sysUser.getRealName());
                    }
                }
            }
            return ResultInfo.success(list);
        } catch (Exception e) {
            throw new CustomException("查询相关设备的历史任务失败");
        }
    }

    @PostMapping("waitHandleAppWlpt.do")
    @ApiOperation("处理提交方法")
    public ResultInfo waitHandleMission(@RequestBody MissionVo bean) {
        try {
            Mission ss = missionService.getById(bean.getId());
            if (ss.getStates() == 3) {
                throw new CustomException("此单据已被处理");
            } else {
                return ResultInfo.success(missionService.waitHandleApp(bean));
            }
        } catch (Exception e) {
            throw new CustomException("操作失败");
        }
    }

    @PostMapping("inspectionAppDataWlpt.do")
    @ApiOperation(value = "处理详情内容展示", notes = "前端传递missionId")
    public ResultInfo inspectionAppDataWlpt(MissionItemRecordVo bean) {
        try {
            return ResultInfo.success(missionService.inspectionAppData1(bean));
        } catch (Exception e) {
            throw new CustomException("获取处理详情失败");
        }
    }


    @PostMapping("pageWorkOrderList")
    @ApiOperation(value = "分页查询")
    public ResultInfo pageWorkOrderList(WorkOrderVo vo) {
        try {
            return ResultInfo.success(missionService.pageWorkOrderList(vo));
        } catch (Exception e) {
            throw new CustomException("获取列表失败");
        }
    }


    @PostMapping("workSourceCount")
    @ApiOperation(value = "待办状态统计数量", notes = "传递参数：small:小类id")
    public ResultInfo workSourceCount(Integer large, Integer small) {
        try {
            return ResultInfo.success(missionService.workSourceCount(large, small));
        } catch (Exception e) {
            throw new CustomException("获取待办状态统计数量失败");
        }
    }

    @PostMapping("wordOrderDetails")
    @ApiOperation(value = "待办详情", notes = "传递参数：id")
    public ResultInfo wordOrderDetails(Integer id) {
        try {
            return ResultInfo.success(missionService.wordOrderDetails(id));
        } catch (Exception e) {
            throw new CustomException("获取待办详情失败");
        }
    }

}
