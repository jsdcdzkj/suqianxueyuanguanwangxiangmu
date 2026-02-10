import request from "@/utils/request";

export function getWarnTypeDict(data) {
    return request({
        url: "/warningInfo/warnType",
        method: "post",
        data
    });
}

export function getAlarmList(data) {
    return request({
        url: "/alarm/category/list",
        method: "get",
        params: data
    });
}

export function getPageList(data) {
    return request({
        url: "/warningInfo/getPageList",
        method: "post",
        params: data
    });
}

export function getRealtimePageList(data) {
    return request({
        url: "/warningInfo/getRealtimePage",
        method: "post",
        params: data
    });
}
export function airLog(data) {
    return request({
        url: "/deviceCollect/airLog",
        method: "get",
        params: data
    });
}
export function getRealtimeMap(data) {
    return request({
        url: "/warningInfo/getRealtimeMap",
        method: "get",
        params: data
    });
}

// 老历史告警
export function getHistoryPage(data) {
    return request({
        url: "/warningInfo/getHistoryPage",
        method: "post",
        params: data
    });
}

// 新历史告警
export function getHistoryList(data) {
    return request({
        url: "/alarmRecords/getHistoryList",
        method: "post",
        data
    });
}

export function getEntityByCollectAndChannel(data) {
    return request({
        url: "/warningInfo/getEntityByCollectAndChannel",
        method: "post",
        params: data
    });
}
export function getRealTimeWarnView(data) {
    return request({
        url: "/warningInfo/getRealTimeWarnView",
        method: "post",
        data
    });
}

export function warmCount(data) {
    return request({
        url: "/warningInfo/warmCount",
        method: "post",
        params: data
    });
}

export function load(data) {
    return request({
        url: "/warningInfo/load",
        method: "post",
        params: data
    });
}

export function getDeviceInfo(data) {
    return request({
        url: "/warningInfo/getDeviceInfo",
        method: "post",
        params: data
    });
}

export function heat(data) {
    return request({
        url: "/statistics/heat",
        method: "post",
        params: data
    });
}

//智慧安防 告警中心 统计分析 告警等级统计 饼图
export function getWarningLevel(data) {
    return request({
        url: "/warningInfo/getWarningLevelPie",
        method: "post",
        params: data
    });
}

export function getWarningSignStat(data) {
    return request({
        url: "/warningInfo/getWarningSignStat",
        method: "post",
        data
    });
}

export function getWarningStausPie(data) {
    return request({
        url: "/warningInfo/getWarningStausPie",
        method: "post",
        params: data
    });
}

export function getWarningHandleStausPie(data) {
    return request({
        url: "/warningInfo/getWarningHandleStausPie",
        method: "post",
        params: data
    });
}

export function getWarningDeviceTypePie(data) {
    return request({
        url: "/warningInfo/getWarningDeviceTypePie",
        method: "post",
        params: data
    });
}
// 告警类型统计
export function getWarningType(data) {
    return request({
        url: "/securityWarning/getWarningType.do",
        method: "post",
        params: data
    });
}

export function getWarningAreaPie(data) {
    return request({
        url: "/warningInfo/getWarningAreaPie",
        method: "post",
        params: data
    });
}

export function getWarningTrendChartApi(data) {
    return request({
        url: "/warningInfo/getWarningTrendChart",
        method: "post",
        params: data
    });
}

export function getWarningTrendWarnType() {
    return request({
        url: "/warningInfo/getWarningTrendWarnType",
        method: "post"
    });
}

export function warningReportTo(data) {
    return request({
        url: "/warningInfo/warningReportTo",
        method: "post",
        data
    });
}
// warning5Ignore
export function warning5Ignore(data) {
    return request({
        url: "/warningInfo/warning5Ignore",
        method: "post",
        data
    });
}
export function warning2Ignore(data) {
    return request({
        url: "/warningInfo/warning2Ignore",
        method: "post",
        data
    });
}

// 电气安全报表 关联设备
export function getDeviceGatewayList(data) {
    return request({
        url: "/warningInfo/getListByDeviceId",
        method: "post",
        params: data
    });
}

// 电气安全报表 关联设备 getDetailsByDeviceId
export function getDetailsByDeviceId(data) {
    return request({
        url: "/warningInfo/getDetailsByDeviceId",
        method: "post",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
        },
        params: data
    });
}
export function getDetailsByDeviceIdNew(data) {
    return request({
        url: "/alarmRecords/getDetailsByDeviceId",
        method: "post",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
        },
        params: data
    });
}

export function deviceVideoList(data) {
    return request({
        url: "/warningInfo/deviceVideoList",
        method: "post",
        data: data
    });
}

//
export function getsRtsp(data) {
    return request({
        url: "warningInfo/getsRtsp",
        method: "get",
        params: data
    });
}

// 设备运维

export function devopsPage(data) {
    return request({
        url: "warningInfo/devopsPage",
        method: "get",
        params: data
    });
}
// 智慧安全-统计分析-《设备告警占比》改成《告警等级占比》（警告/次要/重要/紧急）
export function alarmLevel(data) {
    return request({
        url: "/warningInfo/alarmLevel",
        method: "get",
        params: data
    });
}

// 智慧安全-统计分析-《信号告警类型top10》改成《电气安全告警top10》
export function alarmTop(data) {
    return request({
        url: "/warningInfo/alarmTop",
        method: "get",
        params: data
    });
}
// 智慧安全-统计分析-告警类型占比内容改成：人员安全/车辆安全/消防安全/厨房安全
export function alarmType(data) {
    return request({
        url: "/warningInfo/alarmType",
        method: "get",
        params: data
    });
}
// 智慧安全-统计分析-《告警频次趋势》改成《园区安全排险频次》
export function alarmSecurity(data) {
    return request({
        url: "/warningInfo/alarmSecurity",
        method: "get",
        params: data
    });
}
// 区域告警TOP10
export function getWarningAreaTop(data) {
    return request({
        url: "/warningInfo/getWarningAreaTop",
        method: "get",
        params: data
    });
}
// /warningInfo/batchIgnore

export function batchIgnore(data) {
    return request({
        url: "/warningInfo/batchIgnore",
        method: "post",
        data: data
    });
}

export function devopsByDevice(data) {
    return request({
        url: "/warningInfo/devopsByDevice",
        method: "get",
        params: data
    });
}
// 新
export function pieChart(data) {
    return request({
        url: "/alarm/statistics/pieChart",
        method: "post",
        data: data
    });
}

export function rank(data) {
    return request({
        url: "/alarm/statistics/rank",
        method: "post",
        data: data
    });
}
export function disposal(data) {
    return request({
        url: "/alarm/statistics/disposal",
        method: "post",
        data: data
    });
}
export function alarmTrend(data) {
    return request({
        url: "/alarm/statistics/alarmTrend",
        method: "post",
        data: data
    });
}

export function categoryList(data) {
    return request({
        url: "/alarm/category/list",
        method: "get",
        params: data
    });
}

export function alarmNum(data) {
    return request({
        url: "/alarm/statistics/alarmNum",
        method: "post",
        data: data
    });
}

export function alarmGroupList(data) {
    return request({
        url: "/alarm/group/list",
        method: "get",
        params: data
    });
}

export function securityAssessment(data) {
    return request({
        url: "/alarm/statistics/securityAssessment",
        method: "post",
        data: data
    });
}

export function alarmContentPage(data) {
    return request({
        url: "/alarm/content/page",
        method: "post",
        data: data
    });
}

export function deviceTypeList(data) {
    return request({
        url: "/deviceCollect/deviceType",
        method: "post",
        data
    });
}

export function saveAlarmContent(data) {
    return request({
        url: "/alarm/content/save",
        method: "post",
        data
    });
}

export function exportTemplate(data) {
    return request({
        url: "/alarm/content/template",
        method: "get",
        params: data,
        responseType: "blob"
    });
}

export function exportAlarmContent(data) {
    return request({
        url: "/alarm/content/export",
        method: "post",
        data,
        responseType: "blob"
    });
}
export function alarmCategoryPage(data) {
    return request({
        url: "/alarm/category/page",
        method: "post",
        data
    });
}

export function saveAlarmCategory(data) {
    return request({
        url: "/alarm/category/save",
        method: "post",
        data
    });
}

export function alarmGroupPage(data) {
    return request({
        url: "/alarm/group/page",
        method: "post",
        data
    });
}
export function saveAlarmGroup(data) {
    return request({
        url: "/alarm/group/save",
        method: "post",
        data
    });
}

export function alarmPlanPage(data) {
    return request({
        url: "/alarm/plan/page",
        method: "post",
        data
    });
}
export function saveAlarmPlan(data) {
    return request({
        url: "/alarm/plan/save",
        method: "post",
        data
    });
}
export function alarmPlanTimePage(data) {
    return request({
        url: "/alarm/plan/time/page",
        method: "post",
        data
    });
}

// 根据ID查时间模板
export function getById(data) {
    return request({
        url: "/alarm/plan/time/getById",
        method: "get",
        params: data
    });
}

// 新增编辑时间模板
export function saveTime(data) {
    return request({
        url: "/alarm/plan/time/save",
        method: "post",
        data
    });
}

// 删除时间模板
export function delTime(data) {
    return request({
        url: "/alarm/plan/time/delete",
        method: "post",
        data
    });
}

export function batchDel(data) {
    return request({
        url: "/alarm/plan/batchDel",
        method: "post",
        data
    });
}
export function alarmContentList(data) {
    return request({
        url: "/alarm/content/list",
        method: "get",
        params: data
    });
}

export function switchEnable(data) {
    return request({
        url: "/alarm/plan/switch/enable",
        method: "post",
        data
    });
}

export function alarmContentTree(data) {
    return request({
        url: "/alarm/content/tree",
        method: "get",
        params: data
    });
}

export function getOrgList(data) {
    return request({
        url: "/sysDept/getList",
        method: "post",
        data
    });
}
export function getUserTree(data) {
    return request({
        url: "/sysuser/getUserTree.do",
        method: "post",
        data
    });
}
export function teamGroupsTree(data) {
    return request({
        url: "/teamGroups/tree",
        method: "post",
        data
    });
}
export function getPlanById(data) {
    return request({
        url: "/alarm/plan/getById",
        method: "get",
        params: data
    });
}
