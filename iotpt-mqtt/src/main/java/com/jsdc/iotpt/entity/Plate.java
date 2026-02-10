package com.jsdc.iotpt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 车牌信息对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plate {

    /**
     * 车牌颜色 0:未知; 1:蓝色; 2:黄色; 3:白色; 4:黑色; 5:绿色;
     */
    private Integer color;
    /**
     * 车牌置信度
     */
    private Integer confidence;

    /**
     * 位置坐标对象
     * bottom 下 int 是
     * left 左 int 是
     * right 右 int 是
     * top 上 int 是
     */
    private Map<String, Object> loc;
    /**
     * 车牌 (UTF8后再BASE64编码 如果有车牌:真实车牌号 如果有车无牌:无牌车 如果无车无牌:__无__ 非机动车:非机动车)
     */
    private String plate;

    private Integer plate_type;
    /**
     * 车牌类型 0:未知车牌 1:蓝牌小汽车 2:黑牌小汽车 3:单排黄牌 4:双排黄牌(大车尾牌，农用车) 5:警车车牌 6:武警车牌 7:个性化车牌 8:单排军车牌 9:双排军车牌 10:使馆车牌 11:香港进出中国大陆车牌 12:农用车牌 13:教练车牌 14:澳门进出中国大陆车牌 15:双层武警车牌 16:武警总队车牌 17:双层武警总队车牌 18:民航车牌 19:新能源车牌 20:新能源车牌大 21:应急 22:领馆
     */
    private Integer type;
}
