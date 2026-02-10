import { request } from "../instance";


// 查询班组
export const getList = (params: any) => request.post("/teamGroups/getTeamPage.do", {}, { 
    params,
    headers: {'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'} 
});


// 删除班组
export const delTeam = (params: any) => request.post("/teamGroups/delTeam.do", {}, { params });

// 班组详情
export const detailTeam = (params: any) => request.post("/teamGroups/detailTeam.do", {}, { params });

// 用户分页
export const getUserPage = (params: any) => request.post("/teamGroups/getUserPage.do", {}, { 
    params,
    headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" } 
});

// 单位树
export const orgTreeList = (params: any) => request.post("/teamGroups/orgTreeList", {data: params}, {});

// 新增班组
export const addTeam = (data: any) => request.post("/teamGroups/addTeam.do", data, {});

// 获取角色列表
export const getRoleList = (params: any) => request.post("/teamGroups/getRoleList.do", {}, { 
    params,
    headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" } 
});

// 角色类型
export const getroleDicts = (data: any) => request.post("/teamGroups/getroleDicts.do", data);
