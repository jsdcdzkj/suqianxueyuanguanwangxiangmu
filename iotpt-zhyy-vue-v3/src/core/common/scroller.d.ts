export declare enum ScrollerStatus {
    init = 0,
    fling = 1,
    stop = 2
}
export type FlingCallback = (offset: number) => void;
export declare class Scroller {
    velocityY: number;
    status: ScrollerStatus;
    flingId: number;
    temp: number;
    damping: number;
    minVelocity: number;
    threshold: number;
    flingCallback?: FlingCallback;
    constructor(flingCallback?: FlingCallback);
    onFlingCallback(flingCallback: FlingCallback): void;
    setFv(fv: number, temp: number): void;
    get isCanScroll(): boolean;
    /**惯性滚动**/
    fling(): void;
    /**开始惯性滚动**/
    _startFling(): void;
    /**开始惯性滚动**/
    changeStatus(status: ScrollerStatus): void;
}
