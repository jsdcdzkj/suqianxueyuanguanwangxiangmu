import request from "@/utils/request";

export function getEntity(data) {
    return request({
        url: "/clockingConfig/getEntity",
        method: "post",
        params: data
    });
}

export function saveOrUpdate(data) {
    return request({
        url: "/clockingConfig/saveOrUpdate",
        method: "post",
        params: data
    });
}
