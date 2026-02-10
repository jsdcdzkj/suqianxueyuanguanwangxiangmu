package com.jsdc.iotpt.service;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.mapper.AlarmPlanConfigMapper;
import com.jsdc.iotpt.mapper.AlarmPlanTimeConfigMapper;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.new_alarm.AlarmPlanConfig;
import com.jsdc.iotpt.model.new_alarm.AlarmPlanTimeConfig;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.AlarmPlanTimeConfigVO;
import com.jsdc.iotpt.vo.AlarmPlanTimeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
public class AlarmPlanTimeConfigService {


    @Autowired
    private AlarmPlanTimeConfigMapper alarmPlanTimeConfigMapper;

    @Autowired
    private AlarmPlanConfigMapper alarmPlanConfigMapper;

    @Autowired
    private SysUserService userService;

    public Page<AlarmPlanTimeConfig> getPageList(AlarmPlanTimeConfigVO vo) {
        Page<AlarmPlanTimeConfig> page = new Page<>(vo.getPageNo(), vo.getPageSize());
        return alarmPlanTimeConfigMapper.selectPage(page, new LambdaQueryWrapper<AlarmPlanTimeConfig>()
                .eq(AlarmPlanTimeConfig::getIsDel, 0)
        );
    }

    public AlarmPlanTimeConfig getById(Integer id) {
        AlarmPlanTimeConfig config = alarmPlanTimeConfigMapper.selectById(id);
        if (StrUtil.isNotBlank(config.getMonday())) {
            config.setMondayList(JSON.parseArray(config.getMonday(), AlarmPlanTimeVO.class).stream().sorted(Comparator.comparing(AlarmPlanTimeVO::getSort)).collect(Collectors.toList()));
        }
        if (StrUtil.isNotBlank(config.getTuesday())) {
            config.setTuesdayList(JSON.parseArray(config.getTuesday(), AlarmPlanTimeVO.class).stream().sorted(Comparator.comparing(AlarmPlanTimeVO::getSort)).collect(Collectors.toList()));
        }
        if (StrUtil.isNotBlank(config.getWednesday())) {
            config.setWednesdayList(JSON.parseArray(config.getWednesday(), AlarmPlanTimeVO.class).stream().sorted(Comparator.comparing(AlarmPlanTimeVO::getSort)).collect(Collectors.toList()));
        }
        if (StrUtil.isNotBlank(config.getThursday())) {
            config.setThursdayList(JSON.parseArray(config.getThursday(), AlarmPlanTimeVO.class).stream().sorted(Comparator.comparing(AlarmPlanTimeVO::getSort)).collect(Collectors.toList()));
        }
        if (StrUtil.isNotBlank(config.getFriday())) {
            config.setFridayList(JSON.parseArray(config.getFriday(), AlarmPlanTimeVO.class).stream().sorted(Comparator.comparing(AlarmPlanTimeVO::getSort)).collect(Collectors.toList()));
        }
        if (StrUtil.isNotBlank(config.getSaturday())) {
            config.setSaturdayList(JSON.parseArray(config.getSaturday(), AlarmPlanTimeVO.class).stream().sorted(Comparator.comparing(AlarmPlanTimeVO::getSort)).collect(Collectors.toList()));
        }
        if (StrUtil.isNotBlank(config.getSunday())) {
            config.setSundayList(JSON.parseArray(config.getSunday(), AlarmPlanTimeVO.class).stream().sorted(Comparator.comparing(AlarmPlanTimeVO::getSort)).collect(Collectors.toList()));
        }
        return config;
    }

    public void saveOrUp(AlarmPlanTimeConfig entity) {
        SysUser loginUser = userService.getUser();
        Date now = new Date();
        entity.setUpdateTime(now);
        entity.setUpdateUser(loginUser.getId());

        if (CollUtil.isEmpty(entity.getMondayList()) && CollUtil.isEmpty(entity.getTuesdayList()) && CollUtil.isEmpty(entity.getWednesdayList())
                && CollUtil.isEmpty(entity.getThursdayList()) && CollUtil.isEmpty(entity.getFridayList()) && CollUtil.isEmpty(entity.getSaturdayList()) && CollUtil.isEmpty(entity.getSundayList())) {
            throw new CustomException("请设置时间 ");
        }

        entity.setMonday(listToJson(entity.getMondayList()));
        entity.setTuesday(listToJson(entity.getTuesdayList()));
        entity.setWednesday(listToJson(entity.getWednesdayList()));
        entity.setThursday(listToJson(entity.getThursdayList()));
        entity.setFriday(listToJson(entity.getFridayList()));
        entity.setSaturday(listToJson(entity.getSaturdayList()));
        entity.setSunday(listToJson(entity.getSundayList()));

        entity.setIsDel(0);

        if (Objects.isNull(entity.getId())) {
            entity.setCreateTime(now);
            entity.setCreateUser(loginUser.getId());
            alarmPlanTimeConfigMapper.insert(entity);
        }else {
            alarmPlanTimeConfigMapper.updateById(entity);
        }
    }

    private String listToJson(List<AlarmPlanTimeVO> list) {
        if (CollUtil.isEmpty(list)) {
            return "";
        }else {
            return JSON.toJSONString(list);
        }
    }

    public void delete(AlarmPlanTimeConfig entity) {
        if (Objects.isNull(entity.getId())) {
            throw new CustomException("缺少参数ID");
        }
        List<AlarmPlanConfig> planConfigList = alarmPlanConfigMapper.selectList(new LambdaQueryWrapper<AlarmPlanConfig>()
                .eq(AlarmPlanConfig::getIsDel, 0)
                .eq(AlarmPlanConfig::getPlanTimeConfigId, entity.getId())
        );
        if (CollUtil.isNotEmpty(planConfigList)) {
            throw new CustomException("该时间计划模板正在使用中，不可删除");
        }
        SysUser loginUser = userService.getUser();
        Date now = new Date();
        entity.setUpdateTime(now);
        entity.setUpdateUser(loginUser.getId());
        entity.setIsDel(1);
        alarmPlanTimeConfigMapper.updateById(entity);
    }
}
