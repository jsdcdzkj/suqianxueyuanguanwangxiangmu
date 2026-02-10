import request from '@/utils/request'

/**
 * 峰谷平电费配置相关请求
 */
var baseUrl = "peakGuPing"

//获取列表
export function getAll(data) {
    return request({
        url: baseUrl + '/getAll',
        method: 'post',
        data,
    })
}
//新增编辑
export function saveOrUpdate(data) {
    return request({
        url: baseUrl + '/saveOrUpdate',
        method: 'post',
        data,
    })
}




