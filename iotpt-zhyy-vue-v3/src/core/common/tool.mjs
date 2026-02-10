export function uuid(hasHyphen = false) {
  return (hasHyphen ? "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx" : "xxxxxxxxxxxx4xxxyxxxxxxxxxxxxxxx").replace(/[xy]/g, function(c) {
    const r = Math.random() * 16 | 0;
    const v = c == "x" ? r : r & 3 | 8;
    return v.toString(16);
  });
}
export const deepClone = (object, recursion = false) => {
  if (!object) return object;
  if (!recursion) return JSON.parse(JSON.stringify(object));
  const clonedObj = Array.isArray(object) ? [] : {};
  if (object && typeof object === "object") {
    for (const key in object) {
      if (Reflect.has(object, key)) {
        if (object[key] && typeof object[key] == "object") {
          clonedObj[key] = deepClone(object[key], true);
        } else {
          clonedObj[key] = object[key];
        }
      }
    }
  }
  return clonedObj;
};
export const deepMerge = (target, merged) => {
  for (const key in merged) {
    if (merged[key] instanceof HTMLElement) {
      target[key] = merged[key];
      continue;
    }
    if (target[key] && typeof target[key] === "object") {
      deepMerge(target[key], merged[key]);
      continue;
    } else if (typeof merged[key] === "object") {
      target[key] = deepClone(merged[key], true);
      continue;
    }
    target[key] = merged[key];
  }
  return target;
};
export const cubicB\u00E9zierCurve = (t, p0, p1, p2, p3) => {
  const point = {
    x: 0,
    y: 0
  };
  t = Math.min(1, t);
  t = Math.max(0, t);
  const temp = 1 - t;
  point.x = p0.x * Math.pow(temp, 3) + 3 * p1.x * t * Math.pow(temp, 2) + 3 * p2.x * Math.pow(t, 2) * temp + p3.x * Math.pow(t, 3);
  point.y = p0.y * Math.pow(temp, 3) + 3 * p1.y * t * Math.pow(temp, 2) + 3 * p2.y * Math.pow(t, 2) * temp + p3.y * Math.pow(t, 3);
  return point;
};
export const twoB\u00E9zierCurve = (t, p1, p2, p3) => {
  const point = {
    x: 0,
    y: 0
  };
  t = Math.min(1, t);
  t = Math.max(0, t);
  const temp = 1 - t;
  point.x = Math.pow(temp, 2) * p1.x + 2 * t * temp * p2.x + Math.pow(t, 2) * p3.x;
  point.y = Math.pow(temp, 2) * p1.y + 2 * t * temp * p2.y + Math.pow(t, 2) * p3.y;
  return point;
};
export function isValidIP(ip) {
  const ipformat = /^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
  return ipformat.test(ip);
}
