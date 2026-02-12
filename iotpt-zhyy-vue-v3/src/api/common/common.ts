import { request } from "../instance";

export const getMenus = () => request.get("/menus");

// 获取字典列表
export const getRedisDictList = (params: any) => request.post("/sysDict/getRedisDictList", {}, { params });

// 任务类型字典
export const selectMissionDictAll = (params: any) => request.post("/sysDict/selectMissionDictAll", {}, { params });

// 逻辑位置可选
export const areaTreeList2 = (data: any) => request.post("/smartEnergyReport/areaTreeList2", data);

