import { request } from "../instance";

// 获取菜单树
export const getMenuTree = (data: any) =>
	request.post("/sysmenu/getMenuTree.do", data, {
		headers: {
			"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
		}
	});

// 获取菜单列表
export const getMenuList = (data?: any) =>
	request.post("/sysmenu/getMenuList.do", data, {
		headers: {
			"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
		}
	});

// 新增/编辑菜单
export const saveMenu = (data: any) => request.post("/sysmenu/saveMenu.do", data, { operationMessage: "操作成功" });

// 删除菜单
export const delMenu = (data: { id: string | number }) =>
	request.post("/sysmenu/delMenu.do", data, {
		operationMessage: "删除成功",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
		}
	});

// 修改菜单显示状态
export const doChangeStatus = (data: { id: string | number; isShow: number }) =>
	request.post("/sysmenu/doChangeStatus", data, {
		operationMessage: "状态修改成功",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
		}
	});

// 根据系统ID获取菜单树
export const getMenuTypeTree = (data: any) =>
	request.post("/sysmenu/getMenuTypeTree.do", data, {
		headers: {
			"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
		}
	});

// 获取菜单权限列表
export const getMenuPermissionList = (data: any) => request.post("/menu/getPermissionList", data);

// 保存菜单权限
export const saveMenuPermission = (data: any) =>
	request.post("/menu/savePermission", data, { operationMessage: "权限保存成功" });

// 获取字典映射
export const getRedisDictMap = (data: any) => request.post("/sysDict/getRedisDictMap", data);
