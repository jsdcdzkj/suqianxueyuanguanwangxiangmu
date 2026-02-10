// plugins/performance-plugin.ts
import type { App } from "vue";
import type { PerformanceConfig } from "@/types/performance";
import { PerformanceMonitor } from "@/core/performance/performance-monitor";

interface PerformancePluginOptions extends Partial<PerformanceConfig> {
	autoStart?: boolean;
}

export default {
	install(app: App, options: PerformancePluginOptions = {}) {
		const monitor = new PerformanceMonitor(options);

		// 提供全局访问
		app.config.globalProperties.$performance = monitor;
		app.provide("performance", monitor);

		// 自动开始监控
		if (options.autoStart !== false) {
			monitor.monitorWhiteScreen().then((result) => {
				console.log("白屏时间监控完成:", result);

				// 开始监控其他Core Web Vitals
				monitor.monitorCoreWebVitals();
			});
		}

		// 页面卸载时清理
		const cleanup = (): void => {
			monitor.destroy();
		};

		window.addEventListener("beforeunload", cleanup);
		window.addEventListener("pagehide", cleanup);
	}
};
