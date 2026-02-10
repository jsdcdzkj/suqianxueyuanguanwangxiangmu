package com.jsdc.iotpt.controller;


import cn.dev33.satoken.stp.SaTokenInfo;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.aspose.words.WarningInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.iotpt.common.Constants;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.init.RedisDataInit;
import com.jsdc.iotpt.model.SysMenu;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.service.*;
import com.jsdc.iotpt.util.RedisDictDataUtil;
import com.jsdc.iotpt.util.StrongPasswordValidatorUtils;
import com.jsdc.iotpt.vo.DeviceCollectVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
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

    @PostMapping("/validate")
    @ApiOperation("校验密码强度")
    public ResultInfo login(@RequestBody String password) {
        Map<String, String> map = StrongPasswordValidatorUtils.isValidPassword(password);
        if (map.get("code").equals("-1")) {
            return ResultInfo.error(map.get("msg"));
        }else  {
            return ResultInfo.success();
        }
    }


    @PostMapping("login.do")
    @ApiOperation("登录")
//    @LogInfo(value = LogEnums.LOG_LOGIN, model = Constants.MODEL_YYZT)
    public ResultInfo login(@RequestBody Map<String, Object> params) {
        String username = MapUtils.getString(params, "username");
        String password = MapUtils.getString(params,"password");
        JSONObject jsonObject = new JSONObject();
        String type = MapUtils.getString(params,"type");
        String cardInfo = MapUtils.getString(params,"cardinfo");
        if ("2".equals(type) && StrUtil.isNotEmpty(cardInfo)){
            cardInfo = URLDecoder.decode(cardInfo);
            String cardNo = "";
            System.out.println(cardInfo);
            SysUser sysUser = userService.getOne(new QueryWrapper<SysUser>().eq("phone", cardNo).eq("isDel", "0"));
            if (sysUser != null) {
                SaTokenInfo token = userService.login(sysUser.getLoginName(), "");
                if (null != token) {
                    SysUser user = userService.getUser();
                    if ( 0 == user.getIsEnable()) {
                        return ResultInfo.error("账号已被禁用");
                    }
                    List<SysMenu> authority = userService.selectSysMenuList(user.getId(), G.APP_PLATFORM);
                    jsonObject.put("permissions", userService.buildMenus(authority));
                    jsonObject.put("code", 200);
                    jsonObject.put("success", true);
                    jsonObject.put("msg", "登录成功");
                    jsonObject.put("token", token);
                    jsonObject.put("user", user);
                } else {
                    return ResultInfo.error("账号或者密码错误");
                }
            } else {
                return ResultInfo.error("用户名或密码错误");
            }
        }else {
            if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
                return ResultInfo.error("账号或密码不能为空");
            }
            if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
                SaTokenInfo token = userService.login(username, password);
                if (null != token) {
                    SysUser user = userService.getUser();
                    if ( 0 == user.getIsEnable()) {
                        return ResultInfo.error("账号已被禁用");
                    }
                    List<SysMenu> authority = userService.selectSysMenuList(user.getId(), G.APP_PLATFORM);
                    jsonObject.put("permissions", userService.buildMenus(authority));
                    jsonObject.put("code", 200);
                    jsonObject.put("success", true);
                    jsonObject.put("msg", "登录成功");
                    jsonObject.put("token", token);
//                jsonObject.put("authority", authority);
                    jsonObject.put("user", user);
                } else {
                    return ResultInfo.error("账号或者密码错误");
                }
            }
        }
        redisDataInit.dictInit();
        return ResultInfo.success(jsonObject);
    }


    @PostMapping("/loginByCar.do")
    @ApiOperation("登录")
    @LogInfo(value = LogEnums.LOG_LOGIN, model = Constants.MODEL_YYZT)
    public ResultInfo loginByCar(@RequestBody String body) {
        JSONObject params = JSONObject.parseObject(body);
        String username = params.getString("username");
        String password = params.getString("password");
        JSONObject jsonObject = new JSONObject();
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return ResultInfo.error("账号或密码不能为空");
        }
        if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
            boolean token = userService.token(username, password);
            if (token) {
                jsonObject.put("code", 0);
                jsonObject.put("success", true);
                jsonObject.put("msg", "登录成功");
            } else {
                return ResultInfo.error("账号或者密码错误");
            }
        }
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
        JSONObject result = new JSONObject();
        List<SysMenu> authority = new ArrayList<>();
        if (null != user && StringUtils.equals(user.getLoginName(), "zdq")) {
            authority = menuService.getBaseMapper().selectList(Wrappers.<SysMenu>lambdaQuery().eq(SysMenu::getIsDel, G.ISDEL_NO).eq(SysMenu::getSystemId, 2).orderByAsc(SysMenu::getSort));
        } else {
            authority = userService.selectSysMenuList(user.getId(), G.APP_PLATFORM);
        }
//        // 过滤title 包含首页
//        authority.removeIf(sysMenu -> sysMenu.getTitle().contains("首页"));
//        List<RouterVo> sysMenus = new ArrayList<>();
//        String json = "{\"children\":[{\"children\":[],\"component\":\"index/index\",\"id\":\"62\",\"meta\":{\"icon\":\"chess-king\",\"title\":\"首页\"},\"name\":\"Index\",\"parentId\":\"61\",\"path\":\"index\"}],\"component\":\"Layout\",\"id\":\"61\",\"meta\":{\"title\":\"首页/\"},\"name\":\"/\",\"parentId\":\"0\",\"path\":\"/\",\"redirect\":\"index\"}\n";
//        RouterVo routerVo = JSON.parseObject(json, RouterVo.class);
//        sysMenus.add(routerVo);
//        sysMenus.addAll(userService.buildMenus(authority));
//        result.put("permissions", sysMenus);
        result.put("permissions", userService.buildMenus(authority));
        result.put("username", user.getLoginName());
        result.put("avatar", "https://i.gtimg.cn/club/item/face/img/8/15918_100.gif");
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
        List<SysMenu> authority = new ArrayList<>();
        if (null != user && StringUtils.equals(user.getLoginName(), "zdq")) {
            authority = menuService.getBaseMapper().selectList(Wrappers.<SysMenu>lambdaQuery().eq(SysMenu::getIsDel, G.ISDEL_NO).orderByAsc(SysMenu::getSort));
        } else {
            authority = userService.selectSysMenuList(user.getId(), G.APP_PLATFORM);
        }
        result.put("permissions", userService.buildMenus(authority));
        result.put("username", user.getLoginName());
        result.put("avatar", "https://i.gtimg.cn/club/item/face/img/8/15918_100.gif");
        return ResultInfo.success(result);
    }

    /**
     * 登出
     *
     * @return
     */
    @LogInfo(value = LogEnums.LOG_EXIT, model = Constants.MODEL_YYZT)
    @PostMapping("logout.do")
    @ApiOperation("退出登录")
    public ResultInfo logout() {
        return userService.logout();
    }


    /**
     * @return
     */
    @RequestMapping("/alluser")
    public ResultInfo alluser(){
        return ResultInfo.success(JSONObject.toJSONString(userService.list(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getIsDel, 0).isNotNull(SysUser::getPhone))));
    }

}
