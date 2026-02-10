import request from '@/utils/request'


export function addPeakGuPingTime(data) {
    return request({
        url: '/peak_gu_ping_time/addPeakGuPingTime',
        method: 'post',
        data,
    })
}

export function updatePeakGuPingTime(data) {
    return request({
        url: '/peak_gu_ping_time/updatePeakGuPingTime',
        method: 'post',
        data,
    })
}

export function delPeakGuPingTime(data) {
    return request({
        url: '/peak_gu_ping_time/delPeakGuPingTime?id='+data,
        method: 'post',
    })
}


export function getList(data) {
    return request({
        url: '/peak_gu_ping_time/getList',
        method: 'post',
        data,
    })
}

export function info(data) {
    return request({
        url: '/peak_gu_ping_time/info?id='+id,
        method: 'post',
    })
}

