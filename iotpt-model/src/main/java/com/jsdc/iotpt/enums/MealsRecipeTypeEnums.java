package com.jsdc.iotpt.enums;

import lombok.Getter;

@Getter
public enum MealsRecipeTypeEnums {
    //(1早餐 2午餐 3下午茶 4晚餐5夜宵)
    STATE_BREAKFAST(1, "早餐"),
    STATE_LUNCH(2, "午餐"),
    STATE_AFTERNOON_TEA(3, "下午茶"),
    STATE_DINNER(4, "晚餐"),
    STATE_NIGHT(5, "夜宵")

    ;

    MealsRecipeTypeEnums(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    private Integer value;

    private String desc;

    public static String getDescByValue(int value) {
        for (MealsRecipeTypeEnums myEnum : MealsRecipeTypeEnums.values()) {
            if (myEnum.getValue() == value) {
                return myEnum.getDesc();
            }
        }
        return null;
    }

    public static MealsRecipeTypeEnums getValueByDesc(String desc) {
        for (MealsRecipeTypeEnums myEnum : MealsRecipeTypeEnums.values()) {
            if (myEnum.getDesc().equals(desc)) {
                return myEnum;
            }
        }
        return null;
    }
}
