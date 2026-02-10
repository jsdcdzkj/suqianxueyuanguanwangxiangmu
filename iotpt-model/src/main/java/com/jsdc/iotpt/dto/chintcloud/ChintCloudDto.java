package com.jsdc.iotpt.dto.chintcloud;

import lombok.Data;

/**
 * 泰杰赛智慧照明系统 外层响应体
 */

@Data
public class ChintCloudDto<T> {

    /**
     * 返回数据
     */
    private T data;

    /**
     * 提示
     */
    private String message;

    /**
     * 状态码
     */
    private Integer statusCode;


    private boolean isOk;

}
