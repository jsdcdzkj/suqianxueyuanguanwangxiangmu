import { request } from "../instance";

export const getListUser = (data: any) => request.get("/sysuser/getUserPage.do", { params: data });

// export const saveUser = (data: any) =>
// 	request.post("/sysUser/save", data, {
// 		operationMessage: data.id ? "编辑成功" : "添加成功"
// 	});
export const saveUser = (data: any) => {
	if (data.id) {
		return request.post("/sysuser/editUser.do", data, {
			operationMessage: "编辑成功"
		});
	} else {
		return request.post("/sysuser/addUser.do", data, {
			operationMessage: "添加成功"
		});
	}
};

export const delUser = (data: any) => {
	return request.post(
		`/sysuser/delUser.do`,
		data,
		{
			headers: {
				"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
			}
		},
		{ operationMessage: "删除成功" }
	);
};

export const resetPass = (data: any) => {
	return request.post(`/sysuser/resetPass?id=${data}`, {}, { operationMessage: "重置成功" });
};

export const getRolesByUserId = (data: any) => {
	return request.post(`/sysRole/getRolesByUserId?userId=${data}`);
};

export const getListAll = (data: any) => request.post("/sysuser/getListAll", data);

export const changePassword = (data: any) =>
	request.post("/sysuser/changePassword.do", data, { operationMessage: "密码修改成功" });

export const syncAccountInfo = (data: any) => request.get("/sysuser/syncAccountInfo", { params: data });

// 启用禁用状态
// export function doEnable(data) {
// 	return request({
// 	  url: baseUrl + '/doEnable.do',
// 	  method: 'post',
// 	  params: data,
// 	})
//   }
// 启用禁用状态
export const doEnable = (data: any) =>
	request.post(
		"/sysuser/doEnable.do",
		data,
		{
			headers: {
				"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
			}
		},
		{
			operationMessage: data.status ? "启用成功" : "禁用成功"
		}
	);

export const toExportTemplate = (data: any) =>
	request.post("/sysuser/toExportTemplate", data, { responseType: "blob" });

export const importUser = (data: any) =>
	request.post("/sysuser/importUser", data, {
		operationMessage: "导入成功",
		headers: { "Content-Type": "multipart/form-data" }
	});
