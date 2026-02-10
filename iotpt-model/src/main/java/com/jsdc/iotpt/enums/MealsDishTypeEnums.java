package com.jsdc.iotpt.enums;

import com.alibaba.druid.util.StringUtils;
import lombok.Getter;

@Getter
public enum MealsDishTypeEnums {
    // 0.大荤 1.小荤 2.素菜 3.主食 4.汤 5.其他 6.卤菜 7.面食 8.小吃 9.7.5元菜品 10.15元菜品
    BIG_MEAT("0", "大荤", "750"),
    XIA_HSIEN("1", "小荤", "375"),
    VEGETABLE("2", "素菜", "375"),
    MAIN_FOOD("3", "主食", "100"),
    SOUP("4", "汤", "0"),
    OTHER("5", "其他", "0"),
    LIU_CAI("6", "卤菜", "750"),
    MIAN_SHI("7", "面食", "1125"),
    SNACK("8", "小吃", "375"),
    NINE_DISH("9", "7.5元菜品", "750"),
    TEN_DISH("10", "15元菜品", "1500");

    MealsDishTypeEnums(String value, String desc, String price) {
        this.value = value;
        this.desc = desc;
        this.price = price;
    }

    private String value;

    private String desc;

    private String price;

    public static String getDescByValue(String value) {
        for (MealsDishTypeEnums myEnum : MealsDishTypeEnums.values()) {
            if (StringUtils.equals(myEnum.getValue(), value)) {
                return myEnum.getDesc();
            }
        }
        return null;
    }

    public static MealsDishTypeEnums getValueByDesc(String desc) {
        for (MealsDishTypeEnums myEnum : MealsDishTypeEnums.values()) {
            if (myEnum.getDesc().equals(desc)) {
                return myEnum;
            }
        }
        return null;
    }

    /**
     * 根据类型得到价格
     * @param value
     * @return
     */
    public static String getPriceByValue(String value) {
        for (MealsDishTypeEnums myEnum : MealsDishTypeEnums.values()) {
            if (StringUtils.equals(myEnum.getValue(), value)) {
                return myEnum.getPrice();
            }
        }
        return null;
    }
}
