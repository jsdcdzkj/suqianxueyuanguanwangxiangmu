import request from "@/utils/request";

// 新增/编辑问卷
export function addSurvey(data) {
    return request({
        url: "/questionnaire/saveOrUpdate",
        method: "post",
        data
    });
}

//级联部门
export function companyDept(data) {
    return request({
        url: "/questionnaire/companyDept",
        method: "post",
        data
    });
}

//问卷列表
export function getList(data) {
    return request({
        url: "/questionnaire/getPageList",
        method: "post",
        data
    });
}

//删除问卷
export function delSurvey(data) {
    return request({
        url: "/questionnaire/delete",
        method: "post",
        data
    });
}

//结束问卷
export function endSurvey(data) {
    return request({
        url: "/questionnaire/end",
        method: "post",
        data
    });
}

//问卷信息详情
export function surveyDetail(data) {
    return request({
        url: "/questionnaire/questionnaireInfo",
        method: "post",
        data
    });
}
//问卷填写整体概况
export function surveyBaseData(data) {
    return request({
        url: "/questionnaire/statistical",
        method: "post",
        data
    });
}
//问卷统计结果
export function surveyTotalInfo(data) {
    return request({
        url: "/questionnaire/questionnaireStatistics",
        method: "post",
        data
    });
}
//问卷统计详情
export function surveyTotalDetail(data) {
    return request({
        url: "/questionnaire/questionnaireDetails",
        method: "post",
        data
    });
}
//问卷未完成信息统计
export function surveyNonattendance(data) {
    return request({
        url: "/questionnaire/nonattendance",
        method: "post",
        data
    });
}

//导出
export function surveyExport(data) {
    return request({
        url: "/questionnaire/questionnaireDetailsImport",
        method: "get",
        params: data,
        responseType: "blob"
    });
}
// 问卷对象列表
export function getSurveyObjectList(data) {
    return request({
        url: "/questionnaire/companyDeptUser",
        method: "post",
        data
    });
}
//存为模板
export function saveAsTemplate(data) {
    return request({
        url: "/questionnaire/saveTemplate",
        method: "post",
        headers: {
            "Content-Type": "application/json"
        },
        data
    });
}
//删除模板
export function delTemplate(data) {
    return request({
        url: "/questionnaire/delTemplate",
        method: "post",
        headers: {
            "Content-Type": "application/json"
        },
        data
    });
}
