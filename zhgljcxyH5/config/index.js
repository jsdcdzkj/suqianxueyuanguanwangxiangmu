let env = "prd";

// env =  process.env.NODE_ENV === 'development' ? 'dev' : 'prd';//开发环境

const configs = {
	prd: {
		VUE_APP_TITLE: "综合管网监测系统(PRD)",
		// 接口地址
		VUE_APP_API_BASEURL: "https://smartpark.dincher.cn/wul/web/app",		
	},
	dev: {
		VUE_APP_TITLE: "综合管网监测系统(DEV)",
		// 接口地址		
		VUE_APP_API_BASEURL: "http://172.168.10.99:7074/app",
		
	},
};

let config = configs[env];
export default config;