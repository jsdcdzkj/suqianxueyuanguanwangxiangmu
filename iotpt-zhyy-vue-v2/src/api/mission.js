import request from "@/utils/request";

/**
 * pageListMission 分页查询
 * saveMission 新增修改
 * deleteMission 删除
 * waitHandle 处理功能
 * inspectionData 处理详情
 * statusCount 根据状态统计总数量
 * detailsMission 编辑|详情
 * uploadFile 上传图片
 * teamGroupsAllList 获取所有班组
 * openStatus 开启处理
 * selectRegionByDevice  根据所属区域获取所有设备
 * selectAreaList 查询所有区域
 * @param {*} data
 * @returns
 */

//分页查询
export function pageListMission(data) {
    return request({
        url: "/mission/pageListMission.do",
        method: "post",
        params: data
    });
}
// 任务库列表查询
export function pageListMission1(data) {
    return request({
        url: "/mission/pageListMission1.do",
        method: "post",
        params: data
    });
}
//新增修改
export function saveMission(data) {
    return request({
        url: "/mission/saveMission.do",
        method: "post",
        data
    });
}
//删除
export function deleteMission(data) {
    return request({
        url: "/mission/deleteMission.do",
        method: "post",
        params: data
    });
}
//处理功能
export function waitHandle(data) {
    return request({
        url: "/mission/waitHandle.do",
        method: "post",
        data: data
    });
}
//处理详情
export function inspectionData(data) {
    return request({
        url: "/mission/inspectionData.do",
        method: "post",
        params: data
    });
}

//上传图片
export function uploadFile(data) {
    return request({
        url: "/mission/uploadFile.do",
        method: "post",
        data
    });
}

//根据状态统计总数量
export function statusCount(data) {
    return request({
        url: "/mission/statusCount.do",
        method: "post",
        params: data
    });
}
//编辑|详情
export function detailsMission(data) {
    return request({
        url: "/mission/detailsMission.do",
        method: "post",
        params: data
    });
}
// 获取所有班组的人员
export function teamGroupsList1(data) {
    return request({
        url: "/mission/teamGroupsList1.json",
        method: "post",
        data
    });
}
//获取所有班组
export function teamGroupsAllList(data) {
    return request({
        url: "/teamGroups/getAllList.do",
        method: "post",
        data
    });
}

//开启处理
export function openStatus(data) {
    return request({
        url: "/mission/openStatus.do?id=" + data,
        method: "post"
    });
}

//根据所属区域Id获取所有设备
export function selectRegionByDevice(data, type) {
    return request({
        url: "/missionItemRecord/selectRegionByDevice.do?areaId=" + data + "&type=" + type,
        method: "post"
    });
}

//查询所有区域
export function selectAreaList(data) {
    return request({
        url: "/missionItemRecord/selectAreaList.do",
        method: "post",
        data
    });
}

//查询所有区域
export function getTaskAuthority() {
    return request({
        url: "/mission/getTaskAuthority.do",
        method: "post"
    });
}

// 下载excel
export function exportExcel(data) {
    return request({
        url: "/mission/exportExcel",
        method: "post",
        data,
        responseType: "blob"
    });
}

// 下载excel
export function exportAssignExcel(data) {
    return request({
        url: "/mission/exportAssignExcel",
        method: "post",
        data,
        responseType: "blob"
    });
}

// 下载excel
export function missionExcel(data) {
    return request({
        url: "/mission/missionExcel",
        method: "post",
        data,
        responseType: "blob"
    });
}

//上传图片
export async function uploadAssignFile(data) {
    return request({
        url: "/mission/uploadAssignFile.do",
        method: "post",
        data
    });
}

//设置已读
export function updateIsReads(data) {
    return request({
        url: "/mission/updateIsReads.do?id=" + data,
        method: "post"
    });
}

//撤回
export function backOut(data) {
    return request({
        url: "/mission/backOut.do",
        method: "post",
        params: data
    });
}

//忽略
export function ignoreMission(data) {
    return request({
        url: "/mission/ignoreMission.do",
        method: "post",
        params: data
    });
}

export function getTimeWarnTJ() {
    return request({
        url: "/mission/getTimeWarnTJ",
        method: "get"
    });
}

///mission/missionRejection

export function missionRejection(data) {
    return request({
        url: "/mission/missionRejection",
        method: "post",
        data: data
    });
}
