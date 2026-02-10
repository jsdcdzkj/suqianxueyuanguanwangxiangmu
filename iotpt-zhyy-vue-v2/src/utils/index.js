import dayjs from "dayjs";
// 年月日-时分秒
export const time = function (value = new Date()) {
    return dayjs(value).format("YYYY-MM-DD HH:mm:ss");
};
export const time2 = function (value = new Date()) {
    return dayjs(value).format("HH:mm:ss");
};
// 年月日
export const dateFormat = function (value = new Date()) {
    return dayjs(value).format("YYYY-MM-DD");
};

//获取当前日期的周六
export const saturdayTime = function (value) {
    return dayjs(value).endOf("week").format("YYYY-MM-DD");
};

//获取当前日期的周日
export const sundayTime = function (value) {
    return dayjs(value).day(7).format("YYYY-MM-DD");
};

//计算日期之间的天数
export const DateDiff = function (sDate1, sDate2) {
    var aDate, oDate1, oDate2, iDays;
    aDate = sDate1.split("-");
    oDate1 = new Date(aDate[1] + "-" + aDate[2] + "-" + aDate[0]); //转换为02-05-2018格式
    aDate = sDate2.split("-");
    oDate2 = new Date(aDate[1] + "-" + aDate[2] + "-" + aDate[0]);
    iDays = parseInt(Math.abs(oDate1 - oDate2) / 1000 / 60 / 60 / 24) + 1; //把相差的毫秒数转换为天数
    return iDays;
};

/**
 * Parse the time to string
 * @param {(Object|string|number)} time
 * @param {string} cFormat
 * @returns {string | null}
 */
export function parseTime(time, cFormat) {
    if (arguments.length === 0 || !time) {
        return null;
    }
    const format = cFormat || "{y}-{m}-{d} {h}:{i}:{s}";
    let date;
    if (typeof time === "object") {
        date = time;
    } else {
        if (typeof time === "string") {
            if (/^[0-9]+$/.test(time)) {
                // support "1548221490638"
                time = parseInt(time);
            } else {
                // support safari
                // https://stackoverflow.com/questions/4310953/invalid-date-in-safari
                time = time.replace(new RegExp(/-/gm), "/");
            }
        }

        if (typeof time === "number" && time.toString().length === 10) {
            time = time * 1000;
        }
        date = new Date(time);
    }
    const formatObj = {
        y: date.getFullYear(),
        m: date.getMonth() + 1,
        d: date.getDate(),
        h: date.getHours(),
        i: date.getMinutes(),
        s: date.getSeconds(),
        a: date.getDay()
    };
    const time_str = format.replace(/{([ymdhisa])+}/g, (result, key) => {
        const value = formatObj[key];
        // Note: getDay() returns 0 on Sunday
        if (key === "a") {
            return ["日", "一", "二", "三", "四", "五", "六"][value];
        }
        return value.toString().padStart(2, "0");
    });
    return time_str;
}

/**
 * @param {number} time
 * @param {string} option
 * @returns {string}
 */
export function formatTime(time, option) {
    if (("" + time).length === 10) {
        time = parseInt(time) * 1000;
    } else {
        time = +time;
    }
    const d = new Date(time);
    const now = Date.now();

    const diff = (now - d) / 1000;

    if (diff < 30) {
        return "刚刚";
    } else if (diff < 3600) {
        // less 1 hour
        return Math.ceil(diff / 60) + "分钟前";
    } else if (diff < 3600 * 24) {
        return Math.ceil(diff / 3600) + "小时前";
    } else if (diff < 3600 * 24 * 2) {
        return "1天前";
    }
    if (option) {
        return parseTime(time, option);
    } else {
        return d.getMonth() + 1 + "月" + d.getDate() + "日" + d.getHours() + "时" + d.getMinutes() + "分";
    }
}

/**
 * @param {string} url
 * @returns {Object}
 */
export function param2Obj(url) {
    const search = decodeURIComponent(url.split("?")[1]).replace(/\+/g, " ");
    if (!search) {
        return {};
    }
    const obj = {};
    const searchArr = search.split("&");
    searchArr.forEach((v) => {
        const index = v.indexOf("=");
        if (index !== -1) {
            const name = v.substring(0, index);
            const val = v.substring(index + 1, v.length);
            obj[name] = val;
        }
    });
    return obj;
}

// 原生方法实现一个深度克隆，value可以是任意值
export function deepclone(value) {
    if (Array.isArray(value)) {
        let clone = [];
        for (let i = 0; i < value.length; i++) {
            clone[i] = deepclone(value[i]);
        }
        return clone;
    }
    if (typeof value === "object" && value !== null) {
        let clone = {};
        for (let key in value) {
            clone[key] = deepclone(value[key]);
        }
        return clone;
    }
    return value;
}

export function exportExcel(blob, fileName) {
    let reader = new FileReader();
    reader.readAsText(res.data, "utf-8");
    reader.onload = function () {
        try {
            let jsondata = JSON.parse(reader.result);
            if (jsondata && jsondata.code) {
                if (jsondata.code == 500) {
                    that.$baseMessage(jsondata.msg, "error");
                }
            } else {
                //异常处理
            }
        } catch (err) {
            //成功

            let fileName = "能耗报表导出.xlsx";
            let objectUrl = URL.createObjectURL(new Blob([res.data]));
            const link = document.createElement("a");
            link.download = decodeURI(fileName);
            link.href = objectUrl;
            link.click();
            that.$baseMessage("导出成功！", "success");
        }
    };
}

// 判断字符串是否为空
export function isEmpty(value) {
  // 允许 '0' 和 0
  if (value === '0' || value === 0) {
    return false
  }
  
  // 检查空值
  return !value && value !== 0 && value !== '0'
}