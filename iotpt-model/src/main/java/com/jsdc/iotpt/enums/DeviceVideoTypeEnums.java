package com.jsdc.iotpt.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
public enum DeviceVideoTypeEnums {

    //类型 1.普通视频设备, 2.车位识别视频设备
    TYPE_GUN(1, "普通监控设备"),
    TYPE_BALL_MACHINE(2, "全景视频设备"),
    TYPE_HALF_BALL_MACHINE(3, "电梯视频设备"),
    TYPE_LOCAL_ACQUISITION(5, "门禁视频设备"),
    TYPE_ORDINARY(6, "IVSS行为分析设备"),
    TYPE_PARKING(7, "车位识别视频设备"),

    ;

    DeviceVideoTypeEnums(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    private Integer value;

    private String desc;

    public static String getDescByValue(Integer value) {
        if (null == value){
            return "";
        }
        for (DeviceVideoTypeEnums myEnum : DeviceVideoTypeEnums.values()) {
            if (Objects.equals(myEnum.getValue(), value)) {
                return myEnum.getDesc();
            }
        }
        return "";
    }

    public static DeviceVideoTypeEnums getValueByDesc(String desc) {
        for (DeviceVideoTypeEnums myEnum : DeviceVideoTypeEnums.values()) {
            if (myEnum.getDesc().equals(desc)) {
                return myEnum;
            }
        }
        return null;
    }

    public static List<Map<String, Object>> getSelectList() {
        // 调整为list输出到前端
        List<Map<String, Object>> keyValueList = new ArrayList<>();
        for (DeviceVideoTypeEnums exampleEnum : DeviceVideoTypeEnums.values()) {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("value", exampleEnum.getValue());
            map.put("label", exampleEnum.getDesc());
            keyValueList.add(map);
        }
        return keyValueList;
    }
}
