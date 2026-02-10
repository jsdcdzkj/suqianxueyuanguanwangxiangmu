import request from "@/utils/request";

/**
 * 分页查询
 * @param {*} data
 * @returns
 */
export function sportsGetPageList(data) {
    return request({
        url: "/sports/getProPage",
        method: "post",
        data: data
    });
}

/**
 * 分页查询
 * @param {*} data
 * @returns
 */
export function getProRecordList(data) {
    return request({
        url: "/sports/getProRecordList",
        method: "post",
        data: data
    });
}

/**
 * 查看详情
 * @param {*} data
 * @returns
 */
export function getDetails(data) {
    return request({
        url: "/sports/getDetails",
        method: "POST",
        data: data
    });
}

/**
 * 添加/编辑
 * @param {*} data
 * @returns
 */
export function sportsSaveOrUpdate(data) {
    return request({
        url: "/sports/saveOrUpdate",
        method: "post",
        data: data
    });
}

/**
 * 删除
 * @param {*} data
 * @returns
 */
export function sportsDelete(data) {
    return request({
        url: "/sports/delete",
        method: "post",
        data: data
    });
}

/**
 * 删除
 * @param {*} data
 * @returns
 */
export function getSportsStatistics(data) {
    return request({
        url: "/sports/getSportsStatistics",
        method: "POST",
        data: data
    });
}

/**
 * 健身房审批
 */
export function gymApproval(data) {
    return request({
        url: "/sports/gymApproval",
        method: "post",
        data: data
    });
}

/**
 * 健身房审批
 */
export function returnOrder(data) {
    return request({
        url: "/sports/returnOrder",
        method: "post",
        data: data
    });
}
/**
 * /sports/getPersonalRecordList
 */
export function getPersonalRecordList(data) {
    return request({
        url: "/sports/getPersonalRecordList",
        method: "post",
        data: data
    });
}

//app/sportsRecords/getWeekCount
export function linechart(data) {
    return request({
        url: "/sports/linechart",
        method: "get",
        params: data
    });
}

// recreation/getPersonList

export function getPersonList(data) {
    return request({
        url: "/recreation/getPersonList",
        method: "get",
        params: data
    });
}

export function selectRecreation(data) {
    return request({
        url: "/recreation/selectRecreation",
        method: "get",
        params: data
    });
}

export function saveRecreations(data) {
    return request({
        url: "/recreation/saveRecreations",
        method: "post",
        data
    });
}
