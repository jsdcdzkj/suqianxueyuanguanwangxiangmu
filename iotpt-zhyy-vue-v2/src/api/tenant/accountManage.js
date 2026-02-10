import request from "@/utils/request";

// 账单查询
export function page(data) {
    return request({
        url: "/combinedPaymentBill/page",
        method: "post",
        data
    });
}

// 账单查询
export function billPage(data) {
    return request({
        url: "/customizedBill/page",
        method: "post",
        data
    });
}

// 组合收款保存
export function save(data) {
    return request({
        url: "/combinedPaymentBill/save",
        method: "post",
        data
    });
}

// 账单看板-组合收款上面的list选中的账单详情
export function getCustomizedBillByIds(data) {
    return request({
        url: "/customizedBill/getCustomizedBillByIds",
        method: "post",
        data
    });
}

// 组合收款的详情页面
export function details(data) {
    return request({
        url: "/combinedPaymentBill/details",
        method: "post",
        data
    });
}

// 添加自定义账单
export function saveCustomizedBill(data) {
    return request({
        url: "/customizedBill/saveCustomizedBill",
        method: "post",
        data
    });
}

// 删除自定义账单
export function delCustomizedBill(data) {
    return request({
        url: "/customizedBill/delCustomizedBill",
        method: "post",
        data
    })
}


