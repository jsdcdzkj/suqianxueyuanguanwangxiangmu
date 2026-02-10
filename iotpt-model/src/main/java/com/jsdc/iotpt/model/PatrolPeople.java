package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

/**
 * ClassName: 巡更人员
 * Description:
 * date: 2024/1/9 14:49
 *
 * @author bn
 */
@Entity
@TableName("PATROL_PEOPLE")
@Table(name = "PATROL_PEOPLE")
@KeySequence(value = "PATROL_PEOPLE_ID")
@Data
@ApiModel(value = "巡更人员")
public class PatrolPeople {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "PATROL_PEOPLE_ID", sequenceName = "PATROL_PEOPLE_ID", allocationSize = 1)
    @GeneratedValue(generator = "PATROL_PEOPLE_ID", strategy = GenerationType.SEQUENCE)
    @ApiModelProperty(value = "主键")
    private Integer id;

    // 巡更计划id
    @ApiModelProperty(value = "巡更计划id")
    private Integer planId;

    // 巡更人员
    @ApiModelProperty(value = "巡更人员id")
    private Integer userId;

    @ApiModelProperty(value = "删除标志:0,正常；1，删除")
    private Integer isDel;



}
