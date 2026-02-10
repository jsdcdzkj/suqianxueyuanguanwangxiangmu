export var ScrollerStatus = /* @__PURE__ */ ((ScrollerStatus2) => {
  ScrollerStatus2[ScrollerStatus2["init"] = 0] = "init";
  ScrollerStatus2[ScrollerStatus2["fling"] = 1] = "fling";
  ScrollerStatus2[ScrollerStatus2["stop"] = 2] = "stop";
  return ScrollerStatus2;
})(ScrollerStatus || {});
export class Scroller {
  // 速度
  velocityY = 0;
  // 滑动状态
  status = 0 /* init */;
  flingId = -1;
  // 时间插值
  temp = 0;
  // 阻尼
  damping = 0.9;
  minVelocity = 0.1;
  // 触发惯性的最小速度
  threshold = 0.6;
  // 惯性滚动回调
  flingCallback;
  constructor(flingCallback) {
    if (flingCallback) {
      this.flingCallback = flingCallback;
    }
  }
  onFlingCallback(flingCallback) {
    this.flingCallback = flingCallback;
  }
  setFv(fv, temp) {
    this.velocityY = fv / temp;
  }
  get isCanScroll() {
    return this.status == 0 /* init */ || this.status == 2 /* stop */;
  }
  /**惯性滚动**/
  fling() {
    this.changeStatus(1 /* fling */);
    this.temp = +/* @__PURE__ */ new Date();
    this.flingId = requestAnimationFrame(this._startFling.bind(this));
  }
  /**开始惯性滚动**/
  _startFling() {
    if (Math.abs(this.velocityY) < this.minVelocity) {
      this.changeStatus(2 /* stop */);
      cancelAnimationFrame(this.flingId);
      return;
    }
    this.flingCallback?.(this.velocityY * 16);
    this.velocityY *= this.damping;
    this.flingId = requestAnimationFrame(this._startFling.bind(this));
  }
  /**开始惯性滚动**/
  changeStatus(status) {
    this.status = status;
  }
}
