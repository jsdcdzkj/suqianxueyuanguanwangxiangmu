package com.jsdc.iotpt.vo.operate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jsdc.iotpt.model.operate.Mission;
import com.jsdc.iotpt.model.operate.MissionItemRecord;
import lombok.Data;

import java.util.Date;
import java.util.List;


/**
 * 任务组
 */
@Data
public class MissionVo extends Mission {
    private Integer temporary;
    //1、获取本周已指派 2、获取本月已指派 3、历史已指派 4、待指派
    private Integer searchType;
    // 1.获取本周已挂起 2.本月已挂起 3.历史已经挂起
    private Integer pendType;
    //关注类型 1：我的任务关注  2：待指派关注
    private Integer careType;
    private Integer pageIndex;
    private Integer pageSize;
    //关键词查询
    private String keyword;
    private String createUserName;
    private String rowNum;
    //开始时间
    private String startTime;
    //结束时间
    private String endTime;
    //开始时间
    private String startTime1;
    //结束时间
    private String endTime1;
    //开始时间
    private String startTimeStatistics;
    //结束时间
    private String endTimeStatistics;
    //开始时间
    private String startTimeStatistics1;
    //结束时间
    private String endTimeStatistics1;
    //用户名称
    private String userName;
    //状态名称
    private String statesName;
    //级别
    private String levelsName;
    //级别颜色
    private String levelsColour;
    //来源名称
    private String sourceName;
    //任务类型
    private String taskTypeName;

    private String taskType;


    //紧急程度
    private Integer urgency;
    //紧急程度名称
    private String urgencyName;
    //紧急程度颜色
    private String urgencyColour;
    //班组名称
    private String teamGroupsName;
    //任务类型id
    private Integer assignTaskType;
    //任务类型类别
    private String assignTask;
    //班组id
    private Integer teamGroupsId;
    //处理人
    private String handleName;
    //设备名称
    private String deviceName;
    //安装位置
    private String devicePlace;
    //设备类型
    private String deviceTypeName;
    //商户名称
    private String areaName;
    //是否开启处理按钮
    private Integer isStatus;
    //是否是待处理 2 待处理  10 报修历史
    private Integer isHandle;
    //1、巡检计划生成 2、其他
    private String type;
    //任务项记录表
    private List<MissionItemRecord> recordList;

    // 如果不为空 已时间排序
    private Integer selectSource;
    //处理期限
    private String deadline;
    //班组ids
    private List<Integer> teamGroupIds;

    private Integer appUserId;

    private String address;//商户地址

    private String count0;
    private String count1;
    private String count2;
    private String count3;
    private String name;
    //上报人名称
    private String reportUser;
    //处理率
    private Integer proportion;
    private Integer isAdmin;
    //指派处理人
    private String assionUserName;

    private String radioVal;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date aCreateTime;
}
