import request from "@/utils/request";

// 获取报警类型
export function getAlarmCategory(data) {
    return request({
        url: "/alarmRecords/getAlarmCategory",
        method: "get",
        params: data
    });
}

// 告警中心分页
export function getPageList(data) {
    return request({
        url: "/alarmRecords/getPageList",
        method: "post",
        data
    });
}

// 查看详情
export function getEntity(data) {
    return request({
        url: "/alarmRecords/getEntity",
        method: "post",
        data
    });
}

// 角标
export function getsubscript(data) {
    return request({
        url: "/alarmRecords/getsubscript",
        method: "post",
        data
    });
}

// 统计数量
export function groupAlarmLevel(data) {
    return request({
        url: "/alarmRecords/groupAlarmLevel",
        method: "post",
        data
    });
}

// 折线图
export function line(data) {
    return request({
        url: "/alarmRecords/line",
        method: "post",
        data
    });
}

// 批量处理报警
export function batchProcessing(data) {
    return request({
        url: "/alarmRecords/batchProcessing",
        method: "post",
        data
    });
}

// 导出
export function toExportTemplate(data) {
    return request({
        url: "/alarmRecords/toExportTemplate",
        method: "get",
        params: data,
        responseType: "blob"
    });
}
// 弹窗
export function getNewEntity(data) {
    return request({
        url: "/alarmRecords/getNewEntity",
        method: "post",
        params: data
    });
}

// 报警内容下拉列表
export function contentList(data) {
  return request({
    url: '/alarm/content/list',
    method: 'get',
    params:data
  })
}

// 通知
export function getNotice(data) {
    return request({
        url: "/alarmRecords/notice",
        method: "get",
        params: data
    });
}

//AI分析
export function getAiAnalysis(data) {
  return request({
      url: '/alarmRecords/lamp',
      method: 'post',
      data
  })
}

// 预警
export function getAiAnalysisYJ(data) {
    return request({
        url: '/alarmRecords/lampYJ',
        method: 'post',
        data
    })
  }




