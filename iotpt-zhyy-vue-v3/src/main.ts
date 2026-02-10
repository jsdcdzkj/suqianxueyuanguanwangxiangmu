import App from "./App.vue";
import "@/styles/index.scss";
import "nprogress/nprogress.css";
import "@vueup/vue-quill/dist/vue-quill.snow.css";
import "uno.css";
import mitt from "mitt";
import { createProjectApp } from "./core";
import { Quill } from "@vueup/vue-quill";
import "remixicon/fonts/remixicon.css";

export const bus = mitt();

// @ts-ignore
window.Quill = Quill;

export const appInstance = {};

createProjectApp({
	app: App,
	routes: [
		{
			path: "/login",
			name: "Login",
			component: () => import("@/pages/common/login.vue")
		},
		{
			path: "/index",
			name: "Index",
			component: () => import("@/pages/common/index.vue")
		},
		{
			path: "/",
			name: "Root",
			children: [],
			meta: {
				title: "首页"
			},
			component: () => import("@/layouts/index.vue")
		}
	]
});
