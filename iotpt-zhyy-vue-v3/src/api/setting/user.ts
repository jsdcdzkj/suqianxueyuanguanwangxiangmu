import { request } from "../instance";

export const getList = (data: any) => request.post("/sysUser/getList", data);

export const saveUser = (data: any) =>
	request.post("/sysUser/save", data, {
		operationMessage: data.id ? "编辑成功" : "添加成功"
	});

export const delUser = (data: any) => {
	return request.post(`/sysUser/delUser?userId=${data}`, {}, { operationMessage: "删除成功" });
};

export const resetPass = (data: any) => {
	return request.post(`/sysUser/resetPass?id=${data}`, {}, { operationMessage: "重置成功" });
};

export const getRolesByUserId = (data: any) => {
	return request.post(`/sysRole/getRolesByUserId?userId=${data}`);
};

export const getListAll = (data: any) => request.post("/sysUser/getListAll", data);

// /sysUser/pass 修改密码
export const pass = (data: any) => request.post("/sysUser/pass", data, { operationMessage: "密码修改成功" });

export const syncAccountInfo = (data: any) => request.get("/sysUser/syncAccountInfo", { params: data });
