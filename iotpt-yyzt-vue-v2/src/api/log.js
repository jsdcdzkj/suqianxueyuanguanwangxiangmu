import request from '@/utils/request'

/**
 * 日志相关请求
 */
var baseUrl = "sysLog"

// 获取日志列表
export function getPageList(data) {
    return request({
        url: baseUrl + '/getPage',
        method: 'post',
        params: data,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
        },
    })
}

// 查询所有日志类型数据
export function getTypeAll() {
    return request({
        url: baseUrl + '/getTypeAll',
        method: 'post',
    })
}
