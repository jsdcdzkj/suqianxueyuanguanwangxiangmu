package com.jsdc.iotpt.service;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.mapper.AlarmGroupMapper;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.new_alarm.AlarmCategory;
import com.jsdc.iotpt.model.new_alarm.AlarmGroup;
import com.jsdc.iotpt.vo.AlarmCategoryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class AlarmGroupService {

    @Autowired
    private AlarmGroupMapper alarmGroupMapper;

    @Autowired
    private SysUserService userService;

    public Page<AlarmGroup> getPage(AlarmCategoryVO vo) {
        Page<AlarmGroup> page = new Page<>(vo.getPageNo(), vo.getPageSize());
        return alarmGroupMapper.selectPage(page, new LambdaQueryWrapper<AlarmGroup>()
                .eq(AlarmGroup::getIsDel, 0)
                .like(StrUtil.isNotBlank(vo.getName()), AlarmGroup::getName, vo.getName())
                .orderByDesc(AlarmGroup::getId)
        );
    }

    public List<AlarmGroup> getList() {
        return alarmGroupMapper.selectList(new LambdaQueryWrapper<AlarmGroup>().eq(AlarmGroup::getIsDel, 0));
    }

    public void save(AlarmGroup entity) {
        SysUser loginUser = userService.getUser();
        Date now = new Date();
        entity.setUpdateTime(now);
        entity.setUpdateUser(loginUser.getId());
        if (Objects.isNull(entity.getId())) {
            entity.setIsDel(0);
            entity.setCreateUser(loginUser.getId());
            entity.setCreateTime(now);
            alarmGroupMapper.insert(entity);
        }else {
            alarmGroupMapper.updateById(entity);
        }
    }
}
