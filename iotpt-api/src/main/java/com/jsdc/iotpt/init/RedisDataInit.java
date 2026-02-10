package com.jsdc.iotpt.init;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.service.SysDictService;
import com.jsdc.iotpt.service.SysUserService;
import com.jsdc.iotpt.service.mqtt.DataSheetService;
import com.jsdc.iotpt.vo.ResultInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RedisDataInit {

    @Autowired
    private SysDictService sysDictService;
    @Autowired
    private SysUserService userService;
    @Autowired
    private DataSheetService dataSheetService;

    /**
     * @description 初始化数据
     */
    public void init() {
        dictInit();
        usersInit();
//        userInit();
    }

    /**
     * @description 初始化字典数据，缓存redis
     */
    public void dictInit() {
        List<SysDict> dicts = sysDictService.getAllDicts();
        Map<String, HashMap> dicData = new HashMap<>();
        dicts.forEach(x -> {
            if (dicData.containsKey(x.getDictType())) {
                HashMap hashMap = dicData.get(x.getDictType());
                hashMap.put(x.getDictValue(), x);
            } else {
                HashMap hashMap = new HashMap();
                hashMap.put(x.getDictValue(), x);
                dicData.put(x.getDictType(), hashMap);
            }
        });
        RedisUtils.setBeanValue("dictData", dicData);
    }

    /**
     * @description 更新字典缓存
     */
    public ResultInfo updateDictCache(SysDict sysDict) {
        try {
            //获取缓存数据
            Map<String, HashMap> dictData = (Map<String, HashMap>) RedisUtils.getBeanValue("dictData");
            if (dictData.containsKey(sysDict.getDictType())) {
                if (StringUtils.equals("1", sysDict.getIsDel() + "")) {
                    HashMap dictMap = dictData.get(sysDict.getDictType());
                    dictMap.remove(sysDict.getDictValue());
                } else {
                    HashMap dictMap = dictData.get(sysDict.getDictType());
                    dictMap.put(sysDict.getDictValue(), sysDict);
                }

            } else {
                HashMap hashMap = new HashMap();
                hashMap.put(sysDict.getDictValue(), sysDict);
                dictData.put(sysDict.getDictType(), hashMap);
            }
            RedisUtils.setBeanValue("dictData", dictData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultInfo.error("字典缓存更新失败！");
        }

        return ResultInfo.success();
    }

    /**
     * 储存所有用户信息
     */
    public Map<Integer, SysUser> usersInit() {
        List<SysUser> sysUsers = userService.list(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getIsDel, G.ISDEL_NO));
        Map<Integer, SysUser> map = new HashMap<>();
        sysUsers.forEach(x -> {
            map.put(x.getId(), x);
        });
        RedisUtils.setBeanValue("userDict", map);
        return map;
    }

    /**
     * @description 更新字典缓存
     */
    public ResultInfo updateUsersCache(SysUser sysUser) {
        try {
            //获取缓存数据
            Map<Integer, SysUser> dictData = (Map<Integer, SysUser>) RedisUtils.getBeanValue("userDict");
            if (null != dictData.get(sysUser.getId())) {
                if (G.ISDEL_YES.equals(sysUser.getIsDel())) {
                    dictData.remove(sysUser.getId());
                } else {
                    dictData.put(sysUser.getId(), sysUser);
                }
            } else {
                dictData.put(sysUser.getId(), sysUser);
            }
            RedisUtils.setBeanValue("userDict", dictData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultInfo.error("字典缓存更新失败！");
        }

        return ResultInfo.success();
    }

    /**
     * @description 用户信息缓存
     */
    public void userInit() {
        SysUser sysUser = userService.getUser();
        Map<Integer, SysUser> map = new HashMap<>();
        if (sysUser != null) {
            map.put(sysUser.getId(), sysUser);
        }
        RedisUtils.setBeanValue("userData", map);
    }


    /**
     * @description 用户信息缓存更新
     */
    public void updateUserCache(SysUser user) {
        Map<Integer, SysUser> userData = (Map<Integer, SysUser>) RedisUtils.getBeanValue("userData");
        if ("1".equals(user.getIsDel())) {
            userData.remove(user.getId());
        } else {
            userData.put(user.getId(), user);
        }
        RedisUtils.setBeanValue("userData", userData);
    }


}
