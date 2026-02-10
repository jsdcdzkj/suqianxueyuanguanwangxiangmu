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
export const addReport = (params: any) => request.post("/reportManage/addReport", {data: params}, { 
    headers: { "Content-Type": "multipart/form-data" }
 });

 // 工单情况
export const missionInfo = (params: any) => request.post("/reportManage/missionInfo", {data: params}, {});

// 异常状态分布
export const getWarningDeviceTypePie = (params: any) => request.post("/reportManage/getWarningDeviceTypePie", {data: params}, {});

// 告警状态分布
export const getWarningSignStat = (params: any) => request.post("/reportManage/getWarningSignStat", {data: params}, {});

// 告警区域分布
export const getWarningAreaPie100 = (params: any) => request.post("/reportManage/getWarningAreaPie", {data: params}, {});

// 告警类型分布
export const getWarningType = (params: any) => request.post("/reportManage/getWarningType.do", {data: params}, {});

// 告警数量分布
export const warningDataForNum = (params: any) => request.post("/reportManage/warningDataForNum", {data: params}, {});

// 告警时间分布
export const getDeviceCollectList = (params: any) => request.post("/reportManage/getDeviceCollectList", {data: params}, {});

// 三相不平衡分析
export const tripartiteImbalanceReport = (params: any) => request.post("/reportManage/tripartiteImbalanceReport", {data: params}, {});

// 能耗分析
export const energyInfo = (params: any) => request.post("/reportManage/energyInfo", {data: params}, {});

// 区域负载分析
export const getAreaLoad = (params: any) => request.post("/reportManage/getAreaLoad", {data: params}, {});

// 设备负载分析Top10
export const getDeviceCollectTop = (params: any) => request.post("/reportManage/getDeviceCollectTop", {data: params}, {});

// 工单异常状态分布
export const taskErrorInfo = (params: any) => request.post("/reportManage/taskErrorInfo", {data: params}, {});

// 工单情况
export const taskInfo = (params: any) => request.post("/reportManage/taskInfo", {data: params}, {});

// 基础信息
export const baseInfo = (params: any) => request.post("/reportManage/baseInfo", {data: params}, {});

// 指令情况
export const cmdInfo = (params: any) => request.post("/reportManage/cmdInfo", {data: params}, {});
