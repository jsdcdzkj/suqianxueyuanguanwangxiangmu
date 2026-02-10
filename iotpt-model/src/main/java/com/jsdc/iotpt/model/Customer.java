package com.jsdc.iotpt.model;


import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@TableName("customer")
@Table(name = "customer")
@KeySequence(value = "SEQ_CUSTOMER_ID")
@Data
@ApiModel(value = "客户管理表")
public class Customer {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_CUSTOMER_ID", sequenceName = "SEQ_CUSTOMER_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_CUSTOMER_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "客户管理表id")
    private Integer id;

    @ApiModelProperty(value = "客户类型【字典】")
    @Comment(value = "客户类型【字典】")
    private String type;

    @ApiModelProperty(value = "客户类型")
    @Transient
    @TableField(exist = false)
    private String typeName;

    @ApiModelProperty(value = "客户名称")
    @Comment(value = "客户名称")
    private String name;

    @ApiModelProperty(value = "客户电话")
    @Comment(value = "客户电话")
    private String phone;

    @ApiModelProperty(value = "公司名称")
    @Comment(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "行业【字典】")
    @Comment(value = "行业【字典】")
    private String industryValue;

    @ApiModelProperty(value = "行业名称")
    @Transient
    @TableField(exist = false)
    private String industryName;

    @ApiModelProperty(value = "意向项目【字典】")
    @Comment(value = "意向项目【字典】")
    private String projectValue;

    @ApiModelProperty(value = "意向项目名称")
    @Transient
    @TableField(exist = false)
    private String projectName;

    @ApiModelProperty(value = "需求面积m² 从")
    @Comment(value = "需求面积m² 从")
    private String requiredAreaFrom;

    @ApiModelProperty(value = "需求面积m² 至")
    @Comment(value = "需求面积m² 至")
    private String requiredAreaTo;

    @ApiModelProperty(value = "客户预算金额")
    @Comment(value = "客户预算金额")
    private String budgetAmount;

    @ApiModelProperty(value = "预计签约时间")
    @Comment(value = "预计签约时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date expectedSigningTime;

    @ApiModelProperty(value = "跟进人/招商人员id")
    @Comment(value = "跟进人/招商人员id")
    private Integer investmentPersonId;

    @ApiModelProperty(value = "跟进人/招商人员")
    @Transient
    @TableField(exist = false)
    private String investmentPersonName;

    @ApiModelProperty(value = "跟进人员类型 1指定人员 2加入公海 3分配给创建人")
    @Transient
    @TableField(exist = false)
    private String investmentType;

    @ApiModelProperty(value = "客户状态【字典】：1.接触 2.带看 3.沟通 4.流失 5.成交")
    @Comment(value = "状态【字典】：1.接触 2.带看 3.沟通 4.流失 5.成交")
    private String status;

    @ApiModelProperty(value = "客户状态【字典】：1.接触 2.带看 3.沟通 4.流失 5.成交")
    @Transient
    @TableField(exist = false)
    private String statusName;

    @ApiModelProperty(value = "成交率")
    @Comment(value = "成交率")
    private String closingRate;

    @ApiModelProperty(value = "备注")
    @Comment(value = "备注")
    private String remarks;

    @ApiModelProperty(value = "公海 0否 1是")
    @Comment(value = "公海 0否 1是")
    private String isHighSeas;

    @ApiModelProperty(value = "来源 1录入 2网站")
    @Comment(value = "来源 1录入 2网站")
    private String source;

    @Transient
    @TableField(exist = false)
    private String sourceName;

    public void setSourceName() {
        if (StringUtils.isNotEmpty(getSource())) {
            if ("1".equals(getSource())) {
                sourceName = "录入";
            } else if ("2".equals(getSource())) {
                sourceName = "网站";
            }
        }
    }

    @ApiModelProperty(value = "租赁房间表id")
    @Comment(value = "租赁房间表id")
    private Integer hireRoomId;

    @ApiModelProperty(value = "租赁房间名称")
    @Transient
    @TableField(exist = false)
    private String hireRoomName;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 创建人id
     */
    private Integer createUser;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 更新人id
     */
    private Integer updateUser;

    /**
     * 是否删除 0:正常  1：删除
     */
    private Integer isDel;

    /**
     * 分配人id
     */
    @ApiModelProperty(value = "分配人id")
    private Integer allocateUser;

    @ApiModelProperty(value = "分配人名称")
    @Transient
    @TableField(exist = false)
    private String allocateUserName;

    @ApiModelProperty(value = "分配时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date allocateTime;

    @Transient
    @TableField(exist = false)
    private Integer pageNo;

    @Transient
    @TableField(exist = false)
    private Integer pageSize;

    @Transient
    @TableField(exist = false)
    private String startTime;

    @Transient
    @TableField(exist = false)
    private String endTime;

    @Transient
    @TableField(exist = false)
    private List<CustomerFollowHistory> customerFollowHistoryList;

    @Transient
    @TableField(exist = false)
    private List<CustomerAccessRecord> customerAccessRecordList;

    @ApiModelProperty(value = "查询类型 1我的客户 2全部客户 3客户公海 4网站客户")
    @Transient
    @TableField(exist = false)
    private String searchType;

    @ApiModelProperty(value = "总客户数量")
    @Transient
    @TableField(exist = false)
    private Long totalCount;

    @ApiModelProperty(value = "本日新增客户数量")
    @Transient
    @TableField(exist = false)
    private Long dayCount;

    @ApiModelProperty(value = "本周新增总客户数量")
    @Transient
    @TableField(exist = false)
    private Long weekCount;

    @ApiModelProperty(value = "本月新增总客户数量")
    @Transient
    @TableField(exist = false)
    private Long monthCount;

    @ApiModelProperty(value = "跟进人")
    @Transient
    @TableField(exist = false)
    private Integer userId;
}
