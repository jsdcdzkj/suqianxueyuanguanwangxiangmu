import request from "@/utils/request";

//分页查询
export function customerPage(data) {
    return request({
        url: "/customer/page",
        method: "post",
        data
    });
}

// 客户数量统计
export function onCustomerCount(data) {
    return request({
        url: "/customer/onCustomerCount",
        method: "POST",
        data
    });
}
// /customer/save

export function customerSave(data) {
    return request({
        url: "/customer/save",
        method: "POST",
        data
    });
}

///customer/edit

export function customerEdit(data) {
    return request({
        url: "/customer/edit",
        method: "POST",
        data
    });
}

// /customer/del
export function customerDel(data) {
    return request({
        url: "/customer/del",
        method: "POST",
        data
    });
}

// /customer/view
export function customerView(data) {
    return request({
        url: "/customer/view",
        method: "POST",
        data
    });
}

// /customer/toFollowUp
export function customerToFollowUp(data) {
    return request({
        url: "/customer/toFollowUp",
        method: "POST",
        data
    });
}

// /customer/toSpecifyAllocation
export function customerToSpecifyAllocation(data) {
    return request({
        url: "/customer/toSpecifyAllocation",
        method: "POST",
        data
    });
}

// /customer/toWebSpecifyAllocation
export function customerToWebSpecifyAllocation(data) {
    return request({
        url: "/customer/toWebSpecifyAllocation",
        method: "POST",
        data
    });
}

// /customer/webAdd
export function customerWebAdd(data) {
    return request({
        url: "/customer/webAdd",
        method: "POST",
        data
    });
}

// /customer/upload
export function customerUpload(data) {
    return request({
        url: "/customer/upload",
        method: "POST",
        data
    });
}
// /investmentPerson/investmentPersonAll
export function investmentPersonAll() {
    return request({
        url: "/investmentPerson/investmentPersonAll",
        method: "POST"
    });
}

// /customer/toExitHighSeas
export function customerToExitHighSeas(data) {
    return request({
        url: "/customer/toExitHighSeas",
        method: "POST",
        data
    });
}
