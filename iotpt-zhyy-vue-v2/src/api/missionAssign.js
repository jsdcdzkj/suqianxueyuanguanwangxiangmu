import request from '@/utils/request'

/**
 * tobeAssigned 待指派
 * selectTermQuery 根据条件查询查询数据
 * selectRegionByDevice 根据区域id获取相关的设备
 * @param {*} data 
 * @returns 
 */

//待指派
export function tobeAssigned(data) {
    return request({
        url: '/missionAssign/tobeAssigned.do',
        method: 'post',
        params: data
    })
}

//根据条件查询查询数据
export function selectTermQuery(data) {
    return request({
        url: '/missionAssign/selectTermQuery.do',
        method: 'post',
        params: data
    })
}


//根据区域id获取相关的设备
export function selectRegionByDevice(data) {
    return request({
        url: '/missionItemRecord/selectRegionByDevice.do',
        method: 'post',
        params: data
    })
}


//根据班组获取人员
export function teamGroupsList(data) {
    return request({
        url: '/assignUser/teamGroupsList.do?groupId='+data,
        method: 'post'
    })
}

//保存指派人
export function saveAssignUser(data) {
    return request({
        url: '/assignUser/saveAssignUser.do',
        method: 'post',
        data
    })
}