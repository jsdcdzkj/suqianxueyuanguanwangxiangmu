import { request } from "../instance";

// 获取楼宇楼层列表
export const allBuildFloolList = (buildFloorId: string | number) =>
	request.post(`/sys_build_area/allBuildFloorList?buildFloorId=${buildFloorId}`, {});

// 新增区域
export const addBuildArea = (data: any) =>
	request.post("/sys_build_area/addBuildArea", data, { operationMessage: "新增成功" });

// 获取区域列表
export const selectBuildAreaList = (data: any) => request.post("/sys_build_area/selectBuildAreaList", data);

// 更新区域信息
export const updateBuildArea = (data: any) =>
	request.post("/sys_build_area/updateBuildArea", data, { operationMessage: "更新成功" });

// 删除区域
export const delBuildArea = (id: string | number) =>
	request.post(`/sys_build_area/delBuildArea?id=${id}`, {}, { operationMessage: "删除成功" });

// 获取区域详情
export const getAreaInfo = (id: string | number) => request.post(`/sys_build_area/info?id=${id}`, {});

// 获取所有区域列表
export const getAllList = (data: any) => request.post("/sys_build_area/getAllList", data);

// export function getRedisDictList(data) {
//   return request({
//     url: '/sysDict/getRedisDictList',
//     method: 'post',
//     params: data,
//   })
// }
export const getRedisDictList = (data: any) => request.post("/sysDict/getRedisDictList", data);
