package com.jsdc.iotpt.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @Author：jxl
 * @Date：2024/6/11 15:52
 * @FileDesc：
 */
@Data
public class ReserveUserExport {

    //用户名称
    @ExcelProperty(value = "人员")
    private String realName;
    //是否预定  1是0否
    @ExcelProperty(value = "是否预约")
    private String isReserve;
    //是否就餐  1是0否
    @ExcelProperty(value = "是否就餐")
    private String haveMeal;
    //消费详情
    @ExcelProperty(value = "消费详情")
    private String mealsDish;
    //日期
    @ExcelProperty(value = "日期")
    private String searchTime;

}
