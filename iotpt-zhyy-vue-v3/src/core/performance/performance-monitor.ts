// utils/performance-monitor.ts
import type {
	PerformanceMetrics,
	PerformanceData,
	PerformanceConfig,
	DetectionMethod,
	WhiteScreenResult
} from "@/types/performance";

export class PerformanceMonitor {
	private startTime: number;
	private metrics: PerformanceMetrics = {};
	private observers: (PerformanceObserver | MutationObserver)[] = [];
	private config: PerformanceConfig;
	private hasReported = false;

	constructor(config?: Partial<PerformanceConfig>) {
		this.startTime = performance.now();
		this.config = {
			enabled: true,
			sampleRate: 1,
			endpoint: "/api/performance",
			timeout: 10000,
			autoStart: true,
			metrics: {
				whiteScreen: true,
				firstContentfulPaint: true,
				largestContentfulPaint: true,
				firstInputDelay: true,
				cumulativeLayoutShift: true
			},
			...config
		};
	}

	/**
	 * 监控白屏时间
	 */
	public async monitorWhiteScreen(): Promise<WhiteScreenResult> {
		if (!this.config.enabled || !this.shouldSample()) {
			return { time: 0, method: "disabled" as DetectionMethod };
		}

		return new Promise<WhiteScreenResult>((resolve) => {
			let resolved = false;

			const resolveOnce = (result: WhiteScreenResult): void => {
				if (!resolved) {
					resolved = true;
					resolve(result);
				}
			};

			// 方法1: Performance Observer 监控 FCP
			this.observePaintTiming(resolveOnce);

			// 方法2: DOM变化监控
			this.observeDOMChanges(resolveOnce);

			// 方法3: 超时处理
			setTimeout(() => {
				if (!resolved) {
					const fallbackTime = performance.now() - this.startTime;
					this.reportWhiteScreen(fallbackTime, "timeout");
					resolveOnce({ time: fallbackTime, method: "timeout" });
				}
			}, this.config.timeout);
		});
	}

	/**
	 * 监控Paint Timing事件
	 */
	private observePaintTiming(callback: (result: WhiteScreenResult) => void): void {
		if (!("PerformanceObserver" in window)) {
			console.warn("PerformanceObserver not supported");
			return;
		}

		try {
			const observer = new PerformanceObserver((list: PerformanceObserverEntryList) => {
				const entries = list.getEntries();

				entries.forEach((entry: PerformanceEntry) => {
					if (entry.name === "first-contentful-paint") {
						const fcpTime = entry.startTime;
						this.metrics.firstContentfulPaint = fcpTime;
						this.reportWhiteScreen(fcpTime, "fcp");
						callback({ time: fcpTime, method: "fcp" });
						observer.disconnect();
					}

					if (entry.name === "largest-contentful-paint") {
						this.metrics.largestContentfulPaint = entry.startTime;
					}
				});
			});

			observer.observe({ entryTypes: ["paint", "largest-contentful-paint"] });
			this.observers.push(observer);
		} catch (error) {
			console.error("Error setting up PerformanceObserver:", error);
		}
	}

	/**
	 * 监控DOM变化
	 */
	private observeDOMChanges(callback: (result: WhiteScreenResult) => void): void {
		let hasReported = false;

		const observer = new MutationObserver((mutations: MutationRecord[]) => {
			if (!hasReported && this.hasVisibleContent()) {
				const whiteScreenTime = performance.now() - this.startTime;
				this.metrics.domWhiteScreenTime = whiteScreenTime;

				// 如果FCP还没有触发，使用DOM变化时间
				if (!this.metrics.firstContentfulPaint) {
					this.reportWhiteScreen(whiteScreenTime, "dom");
					callback({ time: whiteScreenTime, method: "dom" });
					hasReported = true;
				}

				observer.disconnect();
			}
		});

		observer.observe(document.documentElement, {
			childList: true,
			subtree: true,
			attributes: true,
			attributeFilter: ["style", "class"]
		});

		this.observers.push(observer);
	}

	/**
	 * 检查是否有可见内容
	 */
	private hasVisibleContent(): boolean {
		const walker = document.createTreeWalker(document.body || document.documentElement, NodeFilter.SHOW_ELEMENT, {
			acceptNode: (node: Node): number => {
				if (!(node instanceof Element)) return NodeFilter.FILTER_SKIP;

				const style = window.getComputedStyle(node);
				const rect = node.getBoundingClientRect();

				return rect.width > 0 &&
					rect.height > 0 &&
					style.opacity !== "0" &&
					style.visibility !== "hidden" &&
					style.display !== "none"
					? NodeFilter.FILTER_ACCEPT
					: NodeFilter.FILTER_SKIP;
			}
		});

		return !!walker.nextNode();
	}

	/**
	 * 上报白屏时间
	 */
	private reportWhiteScreen(time: number, method: DetectionMethod): void {
		if (this.hasReported) return;

		this.hasReported = true;
		this.metrics.whiteScreenTime = time;
		this.metrics.method = method;

		console.log(`白屏时间: ${time.toFixed(2)}ms (方法: ${method})`);

		const data: PerformanceData = {
			type: "whiteScreen",
			time: Math.round(time),
			method,
			url: location.href,
			userAgent: navigator.userAgent,
			timestamp: Date.now(),
			metrics: this.getMetrics()
		};

		this.sendToAnalytics(data);
	}

	/**
	 * 发送分析数据
	 */
	private async sendToAnalytics(data: PerformanceData): Promise<void> {
		try {
			const payload = JSON.stringify(data);
			// console.log(payload);

			// if (navigator.sendBeacon) {
			// 	const success = navigator.sendBeacon(this.config.endpoint, payload);
			// 	if (!success) {
			// 		throw new Error("sendBeacon failed");
			// 	}
			// } else {
			// 	await fetch(this.config.endpoint, {
			// 		method: "POST",
			// 		headers: { "Content-Type": "application/json" },
			// 		body: payload,
			// 		keepalive: true
			// 	});
			// }
		} catch (error) {
			console.error("Failed to send analytics data:", error);
		}
	}

	/**
	 * 判断是否应该采样
	 */
	private shouldSample(): boolean {
		return Math.random() < this.config.sampleRate;
	}

	/**
	 * 获取完整的性能指标
	 */
	public getMetrics(): PerformanceMetrics {
		const timing = performance.timing;
		const navigation = performance.getEntriesByType("navigation")[0];

		return {
			...this.metrics,
			navigationStart: timing.navigationStart,
			domContentLoaded: timing.domContentLoadedEventEnd - timing.navigationStart,
			loadComplete: timing.loadEventEnd - timing.navigationStart,
			resourceTiming: performance.getEntriesByType("resource").length,
			memory: (performance as any).memory
				? {
						usedJSHeapSize: (performance as any).memory.usedJSHeapSize,
						totalJSHeapSize: (performance as any).memory.totalJSHeapSize,
						jsHeapSizeLimit: (performance as any).memory.jsHeapSizeLimit
					}
				: null
		};
	}

	/**
	 * 监控其他Core Web Vitals
	 */
	public monitorCoreWebVitals(): void {
		// First Input Delay (FID)
		this.observeFirstInputDelay();

		// Cumulative Layout Shift (CLS)
		this.observeLayoutShift();
	}

	/**
	 * 监控首次输入延迟
	 */
	private observeFirstInputDelay(): void {
		if (!("PerformanceObserver" in window)) return;

		try {
			const observer = new PerformanceObserver((list: PerformanceObserverEntryList) => {
				const entries = list.getEntries();
				const firstInput = entries[0];

				if (firstInput && "processingStart" in firstInput) {
					const fid = (firstInput as any).processingStart - firstInput.startTime;
					console.log(`First Input Delay: ${fid.toFixed(2)}ms`);

					this.sendToAnalytics({
						type: "firstInputDelay",
						time: Math.round(fid),
						method: "performance-observer",
						url: location.href,
						userAgent: navigator.userAgent,
						timestamp: Date.now()
					});
				}

				observer.disconnect();
			});

			observer.observe({ entryTypes: ["first-input"] });
			this.observers.push(observer);
		} catch (error) {
			console.error("Error monitoring First Input Delay:", error);
		}
	}

	/**
	 * 监控累积布局偏移
	 */
	private observeLayoutShift(): void {
		if (!("PerformanceObserver" in window)) return;

		let cumulativeScore = 0;

		try {
			const observer = new PerformanceObserver((list: PerformanceObserverEntryList) => {
				const entries = list.getEntries();

				entries.forEach((entry: PerformanceEntry) => {
					if ("value" in entry && !(entry as any).hadRecentInput) {
						cumulativeScore += (entry as any).value;
					}
				});
			});

			observer.observe({ entryTypes: ["layout-shift"] });
			this.observers.push(observer);

			// 页面卸载时发送CLS数据
			window.addEventListener("beforeunload", () => {
				console.log(`Cumulative Layout Shift: ${cumulativeScore.toFixed(4)}`);

				this.sendToAnalytics({
					type: "cumulativeLayoutShift",
					time: Math.round(cumulativeScore * 1000),
					method: "performance-observer",
					url: location.href,
					userAgent: navigator.userAgent,
					timestamp: Date.now()
				});
			});
		} catch (error) {
			console.error("Error monitoring Cumulative Layout Shift:", error);
		}
	}

	/**
	 * 清理所有观察器
	 */
	public destroy(): void {
		this.observers.forEach((observer: PerformanceObserver | MutationObserver) => {
			try {
				observer.disconnect();
			} catch (error) {
				console.warn("Error disconnecting observer:", error);
			}
		});
		this.observers = [];
	}
}
