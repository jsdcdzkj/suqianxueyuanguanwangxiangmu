import { request } from "../instance";

// 分页列表
export const messagedistList = (data: any) => request.post("/app/message/config/page", data);

// 列表不分页
export const list = (data: any) => request.post("/app/message/config/list", data);

// 新增保存
export const save = (data: any) => request.post("/app/message/config/save", data, { operationMessage: "保存成功" });

// 分类查询
export const getType = (data: any) => request.get("/app/message/config/type", data);

// 删除
export const del = (data: any) => request.get("/app/message/config/delete", data, { operationMessage: "删除成功" });

// 根据id查询
export const byId = (data: any) => request.get("/app/message/config/byId", data);
