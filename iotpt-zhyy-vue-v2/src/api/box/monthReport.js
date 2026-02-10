import request from "@/utils/request";
//月报统计
export function monthlyReportStatistics(data) {
    return request({
        url: "/record/monthlyReportStatistics",
        method: "post",
        params:data
    });
}

// 月报详情
export function monthlyReportDetails(data) {
    return request({
        url: "/record/monthlyReportDetails",
        method: "post",
        params:data
    });
}

// 包厢下拉
export function boxSelect(data) {
    return request({
        url: "/record/boxSelect",
        method: "get",
        params: data
    });
}

// 明细导出
export function monthlyStatisticsExport(data) {
    return request({
        url: "/record/monthlyStatisticsExport",
        method: "get",
        params: data,
        responseType: "blob"
    });
}


// 详情明细导出
export function monthlyStatisticsDetailExport(data) {
    return request({
        url: "/record/monthlyStatisticsDetailExport",
        method: "get",
        params: data,
        responseType: "blob"
    });
}
