import request from "@/utils/request";

export function saveVisitor(data) {
    return request({
        url: "/visitor/saveVisitor",
        method: "post",
        data
    });
}

export function getVisitorNumAndTodayVisitor(data) {
    return request({
        url: "/visitor/getVisitorNumAndTodayVisitor",
        method: "post",
        params: data
    });
}

export function approve(data) {
    return request({
        url: "/visitor/approveVisitor",
        method: "post",
        data
    });
}

export function visitorManager(data) {
    return request({
        url: "/visitor/visitorManager",
        method: "post",
        params: data
    });
}

export function visitorList(data) {
    return request({
        url: "/visitor/visitorList",
        method: "post",
        data
    });
}

export function getDeptList() {
    return request({
        url: "/visitor/getDeptList",
        method: "post"
    });
}

export function getUnitList() {
    return request({
        url: "/visitor/getUnitList",
        method: "post"
    });
}

//上传图片
export function uploadFile(data) {
    return request({
        url: "/visitor/uploadFile.do",
        method: "post",
        data
    });
}

export function delPhoto(data) {
    return request({
        url: "/visitor/delPhoto.do",
        method: "post",
        params: data
    });
}

//上传图片
export function trackingPhoto(data) {
    return request({
        url: "/visitor/trackingPhoto",
        method: "post",
        data
    });
}

export function updatePhoto(data) {
    return request({
        url: "/visitor/updatePhoto",
        method: "post",
        data
    });
}

export function getDetails(data) {
    return request({
        url: "/visitor/getDetails",
        method: "post",
        data
    });
}

// 访客记录-轨迹追踪
export function tracking(data) {
    return request({
        url: "/visitor/tracking",
        method: "post",
        data
    });
}
