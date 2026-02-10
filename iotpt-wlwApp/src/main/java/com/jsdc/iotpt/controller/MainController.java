package com.jsdc.iotpt.controller;


import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dahuatech.icc.exception.ClientException;
import com.google.common.base.Objects;
import com.jsdc.iotpt.common.Constants;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.dto.SysUserRoleDto;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.model.SysMenu;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.sys.SysOrgDept;
import com.jsdc.iotpt.model.sys.SysOrgManage;
import com.jsdc.iotpt.service.*;
import com.jsdc.iotpt.util.MD5Utils;
import com.jsdc.iotpt.util.StrongPasswordValidatorUtils;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.LogVo;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.UserRegisterVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 登录功能
 */
@RestController
@RequestMapping("/app")
@Slf4j
@Api(tags = "登录功能")
public class MainController {

    @Autowired
    private SysUserService userService;
    @Autowired
    private SysUserService sysUserMapper;
    @Autowired
    private SysOrgManageService sysOrgManageService;

    @Autowired
    SysOrgDeptService orgDeptService;
    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Value("${jsdc.nginxPath}")
    private String nginxPath;

    @Autowired
    private RestTemplate restTemplate;



    @LogInfo(value = LogEnums.LOG_WX_LOGIN, model = Constants.MODEL_APP)
    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ApiOperation(value = "登录接口", notes = "传递参数：userName：用户名(选填),passWord:密码(必填),code:微信生成的随机号")
    @ResponseBody
    public ResultInfo loginPost(String userName, String passWord, String code) {
        if (StringUtils.isNotEmpty(userName) && StringUtils.isNotEmpty(passWord)) {
            SaTokenInfo token = userService.login(userName, passWord);
            if (null != token) {
                SysUser sysUser = userService.getUser();
                if (null != sysUser.getIsEnable() && 0 == sysUser.getIsEnable()) {
                    throw new CustomException("账号已被禁用");
                }
                //如果不存在openId，则获取传递code获取openId
//                if (StringUtils.isEmpty(sysUser.getWxOpenId())) {
                if (StringUtils.isNotEmpty(code)) {
                    ResultInfo resultInfo = userService.wxLoginMethond(code);
                    if (resultInfo.getCode() == 0) {
                        Object data = resultInfo.getData();
                        Map entry = (Map) data;
                        String openid = String.valueOf(entry.get("openId"));
                        //判断是否已经存在openId，若存在提示用户已经被绑定

                        if (StringUtils.isNotEmpty(openid)) {
                            long count = userService.count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getIsDel, 0).eq(SysUser::getWxOpenId, openid));
                            if (count != 0) {
                                LambdaUpdateWrapper<SysUser> updateWrapper = Wrappers.lambdaUpdate(SysUser.class);
                                updateWrapper.eq(SysUser::getWxOpenId, openid).eq(SysUser::getIsDel, 0)
                                        .set(SysUser::getWxOpenId, "");
                                userService.update(updateWrapper);
                            }
                            sysUser.setWxOpenId(openid);
                            userService.updateById(sysUser);
                        }

//                            } else {
//                                throw new CustomException("账号已被绑定，请先解绑后登录");
//                            }
                    }
                }
//                }
                Integer deptId = sysUser.getDeptId();
                String orgName = "";
                String deptName = "";
                if (null != sysUser.getUnitId()) {
                    SysOrgManage org = sysOrgManageService.getById(sysUser.getUnitId());
                    orgName = null == org ? "" : org.getOrgName();
                }
                if (null != deptId) {
                    SysOrgDept dept = orgDeptService.getById(deptId);
                    if (null != dept) {
                        deptName = dept.getDeptName();
                    }
                }
                if (org.apache.commons.lang3.StringUtils.isNotBlank(orgName) && org.apache.commons.lang3.StringUtils.isNotBlank(deptName)) {
                    sysUser.setDeptName(orgName + "-" + deptName);
                } else if (org.apache.commons.lang3.StringUtils.isNotBlank(orgName)) {
                    sysUser.setDeptName(orgName);
                } else if (org.apache.commons.lang3.StringUtils.isNotBlank(deptName)) {
                    sysUser.setDeptName(deptName);
                }
                JSONObject jsonObject = new JSONObject();
                sysUser.setPassword("");
                sysUser.setHeadPortrait(nginxPath + sysUser.getHeadPortrait());
                // 角色标识
                List<SysUserRoleDto> userRole = sysUserRoleService.getUserRole(sysUser.getId());
                List<String> objects = new ArrayList<>();
                userRole.forEach(x -> {
                    objects.add(x.getRoleFlag());
                });
                sysUser.setRoleFlags(objects);
                List<SysMenu> authority = userService.selectSysMenuList(sysUser.getId(), G.WLW_APP);
                jsonObject.put("permissions", userService.buildMenus(authority));
                jsonObject.put("user", sysUser);
                jsonObject.put("token", token.getTokenValue());
                jsonObject.put("roles", StpUtil.getRoleList());
                return ResultInfo.success(jsonObject);
            } else {
                throw new CustomException("用户名或密码错误");
            }
        } else {
            throw new CustomException("用户名和密码不能为空");
        }
    }


    @PostMapping("/login/cid")
    public ResultInfo loginCid(String cid) {
        if (StringUtils.isBlank(cid)) {
            return ResultInfo.error("登录失效，请重新登录");
        }
        SysUser sysUser = userService.loginByCid(cid);
        if (sysUser == null) {
            return ResultInfo.error("登录失效，请重新登录");
        }
        if (null != sysUser.getIsEnable() && 0 == sysUser.getIsEnable()) {
            return ResultInfo.error("账号已被禁用");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user", sysUser);
        return ResultInfo.success(jsonObject);
    }

    @PostMapping("/register")
    @ApiOperation(value = "app用户注册")
    public ResultInfo register(@RequestBody UserRegisterVo userRegisterVo) throws ClientException, IOException {
        userService.register(userRegisterVo);
        return ResultInfo.success();
    }

    @LogInfo(value = LogEnums.LOG_WX_LOGIN, model = Constants.MODEL_APP)
    @PostMapping("/minAppSkip/login")
    public ResultInfo minAppSkipLogin(String loginName, String phone, String code) {
        if (StringUtils.isBlank(loginName) || StringUtils.isBlank(phone)) {
            return ResultInfo.error("缺少必要参数");
        }
        SaTokenInfo token = userService.skipLogin(loginName, phone);
        if (token == null) {
            return ResultInfo.error("用户不存在");
        }
        SysUser sysUser = userService.getUser();
        if (null != sysUser.getIsEnable() && 0 == sysUser.getIsEnable()) {
            return ResultInfo.error("账号已被禁用");
        }
        //如果不存在openId，则获取传递code获取openId
//                if (StringUtils.isEmpty(sysUser.getWxOpenId())) {
        if (StringUtils.isNotEmpty(code)) {
            ResultInfo resultInfo = userService.wxLoginMethond(code);
            if (resultInfo.getCode() == 0) {
                Object data = resultInfo.getData();
                Map entry = (Map) data;
                String openid = String.valueOf(entry.get("openId"));
                //判断是否已经存在openId，若存在提示用户已经被绑定
//                            long count = userService.count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getIsDel, 0).eq(SysUser::getWxOpenId, openid));
//                            if (count == 0) {
                if (StringUtils.isNotEmpty(openid)) {
                    sysUser.setWxOpenId(openid);
                    userService.updateById(sysUser);
                }
//                            } else {
//                                return ResultInfo.error("账号已被绑定，请先解绑后登录");
//                            }
            }
        }
//                }
        Integer deptId = sysUser.getDeptId();
        String orgName = "";
        String deptName = "";
        if (null != sysUser.getUnitId()) {
            SysOrgManage org = sysOrgManageService.getById(sysUser.getUnitId());
            orgName = null == org ? "" : org.getOrgName();
        }
        if (null != deptId) {
            SysOrgDept dept = orgDeptService.getById(deptId);
            if (null != dept) {
                deptName = dept.getDeptName();
            }
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(orgName) && org.apache.commons.lang3.StringUtils.isNotBlank(deptName)) {
            sysUser.setDeptName(orgName + "-" + deptName);
        } else if (org.apache.commons.lang3.StringUtils.isNotBlank(orgName)) {
            sysUser.setDeptName(orgName);
        } else if (org.apache.commons.lang3.StringUtils.isNotBlank(deptName)) {
            sysUser.setDeptName(deptName);
        }
        JSONObject jsonObject = new JSONObject();
        sysUser.setPassword("");
        sysUser.setHeadPortrait(nginxPath + sysUser.getHeadPortrait());
        // 角色标识
        List<SysUserRoleDto> userRole = sysUserRoleService.getUserRole(sysUser.getId());
        List<String> objects = new ArrayList<>();
        userRole.forEach(x -> {
            objects.add(x.getRoleFlag());
        });
        sysUser.setRoleFlags(objects);
        jsonObject.put("user", sysUser);
        jsonObject.put("token", token.getTokenValue());
        return ResultInfo.success(jsonObject);
    }


    @PostMapping("appLoginByName.do")
    @ApiOperation(value = "app登录刷新token", notes = "传递参数：loginName：登录名(必填)")
    public ResultInfo appLoginByName(String loginName) {
        loginName = org.apache.commons.lang3.StringUtils.trimToEmpty(loginName);
        if (StringUtils.isEmpty(loginName) || StringUtils.equals("undefind", loginName)) {
            return ResultInfo.error("请重新登录");
        }
        loginName = loginName.replace("\"", "");
        List<SysUser> list = userService.getBaseMapper().selectList(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getLoginName, loginName).eq(SysUser::getIsDel, G.ISDEL_NO));
        if (CollectionUtils.isEmpty(list)) {
            return ResultInfo.error("未通过登录名查询到用户");
        }
        SysUser user = list.get(0);
        if (null != user) {
            StpUtil.login(user.getId());
            SaTokenInfo token = StpUtil.getTokenInfo();
            StpUtil.getSession().set(user.getId().toString(), user);
            if (null != token) {
                SysUser sysUser = userService.getUser();
                if (null != sysUser.getIsEnable() && 0 == sysUser.getIsEnable()) {
                    return ResultInfo.error("账号已被禁用");
                }
                JSONObject jsonObject = new JSONObject();
                sysUser.setPassword("");
                sysUser.setHeadPortrait(nginxPath + sysUser.getHeadPortrait());

                // 角色标识
                List<SysUserRoleDto> userRole = sysUserRoleService.getUserRole(sysUser.getId());
                List<String> objects = new ArrayList<>();
                userRole.forEach(x -> {
                    objects.add(x.getRoleFlag());
                });
                sysUser.setRoleFlags(objects);
                jsonObject.put("user", sysUser);
                jsonObject.put("token", token.getTokenValue());
                return ResultInfo.success(jsonObject);
            }
            return ResultInfo.error("获取token失败");
        } else {
            return ResultInfo.error("用户不存在");
        }

    }

    /**
     * 查询当前登录的用户信息
     *
     * @return
     */
    @PostMapping("userInfo")
    @ApiOperation(value = "得到当前登录用户信息", notes = "传递参数：id：id(必填)")
    public ResultInfo getUserInfo(Integer id) {
//        SysUser user = userService.getUserByToken(accessToken);
        SysUser user = userService.getById(id);
        Integer deptId = user.getDeptId();
        String orgName = "";
        String deptName = "";
        if (null != user.getUnitId()) {
            SysOrgManage org = sysOrgManageService.getById(user.getUnitId());
            orgName = null == org ? "" : org.getOrgName();
        }
        if (null != deptId) {
            SysOrgDept dept = orgDeptService.getById(deptId);
            if (null != dept) {
                deptName = dept.getDeptName();
            }
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(orgName) && org.apache.commons.lang3.StringUtils.isNotBlank(deptName)) {
            user.setDeptName(orgName + "-" + deptName);
        } else if (org.apache.commons.lang3.StringUtils.isNotBlank(orgName)) {
            user.setDeptName(orgName);
        } else if (org.apache.commons.lang3.StringUtils.isNotBlank(deptName)) {
            user.setDeptName(deptName);
        }
        user.setHeadPortrait(nginxPath + user.getHeadPortrait());
        // 角色标识
        List<SysUserRoleDto> userRole = sysUserRoleService.getUserRole(user.getId());
        List<String> objects = new ArrayList<>();
        userRole.forEach(x -> {
            objects.add(x.getRoleFlag());
        });
        user.setRoleFlags(objects);
        JSONObject result = new JSONObject();
        List<SysMenu> menuList = userService.selectSysMenuList(user.getId(), G.WLW_APP);
        userService.putSysMenuInRedis(user.getId(), menuList, Constants.APP_PERMISSIONS_KEY);
        result.put("permissions", userService.buildMenus(menuList));
        result.put("username", user.getLoginName());
        result.put("token", StpUtil.getTokenInfo().getTokenValue());
//        result.put("avatar", "https://i.gtimg.cn/club/item/face/img/8/15918_100.gif");
        result.put("user", user);
        return ResultInfo.success(result);
    }

    /**
     * 登出
     *
     * @return
     */
    @LogInfo(value = LogEnums.LOG_EXIT, model = Constants.MODEL_APP)
    @PostMapping("logout")
    @ApiOperation("退出登录")
    public ResultInfo logout() {
        return userService.logout();
    }


    /**
     * 微信授权登录
     *
     * @param code
     * @return
     */
    @LogInfo(value = LogEnums.LOG_WX_LOGIN, model = Constants.MODEL_APP)
    @RequestMapping(value = "wxLogin", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo wxLogin(@RequestParam(name = "code") String code) {

        ResultInfo resultInfo = userService.wxLoginMethond(code);
        if (resultInfo.getCode() == 0) {
            JSONObject jo = (JSONObject) resultInfo.getData();
            String openId = jo.getString("openId");
            JSONObject returnJson = new JSONObject();
            List<SysUser> list = userService.list(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getWxOpenId, openId).eq(SysUser::getIsDel, 0));
            if (!list.isEmpty() && list.size() > 0) {
                SysUser sysUser = list.get(0);
                Integer deptId = sysUser.getDeptId();
                String orgName = "";
                String deptName = "";
                if (null != sysUser.getUnitId()) {
                    SysOrgManage org = sysOrgManageService.getById(sysUser.getUnitId());
                    orgName = null == org ? "" : org.getOrgName();
                }
                if (null != deptId) {
                    SysOrgDept dept = orgDeptService.getById(deptId);
                    if (null != dept) {
                        deptName = dept.getDeptName();
                    }
                }
                if (org.apache.commons.lang3.StringUtils.isNotBlank(orgName) && org.apache.commons.lang3.StringUtils.isNotBlank(deptName)) {
                    sysUser.setDeptName(orgName + "-" + deptName);
                } else if (org.apache.commons.lang3.StringUtils.isNotBlank(orgName)) {
                    sysUser.setDeptName(orgName);
                } else if (org.apache.commons.lang3.StringUtils.isNotBlank(deptName)) {
                    sysUser.setDeptName(deptName);
                }
                sysUser.setPassword("");
                sysUser.setHeadPortrait(nginxPath + sysUser.getHeadPortrait());
                // 角色标识
                List<SysUserRoleDto> userRole = sysUserRoleService.getUserRole(sysUser.getId());
                List<String> objects = new ArrayList<>();
                userRole.forEach(x -> {
                    objects.add(x.getRoleFlag());
                });
                sysUser.setRoleFlags(objects);
                StpUtil.login(sysUser.getId());
                SaTokenInfo token = StpUtil.getTokenInfo();
                StpUtil.getSession().set(sysUser.getId().toString(), sysUser);
                returnJson.put("user", sysUser);
                returnJson.put("token", token.getTokenValue());
                returnJson.put("roles", StpUtil.getRoleList());
            }

            returnJson.put("openId", openId);
            return ResultInfo.success(returnJson);
        }

        return ResultInfo.error("登录失败");

    }


    @PostMapping("editUser.do")
    @ApiOperation(value = "修改密码", notes = "传递参数：(id，password,oldPassWord必填)")
    @LogInfo(value = LogEnums.LOG_UP_PASSWORD, model = Constants.MODEL_APP)
    public ResultInfo editUser(Integer id, String password, String oldPassWord) {

        SysUser sysUsers = userService.getBaseMapper().selectOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getPassword, MD5Utils.getMD5(G.PLATFORM + oldPassWord)).eq(SysUser::getId, id));
        if (null == sysUsers) {
            // 项目初始化时 两个系统的密码不一致 ，但是又要用随便一个密码修改密码为统一
            // 如果园区密码不对 从统一门户中获取 验证对错
            sysUsers = userService.synchronizePassword(id,oldPassWord);
            if (null == sysUsers) {
                return ResultInfo.error("密码错误");
            }
        }
        if (Objects.equal(oldPassWord, password)) {
            return ResultInfo.error("新旧密码不可相同");
        }

        // 验证密码强度
        Map<String, String> map = StrongPasswordValidatorUtils.isValidPassword(password);
        if (map.get("code").equals("-1")) {
            throw new CustomException(map.get("msg"));
        }

        sysUsers.setPassword(MD5Utils.getMD5(G.PLATFORM + password));
        userService.getBaseMapper().updateById(sysUsers);
        sysUsers.setOldPassWord(password);
        return ResultInfo.success("修改成功");
    }

    @PostMapping("edit.do")
    @ApiOperation(value = "编辑用户", notes = "传递参数：(id 必填)")
    @LogInfo(value = LogEnums.LOG_UPDATE, model = Constants.MODEL_APP)
    public ResultInfo edit(SysUser user) {
        // 验证密码强度
//        Map<String, String> map = StrongPasswordValidatorUtils.isValidPassword(user.getPassword());
//        if (map.get("code").equals("-1")) {
//            return ResultInfo.error(map.get("msg"));
//        } //编辑用户不修改密码
        if (userService.editAppUser(user)) {
            return ResultInfo.success("操作成功", new LogVo("编辑用户成功"));
        } else {
            return ResultInfo.error("编辑用户失败");
        }
    }

    //修改头像
    @PostMapping("fileUpd.json")
    @ApiOperation(value = "修改头像", notes = "传递参数：file：文件(必填),userId:用户id(必填)")
    @LogInfo(value = LogEnums.LOG_UPDATE, model = Constants.MODEL_APP)
    @ResponseBody
    public ResultInfo updateImage(@RequestParam("file") MultipartFile files, Integer userId) {
        SysUser sysUser = userService.updateImage(files, userId);
        sysUser.setHeadPortrait(nginxPath + sysUser.getHeadPortrait());
        Integer deptId = sysUser.getDeptId();
        String orgName = "";
        String deptName = "";
        if (null != sysUser.getUnitId()) {
            SysOrgManage org = sysOrgManageService.getById(sysUser.getUnitId());
            orgName = null == org ? "" : org.getOrgName();
        }
        if (null != deptId) {
            SysOrgDept dept = orgDeptService.getById(deptId);
            if (null != dept) {
                deptName = dept.getDeptName();
            }
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(orgName) && org.apache.commons.lang3.StringUtils.isNotBlank(deptName)) {
            sysUser.setDeptName(orgName + "-" + deptName);
        } else if (org.apache.commons.lang3.StringUtils.isNotBlank(orgName)) {
            sysUser.setDeptName(orgName);
        } else if (org.apache.commons.lang3.StringUtils.isNotBlank(deptName)) {
            sysUser.setDeptName(deptName);
        }
        return ResultInfo.success(sysUser);
    }

    /**
     * 注销用户
     *
     * @param user
     * @return
     * @throws ClientException
     */
    @PostMapping("signOutUser")
    @ApiOperation("注销用户")
    @LogInfo(value = LogEnums.LOG_SIGN_NO, model = Constants.MODEL_APP)
    public ResultInfo signOutUser(SysUser user) throws ClientException {
        if (sysUserMapper.signOutUser(user)) {
            return ResultInfo.success("注销成功", new LogVo("注销用户成功"));
        } else {
            return ResultInfo.error("注销用户失败");
        }
    }

    /**
     * 添加cId
     *
     * @param user
     * @return
     * @throws ClientException
     */
    @PostMapping("addCId")
    @ApiOperation("添加cId")
    public ResultInfo addCId(SysUser user) throws ClientException {
        SysUser sysUser = sysUserMapper.getById(user.getId());
//        LambdaUpdateWrapper<SysUser> wrapper = new LambdaUpdateWrapper<>();
//        wrapper.eq(SysUser::getId, sysUser.getId());
//        wrapper.set(SysUser::getCId,user.getCId());
        sysUser.setCId(user.getCId());
        sysUser.setLoginCid(user.getCId());
        return ResultInfo.success(sysUserMapper.updateById(sysUser));
    }


    @SneakyThrows
    @GetMapping("/statement")
    public ResultInfo userAgreement() {
        String filePath = "D:/dingchi/protocol/statement.txt";
//        String filePath = "D:/Desktop/statement.txt";
        Stream<String> lines = Files.lines(Paths.get(filePath));
        List<String> collect = lines.collect(Collectors.toList());
        String join = String.join("<br>", collect);
        return ResultInfo.success((Object) join);
    }


    @SneakyThrows
    @GetMapping("/privacy")
    public ResultInfo privacyAgreement() {
        String filePath = "D:/dingchi/protocol/privacy.txt";
//        String filePath = "D:/Desktop/privacy.txt";
        Stream<String> lines = Files.lines(Paths.get(filePath));
        List<String> collect = lines.collect(Collectors.toList());
        String join = String.join("<br>", collect);
        return ResultInfo.success((Object) join);
    }

    /**
     * AI生成规范
     * @return
     */
    @SneakyThrows
    @GetMapping("/aiword")
    public ResultInfo aiword() {
        String filePath = "D:/dingchi/protocol/aiword.txt";
//        String filePath = "D:/Desktop/privacy.txt";
        Stream<String> lines = Files.lines(Paths.get(filePath));
        List<String> collect = lines.collect(Collectors.toList());
        String join = String.join("<br>", collect);
        return ResultInfo.success((Object) join);
    }

    /**
     * 通用读取协议接口
     * 防止以后在加 。给一个通用的接口
     * @return
     */
    @SneakyThrows
    @GetMapping("/genericDoc")
    public ResultInfo genericDoc(String docName) {
        String filePath = "D:/dingchi/protocol/"+docName+".txt";
        File targetFile = new File(filePath);
        if (!targetFile.exists())  {
            String join = "文件不存在";
            return ResultInfo.success((Object) join);
        }
        Stream<String> lines = Files.lines(Paths.get(filePath));
        List<String> collect = lines.collect(Collectors.toList());
        String join = String.join("<br>", collect);
        return ResultInfo.success((Object) join);
    }


    @SneakyThrows
    @GetMapping("/ios/statement")
    public ResultInfo IOSuserAgreement() {
        String filePath = "D:/dingchi/protocol/IOSstatement.txt";
        Stream<String> lines = Files.lines(Paths.get(filePath));
        List<String> collect = lines.collect(Collectors.toList());
        String join = String.join("<br>", collect);
        return ResultInfo.success((Object) join);
    }


    @SneakyThrows
    @GetMapping("/ios/privacy")
    public ResultInfo IOSprivacyAgreement() {
        String filePath = "D:/dingchi/protocol/IOSprivacy.txt";
        Stream<String> lines = Files.lines(Paths.get(filePath));
        List<String> collect = lines.collect(Collectors.toList());
        String join = String.join("<br>", collect);
        return ResultInfo.success((Object) join);
    }


    @RequestMapping(value = "tymhLogin", method = RequestMethod.POST)
    @ApiOperation("统一门户园区验证")
    @ResponseBody
    public ResultInfo tymhLogin(String phone) {
        if (StringUtils.isNotEmpty(phone)) {
            SysUser user = userService.getOne(new QueryWrapper<SysUser>().eq("phone", phone).eq("isDel", "0").eq("isloginty", "1"));
            if (null == user) {
                return ResultInfo.error("用户不存在于园区系统");
            }
            SaTokenInfo token = userService.login(user.getLoginName(), "");
            if (null != token) {
                SysUser sysUser = userService.getUser();
                if (null != sysUser.getIsEnable() && 0 == sysUser.getIsEnable()) {
                    return ResultInfo.error("账号已被禁用");
                }
                //如果不存在openId，则获取传递code获取openId
//                if (StringUtils.isEmpty(sysUser.getWxOpenId())) {
//                }
                Integer deptId = sysUser.getDeptId();
                String orgName = "";
                String deptName = "";
                if (null != sysUser.getUnitId()) {
                    SysOrgManage org = sysOrgManageService.getById(sysUser.getUnitId());
                    orgName = null == org ? "" : org.getOrgName();
                }
                if (null != deptId) {
                    SysOrgDept dept = orgDeptService.getById(deptId);
                    if (null != dept) {
                        deptName = dept.getDeptName();
                    }
                }
                if (org.apache.commons.lang3.StringUtils.isNotBlank(orgName) && org.apache.commons.lang3.StringUtils.isNotBlank(deptName)) {
                    sysUser.setDeptName(orgName + "-" + deptName);
                } else if (org.apache.commons.lang3.StringUtils.isNotBlank(orgName)) {
                    sysUser.setDeptName(orgName);
                } else if (org.apache.commons.lang3.StringUtils.isNotBlank(deptName)) {
                    sysUser.setDeptName(deptName);
                }
                JSONObject jsonObject = new JSONObject();
                sysUser.setPassword("");
                sysUser.setHeadPortrait(nginxPath + sysUser.getHeadPortrait());
                // 角色标识
                List<SysUserRoleDto> userRole = sysUserRoleService.getUserRole(sysUser.getId());
                List<String> objects = new ArrayList<>();
                userRole.forEach(x -> {
                    objects.add(x.getRoleFlag());
                });
                sysUser.setRoleFlags(objects);

                List<SysMenu> authority = userService.selectSysMenuList(sysUser.getId(), G.WLW_APP);
                jsonObject.put("permissions", userService.buildMenus(authority));
                jsonObject.put("user", sysUser);
                jsonObject.put("token", token.getTokenValue());
                return ResultInfo.success(jsonObject);
            } else {
                return ResultInfo.error("用户名或密码错误");
            }
        } else {
            return ResultInfo.error("手机不得为空");
        }
    }

}
