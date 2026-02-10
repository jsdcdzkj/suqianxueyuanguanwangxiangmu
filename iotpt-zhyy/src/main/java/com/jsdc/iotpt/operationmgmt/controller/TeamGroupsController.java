package com.jsdc.iotpt.operationmgmt.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.SysRole;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.operate.TeamGroups;
import com.jsdc.iotpt.service.SysOrgManageService;
import com.jsdc.iotpt.service.SysRoleService;
import com.jsdc.iotpt.service.SysUserService;
import com.jsdc.iotpt.service.TeamGroupsService;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teamGroups")
public class TeamGroupsController {

    @Autowired
    private TeamGroupsService teamGroupsService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysOrgManageService sysOrgManageService;

    @Autowired
    SysRoleService roleService;

    @PostMapping("getTeamPage.do")
    @ApiOperation("分页查询班组")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageIndex",value = "页码",dataType = "int"),
            @ApiImplicitParam(name = "pageSize",value = "每页条数",dataType = "int")
    })
    public ResultInfo getTeamPage(TeamGroups bean, @RequestParam(name = "pageNo") Integer pageIndex, @RequestParam(name = "pageSize") Integer pageSize){
        Page<TeamGroups> page =teamGroupsService.getTeamGroupPage(bean, pageIndex, pageSize);
        return ResultInfo.success(page);
    }


    @PostMapping("getUserPage.do")
    @ApiOperation("分页查询用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageIndex",value = "页码",dataType = "int"),
            @ApiImplicitParam(name = "pageSize",value = "每页条数",dataType = "int")
    })
    public ResultInfo getUserPage(SysUser user, @RequestParam(name = "pageNo") Integer pageIndex, @RequestParam(name = "pageSize") Integer pageSize){
        Page<SysUser> page = sysUserService.getUserPage(user, pageIndex, pageSize);
        return ResultInfo.success(page);
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

    @PostMapping("getRoleList.do")
    @ApiOperation("所有角色列表")
    public ResultInfo getRoleList(SysRole role){
        return ResultInfo.success(roleService.getRoleList(role));
    }



    @PostMapping("addTeam.do")
    @ApiOperation("新增编辑班组")
    public ResultInfo addTeam(@RequestBody TeamGroups bean){
        return teamGroupsService.editTeamGroup(bean);
    }




    @PostMapping("delTeam.do")
    @ApiOperation("删除班组")
    public ResultInfo delTeam(TeamGroups bean){
        return teamGroupsService.delTeamGroup(bean.getId());
    }

    @PostMapping("detailTeam.do")
    @ApiOperation("班组详情")
    public ResultInfo getDetail(TeamGroups bean){
        return ResultInfo.success(teamGroupsService.getOneDetail(bean.getId()));
    }


    @PostMapping("getAllList.do")
    @ApiOperation("获取所有班组")
    public ResultInfo getAllList(){
        return ResultInfo.success(teamGroupsService.getAllList());
    }



    @PostMapping("getroleDicts.do")
    @ApiOperation("获取所有班组角色")
    public ResultInfo getroleDicts(){
        return ResultInfo.success(teamGroupsService.getroleDicts());
    }



    @PostMapping("tree")
    @ApiOperation("班组树形结构")
    public ResultInfo getGroupTree(){
        return ResultInfo.success(teamGroupsService.getTeamlist());
    }



}
