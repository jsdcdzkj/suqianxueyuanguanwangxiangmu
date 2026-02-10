import request from "@/utils/request";

export function getMeetList(data) {
    return request({
        url: "/config2F/getMeetList",
        method: "get",
        data
    });
}
export function getAirList(data) {
    return request({
        url: "/config2F/getAirList",
        method: "get",
        data
    });
}
export function getDoorList(data) {
    return request({
        url: "/config2F/getDoorList",
        method: "get",
        params: data
    });
}
export function getDoorVideoList(data) {
    return request({
        url: "/config2F/getDoorVideoList",
        method: "get",
        data
    });
}
export function getVideoList(data) {
    return request({
        url: "/config2F/getVideoList",
        method: "get",
        data
    });
}
export function uploadFile(data) {
    return request({
        url: "/config2F/uploadFile",
        method: "post",
        data
    });
}

export function saveOrUpdate(data) {
    return request({
        url: "/config2F/saveOrUpdate",
        method: "get",
        params: data
    });
}
export function getEntity(data) {
    return request({
        url: "/config2F/getEntity",
        method: "get",
        params: data
    });
}
export function getLogoList(data) {
    return request({
        url: "/sys/logo/logoList",
        method: "post",
        data
    });
}
export function updateLogo(data) {
    return request({
        url: "/sys/logo/updateLogo",
        method: "post",
        data
    });
}
