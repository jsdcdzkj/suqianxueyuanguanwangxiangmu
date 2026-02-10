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

@Entity
@TableName("customer_access_record")
@Table(name = "customer_access_record")
@KeySequence(value = "SEQ_CUSTOMER_RECORD_ID")
@Data
@ApiModel(value ="客户出入公海记录表")
public class CustomerAccessRecord {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "SEQ_CUSTOMER_RECORD_ID", sequenceName = "SEQ_CUSTOMER_RECORD_ID", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_CUSTOMER_RECORD_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value ="客户出入公海记录表id")
    private Integer id;

    @ApiModelProperty(value ="客户id")
    @Comment(value ="客户id")
    private Integer customerId;

    @ApiModelProperty(value ="状态：1.进入 2.移出")
    @Comment(value ="状态：1.进入 2.移出")
    private String status;

    @ApiModelProperty(value ="状态：1.进入 2.移出")
    @Transient
    @TableField(exist = false)
    private String statusName;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value ="操作人/招商人员id")
    @Comment(value ="操作人/招商人员id")
    private Integer investmentPersonId;

    @ApiModelProperty(value ="操作人/招商人员")
    @Transient
    @TableField(exist = false)
    private String investmentPersonName;

    /**
     * 是否删除 0:正常  1：删除
     */
    private Integer isDel;

    public void setStatusName() {
        if (StringUtils.isNotEmpty(getStatus())) {
            if ("1".equals(getStatus())) {
                statusName = "进入";
            } else if ("2".equals(getStatus())) {
                statusName = "移出";
            }
        }
    }

}
