const tool = {};
import config from "@/config";

tool.back = function() {
	uni.navigateBack({
		delta: 1,
	});
};

tool.toPage = function(url) {
	try {
		uni.navigateTo({
			url,
		});
	} catch (e) {
		uni.switchTab({
			url,
		});
	}
};

tool.homeToPage = function(url) {
	if (url === "toZCmini") {
		uni.navigateToMiniProgram({
			appId: "wxc45a3bfd9815cee0", // 此为协同办公appid
			path: "pages/login/index", // 此为鼎驰协同办公首页路径
			envVersion: "release", //打开的对应小程序环境：开发develop、体验trial、生产release
			success: (res) => {
				// 打开成功
				// console.log("打开成功", res);
			},
			fail: (err) => {
				// console.log(err);
			},
		});
	} else if (url === "toYMmini") {
		uni.navigateToMiniProgram({
			appId: "wxc45a3bfd9815cee0", // 此为协同办公appid
			path: "pages/login/index", // 此为鼎驰协同办公首页路径
			envVersion: "release", //打开的对应小程序环境：开发develop、体验trial、生产release
			success: (res) => {
				// 打开成功
				// console.log("打开成功", res);
			},
			fail: (err) => {
				// console.log(err);
			},
		});
	} else {
		uni.navigateTo({
			url,
		});
	}
};

tool.getDateDiff = function(time) {
	var dateTimeStamp = new Date(time).getTime();
	var minute = 1000 * 60; //把分，时，天，周，半个月，一个月用毫秒表示
	var hour = minute * 60;
	var day = hour * 24;
	var week = day * 7;
	var halfamonth = day * 15;
	var month = day * 30;
	var now = new Date().getTime(); //获取当前时间毫秒

	var diffValue = now - dateTimeStamp; //时间差

	if (diffValue < 0) {
		return;
	}
	var minC = diffValue / minute; //计算时间差的分，时，天，周，月
	var hourC = diffValue / hour;
	var dayC = diffValue / day;
	var weekC = diffValue / week;
	var monthC = diffValue / month;
	var result = "";
	if (monthC >= 1 && monthC <= 3) {
		result = " " + parseInt(monthC) + "月前";
	} else if (weekC >= 1 && weekC <= 3) {
		result = " " + parseInt(weekC) + "周前";
	} else if (dayC >= 1 && dayC <= 6) {
		result = " " + parseInt(dayC) + "天前";
	} else if (hourC >= 1 && hourC <= 23) {
		result = " " + parseInt(hourC) + "小时前";
	} else if (minC >= 1 && minC <= 59) {
		result = " " + parseInt(minC) + "分钟前";
	} else if (diffValue >= 0 && diffValue <= minute) {
		result = "刚刚";
	} else {
		var datetime = new Date();
		datetime.setTime(dateTimeStamp);
		var Nyear = datetime.getFullYear();
		var Nmonth =
			datetime.getMonth() + 1 < 10 ?
			"0" + (datetime.getMonth() + 1) :
			datetime.getMonth() + 1;
		var Ndate =
			datetime.getDate() < 10 ? "0" + datetime.getDate() : datetime.getDate();
		var Nhour =
			datetime.getHours() < 10 ?
			"0" + datetime.getHours() :
			datetime.getHours();
		var Nminute =
			datetime.getMinutes() < 10 ?
			"0" + datetime.getMinutes() :
			datetime.getMinutes();
		var Nsecond =
			datetime.getSeconds() < 10 ?
			"0" + datetime.getSeconds() :
			datetime.getSeconds();
		result = Nyear + "-" + Nmonth + "-" + Ndate;
	}
	return result;
};

/* 日期格式化 */
tool.dateFormat = function(dt, fmt = "yyyy-MM-dd hh:mm:ss") {
	var date = new Date(dt);
	var o = {
		"M+": date.getMonth() + 1, //月份
		"d+": date.getDate(), //日
		"h+": date.getHours(), //小时
		"m+": date.getMinutes(), //分
		"s+": date.getSeconds(), //秒
		"q+": Math.floor((date.getMonth() + 3) / 3), //季度
		S: date.getMilliseconds(), //毫秒
	};
	if (/(y+)/.test(fmt)) {
		fmt = fmt.replace(
			RegExp.$1,
			(date.getFullYear() + "").substr(4 - RegExp.$1.length)
		);
	}
	for (var k in o) {
		if (new RegExp("(" + k + ")").test(fmt)) {
			fmt = fmt.replace(
				RegExp.$1,
				RegExp.$1.length === 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length)
			);
		}
	}
	return fmt;
};

/* 千分符 */
tool.groupSeparator = function(num) {
	num = num + "";
	if (!num.includes(".")) {
		num += ".";
	}
	return num
		.replace(/(\d)(?=(\d{3})+\.)/g, function($0, $1) {
			return $1 + ",";
		})
		.replace(/\.$/, "");
};

/* 获取url参数 */
tool.getQueryVariable = function(variable) {
	var query = window.location.search.substring(1);
	var vars = query.split("&");
	for (var i = 0; i < vars.length; i++) {
		var pair = vars[i].split("=");
		if (pair[0] === variable) {
			return pair[1];
		}
	}
	return false;
};

tool.checkMobile = function(n) {
	return /^1[3|4|5|6|7|8|9][0-9]\d{8}$/.test(n);
};

tool.toPage = function(n) {
	let token = uni.getStorageSync("accessToken");
	let isLogin = Boolean(token); //返回布尔值
	if (!isLogin) {
		uni.navigateTo({
			url: "/pages/login/index",
		});
	} else {
		uni.navigateTo({
			url: n,
		});
	}
};

tool.backPage = function(delta = 1) {
	uni.navigateBack({
		delta,
	});
};

tool.arrayEqual = function(arr1, arr2) {
	if (arr1 === arr2) return true;
	if (arr1.length != arr2.length) return false;
	for (var i = 0; i < arr1.length; ++i) {
		if (arr1[i] !== arr2[i]) return false;
	}
	return true;
};

//节流
let timer;
let flag;
tool.throttle = function(func, wait = 500, immediate = true) {
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
};

//深拷贝
tool.deepClone = function(source) {
	if (!source && typeof source !== "object") {
		throw new Error("error arguments", "deepClone");
	}
	const targetObj = source.constructor === Array ? [] : {};
	Object.keys(source).forEach((keys) => {
		if (source[keys] && typeof source[keys] === "object") {
			targetObj[keys] = deepClone(source[keys]);
		} else {
			targetObj[keys] = source[keys];
		}
	});
	return targetObj;
};

//提示弹窗
tool.toast = function(title, icon = "none", duration = 2000) {
	uni.showToast({
		title,
		icon,
		duration,
	});
};

//模态框
tool.modal = function(content, title = "提示", showCancel = true) {
	return new Promise((resolve, reject) => {
		uni.showModal({
			title,
			content,
			showCancel,
			success: (res) => {
				if (res.confirm) {
					resolve();
				} else {
					reject();
				}
			},
		});
	});
};

//模态输入框
tool.inputModal = function(
	content,
	title = "提示",
	placeholderText = "请输入"
) {
	return new Promise((resolve, reject) => {
		uni.showModal({
			title,
			content,
			editable: true,
			placeholderText,
			success: (res) => {
				if (res.confirm) {
					resolve(res.content);
				} else {
					reject();
				}
			},
		});
	});
};
//上传文件   url:文件临时路径
tool.uploadImage = function(url, data = {}) {
	return new Promise((resolve, reject) => {
		if (!url) {
			reject();
			return;
		}
		uni.showLoading({
			title: "上传中",
			mask: true,
		});
		uni.uploadFile({
			url: config.VUE_APP_API_BASEURL + "/visitor/uploadFile.do",
			fileType: "image",
			filePath: url,
			name: "file",
			formData: data,
			header: {
				Authorization: uni.getStorageSync("accessToken"),
			},
			success: (res) => {
				var data = JSON.parse(res.data);
				if (data.code === 0) {
					resolve(data);
				} else {
					this.modal(data.msg, "提示", false);
					reject();
				}
			},
			fail: () => {
				reject();
			},
			complete() {
				uni.hideLoading();
			},
		});
	});
};
//查看大图
tool.imagePreview = function(src, index = 0) {
	uni.previewImage({
		urls: typeof src == "string" ? [src] : src,
		current: index,
	});
};
tool.previewBase64 = function(base64) {
	const fsm = wx.getFileSystemManager();
	const filePath = `${
    wx.env.USER_DATA_PATH
  }/temp_image${new Date().getTime()}.png`;
	fsm.writeFileSync(filePath, base64, "base64");
	uni.previewImage({
		urls: [filePath],
	});
};
tool.todoStatus = [{
		label: "巡检",
		value: 0,
		index: 0,
	},
	{
		label: "维保",
		value: 1,
		index: 1,
	},
	{
		label: "维修",
		value: 2,
		index: 2,
	},
	{
		label: "任务指派",
		value: 3,
		index: 3,
	},
	{
		label: "审批",
		value: 4,
		index: 4,
	},
];

tool.calcSum = (list) => {
	let sum = 0;
	list.map((res) => {
		sum += res.price * res.amount;
	});
	return sum.toFixed(2);
};
tool.getHourTime = () => {
	const hour = new Date().getHours();
	return hour < 8 ?
		"早上好" :
		hour <= 11 ?
		"上午好" :
		hour <= 13 ?
		"中午好" :
		hour < 18 ?
		"下午好" :
		"晚上好";
};

export default tool;