import type { AxiosInstance, InternalAxiosRequestConfig } from "axios";
import type { RequestClientConfig, RequestClientResponse } from "./type";
export declare const clientResponseInterceptor: (response: RequestClientResponse<any>) => RequestClientResponse<any>;
export declare const clientRequestInterceptor: (config: RequestClientConfig) => RequestClientConfig;
export declare class InterceptorManager {
    instance: AxiosInstance;
    constructor(instance: AxiosInstance);
    addRequestInterceptor(onFulfilled?: ((value: InternalAxiosRequestConfig<any>) => InternalAxiosRequestConfig<any> | Promise<InternalAxiosRequestConfig<any>>) | null, onRejected?: ((error: any) => any) | null): void;
    addResponseInterceptor<T>(onFulfilled?: ((value: RequestClientResponse<T>) => any | Promise<any>) | null, onRejected?: ((error: any) => any) | null): void;
}
