package com.jsdc.iotpt.model;


import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@TableName("investment_person")
@Table(name = "investment_person")
@KeySequence(value = "INVESTMENT_PERSON_ID")
@Data
@ApiModel(value ="招商人员表")
public class InvestmentPerson extends Model<InvestmentPerson> {

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "investmentPersonId", sequenceName = "INVESTMENT_PERSON_ID", allocationSize = 1)
    @GeneratedValue(generator = "investmentPersonId", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @ApiModelProperty(value ="用户id")
    private Integer user_id;


    @ApiModelProperty(value ="是否招商经理 0否1是 ")
    private String is_manager;



    //招商人名称
    @Transient
    @TableField(exist = false)
    private String realname ;

    //招商人电话
    @Transient
    @TableField(exist = false)
    private String phone ;

    //意向客户数量
    @Transient
    @TableField(exist = false)
    private Integer customersCount ;


    //合同数量
    @Transient
    @TableField(exist = false)
    private Integer contractQuantity ;

    @Transient
    @TableField(exist = false)
    private Integer pageNo ;

    @Transient
    @TableField(exist = false)
    private Integer pageSize ;

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
}
