import { request } from "../instance";

// 获取凭证列表
export const allBuildList = (params: any) => request.get("/credential/getEntityList", { params });

// 获取楼宇列表
export const getBuildList = () => request.post("/sys_build/allBuildList", {});

// 保存或更新凭证
export const saveOrUpCredential = (params: any) =>
	request.post("/credential/saveOrUpCredential", params, { operationMessage: "保存成功" });

// 删除凭证
export const delCredential = (params: any) =>
	request.get("/credential/delCredential", { params }, { operationMessage: "删除成功" });

// 新增楼宇
export const addBuild = (data: any) => request.post("/sys_build/addBuild", data, { operationMessage: "新增成功" });

// 获取楼宇选择列表
export const selectBuildList = (data: any) => request.post("/sys_build/selectBuildList", data);

// 更新楼宇信息
export const updateBuild = (data: any) =>
	request.post("/sys_build/updateBuild", data, { operationMessage: "更新成功" });

// 删除楼宇
export const delBuild = (id: string | number) =>
	request.post(`/sys_build/delBuild?id=${id}`, {}, { operationMessage: "删除成功" });

// 获取楼宇详情
export const info = (id: string | number) => request.post(`/sys_build/info?id=${id}`, {});

// 获取楼宇树形列表
export const bulidTreeList = () => request.post("/sys_build/bulidTreeList", {});
