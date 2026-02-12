import { request } from "../instance";

export const getPageList = (data: any) =>
	request.post("/sysRole/getRolePage.do", data, {
		headers: {
			"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
		}
	});

export const getList = (data: any) => request.post("/sysRole/getRoleList.do", data);

export const doEdit = (data: any) => request.post("/sysRole/saveRole.do", data, { operationMessage: "保存成功" });

export const doDelete = (data: any) =>
	request.post("/sysRole/delRole.do", data, {
		headers: {
			"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
		},
		operationMessage: "删除成功"
	});

export const getRoleMenu = (data: any) => request.post("/sysRole/getRoleMenu.do", data);

export const getRoleUser = (data: any) =>
	request.post("/sysRole/getRoleUser.do", data, {
		headers: {
			"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
		}
	});
