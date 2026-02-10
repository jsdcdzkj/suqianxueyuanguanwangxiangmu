import request from "@/utils/request";

export function getList(params) {
    return request({
        url: "/device/lift/getDeviceLiftPageList",
        method: "get",
        params
    });
}

export function getDetail(params) {
    return request({
        url: "/device/lift/getDeviceLift",
        method: "get",
        params
    });
}

export function totalInfo(params) {
    return request({
        url: "/device/lift/statistics",
        method: "get",
        params
    });
}

export function totalInfo2(params) {
    return request({
        url: "/device/lift/statistics2",
        method: "get",
        params
    });
}

// 告警分类统计
export function getWarnInfoCount(params) {
    return request({
        url: "/device/lift/getWarnInfoCount",
        method: "get",
        params
    });
}
// 告警处理情况
export function getWarnInfoCount1(params) {
    return request({
        url: "/device/lift/getWarnInfoCount1",
        method: "get",
        params
    });
}
//告警分布
export function getWarnInfoAllCount(params) {
    return request({
        url: "/device/lift/getWarnInfoAllCount",
        method: "get",
        params
    });
}
// 告警时段发生
export function getWarnInfoTime(params) {
    return request({
        url: "/device/lift/getWarnInfoTime",
        method: "get",
        params
    });
}
// 近七日告警趋势
export function getWarnInfoDay(params) {
    return request({
        url: "/device/lift/getWarnInfoDay",
        method: "get",
        params
    });
}
export function getReport(params) {
    return request({
        url: "/device/lift/getInformationReporting",
        method: "get",
        params
    });
}

export function getWorkData(params) {
    return request({
        url: "/device/lift/getDeviceLiftStatic",
        method: "get",
        params
    });
}

export function ignore(params) {
    return request({
        url: "/device/lift/warning2Ignore",
        method: "get",
        params
    });
}
