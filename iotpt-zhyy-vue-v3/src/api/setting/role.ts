import { request } from "../instance";

export const getList = (data: any) =>
	request.post("/sysRole/getList", {}, { params: data });

export const saveRole = (data: any) =>
	request.post("/sysRole/saveRole", data, {
		operationMessage: data.id ? "编辑成功" : "添加成功"
	});

export const getRoleList = (data: any) => {
	return request.post(`/sysRole/getRoleList?roleId=${data}`);
};

export const getAllRoles = (data: any) =>
	request.post("/sysRole/getAllRoles", data);

export const delRole = (data: any) => {
	return request.post(`/sysRole/delRole?roleId=${data}`);
};
