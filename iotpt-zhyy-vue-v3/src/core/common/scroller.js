export var ScrollerStatus = ((i) => (
	(i[(i.init = 0)] = "init"), (i[(i.fling = 1)] = "fling"), (i[(i.stop = 2)] = "stop"), i
))(ScrollerStatus || {});
export class Scroller {
	velocityY = 0;
	status = 0;
	flingId = -1;
	temp = 0;
	damping = 0.9;
	minVelocity = 0.1;
	threshold = 0.6;
	flingCallback;
	constructor(t) {
		t && (this.flingCallback = t);
	}
	onFlingCallback(t) {
		this.flingCallback = t;
	}
	setFv(t, s) {
		this.velocityY = t / s;
	}
	get isCanScroll() {
		return this.status == 0 || this.status == 2;
	}
	fling() {
		this.changeStatus(1),
			(this.temp = +new Date()),
			(this.flingId = requestAnimationFrame(this._startFling.bind(this)));
	}
	_startFling() {
		if (Math.abs(this.velocityY) < this.minVelocity) {
			this.changeStatus(2), cancelAnimationFrame(this.flingId);
			return;
		}
		this.flingCallback?.(this.velocityY * 16),
			(this.velocityY *= this.damping),
			(this.flingId = requestAnimationFrame(this._startFling.bind(this)));
	}
	changeStatus(t) {
		this.status = t;
	}
}
