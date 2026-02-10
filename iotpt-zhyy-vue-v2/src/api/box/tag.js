import request from "@/utils/request";

export function proLabelSave(data) {
    return request({
        url: "/proLabel/save",
        method: "post",
        data
    });
}

export function selectPageList(data) {
    return request({
        url: "/proLabel/selectPageList",
        method: "post",
        data
    });
}

export function deleteLabel(data) {
    return request({
        url: "/proLabel/deleteLabel",
        method: "get",
        params: data
    });
}

///proLabel/getLabelListByName

export function getLabelListByName(data) {
    return request({
        url: "/proLabel/getLabelListByName",
        method: "get",
        params: data
    });
}

export function countTop(data) {
    return request({
        url: "/proFood/countTop",
        method: "post",
        data
    });
}