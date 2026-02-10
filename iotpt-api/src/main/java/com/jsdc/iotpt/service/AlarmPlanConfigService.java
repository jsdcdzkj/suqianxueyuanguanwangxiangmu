package com.jsdc.iotpt.service;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.mapper.AlarmContentConfigMapper;
import com.jsdc.iotpt.mapper.AlarmPlanConfigMapper;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.new_alarm.AlarmContentConfig;
import com.jsdc.iotpt.model.new_alarm.AlarmPlanConfig;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.AlarmPlanConfigVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class AlarmPlanConfigService {

    @Autowired
    private AlarmPlanConfigMapper alarmPlanConfigMapper;

    @Autowired
    private AlarmContentConfigMapper alarmContentConfigMapper;

    @Autowired
    private SysUserService userService;

    public Page<AlarmPlanConfig> getPageList(AlarmPlanConfigVO vo) {
        if (StrUtil.isNotBlank(vo.getKeyword())) {
            List<AlarmContentConfig> contentConfigs = alarmContentConfigMapper.selectList(new LambdaQueryWrapper<AlarmContentConfig>()
                    .eq(AlarmContentConfig::getIsDel, 0)
                    .and(i -> i.like(AlarmContentConfig::getAlarmContent, vo.getKeyword()).or().like(AlarmContentConfig::getDeviceTypeName, vo.getKeyword()))
            );
            if (CollUtil.isEmpty(contentConfigs)) {
                return new Page<>(vo.getPageNo(), vo.getPageSize());
            }
            List<Integer> ids = contentConfigs.stream().map(AlarmContentConfig::getId).collect(Collectors.toList());
            vo.setContentIds(ids);
        }
        Page<AlarmPlanConfig> page = new Page<>(vo.getPageNo(), vo.getPageSize());
        Page<AlarmPlanConfig> pageList = alarmPlanConfigMapper.getPage(page, vo);
        List<AlarmPlanConfig> records = pageList.getRecords();
        if (CollUtil.isNotEmpty(records)) {
            List<AlarmContentConfig> contentConfigs = alarmContentConfigMapper.selectList(new LambdaQueryWrapper<AlarmContentConfig>().eq(AlarmContentConfig::getIsDel, 0));
            Map<Integer, String> contentMap = contentConfigs.stream().collect(Collectors.toMap(AlarmContentConfig::getId, AlarmContentConfig::getAlarmContent));
            for (AlarmPlanConfig record : records) {
                String[] split = record.getAlarmContent().split(",");
                List<String> contentList = new ArrayList<>();
                for (String s : split) {
                    contentList.add(contentMap.get(Integer.parseInt(s)));
                }
                record.setAlarmContentStr(StrUtil.join("、", contentList));
                record.setAlarmContentList(Arrays.asList(record.getAlarmContent().split(",")));
                if (StrUtil.isNotBlank(record.getLinkageRule())) {
                    record.setLinkageRuleList(Arrays.asList(record.getLinkageRule().split(",")));
                }
                if (StrUtil.isNotBlank(record.getPushConfig())) {
                    record.setPushConfigList(Arrays.asList(record.getPushConfig().split(",")));
                }
                if (StrUtil.isNotBlank(record.getPushUser())) {
                    record.setPushUserList(Arrays.asList(record.getPushUser().split(",")));
                }
            }
        }
        return pageList;
    }

    public AlarmPlanConfig getById(Integer id) {
        AlarmPlanConfig record = alarmPlanConfigMapper.selectById(id);
        List<AlarmContentConfig> contentConfigs = alarmContentConfigMapper.selectList(new LambdaQueryWrapper<AlarmContentConfig>().eq(AlarmContentConfig::getIsDel, 0));
        Map<Integer, String> contentMap = contentConfigs.stream().collect(Collectors.toMap(AlarmContentConfig::getId, AlarmContentConfig::getAlarmContent));
        String[] split = record.getAlarmContent().split(",");
        List<String> contentList = new ArrayList<>();
        for (String s : split) {
            contentList.add(contentMap.get(Integer.parseInt(s)));
        }
        record.setAlarmContentStr(StrUtil.join("、", contentList));
        record.setAlarmContentList(Arrays.asList(record.getAlarmContent().split(",")));
        if (StrUtil.isNotBlank(record.getLinkageRule())) {
            record.setLinkageRuleList(Arrays.asList(record.getLinkageRule().split(",")));
        }
        if (StrUtil.isNotBlank(record.getPushConfig())) {
            record.setPushConfigList(Arrays.asList(record.getPushConfig().split(",")));
        }
        if (StrUtil.isNotBlank(record.getPushUser())) {
            record.setPushUserList(Arrays.asList(record.getPushUser().split(",")));
        }
        return record;
    }

    public void saveOrUp(AlarmPlanConfig entity) {
        SysUser loginUser = userService.getUser();
        Date now = new Date();

        entity.setUpdateUser(loginUser.getId());
        entity.setUpdateTime(now);

        if (CollUtil.isEmpty(entity.getAlarmContentList())) {
            throw new CustomException("缺少必要参数");
        }
        for (String contentId : entity.getAlarmContentList()) {
            AlarmPlanConfig plan = alarmPlanConfigMapper.selectByContentId(contentId);
            if (Objects.nonNull(plan) && !Objects.equals(entity.getId(), plan.getId())) {
                AlarmContentConfig config = alarmContentConfigMapper.selectById(contentId);
                throw new CustomException("报警内容【" + config.getAlarmContent() + "】已存在其他预案中");
            }
        }


        entity.setAlarmContent(StrUtil.join(",", entity.getAlarmContentList()));

        if (CollUtil.isEmpty(entity.getLinkageRuleList())) {
            entity.setLinkageRule("");
        }else {
            entity.setLinkageRule(StrUtil.join(",", entity.getLinkageRuleList()));
        }
        if (CollUtil.isEmpty(entity.getPushConfigList())) {
            entity.setPushConfig("");
        }else {
            entity.setPushConfig(StrUtil.join(",", entity.getPushConfigList()));
        }

        if (CollUtil.isNotEmpty(entity.getPushUserList())) {
            entity.setPushUser(StrUtil.join(",", entity.getPushUserList()));
        }else {
            entity.setPushUser("");
        }

        entity.setIsDel(0);

        if (Objects.isNull(entity.getId())) {
            entity.setEnable(1);
            entity.setCreateUserName(loginUser.getRealName());
            entity.setCreateUser(loginUser.getId());
            entity.setCreateTime(now);
            alarmPlanConfigMapper.insert(entity);
        }else {
            alarmPlanConfigMapper.updateById(entity);
        }
    }

    public void batchDel(List<Integer> ids) {
        SysUser loginUser = userService.getUser();
        Date now = new Date();

        for (Integer id : ids) {
            AlarmPlanConfig entity = new AlarmPlanConfig();
            entity.setId(id);
            entity.setIsDel(1);
            entity.setUpdateUser(loginUser.getId());
            entity.setUpdateTime(now);
            alarmPlanConfigMapper.updateById(entity);
        }
    }

    public void switchEnable(AlarmPlanConfig entity) {
        AlarmPlanConfig bean = new AlarmPlanConfig();
        bean.setId(entity.getId());
        bean.setEnable(entity.getEnable());
        alarmPlanConfigMapper.updateById(bean);
    }
}
