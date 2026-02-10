export declare enum GestureEnum {
    none = 0,
    over = 1,
    down = 2,
    move = 3,
    up = 4,
    wheel = 5,
    leave = 6,
    click = 7
}
export type GestureEventOptions = {
    downX?: number;
    downY?: number;
    offsetX?: number;
    offsetY?: number;
    temp?: number;
    t?: number;
    downTemp?: number;
};
export type GestureOptions = {
    container?: HTMLElement;
    preventDefault?: boolean;
    stopPropagation?: boolean;
    callback?: GestureCallback;
};
export type GestureEvent = {
    status: GestureEnum;
    gesture: GestureEventOptions;
    e?: MouseEvent;
};
export type GestureCallback = (event: GestureEvent) => void;
export declare class Gesture {
    currentGesture: GestureEnum;
    gesture: GestureEventOptions;
    _options: GestureOptions;
    constructor(options: GestureOptions);
    /**更新手势数据**/
    _updateGesture(gesture: GestureEventOptions): void;
    /**注册获取焦点和失去焦点事件**/
    registerEvent(): void;
    /**移除事件**/
    removeEvent(): void;
    removeBodyEvent(): void;
    /**
     * 数据改变
     * @param gesture 手势变化数据
     * @param event 事件对象
     */
    _changeGesture(gesture: GestureEnum, event?: MouseEvent): void;
    /**鼠标放在节点上，节点获取焦点，监听事件**/
    _mouseover(event: MouseEvent): void;
    /**鼠标按下**/
    _mousedown(event: MouseEvent): void;
    /**鼠标移动**/
    _mousemove(event: MouseEvent): void;
    /**鼠标抬起**/
    _mouseup(event: MouseEvent): void;
    /**点击事件**/
    _emitClick(event: MouseEvent): void;
    /**鼠标离开节点**/
    _mouseleave(): void;
    /**鼠标滚轮事件**/
    _mousewheel(wheel: WheelEvent): void;
    /**事件的默认事件**/
    _eventInterception(event: Event): void;
}
