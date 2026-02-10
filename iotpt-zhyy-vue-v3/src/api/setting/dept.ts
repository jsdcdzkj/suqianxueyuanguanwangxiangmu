import { request } from "../instance";

export const addOrgDept = (data: any) =>
	request.post("/sys_org_dept/addOrgDept", data, { operationMessage: "新增成功" });

export const selectOrgDeptList = (data: any) => request.post("/sys_org_dept/selectOrgDeptList", data);

export const updateOrgDept = (data: any) =>
	request.post("/sys_org_dept/updateOrgDept", data, { operationMessage: "更新成功" });

export const delOrgDept = (data: any) =>
	request.post(`/sys_org_dept/delOrgDept?id=${data}`, {}, { operationMessage: "删除成功" });

export const info = (data: any) => request.post(`/sys_org_dept/info?id=${data}`, {});

export const findDept = (data: any) => request.post("/sys_org_dept/findDept", data);

// 门禁设备列表
export const getDoorList = (data: any) => request.post("/sysuser/getDoorList", data);

export const SaveAi = (data: any) => request.post("/sysuser/aiAuthority", data, { operationMessage: "保存成功" });

export const getCarList = (data: any) => request.post(`/sysuser/getCarList?userId=${data}`, {});

export const visitorRegionList = (data: any) => request.post("/sys_org_dept/visitorRegionList", data);

// 同步大华数据
export const visitorRegion = (data: any) =>
	request.post("/sys_org_dept/visitorRegion", data, { operationMessage: "同步成功" });

export const visitorRegion2 = (data: any) =>
	request.get(`/sys_org_dept/synchronization?iccId=${data.iccId}&orgId=${data.orgId}`);
