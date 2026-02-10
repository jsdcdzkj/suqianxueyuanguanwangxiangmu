import Vue from "vue";
import axios from "axios";

import store from "@/store";
import qs from "qs";
import router from "@/router";
import { isArray } from "@/utils/validate";

let invalidCode = 402,
    noPermissionCode = 401,
    meassageCode = -1,
    meassageErrCode = 500,
    successCode = [200, 0, -2],
    loginInterception = true;

//let loadingInstance

/**
 * @author https://gitee.com/chu1204505056/vue-admin-better （不想保留author可删除）
 * @description 处理code异常
 * @param {*} code
 * @param {*} msg
 */
const handleCode = (code, msg) => {
    switch (code) {
        case invalidCode:
            // Vue.prototype.$baseMessage(msg || `后端接口${code}异常`, "error");
            store.dispatch("user/resetToken").catch(() => {});
            if (loginInterception) {
                location.reload();
            }
            break;
        case noPermissionCode:
            router.push({ path: "/401" }).catch(() => {});
            break;
        case meassageCode:
            Vue.prototype.$baseMessage(msg || `后端接口${code}异常`, "error");
            break;
        case meassageErrCode:
            Vue.prototype.$baseMessage(msg || `后端接口${code}异常`, "error");
            break;
        default:
            // Vue.prototype.$baseMessage(msg || `后端接口${code}异常`, "error");
            break;
    }
};
const instance = axios.create({
    baseURL: process.env.VUE_APP_BASE_API, // url = base url + request url
    timeout: 100000, //请求超过100秒
    headers: {
        "Content-Type": "application/json;charset=UTF-8"
    }
});

instance.interceptors.request.use(
    (config) => {
        if (store.state.user.token) {
            config.headers["accessToken"] = store.state.user.token;
        }
        //这里会过滤所有为空、0、false的key，如果不需要请自行注释
        // if (config.data)
        //   config.data = Vue.prototype.$baseLodash.pickBy(
        //     config.data,
        //     Vue.prototype.$baseLodash.identity
        //   )
        if (config.data && config.headers["Content-Type"] === "application/x-www-form-urlencoded;charset=UTF-8")
            config.data = qs.stringify(config.data);
        //if (debounce.some((item) => config.url.includes(item))) loadingInstance = Vue.prototype.$baseLoading()
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

instance.interceptors.response.use(
    (response) => {
        // if (response.status == 200) {
        //   if (Object.prototype.toString.call(response.data).includes("Blob")) {
        //     // 如果是下载
        //     return response;
        //   }
        // }
        //if (loadingInstance) loadingInstance.close()

        const { data, config } = response;
        // 文件流直接返回
        if (config.responseType === "blob") {
            return response;
        }

        const { code, msg } = data;
        // 是否操作正常
        if (
            successCode.includes(code) ||
            response.data.type == "application/vnd.ms-excel" ||
            response.data.type == "application/octet-stream"
        ) {
            return data;
        } else {
            handleCode(code, msg);
            //若token失效则跳转到登录页
            if (code == 40003) {
                store.commit("user/RESET_USER");
                router.replace("/login");
                // window.location.href = 'http://192.168.0.40:3010';
                // window.location.href = location.origin + "/#/login";
            }
            return Promise.reject("请求异常拦截:" + JSON.stringify({ url: config.url, code, msg }) || "错误");
        }
    },
    (error) => {
        //if (loadingInstance) loadingInstance.close()
        const { response, message } = error;
        if (error.response && error.response.data) {
            const { status, data } = response;
            handleCode(status, data.msg || message);
            return Promise.reject(error);
        } else {
            let { message } = error;
            if (message === "Network Error") {
                message = "后端接口连接异常";
            }
            if (message.includes("timeout")) {
                message = "后端接口请求超时";
            }
            if (message.includes("Request failed with status code")) {
                const code = message.substr(message.length - 3);
                message = "后端接口" + code + "异常";
            }
            // Vue.prototype.$baseMessage(message || `后端接口未知异常`, "error");
            return Promise.reject(error);
        }
    }
);

export default instance;
