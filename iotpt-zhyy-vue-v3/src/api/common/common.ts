import { request } from "../instance";

export const getMenus = () => request.get("/menus");

// 获取字典列表
export const getRedisDictList = (params: any) => request.post("/sysDict/getRedisDictList", {}, { params });

// 任务类型字典
export const selectMissionDictAll = (params: any) => request.post("/sysDict/selectMissionDictAll", {}, { params });
