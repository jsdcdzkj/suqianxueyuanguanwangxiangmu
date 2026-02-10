package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.operate.*;
import com.jsdc.iotpt.vo.operate.MissionVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class MissionAssignUserService extends BaseService<MissionAssignUser> {


    @Autowired
    private SysUserService userService;
    @Autowired
    private TeamGroupUserService groupUserService;
    @Autowired
    private MissionAssignUserService assignUserService;


    //根据当前用户查询到是否是班长，班长可以看到自己所在班组下开启的任务。
    //组员可以看到班长指定的任务
    public MissionVo selectGroupId(Integer userId) {
        MissionVo vo = new MissionVo();
        if (null == userId) {
            SysUser user = userService.getUser();
            userId = user.getId();
        }
        List<Integer> groups = new ArrayList<>();
        Integer grs = 0;
        List<TeamGroupUser> list = groupUserService.list(Wrappers.<TeamGroupUser>lambdaQuery()
                .eq(TeamGroupUser::getIsDel, G.ISDEL_NO).eq(TeamGroupUser::getUserId, userId));
        if (!CollectionUtils.isEmpty(list)) {
            for (TeamGroupUser a : list) {
                if (StringUtils.equals("1", a.getTeamRole())) {
                    groups.add(a.getGroupId());
                    grs = 1;
                }
            }
        }
        if (grs == 0) {
            vo.setUserId(userId);
            List<MissionAssignUser> ls = assignUserService.list(new LambdaQueryWrapper<MissionAssignUser>().eq(MissionAssignUser::getUserId, userId));
            List<Integer> collect = ls.stream().map(MissionAssignUser::getGroupId).collect(Collectors.toList());
            groups.addAll(collect);
        }
        vo.setTeamGroupIds(groups);
        return vo;
    }

}
