import request from "@/utils/request";

//人员
export function userListDC(data) {
    return request({
        url: "/common/userListDC",
        method: "post",
        data
    });
}

// 部门
export function getDeptListDC(data) {
    return request({
        url: "/common/getDeptListDC",
        method: "post",
        data
    });
}

// 获取流程配置
export function getList(data) {
    return request({
        url: "/changeWork/getList",
        method: "post",
        data
    });
}

// 保存配置
export function saveProcess(data) {
    return request({
        url: "/changeWork/saveProcess",
        method: "post",
        data
    });
}

// 调班管理分页
export function getPage(data) {
    return request({
        url: "/changeWork/getPage",
        method: "post",
        data
    });
}

// 导出
export function exportInfo(data) {
    return request({
        url: "/changeWork/export",
        method: "post",
        data,
        responseType: "blob"
    });
}

// 调班详情
export function detail(data) {
    return request({
        url: "/changeWork/detail",
        method: "post",
        data
    });
}

// 审核人

export function companyDeptUser(data) {
    return request({
        url: "/common/getDeptUserListDC",
        method: "post",
        data
    });
}
