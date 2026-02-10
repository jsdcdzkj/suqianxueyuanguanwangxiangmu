package com.jsdc.iotpt.vo;

import lombok.Data;

@Data
public class TransformerVo {
    //监测时间
    private String time;
    //监测时间
    private String dataTime;

    //额定容量
    private String rated_capacity;

    //最大负荷占用
    private String load_occupancy;

    //值
    private String val;
}
