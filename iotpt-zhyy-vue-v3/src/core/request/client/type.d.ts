import type { AxiosRequestConfig, AxiosResponse, RawAxiosResponseHeaders, AxiosResponseHeaders, InternalAxiosRequestConfig } from "axios";
import { RequestClient } from "./instance";
export type RequestClientResponse<T> = {
    data: T;
    status: number;
    statusText: string;
    headers: RawAxiosResponseHeaders | AxiosResponseHeaders;
    config: RequestClientConfig;
    request?: any;
};
export interface RequestInterClientConfig extends InternalAxiosRequestConfig<any> {
    operationMessage?: string;
    noAuthToLogin?: boolean;
    enableCancelTokenManger?: boolean;
}
export interface RequestClientConfig extends AxiosRequestConfig<any> {
    operationMessage?: string;
    noAuthToLogin?: boolean;
    enableCancelTokenManger?: boolean;
    uploadFileName?: string;
}
export type ResponseRefreshTokenOperation = {
    client: RequestClient;
    doAuth: () => Promise<any>;
    doRefreshToken: () => Promise<any>;
    enableRefreshToken: boolean;
};
export type RefreshQueueItem = (newToken: string) => any;
export type ResponseConfigInterceptor<T = any> = {
    onFulfilled?: (response: AxiosResponse<T>) => AxiosResponse | Promise<AxiosResponse>;
    onRejected: (error: any) => any;
};
