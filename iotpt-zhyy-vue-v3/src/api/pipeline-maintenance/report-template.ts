import { request } from "../instance";

// 分页数据
export const selectPageList = (params: any) => request.post("/report/selectPageList", {}, {params});

// 保存数据
export const saveTemplate = (data: any) => request.post("/report/saveTemplate", data, {});

// 更新数据
export const updTemplate = (data: any) => request.post("/report/updTemplate", data, {});



// 删除模板
export const deleteTemplate = (params: any) => request.post("/report/deleteTemplate", {}, { params });

// 获取模板树结构
export const selectReportTypeTree = (data: any) => request.post("/report/selectReportTypeTree", data, {});