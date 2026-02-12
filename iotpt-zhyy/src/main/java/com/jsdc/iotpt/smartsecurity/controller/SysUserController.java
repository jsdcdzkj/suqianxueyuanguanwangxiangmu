//package com.jsdc.iotpt.smartsecurity.controller;
//
//import cn.hutool.core.util.StrUtil;
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.dahuatech.icc.exception.ClientException;
//import com.jsdc.iotpt.common.Constants;
//import com.jsdc.iotpt.common.G;
//import com.jsdc.iotpt.common.logaop.LogInfo;
//import com.jsdc.iotpt.enums.LogEnums;
//import com.jsdc.iotpt.init.RedisDataInit;
//import com.jsdc.iotpt.model.SysUser;
//import com.jsdc.iotpt.service.SysOrgManageService;
//import com.jsdc.iotpt.service.SysUserService;
//import com.jsdc.iotpt.util.MD5Utils;
//import com.jsdc.iotpt.util.StrongPasswordValidatorUtils;
//import com.jsdc.iotpt.util.exception.CustomException;
//import com.jsdc.iotpt.vo.LogVo;
//import com.jsdc.iotpt.vo.ResultInfo;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiImplicitParam;
//import io.swagger.annotations.ApiImplicitParams;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
/// **
// * @projectName: IOT
// * @className: SysUserController
// * @author: wp
// * @description:
// * @date: 2023/5/8 14:12
// */
//@RestController
//@RequestMapping("sysuser")
//@Slf4j
//@Api(tags = "系统用户管理")
//public class SysUserController {
//
//    @Autowired
//    private SysUserService sysUserService;
//
//    @Autowired
//    private SysOrgManageService sysOrgManageService;
//
//    @Autowired
//    private RedisDataInit redisDataInit;
//
//
//    @PostMapping("getUserPage.do")
//    @ApiOperation("分页查询")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "pageIndex",value = "页码",dataType = "int"),
//            @ApiImplicitParam(name = "pageSize",value = "每页条数",dataType = "int")
//    })
//    public ResultInfo getUserPage(SysUser user, @RequestParam(name = "pageNo") Integer pageIndex, @RequestParam(name = "pageSize") Integer pageSize){
//        try {
//            Page<SysUser> page = sysUserService.getUserPage(user, pageIndex, pageSize);
//            return ResultInfo.success(page);
//        } catch (Exception e) {
//           e.printStackTrace();
//            throw new CustomException("获取列表失败");
//        }
//    }
//
//    @PostMapping("getUser.do")
//    @ApiOperation("查询用户信息")
//    public ResultInfo getUser(SysUser user){
//        try {
//            return ResultInfo.success(sysUserService.getUser(user));
//        } catch (Exception e) {
//           e.printStackTrace();
//            throw new CustomException("查询用户信息失败");
//        }
//    }
//
//    /**
//     * 单位树形图结构
//     * Author wzn
//     * Date 2023/6/30 8:58
//     */
//    @PostMapping("/orgTreeList")
//    public ResultInfo orgTreeList() {
//        try {
//            return ResultInfo.success(sysOrgManageService.orgTreeList());
//        } catch (Exception e) {
//           e.printStackTrace();
//            throw new CustomException("获取单位树失败");
//        }
//    }
//
//
//
//    @PostMapping("getUserTree.do")
//    @ApiOperation("分组树")
//    public ResultInfo getUserTree(){
//        try {
//            return ResultInfo.success(sysUserService.getUserTree());
//        } catch (Exception e) {
//           e.printStackTrace();
//            throw new CustomException("获取分组树失败");
//        }
//    }
//
//
//    @PostMapping("changePassword.do")
//    @ApiOperation("修改密码")
//    @LogInfo(value = LogEnums.LOG_SYSTEM_USER,model = Constants.MODEL_ZHYY)
//    public ResultInfo editUser(@RequestBody SysUser user) throws ClientException, IOException {
//        try {
//            if (StrUtil.isBlank(user.getOldPassWord())){
//                throw new CustomException("原密码不能为空");
//            }
//            // 得到当前登录的用户
//            SysUser current = sysUserService.getUser();
//            // 比较原密码 和当前用户密码是否一样
//            if (!StrUtil.equals(MD5Utils.getMD5(G.PLATFORM + user.getOldPassWord()), current.getPassword())){
//                SysUser sysUser = this.sysUserService.synchronizePassword(current.getId(), user.getOldPassWord());
//                if (sysUser == null){
//                    throw new CustomException("原密码错误");
//                }
//            }
//
//            // 验证密码强度
//            Map<String, String> map = StrongPasswordValidatorUtils.isValidPassword(user.getPassword());
//            if (map.get("code").equals("-1")) {
//                throw new CustomException(map.get("msg"));
//            }
//            try {
//                sysUserService.update(Wrappers.<SysUser>lambdaUpdate()
//                        .eq(SysUser::getId, current.getId())
//                        .set(SysUser::getPassword, MD5Utils.getMD5(G.PLATFORM + user.getPassword()))
//                );
//            } catch (Exception e) {
//                throw new CustomException("修改密码失败, " + e.getMessage());
//            }
//            SysUser newUser = sysUserService.getBaseMapper().selectById(current.getId());
//            newUser.setOldPassWord(user.getPassword());
//            List<SysUser> objects = new ArrayList<>();
//            objects.add(newUser);
//
//            redisDataInit.updateUsersCache(newUser);
//            return ResultInfo.success("操作成功", new LogVo("修改密码成功"));
//        } catch (Exception e) {
//           e.printStackTrace();
//            throw new CustomException("修改密码失败");
//        }
//    }
//}
