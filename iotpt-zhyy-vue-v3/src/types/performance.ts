// types/performance.ts
export interface PerformanceMetrics {
	whiteScreenTime?: number;
	firstContentfulPaint?: number;
	largestContentfulPaint?: number;
	domWhiteScreenTime?: number;
	method?: "fcp" | "dom" | "timeout";
	navigationStart?: number;
	domContentLoaded?: number;
	loadComplete?: number;
	resourceTiming?: number;
	memory?: {
		usedJSHeapSize: number;
		totalJSHeapSize: number;
		jsHeapSizeLimit: number;
	} | null;
}

export interface PerformanceData {
	type: string;
	time: number;
	method: string;
	url: string;
	userAgent: string;
	timestamp: number;
	metrics?: PerformanceMetrics;
}

export interface PerformanceConfig {
	enabled: boolean;
	sampleRate: number;
	endpoint: string;
	timeout: number;
	autoStart: boolean;
	metrics: {
		whiteScreen: boolean;
		firstContentfulPaint: boolean;
		largestContentfulPaint: boolean;
		firstInputDelay: boolean;
		cumulativeLayoutShift: boolean;
	};
}

export type DetectionMethod = "fcp" | "dom" | "timeout";

export interface WhiteScreenResult {
	time: number;
	method: DetectionMethod;
}
