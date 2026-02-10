import { request } from "../instance";

// 获取Redis字典列表
export const getRedisDictList = (data: any) => request.post("/sysDict/getRedisDictList", {}, { params: data });

// 获取字典选择列表
export const selectDictList = (data: any) => request.post("/sysDict/selectDictList", {}, { params: data });
