package com.jsdc.iotpt.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.init.RedisDataInit;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.service.SysUserService;
import com.jsdc.iotpt.util.AliSmsUtil;
import com.jsdc.iotpt.util.MD5Utils;
import com.jsdc.iotpt.util.StrongPasswordValidatorUtils;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/app/resetPassword")
@Slf4j
@Api(tags = "重置密码")
public class ResetPasswordController {
    @Autowired
    private SysUserService userService;

    @Autowired
    private RedisDataInit redisDataInit;

    /**
     * 发送验证码
     * @param loginName
     * @param phone
     * @return
     */
    @PostMapping("sendVerificationCode")
    @ApiOperation(value = "发送验证码", notes = "传递参数：loginName:登录名(必填)，phone：手机号(必填)")
    public ResultInfo sendVerificationCode(String loginName, String phone) {
        SysUser sysUser = userService.getBaseMapper().selectOne(Wrappers.<SysUser>lambdaQuery()
                .and(i->i.eq(SysUser::getLoginName,loginName).or().eq(SysUser::getRealName,loginName))
                .eq(SysUser::getPhone, phone)
                .eq(SysUser::getIsDel, 0)
                .eq(SysUser::getIsEnable, 1)
        );
        if (null == sysUser) {
            return ResultInfo.error("用户不存在");
        }
        //发送验证码
        try{
            String verificationCode = generateVerificationCode();
            //发送验证码 某个手机号
            String s = AliSmsUtil.smsMethod(verificationCode, phone);
            if (s.equals("-1")){
                return ResultInfo.error("发送失败");
            }
            boolean b = RedisUtils.existsKey(phone);
            if (b){
                RedisUtils.deleteKey(phone);
            }
            //将验证码存入redis 5分钟过期
            RedisUtils.setBeanValue(phone, verificationCode, 500);
        } catch (Exception e){
            e.printStackTrace();
            return ResultInfo.error("验证码发送失败");
        }
        return ResultInfo.success("验证码发送成功");
    }

    // 生成6位随机数字验证码 重置密码
    private static String generateVerificationCode() {
        // 定义验证码的长度
        int codeLength = 6;
        // 随机数生成器
        Random random = new Random();
        // 用StringBuilder存储生成的验证码
        StringBuilder codeBuilder = new StringBuilder();
        // 生成指定长度的随机数字
        for (int i = 0; i < codeLength; i++) {
            int randomDigit = random.nextInt(10); // 生成0到9的随机数字
            codeBuilder.append(randomDigit);
        }
        System.out.println(codeBuilder.toString());
        return codeBuilder.toString();
    }

    /**
     * 验证验证码
     * @param loginName
     * @param phone
     * @param verificationCode
     * @return
     */
    @PostMapping("verifyVerificationCode")
    @ApiOperation(value = "验证验证码", notes = "传递参数：loginName:登录名(必填)，phone：手机号(必填)，verificationCode：验证码(必填)")
    public ResultInfo verifyVerificationCode(String loginName, String phone,String verificationCode) {
        //判断是否存在是否过期
        boolean b = RedisUtils.existsKey(phone);
        if (b){
            if (RedisUtils.getBeanValue(phone).equals(verificationCode)){
                Map<String, String> map = new HashMap<>();
                map.put("loginName", loginName);
                map.put("phone", phone);
                return ResultInfo.success("验证通过",map);
            }else {
                return ResultInfo.error("验证码错误");
            }
        }else {
            return ResultInfo.error("验证码已过期");
        }
    }

    @PostMapping("resetPassword")
    @ApiOperation(value = "重置密码", notes = "传递参数：passWord:密码(必填) loginName:登录名(必填)，phone：手机号(必填)")
    public ResultInfo resetPassword(String passWord, String loginName,String phone) {
        SysUser sysUser = userService.getBaseMapper().selectOne(Wrappers.<SysUser>lambdaQuery()
                .and(i->i.eq(SysUser::getLoginName,loginName).or().eq(SysUser::getRealName,loginName))
                .eq(SysUser::getPhone, phone)
                .eq(SysUser::getIsDel, 0)
                .eq(SysUser::getIsEnable, 1)
        );
        // 判断密码强度
        Map<String, String> map = StrongPasswordValidatorUtils.isValidPassword(passWord);
        if (map.get("code").equals("-1")) {
            return ResultInfo.error(map.get("msg"));
        }else  {
            sysUser.setPassword(MD5Utils.getMD5(G.PLATFORM + passWord));
            userService.getBaseMapper().updateById(sysUser);
            redisDataInit.updateUsersCache(sysUser);
            sysUser.setOldPassWord(passWord);
            return ResultInfo.success("密码重置成功");
        }
    }


}
