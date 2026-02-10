import request from "@/utils/request";

// 合同列表
export function page(data) {
    return request({
        url: "/tenant/contract/page",
        method: "post",
        data
    });
}

// 合同统计
export function statistics(data) {
    return request({
        url: "/tenant/contract/statistics",
        method: "post",
        data
    });
}

// 详情
export function detail(data) {
    return request({
        url: "/tenant/contract/detail",
        method: "post",
        data
    });
}

// 新增或者编辑
export function save(data) {
    return request({
        url: "/tenant/contract/save",
        method: "post",
        data
    });
}

// 房间列表
export function list(data) {
    return request({
        url: "/hireRoom/list",
        method: "post",
        data
    });
}

// 房间树形结构
export function buildTreeListTenantContract(data) {
    return request({
        url: "/tenant/contract/buildTreeListTenantContract",
        method: "get",
        params: data
    });
}

// 合同状态数
export function contractStatusTree(data) {
    return request({
        url: "/tenant/contract/contractStatusTree",
        method: "get",
        params: data
    });
}

// 退租信息回显
export function exitTenantMsg(data) {
    return request({
        url: "/tenant/contract/exitTenantMsg",
        method: "post",
        data
    });
}

// 租金预览
export function rentPreview(data) {
    return request({
        url: "/tenant/contract/rentPreview",
        method: "post",
        data
    });
}

// 物业费预览
export function propertyFeesPreview(data) {
    return request({
        url: "/tenant/contract/previewOfPropertyFees",
        method: "post",
        data
    });
}

// 退租回显
export function exitTenantPreview(data) {
    return request({
        url: "/tenant/contract/exitTenantMsg",
        method: "post",
        data
    });
}

export function continueTenant(data) {
    return request({
        url: "/tenant/contract/continueTenant",
        method: "post",
        data
    });
}

//退租
export function exitTenant(data) {
    return request({
        url: "/tenant/contract/exitTenant",
        method: "post",
        data
    });
}

// 撤回
export function withdraw(data) {
    return request({
        url: "/tenant/contract/withdraw",
        method: "post",
        data
    });
}

// 删除
export function del(data) {
    return request({
        url: "/tenant/contract/del",
        method: "post",
        data
    });
}

//作废
export function invalid(data) {
    return request({
        url: "/tenant/contract/invalid",
        method: "post",
        data
    });
}

export function calculationLeaseTerminationBill(data) {
    return request({
        url: "/tenant/contract/calculationLeaseTerminationBill",
        method: "post",
        data
    });
}

export function contractChange(data) {
    return request({
        url: "/tenant/contract/change",
        method: "post",
        data
    });
}
