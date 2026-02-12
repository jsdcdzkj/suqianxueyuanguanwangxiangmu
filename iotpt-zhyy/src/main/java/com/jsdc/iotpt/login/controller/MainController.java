package com.jsdc.iotpt.login.controller;


import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.jsdc.iotpt.common.Constants;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.init.RedisDataInit;
import com.jsdc.iotpt.mapper.InvestmentPersonMapper;
import com.jsdc.iotpt.model.InvestmentPerson;
import com.jsdc.iotpt.model.SysMenu;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.service.SysMenuService;
import com.jsdc.iotpt.service.SysUserService;
import com.jsdc.iotpt.util.StrongPasswordValidatorUtils;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;


/**
 * 系统登录
 */
@RestController
@RequestMapping("/")
@Api(tags = "系统登录")
public class MainController {

    @Autowired
    private SysUserService userService;

    @Autowired
    private SysMenuService menuService;

    @Autowired
    private RedisDataInit redisDataInit;


    @Value("${systemName}")
    private String systemName;
    @Autowired
    private InvestmentPersonMapper investmentPersonMapper;


    @PostMapping("/validate")
    @ApiOperation("校验密码强度")
    public ResultInfo login(@RequestBody String password) {
        Map<String, String> map = StrongPasswordValidatorUtils.isValidPassword(password);
        if (map.get("code").equals("-1")) {
            throw new CustomException(map.get("msg"));
        } else {
            return ResultInfo.success();
        }
    }


    @PostMapping("login.do")
    @ApiOperation("登录")
    @LogInfo(value = LogEnums.LOG_LOGIN, model = Constants.MODEL_ZHYY)
    public ResultInfo login(@RequestBody Map<String, Object> params) {
        String username = MapUtils.getString(params, "username");
        String password = MapUtils.getString(params, "password");
        JSONObject jsonObject = new JSONObject();
        String type = MapUtils.getString(params, "type");
        String cardInfo = MapUtils.getString(params, "cardinfo");
        if ("2".equals(type) && StrUtil.isNotEmpty(cardInfo)) {
            cardInfo = URLDecoder.decode(cardInfo);
            String cardNo = "";
            System.out.println(cardInfo);
            SysUser sysUser = userService.getOne(new QueryWrapper<SysUser>().eq("phone", cardNo).eq("isDel", "0").eq("isloginty", "1"));
            if (sysUser != null) {
                SaTokenInfo token = userService.login(sysUser.getLoginName(), "");
                if (null != token) {
                    SysUser user = userService.getUser();
                    if (0 == user.getIsEnable()) {
                        throw new CustomException("账号已被禁用");
                    }
                    List<SysMenu> authority = userService.selectSysMenuList(user.getId(), G.APP_PLATFORM);
                    jsonObject.put("permissions", userService.buildMenus(authority));
                    jsonObject.put("code", 200);
                    jsonObject.put("success", true);
                    jsonObject.put("msg", "登录成功");
                    jsonObject.put("token", token);
                    jsonObject.put("user", user);
                } else {
                    throw new CustomException("账号或者密码错误");
                }
            } else {
                throw new CustomException("账号未同步");
            }
        } else {
            if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
                throw new CustomException("账号或密码不能为空");
            }
            if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
                SaTokenInfo token = userService.login(username, password);
                if (null != token) {
                    SysUser user = userService.getUser();
                    if (0 == user.getIsEnable()) {
                        throw new CustomException("账号已被禁用");
                    }
                    List<SysMenu> authority = userService.selectSysMenuList(user.getId(), G.APP_WISDOM);
                    jsonObject.put("permissions", userService.buildMenus(authority));
                    jsonObject.put("code", 200);
                    jsonObject.put("success", true);
                    jsonObject.put("msg", "登录成功");
                    jsonObject.put("token", token);
//                jsonObject.put("authority", authority);
                    jsonObject.put("user", user);
                } else {
                    throw new CustomException("账号或者密码错误");
                }
            }
        }
        redisDataInit.dictInit();
        return ResultInfo.success(jsonObject);
    }

    /**
     * 查询当前登录的用户信息
     *
     * @return
     */
    @PostMapping("userInfo.do")
    @ApiOperation("得到当前登录用户信息")
    public ResultInfo getUserInfo(String accessToken) {
        SysUser user = userService.getUserByToken(accessToken);

        List<SysMenu> menuList = userService.selectSysMenuList(user.getId(), G.APP_WISDOM);
        userService.putSysMenuInRedis(user.getId(), menuList, Constants.ZHYY_PERMISSIONS_KEY);

        JSONObject result = new JSONObject();
        result.put("permissions", userService.buildMenus(userService.selectSysMenuList(user.getId(), G.APP_WISDOM)));
        result.put("username", user.getLoginName());
        result.put("user", user);
        result.put("avatar", "https://i.gtimg.cn/club/item/face/img/8/15918_100.gif");

        InvestmentPerson investmentPerson = investmentPersonMapper.selectOne(new LambdaQueryWrapper<InvestmentPerson>().eq(InvestmentPerson::getIsDel, 0).eq(InvestmentPerson::getUser_id, user.getId()));
        if (null != investmentPerson) {
            result.put("is_manager", investmentPerson.getIs_manager());
        } else {
            result.put("is_manager", "0");
        }

        return ResultInfo.success(result);
    }

    /**
     * 查询当前登录的用户信息
     *
     * @return
     */
    @PostMapping("userInfoPermissions.do")
    @ApiOperation("得到当前登录用户信息")
    public ResultInfo getUserInfo() {
        SysUser user = userService.getUser();
        JSONObject result = new JSONObject();
        result.put("permissions", userService.buildMenus(userService.selectSysMenuList(user.getId(), G.APP_WISDOM)));
        result.put("username", user.getLoginName());
        result.put("avatar", "https://i.gtimg.cn/club/item/face/img/8/15918_100.gif");
        return ResultInfo.success(result);
    }

    /**
     * 登出
     *
     * @return
     */
    @LogInfo(value = LogEnums.LOG_EXIT, model = Constants.MODEL_ZHYY)
    @PostMapping("logout.do")
    @ApiOperation("退出登录")
    public ResultInfo logout() {
        return userService.logout();
    }


    @RequestMapping("/kickout.do")
    @ApiOperation("")
    public ResultInfo kickout(int userId) {

        if (userId > 0) {
            StpUtil.logout(userId);
            StpUtil.kickout(userId);
        }
        return ResultInfo.success(userId);
    }

    @RequestMapping("/checkLogin.do")
    @ApiOperation("")
    public ResultInfo checkLogin(int userId) {
        try {
            StpUtil.checkLogin();
        } catch (Exception e) {
            throw new CustomException("未登录");
        }
        return ResultInfo.success(userId);
    }


}
