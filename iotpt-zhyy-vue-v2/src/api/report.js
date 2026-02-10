import request from "@/utils/request";

//获取模板树结构
export function selectReportTypeTree(data) {
    return request({
        url: "/report/selectReportTypeTree",
        method: "post",
        data
    });
}

// 保存模板
export function saveTemplate(data) {
    return request({
        url: "/report/saveTemplate",
        method: "post",
        data
    });
}

// 保存模板
export function updTemplate(data) {
    return request({
        url: "/report/updTemplate",
        method: "post",
        data
    });
}

// 分页

export function selectPageList(data) {
    return request({
        url: "/report/selectPageList",
        method: "post",
        params: data
    });
}

// 删除模板
export function deleteTemplate(data) {
    return request({
        url: "/report/deleteTemplate",
        method: "post",
        params: data
    });
}

// /reportManage/areaTreeList

export function areaTreeList(data) {
    return request({
        url: "/reportManage/areaTreeList",
        method: "post",
        params: data
    });
}

//
export function getWarningType(data) {
    return request({
        url: "/reportManage/getWarningType.do",
        method: "post",
        data: data
    });
}

export function getWarningSignStat(data) {
    return request({
        url: "/reportManage/getWarningSignStat",
        method: "post",
        data: data
    });
}

export function getWarningDeviceTypePie(data) {
    return request({
        url: "/reportManage/getWarningDeviceTypePie",
        method: "post",
        data: data
    });
}

export function warningDataForNum(data) {
    return request({
        url: "/reportManage/warningDataForNum",
        method: "post",
        data: data
    });
}

export function getWarningAreaPie100(data) {
    return request({
        url: "/reportManage/getWarningAreaPie",
        method: "post",
        data: data
    });
}

export function reportManageGetPage(data) {
    return request({
        url: "/reportManage/getPage",
        method: "post",
        params: data
    });
}

export function addReport(file, data) {
    const formData = new FormData();
    formData.append("file", file);
    // formData.append("body", `${JSON.stringify(data)}`);
    Reflect.ownKeys(data).forEach((item) => {
        formData.append(item, data[item]);
    });
    return request({
        url: "/reportManage/addReport",
        method: "post",
        data: formData,
        headers: { "Content-Type": "multipart/form-data" }
    });
}

//
export function deleteReport(data) {
    return request({
        url: "/reportManage/deleteReport",
        method: "post",
        params: data
    });
}

//项目概括-基本信息
export function baseInfo(data) {
    return request({
        url: "/reportManage/baseInfo",
        method: "post",
        data: data
    });
}
//项目概括-终端情况
export function cmdInfo(data) {
    return request({
        url: "/reportManage/cmdInfo",
        method: "post",
        data: data
    });
}
//用电统计-用电量统计
export function energyInfo(data) {
    return request({
        url: "/reportManage/energyInfo",
        method: "post",
        data: data
    });
}
//用电统计-负载率分析
export function getAreaLoad(data) {
    return request({
        url: "/reportManage/getAreaLoad",
        method: "post",
        data: data
    });
}
//用电统计-负载率排行
export function getDeviceCollectTop(data) {
    return request({
        url: "/reportManage/getDeviceCollectTop",
        method: "post",
        data: data
    });
}

//巡检运维-工单信息
export function missionInfo(data) {
    return request({
        url: "/reportManage/missionInfo",
        method: "post",
        data: data
    });
}

//巡检运维-巡检记录
export function taskInfo(data) {
    return request({
        url: "/reportManage/taskInfo",
        method: "post",
        data: data
    });
}

//巡检运维-巡检异常
export function taskErrorInfo(data) {
    return request({
        url: "/reportManage/taskErrorInfo",
        method: "post",
        data: data
    });
}

//
export function tripartiteImbalanceReport(data) {
    return request({
        url: "/reportManage/tripartiteImbalanceReport",
        method: "post",
        data: data
    });
}

export function getDeviceCollectList(data) {
    return request({
        url: "/reportManage/getDeviceCollectList",
        method: "post",
        data: data
    });
}
