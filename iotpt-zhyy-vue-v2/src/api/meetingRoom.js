import request from "@/utils/request";

/**
 * 智慧应用 会议室
 */

/**
 * 设备分项
 */
export function getMeetingRoomPage(data) {
    return request({
        url: "/meetingRoom/getMeetingRoomPage",
        method: "post",
        params: data,
        headers: {
            "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
        }
    });
}

/**
 * 根据id获取会议室
 */
export function getMeetingRoomById(data) {
    return request({
        url: "/meetingRoom/getMeetingRoomById",
        method: "post",
        params: data,
        headers: {
            "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
        }
    });
}

/**
 * 新增或者编辑
 */
export function saveOrUpdate(data) {
    return request({
        url: "/meetingRoom/addMeetingRoom",
        method: "post",
        data
    });
}

/**
 * 删除会议室
 */
export function delMeetingRoom(data) {
    return request({
        url: "/meetingRoom/delMeetingRoom",
        method: "post",
        params: data,
        headers: {
            "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
        }
    });
}

/**
 * 新增/编辑会议室预约
 */
export function saveOrUpdateMeetingRoomOrder(data) {
    return request({
        url: "/meetingRoom/addMeetingRoomOrder",
        method: "post",
        data
    });
}

/**
 * 获取会议室预约列表
 */
export function getMeetingRoomOrderPage(data) {
    return request({
        url: "/meetingRoom/getMeetingRoomOrderPage",
        method: "post",
        params: data,
        headers: {
            "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
        }
    });
}

/**
 * 根据id查询会议
 */
export function getMeetingRoomOrderById(data) {
    return request({
        url: "/meetingRoom/getMeetingRoomOrderById",
        method: "post",
        params: { id: data },
        headers: {
            "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
        }
    });
}

/**
 * 查询会议室预约状态
 */
export function getMeetingRoomOrderList(data) {
    return request({
        url: "/meetingRoom/getMeetingRoomOrderList",
        method: "post",
        params: data,
        headers: {
            "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
        }
    });
}

/**
 * 查询服务类型列表
 */
export function getServiceList(data) {
    return request({
        url: "/serviceOptions/getUsableServiceOption",
        method: "post",
        params: data,
        headers: {
            "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
        }
    });
}

/**
 * 场景列表
 */
export function getSceneList(data) {
    return request({
        url: "/scene/getSceneList",
        method: "post",
        params: data
    });
}
/**
 * 场景列表new
 */
export function getSceneListNew(data) {
    return request({
        url: "/meetingScene/getSceneList",
        method: "get",
        params: data
    });
}
/**
 * 场景设备
 */
export function getSceneDeviceInfoBySceneId(data) {
    return request({
        url: "/meetingSceneDeviceInfo/getSceneDeviceInfoBySceneId",
        method: "get",
        params: data
    });
}

/**
 * 场景添加
 */
export function addScene(data) {
    return request({
        url: "/scene/addScene",
        method: "post",
        params: data
    });
}

/**
 * 场景添加new
 */
export function addSceneNew(data) {
    return request({
        url: "/meetingScene/saveOrUpd",
        method: "post",
        data
    });
}

/**
 * 设备保存
 */
export function getSceneListSave(data) {
    return request({
        url: "/meetingSceneDeviceInfo/getSceneList",
        method: "post",
        data
    });
}
/**
 * 设备保存 -- 新
 */
export function sceneDeviceSaveOrUpd(data) {
    return request({
        url: "/meetingSceneDeviceInfo/saveOrUpd",
        method: "post",
        data
    });
}
/**
 * 场景修改
 */
export function updateScene(data) {
    return request({
        url: "/scene/updateScene",
        method: "post",
        params: data
    });
}

/**
 * 场景删除
 */
export function delScene(data) {
    return request({
        url: "/scene/delScene",
        method: "post",
        params: data
    });
}

/**
 * 场景删除--new
 */
export function delSceneNew(data) {
    return request({
        url: "/meetingScene/deleteById",
        method: "get",
        params: data
    });
}
/**
 * 设备调控
 */
export function getSceneDeviceList(data) {
    return request({
        url: "/scene/getSceneDeviceList",
        method: "post",
        params: data
    });
}

/**
 * 设备调控修改
 */
export function editSceneDeviceList(data) {
    return request({
        url: "/scene/editSceneDevice",
        method: "post",
        data
    });
}

/**
 * 设备调控修改
 */
export function getDeviceList(data) {
    return request({
        url: "/meetingSceneControl/getDeviceList",
        method: "get",
        params: data
    });
}
