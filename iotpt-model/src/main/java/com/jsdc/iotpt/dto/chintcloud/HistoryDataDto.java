package com.jsdc.iotpt.dto.chintcloud;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 泰杰赛智慧照明系统 历史数据返回体
 */

@Data
public class HistoryDataDto {

    /**
     * 存储标识
     */
    private String id;

    /**
     * 点位Id
     */
    private String storagePoint;

    /**
     * 时间戳
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;

    /**
     * 值
     */
    private String value;

    /**
     * 新增时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;

    /**
     * 格式化时间
     */
    private String timeFormate;

    /**
     * Index
     */
    private String index;

}
