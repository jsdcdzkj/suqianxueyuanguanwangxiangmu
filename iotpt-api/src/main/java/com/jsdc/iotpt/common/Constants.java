package com.jsdc.iotpt.common;

//常量类
public class Constants {

    public static final String MODEL_ZHYY = "智慧应用";//ZHYY
    public static final String MODEL_YYZT = "应用中台";//YYZT
    public static final String MODEL_JSC = "驾驶舱";//JSC
    public static final String MODEL_APP = "小程序/APP";//APP
    //app权限redis key 前缀
    public static final String APP_PERMISSIONS_KEY = "WLQUWULIANWANGAPP_PERMISSIONS_KEY_";
    public static final String ZHYY_PERMISSIONS_KEY = "ZHYY_PERMISSIONS_KEY_";

    /**空调权限标识**/
    public static final String APP_PERMISSIONS_AIRAC = "app_zhkt";
    public static final String ZHYY_PERMISSIONS_AIRAC = "zhyy_kttf";

    /**照明权限标识**/
    public static final String APP_PERMISSIONS_LIGHT = "app_zhzm";
    public static final String ZHYY_PERMISSIONS_LIGHT = "zhyy_zhhzm";
    /**门禁监控权限标识**/
    public static final String APP_PERMISSIONS_DOOR = "app_mjkzh";
    public static final String ZHYY_PERMISSIONS_DOOR = "zhyy_mjgl";
    /**
     * 10F和11F 空调开机时解除面板锁定 关机时锁定空调面板
     */
    public static final int IS_STAFF_AIR_AC = 1; //10F和11F 空调开机时解除面板锁定 关机时锁定空调面板

    public static final String errorMsg = "网络走丢了请稍后再试！";
}
