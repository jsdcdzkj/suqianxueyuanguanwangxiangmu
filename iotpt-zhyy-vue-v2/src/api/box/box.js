import request from "@/utils/request";
//包厢新增、编辑
export function saveOrUp(data) {
    return request({
        url: "/proBox/saveOrUp",
        method: "post",
        data
    });
}

export function selectPageList(data) {
    return request({
        url: "/proBox/selectPageList",
        method: "post",
        data: data
    });
}

export function getEntity(data) {
    return request({
        url: "/proBox/getEntity",
        method: "get",
        params: data
    });
}
// 包厢删除
export function deleteBox(data) {
    return request({
        url: "/proBox/deleteBox",
        method: "get",
        params: data
    });
}
// v包厢发布状态修改
export function editPublish(data) {
    return request({
        url: "/proBox/editPublish",
        method: "get",
        params: data
    });
}

// v包厢发布状态修改
export function importFile(data) {
    return request({
        url: "/minio/importFile",
        method: "get",
        data: data
    });
}

export function saveOrUpdConfig(data) {
    return request({
        url: "/arrange/saveOrUpdConfig",
        method: "post",
        data: data
    });
}

export function detailsFood(data) {
    return request({
        url: "/arrange/detailsFood",
        method: "post",
        data: data
    });
}

export function selectBoxPageList(data) {
    return request({
        url: "/proBoxOrder/selectBoxList",
        method: "post",
        data: data
    });
}

export function selectBoxOrderPageList(data) {
    return request({
        url: "/proBoxOrder/selectBoxOrderPageList",
        method: "post",
        data: data
    });
}

export function exportBoxOrder(data) {
    return request({
        url: "/proBoxOrder/exportBoxOrder",
        method: "post",
        data: data,
        responseType: "blob"
    });
}

export function auditBoxOrder(data) {
    return request({
        url: "/proBoxOrder/auditBoxOrder",
        method: "post",
        data: data
    });
}

// /proBoxOrder/getBoxOrderInfo

export function getBoxOrderInfo(data) {
    return request({
        url: "/proBoxOrder/getBoxOrderInfo",
        method: "get",
        params: data
    });
}

export function auditAddFoodBoxOrder(data) {
    return request({
        url: "/proBoxOrder/auditAddFoodBoxOrder",
        method: "post",
        data: data
    });
}

// 添加回复或者备注
export function editOrder(data) {
    return request({
        url: "/proBoxOrder/editOrder",
        method: "post",
        data: data
    });
}
// 添加回复或者备注
export function exportDishByOrderId(data) {
    return request({
        url: "/proBoxOrder/exportDishByOrderId",
        method: "post",
        data: data,
        responseType: "blob"
    });
}
