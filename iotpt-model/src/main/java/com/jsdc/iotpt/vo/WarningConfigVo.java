package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.WarningConfig;
import com.jsdc.iotpt.model.WarningSignDetails;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lb
 */
@Data
public class WarningConfigVo extends WarningConfig {
    private Integer pageNo = 1;
    private Integer pageSize = 10;

    //设备类型名称
    private String deviceTypeName;

    //配置范围名称
    private String configTypeName;
    /**
     * 告警详情
     */
    private List<WarningSignDetails> details;
    private List<WarningSignDetailsVo> warnDetailsList;

    //正常（非异常状态
    private String zc;
    //异常（非离线异常状态）
    private String yc;
    //离线（离线异常状态）
    private String lx;

    // 模板id
    private Integer tempId;
    private Integer collectId;
    private String signName;
    private String signType;
    private String valueType;
    private String content;
    private double valueBegin;
    private double valueEnd;
    private Integer numberBool;
    private String warnLevel;
    private Integer valueBoolen;

    private String timeType ;

    private String startTime ;
    private String endTime ;
    private String areaId ;

    private List<Integer> areaIds = new ArrayList<>();

}
