import { request } from "../instance";

// 分页数据
export const selectPageList = (params: any) => request.post("/reportManage/selectPageList", {}, {params});
// 报表类型树
export const selectReportTypeTree = (params: any) => request.post("/report/selectReportTypeTree", {}, {params});
// 报告模板列表
export const selectReportTemplateList = (params: any) => request.post("/report/selectPageList", {}, {params});



// 获取列表
export const reportManageGetPage = (params: any) => request.post("/reportManage/getPage", {}, { params });

// 删除报表
export const deleteReport = (params: any) => request.post("/reportManage/deleteReport", {}, { params });

// 报表树
export const areaTreeList = (params: any) => request.post("/reportManage/areaTreeList", {}, { params });

// 新增报表
export const addReport = (data: any) => request.post("/reportManage/addReport", data, { 
    headers: { "Content-Type": "multipart/form-data" }
 });

 // 工单情况
export const missionInfo = (data: any) => request.post("/reportManage/missionInfo", data, {});

// 异常状态分布
export const getWarningDeviceTypePie = (data: any) => request.post("/reportManage/getWarningDeviceTypePie", data, {});

// 告警状态分布
export const getWarningSignStat = (data: any) => request.post("/reportManage/getWarningSignStat", data, {});

// 告警区域分布
export const getWarningAreaPie100 = (data: any) => request.post("/reportManage/getWarningAreaPie", data, {});

// 告警类型分布
export const getWarningType = (data: any) => request.post("/reportManage/getWarningType.do", data, {});

// 告警数量分布
export const warningDataForNum = (data: any) => request.post("/reportManage/warningDataForNum", data, {});

// 告警时间分布
export const getDeviceCollectList = (data: any) => request.post("/reportManage/getDeviceCollectList", data, {});

// 三相不平衡分析
export const tripartiteImbalanceReport = (data: any) => request.post("/reportManage/tripartiteImbalanceReport", data, {});

// 能耗分析
export const energyInfo = (data: any) => request.post("/reportManage/energyInfo", data, {});

// 区域负载分析
export const getAreaLoad = (data: any) => request.post("/reportManage/getAreaLoad", data, {});

// 设备负载分析Top10
export const getDeviceCollectTop = (data: any) => request.post("/reportManage/getDeviceCollectTop", data, {});

// 工单异常状态分布
export const taskErrorInfo = (data: any) => request.post("/reportManage/taskErrorInfo", data, {});

// 工单情况
export const taskInfo = (data: any) => request.post("/reportManage/taskInfo", data, {});

// 基础信息
export const baseInfo = (data: any) => request.post("/reportManage/baseInfo", data, {});

// 指令情况
export const cmdInfo = (data: any) => request.post("/reportManage/cmdInfo", data, {});
