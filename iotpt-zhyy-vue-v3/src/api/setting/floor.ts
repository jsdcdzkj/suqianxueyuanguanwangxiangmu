import { request } from "../instance";

// 获取所有楼宇列表
export const allBuildList = () => request.post("/sys_build_floor/allBuildList", {});

// 新增楼层
export const addBuildFloor = (data: any) =>
	request.post("/sys_build_floor/addBuildFloor", data, { operationMessage: "新增成功" });

// 获取楼层列表
export const selectBuildFloorList = (data: any) => request.post("/sys_build_floor/selectBuildFloorList", data);

// 获取楼层分页列表
export const getList = (data: any) => request.post("/sys_build_floor/getList", data);

// 获取所有楼层列表
export const getAllList = (data: any) => request.post("/sys_build_floor/getAllList", data);

// 更新楼层信息
export const updateBuildFlool = (data: any) =>
	request.post("/sys_build_floor/updateBuildFloor", data, {
		operationMessage: "更新成功",
		headers: {
			"Content-Type": "multipart/form-data"
		}
	});

// 获取楼层详情
export const getAreaInfo = (id: string | number) => request.post(`/sys_build_floor/info?id=${id}`, {});

// 删除楼层
export const delBuildFloor = (id: string | number) =>
	request.post(`/sys_build_floor/delBuildFloor?id=${id}`, {}, { operationMessage: "删除成功" });
