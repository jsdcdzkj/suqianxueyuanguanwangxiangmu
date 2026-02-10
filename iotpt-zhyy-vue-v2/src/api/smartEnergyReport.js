import request from "@/utils/request";

/**
 * 智慧应用 智慧能源 所有报表接口
 */

/**
 * 设备分项
 */
export function getDeviceSubitemList(data) {
    return request({
        url: "/smartEnergyReport/getDeviceSubitemList",
        method: "post",
        params: data
    });
}

/**
 * 设备分项
 */
export function updSubitem(data) {
    return request({
        url: "smartEnergyReport/updSubitem",
        method: "post",
        params: data
    });
}

/**
 * 查询设备类型列表
 */
export function getDeviceTypeList(data) {
    return request({
        url: "/smartEnergyReport/getDeviceTypeList",
        method: "post",
        params: data
    });
}

/**
 * 查询所有设备类型状态
 */
export function getAllDeviceStatus(params) {
    return request({
        url: "/warningInfo/devops",
        method: "get",
        params
    });
}

/**
 * 多维分析
 */
export function multidimensionalAnalysis(data) {
    return request({
        url: "/smartEnergyReport/multidimensionalAnalysis",
        method: "post",
        data
    });
}

/**
 * 区域
 */
export function areaTreeList(data) {
    return request({
        url: "/smartEnergyReport/areaTreeList",
        method: "post",
        params: data
    });
}

/**
 * 区域 楼宇楼层+总表分表
 */
export function areaTreeZfList(data) {
    return request({
        url: "/smartEnergyReport/areaTreeZfList",
        method: "post",
        params: data
    });
}
/**
 * 逻辑位置 任意层级可选择
 * 区域
 */
export function areaTreeList2(data) {
    return request({
        url: "/smartEnergyReport/areaTreeList2",
        method: "post",
        data
    });
}

/**
 * 逻辑位置 楼宇楼层
 */
export function buildFloorTreeList(data) {
    return request({
        url: "/smartEnergyReport/buildFloorTreeList",
        method: "post",
        data
    });
}

/**
 * 采集终端设备
 */
export function getDeviceCollectList(data) {
    return request({
        url: "/smartEnergyReport/getDeviceCollectList",
        method: "post",
        params: data
    });
}
export function getDeviceCollectListNew(data) {
    return request({
        url: "/smartEnergyReport/getDeviceCollectListNew",
        method: "post",
        data: data
    });
}

/**
 * 明细分析
 */
export function deviceDetailsAnalysis(data) {
    return request({
        url: "/smartEnergyReport/deviceDetailsAnalysis",
        method: "post",
        params: data
    });
}

/**
 * 明细分析
 */
export function deviceDetailsAnalysis2(data) {
    return request({
        url: "/smartEnergyReport/deviceDetailsAnalysis2",
        method: "post",
        params: data
    });
}

/**
 * 能耗报表
 */
export function analysis(data) {
    return request({
        url: "/smartEnergyReport/analysis",
        method: "post",
        data
    });
}

/**
 * 复费率报表
 */
export function multiRate(data) {
    return request({
        url: "/smartEnergyReport/multiRate",
        method: "post",
        data
    });
}

//Excel导出
export function deviceExport(data) {
    return request({
        url: "/smartEnergyReport/deviceExport",
        method: "post",
        responseType: "blob",
        headers: {
            "Content-Type": "application/x-download"
        },
        params: data
    });
}

export function analysisExport(data) {
    return request({
        url: "/smartEnergyReport/analysisExport",
        method: "post",
        responseType: "blob",
        headers: {
            "Content-Type": "application/json",
            Accept: "application/x-download"
        },
        data
    });
}

export function multiRateExport(data) {
    return request({
        url: "/smartEnergyReport/multiRateExport",
        method: "post",
        responseType: "blob",
        headers: {
            "Content-Type": "application/json",
            Accept: "application/x-download"
        },
        data
    });
}

/**
 * 电能质量-谐波分析
 */
export function harmonics(data) {
    return request({
        url: "/smartEnergyReport/harmonics",
        method: "post",
        data
    });
}

/**
 * 电能质量-三项不平衡统计
 */
export function tripartiteImbalanceReport(data) {
    return request({
        url: "/smartEnergyReport/tripartiteImbalanceReport",
        method: "post",
        params: data
    });
}

export function getFloorList(data) {
    return request({
        url: "/smartEnergyReport/getFloorList",
        method: "post",
        params: data
    });
}

/**
 * 告警类型
 */
export function getWarningType(data) {
    return request({
        url: "/securityWarning/getWarningType.do",
        method: "post",
        params: data
    });
}

/**
 * 重点区域告警
 */
export function getKeyAreaWarning2(data) {
    return request({
        url: "/securityWarning/getKeyAreaWarning2.do",
        method: "post",
        params: data
    });
}

/**
 * 派单情况
 */

export function totalTasks(data) {
    return request({
        url: "/securityWarning/totalTasks",
        method: "get",
        params: data
    });
}

/**
 * 告警趋势
 */

export function getWarnTrend(data) {
    return request({
        url: "/securityWarning/getWarnTrend.do",
        method: "post",
        params: data
    });
}

// export function getDeviceTypeList() {
//     return request({
//         url: "/smartEnergyReport/getDeviceTypeList",
//         method: "post",
//         data: {}
//     });
// }

// 谐波分析设备
export function harmonicDevices(data) {
    return request({
        url: "/smartEnergyReport/harmonicDevices",
        method: "post",
        data
    });
}
// 谐波分析折线数据
export function harmonicAnalysis(data) {
    return request({
        url: "/smartEnergyReport/harmonicAnalysis",
        method: "post",
        data
    });
}

// /smartEnergyReport/transformerMonitoring

export function transformerMonitoring(data) {
    return request({
        url: "/smartEnergyReport/transformerMonitoring",
        method: "post",
        data
    });
}
// 折线图
export function lineAlarmInfo(data) {
    return request({
        url: "/fireAlarmInfo/line",
        method: "get",
        params: data
    });
}

// 柱状图
export function getBarChart(data) {
    return request({
        url: "/fireAlarmInfo/column",
        method: "get",
        params: data
    });
}
// 消防记录 保存
export function saveOrUpdate(data) {
    return request({
        url: "/fireAlarmInfo/saveOrUpdate",
        method: "post",
        data
    });
}

// 消防记录 班组
export function getTeamlist(data) {
    return request({
        url: "/fireAlarmInfo/getTeamlist",
        method: "get",
        params: data
    });
}
// 导出
export function toExportTemplate(data) {
    return request({
        url: "/fireAlarmInfo/toExportTemplate",
        method: "get",
        params: data,
        responseType: "blob"
    });
}

// 消防记录 列表
export function getPageList(data) {
    return request({
        url: "/fireAlarmInfo/getPageList",
        method: "get",
        params: data
    });
}

// 消防记录 删除
export function del(data) {
    return request({
        url: "/fireAlarmInfo/del",
        method: "get",
        params: data
    });
}
// 消防记录 详情
export function getEntity(data) {
    return request({
        url: "/fireAlarmInfo/getEntity",
        method: "get",
        params: data
    });
}

export function smartEnergyReportLoadReport(data) {
    return request({
        url: "/smartEnergyReport/loadReport",
        method: "post",
        data: data
    });
}

export function loadReportExport(data) {
    return request({
        url: "/smartEnergyReport/loadReportExport",
        method: "post",
        data: data,

        responseType: "blob"
    });
}

//
