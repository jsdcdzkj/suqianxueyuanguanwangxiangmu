import { ResponseRefreshTokenOperation, ResponseConfigInterceptor } from "./type";
export declare const doAuthResponseInterceptor: ({ client, doAuth, doRefreshToken, enableRefreshToken }: ResponseRefreshTokenOperation) => ResponseConfigInterceptor;
export declare const doResponseInterceptor: (messageFunc?: (message: string, error: any) => any) => ResponseConfigInterceptor;
