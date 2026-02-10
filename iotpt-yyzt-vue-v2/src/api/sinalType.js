import request from '@/utils/request'

export function getPage(data) {
    return request({
        url: '/configSignalType/getPage',
        method: 'post',
        params: data,
    })
}

export function saveOrUpdate(data) {
    return request({
        url: '/configSignalType/saveOrUpdate',
        method: 'post',
        data,
    })
}

export function del(data) {
    return request({
        url: '/configSignalType/delete',
        method: 'post',
        params: data,
    })
}

export function getEntity(data) {
    return request({
        url: '/configSignalType/getEntity',
        method: 'post',
        params: data,
    })
}