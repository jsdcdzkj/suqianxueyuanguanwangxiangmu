import { request } from "../instance";

// 告警类型字典
export const getWarnTypeDict = (data: any) => request.post("/warningInfo/warnType", data);

// 告警类型列表（可选）
export const getAlarmList = (data: any) => request.get("/alarm/category/list", {}, {params: data});

// 告警列表
export const getPageList = (data: any) => request.post("/warningInfo/getPageList", {}, {params: data});

// 告警详情
export const getRealtimePageList = (params: any) => request.post("/warningInfo/getRealtimePage", {}, { params });

// 设备实时数据
export const airLog = (params: any) => request.get("/deviceCollect/airLog", {}, { params });

// 告警地图
export const getRealtimeMap = (params: any) => request.get("/warningInfo/getRealtimeMap", {}, { params });

// 历史告警地图
export const getHistoryMap = (params: any) => request.post("/warningInfo/getHistoryPage", {}, { params });

// 新历史告警
export const getHistoryList = (data: any) => request.post("/alarmRecords/getHistoryList", data);

// 历史告警详情
export const getEntityByCollectAndChannel = (params: any) => request.post("/warningInfo/getEntityByCollectAndChannel", {}, { params });

// 实时告警详情
export const getRealTimeWarnView = (data: any) => request.post("/warningInfo/getRealTimeWarnView", data);

// 告警数量
export const warmCount = (params: any) => request.post("/warningInfo/warmCount", {}, { params });

// 告警详情
export const load = (params: any) => request.post("/warningInfo/load", {}, { params });

// 设备实时数据
export const getDeviceInfo = (params: any) => request.post("/warningInfo/getDeviceInfo", {}, { params });

// 告警统计
export const heat = (params: any) => request.post("/statistics/heat", {}, { params });

// 告警等级分布
export const getWarningLevel = (params: any) => request.post("/warningInfo/getWarningLevelPie", {}, { params });

// 告警类型分布
export const getWarningSignStat = (data: any) => request.post("/warningInfo/getWarningSignStat", data);

// 告警状态分布
export const getWarningStausPie = (params: any) => request.post("/warningInfo/getWarningStausPie", {}, { params });

// 告警处理状态分布
export const getWarningHandleStausPie = (params: any) => request.post("/warningInfo/getWarningHandleStausPie", {}, { params });

// 告警设备类型分布
export const getWarningDeviceTypePie = (params: any) => request.post("/warningInfo/getWarningDeviceTypePie", {}, { params });

// 告警类型分布
export const getWarningType = (params: any) => request.post("/securityWarning/getWarningType.do", {}, { params });

// 告警区域分布
export const getWarningAreaPie = (params: any) => request.post("/warningInfo/getWarningAreaPie", {}, { params });

// 告警趋势图
export const getWarningTrendChartApi = (params: any) => request.post("/warningInfo/getWarningTrendChart", {}, { params });

// 告警趋势图-告警类型
export const getWarningTrendWarnType = (data: any) => request.post("/warningInfo/getWarningTrendWarnType", data);

// 告警
export const warningReportTo = (data: any) => request.post("/warningInfo/warningReportTo", data);

// 告警-忽略
export const warning5Ignore = (data: any) => request.post("/warningInfo/warning5Ignore", data);

// 告警-处理
export const warning2Ignore = (data: any) => request.post("/warningInfo/warning2Ignore", data);

// 告警-设备告警列表
export const getDeviceGatewayList = (data: any) => request.post("/warningInfo/getListByDeviceId", {}, {params: data});

// 告警-设备告警详情
export const getDetailsByDeviceId = (data: any) => request.post("/warningInfo/getDetailsByDeviceId", {}, {
	params: data,
	headers: {
		"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
	}
});

// 告警-设备告警详情-新
export const getDetailsByDeviceIdNew = (data: any) => request.post("/alarmRecords/getDetailsByDeviceId", {}, {
	params: data,
	headers: {
		"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
	}
});

// 告警-设备视频列表
export const deviceVideoList = (data: any) => request.post("/warningInfo/deviceVideoList", data);

// 告警-设备视频详情
export const getsRtsp = (data: any) => request.get("/warningInfo/getsRtsp", {}, {params: data});

// 设备运维
export const devopsPage = (data: any) => request.get("/warningInfo/devopsPage", {}, {params: data});

export const alarmLevel = (data: any) => request.get("/warningInfo/alarmLevel", {}, {params: data});

// 告警-告警等级列表
export const alarmTop = (data: any) => request.get("/warningInfo/alarmTop", {}, {params: data});

// 告警-告警类型列表
export const alarmType = (data: any) => request.get("/warningInfo/alarmType", {}, {params: data});

// 告警-安全等级列表
export const alarmSecurity = (data: any) => request.get("/warningInfo/alarmSecurity", {}, {params: data});

// 告警-告警区域分布
export const getWarningAreaTop = (data: any) => request.get("/warningInfo/getWarningAreaTop", {}, {params: data});

// 告警-批量忽略
export const batchIgnore = (data: any) => request.post("/warningInfo/batchIgnore", data);

// 告警-设备运维详情
export const devopsByDevice = (data: any) => request.get("/warningInfo/devopsByDevice", {}, {params: data});

// 饼图
export const pieChart = (data: any) => request.get("/warningInfo/pieChart", {}, {params: data});

export const rank = (data: any) => request.post("/alarm/statistics/rank", data);

export const disposal = (data) => request.post("/alarm/statistics/disposal", data)

export const alarmTrend = (data: any) => request.post("/alarm/statistics/alarmTrend", data)

export const categoryList = (data: any) => request.get("/alarm/category/list", {}, {params: data})

export const alarmNum = (data: any) => request.post("/alarm/statistics/alarmNum", data)

export const alarmGroupList = (data: any) => request.get("/alarm/group/list", {}, {params: data})

export const securityAssessment = (data: any) => request.post("/alarm/statistics/securityAssessment", data)

export const alarmContentPage = (data: any) => request.post("/alarm/content/page", data)

export const deviceTypeList = (data: any) => request.get("/deviceCollect/deviceType", {}, {params: data})

export const saveAlarmContent = (data: any) => request.post("/alarm/content/save", data)

export const exportTemplate = (data: any) => request.post("/alarm/content/template", data)

export const exportAlarmContent = (data: any) => request.post("/alarm/content/export", data)

export const alarmCategoryPage = (data: any) => request.post("/alarm/category/page", data)

export const saveAlarmCategory = (data: any) => request.post("/alarm/category/save", data)

export const alarmGroupPage = (data: any) => request.post("/alarm/group/page", data)

export const saveAlarmGroup = (data: any) => request.post("/alarm/group/save", data)

export const alarmPlanPage = (data: any) => request.post("/alarm/plan/page", data)
export const saveAlarmPlan = (data: any) => request.post("/alarm/plan/save", data)
export const alarmPlanTimePage = (data: any) => request.post("/alarm/plan/time/page", data)

// 根据ID查时间模板
export const getTimeById = (data: any) => request.get("/alarm/plan/time/getById", {}, {params: data})

// 新增编辑时间模板
export const saveTime = (data: any) => request.post("/alarm/plan/time/save", data)

// 删除时间模板
export const delTime = (data: any) => request.post("/alarm/plan/time/delete", data)

export const batchDel = (data: any) => request.post("/alarm/plan/batchDel", data)
export const alarmContentList = (data: any) => request.get("/alarm/content/list", {}, {params: data})

export const switchEnable = (data: any) => request.post("/alarm/plan/switch/enable", data)

export const alarmContentTree = (data: any) => request.get("/alarm/content/tree", {}, {params: data})

export const getOrgList = (data: any) => request.post("/sysDept/getList", data)

export const getUserTree = (data: any) => request.post("/sysuser/getUserTree.do", data)
export const teamGroupsTree = (data: any) => request.post("/teamGroups/tree", data)

export const getPlanById = (data: any) => request.get("/alarm/plan/getById", {}, {params: data})
