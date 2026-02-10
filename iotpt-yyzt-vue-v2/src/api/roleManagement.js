import request from '@/utils/request'

/**
 * 角色相关请求
 */
var baseUrl = "sysRole"

export function getPageList(data) {
    return request({
        url: baseUrl + '/getRolePage.do',
        method: 'post',
        params: data,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
        },
    })
}

// 获取角色列表
export function getList(data) {
    return request({
        url: baseUrl + '/getRoleList.do',
        method: 'post',
        params: data,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
        },
    })

}

// 新增或者修改角色
export function doEdit(data) {
    return request({
        url: baseUrl + '/saveRole.do',
        method: 'post',
        data,
    })
}

export function doDelete(data) {
    return request({
        url: baseUrl + '/delRole.do',
        method: 'post',
        params: data,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
        },
    })
}

// 根据角色id 查询关联菜单
export function getRoleMenu(data) {
    return request({
        url: baseUrl + '/getRoleMenu.do',
        method: 'post',
        params: data,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
        },
    })
}

// 根据角色,查询绑定的用户
export function getRoleUser(data) {
    return request({
        url: baseUrl + '/getRoleUser.do',
        method: 'post',
        params: data,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
        },
    })
}
