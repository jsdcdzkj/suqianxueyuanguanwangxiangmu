package com.jsdc.iotpt.common.enums;

import java.util.stream.Stream;

/**
 * @Author lb
 * @Date 2023/11/29 8:50
 * @Description 类描述
 **/
public enum AepDeviceTypeEnum {

    MAGNETIC_DETECTOR_TYPE("门磁探测器", "01"),
    WATER_IMMERSION_TYPE("水浸", "02"),
    SMOKE_DETECTOR_TYPE("烟感", "03"),
    GAS_TYPE("燃气", "04"),
    INFRARED_INDUCTION_TYPE("红外感应", "05"),
    CO_SENSOR_TYPE("CO传感器", "06"),
    EMERGENCY_BUTTON_TYPE("紧急按钮", "07"),
    TEMPERATURE_HUMIDITY_SENSOR_TYPE("温湿度传感器", "08");

    private String name;
    private String value;


    AepDeviceTypeEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 通过值获取枚举类
     *
     * @param val
     * @return
     */
    public static AepDeviceTypeEnum getValue(String val) {
        return Stream.of(AepDeviceTypeEnum.values()).filter(s -> s.value.equals(val)).findAny().orElse(null);
    }

    /**
     * 通过值获取描name
     *
     * @param val
     * @return
     */
    public static String getName(String val) {
        AepDeviceTypeEnum[] values = AepDeviceTypeEnum.values();
        return Stream.of(values).filter(s -> s.value.equals(val)).map(s -> s.name).findAny().orElse(null);
    }

//    public static void main(String[] args) {
//        System.out.println(AepDeviceTypeEnum.getName("01"));
//    }
}
