package com.jsdc.iotpt.common;

import io.swagger.annotations.ApiModelProperty;

public class RentInfoConstant {
    /**
     * 计租方式 固定租金
     */
    public static final String RENT_MODE_FIXED = "0";      // 固定租金

    /**
     * 计租方式 递增租金
     */
    public static final String RENT_MODE_INCREMENTAL = "1"; // 递增租金


    /**
     * 租金单位    元/㎡*天
     */
    public static final String RENT_UNIT_PER_SQM_DAY = "0";  // 元/㎡*天
    /**
     * 租金单位    元/㎡*月
     */
    public static final String RENT_UNIT_PER_SQM_MONTH = "1";// 元/㎡*月
    /**
     * 租金单位     元/月
     */
    public static final String RENT_UNIT_FIXED_MONTHLY = "2";// 元/月

    /**
     * 不含税
     */
    public static final String TAX_INCLUSION_EXCLUDED = "0";    // 不含税
    /**
     * 含税
     */
    public static final String TAX_INCLUSION_INCLUDED = "1";     // 含税



    /**
     * 计费类型 按月计费
     */
    public static final String BILLING_TYPE_MONTHLY = "0";     // 按月计费
    /**
     * 计费类型 按天计费
     */
    public static final String BILLING_TYPE_DAILY = "1";        // 按天计费


    /**
     * 免租状态  非免租
     */
    public static final String FREERENTSTATUS_NOT_FREE = "0";     // 非免租

    /**
     * 免租状态  非免租
     */
    public static final String  FREERENTSTATUS_FREE = "1";         // 免租


    /**
     * 租金递增方式 百分比递增
     */
    public static final String INCREASEMETHOD_PERCENTAGE = "0";   // 百分比递增
    /**
     * 租金递增方式  固定金额递增
     */
    public static final String INCREASEMETHOD_FIXED_AMOUNT = "1"; // 固定金额递增

//    付款时间类型  0:提前;  1:延后
    public static final String PAY_MODE_EARLY = "0"; // 付款时间类型  0:提前;  1:延后
    public static final String PAY_MODE_LATE = "1"; // 付款时间类型  0:提前;  1:延后

    public static final String  DETAIL_TYPE_DAILY = "daily";//每日明细
    public static final String  DETAIL_TYPE_MONTHLY = "monthly";//每月明细小结
    public static final String  DETAIL_RENT_CYCLE = "rentCycle";//租金周期
    public static final String  DETAIL_PROPERTY_DAILY = "propertyDaily";//物业费明细小结
    public static final String  DETAIL_PROPERTY_FEE_CYCLE = "propertyFeeCycle";// 物业费周期



    /**
     *   0:未到应收期  1:待收款;  2:待审批; 3:已收款; 4:拒绝收款; 5:部分收款  6已退租无需支付;
     */
    //付款状态
    public static final String PAY_STATUS_NOT_REACH_RECEIVABLE_DATE = "0";// 0:未到应收期
    public static final String PAY_STATUS_WAIT_FOR_PAY = "1";//1:待收款;
    public static final String PAY_STATUS_WAIT_FOR_APPROVE = "2";//2:待审批;
    public static final String PAY_STATUS_PAID = "3";//3:已收款
    public static final String PAY_STATUS_REJECT_PAY = "4";//4:拒绝收款;
    public static final String PAY_STATUS_PARTIAL_PAY = "5";    //部分收款
    public static final String PAY_STATUS_NOT_NEED_PAY = "6";//6已退租无需支付;




    //getFeeCategory 0:物业费; 1:停车费;  2:租金 ;  3:杂费 ;  4: 场地使用费 ;  5:卫生费 ;  6:空调费;  7:活动费;  8:维修费;  9:水电费;  10:公共能源费;  11:租金保证金; 12:物业费保证金; 13:空调电费; 14:水费;   100:其他
    public static final String FEE_CATEGORY_PROPERTY_FEE = "0";//0:物业费
    public static final String FEE_CATEGORY_PARKING_FEE = "1";//1:停车费;
    public static final String FEE_CATEGORY_RENT = "2";//  2:租金 ;

    public static final String FEE_CATEGORY_MISCELLANEOUS_FEE = "3";//3:杂费 ;
    public static final String FEE_CATEGORY_SITE_USAGE_FEE = "4";// 4: 场地使用费
    public static final String FEE_CATEGORY_HYGIENE_FEE = "5";//5:卫生费 ;
    public static final String FEE_CATEGORY_AIRCONDITIONING_FEE = "6";//6:空调费;
    public static final String FEE_CATEGORY_ACTIVITY_FEE = "7";// 7:活动费;
    public static final String FEE_CATEGORY_REPAIR_FEE = "8";// 8:维修费
    public static final String FEE_CATEGORY_ELECTRICITY_FEE = "9";//9:水电费;
    public static final String FEE_CATEGORY_PUBLIC_ENERGY_FEE = "10";//10:公共能源费;
    public static final String FEE_CATEGORY_RENT_MARGIN = "11";//11:租金保证金;
    public static final String FEE_CATEGORY_PROPERTY_MARGIN = "12";//12:物业费保证金;
    public static final String FEE_CATEGORY_AIRCONDITIONING_ELECTRICITY_FEE = "13";//13:空调电费;
    public static final String FEE_CATEGORY_WATER_FEE = "14";// 14:水费;
    public static final String FEE_CATEGORY_OTHER = "100";// 100:其他


}
