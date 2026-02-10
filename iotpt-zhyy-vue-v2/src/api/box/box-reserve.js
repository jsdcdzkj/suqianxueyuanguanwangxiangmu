import request from "@/utils/request";
//包厢新增、编辑
export function saveOrUp(data) {
    return request({
        url: "/proBox/saveOrUp",
        method: "post",
        data
    });
}

// 新增菜品分类
export function proCategorySave(data) {
    return request({
        url: "/proCategory/save",
        method: "post",
        data
    });
}

// 获取菜品分类列表
export function getCategoryList(data) {
    return request({
        url: "/proCategory/getCategoryList",
        method: "post",
        data
    });
}

// 获取菜品分类分页列表

export function selectPageList(data) {
    return request({
        url: "/proCategory/selectPageList",
        method: "post",
        data
    });
}

// 删除菜品分类
export function deleteFood(data) {
    return request({
        url: "/proCategory/deleteFood",
        method: "get",
        params: data
    });
}

export function getCategoryTreeList(data) {
    return request({
        url: "/proCategory/getCategoryTreeList",
        method: "get",
        params: data
    });
}
