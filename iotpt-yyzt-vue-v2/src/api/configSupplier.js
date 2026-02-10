import request from '@/utils/request'

/**
 * 供应商管理
 * @param data
 * @returns {*}
 */

// 列表分页查询
export function getPageList(data) {
    return request({
        url: '/configSupplier/getPageList',
        method: 'post',
        params: data
    })
}

export function getConfigSupplierList(data) {
    return request({
        url: '/configSupplier/getList',
        method: 'post',
        params: data
    })
}

// 保存或修改
export function saveOrUpdate(data) {
    return request({
        url: '/configSupplier/saveOrUpdate',
        method: 'post',
        data
    })
}

export function getEntity(data) {
    return request({
        url: '/configSupplier/getById',
        method: 'post',
        params: data
    })
}

// 删除
export function onDel(data) {
    return request({
        url: '/configSupplier/delete',
        method: 'post',
        params: data
    })
}

// -------------------- 型号管理 --------------------
export function getModelList(data) {
    return request({
        url: '/configSupplier/getModelList',
        method: 'post',
        params: data
    })
}

