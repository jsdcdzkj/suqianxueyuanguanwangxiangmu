package com.jsdc.iotpt.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.persistence.Transient;
import java.util.List;

/**
 *
 */
@Data
public class EnergyUseAnomalyVo {

    //实际能耗
    private String actualEnergyConsumption;

    //年总定额
    private String totalAnnualQuota;

    //状态 0正常 1异常 2警告
    private String status;

    //能耗比管控值
    private String controlValue;

    //A相空载电流
    private Double I_A;
    //B相空载电流
    private Double I_B;
    //C相空载电流
    private Double I_C;



    //当月实际能耗比管控
    private String realcontrolValue;



}
