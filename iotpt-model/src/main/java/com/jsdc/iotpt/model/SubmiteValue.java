package com.jsdc.iotpt.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;




@Entity
@TableName("SUBMITE_VALUE")
@Table(name = "SUBMITE_VALUE")
@KeySequence(value = "SUBMITE_VALUE_ID")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmiteValue implements Serializable {



    @Id
    @TableId(value = "id", type = IdType.INPUT)
    @SequenceGenerator(name = "submiteValueId", sequenceName = "SUBMITE_VALUE_ID", allocationSize = 1)
    @GeneratedValue(generator = "submiteValueId", strategy = GenerationType.SEQUENCE)
    private Integer id;



    //分项id
    private String subitemId;

    @Transient
    @TableField(exist = false)
    private String year;

    //定额值
    private String ratedValue;



    //时间
    private String searchDate;


    //实际能耗
    @Transient
    @TableField(exist = false)
    private String actualValue ;

    //同比增长率
    @Transient
    @TableField(exist = false)
    private String yearOnYearGrowthRate ;

    //实际能耗
    @Transient
    @TableField(exist = false)
    private String lastRatedValue ;

    //剩余额度
    @Transient
    @TableField(exist = false)
    private String residualCredit ;


}
