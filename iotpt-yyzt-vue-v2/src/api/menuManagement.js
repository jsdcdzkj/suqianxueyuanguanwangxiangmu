import request from '@/utils/request'

export function getTree(data) {
    return request({
        url: '/sysmenu/getMenu.do',
        method: 'post',
        data,
    })
}

export function getMenuList(data) {
    return request({
        url: '/sysmenu/getMenuList.do',
        method: 'post',
        params: data,
        // 重写请求头 content-type 为 application/x-www-form-urlencoded
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
        },
    })
}

// 查询菜单树
export function getMenuTree(data) {
    return request({
        url: '/sysmenu/getMenuTree.do',
        method: 'post',
        params: data,
        // 重写请求头 content-type 为 application/x-www-form-urlencoded
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
        },
    })
}

// 修改显示隐藏状态
export function doChangeStatus(data) {
    return request({
        url: '/sysmenu/doChangeStatus',
        method: 'post',
        params: data,
        // 重写请求头 content-type 为 application/x-www-form-urlencoded
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
        },
    })
}

// 新增或者修改菜单
export function doEdit(data) {
    return request({
        url: '/sysmenu/saveMenu.do',
        method: 'post',
        data,
    })
}

export function doDelete(data) {
    return request({
        url: '/sysmenu/delMenu.do',
        method: 'post',
        params: data,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
        },
    })
}

// 查询菜单树-根据系统id
export function getMenuTypeTree(data) {
    return request({
        url: '/sysmenu/getMenuTypeTree.do',
        method: 'post',
        params: data,
        // 重写请求头 content-type 为 application/x-www-form-urlencoded
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
        },
    })
}
