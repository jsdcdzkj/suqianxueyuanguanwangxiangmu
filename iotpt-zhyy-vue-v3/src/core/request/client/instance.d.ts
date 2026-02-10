import type { AxiosInstance } from "axios";
import type { RequestClientConfig, RefreshQueueItem } from "./type";
import { InterceptorManager } from "./interceptors";
import { CancelTokenManager } from "./cancel-token";
import { DownloaderManger } from "./downloader";
import { UploaderManager } from "./uploader";
declare class RequestClient {
	options: RequestClientConfig | null;
	instance: AxiosInstance | null;
	addRequestInterceptor: InterceptorManager["addRequestInterceptor"];
	addResponseInterceptor: InterceptorManager["addResponseInterceptor"];
	download: DownloaderManger["download"];
	downloadFile: DownloaderManger["downloadFile"];
	upload: UploaderManager["upload"];
	refreshTokenQueue: Array<RefreshQueueItem>;
	cancelTokenManager?: CancelTokenManager;
	refreshing?: boolean;
	constructor(options?: RequestClientConfig);
	get<T = any>(url: string, config?: RequestClientConfig): Promise<T>;
	post<T = any>(url: string, data: any, config?: RequestClientConfig): Promise<T>;
	delete<T>(url: string, data: T, config?: RequestClientConfig): Promise<T>;
	put<T = any>(url: string, data: any, config?: RequestClientConfig): Promise<T>;
	request<T = any>(config: RequestClientConfig): Promise<T>;
}
export { RequestClient };
