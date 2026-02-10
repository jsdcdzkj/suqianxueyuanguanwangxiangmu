import request from "@/utils/request";

// 商户列表
export function findMerchant(data) {
    return request({
        url: "/investmentPerson/findMerchant",
        method: "get",
        params:data
    });
}

// 组织机构详情
export function info(data) {
    return request({
        url: "/investmentPerson/info?id=",
        method: "post",
        params:data
    });
}

// 移除招商人员
export function delInvestmentPerson(data) {
    return request({
        url: "/investmentPerson/delInvestmentPerson",
        method: "post",
        data
    });
}

// 全部人员列表
export function getUserPage(data) {
    return request({
        url: "/investmentPerson/getUserPage.do",
        method: "post",
        params: data
    });
}

// 查询商户合同
export function contract(data) {
    return request({
        url: "/merchant/contract",
        method: "get",
        params: data
    });
}

// 查询商户电表状态
export function state(data) {
    return request({
        url: "/merchant/electricity/state",
        method: "get",
        params: data
    });
}

// 获取电表是否显示正常
export function warningState(data) {
    return request({
        url: "/merchant/electricity/warning/state",
        method: "get",
        params: data
    });
}

// 查询商户电表价格
export function price(data) {
    return request({
        url: "/merchant/electricity/price",
        method: "get",
        params: data
    });
}

// 查询当前水价
export function waterPrice(data) {
    return request({
        url: "/merchant/water/price",
        method: "get",
        params: data
    });
}

// 电价设置
export function priceSave(data) {
    return request({
        url: "/merchant/electricity/price/save",
        method: "post",
        data
    });
}

// 水价设置
export function waterPriceSave(data) {
    return request({
        url: "/merchant/water/price/save",
        method: "post",
        data
    });
}

// 查询电价历史
export function priceHistory(data) {
    return request({
        url: "/merchant/electricity/price/history",
        method: "post",
        data
    });
}

// 查询水价历史
export function waterPriceHistory(data) {
    return request({
        url: "/merchant/water/price/history",
        method: "post",
        data
    });
}

// 查询商户配置详情
export function configDetail(data) {
    return request({
        url: "/merchant/config/detail",
        method: "get",
        params: data
    });
}

// 修改租户配置保存
export function configSave(data) {
    return request({
        url: "/merchant/config/save",
        method: "post",
        data
    });
}

// 查询商户累计购电次数、预充值统计
export function prepaidStatistics(data) {
    return request({
        url: "/merchant/prepaid/statistics",
        method: "get",
        params: data
    });
}

// 查询空调费用

export function airfee(data) {
    return request({
        url: "/merchant/air/conditioner/fee",
        method: "get",
        params: data
    });
}

// 查询当月水量
export function waterconsumption(data) {
    return request({
        url: "/merchant/water/consumption",
        method: "get",
        params: data
    });
}

// 充值
export function recharge(data) {
    return request({
        url: "/merchant/prepaid/recharge",
        method: "post",
        data
    });
}

// 退款
export function refund(data) {
    return request({
        url: "/merchant/prepaid/refund",
        method: "post",
        data
    });
}

// 充值记录分页
export function prepaidRechargePage(data) {
    return request({
        url: "/merchant/prepaid/recharge/page",
        method: "post",
        data
    });
}

//账单分页查询
export function prepaidBillPage(data) {
    return request({
        url: "/merchant/prepaid/bill/page",
        method: "post",
        data
    });
}

// 商户预警分页查询
export function warningPage(data) {
    return request({
        url: "/merchant/warning/page",
        method: "post",
        data
    });
}

//账单分页查询
export function billPage(data) {
    return request({
        url: "/merchant/bill/page",
        method: "post",
        data
    });
}

// 电表统计
export function electricityStatistics(data) {
    return request({
        url: "/merchant/electricity/statistics",
        method: "get",
        params: data
    });
}

// 空调统计
export function airStatistics(data) {
    return request({
        url: "/merchant/air/conditioner/statistics",
        method: "get",
        params: data
    });
}

// 水表统计
export function waterStatistics(data) {
    return request({
        url: "/merchant/water/statistics",
        method: "get",
        params: data
    });
}

// 预充值账单分页查询
export function prepaidMonthlyBillPage(data) {
    return request({
        url: "/prepaid/monthly/bill/page",
        method: "post",
        data
    });
}

// 根据商户查询房间设备
export function roomDeviceByOrg(data) {
    return request({
        url: "/hire/room/device/byOrg",
        method: "post",
        data
    });
}

// 文件上传
export function returnFile(data) {
    return request({
        url: "/minio/importFile/returnFile",
        method: "post",
        data
    });
}

// 控制电表合闸断闸
export function elSwitch(data) {
    return request({
        url: "/merchant/electricity/switch",
        method: "post",
        data
    });
}

// 查询商户上月电水统计
export function monthStatistics(data) {
    return request({
        url: "/merchant/last/month/statistics",
        method: "get",
        params: data
    });
}

// 下载合同
export function downloadContract(data) {
    return request({
        url: "/merchant/contract/download",
        method: "get",
        params: data,
        responseType: 'blob'
    });
}

// 判断是否绑定了设备
export function deviceBindCheck(data) {
    return request({
        url: "/hire/room/device/byOrg",
        method: "post",
        data
    });
}

