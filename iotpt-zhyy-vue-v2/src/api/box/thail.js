import request from "@/utils/request";

export function proThaliSave(data) {
    return request({
        url: "/proThali/saveOrUp",
        method: "post",
        data
    });
}

export function selectPageList(data) {
    return request({
        url: "/proThali/selectPageList",
        method: "post",
        data
    });
}

export function getThali(data) {
    return request({
        url: "/proThali/getThali",
        method: "get",
        params: data
    });
}
export function deleteThali(data) {
    return request({
        url: "/proThali/deleteThali",
        method: "get",
        params: data
    });
}
