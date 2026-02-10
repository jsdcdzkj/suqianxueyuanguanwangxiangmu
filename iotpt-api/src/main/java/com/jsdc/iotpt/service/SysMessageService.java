package com.jsdc.iotpt.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.AppMessageConfigMapper;
import com.jsdc.iotpt.mapper.SysMessageMapper;
import com.jsdc.iotpt.model.SysMenu;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.sys.SysMessage;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.SysMessageVo;
import com.jsdc.iotpt.vo.operate.MissionVo;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ClassName: SysMessageService
 * Description:
 * date: 2024/11/21 8:52
 *
 * @author bn
 */
@Service
@Transactional
public class SysMessageService extends BaseService<SysMessage> {

    @Autowired
    private SysMessageMapper messageMapper;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private AppMessageConfigMapper appMessageConfigMapper;

    public JSONObject toIndex(){
        JSONObject result=new JSONObject();
        SysUser sysUser=sysUserService.getUser();
        // 所有消息
        List<SysMessage> messages=messageMapper.
                selectList(Wrappers.<SysMessage>lambdaQuery().
                        eq(SysMessage::getIsDel,0).
                        eq(!"admin".equals(sysUser.getLoginName()),SysMessage::getPushUser,sysUser.getId()).
                        orderByAsc(SysMessage::getIsRead).
                        orderByDesc(SysMessage::getCreateTime));

        // 所有未读消息的数量
        List<SysMessage> readMessage=messages.stream().
                filter(x->0==x.getIsRead()).collect(Collectors.toList());

        result.put("readMsgCount",readMessage.size());
        // 系统设置未读数量
        List<SysMessage> sysMessages=readMessage.stream().
                filter(x->"1".equals(x.getMsgType())).collect(Collectors.toList());

        result.put("sysMsgCount",sysMessages.size());

        Optional<SysMessage> firstMessage = messages.stream()
                .filter(x -> "1".equals(x.getMsgType()))
                .findFirst();

        if (firstMessage.isPresent()) {
            result.put("sysMsg",firstMessage.get());
        } else {
            result.put("sysMsg",null);
        }

        // itss未读数量
        List<SysMessage> itssMsgs=readMessage.stream().
                filter(x->"2".equals(x.getMsgType())).collect(Collectors.toList());

        result.put("itssMsgCount",itssMsgs.size());

        Optional<SysMessage> firstItss = messages.stream()
                .filter(x -> "2".equals(x.getMsgType()))
                .findFirst();

        if (firstItss.isPresent()) {
            result.put("itssMsg",firstItss.get());
        } else {
            result.put("itssMsg",null);
        }

        // 固定资产未读数量
        List<SysMessage> rfidMsgs=readMessage.stream().
                filter(x->"3".equals(x.getMsgType())).collect(Collectors.toList());

        result.put("rfidMsgCount",rfidMsgs.size());

        Optional<SysMessage> firstRfid = messages.stream()
                .filter(x -> "3".equals(x.getMsgType()))
                .findFirst();

        if (firstRfid.isPresent()) {
            result.put("rfidMsg",firstRfid.get());
        } else {
            result.put("rfidMsg",null);
        }

        // 办公未读数量
        List<SysMessage> offMsgs=readMessage.stream().
                filter(x->"4".equals(x.getMsgType())).collect(Collectors.toList());

        result.put("offMsgCount",offMsgs.size());

        Optional<SysMessage> firstOff = messages.stream()
                .filter(x -> "4".equals(x.getMsgType()))
                .findFirst();

        if (firstOff.isPresent()) {
            result.put("offMsg",firstOff.get());
        } else {
            result.put("offMsg",null);
        }

        // 用车未读数量
        List<SysMessage> carMsgs=readMessage.stream().
                filter(x->"5".equals(x.getMsgType())).collect(Collectors.toList());

        result.put("carMsgCount",carMsgs.size());

        Optional<SysMessage> firstCar = messages.stream()
                .filter(x -> "5".equals(x.getMsgType()))
                .findFirst();

        if (firstCar.isPresent()) {
            result.put("carMsg",firstCar.get());
        } else {
            result.put("carMsg",null);
        }

        return result;
    }


    public Page<SysMessage> appPageList(SysMessageVo bean) {
        Page page = new Page(bean.getPageIndex(), bean.getPageSize());
        QueryWrapper<SysMessage> queryWrapper=new QueryWrapper<>();

        SysUser sysUser=sysUserService.getUser();

        if (null!=bean.getIsRead()){
            queryWrapper.eq("isRead",bean.getIsRead());
        }

        if (StringUtils.isNotEmpty(bean.getMsgType())){
            queryWrapper.eq("msgType",bean.getMsgType());
        }

        queryWrapper.eq(!"admin".equals(sysUser.getLoginName()),"pushUser",sysUser.getId());

        queryWrapper.eq("isDel",0);

        queryWrapper.orderByAsc("isRead");
        queryWrapper.orderByDesc("createTime");

        Page<SysMessage> sysMessagePage=messageMapper.selectPage(page,queryWrapper);

        sysMessagePage.getRecords().forEach(x->{
            if (null!=x.getMenuId()){
                x.setAppMessageConfig(appMessageConfigMapper.selectById(x.getMenuId()));
            }
        });

        return sysMessagePage;
    }


    /**
     *  消息推送保存
     * @param messages
     */
    public void saveMessages(List<SysMessage> messages){
        messages.forEach(x->{
            x.setIsDel(0);
            x.setIsRead(0);
            messageMapper.insert(x);
        });
    }


    public ResultInfo editMsg(SysMessageVo bean) {
        bean.getMsgIds().stream().forEach(x->{
            messageMapper.update(null,Wrappers.<SysMessage>lambdaUpdate().
                    set(bean.getIsDel()!=null,SysMessage::getIsDel,bean.getIsDel()).
                    set(bean.getIsRead()!=null,SysMessage::getIsRead,bean.getIsRead()).
                    eq(SysMessage::getId,x));
        });
        return ResultInfo.success();
    }

    public ResultInfo selectMsg(SysMessageVo bean) {
        if (StringUtils.isEmpty(bean.getCid())||StringUtils.isEmpty(bean.getContext())){
            return ResultInfo.error("无效的值！");
        }
        List<SysUser> sysUsers=sysUserService.getBaseMapper().
                selectList(Wrappers.<SysUser>lambdaQuery().
                        eq(SysUser::getCId,bean.getCid()).
                        eq(SysUser::getIsDel,0));

        if (!sysUsers.isEmpty()){
            List<SysMessage> sysMessages=messageMapper.
                    selectList(Wrappers.<SysMessage>lambdaQuery().
                            eq(SysMessage::getPushUser,sysUsers.get(0).getId()).
                            eq(SysMessage::getContext,bean.getContext()));


            if (!sysMessages.isEmpty()){
                return ResultInfo.success(sysMessages.get(0));
            }
        }

        return ResultInfo.error("数据未匹配");
    }

    public ResultInfo allEditMsg(SysMessageVo bean) {

        messageMapper.update(null,Wrappers.<SysMessage>lambdaUpdate().
                set(bean.getIsDel()!=null,SysMessage::getIsDel,bean.getIsDel()).
                set(bean.getIsRead()!=null,SysMessage::getIsRead,bean.getIsRead()).
                eq(bean.getMsgType()!=null,SysMessage::getMsgType,bean.getMsgType()).
                eq(SysMessage::getPushUser,bean.getPushUser()));
        return ResultInfo.success();
    }
}
