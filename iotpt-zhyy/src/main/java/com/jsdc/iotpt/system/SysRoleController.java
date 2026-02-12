package com.jsdc.iotpt.system;

import com.jsdc.iotpt.common.Constants;
import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.model.SysRole;
import com.jsdc.iotpt.service.SysRoleService;
import com.jsdc.iotpt.vo.LogVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @projectName: IOT
 * @className: SysRoleController
 * @author: wp
 * @description:
 * @date: 2023/5/9 17:03
 */
@RestController
@RequestMapping("sysRole")
@Api(tags = "角色管理")
public class SysRoleController {

    @Autowired
    SysRoleService roleService;

    @PostMapping("getRolePage.do")
    @ApiOperation("分页查询角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageIndex", value = "页码", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", dataType = "int")
    })
    public ResultInfo getRolePage(SysRole role, @RequestParam(name = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                  @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return ResultInfo.success(roleService.getRolePage(role, pageIndex, pageSize));
    }

    @PostMapping("getRoleList.do")
    @ApiOperation("所有角色列表")
    public ResultInfo getRoleList(SysRole role) {
        return ResultInfo.success(roleService.getRoleList(role));
    }

    @PostMapping("saveRole.do")
    @ApiOperation("新增或者编辑角色")
    @LogInfo(value = LogEnums.LOG_SYSTEM_ROLE_SAVE, model = Constants.MODEL_YYZT)
    public ResultInfo saveRole(@RequestBody SysRole role) {
        try {
            roleService.checkName(role);
        } catch (Exception e) {
            return ResultInfo.error(e.getMessage());
        }
        int code = roleService.saveRole(role);
        if (1 == code) {
            return ResultInfo.success("操作成功");
        } else if (-1 == code) {
            return ResultInfo.error("操作失败!");
        } else if (-2 == code) {
            return ResultInfo.error("添加角色重复!");
        }
        return ResultInfo.success();
    }

    @PostMapping("getRole.do")
    @ApiOperation("查询角色信息")
    public ResultInfo getRole(SysRole role) {
        return ResultInfo.success(roleService.getRole(role));
    }


    @PostMapping("editRole.do")
    @ApiOperation("编辑角色")
    @LogInfo(value = LogEnums.LOG_SYSTEM_ROLE_SAVE, model = Constants.MODEL_YYZT)
    public ResultInfo editRole(SysRole role) {
        return ResultInfo.success(roleService.editRole(role), new LogVo("编辑角色成功"));
    }

    @PostMapping("delRole.do")
    @ApiOperation("删除角色")
    @LogInfo(value = LogEnums.LOG_SYSTEM_ROLE_DEL, model = Constants.MODEL_YYZT)
    public ResultInfo delRole(SysRole role) {
        return ResultInfo.success(roleService.delRole(role), new LogVo("删除角色成功"));
    }

    @PostMapping("getRoleMenu.do")
    @ApiOperation("查询角色菜单")
    public ResultInfo getRoleMenu(Integer role_id) {
        return ResultInfo.success(roleService.getRoleMenu(role_id));
    }

    @PostMapping("getRoleUser.do")
    @ApiOperation("查询角色用户")
    public ResultInfo getRoleUser(Integer role_id) {
        return ResultInfo.success(roleService.getRoleUser(role_id));
    }

}
