package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.ReportManage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * ClassName: ReportManageVo
 * Description:
 * date: 2024/2/26 15:06
 *
 * @author bn
 */
@Data
public class ReportManageVo extends ReportManage {


    // 开始时间
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    // 结束时间
    @ApiModelProperty(value = "结束时间")
    private String endTime;

    // 按日，月，年
    @ApiModelProperty(value = "日 1，月 2，年 3")
    private Integer timeType;

    // 单商户，多商户
    @ApiModelProperty(value = "单商户 1，双商户 2")
    private Integer splType;






}
