package com.jsdc.iotpt.entity;

import lombok.Data;

import java.util.List;

@Data
public class RequestEntity {

    /**
     * 报警信息对象
     */
    private AlarmInfo alarm_info;

    /**
     * 视频通道号  单通道时为0
     */
    private Integer chnlid;

    /**
     * 设备信息对象
     */
    private DeviceInfo device_info;
    /**
     * 路内产品信息
     */
    private Product_h product_h;

    private String protocol;
    /**
     * 识别id
     */
    private Integer reco_id;
    /**
     * 结果序列号
     */
    private Long serial;
    /**
     * UTC时间
     */
    private Integer time_s;
    /**
     * 触发类型  5:虚拟线圈触发; 6:稳定触发; 7:IO强制触发; 8:手动触发; 9:SDK抓拍; 10:实时结果（车辆实时入场事 件） 12:单帧识别结果; 13:定时触发;
     */
    private Integer trigger_type;
    /**
     * 特征图信息对象(数组)
     */
    private List<FetureImg> feture_img;

}
