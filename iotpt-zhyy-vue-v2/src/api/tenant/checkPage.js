import request from "@/utils/request";

// 审批管理
export function page(data) {
    return request({
        url: "/merchant/approval/page",
        method: "post",
        data
    });
}

// 审批
export function approval(data) {
    return request({
        url: "/merchant/approval/approval",
        method: "post",
        data
    });
}

// 操作人查询
export function userList(data) {
    return request({
        url: "/common/userList",
        method: "post",
        data
    });
}

export function getValList(data) {
    return request({
        url: "/merchant/getValList",
        method: "get",
        params: data
    });
}

export function exchangeDegree(data) {
    return request({
        url: "/merchant/exchangeDegree",
        method: "post",
        data: data
    });
}

export function exchangeValue(data) {
    return request({
        url: "/merchant/exchange",
        method: "post",
        data: data
    });
}
