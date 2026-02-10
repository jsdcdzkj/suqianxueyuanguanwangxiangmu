import { AxiosResponse } from "axios";
import { RequestClient } from "./instance";
import type { RequestClientConfig } from "./type";
declare class DownloaderManger {
    client: RequestClient;
    constructor(client: RequestClient);
    download(url: string, config?: RequestClientConfig): Promise<AxiosResponse<Blob>>;
    downloadFile(blob: Blob, fileName: string): void;
}
export { DownloaderManger };
