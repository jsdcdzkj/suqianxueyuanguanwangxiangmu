import request from "@/utils/request";

//招商人员列表
export function investmentPersonList(data) {
    return request({
        url: "/investmentPerson/investmentPersonList",
        method: "post",
        data
    });
}

// 新增招商人员
export function addInvestmentPerson(data) {
    return request({
        url: "/investmentPerson/addInvestmentPerson",
        method: "post",
        data
    });
}

// 移除招商人员
export function delInvestmentPerson(data) {
    return request({
        url: "/investmentPerson/delInvestmentPerson",
        method: "post",
        data
    });
}

// 全部人员列表
export function getUserPage(data) {
    return request({
        url: "/investmentPerson/getUserPage.do",
        method: "post",
        params: data
    });
}