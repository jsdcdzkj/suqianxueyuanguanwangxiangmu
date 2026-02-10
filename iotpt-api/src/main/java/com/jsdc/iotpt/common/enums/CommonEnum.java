package com.jsdc.iotpt.common.enums;

public class CommonEnum {
    public enum PersonSubscribeRecordSched {
        UPLOADED("已经上传报告", "已经上传报告");

        PersonSubscribeRecordSched(String name, String describe) {
        }
    }


    public static String COLLECT = "汇总";//汇总
    public static String EMPLOYEECLINIC = "职工门诊（含单病种）";//职工门诊（含单病种）
    public static String EMPLOYEEHOSPITALIZATION = "职工住院（含单病种含家床含按床日）";//职工住院（含单病种含家床含按床日）
    public static String RESIDENTCLINIC = "居民门诊（含单病种）";//居民门诊（含单病种）
    public static String RESIDENTHOSPITALIZATION = "居民住院（含单病种含家床含按床日）";//居民住院（含单病种含家床含按床日）
    public static String DISABILITYCLINIC = "伤残门诊";//伤残门诊
    public static String HOSPITALIZATIONFORDISABILITY = "伤残住院";//伤残住院
    public static String GIVEBIRTHTO = "职工、灵活就业人员生育";//职工、灵活就业人员生育
    public static String RESIDENTBIRTH = "居民生育";//居民生育
    public static String RETIRE = "离休";//离休

    //我的任务-任务上报-图片
    public static String MISSION_SB_FILE= "4";
    //我的任务-巡检异常-处理时上传图片
    public static String MISSION_CL_FILE = "11";
}
