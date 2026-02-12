import { request } from "../instance";

// 分页查询
export const pageListMission = (data: any) =>
request.post("/mission/pageListMission.do", {}, {params: data});

// 任务库列表查询
export const pageListMission1 = (data: any) =>
request.post("/mission/pageListMission1.do", {}, {params: data});

// 新增修改
export const saveMission = (data: any) =>
	request.post("/mission/saveMission.do", data);

// 删除
export const deleteMission = (data: any) =>
request.post("/mission/deleteMission.do", {}, {params: data});

// 处理功能
export const waitHandle = (data: any) => request.post("/mission/waitHandle.do", data);

// 处理详情
export const inspectionData = (data: any) =>
request.post("/mission/inspectionData.do", {}, {params: data});

// 上传图片
export const uploadFile = (data: any) =>
	request.post("/mission/uploadFile.do", data);

// 根据状态统计总数量
export const statusCount = (data: any) =>
request.post("/mission/statusCount.do", {}, {params: data});

// 编辑|详情
export const detailsMission = (data: any) =>
request.post("/mission/detailsMission.do", {}, {params: data});

// 获取所有班组的人员
export const teamGroupsList1 = (data: any) => request.post("/mission/teamGroupsList1.json", data);

// 获取所有班组
export const teamGroupsAllList = () => request.post("/teamGroups/getAllList.do", {});

// 开启处理
export const openStatus = (id: string | number) =>
	request.post(`/mission/openStatus.do?id=${id}`, {});

// 根据所属区域Id获取所有设备
export const selectRegionByDevice = (data: Object) =>
	request.post("/missionItemRecord/selectRegionByDevice.do?areaId=" + data.data + "&type=" + data.type, {});

// 查询所有区域
export const selectAreaList = (data: any) => request.post("/missionItemRecord/selectAreaList.do", data);

// 查询任务权限
export const getTaskAuthority = (data: any) => request.post("/mission/getTaskAuthority.do", data);

// 下载excel
export const exportAssignExcel = (data: any) =>
	request.get("/mission/exportAssignExcel", {params:data,responseType: "blob"});

// 下载excel
export const missionExcel = (data: any) =>
	request.post("/mission/missionExcel", data,{responseType: "blob"});

// 上传图片
export const uploadAssignFile = (data: any) => request.post("/mission/uploadAssignFile.do", data);

// 设置已读
export const updateIsReads = (id: string | number) =>
	request.post(`/mission/updateIsReads.do?id=${id}`, {});


// 撤回
export const backOut = (data: any) =>
request.post("/mission/backOut.do", {}, {params: data});

// 忽略
export const ignoreMission = (data: any) =>
request.post("/mission/ignoreMission.do", {}, {params: data});

// 获取时间预警统计
export const getTimeWarnTJ = (data: any) => request.get("/mission/getTimeWarnTJ", { params: data });

//
export const missionRejection = (data: any) => request.post("/mission/missionRejection", data);


// 指派相关数据
// 待指派
export const tobeAssigned = (data: any) => request.post("/missionAssign/tobeAssigned.do", {}, {params: data});


