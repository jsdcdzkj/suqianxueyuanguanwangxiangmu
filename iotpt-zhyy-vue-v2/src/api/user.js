import request from "@/utils/request";

export async function login(data) {
    return request({
        url: "/login.do",
        method: "post",
        data
    });
}

export function getUserInfo(accessToken) {
    return request({
        url: "/userInfo.do",
        method: "post",
        data: {
            ["accessToken"]: accessToken
        },
        // 重写请求头 content-type 为 application/x-www-form-urlencoded
        headers: {
            "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
        }
    });
}

export function userInfoPermissions() {
    return request({
        url: "/userInfoPermissions.do",
        method: "post"
    });
}

export function logout() {
    return request({
        url: "/logout.do",
        method: "post"
    });
}

export function register() {
    return request({
        url: "/register",
        method: "post"
    });
}

// 获取用户列表分页
export function getList(data) {
    return request({
        url: "/sysuser/getUserPage.do",
        method: "post",
        params: data,
        headers: {
            "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
        }
    });
}

export function orgTreeList(data) {
    return request({
        url: "/sysuser/orgTreeList",
        method: "post",
        data
    });
}

export function getWarning() {
    return request({
        url: "/getWarning.do",
        method: "post"
    });
}

export function upeWarning(data) {
    return request({
        url: "/upeWarning.do",
        method: "post",
        params: data
    });
}

//设备监控远程控制，验证密码
export function passCheck(data) {
    return request({
        url: "/passCheck.do",
        method: "post",
        data
    });
}

export function getUserTree(data) {
    return request({
        url: "/sysuser/getUserTree.do",
        method: "post",
        data
    });
}

export function changePwd(data) {
    return request({
        url: "/sysuser/changePassword.do",
        method: "post",
        data
    });
}
