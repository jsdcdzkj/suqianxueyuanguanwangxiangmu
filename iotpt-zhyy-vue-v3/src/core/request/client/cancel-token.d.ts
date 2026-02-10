import { CancelTokenSource } from "axios";
import { RequestClient } from "./instance";
type ItemCancelToken = {
    id: string;
    source: CancelTokenSource;
};
export declare class CancelTokenManager {
    instance: RequestClient;
    cancelTokenQueue: Array<ItemCancelToken>;
    constructor(instance: RequestClient);
    cancelRequest(): void;
}
export {};
