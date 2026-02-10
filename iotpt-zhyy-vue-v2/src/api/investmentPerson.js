import request from "@/utils/request";

export function getUserPage(data) {
    return request({
        url: "/investmentPerson/getUserPage.do?realName=",
        method: "post",
        params: data
    });
}

export function investmentPersonList(data) {
    return request({
        url: "/investmentPerson/investmentPersonList",
        method: "post",
        data
    });
}

export function delInvestmentPerson(data) {
    return request({
        url: "/investmentPerson/delInvestmentPerson",
        method: "post",
        data
    });
}

export function addInvestmentPerson(data) {
    return request({
        url: "/investmentPerson/addInvestmentPerson",
        method: "post",
        data
    });
}

export function propertyList(data) {
    return request({
        url: "/investmentPerson/propertyList",
        method: "post",
        data
    });
}

export function addProperty(data) {
    return request({
        url: "/investmentPerson/addProperty",
        method: "post",
        data
    });
}

export function getRedisDictList(data = {}) {
    return request({
        url: "/sysDict/getRedisDictList?dictType=decoration",
        method: "post",
        data
    });
}

export function getRedisDictListByType(type) {
    return request({
        url: "/sysDict/getRedisDictList?dictType=" + type,
        method: "post"
    });
}

export function updateStatus(data = {}) {
    return request({
        url: "/investmentPerson/updateStatus",
        method: "get",
        params: data
    });
}

// /investmentPerson/buildInfo
export function buildInfo(data = {}) {
    return request({
        url: "/investmentPerson/buildInfo",
        method: "post",
        data
    });
}
