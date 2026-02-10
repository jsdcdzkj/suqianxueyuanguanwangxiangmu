package com.jsdc.iotpt.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dahuatech.icc.exception.ClientException;
import com.jsdc.iotpt.common.Constants;
import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.service.SysUserService;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.LogVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @projectName: IOT
 * @className: SysUserController
 * @author: wp
 * @description:
 * @date: 2023/5/8 14:12
 */
@RestController
@RequestMapping("sysuser")
@Api(tags = "系统用户管理")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;


    @PostMapping("getUserPage.do")
    @ApiOperation("分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageIndex",value = "页码",dataType = "int"),
            @ApiImplicitParam(name = "pageSize",value = "每页条数",dataType = "int")
    })
    public ResultInfo getUserPage(SysUser user, @RequestParam(name = "pageNo") Integer pageIndex, @RequestParam(name = "pageSize") Integer pageSize){
        try {
            Page<SysUser> page = sysUserService.getUserPage(user, pageIndex, pageSize);
            return ResultInfo.success(page);
        }catch (CustomException e){
            throw e;
        } catch (Exception e){
            e.printStackTrace();
            throw new CustomException("获取列表失败!");
        }

    }

    @PostMapping("getUser.do")
    @ApiOperation("查询用户信息")
    public ResultInfo getUser(SysUser user){
        return ResultInfo.success(sysUserService.getUser(user));
    }


    /**
     * 得到redis数据字典返回map
     */
    @PostMapping("/getRedisUserDictMap")
    @ApiOperation("数据字典列表")
    public ResultInfo getRedisUserDictMap() {
        return ResultInfo.success(RedisUtils.getBeanValue("userDict"));
    }

    @PostMapping("addUser.do")
    @ApiOperation("新增用户")
    @LogInfo(value = LogEnums.LOG_SYSTEM_USER_SAVE,model = Constants.MODEL_YYZT)
    public ResultInfo addUser(@RequestBody SysUser user) throws ClientException, IOException {
        if (sysUserService.checkUserName(user)) {
            return ResultInfo.error("用户名已存在");
        }
        // 验证密码是否大于6位
        if(user.getPassword().length() < 6){
            return ResultInfo.error("密码长度不能小于6位");
        }
        if(sysUserService.addUser(user)){
            return ResultInfo.success(user.getId());
        }else{
            return ResultInfo.error("新增用户失败");
        }
    }

    @PostMapping("editUser.do")
    @ApiOperation("编辑用户")
    @LogInfo(value = LogEnums.LOG_SYSTEM_USER_UPDATE,model = Constants.MODEL_YYZT)
    public ResultInfo editUser(@RequestBody SysUser user) throws ClientException, IOException {
        try{
            if (sysUserService.checkUserName(user)) {
                return ResultInfo.error("用户名已存在");
            }
            // 验证密码是否大于6位
            if(user.getPassword().length() < 6){
                return ResultInfo.error("密码长度不能小于6位");
            }
            if(sysUserService.editUser(user)){
                return ResultInfo.success(user.getId());
            }else{
                return ResultInfo.error("编辑用户失败");
            }
        }catch (CustomException e){
            throw e;
        } catch (Exception e){
            e.printStackTrace();
            throw new CustomException("编辑用户失败!");
        }

    }

    @PostMapping("delUser.do")
    @ApiOperation("删除用户")
    @LogInfo(value = LogEnums.LOG_SYSTEM_USER_DELETE,model = Constants.MODEL_YYZT)
    public ResultInfo delUser(SysUser user) throws ClientException {
        if(sysUserService.delUser(user)){
            return ResultInfo.success("删除成功", new LogVo("删除用户成功"));
        }else{
            return ResultInfo.error("删除用户失败");
        }
    }

    @PostMapping("doEnable.do")
    @ApiOperation("启用/禁用用户")
    @LogInfo(LogEnums.LOG_SYSTEM_USER)
    public ResultInfo doEnable(SysUser user) throws ClientException {
        if(sysUserService.doEnable(user)){
            return ResultInfo.success("启动/禁用用户成功", new LogVo("启动/禁用用户成功"));
        }else{
            return ResultInfo.error("启用/禁用用户失败");
        }
    }
    /**
     * 添加通行权限
     */
    @RequestMapping("/accessAuthority")
    @ApiOperation("添加通行权限")
    @LogInfo(value = LogEnums.LOG_SYSTEM_USER_AUTHORITY,model = Constants.MODEL_YYZT)
    public ResultInfo accessAuthority(@RequestBody SysUser user) throws ClientException {
        return ResultInfo.success(sysUserService.accessAuthority(user));
    }
}
