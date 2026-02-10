import { request } from "../instance";

// 获取告警类型字典
export const getWarnTypeDict = (data: any) => request.post("/warningInfo/warnType", data);

// 获取告警列表
export const getAlarmList = (data: any) => request.get("/alarm/category/list", { params: data });

// 获取分页列表
export const getPageList = (data: any) => request.post("/warningInfo/getPageList", data);

// 获取实时分页列表
export const getRealtimePageList = (data: any) => request.post("/warningInfo/getRealtimePage", data);

// 获取空气日志
export const airLog = (data: any) => request.get("/deviceCollect/airLog", { params: data });

// 获取实时地图
export const getRealtimeMap = (data: any) => request.get("/warningInfo/getRealtimeMap", { params: data });

// 获取历史告警
export const getHistoryPage = (data: any) => request.post("/warningInfo/getHistoryPage", data);

// 获取新的历史告警列表
export const getHistoryList = (data: any) => request.post("/alarmRecords/getHistoryList", data);

// 根据采集和通道获取实体
export const getEntityByCollectAndChannel = (data: any) =>
	request.post("/warningInfo/getEntityByCollectAndChannel", data);

// 获取实时告警视图
export const getRealTimeWarnView = (data: any) => request.post("/warningInfo/getRealTimeWarnView", data);

// 告警计数
export const warmCount = (data: any) => request.post("/warningInfo/warmCount", data);

// 加载
export const load = (data: any) => request.post("/warningInfo/load", data);

// 获取设备信息
export const getDeviceInfo = (data: any) => request.post("/warningInfo/getDeviceInfo", data);

// 热力图
export const heat = (data: any) => request.post("/statistics/heat", data);

// 获取告警等级饼图
export const getWarningLevel = (data: any) => request.post("/warningInfo/getWarningLevelPie", data);

// 获取告警标记统计
export const getWarningSignStat = (data: any) => request.post("/warningInfo/getWarningSignStat", data);

// 获取告警状态饼图
export const getWarningStausPie = (data: any) => request.post("/warningInfo/getWarningStausPie", data);

// 获取告警处理状态饼图
export const getWarningHandleStausPie = (data: any) => request.post("/warningInfo/getWarningHandleStausPie", data);

// 获取告警设备类型饼图
export const getWarningDeviceTypePie = (data: any) => request.post("/warningInfo/getWarningDeviceTypePie", data);

// 获取告警类型
export const getWarningType = (data: any) => request.post("/securityWarning/getWarningType.do", data);

// 获取告警区域饼图
export const getWarningAreaPie = (data: any) => request.post("/warningInfo/getWarningAreaPie", data);

// 获取告警趋势图表
export const getWarningTrendChartApi = (data: any) => request.post("/warningInfo/getWarningTrendChart", data);

// 获取告警趋势类型
export const getWarningTrendWarnType = () => request.post("/warningInfo/getWarningTrendWarnType");

// 告警上报
export const warningReportTo = (data: any) =>
	request.post("/warningInfo/warningReportTo", data, { operationMessage: "上报成功" });

// 5分钟告警忽略
export const warning5Ignore = (data: any) =>
	request.post("/warningInfo/warning5Ignore", data, { operationMessage: "忽略成功" });

// 2分钟告警忽略
export const warning2Ignore = (data: any) =>
	request.post("/warningInfo/warning2Ignore", data, { operationMessage: "忽略成功" });

// 获取设备网关列表
export const getDeviceGatewayList = (data: any) => request.post("/warningInfo/getListByDeviceId", data);

// 获取设备详情
export const getDetailsByDeviceId = (data: any) =>
	request.post("/warningInfo/getDetailsByDeviceId", data, {
		headers: {
			"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
		}
	});

// 获取新的设备详情
export const getDetailsByDeviceIdNew = (data: any) =>
	request.post("/alarmRecords/getDetailsByDeviceId", data, {
		headers: {
			"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
		}
	});

// 获取设备视频列表
export const deviceVideoList = (data: any) => request.post("/warningInfo/deviceVideoList", data);

// 获取RTSP
export const getsRtsp = (data: any) => request.get("warningInfo/getsRtsp", { params: data });

// 设备运维分页
export const devopsPage = (data: any) => request.get("warningInfo/devopsPage", { params: data });

// 告警等级统计
export const alarmLevel = (data: any) => request.get("/warningInfo/alarmLevel", { params: data });

// 电气安全告警TOP10
export const alarmTop = (data: any) => request.get("/warningInfo/alarmTop", { params: data });

// 告警类型统计
export const alarmType = (data: any) => request.get("/warningInfo/alarmType", { params: data });

// 园区安全排险频次
export const alarmSecurity = (data: any) => request.get("/warningInfo/alarmSecurity", { params: data });

// 区域告警TOP10
export const getWarningAreaTop = (data: any) => request.get("/warningInfo/getWarningAreaTop", { params: data });

// 批量忽略
export const batchIgnore = (data: any) =>
	request.post("/warningInfo/batchIgnore", data, { operationMessage: "批量忽略成功" });

// 设备运维详情
export const devopsByDevice = (data: any) => request.get("/warningInfo/devopsByDevice", { params: data });

// 饼图统计
export const pieChart = (data: any) => request.post("/alarm/statistics/pieChart", data);

// 排名统计
export const rank = (data: any) => request.post("/alarm/statistics/rank", data);

// 处置统计
export const disposal = (data: any) => request.post("/alarm/statistics/disposal", data);

// 告警趋势
export const alarmTrend = (data: any) => request.post("/alarm/statistics/alarmTrend", data);

// 分类列表
export const categoryList = (data: any) => request.get("/alarm/category/list", { params: data });

// 告警数量
export const alarmNum = (data: any) => request.post("/alarm/statistics/alarmNum", data);

// 告警分组列表
export const alarmGroupList = (data: any) => request.get("/alarm/group/list", { params: data });

// 安全评估
export const securityAssessment = (data: any) => request.post("/alarm/statistics/securityAssessment", data);

// 告警内容分页
export const alarmContentPage = (data: any) => request.post("/alarm/content/page", data);

// 设备类型列表
export const deviceTypeList = (data: any) => request.post("/deviceCollect/deviceType", data);

// 保存告警内容
export const saveAlarmContent = (data: any) =>
	request.post("/alarm/content/save", data, {
		operationMessage: data.id ? "编辑成功" : "添加成功"
	});

// 导出模板
export const exportTemplate = (data: any) =>
	request.get("/alarm/content/template", {
		params: data,
		responseType: "blob"
	});

// 导出告警内容
export const exportAlarmContent = (data: any) =>
	request.post("/alarm/content/export", data, {
		responseType: "blob"
	});

// 告警分类分页
export const alarmCategoryPage = (data: any) => request.post("/alarm/category/page", data);

// 保存告警分类
export const saveAlarmCategory = (data: any) =>
	request.post("/alarm/category/save", data, {
		operationMessage: data.id ? "编辑成功" : "添加成功"
	});

// 告警分组分页
export const alarmGroupPage = (data: any) => request.post("/alarm/group/page", data);

// 保存告警分组
export const saveAlarmGroup = (data: any) =>
	request.post("/alarm/group/save", data, {
		operationMessage: data.id ? "编辑成功" : "添加成功"
	});

// 告警计划分页
export const alarmPlanPage = (data: any) => request.post("/alarm/plan/page", data);

// 保存告警计划
export const saveAlarmPlan = (data: any) =>
	request.post("/alarm/plan/save", data, {
		operationMessage: data.id ? "编辑成功" : "添加成功"
	});

// 告警计划时间分页
export const alarmPlanTimePage = (data: any) => request.post("/alarm/plan/time/page", data);

// 根据ID获取时间模板
export const getById = (data: any) => request.get("/alarm/plan/time/getById", { params: data });

// 保存时间模板
export const saveTime = (data: any) =>
	request.post("/alarm/plan/time/save", data, {
		operationMessage: data.id ? "编辑成功" : "添加成功"
	});

// 删除时间模板
export const delTime = (data: any) => request.post("/alarm/plan/time/delete", data, { operationMessage: "删除成功" });

// 批量删除
export const batchDel = (data: any) => request.post("/alarm/plan/batchDel", data, { operationMessage: "批量删除成功" });

// 告警内容列表
export const alarmContentList = (data: any) => request.get("/alarm/content/list", { params: data });

// 切换启用状态
export const switchEnable = (data: any) =>
	request.post("/alarm/plan/switch/enable", data, { operationMessage: "状态切换成功" });

// 告警内容树
export const alarmContentTree = (data: any) => request.get("/alarm/content/tree", { params: data });

// 获取组织列表
export const getOrgList = (data: any) => request.post("/sysDept/getList", data);

// 获取用户树
export const getUserTree = (data: any) => request.post("/sysuser/getUserTree.do", data);

// 获取团队组树
export const teamGroupsTree = (data: any) => request.post("/teamGroups/tree", data);

// 根据ID获取计划
export const getPlanById = (data: any) => request.get("/alarm/plan/getById", { params: data });
