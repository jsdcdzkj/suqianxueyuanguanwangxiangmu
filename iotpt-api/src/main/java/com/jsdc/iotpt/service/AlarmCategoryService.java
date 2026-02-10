package com.jsdc.iotpt.service;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.mapper.AlarmCategoryMapper;
import com.jsdc.iotpt.mapper.AlarmGroupMapper;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.new_alarm.AlarmCategory;
import com.jsdc.iotpt.model.new_alarm.AlarmGroup;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.AlarmCategoryVO;
import com.jsdc.iotpt.vo.AlarmStatisticsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AlarmCategoryService {

    @Autowired
    private AlarmCategoryMapper alarmCategoryMapper;

    @Autowired
    private AlarmGroupMapper alarmGroupMapper;

    @Autowired
    private SysUserService userService;

    public Page<AlarmCategory> getPage(AlarmCategoryVO vo) {
        Page<AlarmCategory> page = new Page<>(vo.getPageNo(), vo.getPageSize());
        List<AlarmGroup> alarmGroups = alarmGroupMapper.selectList(new LambdaQueryWrapper<AlarmGroup>().eq(AlarmGroup::getIsDel, 0));
        Map<Integer, String> groupMap = alarmGroups.stream().collect(Collectors.toMap(AlarmGroup::getId, AlarmGroup::getName));
        Page<AlarmCategory> pageList = alarmCategoryMapper.selectPage(page, new LambdaQueryWrapper<AlarmCategory>()
                .eq(AlarmCategory::getIsDel, 0)
                .like(StrUtil.isNotBlank(vo.getName()), AlarmCategory::getName, vo.getName())
                .eq(Objects.nonNull(vo.getAlarmGroup()), AlarmCategory::getAlarmGroup, vo.getAlarmGroup())
                .orderByDesc(AlarmCategory::getId)
        );
        List<AlarmCategory> records = pageList.getRecords();
        if (CollUtil.isNotEmpty(records)) {
            Map<String, HashMap<String, SysDict>> map = (Map<String, HashMap<String, SysDict>>) RedisUtils.getBeanValue("dictData");
            HashMap<String, SysDict> alarmType = map.get("alarmType");
            for (AlarmCategory record : records) {
                record.setAlarmGroupName(groupMap.get(record.getAlarmGroup()));
                record.setAlarmTypeName(alarmType.get(String.valueOf(record.getAlarmType())).getDictLabel());
            }
        }
        return pageList;
    }

    public List<AlarmCategory> getList(AlarmStatisticsVo vo) {
        return alarmCategoryMapper.selectList(new LambdaQueryWrapper<AlarmCategory>().eq(AlarmCategory::getIsDel, 0)
                .eq(StrUtil.isNotBlank(vo.getAlarmType()), AlarmCategory::getAlarmType, vo.getAlarmType()));
    }

    public void save(AlarmCategory entity) {
        SysUser loginUser = userService.getUser();
        Date now = new Date();
        entity.setUpdateTime(now);
        entity.setUpdateUser(loginUser.getId());
        if (Objects.isNull(entity.getId())) {
            entity.setIsDel(0);
            entity.setCreateUser(loginUser.getId());
            entity.setCreateTime(now);
            alarmCategoryMapper.insert(entity);
        }else {
            alarmCategoryMapper.updateById(entity);
        }
    }
}
