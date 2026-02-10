package com.jsdc.iotpt.model;


import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@TableName("customer_follow_history")
@Table(name = "customer_follow_history")
@KeySequence(value = "SEQ_CUSTOMER_FOLLOW_ID")
@Data
@ApiModel(value ="客户跟进历史记录表")
public class CustomerFollowHistory {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_CUSTOMER_FOLLOW_ID", sequenceName = "SEQ_CUSTOMER_FOLLOW_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_CUSTOMER_FOLLOW_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value ="客户跟进历史记录表id")
    private Integer id;

    @ApiModelProperty(value ="客户id")
    @Comment(value ="客户id")
    private Integer customerId;

    @ApiModelProperty(value ="状态【字典】：1.接触 2.带看 3.沟通 4.流失 5.成交")
    @Comment(value ="状态【字典】：1.接触 2.带看 3.沟通 4.流失 5.成交")
    private String status;

    @ApiModelProperty(value ="状态【字典】：1.接触 2.带看 3.沟通 4.流失 5.成交")
    @Transient
    @TableField(exist = false)
    private String statusName;

    @ApiModelProperty(value ="内容")
    @Comment(value ="内容")
    private String content;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 下次跟进时间
     */
    @ApiModelProperty(value ="下次跟进时间")
    @Comment(value ="下次跟进时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date nextFollowUpTime;

    @ApiModelProperty(value ="跟进人/招商人员id")
    @Comment(value ="跟进人/招商人员id")
    private Integer investmentPersonId;

    @ApiModelProperty(value ="跟进人")
    @Transient
    @TableField(exist = false)
    private String investmentPersonName;


    /**
     * 是否删除 0:正常  1：删除
     */
    private Integer isDel;

    //图片上传功能
    @Transient
    @TableField(exist = false)
    @JSONField(serialize = false)
    @ApiModelProperty(hidden = true)
    private List<SysFile> fileList;
}
