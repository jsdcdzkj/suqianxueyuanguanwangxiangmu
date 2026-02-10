import { request } from "../instance";

// 获取计划区域树
export const getTreeBuild = () => request.get("/planArea/getTreeBuild.do");

// 获取计划区域树（禁用）
export const getTreeBuildDisable = () => request.get("/planArea/getTreeBuildDisable.do");

// 获取所有班组列表
export const getGroupList = (data: any) => request.post("/teamGroups/getAllList.do", {}, { data });

// 获取所有检查模板列表
export const getTemplateList = (data: any) => request.post("/checkTemlate/getAllList.do", {}, { data });

// 获取计划列表
export const getPageList = (params: any) => request.post("/jobPlan/getPageList.do", {}, { params });

// 获取计划详情
export const getJobPlanById = (params: any) => request.post("/jobPlan/getJobPlanById.do", {}, { params });

// 新增计划
export const toAdd = (data: any) => request.post("/jobPlan/toAdd.do", {}, { data });

// 编辑计划
export const toEdit = (data: any) => request.post("/jobPlan/toEdit.do", {}, { data });

// 启用/禁用计划
export const isEnablePlan = (params: any) => request.post("/jobPlan/isEnablePlan.do", {}, { params });

// 删除计划
export const toDel = (params: any) => request.post("/jobPlan/toDel.do", {}, { params });

// 所有模板列表
export const getList = (params: any) => request.post("/checkTemlate/getCheckPage.do", {}, { params,
	headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" }
 });

// 所有模板类型
export const getTypeList = (params: any) => request.post("/checkTemlate/getAllTypeList.do", {}, { params });


// 删除模板
export const delCheckTem = (params: any) => request.post("checkTemlate/delCheckTem.do", {}, { params });

// 修改状态
export const setIsAble = (params: any) => request.post("/checkTemlate/setIsAble.do", {}, { params });

// 复制模板
export const copyTemplate = (params: any) => request.post("/checkTemlate/copyTemplate.do", {}, { params });

// 编辑模板
export const addCheck = (data: any) => request.post("/checkTemlate/addCheck.do", data);

// 获取模板详情
export const getOneDetail = (params: any) => request.post("/checkTemlate/getOneDetail.do", {}, { params });

// 查询所有设备类型
export const getAllDeviceType = (data: any) => request.post("/checkTemlate/getAllDeviceType.do", {}, { data });
