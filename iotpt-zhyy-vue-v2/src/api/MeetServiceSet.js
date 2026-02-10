import request from "@/utils/request";

export function meetServiceList(data) {
    return request({
        url: "/serviceOptions/getServiceOptionPage",
        method: "post",
        params: data
    });
}

export function meetServiceDel(data) {
    return request({
        url: "/serviceOptions/delServiceOption",
        method: "post",
        params: data
    });
}

export function meetServiceSave(data) {
    return request({
        url: "/serviceOptions/addServiceOptions",
        method: "post",
        params: data
    });
}

export function pageListGroup(data) {
    return request({
        url: "/meetingGroup/page",
        method: "post",
        params: data
    });
}

export function pageDelGroup(data) {
    return request({
        url: "/meetingGroup/del",
        method: "post",
        params: data
    });
}

export function pageEditGroup(data) {
    return request({
        url: "/meetingGroup/edit",
        method: "post",
        data
    });
}

export function groupUser(data) {
    return request({
        url: "/meetingGroup/groupUser",
        method: "post",
        params: data
    });
}

export function listByUser(data) {
    return request({
        url: "/meetingGroup/listByUser",
        method: "post",
        params: data
    });
}
