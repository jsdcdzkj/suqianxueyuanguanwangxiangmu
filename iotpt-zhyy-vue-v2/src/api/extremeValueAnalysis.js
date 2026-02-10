import request from "@/utils/request";

export function electricityMeterData(data) {
    return request({
        url: "/extremeValueAnalysis/electricityMeterData",
        method: "post",
        data
    });
}
export function airSwitchData(data) {
    return request({
        url: "/extremeValueAnalysis/airSwitchData",
        method: "post",
        data
    });
}

export function getDataInfo(data) {
    return request({
        url: "/extremeValueAnalysis/getDataInfo",
        method: "post",
        data
    });
}
