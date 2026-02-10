import request from "@/utils/request";
import store from "@/store";
var jsyUrl = process.env.VUE_APP_JSY_URL;
import { getToken } from "@/utils/auth"; // get token from cookie

/**
 * 通用js方法封装处理
 * Copyright (c) 2019 ruoyi
 */
// 得到字典表
export function getDictByKey() {
  return request({
    url: "/sysDict/getRedisDictMap",
    method: "post",
    async: false,
  });
}

// 回显数据字典
export function selectDictLabel(ali, value) {
  // var arr = this.getDictByKey(dictType);
  var arr = [];

  // 判断arr是否为空
  if (ali === undefined) {
    return "";
  }
  if (value === undefined) {
    return "";
  }
  var actions = [];
  Object.keys(ali).forEach(function (key) {
    if (ali[key].dictValue == value) {
      actions.push(ali[key].dictLabel);
      // 跳出循环
      return false;
    }
  });
  if (actions.length === 0) {
    actions.push(value);
  }
  return actions.join("");
}

// 空判断 isEmpty
export function isEmpty(str) {
  if (
    Object.prototype.toString.call(str) === "[object Undefined]" ||
    str === null
  ) {
    //空
    return true;
  } else if (
    Object.prototype.toString.call(str) === "[object String]" ||
    Object.prototype.toString.call(str) === "[object Array]"
  ) {
    //字条串或数组
    return str.length === 0 || str === "undefined" ? true : false;
  } else if (Object.prototype.toString.call(str) === "[object Object]") {
    return JSON.stringify(str) === "{}" ? true : false;
  } else if (typeof str == "number") {
    //Number 型
    if (str) {
      return false;
    } else {
      //数字0 不算空
      if (str == 0) {
        return false;
      }
      return true;
    }
  } else if (typeof str == "boolean") {
    if (str.toString() === "true" || str.toString() === "false") {
      return false;
    }
    return true;
  } else {
    return str.toString().length === 0;
  }
}
//时间格式化
export function formatDate(row, column) {
  // 获取单元格数据
  let datac = row[column.property];
  let dtc = new Date(datac);
  //获取月,默认月份从0开始
  let dtuMonth = dtc.getMonth() + 1;
  //获取日
  let dtuDay = dtc.getDate();
  //处理1-9月前面加0
  if (dtuMonth < 10) {
    dtuMonth = "0" + (dtc.getMonth() + 1);
  }
  //处理1-9天前面加0
  if (dtuDay < 10) {
    dtuDay = "0" + dtc.getDate();
  }
  //获取小时
  let dtuHours = dtc.getHours();
  //处理1-9时前面加0
  if (dtuHours < 10) {
    dtuHours = "0" + dtc.getHours();
  }
  //获取分钟
  let dtuMinutes = dtc.getMinutes();
  //处理1-9分前面加0
  if (dtuMinutes < 10) {
    dtuMinutes = "0" + dtc.getMinutes();
  }
  //获取秒
  let dtuSeconds = dtc.getSeconds();
  //处理1-9秒前面加0
  if (dtuSeconds < 10) {
    dtuSeconds = "0" + dtc.getSeconds();
  }
  //组装年月日时分秒,按自己的要求来
  let dd =
    dtc.getFullYear() +
    "-" +
    dtuMonth +
    "-" +
    dtuDay +
    " " +
    dtuHours +
    ":" +
    dtuMinutes;
  return (row.TableIsbibei = dd);
  //+ " " + dtuHours + ":" + dtuMinutes + ":" + dtuSeconds
}

// 日期格式化
export function parseTime(time, pattern) {
  if (arguments.length === 0 || !time) {
    return null;
  }
  const format = pattern || "{y}-{m}-{d} {h}:{i}:{s}";
  let date;
  if (typeof time === "object") {
    date = time;
  } else {
    if (typeof time === "string" && /^[0-9]+$/.test(time)) {
      time = parseInt(time);
    } else if (typeof time === "string") {
      time = time
        .replace(new RegExp(/-/gm), "/")
        .replace("T", " ")
        .replace(new RegExp(/\.[\d]{3}/gm), "");
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
    a: date.getDay(),
  };
  const time_str = format.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
    let value = formatObj[key];
    // Note: getDay() returns 0 on Sunday
    if (key === "a") {
      return ["日", "一", "二", "三", "四", "五", "六"][value];
    }
    if (result.length > 0 && value < 10) {
      value = "0" + value;
    }
    return value || 0;
  });
  return time_str;
}

// 表单重置
export function resetForm(refName) {
  if (this.$refs[refName]) {
    this.$refs[refName].resetFields();
  }
}

// 添加日期范围
export function addDateRange(params, dateRange, propName) {
  let search = params;
  search.params =
    typeof search.params === "object" &&
    search.params !== null &&
    !Array.isArray(search.params)
      ? search.params
      : {};
  dateRange = Array.isArray(dateRange) ? dateRange : [];
  if (typeof propName === "undefined") {
    search.params["beginTime"] = dateRange[0];
    search.params["endTime"] = dateRange[1];
  } else {
    search.params["begin" + propName] = dateRange[0];
    search.params["end" + propName] = dateRange[1];
  }
  return search;
}

// 回显数据字典（字符串、数组）
export function selectDictLabels(datas, value, separator) {
  if (value === undefined || value.length === 0) {
    return "";
  }
  if (Array.isArray(value)) {
    value = value.join(",");
  }
  var actions = [];
  var currentSeparator = undefined === separator ? "," : separator;
  var temp = value.split(currentSeparator);
  Object.keys(value.split(currentSeparator)).some((val) => {
    var match = false;
    Object.keys(datas).some((key) => {
      if (datas[key].value == "" + temp[val]) {
        actions.push(datas[key].label + currentSeparator);
        match = true;
      }
    });
    if (!match) {
      actions.push(temp[val] + currentSeparator);
    }
  });
  return actions.join("").substring(0, actions.join("").length - 1);
}

// 字符串格式化(%s )
export function sprintf(str) {
  var args = arguments,
    flag = true,
    i = 1;
  str = str.replace(/%s/g, function () {
    var arg = args[i++];
    if (typeof arg === "undefined") {
      flag = false;
      return "";
    }
    return arg;
  });
  return flag ? str : "";
}

// 转换字符串，undefined,null等转化为""
export function parseStrEmpty(str) {
  if (!str || str == "undefined" || str == "null") {
    return "";
  }
  return str;
}

// 数据合并
export function mergeRecursive(source, target) {
  for (var p in target) {
    try {
      if (target[p].constructor == Object) {
        source[p] = mergeRecursive(source[p], target[p]);
      } else {
        source[p] = target[p];
      }
    } catch (e) {
      source[p] = target[p];
    }
  }
  return source;
}

/**
 * 构造树型结构数据
 * @param {*} data 数据源
 * @param {*} id id字段 默认 'id'
 * @param {*} parentId 父节点字段 默认 'parentId'
 * @param {*} children 孩子节点字段 默认 'children'
 */
export function handleTree(data, id, parentId, children) {
  let config = {
    id: id || "id",
    parentId: parentId || "parentId",
    childrenList: children || "children",
  };

  var childrenListMap = {};
  var nodeIds = {};
  var tree = [];

  for (let d of data) {
    let parentId = d[config.parentId];
    if (childrenListMap[parentId] == null) {
      childrenListMap[parentId] = [];
    }
    nodeIds[d[config.id]] = d;
    childrenListMap[parentId].push(d);
  }

  for (let d of data) {
    let parentId = d[config.parentId];
    if (nodeIds[parentId] == null) {
      tree.push(d);
    }
  }

  for (let t of tree) {
    adaptToChildrenList(t);
  }

  function adaptToChildrenList(o) {
    if (childrenListMap[o[config.id]] !== null) {
      o[config.childrenList] = childrenListMap[o[config.id]];
    }
    if (o[config.childrenList]) {
      for (let c of o[config.childrenList]) {
        adaptToChildrenList(c);
      }
    }
  }
  return tree;
}

/**
 * 参数处理
 * @param {*} params  参数
 */
export function tansParams(params) {
  let result = "";
  for (const propName of Object.keys(params)) {
    const value = params[propName];
    var part = encodeURIComponent(propName) + "=";
    if (value !== null && value !== "" && typeof value !== "undefined") {
      if (typeof value === "object") {
        for (const key of Object.keys(value)) {
          if (
            value[key] !== null &&
            value[key] !== "" &&
            typeof value[key] !== "undefined"
          ) {
            let params = propName + "[" + key + "]";
            var subPart = encodeURIComponent(params) + "=";
            result += subPart + encodeURIComponent(value[key]) + "&";
          }
        }
      } else {
        result += part + encodeURIComponent(value) + "&";
      }
    }
  }
  return result;
}

// 验证是否为blob格式
export async function blobValidate(data) {
  try {
    const text = await data.text();
    JSON.parse(text);
    return false;
  } catch (error) {
    return true;
  }
}

/**
 * 节流
 *
 * @param {Function} func 要执行的回调函数
 * @param {Number} wait 延时的时间
 * @param {Boolean} immediate 是否立即执行
 * @return null
 */
let timer;
let flag;
export function throttle(func, wait = 500, immediate = true) {
  if (immediate) {
    if (!flag) {
      flag = true;
      // 如果是立即执行，则在wait毫秒内开始时执行
      typeof func === "function" && func();
      timer = setTimeout(() => {
        flag = false;
      }, wait);
    }
  } else if (!flag) {
    flag = true;
    // 如果是非立即执行，则在wait毫秒内的结束处执行
    timer = setTimeout(() => {
      flag = false;
      typeof func === "function" && func();
    }, wait);
  }
}

export function downFile(url, name) {
  const a = document.createElement("a");
  a.style.display = "none";
  a.download = name;
  a.href = url;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
}
