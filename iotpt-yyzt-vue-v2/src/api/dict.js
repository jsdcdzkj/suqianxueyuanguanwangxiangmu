import request from '@/utils/request'

/**
 * 字典相关请求模块
 */
var baseUrl = '/sysDict'

export function getDictList(data) {
    return request({
        url: baseUrl + '/getPage',
        method: 'post',
        params: data,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
        },
    })
}

export function getDictTree(data) {
    return request({
        url: baseUrl + '/getTree',
        method: 'post',
        params: data,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
        },
    })
}

// 查询所有类型
export function getDictTypeList() {
    return request({
        url: baseUrl + '/getDictTypeList',
        method: 'post',
    })
}

// 新增或者编辑
export function saveOrUpdate(data) {
    return request({
        url: baseUrl + '/saveOrUpdate',
        method: 'post',
        data,
    })
}

// 得到字典集合
export function getList(data) {
    return request({
        url: baseUrl + '/getList',
        method: 'post',
        params: data,
    })
}

// 删除
export function deleteDict(data) {
    return request({
        url: baseUrl + '/delete',
        method: 'post',
        params: data,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
        },
    })
}

