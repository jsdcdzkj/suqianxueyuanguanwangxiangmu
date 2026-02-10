export class AbsUpdate {
}
export class PageUpdate {
  temp = +/* @__PURE__ */ new Date();
  requestFrameId = -1;
  onceUpdateList = [];
  updateList = [];
  _update(dt) {
    this._emitUpdateOnce(dt);
    this._emitUpdate(dt);
  }
  _emitUpdateOnce(dt) {
    for (let i = this.onceUpdateList.length - 1; i >= 0; i--) {
      const [update] = this.onceUpdateList.splice(i, 1);
      if (update) update(dt);
    }
  }
  _emitUpdate(dt) {
    for (let i = this.updateList.length - 1; i >= 0; i--) {
      this.updateList[i](dt);
    }
  }
  stop() {
    if (this.requestFrameId != -1) {
      cancelAnimationFrame(this.requestFrameId);
    }
  }
  addUpdateListener(func) {
    const isExist = this.updateList.find((call) => call === func);
    if (!isExist) {
      this.updateList.push(func);
    }
  }
  addUpdateOnceListener(func) {
    const isExist = this.onceUpdateList.find((call) => call === func);
    if (!isExist) {
      this.onceUpdateList.push(func);
    }
  }
  removeUpdateListener(func) {
    const isExist = this.updateList.findIndex((call) => call === func);
    if (isExist > -1) {
      this.updateList.splice(isExist, 1);
    }
  }
  removeUpdateOnceListener(func) {
    const isExist = this.onceUpdateList.findIndex((call) => call === func);
    if (isExist > -1) {
      this.onceUpdateList.splice(isExist, 1);
    }
  }
  start() {
    this.temp = +/* @__PURE__ */ new Date();
    this.requestFrameId = requestAnimationFrame(() => {
      const dt = +/* @__PURE__ */ new Date() - this.temp;
      this._update(dt / 1e3);
      this.start();
    });
  }
  removeAll() {
    this.updateList = [];
    this.onceUpdateList = [];
  }
  destroy() {
    this.stop();
    this.requestFrameId = -1;
    this.removeAll();
  }
}
