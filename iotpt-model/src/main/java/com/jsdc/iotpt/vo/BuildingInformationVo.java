package com.jsdc.iotpt.vo;

import lombok.Data;

/**
 * 楼宇信息
 */
@Data
public class BuildingInformationVo {
    //楼宇名称
    private String buildingName = "鼎驰大厦";
    //楼宇数量
    private String buildingNumber = "1";
    //建筑高度
    private String buildingHeight = "56";
    //楼层层高
    private String floorHeight = "17";
    //建筑面积
    private String floorArea = "29600";
    //能源种类
    private String energyType = "2";
    //服务人数
    private String numberOfPeople = "223";
    //车位数量
    private String numberOfParkingSpaces = "320";
}
