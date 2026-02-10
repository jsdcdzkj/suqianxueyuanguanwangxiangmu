import axios from "axios";
import request from "@/utils/request";

export function getList(params) {
    return request({
        url: "https://api.yytianqi.com/observe?city=CH190801&key=52k08dr7ka77i2r4",
        method: "get",
        params
    });
}

export function getWeather() {
    return axios.get("https://api.yytianqi.com/observe?city=CH190801&key=52k08dr7ka77i2r4").then((data) => data.data);
}
// 得到字典表
export function getDictByKey() {
    return request({
        url: "/common/getRedisDictMap",
        method: "post",
        async: false
    });
}
export function getDeviceTypeList(data) {
    return request({
        url: "/common/getDeviceTypeList",
        method: "post",
        params: data
    });
}

let urlUpload = process.env.VUE_APP_BASE_API + "/minio/importFile";

export async function importData(form) {
    console.log("222222222222221111111111112", form);
    var result = {};
    if (null != form.get("file") && "" != form.get("file") && undefined != form.get("file")) {
        await axios
            .post(urlUpload, form, {
                headers: {
                    "Content-Type": "multipart/form-data",
                    accessToken: localStorage.getItem("vue-admin-beautiful-2021")
                }
            })
            .then((res) => {
                console.log("5555555555555", res);
                result = res;
            });
    }
    return result;
}

export function validate(data) {
    return request({
        url: "/validate",
        method: "post",
        data
    });
}
