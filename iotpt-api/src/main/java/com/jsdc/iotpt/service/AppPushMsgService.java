package com.jsdc.iotpt.service;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.iotpt.config.GetTuiConfig;
import com.jsdc.iotpt.mapper.DcycParkingNoticeMapper;
import com.jsdc.iotpt.mapper.SysUserMapper;
import com.jsdc.iotpt.model.AppMessageConfig;
import com.jsdc.iotpt.model.DcycParkingNotice;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.vo.AppPushMsgVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class AppPushMsgService {

    @Autowired
    private GetTuiConfig getTuiConfig;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private AppMessageConfigService appMessageConfigService;

    @Autowired
    private DcycParkingNoticeMapper dcycParkingNoticeMapper;

    /**
     * 通用 App推送消息
     *
     * @param data
     */
    public void pushMsg(AppPushMsgVo data) {
        // 异步编程 防止堵塞主线程
        // runAsync 无需等待返回值
        CompletableFuture.runAsync(() -> {
            try {
                AppMessageConfig appMessageConfig = appMessageConfigService.queryByCode(data.getAppConfigCode());
                if (appMessageConfig == null) {
                    log.error("未查询到App消息推送配置，无法推送消息。");
                    return;
                }
                String result = getTuiConfig.getListCid(data.getUserIdList(), data.getCustomParameters(), appMessageConfig.getId(), appMessageConfig.getType(), appMessageConfig.getTitle(), replaceTemplate(appMessageConfig.getContent(), data));
                log.error("调用消息推送完毕，result={}", result);
            } catch (Exception e) {
                log.error("推送消息失败", e);
            }
        });
    }

    /**
     * itss 工单响应推送
     *
     * @param jsonBean
     */
    public void itssPush(JSONObject jsonBean, String appConfigCode) {
        String username = (String) jsonBean.get("username");
        List<Integer> userIds = new ArrayList<>();
        for (String name : username.split(",")) {
            List<SysUser> users = userMapper.selectList(new LambdaQueryWrapper<SysUser>().eq(SysUser::getIsDel, 0).eq(SysUser::getRealName, name));
            if (CollUtil.isNotEmpty(users)) {
                userIds.add(users.get(0).getId());
            }
        }
        AppPushMsgVo msgVo = new AppPushMsgVo();
        msgVo.setUserIdList(userIds);
        msgVo.setAppConfigCode(appConfigCode);
        String title = (String) jsonBean.get("title");
        msgVo.setTitle(title);
        pushMsg(msgVo);
    }

    /**
     * 用车管理 推送
     *
     * @param data
     */
    public void dcycPush(AppPushMsgVo data) {

//        log.debug("调用用车管理推送消息，data={}", data);
//        log.info("调用用车管理推送消息，data={}", data);
//        log.warn("调用用车管理推送消息，data={}", data);
//        log.error("调用用车管理推送消息，data={}", data);
//        System.out.println("调用用车管理推送消息，data={}" + JSONObject.toJSONString(data));

        SysUser one = sysUserService.getOne(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getPhone, data.getSending_phone())
                .eq(SysUser::getIsDel, 0)
        );
        if (one == null) {
            log.error("手机号不存在，无法推送消息。");
            return;
        }
        if (Objects.equals("CAR_PARKING_NOTICE", data.getAppConfigCode())) {
            DcycParkingNotice notice = JSON.parseObject(data.getCustomParameters(), DcycParkingNotice.class);
            dcycParkingNoticeMapper.insert(notice);
            Map<String, Integer> map = new HashMap<>();
            map.put("id", notice.getId());
            data.setCustomParameters(JSON.toJSONString(map));
        }
        data.setUserIdList(Collections.singletonList(one.getId()));
        pushMsg(data);
    }

    /**
     * 资产管理 推送
     *
     * @param data
     */
    public void dcycRfid(AppPushMsgVo data) {

        log.debug("调用资产管理推送消息，data={}", data);
        log.info("调用资产管理推送消息，data={}", data);
        log.warn("调用资产管理推送消息，data={}", data);
        log.error("调用资产管理推送消息，data={}", data);
        System.out.println("调用资产管理推送消息，data={}" + JSONObject.toJSONString(data));

        if (data.getSending_phone() == null || "".equals(data.getSending_phone())) {
            log.error("手机号不存在，无法推送消息。");
            return;
        }
        List<String> phones = new ArrayList<>();
        if (data.getSending_phone().contains(",")) {
            phones = new ArrayList<>(Arrays.asList(data.getSending_phone().split(",")));
        } else {
            phones.add(data.getSending_phone());
        }

        List<SysUser> users = sysUserService.getBaseMapper().selectList(Wrappers.<SysUser>lambdaQuery()
                .in(SysUser::getPhone, phones)
                .eq(SysUser::getIsDel, 0)
        );
        if (CollUtil.isEmpty(users)) {
            log.error("手机号不存在，无法推送消息。");
            return;
        }
        data.setUserIdList(users.stream().map(SysUser::getId).collect(Collectors.toList()));
        pushMsg(data);
    }


    // 根据模板和 VO 实例生成替换后的字符串
    public static String replaceTemplate(String template, Object vo) {
        Pattern pattern = Pattern.compile("\\$\\{(\\w+)}");
        Matcher matcher = pattern.matcher(template);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String fieldName = matcher.group(1);
            try {
                Field field = vo.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(vo);
                matcher.appendReplacement(result, value != null ? value.toString() : "");
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
                matcher.appendReplacement(result, "");  // 字段不存在或无法访问时，用空字符串替换
            }
        }
        matcher.appendTail(result);
        return result.toString().replaceAll("<p>|</p>", "");
    }


    public String test(AppPushMsgVo data) {
        AppMessageConfig appMessageConfig = new AppMessageConfig();
        appMessageConfig.setContent("【用车管理】您有一条${title}待审批流程，申请：${name}");
        System.out.println(replaceTemplate(appMessageConfig.getContent(), data));
        System.out.println(replaceTemplate("您好，您的访客已经顺利到达鼎驰⼤厦，请知悉。", data));
        System.out.println(replaceTemplate("【⽤⻋管理】您有⼀条${title}审批回退待办理，回退理由：${reasons}", data));
        return "test";
    }


    /**
     * 工时系统 任务推送
     */
    public void taskPush(AppPushMsgVo data) {
        SysUser user = sysUserService.getOne(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getPhone, data.getSending_phone())
                .eq(SysUser::getIsDel, 0)
        );
        if (user == null) {
            log.error("手机号不存在，无法推送工时消息。" + data.getSending_phone());
            return;
        }
        data.setUserIdList(Collections.singletonList(user.getId()));
        pushMsg(data);
    }
}
