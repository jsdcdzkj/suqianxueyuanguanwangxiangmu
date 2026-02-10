package com.jsdc.iotpt.service;


import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.*;
import com.jsdc.iotpt.model.*;
import com.jsdc.iotpt.model.sys.SysBuildArea;
import com.jsdc.iotpt.model.sys.SysBuildFloor;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.SysBuildAreaVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PeakGuPingTimeService extends BaseService<PeakGuPingTime> {

    @Autowired
    private SysUserService sysUserService ;

    @Autowired
    private PeakGuPingTimeMapper peakGuPingTimeMapper ;

    /**
    *峰谷配置新增
    * Author wzn
    * Date 2023/8/30 10:42
    */
    public void addPeakGuPingTime(PeakGuPingTime peakGuPingTime) {
        if(this.isExist(Integer.valueOf(peakGuPingTime.getStartTime()), Integer.valueOf(peakGuPingTime.getEndTime()))){
            throw new CustomException("时间范围冲突!请重新选择") ;
        }
        SysUser sysUser = sysUserService.getUser();
        peakGuPingTime.setCreateUser(sysUser.getId());
        peakGuPingTime.setCreateTime(new Date());
        peakGuPingTime.setIsDel(0);
        peakGuPingTime.insert();
    }

    /**
     * 峰谷配置修改
     * Author wzn
     * Date 2023/5/9 9:06
     */
    public void updatePeakGuPingTime(PeakGuPingTime peakGuPingTime) {
        if(this.isExist(Integer.valueOf(peakGuPingTime.getStartTime()), Integer.valueOf(peakGuPingTime.getEndTime()))){
            throw new CustomException("时间范围冲突!请重新选择") ;
        }
        SysUser sysUser = sysUserService.getUser();
        peakGuPingTime.setUpdateUser(sysUser.getId());
        peakGuPingTime.setUpdateTime(new Date());
        peakGuPingTime.updateById();
    }


    /**
     * 区域列表
     * Author wzn
     * Date 2023/5/9 9:09
     */
    public List<PeakGuPingTime> getList(PeakGuPingTime peakGuPingTime) {
        QueryWrapper<PeakGuPingTime> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        queryWrapper.eq("type", peakGuPingTime.getType());
        queryWrapper.orderByAsc("startTime");
        List<PeakGuPingTime> peakGuPingTimes = peakGuPingTimeMapper.selectList(queryWrapper);
        return peakGuPingTimes;
    }



    /**
     * 区域详情
     * Author wzn
     * Date 2023/5/9 9:10
     */
    public PeakGuPingTime info(String id) {
        return peakGuPingTimeMapper.selectById(id);
    }


    /**
    *该时间到是否已存在
    * Author wzn
    * Date 2023/8/30 11:48
    */
    public boolean isExist(Integer startTime,Integer endTime){
        boolean isexist = false ;
        QueryWrapper<PeakGuPingTime> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        List<PeakGuPingTime> peakGuPingTimes = peakGuPingTimeMapper.selectList(queryWrapper);
        for(PeakGuPingTime p:peakGuPingTimes){
            if((startTime < Integer.valueOf(p.getEndTime())) & (endTime > Integer.valueOf(p.getStartTime()))){
                isexist = true ;
            }
        }
        return isexist ;
    }



}
