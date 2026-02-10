package com.jsdc.iotpt.vo;

import lombok.Data;

/**
 * ClassName: LeNiuVo
 * Description:
 * date: 2024/5/28 19:24
 *
 * @author bn
 */
@Data
public class LeNiuVo {

    // 账户id
    private Long custThirdId;
    // 食堂id
    private String canteenId;
    //  支付状态  请求时  1 支付成功 2 支付失败；响应时 2-支付中，3-支付成功，4-支付失败
    private String payState;
    private String startDate;
    private String endDate;
    // 餐次
    private Integer intervalType;
    //大华用户id
    private Long dahuauserId;

}
