import { defineConfig } from "vite";
import Vue from "@vitejs/plugin-vue";
import VueRouter from "unplugin-vue-router/vite";
import Unocss from "unocss/vite";
import VueJsx from "@vitejs/plugin-vue-jsx";
import path from "node:path";
import Components from "unplugin-vue-components/vite";
import { viteMockServe } from "vite-plugin-mock";

export default defineConfig({
	plugins: [
		VueRouter({
			extensions: [".vue", ".md"],
			dts: "src/typed-router.d.ts",
			// 不好包含dialog
			exclude: ["src/pages/modules/**/dialog/**"],
			routesFolder: "src/pages/modules"
		}),
		viteMockServe({
			mockPath: "mock", // 指定Mock数据文件夹路径
			enable: true, // 启用Mock功能
			logger: true // 控制台打印请求日志
		}),
		Components({
			extensions: ["vue", "md"],
			include: [/\.vue$/, /\.vue\?vue/, /\.md$/],
			resolvers: [
				// ElementPlusResolver({
				// 	importStyle: "sass"
				// })
			],
			dts: "src/components.d.ts"
		}),
		Vue(),
		VueJsx(),
		Unocss()
	],

	resolve: {
		alias: {
			"@": path.resolve(__dirname, "src"),
			"@@": path.resolve(__dirname, "packages")
		},
		extensions: [".tsx", ".ts", ".jsx", ".js", ".css"]
	},

	build: {
		rollupOptions: {
			output: {
				manualChunks(id, { getModuleInfo }) {
					const match = /.*\.strings\.(\w+)\.js/.exec(id);
					if (match) {
						const language = match[1]; // e.g. "en"
						const dependentEntryPoints = [];

						// we use a Set here so we handle each module at most once. This
						// prevents infinite loops in case of circular dependencies
						const idsToHandle = new Set(getModuleInfo(id).dynamicImporters);

						for (const moduleId of idsToHandle) {
							const { isEntry, dynamicImporters, importers } = getModuleInfo(moduleId);
							if (isEntry || dynamicImporters.length > 0) dependentEntryPoints.push(moduleId);

							// The Set iterator is intelligent enough to iterate over
							// elements that are added during iteration
							for (const importerId of importers) idsToHandle.add(importerId);
						}

						// If there is a unique entry, we put it into a chunk based on the
						// entry name
						if (dependentEntryPoints.length === 1) {
							return `${
								dependentEntryPoints[0].split("/").slice(-1)[0].split(".")[0]
							}.strings.${language}`;
						}
						// For multiple entries, we put it into a "shared" chunk
						if (dependentEntryPoints.length > 1) {
							return `shared.strings.${language}`;
						}
					}
				}
			}
		}
	},
	ssr: {
		noExternal: ["element-plus"]
	},
	server: {
		port: 8099,
		host: true,
		open: true,
		proxy: {
			"/api": {
				target: "http://172.168.80.3:3022",
				changeOrigin: true,
				rewrite: (path) => path.replace(/^\/api/, "")
			}
		}
	}
});
