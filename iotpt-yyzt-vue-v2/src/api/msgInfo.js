import request from '@/utils/request'
/**
 *消息服务
 */
var baseUrl = '/msgMessageInfo'

/**
 * 分页
 * @param data
 * @returns {AxiosPromise}
 */
export function getMsgPage(data) {
    return request({
        url: baseUrl + '/getMsgPage.do',
        method: 'post',
        params: data,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
        },
    })
}

/**
 * 修改状态
 * @param data
 * @returns {AxiosPromise}
 */
export function changeMsg(data) {
    return request({
        url: baseUrl + '/changeMsg.do',
        method: 'post',
        params: data,
    })
}

/**
 * 删除
 * @param data
 * @returns {AxiosPromise}
 */
export function delOne(data) {
    return request({
        url: baseUrl + '/del.do',
        method: 'post',
        params: data,
    })
}


/**
 * 新增删除
 * @param data
 * @returns {AxiosPromise}
 */
export function doEdit(data) {
    return request({
        url: baseUrl + '/edit.do',
        method: 'post',
        params: data,
    })
}
