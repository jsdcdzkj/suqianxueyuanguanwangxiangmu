import { request, type ResponseData } from "../instance";

export const getRealtimeTop = () => request.get<ResponseData>("/monitoring-water/realtime");
