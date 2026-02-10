import { request } from "../instance";

export const login = (data: any) => request.post("/login.do", data, { operationMessage: "登录成功" });

export const getUserInfo = (accessToken: string) =>
	request.post(
		"/userInfo.do",
		{ accessToken },
		{
			headers: {
				"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
			}
		}
	);

export const logout = () => request.get("/logout.do");

export const uploadFile = (files: any) =>
	request.upload("/minio/batchImportFile", { files }, { uploadFileName: "files" });

export const getUserList = (data: any) =>
	request.post("/sysUser/getList", data, {
		params: data
	});

export const getDictSignList = () => request.post("/sysDict/getDictSignList", {});
// 获取资产管理字典
export const dict = (data: any) => request.get("/rfid/assetManage/dict", { params: data });

export const sysDictPage = () => request.post<any[]>("/dictType/getAllDicts", {});

export const getUserMenu = (id: number) => request.get("/userMenuTree", { params: { id } });
// userMenu

// 项目迭代/计划/版本
export const iterationTree = (data: any) => request.post("/proPlan/iterationTree", data);

// 里程碑
export const getPlanAndMilestone = (data: any) => request.post("/proPlan/getPlanAndMilestone", { params: data });
