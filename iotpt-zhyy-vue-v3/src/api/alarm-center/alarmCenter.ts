import { request } from "../instance";

// 获取报警类型
export const getAlarmCategory = (data: any) => request.get("/alarmRecords/getAlarmCategory", {}, {params: data});
// 告警中心分页
export const getPageList = (data: any) => request.post("/alarmRecords/getPageList", data);

// 查看详情
export const getEntity = (data: any) => request.post("/alarmRecords/getEntity", data);

// 角标
export const getsubscript = (data: any) => request.post("/alarmRecords/getsubscript", data);

// 统计数量
export const groupAlarmLevel = (data: any) => request.post("/alarmRecords/groupAlarmLevel", data);

// 折线图
export const line = (data: any) => request.post("/alarmRecords/line", data);

// 批量处理报警
export const batchProcessing = (data: any) => request.post("/alarmRecords/batchProcessing", data);

// 导出
export const toExportTemplate = (data: any) => request.get("/alarmRecords/toExportTemplate", {}, {
    responseType: "blob",
    params: data
});

// 弹窗
export const getNewEntity = (data: any) => request.post("/alarmRecords/getNewEntity", {}, {params: data});

// 报警内容下拉列表
export const contentList = (data: any) => request.get("/alarm/content/list", {}, {params: data});

// 通知
export const getNotice = (data: any) => request.get("/alarmRecords/notice", {}, {params: data});

//AI分析
export const getAiAnalysis = (data: any) => request.post("/alarmRecords/lamp",data);

// 预警
export const getAiAnalysisYJ = (data: any) => request.post("/alarmRecords/lampYJ",data);
