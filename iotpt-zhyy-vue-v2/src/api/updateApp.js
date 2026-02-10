import request from "@/utils/request";

export function getPageList(data) {
    return request({
        url: "/appVersionInfo/getPageList",
        method: "post",
        data
    });
}
export function saveOrUpdate(data) {
    return request({
        url: "/appVersionInfo/saveOrUpdate",
        method: "post",
        data
    });
}

export function getEntity(data) {
    return request({
        url: "/appVersionInfo/getEntity",
        method: "post",
        data
    });
}
