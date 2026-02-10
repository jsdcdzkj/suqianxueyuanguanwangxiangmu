import request from '@/utils/request'

export function getPage(data) {
    return request({
        url: '/DeviceVideo/getPageList',
        method: 'post',
        params: data,
    })
}

export function saveOrUpdate(data) {
    return request({
        url: '/DeviceVideo/saveOrUpdate',
        method: 'post',
        data,
    })
}

export function del(data) {
    return request({
        url: '/DeviceVideo/delete',
        method: 'post',
        params: data,
    })
}

export function getEntity(data) {
    return request({
        url: '/DeviceVideo/getById',
        method: 'post',
        params: data,
    })
}

export function typeSelectLists() {
    return request({
        url: '/DeviceVideo/typeSelectLists',
        method: 'post',
    })
}

export function getGroundLockParkingDict() {
    return request({
        url: '/DeviceVideo/getGroundLockParkingDict',
        method: 'post',
    })
}

export function saveGroundLock(data) {
    return request({
        url: '/DeviceVideo/saveGroundLock',
        method: 'post',
        data
    })
}

export function getGroundLockByDeviceId(data) {
    return request({
        url: '/DeviceVideo/getGroundLockByDeviceId',
        method: 'post',
        params:data
    })
}

export function syncDahua(data) {
    return request({
        url: '/DeviceVideo/syncDahua',
        method: 'post',
        params:data
    })
}