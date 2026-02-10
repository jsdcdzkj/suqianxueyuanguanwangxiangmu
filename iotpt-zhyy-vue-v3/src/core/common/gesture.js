import { deepMerge as u } from "./tool.js";
export var GestureEnum = ((t) => (
	(t[(t.none = 0)] = "none"),
	(t[(t.over = 1)] = "over"),
	(t[(t.down = 2)] = "down"),
	(t[(t.move = 3)] = "move"),
	(t[(t.up = 4)] = "up"),
	(t[(t.wheel = 5)] = "wheel"),
	(t[(t.leave = 6)] = "leave"),
	(t[(t.click = 7)] = "click"),
	t
))(GestureEnum || {});
export class Gesture {
	currentGesture = 0;
	gesture = { offsetX: 0, offsetY: 0, downX: 0, downY: 0, temp: 0, downTemp: 0 };
	_options = { container: void 0, preventDefault: !0, stopPropagation: !1, callback: void 0 };
	constructor(e) {
		if (!e.container) throw new Error("container \u4E0D\u5B58\u5728");
		u(this._options, e);
	}
	_updateGesture(e) {
		Reflect.ownKeys(e).forEach((o) => {
			Reflect.set(this.gesture, o, Reflect.get(e, o));
		});
	}
	registerEvent() {
		this._options.container &&
			((this._options.container.onmouseover = this._mouseover.bind(this)),
			(this._options.container.onmouseleave = this._mouseleave.bind(this)));
	}
	removeEvent() {
		this._options.container &&
			((this._options.container.onmouseup = null),
			(this._options.container.onmousedown = null),
			(this._options.container.onwheel = null),
			this.removeBodyEvent());
	}
	removeBodyEvent() {
		(document.body.onmousemove = null), (document.body.onmouseup = null);
	}
	_changeGesture(e, o) {
		(this.currentGesture = e),
			this._options.callback &&
				this._options.callback({ status: this.currentGesture, gesture: this.gesture, e: o });
	}
	_mouseover(e) {
		this._eventInterception(e),
			this._options.container &&
				((this._options.container.onmousedown = this._mousedown.bind(this)),
				(this._options.container.onwheel = this._mousewheel.bind(this))),
			this._changeGesture(1);
	}
	_mousedown(e) {
		this._eventInterception(e),
			this._updateGesture({
				downX: e.clientX,
				downY: e.clientY,
				temp: +new Date(),
				t: +new Date(),
				downTemp: +new Date()
			}),
			this._changeGesture(2),
			(document.body.onmousemove = this._mousemove.bind(this)),
			(document.body.onmouseup = this._mouseup.bind(this));
	}
	_mousemove(e) {
		this._eventInterception(e);
		const { downY: o, downX: n } = this.gesture,
			{ clientX: s, clientY: i } = e,
			r = +new Date();
		this._updateGesture({
			offsetX: s - (n || 0),
			offsetY: i - (o || 0),
			downX: s,
			downY: i,
			temp: r,
			t: r - (this.gesture.temp ? this.gesture.temp : 0)
		}),
			this._changeGesture(3);
	}
	_mouseup(e) {
		this._eventInterception(e),
			this.removeBodyEvent(),
			this.currentGesture === 2 || this.currentGesture === 3
				? +new Date() - (this.gesture.downTemp ? this.gesture.downTemp : 0) < 100
					? ((this.gesture.downTemp = 0), this._emitClick(e))
					: this._changeGesture(4)
				: this._changeGesture(4);
	}
	_emitClick(e) {
		this._changeGesture(7, e);
	}
	_mouseleave() {
		this._changeGesture(6);
	}
	_mousewheel(e) {
		this._eventInterception(e), this._updateGesture({ offsetY: e.deltaY * 0.1 }), this._changeGesture(5, e);
	}
	_eventInterception(e) {
		this._options.preventDefault && e.preventDefault(), this._options.stopPropagation && e.stopPropagation();
	}
}
