export interface IPageUpdate {
    stop(): void;
    start(): void;
    destroy(): void;
    addUpdateListener(func: UpdateCallback): void;
    addUpdateOnceListener(func: UpdateCallback): void;
    removeUpdateListener(func: UpdateCallback): void;
    removeUpdateOnceListener(func: UpdateCallback): void;
}
export type UpdateCallback = (dt: number) => void;
export declare abstract class AbsUpdate {
    abstract onUpdate(dt: number): void;
}
export declare class PageUpdate implements IPageUpdate {
    temp: number;
    requestFrameId: number;
    onceUpdateList: any[];
    updateList: any[];
    _update(dt: number): void;
    _emitUpdateOnce(dt: number): void;
    _emitUpdate(dt: number): void;
    stop(): void;
    addUpdateListener(func: UpdateCallback): void;
    addUpdateOnceListener(func: UpdateCallback): void;
    removeUpdateListener(func: UpdateCallback): void;
    removeUpdateOnceListener(func: UpdateCallback): void;
    start(): void;
    removeAll(): void;
    destroy(): void;
}
