package com.jsdc.iotpt.vo.operate;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class WorkOrderVo {

    private Integer id;
    //标题
    private String title;
    //状态
    private Integer states;
    //状态名称
    private String statesName;
    //内容
    private String notes;
    //执行人
    private String username;
    //来源1、(系统工单)告警上报；2、人员上报；3、（巡检工单）巡检异常上报；4、巡检计划生成  5、（服务工单）报事报修  6、（投诉工单）投诉申请
    private Integer source;
    //来源名称
    private String sourceName;
    //执行时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date deadline;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    //以下查询条件
    //大类（默认0）
    private Integer large;
    //小类（不必填）
    private Integer small;
    //状态类型（默认0）0、全部 1、进行中 2、我发起的  3、指派给我的  4、已完成的
    private Integer stateType;
    //排序类型（默认0）0、全部 1、创建时间近到远 2、创建时间远到近  3、截止时间近到远  4、截止时间远到近
    private Integer sortType;
    //用户id
    private Integer userId;
    private Integer handleId;
    //分页
    private Integer pageIndex;
    private Integer pageSize;
}
