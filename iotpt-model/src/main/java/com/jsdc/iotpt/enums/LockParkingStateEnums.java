package com.jsdc.iotpt.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
public enum LockParkingStateEnums {
    //1：⼊场； 2：在场； 4：出场； 8：空场； 16：⻋位异常； 32：延迟上报出场； 64：合并出⼊场； 128：预⼊场； 256：预出场； 512：⼊场修正
    IN(1, "⼊场"),
    IN_PARKING(2, "在场"),
    OUT(4, "出场"),
    EMPTY(8, "空场"),
    ABNORMAL(16, "⻋位异常"),
    DELAY_REPORT_OUT(32, "延迟上报出场"),
    MERGE_IN_OUT(64, "合并出⼊场"),
    PRE_IN(128, "预⼊场"),
    PRE_OUT(256, "预出场"),
    IN_CORRECTION(512, "⼊场修正")

    ;

    LockParkingStateEnums(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    private Integer value;

    private String desc;

    public static String getDescByValue(Integer value) {
        if (null == value){
            return "";
        }
        for (LockParkingStateEnums myEnum : LockParkingStateEnums.values()) {
            if (Objects.equals(myEnum.getValue(), value)) {
                return myEnum.getDesc();
            }
        }
        return "";
    }

    public static LockParkingStateEnums getValueByDesc(String desc) {
        for (LockParkingStateEnums myEnum : LockParkingStateEnums.values()) {
            if (myEnum.getDesc().equals(desc)) {
                return myEnum;
            }
        }
        return null;
    }

    public static List<Map<String, Object>> getSelectList() {
        // 调整为list输出到前端
        List<Map<String, Object>> keyValueList = new ArrayList<>();
        for (LockParkingStateEnums exampleEnum : LockParkingStateEnums.values()) {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("value", exampleEnum.getValue());
            map.put("label", exampleEnum.getDesc());
            keyValueList.add(map);
        }
        return keyValueList;
    }
}
