import request from "@/utils/request";

export function areaTreeList(data) {
    return request({
        url: "/deviceCollect/areaTreeList",
        method: "post",
        data
    });
}

export function getBuildAreaList(data) {
    return request({
        url: "/deviceCollect/getList",
        method: "post",
        params: data
    });
}

export function deviceMonitoring(data) {
    return request({
        url: "/deviceCollect/deviceMonitoring",
        method: "post",
        params: data
    });
}

export function getDeviceInfo(data) {
    return request({
        url: "/smartEnergyReport/getDeviceInfo",
        method: "post",
        data
    });
}

export function deviceType(data) {
    return request({
        url: "/deviceCollect/deviceType",
        method: "post",
        params: data
    });
}

export function getById(data) {
    return request({
        url: "/deviceAep/getById",
        method: "post",
        params: data
    });
}

export function getInformationReporting(data) {
    return request({
        url: "/deviceCollect/getInformationReporting",
        method: "post",
        params: data
    });
}

export function getReportingList(data) {
    return request({
        url: "/deviceCollect/getReportingList",
        method: "post",
        params: data
    });
}

export function getReportingCount(data) {
    return request({
        url: "/deviceCollect/getReportingCount",
        method: "post",
        params: data
    });
}

export function getReportingListAll(data) {
    return request({
        url: "/deviceCollect/getReportingListAll",
        method: "post",
        params: data
    });
}

export function energyTrend(data) {
    return request({
        url: "/deviceCollect/energyTrend",
        method: "post",
        params: data
    });
}

export function energyTrendAVG(data) {
    return request({
        url: "/deviceCollect/energyTrendAVG",
        method: "post",
        params: data
    });
}

export function cumulativeEmission(data) {
    return request({
        url: "/deviceCollect/cumulativeEmission",
        method: "post",
        params: data
    });
}

export function electricityYOY(data) {
    return request({
        url: "/deviceCollect/getElectricityYOY",
        method: "post",
        data
    });
}

export function electricityTable(data) {
    return request({
        url: "/deviceCollect/getElectricityTable",
        method: "post",
        data
    });
}

// export function getSignalByDevice(data) {
//   return request({
//     url: '/deviceCollect/getSignalByDevice',
//     method: 'post',
//     data
//   })
// }
export function remoteControlApi(data) {
    return request({
        url: "/deviceCollect/remoteControl",
        method: "post",
        data
    });
}

export function getFloorByBId(data) {
    return request({
        url: "/deviceCollect/getFloorByBId",
        method: "post",
        params: data
    });
}

export function getEntityByTIdGroup(data) {
    return request({
        url: "/deviceCollect/getEntityByTIdGroup",
        method: "post",
        params: data
    });
}

export function energyConsumption(data) {
    return request({
        url: "/statistics/energyConsumption",
        method: "post",
        params: data
    });
}

export function analyzeData(data) {
    return request({
        url: "/analysisCenter/getEnergyStatistics.do",
        method: "post",
        data
    });
}

export function getSubitemEnergy(data) {
    return request({
        url: "/analysisCenter/getSubitemEnergy.do",
        method: "post",
        data
    });
}

export function getTotal(data) {
    return request({
        url: "/statistics/energyConsumption",
        method: "post",
        data
    });
}

export function getTotalLine(data) {
    return request({
        url: "/statistics/getRealTimeWater.do",
        method: "post",
        data
    });
}

export function getSubitemEnergyLine(data) {
    return request({
        url: "/statistics/getSubitemEnergy",
        method: "post",
        data
    });
}

export function getDeviceDetail(data) {
    return request({
        url: "/statistics/subitemEnergy",
        method: "post",
        params: data
    });
}

//获取空调状态
export function getAirStatus(data) {
    return request({
        url: "/acController/indoor",
        method: "post",
        params: data
    });
}
//空调控制
export function airControl(data) {
    return request({
        url: "/acController/control",
        method: "post",
        params: data
    });
}
