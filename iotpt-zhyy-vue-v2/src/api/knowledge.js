import request from "@/utils/request";

//分页
export function getPage(data) {
    return request({
        url: "/knowledge/getPage",
        method: "post",
        params: data
    });
}
// 获取详情
export function getDetails(data) {
    return request({
        url: "/knowledge/getDetails",
        method: "post",
        params: data
    });
}

// 增删改
export function saveOrUpdate(data) {
    return request({
        url: "/knowledge/saveOrUpdate",
        method: "post",
        data: data
    });
}

// 分页查询退出 Slip 列表
export function getPcPage(data) {
    return request({
        url: "/exit/getPcPage",
        method: "post",
        data: data
    });
}
export function findOrg(data) {
    return request({
        url: "/exit/findOrg",
        method: "post",
        data: data
    });
}
