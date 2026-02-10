import request from "@/utils/request";

/**
 * redis
 * 数据字典列表
 */
export function getRedisDictList(data) {
    return request({
        url: "/sysDict/getRedisDictList",
        method: "post",
        params: data
    });
}

/**
 * 任务类型的字典
 * @param {*} data
 * @returns
 */
export function selectMissionDictAll(data) {
    return request({
        url: "/sysDict/selectMissionDictAll",
        method: "post",
        params: data
    });
}
