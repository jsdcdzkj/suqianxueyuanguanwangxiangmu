import request from "@/utils/request";

export function toiletList(data) {
    return request({
        url: "/toilet/getList",
        method: "post",
        params: data
    });
}

export function areaTreeList(data) {
    return request({
        url: "/toilet/areaTreeList2",
        method: "post"
    });
}

export function getEntity(data) {
    return request({
        url: "/toilet/getEntity",
        method: "post",
        params: data
    });
}

export function getPageList(data) {
    return request({
        url: "/toilet/getPageList",
        method: "post",
        params: data
    });
}

export function saveOrUpdate(data) {
    return request({
        url: "/toilet/saveOrUpdate",
        method: "post",
        data
    });
}

export function delectToilt(data) {
    return request({
        url: "/toilet/delectToilt",
        method: "post",
        params: data
    });
}

export function uploadFile(data) {
    return request({
        url: "/toilet/notice/upload/file",
        method: "post",
        data
    });
}

export function noticeInfo(data) {
    return request({
        url: "/toilet/notice/info",
        method: "post",
        data
    });
}
///toilet/notice/del/file

export function delFile(data) {
    return request({
        url: "/toilet/notice/del/file",
        method: "post",
        params: data
    });
}

// /toilet/notice/edit

export function noticeEdit(data) {
    return request({
        url: "/toilet/notice/edit",
        method: "post",
        data
    });
}
