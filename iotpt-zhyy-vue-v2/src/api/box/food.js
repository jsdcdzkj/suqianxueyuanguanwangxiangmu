import request from "@/utils/request";

export function selectPageList(data) {
    return request({
        url: "/proFood/selectPageList",
        method: "post",
        data
    });
}

export function proFoodSaveOrUp(data) {
    return request({
        url: "/proFood/saveOrUp",
        method: "post",
        data
    });
}

export function editPublish(data) {
    return request({
        url: "/proFood/editPublish",
        method: "get",
        params: data
    });
}

export function deleteFood(data) {
    return request({
        url: "/proFood/deleteFood",
        method: "get",
        params: data
    });
}

export function getEntity(data) {
    return request({
        url: "/proFood/getEntity",
        method: "get",
        params: data
    });
}
