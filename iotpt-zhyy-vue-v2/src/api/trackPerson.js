import request from "@/utils/request";

export function findRecord(data) {
    return request({
        url: "/trackPerson/findRecord",
        method: "post",
        params: data
    });
}

export function getPageList(data) {
    return request({
        url: "/trackPerson/getPageList",
        method: "post",
        params: data
    });
}

export function faceSearchSync(data) {
    return request({
        url: "/trackPerson/faceSearchSync",
        method: "post",
        data
    });
}

// export function getRTSPAddr(id) {
//     return request({
//         url: "/car/getRTSPAddr?id=" + id,
//         method: "post"
//     });
// }
export function getRTSPAddr(data) {
    return request({
        url: "/warningInfo/viewVideo",
        method: "post",
        params: data
    });
}
export function getCount(data) {
    return request({
        url: "/warningInfo/todayOrYearVideoWarn",
        method: "post",
        params: data
    });
}
export function warnMonth(data) {
    return request({
        url: "/warningInfo/warnMonth",
        method: "post",
        params: data
    });
}
export function warnPerson(data) {
    return request({
        url: "/warningInfo/reportType",
        method: "post",
        params: data
    });
}

// 紧急情况 特殊处理 后期请删除
export function getPerPageList(data) {
    return request({
        url: "/warningInfo/getVideoWarnPage",
        method: "post",
        params: data
    });
}

export function getPageListAll(data) {
    return request({
        url: "/dVideo/getPageListAll",
        method: "post",
        params: data
    });
}

// 告警设备
export function warnDeviceSelect(data) {
    return request({
        url: "/warningInfo/warnDeviceSelect",
        method: "post",
        params: data
    });
}

export function getUserById(data) {
    return request({
        url: "/trackPerson/getUserById",
        method: "post",
        params: data
    });
}

export function getRedisDictList(data) {
    return request({
        url: "/sysDict/getRedisDictList",
        method: "post",
        params: data
    });
}
