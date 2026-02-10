package com.jsdc.iotpt.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum CarouseLinkOptionEnums {
    //1.无, 2.站内文档, 3.url
    OPTION_NONE("1", "无"),
    OPTION_INDOC("2", "站内文档"),
    OPTION_URL("3", "url"),

    ;

    CarouseLinkOptionEnums(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    private String value;

    private String desc;

    public static String getDescByValue(String value) {
        for (CarouseLinkOptionEnums myEnum : CarouseLinkOptionEnums.values()) {
            if (StringUtils.equals(myEnum.getValue(), value)) {
                return myEnum.getDesc();
            }
        }
        return null;
    }

    public static CarouseLinkOptionEnums getValueByDesc(String desc) {
        for (CarouseLinkOptionEnums myEnum : CarouseLinkOptionEnums.values()) {
            if (myEnum.getDesc().equals(desc)) {
                return myEnum;
            }
        }
        return null;
    }
}
