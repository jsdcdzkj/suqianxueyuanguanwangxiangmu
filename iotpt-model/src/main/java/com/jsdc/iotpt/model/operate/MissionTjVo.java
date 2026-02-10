package com.jsdc.iotpt.model.operate;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.persistence.Transient;
import java.util.List;

@Data
public class MissionTjVo {

    private String id ;
    private String teamGroupsName ;
    private String teamGroupsId ;

    private String taskTypeName ;
    private String taskType ;

    private String count ;//数量
    private String total ;//合计

    private String stateCount ;//任务状态数量
    private String state ;
    private String planName ;//任务名称
    private String handleDate ;//处理时间
    private String planStartTime ;//任务发起时间

    private String timeType;//1:日 2：月 3：年 4 周

    private String timeStr;//选择月或年时，用来存储月份和年份

    private String timeStart;//开始时间
    private String timeEnd;//结束时间
    private String[] times;//结束时间

    private String day;
    private String finishingRate;//完成率
    private String formate;
    private String infoC0;
    private String infoC1;
    private String infoC2;
    private String querryTimeType; //0完成时间 1上报时间
    private String querryTimeString;



    private Integer pageIndex ;


    private Integer pageSize ;


    private List<String> count1;



}
