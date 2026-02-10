import request from "@/utils/request";

/**
 * 智慧应用 电气安全
 */

/**
 * 电气安全分析
 */
export function electricalSafetyAnalysis(data) {
    return request({
        url: "/electricalSafetyReport/electricalSafetyAnalysis",
        method: "post",
        params: data
    });
}

/**
 * 电气安全报表
 */
/* export function electricalSafetyReport(data) {
  return request({
    url: '/electricalSafetyReport/electricalSafetyReport',
    method: 'post',
    params: data
  })
} */
export function electricalSafetyReport(data) {
    return request({
        url: "/statistics/exportList",
        method: "get",
        params: data
    });
}

/**
 * 电气安全报表导出
 */
export function electricalSafetyExport(data) {
    return request({
        url: "/electricalSafetyReport/electricalSafetyExport",
        method: "get",
        params: data,
        responseType: "blob"
    });
}

/**
 * 电气安全报表导出New
 */
export function electricalSafetyExportNew(data) {
    return request({
        url: "/statistics/export",
        method: "get",
        params: data,
        responseType: "blob"
    });
}
