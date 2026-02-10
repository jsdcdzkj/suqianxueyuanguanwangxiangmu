package com.jsdc.iotpt.enums;

import com.alibaba.druid.util.StringUtils;
import lombok.Getter;

@Getter
public enum LogEnums {
    LOG_LOGIN("1", "登录"),
    LOG_UP_PASSWORD("1-1", "修改密码"),
    LOG_UPDATE("1-2", "更新个人信息"),
    LOG_SIGN_NO("1-3", "注销用户"),
    LOG_WX_LOGIN("1-4", "微信登陆"),
    LOG_EXIT("2", "退出"),

    /**
     * 系统管理
     */
    LOG_SYSTEM_BUILD("3-1", "系统管理-楼宇管理"),
    LOG_SYSTEM_FLOOR("3-2", "系统管理-楼层管理"),
    LOG_SYSTEM_AREA("3-3", "系统管理-区域管理"),
    LOG_SYSTEM_UNIT("3-4", "系统管理-单位管理"),
    LOG_SYSTEM_MENU("3-5", "系统管理-菜单管理"),
    LOG_SYSTEM_MENU_SAVE("3-5-1", "系统管理-菜单管理-保存菜单"),
    LOG_SYSTEM_MENU_DEL("3-5-2", "系统管理-菜单管理-删除菜单"),
    LOG_SYSTEM_MENU_SHOW("3-5-3", "系统管理-菜单管理-修改菜单显示状态"),
    LOG_SYSTEM_ROLE("3-6", "系统管理-角色管理"),
    LOG_SYSTEM_ROLE_SAVE("3-6-1", "系统管理-角色管理-保存角色"),
    LOG_SYSTEM_ROLE_DEL("3-6-2", "系统管理-角色管理-删除角色"),
    LOG_SYSTEM_USER("3-7", "系统管理-用户管理"),
    LOG_SYSTEM_DICT("3-8", "系统管理-字典管理"),
    LOG_SYSTEM_CONFIG("3-9", "系统管理-系统配置"),
    LOG_SYSTEM_POINT_ADD("3-10", "系统管理-系统配置-点位新增"),
    LOG_SYSTEM_POINT_EDIT("3-11", "系统管理-系统配置-点位编辑"),
    LOG_SYSTEM_POINT_DEL("3-12", "系统管理-系统配置-点位删除"),

    /**
     * 配置管理
     */
    LOG_CONFIG_TOPIC("4-1", "配置管理-主题管理"),
    LOG_CONFIG_LINK("4-2", "配置管理-连接管理"),
    LOG_CONFIG_TRANSFER("4-3", "配置管理-数据模板"),
    LOG_CONFIG_DEVICE("4-4", "配置管理-设备类型"),
    LOG_CONFIG_SIGNAL("4-5", "配置管理-信号类型"),
    LOG_CONFIG_BREAKDOWN("4-6", "配置管理-设备分项"),
    LOG_CONFIG_SUPPLIER("4-7", "配置管理-供应商管理"),
    LOG_CONFIG_SUPPLIER_SAVE("4-7-1", "配置管理-供应商管理-保存供应商"),
    LOG_CONFIG_SUPPLIER_DEL("4-7-2", "配置管理-供应商管理-删除供应商"),
    LOG_CONFIG_SUPPLIER_MODEL("4-7", "配置管理-供应商管理-型号管理"),

    /**
     * 设备中心
     */
    LOG_DEVICE_COLLECT("5-1", "设备中心-采集终端"),
    LOG_DEVICE_DOOR("5-2", "设备中心-门禁设备"),
//    LOG_DEVICE_COLLECT("5-3", "设备中心-视频设备"),
    LOG_DEVICE_GATEWAY("5-4", "设备中心-网关设备"),

    /**
     * 告警中心
     */
    LOG_WARNING("6-1", "告警中心-实时告警"),
//    LOG_WARNING("6-2", "告警中心-告警配置"),
//    LOG_WARNING("6-3", "告警中心-历史告警"),

    /**
     * 消息服务
     */
    LOG_MESSAGE("7-1", "消息服务-服务管理"),
//    LOG_MESSAGE("7-2", "消息服务-黑名单"),

    /**
     * 门禁控制
     */
    LOG_DOOR_CONTROLS("8-1", "门禁控制-门禁开关"),
    LOG_DOOR_ADD("8-2", "门禁控制-新增"),
    LOG_DOOR_UPDATE("8-3", "门禁控制-修改"),
    LOG_DOOR_DELETE("8-4", "门禁控制-删除"),
    LOG_DOOR_ADD_USER("8-5", "门禁控制-门禁添加人"),
    LOG_DOOR_DELETE_USER("8-6", "门禁控制-门禁删除人"),

    /**
     * 照明控制
     */
    LOG_LIGHT_CONTROLS("9-1", "照明控制-照明开关"),
    LOG_LIGHT_SYNCDATA("9-2", "照明控制-数据同步"),
    LOG_LIGHT_UPDATE("9-3", "照明控制-修改"),
    LOG_LIGHT_DELETE("9-4", "照明控制-删除"),


    LOG_ACAIR_CONTROLS("10-1", "空调控制-空调下发指令"),
    LOG_ACAIR_STATUS_CONTROLS("10-2", "空调控制-空调状态"),
    LOG_ACAIR_BATCH_CONTROLS("10-3", "空调控制-空调批量开关"),
    LOG_ACAIR_OTHER_CONTROLS("10-4", "空调控制-空调其它"),
    LOG_ACAIR_FLOOR_CONTROLS("10-5", "空调控制-楼层选择"),

    LOG_AREA_ADD("11-1", "区域新增"),
    LOG_AREA_UPDATE("11-2", "区域修改"),
    LOG_AREA_DELETE("11-3", "区域删除"),

    LOG_BUILD_ADD("12-1", "楼宇新增"),
    LOG_BUILD_UPDATE("12-2", "楼宇修改"),
    LOG_BUILD_DELETE("12-3", "楼宇删除"),

    LOG_FLOOR_ADD("13-1", "楼层新增"),
    LOG_FLOOR_UPDATE("13-2", "楼层修改"),
    LOG_FLOOR_DELETE("13-3", "楼层删除"),

    /**
     * Aep设备操作
     */
    LOG_AEP_ADD_OR_UPDATE("14-1", "Aep设备-新增/修改"),
//    LOG_AEP_ADD("14-2", "Aep设备-新增"),
//    LOG_AEP_UPDATE("14-3", "Aep设备-修改"),
    LOG_AEP_DELETE("14-4", "Aep设备-删除"),
    LOG_AEP_UPLOAD("14-5", "Aep设备-导入"),


    /**
     * 视频设备
     */
    LOG_VIDEO_ADD_OR_UPDATE("15-1", "视频设备-新增/修改"),
    //    LOG_AEP_ADD("14-2", "Aep设备-新增"),
//    LOG_AEP_UPDATE("14-3", "Aep设备-修改"),
    LOG_VIDEO_DELETE("15-4", "视频设备-删除"),
    LOG_VIDEO_UPLOAD("15-5", "视频设备-导入"),
    LOG_VIDEO_LOCK_UPDATE("15-6", "视频设备-地锁信息修改"),


    /**
     * 电梯设备
     */
//    LOG_LIFT_ADD_OR_UPDATE("15-1", "视频设备-新增/修改"),
    LOG_LIFT_ADD("16-2", "电梯设备-新增"),
    LOG_LIFT_UPDATE("16-3", "电梯设备-修改"),
    LOG_LIFT_DELETE("16-4", "电梯设备-删除"),

    /**
     * 用户信息 权限
     */
    LOG_SYSTEM_USER_SAVE("17-1", "用户-新增"),
    LOG_SYSTEM_USER_UPDATE("17-2", "用户-修改"),
    LOG_SYSTEM_USER_DELETE("17-3", "用户-删除"),
    LOG_SYSTEM_USER_DAHUA_SYNCHRONIZATION("17-4", "用户-大华同步"),
    LOG_SYSTEM_USER_AUTHORITY("17-5", "用户-权限修改"),
    LOG_SYSTEM_USER_CAR("17-6", "用户-车辆"),
    LOG_SYSTEM_USER_AI_MENU("17-7", "用户-AI权限"),


    LOG_WARNING_CONFIG_UPDATE("18-1", "告警配置-新增修改"),
    LOG_WARNING_CONFIG_DELETE("18-2", "告警配置-删除"),




    LOG_ELECTRIC_CHARGE_ADD("19-1", "电费价格新增"),
    LOG_ELECTRIC_CHARGE_UPD("19-2", "电费价格修改"),
    LOG_ELECTRIC_CHARGE_DEL("19-3", "电费价格删除"),


    LOG_WATER_CHARGE_ADD("20-1", "水费价格新增"),
    LOG_WATER_CHARGE_UPD("20-2", "水费价格修改"),
    LOG_WATER_CHARGE_DEL("20-3", "水费价格删除"),
//    LOG_WARNING("6-2", "告警中心-告警配置"),
//    LOG_WARNING("6-3", "告警中心-历史告警"),

    LOG_CALL_LIFT("21-1", "智能呼梯"),
    LOG_CALL_BOSS_LIFT("21-2", "智能呼专梯"),


    LOG_ACAIR_17_CONTROLS("22-5", "智慧插座-下发指令"),
    ;

    LogEnums(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    private String value;

    private String desc;

    public static String getDescByValue(String value) {
        for (LogEnums myEnum : LogEnums.values()) {
            if (StringUtils.equals(myEnum.getValue(), value)) {
                return myEnum.getDesc();
            }
        }
        return null;
    }

    public static LogEnums getValueByDesc(String desc) {
        for (LogEnums myEnum : LogEnums.values()) {
            if (myEnum.getDesc().equals(desc)) {
                return myEnum;
            }
        }
        return null;
    }
}
