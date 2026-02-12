import { request } from "../instance";
export const getCenterData = (data: any) => request.post("/systemOverview/getCenterData", { params: data });
