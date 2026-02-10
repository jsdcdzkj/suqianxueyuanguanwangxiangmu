package com.jsdc.iotpt.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.SysMsgSettingMapper;
import com.jsdc.iotpt.model.SysFile;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.sys.SysMessage;
import com.jsdc.iotpt.model.sys.SysMsgSetting;
import com.jsdc.iotpt.vo.ResultInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: SysMsgSettingService
 * Description:
 * date: 2024/11/26 9:08
 *
 * @author bn
 */
@Slf4j
@Service
public class SysMsgSettingService extends BaseService<SysMsgSetting> {


    @Autowired
    private SysMsgSettingMapper msgSettingMapper;

    @Autowired
    private SysUserService sysUserService;

    public SysMsgSetting getSysMsgSet(){

        SysUser sysUser=sysUserService.getUser();

        log.info("====sysUser==="+ JSON.toJSONString(sysUser));
        List<SysMsgSetting> sysMsgSettings=msgSettingMapper.
                selectList(Wrappers.<SysMsgSetting>lambdaQuery().
                        eq(SysMsgSetting::getUserId,sysUser.getId()).
                        orderByAsc(SysMsgSetting::getId));

        log.info("====sysMsgSetting==="+ JSON.toJSONString(sysUser));
        if (sysMsgSettings.isEmpty()){
            SysMsgSetting sysMsgSetting=new SysMsgSetting();
            // 系统设置
            sysMsgSetting.setIsSys(1);
            sysMsgSetting.setUserId(sysUser.getId());
            // 是否接收消息
            sysMsgSetting.setIsMsg(1);
            // 协同办公
            sysMsgSetting.setIsOff(1);
            // itss
            sysMsgSetting.setIsItss(1);
            // 用车
            sysMsgSetting.setIsCar(1);
            // 固定资产
            sysMsgSetting.setIsRfid(1);
            msgSettingMapper.insert(sysMsgSetting);

            return sysMsgSetting;
        }


        return sysMsgSettings.get(0);
    }


    public ResultInfo editSysMsgSet(SysMsgSetting msgSetting){

        msgSettingMapper.updateById(msgSetting);

        return ResultInfo.success();
    }



}
