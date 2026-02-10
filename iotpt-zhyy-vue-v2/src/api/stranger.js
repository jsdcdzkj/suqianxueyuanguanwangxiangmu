import request from '@/utils/request'

/**
 * 陌生人管理分页
 * @param {*} data 
 * @returns 
 */
export function strangerDataList(data) {
    return request({
        url: '/stranger/strangerDataList.json',
        method: 'post',
        params: data
    })
}

/**
 * 删除|批量删除
 * @param {*} data 
 * @returns 
 */
export function deleteById(data) {
    return request({
        url: '/stranger/deleteById.json',
        method: 'post',
        data
    })
}

/**
 * 抓拍
 * @param {*} data 
 * @returns 
 */
export function snapUser(data) {
    return request({
        url: '/stranger/snapUser.json',
        method: 'post',
        params: data
    })
}

/**
 * 对比轨迹记录图片
 * @param {*} data 
 * @returns 
 */
export function tracking(data) {
    return request({
        url: '/stranger/tracking.json',
        method: 'post',
        params: data
    })
}



/**
 * 生成测试数据，后期删除
 * @param {*} data 
 * @returns 
 */
export function scsj(data) {
    return request({
        url: '/stranger/scsj.json',
        method: 'post',
        params: data
    })
}

