/**
 * uuid
 * @param hasHyphen
 * @returns
 */
export declare function uuid(hasHyphen?: boolean): string;
/**
 * 递归深度克隆
 * @param object 被克隆的对象
 * @param recursion 是否使用 Json.stringify 简单克隆
 * @returns 新的对象
 */
export declare const deepClone: (object: any, recursion?: boolean) => any;
/**
 * 深度合并
 * @param target
 * @param merged
 * @returns
 */
export declare const deepMerge: (target: any, merged: any) => any;
export type CurvePoint = {
    x: number;
    y: number;
};
/**
 * 三阶贝塞尔曲线
 *  B(t) =
 *      P0 * (1-t)^3 +
 *      3 * P1 * t * (1 - t)^2 +
 *      3 * P2 * t^2 * (1-t) +
 *      P3 * t^3
 * @param t {Number} 曲线长度比例  [0,1]
 * @param p0 {CurvePoint} 起始点
 * @param p1 {CurvePoint} 控制点1
 * @param p2 {CurvePoint} 控制点2
 * @param p3 {CurvePoint} 控制点3
 */
export declare const cubicBézierCurve: (t: number, p0: CurvePoint, p1: CurvePoint, p2: CurvePoint, p3: CurvePoint) => CurvePoint;
/**
 * 二阶贝塞尔曲线
 *  B(t) = (1-t)^2 * P0 + 2 * t * (1-t)*P1 + t^2 * P2
 * @param t {Number} 曲线长度比例  [0,1]
 * @param p1 {CurvePoint} 起始点
 * @param p2 {CurvePoint} 控制点2
 * @param p3 {CurvePoint} 控制点3
 */
export declare const twoBézierCurve: (t: number, p1: CurvePoint, p2: CurvePoint, p3: CurvePoint) => CurvePoint;
export declare function isValidIP(ip: string): boolean;
