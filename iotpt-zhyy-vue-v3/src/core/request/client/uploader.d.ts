import { AxiosResponse } from "axios";
import { RequestClient } from "./instance";
import { RequestClientConfig } from "./type";
declare class UploaderManager {
    client: RequestClient;
    constructor(client: RequestClient);
    upload<T = any>(url: string, data: {
        files: Array<File>;
    } & Record<string, any>, config?: RequestClientConfig): Promise<AxiosResponse<T>>;
}
export { UploaderManager };
