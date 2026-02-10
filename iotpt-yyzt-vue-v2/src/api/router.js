import request from '@/utils/request'

export function getRouterList(data) {
    return request({
        url: '/sysmenu/getMenuTree.do',
        method: 'post',
        params: data,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
        },
    })
}
