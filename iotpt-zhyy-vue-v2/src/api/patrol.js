import request from "@/utils/request";

export function areaTreeList(data) {
    return request({
        url: "/point/areaTreeList",
        method: "post",
        data: data
    });
}

export function addPoint(data) {
    return request({
        url: "/point/add",
        method: "post",
        data: data
    });
}

export function pointList(data) {
    return request({
        url: "/point/getList",
        method: "get",
        params: data
    });
}

export function delPoint(id) {
    return request({
        url: "/point/del",
        method: "get",
        params: { id }
    });
}

export function editPoint(data) {
    return request({
        url: "/point/edit",
        method: "post",
        data: data
    });
}

export function getById(id) {
    return request({
        url: "/point/getById",
        method: "get",
        params: { id }
    });
}

//  巡更任务
export function selectStallList(data) {
    return request({
        url: "/patrolTask/selectStallList",
        method: "get",
        params: data
    });
}

// /patrolTask/getVideo
export function getVideo(id) {
    return request({
        url: "/patrolTask/getVideo",
        method: "get",
        params: { id }
    });
}

export function patrolTaskReport(data, file) {
    const form = new FormData();
    form.append("file", file);
    return request({
        url: "/patrolTask/report",
        method: "post",
        params: data,
        data: form,
        headers: {
            "Content-Type": "application/form-data"
        }
    });
}

export function patrolTaskClock(data) {
    return request({
        url: "/patrolTask/clock",
        method: "post",
        params: data
    });
}

//

export function toAdd(data, file) {
    return request({
        url: "/patrolPlan/toAdd.do",
        method: "post",
        data
    });
}
//
export function getUsers() {
    return request({
        url: "/patrolPlan/getUsers.do",
        method: "post"
    });
}

export function getPageList(data) {
    return request({
        url: "/patrolPlan/getPageList.do",
        method: "post",
        data
    });
}

// /patrolPlan/getPatrolPlanById.do

export function getPatrolPlanById(id) {
    return request({
        url: "/patrolPlan/getPatrolPlanById.do",
        method: "post",
        data: { id }
    });
}

// /patrolPlan/del.do

export function patrolPlanDel(id) {
    return request({
        url: "/patrolPlan/del.do",
        method: "post",
        data: { id }
    });
}

// 修改

export function patrolPlanToEdit(data) {
    return request({
        url: "/patrolPlan/toEdit.do",
        method: "post",
        data
    });
}
// 电子巡检查看

export function patrolTaskGetPoint(id) {
    return request({
        url: "/patrolTask/getPoint",
        method: "get",
        params: { id }
    });
}

// /

export function videoTourInfo(taskId, userId) {
    return request({
        url: "/patrolTask/videoTourInfo",
        method: "get",
        params: { taskId, userId }
    });
}

export function getDeviceVideo(data) {
    return request({
        url: "/patrolPlan/getDeviceVideo.do",
        method: "post",
        data
    });
}

export function getPageListPatrol(data) {
    return request({
        url: "patrolStatistic/getPageList.do",
        method: "post",
        data
    });
}

export function getStatisticView(data) {
    return request({
        url: "patrolStatistic/getStatisticView.do",
        method: "post",
        data
    });
}
