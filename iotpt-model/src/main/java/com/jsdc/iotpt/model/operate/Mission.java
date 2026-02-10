package com.jsdc.iotpt.model.operate;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jsdc.iotpt.model.SysFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @projectName: IOT
 * @className: mission
 * @author: zln
 * @description: 我的任务—任务表
 * @date: 2023/8/23 13:55
 */
@Entity
@TableName("MISSION")
@Table(name = "MISSION")
@KeySequence(value = "SEQ_MISSION_ID")
@Data
@ApiModel(value = "任务表")
public class Mission implements Serializable {

    private static final long serialVersionUID = 3234235552261L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "missionId", sequenceName = "SEQ_MISSION_ID", allocationSize = 1)
    @GeneratedValue(generator = "missionId", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "任务表ID")
    private Integer id;
    //来源1、(系统工单)告警上报；2、人员上报；3、（巡检工单）巡检异常上报；4、巡检计划生成  5、（服务工单）报事报修  6、（投诉工单）投诉申请
    @ApiModelProperty(value = "来源", required = true)
    private Integer source;
    //订单号(GD+根据来源编号+当前时间戳生成)
    @ApiModelProperty(value = "订单号", required = true)
    private String orderID;
    @ApiModelProperty(value = "服务类型 1：公区服务 2：有偿服务")
    private Integer serviceType;
    @ApiModelProperty(value = "处理状态 1：同意 2：拒绝 3:报修人确认中 4:暂存")
    private Integer handleStatus;
    //关联外键id
    @ApiModelProperty(value = "来源ID")
    private Integer sourceId;
    //告警表设备编码
    @ApiModelProperty(value = "告警设备编码")
    private String deviceCode;
    //取字典（warnLevel）
    @ApiModelProperty(value = "紧急程度", required = true)
    private Integer levels;
    //联系方式
    @ApiModelProperty(value = "联系方式", required = true)
    private String phone;
    //废除掉
    @ApiModelProperty(value = "内容", required = true)
    @Column(columnDefinition = "VARCHAR2(1000)")
    private String substance;
    @ApiModelProperty(value = "描述内容")
    @Column(columnDefinition = "VARCHAR2(1000)")
    private String notes;
    @ApiModelProperty(value = "上报人ID", required = true)
    private Integer userId;
    @ApiModelProperty(value = "问题地点", required = true)
    private String location;
    //取字典（missQuestion）
    @ApiModelProperty(value = "问题类型", required = true)
    private Integer questionType;
    @ApiModelProperty(value = "上报时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date reportingTime;
    @ApiModelProperty(value = "指派时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date assignedTime;
    @ApiModelProperty(value = "开启时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date openTime;
    @ApiModelProperty(value = "检查项ID", required = true)
    private Integer manageId;
    //1、待指派；2、待处理；3、已处理  0、暂存 4、未开启(组长未分配) 5、撤销,6：忽略 1->4->2 7：已关闭 8:驳回
    @ApiModelProperty(value = "状态", required = true)
    private Integer states;
    @ApiModelProperty(value = "驳回原因", required = true)
    private String reason;
    @ApiModelProperty(value = "驳回人", required = true)
    private Integer rejectUser;
    // 挂起 0/null未挂起，1：挂起
    private Integer isPending;
    // 挂起类型 1.所需耗材暂无库存、2.需等待其他任务完成、3.遇到技术或者操作问题、需进一步调查或修复、4.其他
    private Integer pendingType;
    // 挂起原因
    private String pendingReason;
    @ApiModelProperty(value = "挂起时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date pendingTime;
    //处理信息、处理状态、处理人、处理时间、指定人只有告警、人员上报、巡检异常上报时填写
    @ApiModelProperty(value = "处理信息", required = true)
    private String crunch;
    @ApiModelProperty(value = "处理人ID", required = true)
    private Integer handleId;
    @ApiModelProperty(value = "协助处理人所在组ID:协助处理人Id，逗号分割")
    private String assistHandleIds;
    @Transient
    @TableField(exist = false)
    private String assistHandleNames;
    @ApiModelProperty(value = "处理时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date handleDate;
    @ApiModelProperty(value = "设备id", required = true)
    private Integer deviceId;
    //1、采集设备 2、视频设备 3、网关设备 4电梯
    @ApiModelProperty(value = "类型标识", required = true)
    private Integer deviceType;
    @ApiModelProperty(value = "商户id", required = true)
    private Integer areaId;
    //暂不确定
    @ApiModelProperty(value = "指定人ID")
    private Integer assignerId;
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @ApiModelProperty(value = "创建人id")
    private Integer createUser;
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    @ApiModelProperty(value = "更新人id")
    private Integer updateUser;
    //0未收藏、1为收藏
    @ApiModelProperty(value = "是否关注收藏")
    private Integer publicity;
    @ApiModelProperty(value = "是否已读")
    private Integer isReads;
    @ApiModelProperty(value = "是否删除")
    private Integer is_del;
    @ApiModelProperty(value = "是否超时提醒 1是0否")
    private Integer is_remind;
    @ApiModelProperty(value = "忽略备注")
    @Column(columnDefinition = "VARCHAR2(1000)")
    private String ignore_remark;
    @ApiModelProperty(value = "撤回备注")
    @Column(columnDefinition = "VARCHAR2(1000)")
    private String back_remark;
    //1、已逾期  2、7天已完成  3、30天已完成
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Integer home_tab_type;
    //图片上传功能
    @Transient
    @TableField(exist = false)
    @JSONField(serialize = false)
    @ApiModelProperty(hidden = true)
    private List<SysFile> fileList;
    //指派处理期限
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Date deadlineTime;
    //任务指派信息
    @Transient
    @TableField(exist = false)
    private MissionAssign missionAssign;
    //耗材信息列表
    @Transient
    @TableField(exist = false)
    private List<MissionConsumable> consumables;
    //查询条件：处理时间
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String handleDay;
    //任务标题
    @ApiModelProperty(value = "任务标题")
    private String title;
    @ApiModelProperty(value = "驳回人")
    private String rejectUsername;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String handleName;

    //巡检计划待处理、已处理标识
    @Transient
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Integer isHandle;
    //三项设备拼接标识
    @Transient
    @TableField(exist = false)
    private String pId;

    @Transient
    @TableField(exist = false)
    private String areaName;
    //部门名称
    @Transient
    @TableField(exist = false)
    private String deptName;
    //用户名称
    @Transient
    @TableField(exist = false)
    private String userName;
    //巡检名称
    @Transient
    @TableField(exist = false)
    private String planName;
    //问题类型名称
    @Transient
    @TableField(exist = false)
    private String questionName;
    //紧急名称
    @Transient
    @TableField(exist = false)
    private String levelsName;
    //状态名称
    @Transient
    @TableField(exist = false)
    private String statesName;
    //来源名称
    @Transient
    @TableField(exist = false)
    private String sourceName;
}
