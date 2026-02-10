import { deepMerge } from "./tool.ts";
export var GestureEnum = /* @__PURE__ */ ((GestureEnum2) => {
  GestureEnum2[GestureEnum2["none"] = 0] = "none";
  GestureEnum2[GestureEnum2["over"] = 1] = "over";
  GestureEnum2[GestureEnum2["down"] = 2] = "down";
  GestureEnum2[GestureEnum2["move"] = 3] = "move";
  GestureEnum2[GestureEnum2["up"] = 4] = "up";
  GestureEnum2[GestureEnum2["wheel"] = 5] = "wheel";
  GestureEnum2[GestureEnum2["leave"] = 6] = "leave";
  GestureEnum2[GestureEnum2["click"] = 7] = "click";
  return GestureEnum2;
})(GestureEnum || {});
export class Gesture {
  currentGesture = 0 /* none */;
  gesture = {
    // x轴相对上一次位移数据
    offsetX: 0,
    // y轴相对上一次位移数据
    offsetY: 0,
    // 按下x轴坐标
    downX: 0,
    // 按下y轴坐标
    downY: 0,
    // 时间戳
    temp: 0,
    // 按下时间戳
    downTemp: 0
  };
  _options = {
    container: void 0,
    preventDefault: true,
    stopPropagation: false,
    callback: void 0
  };
  constructor(options) {
    if (!options.container) throw new Error("container \u4E0D\u5B58\u5728");
    deepMerge(this._options, options);
  }
  /**更新手势数据**/
  _updateGesture(gesture) {
    Reflect.ownKeys(gesture).forEach((key) => {
      Reflect.set(this.gesture, key, Reflect.get(gesture, key));
    });
  }
  /**注册获取焦点和失去焦点事件**/
  registerEvent() {
    if (this._options.container) {
      this._options.container.onmouseover = this._mouseover.bind(this);
      this._options.container.onmouseleave = this._mouseleave.bind(this);
    }
  }
  /**移除事件**/
  removeEvent() {
    if (this._options.container) {
      this._options.container.onmouseup = null;
      this._options.container.onmousedown = null;
      this._options.container.onwheel = null;
      this.removeBodyEvent();
    }
  }
  removeBodyEvent() {
    document.body.onmousemove = null;
    document.body.onmouseup = null;
  }
  /**
   * 数据改变
   * @param gesture 手势变化数据
   * @param event 事件对象
   */
  _changeGesture(gesture, event) {
    this.currentGesture = gesture;
    if (this._options.callback)
      this._options.callback({
        status: this.currentGesture,
        gesture: this.gesture,
        e: event
      });
  }
  /**鼠标放在节点上，节点获取焦点，监听事件**/
  _mouseover(event) {
    this._eventInterception(event);
    if (this._options.container) {
      this._options.container.onmousedown = this._mousedown.bind(this);
      this._options.container.onwheel = this._mousewheel.bind(this);
    }
    this._changeGesture(1 /* over */);
  }
  /**鼠标按下**/
  _mousedown(event) {
    this._eventInterception(event);
    this._updateGesture({
      downX: event.clientX,
      downY: event.clientY,
      temp: +/* @__PURE__ */ new Date(),
      t: +/* @__PURE__ */ new Date(),
      downTemp: +/* @__PURE__ */ new Date()
    });
    this._changeGesture(2 /* down */);
    document.body.onmousemove = this._mousemove.bind(this);
    document.body.onmouseup = this._mouseup.bind(this);
  }
  /**鼠标移动**/
  _mousemove(event) {
    this._eventInterception(event);
    const { downY, downX } = this.gesture;
    const { clientX, clientY } = event;
    const temp = +/* @__PURE__ */ new Date();
    this._updateGesture({
      offsetX: clientX - (downX ? downX : 0),
      offsetY: clientY - (downY ? downY : 0),
      downX: clientX,
      downY: clientY,
      temp,
      t: temp - (this.gesture.temp ? this.gesture.temp : 0)
    });
    this._changeGesture(3 /* move */);
  }
  /**鼠标抬起**/
  _mouseup(event) {
    this._eventInterception(event);
    this.removeBodyEvent();
    if (this.currentGesture === 2 /* down */ || this.currentGesture === 3 /* move */) {
      const t = +/* @__PURE__ */ new Date() - (this.gesture.downTemp ? this.gesture.downTemp : 0);
      if (t < 100) {
        this.gesture.downTemp = 0;
        this._emitClick(event);
      } else {
        this._changeGesture(4 /* up */);
      }
    } else {
      this._changeGesture(4 /* up */);
    }
  }
  /**点击事件**/
  _emitClick(event) {
    this._changeGesture(7 /* click */, event);
  }
  /**鼠标离开节点**/
  _mouseleave() {
    this._changeGesture(6 /* leave */);
  }
  /**鼠标滚轮事件**/
  _mousewheel(wheel) {
    this._eventInterception(wheel);
    this._updateGesture({ offsetY: wheel.deltaY * 0.1 });
    this._changeGesture(5 /* wheel */, wheel);
  }
  /**事件的默认事件**/
  _eventInterception(event) {
    if (this._options.preventDefault) {
      event.preventDefault();
    }
    if (this._options.stopPropagation) {
      event.stopPropagation();
    }
  }
}
