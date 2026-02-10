import request from '@/utils/request'

export function getPage(data) {
    return request({
        url: '/deviceAccessControl/getPageList',
        method: 'post',
        params: data,
    })
}

export function saveOrUpdate(data){
    return request({
        url: '/deviceAccessControl/saveOrUpdate',
        method: 'post',
        data,
    })
}

export function del(data) {
    return request({
        url: '/deviceAccessControl/delete',
        method: 'post',
        params: data,
    })
}


// 门禁管理分页查询
export function doorPage(data) {
    return request({
        url: '/door/doorPage',
        method: 'post',
        data,
    })
}

export function addDoor(data) {
    return request({
        url: '/door/addDoor',
        method: 'post',
        data,
    })
}

export function updateDoor(data) {
    return request({
        url: '/door/updateDoor',
        method: 'post',
        data,
    })
}

export function delDoor(data) {
    return request({
        url: '/door/delDoor',
        method: 'post',
        data,
    })
}

export function getEntity(data) {
    return request({
        url: '/deviceAccessControl/getEntity',
        method: 'post',
        params: data,
    })
}

export function areaTreeList(data) {
    return request({
        url: '/sys_org_manage/areaTreeList',
        method: 'post',
        data,
    })
}
