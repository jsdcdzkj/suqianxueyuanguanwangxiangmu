import request from "@/utils/request";

export function getList(data) {
    return request({
        url: "/chint/device/point/page",
        method: "post",
        data
    });
}

export function totalInfo(params) {
    return request({
        url: "/chint/device/point/statistics",
        method: "post",
        params
    });
}

export function oneControl(data) {
    return request({
        url: "/chint/device/point/switchByPoint",
        method: "post",
        data
    });
}

export function areaControl(params) {
    return request({
        url: "/chint/device/point/switchBySpace",
        method: "get",
        params
    });
}

export function allControl(params) {
    return request({
        url: "/chint/device/point/master",
        method: "get",
        params
    });
}

export function radarControl(params) {
    return request({
        url: "/chint/device/point/radar",
        method: "get",
        params
    });
}
