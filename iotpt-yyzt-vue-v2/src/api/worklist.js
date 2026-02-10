import request from '@/utils/request'

/**
 * 字典相关请求模块
 */
var baseUrl = '/ai/model/tag'


// 新增或者编辑
export function saveOrUpdate(data) {
    return request({
        url: baseUrl + '/save',
        method: 'post',
        data,
    })
}

// 得到字典集合
export function getList(data) {
    return request({
        url: baseUrl + '/page',
        method: 'post',
        data,
    })
}

// 删除
export function del(data) {
    return request({
        url: baseUrl + '/delete',
        method: 'post',
        params: data,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
        },
    })
}

