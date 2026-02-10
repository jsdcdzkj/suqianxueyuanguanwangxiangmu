package com.jsdc.iotpt.common;

/**
 * AppMessageTemplateCodeContonts
 *
 * @author 许森森
 * @date 2024/11/20
 */
public class AppMessageConstants {

    private static final String SYSTEM_NEWS = "SYSTEM_NEWS"; // 新闻

    private static final String SYSTEM_REPAST_NOTICE = "SYSTEM_REPAST_NOTICE"; // 报餐提醒

    private static final String SYSTEM_ATTEND_MEETING = "SYSTEM_ATTEND_MEETING"; // 会议参会通知

    private static final String SYSTEM_GYM_CHECK_SUCCESS = "SYSTEM_GYM_CHECK_SUCCESS"; // 健身房审核结果

    private static final String SYSTEM_GYM_CHECK_REJECT = "SYSTEM_GYM_CHECK_REJECT"; // 健身房审核结果

    private static final String SYSTEM_REPASTROOM_CHECK_SUCCESS = "SYSTEM_REPASTROOM_CHECK_SUCCESS"; // 包厢预定告知业务审核结果

    private static final String SYSTEM_REPASTROOM_CHECK_REJECT = "SYSTEM_REPASTROOM_CHECK_REJECT"; // 包厢预定告知业务审核结果

    private static final String SYSTEM_REPASTROOM_PREPARE = "SYSTEM_REPASTROOM_PREPARE"; // 包厢预定通过后物业告知备餐

    private static final String SYSTEM_REPAST_STATISTICS = "SYSTEM_REPAST_STATISTICS"; // 包厢消费结果及第二天报餐结果

    private static final String SYSTEM_VISITORS_ARRIVAL = "SYSTEM_VISITORS_ARRIVAL"; // 访客到达通知

    private static final String SYSTEM_REPAIRS_RESULT = "SYSTEM_REPAIRS_RESULT"; // 报事报修处理结果

    private static final String SYSTEM_REPAIRS_NOTICE = "SYSTEM_REPAIRS_NOTICE"; // 报事工单处理提醒（组长开启后）

    private static final String SYSTEM_MEETING_END_DEVICE = "SYSTEM_MEETING_END_DEVICE"; // 会议室结束后监测到有设备未关闭

    private static final String SYSTEM_MEETING_START_PERSON = "SYSTEM_MEETING_START_PERSON"; // 会议开启15分钟后监测到无人

    private static final String SYSTEM_CERTIFICATE_EXPIRATION = "SYSTEM_CERTIFICATE_EXPIRATION"; // 证书到期提醒

    private static final String TEAMWORK_GYM_CHECK = "TEAMWORK_GYM_CHECK"; // 通知审核健身房

    private static final String TEAMWORK_REPASTROOM_CHECK = "TEAMWORK_REPASTROOM_CHECK"; // 通知审核包厢预定

    private static final String TEAMWORK_VISITORS_CHECK = "TEAMWORK_VISITORS_CHECK"; // 通知审核访客预约

    private static final String TEAMWORK_REPAIRS_ASSIGN = "TEAMWORK_REPAIRS_ASSIGN"; // 通知审核上报工单（指派到维修组）

    private static final String TEAMWORK_REPAIRS_TOSOLVE = "TEAMWORK_REPAIRS_TOSOLVE"; // 通知响应（开启）被指派的工单

    private static final String ITSS_REPAIRS_NOTICE = "ITSS_REPAIRS_NOTICE"; // 工单响应提醒

    private static final String ITSS_REPAIRS_TIMEOUT = "ITSS_REPAIRS_TIMEOUT"; // 工单超时提醒

    private static final String CAR_USECAR_CHECK = "CAR_USECAR_CHECK"; // 用车审核通知

    private static final String CAR_USECAR_CHECKED = "CAR_USECAR_CHECKED"; // 申请人用车结果通知

    private static final String CAR_USECAR_RETURN = "CAR_USECAR_RETURN"; // 申请人用车结果通知

    private static final String CAR_USECAR_REJECT = "CAR_USECAR_REJECT"; // 申请人用车结果通知

    private static final String CAR_USECAR_ASSIGNED = "CAR_USECAR_ASSIGNED"; // 申请人用车结果通知

    private static final String CAR_USECAR_ASSIGN = "CAR_USECAR_ASSIGN"; // 车辆管理员

}
