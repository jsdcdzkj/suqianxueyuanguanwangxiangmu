import request from '@/utils/request'

export function getPage(data) {
    return request({
        url: '/configDeviceSubitem/getPageList',
        method: 'post',
        params: data,
    })
}

export function saveOrUpdate(data) {
    return request({
        url: '/configDeviceSubitem/saveOrUpdate',
        method: 'post',
        data,
    })
}

export function del(data) {
    return request({
        url: '/configDeviceSubitem/delete',
        method: 'post',
        params: data,
    })
}

export function getEntity(data) {
    return request({
        url: '/configDeviceSubitem/getById',
        method: 'post',
        params: data,
    })
}