import request from '@/utils/request'

export function getPage(data) {
    return request({
        url: '/chint/device/point/page',
        method: 'post',
        data,
    })
}

export function saveOrUpdate(data) {
    return request({
        url: '/chint/device/point/edit',
        method: 'post',
        data,
    })
}

export function del(params) {
    return request({
        url: '/chint/device/point/del',
        method: 'get',
        params,
    })
}

export function refresh(data) {
    return request({
        url: '/chint/device/point/refresh',
        method: 'post',
        data,
    })
}

