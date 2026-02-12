package com.jsdc.iotpt.operationmgmt.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.operate.Mission;
import com.jsdc.iotpt.model.operate.TeamGroupUser;
import com.jsdc.iotpt.model.operate.TeamGroups;
import com.jsdc.iotpt.service.*;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.UploadParams;
import com.jsdc.iotpt.vo.operate.MissionItemRecordVo;
import com.jsdc.iotpt.vo.operate.MissionVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务组功能
 *
 * @author zln
 */
@RestController
@RequestMapping("/mission")
@Api(tags = "任务组")
public class MissionController {
    @Autowired
    private TeamGroupUserService groupUserService;
    @Autowired
    private TeamGroupsService teamGroupsService;
    @Autowired
    private MissionService missionService;
    @Autowired
    private SysUserService userService;
    @Autowired
    private SysFileService fileService;


    @PostMapping("pageListMission.do")
    @ApiOperation(value = "分页查询", position = 1)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageIndex", value = "页码", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", dataType = "int")
    })
    public ResultInfo pageListMission(MissionVo bean) {
        try {
            if (null != bean.getStates() && null != bean.getIsHandle() && (0 == bean.getStates() || 3 == bean.getStates() || 2 == bean.getIsHandle())) {//我的上报和待处理
                bean.setSelectSource(1);
            }
            return ResultInfo.success(missionService.pageListMission(bean, bean.getPageIndex(), bean.getPageSize()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("获取列表失败");
        }
    }

    //PC端任务库单独使用接口
    @PostMapping("pageListMission1.do")
    @ApiOperation(value = "分页查询", position = 1)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageIndex", value = "页码", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", dataType = "int")
    })
    public ResultInfo pageListMission1(MissionVo bean) {
        try {
            if (null != bean.getStates() && null != bean.getIsHandle() && (0 == bean.getStates() || 3 == bean.getStates() || 2 == bean.getIsHandle())) {//我的上报和待处理
                bean.setSelectSource(1);
            }
            return ResultInfo.success(missionService.pageListMission1(bean, bean.getPageIndex(),bean.getPageSize()));
        } catch (Exception e) {
            throw new CustomException("获取列表失败");
        }
    }

    @PostMapping("missionExcel")
    @ApiOperation("已完成工单导出")

    public void missionExcel(HttpServletResponse response, @RequestBody MissionVo bean) {
        List<MissionVo> records = missionService.missionExcel(bean, bean.getPageIndex(), bean.getPageSize());
        if (CollUtil.isNotEmpty(records)) {
            ExcelWriter writer = ExcelUtil.getWriter();
            writer.addHeaderAlias("urgencyName", "紧急程度");
            writer.addHeaderAlias("sourceName", "来源");
            writer.addHeaderAlias("taskTypeName", "任务类型");
            writer.addHeaderAlias("title", "标题");
            writer.addHeaderAlias("notes", "内容");
            writer.addHeaderAlias("userName", "上报人");
            writer.addHeaderAlias("reportingTime", "上报时间");
            writer.addHeaderAlias("handleDate", "处理时间");
            writer.addHeaderAlias("teamGroupsName", "处理班组");
            writer.addHeaderAlias("statesName", "状态");
            writer.setOnlyAlias(true);
            writer.setColumnWidth(4, 30);
            writer.setColumnWidth(3, 30);
            writer.setColumnWidth(6, 20);
            writer.setColumnWidth(7, 20);
            writer.setColumnWidth(8, 20);
            writer.setColumnWidth(9, 20);
            writer.write(records, true);
            OutputStream outputStream = null;
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/vnd.ms-excel");
            try {
                response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("已完成工单.xls", "UTF-8"));
                outputStream = response.getOutputStream();
                writer.flush(outputStream, true);
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new CustomException("导出失败");
            }
        }
    }

    @PostMapping("saveMission.do")
    @ApiOperation("新增编辑")
    public ResultInfo saveMission(@RequestBody Mission bean) {
        try {
            return ResultInfo.success(missionService.saveMission(bean));
        } catch (Exception e) {
            throw new CustomException("新增失败");
        }
    }

    @PostMapping("detailsMission.do")
    @ApiOperation("详情")
    public ResultInfo detailsMission(Mission bean) {
        try {
            return ResultInfo.success(missionService.detailsMission(bean));
        } catch (Exception e) {
            throw new CustomException("详情获取失败");
        }
    }


    @PostMapping("deleteMission.do")
    @ApiOperation("删除")
    public ResultInfo deleteMission(Integer id) {
        try {
            missionService.deleteMission(id);
            return ResultInfo.success();
        } catch (Exception e) {
            throw new CustomException("删除失败");
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
            throw new CustomException("驳回失败");
        }
    }

    @PostMapping("handleMission.do")
    @ApiOperation(value = "处理工单（同意或拒绝）耗材消费", notes = "id:工单Id,handleStatus:1：同意 2：拒绝)")
    public ResultInfo handleMission(@RequestBody MissionVo bean) {
        try {
            return missionService.handleMission(bean);
        } catch (Exception e) {
            throw new CustomException("处理失败");
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
            throw new CustomException("获取班组人员信息失败");
        }
    }

    @PostMapping("waitHandle.do")
    @ApiOperation("处理功能")
    public ResultInfo waitHandle(@RequestBody MissionVo mission) {
        try {
            Mission ss = missionService.getById(mission.getId());
            if (ss.getStates() == 3) {
                throw new CustomException("此单据已被处理");
            } else {
                if (StringUtils.isNotEmpty(mission.getStartTime())) {
                    mission.setHandleDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(mission.getStartTime()));
                }
                return ResultInfo.success(missionService.waitHandle(mission));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("处理失败");
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


    @PostMapping("uploadAssignFile.do")
    @ApiOperation("上传文件")
    public ResultInfo uploadAssignFile(@RequestParam("file") MultipartFile files, @RequestParam("missionId") String missionId, @RequestParam("bizType") String bizType) {
        try {
            return missionService.uploadAssignFile(files, missionId, bizType);
        } catch (Exception e) {
            throw new CustomException("上传失败");
        }
    }

    @PostMapping("inspectionData.do")
    @ApiOperation(value = "处理详情内容展示", notes = "前端传递missionId、teamGroupsId参数获取数据")
    public ResultInfo selectByMissionId(MissionItemRecordVo bean) {
        try {
            return ResultInfo.success(missionService.inspectionData(bean));
        } catch (Exception e) {
            throw new CustomException("详情展示失败");
        }
    }


    @PostMapping("statusCount.do")
    @ApiOperation("根据状态统计总数量")
    public ResultInfo statusCount(Integer home_tab_type) {
        try {
            return ResultInfo.success(missionService.statusCount(null, null, null, home_tab_type));
        } catch (Exception e) {
            throw new CustomException("统计失败");
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
            throw new CustomException("开启失败");
        }
    }

    @PostMapping("getTaskAuthority.do")
    @ApiOperation("查询我的任务权限")
    public ResultInfo getTaskAuthority() {
        try {
            Integer id = userService.getUser().getId();
            List<String> authority = userService.selectAppSysMenuList(id, G.APP_WISDOM);
            return ResultInfo.success(authority);
        } catch (Exception e) {
            throw new CustomException("权限查看失败");
        }
    }


    @PostMapping("exportExcel")
    public void exportExcel(@RequestBody MissionItemRecordVo bean, HttpServletResponse response) {
        try {
            missionService.exportExcel(bean, response);
        } catch (Exception e) {
            throw new CustomException("导出失败");
        }
    }

    @PostMapping("exportAssignExcel")
    public void exportAssignExcel(@RequestBody MissionItemRecordVo bean, HttpServletResponse response) {
        try {
            missionService.exportAssignExcel(bean, response);
        } catch (Exception e) {
            throw new CustomException("导出失败");
        }
    }

    @PostMapping("backOut.do")
    @ApiOperation("撤销")
    public ResultInfo backOut(Integer id, String notes) {
        try {
            Mission mission = missionService.getById(id);
            mission.setPublicity(1);
            mission.setBack_remark(notes);
            mission.setStates(5);
            return ResultInfo.success(missionService.updateById(mission));
        } catch (Exception e) {
            throw new CustomException("撤销失败");
        }
    }


    @PostMapping("ignoreMission.do")
    @ApiOperation("忽略")
    public ResultInfo ignoreMission(Mission bean) {
        try {
            return ResultInfo.success(missionService.updateById(bean));
        } catch (Exception e) {
            throw new CustomException("忽略失败");
        }
    }


    @PostMapping("updateIsReads.do")
    @ApiOperation("状态变成已读")
    public ResultInfo updateIsReads(Integer id) {
        try {
            Mission mission = missionService.getById(id);
            if (null == mission.getIsReads()) {
                mission.setIsReads(0);
            }
            if (null != mission.getIsReads() && 0 == mission.getIsReads()) {
                mission.setIsReads(1);
                missionService.updateById(mission);
            }
            return ResultInfo.success();
        } catch (Exception e) {
            throw new CustomException("已读失败");
        }
    }

    @GetMapping("getTimeWarnTJ")
    @ApiOperation("告警中心 统计 处理时间")
    public ResultInfo getTimeWarnTJ() {
        try {
            return ResultInfo.success(missionService.getTimeWarnTJ());
        } catch (Exception e) {
            throw new CustomException("获取时间失败");
        }
    }


}
