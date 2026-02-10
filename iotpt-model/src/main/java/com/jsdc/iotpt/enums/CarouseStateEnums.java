package com.jsdc.iotpt.enums;

import lombok.Getter;

@Getter
public enum CarouseStateEnums {
    //1.未开始 2.展示中 3.已结束
    STATE_START(1, "未开始"),
    STATE_ING(2, "展示中"),
    STATE_END(3, "已结束"),

    ;

    CarouseStateEnums(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    private Integer value;

    private String desc;

    public static String getDescByValue(int value) {
        for (CarouseStateEnums myEnum : CarouseStateEnums.values()) {
            if (myEnum.getValue() == value) {
                return myEnum.getDesc();
            }
        }
        return null;
    }

    public static CarouseStateEnums getValueByDesc(String desc) {
        for (CarouseStateEnums myEnum : CarouseStateEnums.values()) {
            if (myEnum.getDesc().equals(desc)) {
                return myEnum;
            }
        }
        return null;
    }
}
