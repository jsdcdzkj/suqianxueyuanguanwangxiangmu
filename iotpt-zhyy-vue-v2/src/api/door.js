import request from "@/utils/request";

/**
 * 楼层管理
 * @param {*} data
 * @returns
 */
export function floorDataList(data) {
    return request({
        url: "/door/floorDataList.json",
        method: "post",
        params: data
    });
}

/**
 * 根据楼层获取门禁设备
 * @param {*} data
 * @returns
 */
export function doorDataList(data) {
    return request({
        url: "/door/doorDataList.json",
        method: "post",
        params: data
    });
}

export function delDoorPurview(id) {
    return request({
        url: "/door/delDoorPurview",
        method: "post",
        params: { id }
    });
}

/**
 * 常开，常关，普通状态操作
 * @param {*} data
 * @returns
 */
export function updDoor(data) {
    return request({
        url: "/door/updDoor",
        method: "post",
        params: data
    });
}

/**
 * 远程开关状态操作
 * @param {*} data
 * @returns
 */
export function openDoor(data) {
    return request({
        url: "/door/openDoor",
        method: "post",
        params: data
    });
}

/**
 * 获取所有部门
 * @param {*} data
 * @returns
 */
export function selectDeptList(data) {
    return request({
        url: "/door/selectDeptList.json",
        method: "post",
        params: data
    });
}

/**
 * 获取所有公司
 * @param {*} data
 * @returns
 */
export function selectOrgManageList(data) {
    return request({
        url: "/door/selectOrgManageList.json",
        method: "post",
        params: data
    });
}

/**
 * 根据公司获取部门
 * @param {*} data
 * @returns
 */
export function selectOrgDeptList(data) {
    return request({
        url: "/door/selectOrgDeptList.json?orgId=" + data,
        method: "post"
    });
}

/**
 * 根据部门获取用户
 * @param {*} data
 * @returns
 */
export function selectOrgUserList(data) {
    return request({
        url: "/door/selectOrgUserList.json?deptId=" + data,
        method: "post"
    });
}

/**
 * 保存门禁权限
 * @param {*} data
 * @returns
 */
export function addPurview(data) {
    return request({
        url: "/door/addPurview.json",
        method: "post",
        params: data
    });
}

/**
 * 通行记录
 * @param {*} data
 * @returns
 */
export function doorRecordPageList(data) {
    return request({
        url: "/door/doorRecordPageList.json",
        method: "post",
        params: data
    });
}
/**
 * 通行权限
 * @param {*} data
 * @returns
 */
export function getDoorUserList(data) {
    return request({
        url: "/door/getDoorUserList.json",
        method: "post",
        data,
        headers: {
            "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
        }
    });
}

/**
 * 生成测试数据，后期删掉
 * @param {*} data
 * @returns
 */
export function scsj(data) {
    return request({
        url: "/door/scsj.json",
        method: "post",
        params: data
    });
}

export function scsj11(data) {
    return request({
        url: "/door/scsj11.json",
        method: "post",
        params: data
    });
}

export function getDoorRole(data) {
    return request({
        url: "/door/getDoorRole.do",
        method: "post",
        params: data
    });
}

export function getGroupList(data) {
    return request({
        url: "/door/getGroupList.json",
        method: "post",
        params: data
    });
}
