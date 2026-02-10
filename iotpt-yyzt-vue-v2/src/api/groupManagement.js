import request from '@/utils/request'

/**
 * 分组管理
 */
var baseUrl = '/configGroup'

// 获取分组列表分页
export function getList(data) {
    return request({
        url: baseUrl + '/getPageList',
        method: 'post',
        params: data,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
        },
    })
}

// 新增或者修改分组
export function doEdit(data) {
    if (data.id) {
        return request({
            url: baseUrl + '/toEdit.do',
            method: 'post',
            data,
        })
    } else {
        return request({
            url: baseUrl + '/toAdd.do',
            method: 'post',
            data,
        })
    }
}

// 删除分组
export function doDelete(data) {
    return request({
        url: baseUrl + '/toEdit.do',
        method: 'post',
        data
    })
}

// 用户列表
export function getRedisUserDictMap(data) {
    return request({
        url: baseUrl + '/getRedisUserDictMap',
        method: 'post',
        data,
    })
}

// 分组单位列表
export function getManageDeptUserTree(data) {
    return request({
        url: baseUrl + '/getManageDeptUserTree.do',
        method: 'post',
        data,
    })
}
