import request from "@/utils/request";

export function statistics(data) {
    return request({
        url: "/tenant/payment/bill/statistics",
        method: "post",
        data
    });
}
export function trend(data) {
    return request({
        url: "/tenant/electricity/trend",
        method: "post",
        data
    });
}
export function isOnOff(params) {
    return request({
        url: "/tenant/room/device/isOnOff",
        method: "get",
        params
    });
}

export function pageTenant(data) {
    return request({
        url: "/tenant/payment/bill/page",
        method: "post",
        data
    });
}
export function pageWarnning(data) {
    return request({
        url: "/tenant/warning/page",
        method: "post",
        data
    });
}
export function tenantTree(params) {
    return request({
        url: "/tenant/tree",
        method: "get",
        params
    });
}
export function statusDevice(params) {
    return request({
        url: "/tenant/room/device/status",
        method: "get",
        params
    });
}
export function tenantList(params) {
    return request({
        url: "/tenant/list",
        method: "get",
        params
    });
}
export function saveTenant(data) {
    return request({
        url: "/tenant/save",
        method: "post",
        data
    });
}
export function exitRoom(data) {
    return request({
        url: "/tenant/exit",
        method: "post",
        data
    });
}
export function recharge(data) {
    return request({
        url: "/tenant/payment/bill/recharge",
        method: "post",
        data
    });
}
export function refund(data) {
    return request({
        url: "/tenant/payment/bill/refund",
        method: "post",
        data
    });
}
export function getSwitchPwd(data) {
    return request({
        url: "/tenant/getSwitchPwd",
        method: "post",
        data
    });
}
export function getRoomDeviceList(data) {
    return request({
        url: "/tenant/room/device/list",
        method: "post",
        data
    });
}

export function saveConfigElectric(data) {
    return request({
        url: "/tenant/electricity/config/save",
        method: "post",
        data
    });
}
export function byTenantElectric(params) {
    return request({
        url: "/tenant/electricity/config/byTenant",
        method: "get",
        params
    });
}
export function statisticsElectric(params) {
    return request({
        url: "/tenant/electricity/statistics",
        method: "get",
        params
    });
}

export function statisticsRoom(params) {
    return request({
        url: "/tenant/payment/bill/room/statistics",
        method: "get",
        params
    });
}

export function byId(params) {
    return request({
        url: "/tenant/byId",
        method: "get",
        params
    });
}
export function roomListNot(params) {
    return request({
        url: "/tenant/room/list",
        method: "get",
        params
    });
}

export function electricityNotList(params) {
    return request({
        url: "/tenant/room/device/electricity/list",
        method: "get",
        params
    });
}
export function byTenant(params) {
    return request({
        url: "/tenant/room/byTenant",
        method: "get",
        params
    });
}
//上传文件
export function uploadFile(data) {
    return request({
        url: "/tenant/upload",
        method: "post",
        data
    });
}

// 充值文件上传
export function uploadFileRecharge(data) {
    return request({
        url: "/tenant/payment/bill/uploadFile.do",
        method: "post",
        data
    });
}
// 模板导入
export function importData(data) {
    return request({
        url: "/car/importData",
        method: "post",
        params: data
    });
}
// 模板下载
export function exportData(data) {
    return request({
        url: "/car/export",
        method: "post",
        data,
        responseType: "blob"
    });
}

