import request from "@/utils/request";

//根据租户ID查询房间信息
export function getEntity(data) {
    return request({
        url: "/tenant/room/byTenant",
        method: "get",
        params: data
    });
}

// 房间分页查询
export function getPages(data) {
    return request({
        url: "/tenant/room/page",
        method: "post",
        data
    });
}

// 租赁房间统计
export function getStatistics(data) {
    return request({
        url: "/tenant/room/statistics",
        method: "get",
        params: data
    });
}

// 保存房间信息
export function saveRoom(data) {
    return request({
        url: "/tenant/room/save",
        method: "post",
        data
    });
}
// 查询未租赁的房间列表
export function roomList(data) {
    return request({
        url: "/tenant/room/list",
        method: "post",
        data
    });
}

export function getDeviceList(data) {
    return request({
        url: "/tenant/room/device/electricity/list",
        method: "get",
        params: data
    });
}

// 租赁房间已选设备列表
export function deviceListByRoomId(data) {
    return request({
        url: "/tenant/room/device/list",
        method: "post",
        data: data
    });
}

// 批量保存租赁房间设备
export function deviceSave(data) {
    return request({
        url: "/tenant/room/device/save",
        method: "post",
        data: data
    });
}

// 删除租赁房间设备
export function deviceDelete(data) {
    return request({
        url: "/tenant/room/device/delete",
        method: "post",
        data: data
    });
}

// http://172.168.10.52:7072/configTenantNotice/saveOrUpdate

export function configTenantNoticeSaveOrUpdate(data) {
    return request({
        url: "/configTenantNotice/saveOrUpdate",
        method: "post",
        data: data
    });
}

export function configTenantNoticeGetEntityByTId(data) {
    return request({
        url: "/configTenantNotice/getEntityByTId",
        method: "get",
        params: data
    });
}

export function roomUpload(data) {
    return request({
        url: "tenant/room/upload",
        method: "post",
        data
    });
}

export function roomUpdate(data) {
    return request({
        url: "tenant/room/save/update",
        method: "post",
        data
    });
}

export function historyPage(data) {
    return request({
        url: "tenant/room/history/page",
        method: "post",
        data
    });
}

export function switchEnable(data) {
    return request({
        url: "tenant/room/switch/enable",
        method: "post",
        data
    });
}

// 分页查询房间列表
export function roomPage(data) {
    return request({
        url: "/hireRoom/Page",
        method: "post",
        data
    });
}
// 房间租赁数据统计
export function roomStatistics(data) {
    return request({
        url: "/hireRoom/getNum",
        method: "post",
        data
    });
}
// 删除房间
export function roomDel(data) {
    return request({
        url: "/hireRoom/del",
        method: "post",
        data
    });
}
// 新增房间
export function roomAdd(data) {
    return request({
        url: "/hireRoom/save",
        method: "post",
        data
    });
}
// 房间详情
export function roomDetail(data) {
    return request({
        url: "/hireRoom/detail",
        method: "post",
        data
    });
}
// 租户采集终端设备列表
export function deviceListNew(data) {
    return request({
        url: "/hireRoom/getDevice",
        method: "post",
        data
    });
}
//房态图统计
export function roomStatusStatistics(data = {}) {
    return request({
        url: "/hireRoom/statistics",
        method: "post",
        data
    });
}
//房态图列表
export function roomStatusList(data = {}) {
    return request({
        url: "/hireRoom/getRoom",
        method: "post",
        data
    });
}
// 房间绑定设备
export function roomBindDevice(data = {}) {
    return request({
        url: "/hireRoom/saveDevice",
        method: "post",
        data
    });
}
// 房间解绑设备
export function roomUnBindDevice(data = {}) {
    return request({
        url: "/hireRoom/delDevice",
        method: "post",
        data
    });
}
