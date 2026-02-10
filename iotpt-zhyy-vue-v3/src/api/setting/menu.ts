import { request } from "../instance";

// 获取菜单树
export const getMenuTree = (data: any) => request.post("/menu/getTree", data);

// 获取菜单列表
export const getMenuList = () => request.post("/menu/getList", {});

// 新增/编辑菜单
export const saveMenu = (data: any) => request.post("/menu/save", data, { operationMessage: "操作成功" });

// 删除菜单
export const delMenu = (data: { id: string | number }) =>
	request.post("/menu/delete", data, { operationMessage: "删除成功" });

// 修改菜单显示状态
export const doChangeStatus = (data: { id: string | number; isShow: number }) =>
	request.post("/menu/changeStatus", data, { operationMessage: "状态修改成功" });

// 获取菜单权限列表
export const getMenuPermissionList = (data: any) => request.post("/menu/getPermissionList", data);

// 保存菜单权限
export const saveMenuPermission = (data: any) =>
	request.post("/menu/savePermission", data, { operationMessage: "权限保存成功" });
export const getRedisDictMap = (data: any) => request.post("/sysDict/getRedisDictMap", data);
