import request from "@/utils/request";

export function findYouselfAll(data) {
    return request({
        url: "/clockingRecord/findYouselfAll",
        method: "post",
        params: data
    });
}

export function getPageList(data) {
    return request({
        url: "/clockingRecord/getPageList",
        method: "post",
        params: data
    });
}

export function findYouself(data) {
    return request({
        url: "/clockingRecord/findYouself",
        method: "post",
        params: data
    });
}

export function groupH(data) {
    return request({
        url: "/clockingRecord/groupH",
        method: "post",
        params: data
    });
}

//部门列表
export function getDept(data) {
    return request({
        url: "/door/selectDeptList.json",
        method: "post",
        data
    });
}

//打卡记录列表
export function getUserRecord(data) {
    return request({
        url: "/clockingRecord/getUserRecord",
        method: "post",
        data
    });
}

//打卡记录统计
export function getRecordStatic(data) {
    return request({
        url: "/clockingRecord/getRecordStatic",
        method: "post",
        data
    });
}

//时间段配置
export function saveTimeConfig(data) {
    return request({
        url: "/clockingConfig/saveOrUpd",
        method: "post",
        data
    });
}

//获取时间段配置信息
export function getTimeConfig(data) {
    return request({
        url: "/clockingConfig/selectConfigDetails",
        method: "post",
        data
    });
}

//个人打卡记录统计
export function getUserStatic(data) {
    return request({
        url: "/clockingRecord/getUserStatic",
        method: "post",
        data
    });
}

//获取出勤配置
export function getMonthConfig(params) {
    return request({
        url: "/attendanceDate/getAttendanceDateList",
        method: "get",
        params
    });
}

//保存出勤配置
export function saveMonthConfig(data) {
    return request({
        url: "/attendanceDate/updateAttendanceDatePersonState",
        method: "post",
        data
    });
}
