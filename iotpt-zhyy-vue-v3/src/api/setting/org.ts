import { request } from "../instance";

export const addOrgManage = (data: any) =>
	request.post("/sys_org_manage/addOrgManage", data, { operationMessage: "新增成功" });

export const selectBuildList = (data: any) => request.post("/sys_org_manage/selectBuildList", data);

export const updateOrgManage = (data: any) =>
	request.post("/sys_org_manage/updateOrgManage", data, { operationMessage: "更新成功" });

export const orgTreeList = (data: any) => request.post("/sys_org_manage/orgTreeList", data);

export const areaTreeList = (data: any) => request.post("/sys_org_manage/areaTreeList", data);

export const areaTreeList2 = (data: any) => request.post("/sys_org_manage/areaTreeList2", data);

export const delOrgManage = (data: any) =>
	request.post(`/sys_org_manage/delOrgManage?id=${data}`, {}, { operationMessage: "删除成功" });

export const info = (data: any) => request.post(`/sys_org_manage/info?id=${data}`, {});

export const findOrg = (data: any) => request.post("/sys_org_manage/findOrg", data);

export const findAllDept = (data: any) => request.post("/sys_org_dept/findAllDept", data);

export const findAllDeptByUnit = (data: any) => request.post("/sys_org_dept/findAllDeptByUnit", data);
