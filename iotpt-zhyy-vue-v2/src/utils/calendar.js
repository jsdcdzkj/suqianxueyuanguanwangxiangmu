var __defProp = Object.defineProperty;
var __defNormalProp = (obj, key, value) => key in obj ? __defProp(obj, key, { enumerable: true, configurable: true, writable: true, value }) : obj[key] = value;
var __publicField = (obj, key, value) => {
  __defNormalProp(obj, typeof key !== "symbol" ? key + "" : key, value);
  return value;
};
"undefined" != typeof globalThis ? globalThis : "undefined" != typeof window ? window : "undefined" != typeof global ? global : "undefined" != typeof self && self;
function e(e2) {
  return e2 && e2.__esModule && Object.prototype.hasOwnProperty.call(e2, "default") ? e2.default : e2;
}
var t = { exports: {} };
const b = e(t.exports = function() {
  var e2 = 1e3, t2 = 6e4, b2 = 36e5, f2 = "millisecond", n2 = "second", c2 = "minute", a2 = "hour", r2 = "day", i2 = "week", s2 = "month", u2 = "quarter", o2 = "year", d2 = "date", h2 = "Invalid Date", l = /^(\d{4})[-/]?(\d{1,2})?[-/]?(\d{0,2})[Tt\s]*(\d{1,2})?:?(\d{1,2})?:?(\d{1,2})?[.:]?(\d+)?$/, y = /\[([^\]]+)]|Y{1,4}|M{1,4}|D{1,2}|d{1,4}|H{1,2}|h{1,2}|a|A|m{1,2}|s{1,2}|Z{1,2}|SSS/g, D = { name: "en", weekdays: "Sunday_Monday_Tuesday_Wednesday_Thursday_Friday_Saturday".split("_"), months: "January_February_March_April_May_June_July_August_September_October_November_December".split("_"), ordinal: function(e3) {
    var t3 = ["th", "st", "nd", "rd"], b3 = e3 % 100;
    return "[" + e3 + (t3[(b3 - 20) % 10] || t3[b3] || t3[0]) + "]";
  } }, g = function(e3, t3, b3) {
    var f3 = String(e3);
    return !f3 || f3.length >= t3 ? e3 : "" + Array(t3 + 1 - f3.length).join(b3) + e3;
  }, v = { s: g, z: function(e3) {
    var t3 = -e3.utcOffset(), b3 = Math.abs(t3), f3 = Math.floor(b3 / 60), n3 = b3 % 60;
    return (t3 <= 0 ? "+" : "-") + g(f3, 2, "0") + ":" + g(n3, 2, "0");
  }, m: function e3(t3, b3) {
    if (t3.date() < b3.date())
      return -e3(b3, t3);
    var f3 = 12 * (b3.year() - t3.year()) + (b3.month() - t3.month()), n3 = t3.clone().add(f3, s2), c3 = b3 - n3 < 0, a3 = t3.clone().add(f3 + (c3 ? -1 : 1), s2);
    return +(-(f3 + (b3 - n3) / (c3 ? n3 - a3 : a3 - n3)) || 0);
  }, a: function(e3) {
    return e3 < 0 ? Math.ceil(e3) || 0 : Math.floor(e3);
  }, p: function(e3) {
    return { M: s2, y: o2, w: i2, d: r2, D: d2, h: a2, m: c2, s: n2, ms: f2, Q: u2 }[e3] || String(e3 || "").toLowerCase().replace(/s$/, "");
  }, u: function(e3) {
    return void 0 === e3;
  } }, M = "en", m = {};
  m[M] = D;
  var $ = "$isDayjsObject", p = function(e3) {
    return e3 instanceof S || !(!e3 || !e3[$]);
  }, w = function e3(t3, b3, f3) {
    var n3;
    if (!t3)
      return M;
    if ("string" == typeof t3) {
      var c3 = t3.toLowerCase();
      m[c3] && (n3 = c3), b3 && (m[c3] = b3, n3 = c3);
      var a3 = t3.split("-");
      if (!n3 && a3.length > 1)
        return e3(a3[0]);
    } else {
      var r3 = t3.name;
      m[r3] = t3, n3 = r3;
    }
    return !f3 && n3 && (M = n3), n3 || !f3 && M;
  }, Y = function(e3, t3) {
    if (p(e3))
      return e3.clone();
    var b3 = "object" == typeof t3 ? t3 : {};
    return b3.date = e3, b3.args = arguments, new S(b3);
  }, _ = v;
  _.l = w, _.i = p, _.w = function(e3, t3) {
    return Y(e3, { locale: t3.$L, utc: t3.$u, x: t3.$x, $offset: t3.$offset });
  };
  var S = function() {
    function D2(e3) {
      this.$L = w(e3.locale, null, true), this.parse(e3), this.$x = this.$x || e3.x || {}, this[$] = true;
    }
    var g2 = D2.prototype;
    return g2.parse = function(e3) {
      this.$d = function(e4) {
        var t3 = e4.date, b3 = e4.utc;
        if (null === t3)
          return /* @__PURE__ */ new Date(NaN);
        if (_.u(t3))
          return /* @__PURE__ */ new Date();
        if (t3 instanceof Date)
          return new Date(t3);
        if ("string" == typeof t3 && !/Z$/i.test(t3)) {
          var f3 = t3.match(l);
          if (f3) {
            var n3 = f3[2] - 1 || 0, c3 = (f3[7] || "0").substring(0, 3);
            return b3 ? new Date(Date.UTC(f3[1], n3, f3[3] || 1, f3[4] || 0, f3[5] || 0, f3[6] || 0, c3)) : new Date(f3[1], n3, f3[3] || 1, f3[4] || 0, f3[5] || 0, f3[6] || 0, c3);
          }
        }
        return new Date(t3);
      }(e3), this.init();
    }, g2.init = function() {
      var e3 = this.$d;
      this.$y = e3.getFullYear(), this.$M = e3.getMonth(), this.$D = e3.getDate(), this.$W = e3.getDay(), this.$H = e3.getHours(), this.$m = e3.getMinutes(), this.$s = e3.getSeconds(), this.$ms = e3.getMilliseconds();
    }, g2.$utils = function() {
      return _;
    }, g2.isValid = function() {
      return !(this.$d.toString() === h2);
    }, g2.isSame = function(e3, t3) {
      var b3 = Y(e3);
      return this.startOf(t3) <= b3 && b3 <= this.endOf(t3);
    }, g2.isAfter = function(e3, t3) {
      return Y(e3) < this.startOf(t3);
    }, g2.isBefore = function(e3, t3) {
      return this.endOf(t3) < Y(e3);
    }, g2.$g = function(e3, t3, b3) {
      return _.u(e3) ? this[t3] : this.set(b3, e3);
    }, g2.unix = function() {
      return Math.floor(this.valueOf() / 1e3);
    }, g2.valueOf = function() {
      return this.$d.getTime();
    }, g2.startOf = function(e3, t3) {
      var b3 = this, f3 = !!_.u(t3) || t3, u3 = _.p(e3), h3 = function(e4, t4) {
        var n3 = _.w(b3.$u ? Date.UTC(b3.$y, t4, e4) : new Date(b3.$y, t4, e4), b3);
        return f3 ? n3 : n3.endOf(r2);
      }, l2 = function(e4, t4) {
        return _.w(b3.toDate()[e4].apply(b3.toDate("s"), (f3 ? [0, 0, 0, 0] : [23, 59, 59, 999]).slice(t4)), b3);
      }, y2 = this.$W, D3 = this.$M, g3 = this.$D, v2 = "set" + (this.$u ? "UTC" : "");
      switch (u3) {
        case o2:
          return f3 ? h3(1, 0) : h3(31, 11);
        case s2:
          return f3 ? h3(1, D3) : h3(0, D3 + 1);
        case i2:
          var M2 = this.$locale().weekStart || 0, m2 = (y2 < M2 ? y2 + 7 : y2) - M2;
          return h3(f3 ? g3 - m2 : g3 + (6 - m2), D3);
        case r2:
        case d2:
          return l2(v2 + "Hours", 0);
        case a2:
          return l2(v2 + "Minutes", 1);
        case c2:
          return l2(v2 + "Seconds", 2);
        case n2:
          return l2(v2 + "Milliseconds", 3);
        default:
          return this.clone();
      }
    }, g2.endOf = function(e3) {
      return this.startOf(e3, false);
    }, g2.$set = function(e3, t3) {
      var b3, i3 = _.p(e3), u3 = "set" + (this.$u ? "UTC" : ""), h3 = (b3 = {}, b3[r2] = u3 + "Date", b3[d2] = u3 + "Date", b3[s2] = u3 + "Month", b3[o2] = u3 + "FullYear", b3[a2] = u3 + "Hours", b3[c2] = u3 + "Minutes", b3[n2] = u3 + "Seconds", b3[f2] = u3 + "Milliseconds", b3)[i3], l2 = i3 === r2 ? this.$D + (t3 - this.$W) : t3;
      if (i3 === s2 || i3 === o2) {
        var y2 = this.clone().set(d2, 1);
        y2.$d[h3](l2), y2.init(), this.$d = y2.set(d2, Math.min(this.$D, y2.daysInMonth())).$d;
      } else
        h3 && this.$d[h3](l2);
      return this.init(), this;
    }, g2.set = function(e3, t3) {
      return this.clone().$set(e3, t3);
    }, g2.get = function(e3) {
      return this[_.p(e3)]();
    }, g2.add = function(f3, u3) {
      var d3, h3 = this;
      f3 = Number(f3);
      var l2 = _.p(u3), y2 = function(e3) {
        var t3 = Y(h3);
        return _.w(t3.date(t3.date() + Math.round(e3 * f3)), h3);
      };
      if (l2 === s2)
        return this.set(s2, this.$M + f3);
      if (l2 === o2)
        return this.set(o2, this.$y + f3);
      if (l2 === r2)
        return y2(1);
      if (l2 === i2)
        return y2(7);
      var D3 = (d3 = {}, d3[c2] = t2, d3[a2] = b2, d3[n2] = e2, d3)[l2] || 1, g3 = this.$d.getTime() + f3 * D3;
      return _.w(g3, this);
    }, g2.subtract = function(e3, t3) {
      return this.add(-1 * e3, t3);
    }, g2.format = function(e3) {
      var t3 = this, b3 = this.$locale();
      if (!this.isValid())
        return b3.invalidDate || h2;
      var f3 = e3 || "YYYY-MM-DDTHH:mm:ssZ", n3 = _.z(this), c3 = this.$H, a3 = this.$m, r3 = this.$M, i3 = b3.weekdays, s3 = b3.months, u3 = b3.meridiem, o3 = function(e4, b4, n4, c4) {
        return e4 && (e4[b4] || e4(t3, f3)) || n4[b4].slice(0, c4);
      }, d3 = function(e4) {
        return _.s(c3 % 12 || 12, e4, "0");
      }, l2 = u3 || function(e4, t4, b4) {
        var f4 = e4 < 12 ? "AM" : "PM";
        return b4 ? f4.toLowerCase() : f4;
      };
      return f3.replace(y, function(e4, f4) {
        return f4 || function(e5) {
          switch (e5) {
            case "YY":
              return String(t3.$y).slice(-2);
            case "YYYY":
              return _.s(t3.$y, 4, "0");
            case "M":
              return r3 + 1;
            case "MM":
              return _.s(r3 + 1, 2, "0");
            case "MMM":
              return o3(b3.monthsShort, r3, s3, 3);
            case "MMMM":
              return o3(s3, r3);
            case "D":
              return t3.$D;
            case "DD":
              return _.s(t3.$D, 2, "0");
            case "d":
              return String(t3.$W);
            case "dd":
              return o3(b3.weekdaysMin, t3.$W, i3, 2);
            case "ddd":
              return o3(b3.weekdaysShort, t3.$W, i3, 3);
            case "dddd":
              return i3[t3.$W];
            case "H":
              return String(c3);
            case "HH":
              return _.s(c3, 2, "0");
            case "h":
              return d3(1);
            case "hh":
              return d3(2);
            case "a":
              return l2(c3, a3, true);
            case "A":
              return l2(c3, a3, false);
            case "m":
              return String(a3);
            case "mm":
              return _.s(a3, 2, "0");
            case "s":
              return String(t3.$s);
            case "ss":
              return _.s(t3.$s, 2, "0");
            case "SSS":
              return _.s(t3.$ms, 3, "0");
            case "Z":
              return n3;
          }
          return null;
        }(e4) || n3.replace(":", "");
      });
    }, g2.utcOffset = function() {
      return 15 * -Math.round(this.$d.getTimezoneOffset() / 15);
    }, g2.diff = function(f3, d3, h3) {
      var l2, y2 = this, D3 = _.p(d3), g3 = Y(f3), v2 = (g3.utcOffset() - this.utcOffset()) * t2, M2 = this - g3, m2 = function() {
        return _.m(y2, g3);
      };
      switch (D3) {
        case o2:
          l2 = m2() / 12;
          break;
        case s2:
          l2 = m2();
          break;
        case u2:
          l2 = m2() / 3;
          break;
        case i2:
          l2 = (M2 - v2) / 6048e5;
          break;
        case r2:
          l2 = (M2 - v2) / 864e5;
          break;
        case a2:
          l2 = M2 / b2;
          break;
        case c2:
          l2 = M2 / t2;
          break;
        case n2:
          l2 = M2 / e2;
          break;
        default:
          l2 = M2;
      }
      return h3 ? l2 : _.a(l2);
    }, g2.daysInMonth = function() {
      return this.endOf(s2).$D;
    }, g2.$locale = function() {
      return m[this.$L];
    }, g2.locale = function(e3, t3) {
      if (!e3)
        return this.$L;
      var b3 = this.clone(), f3 = w(e3, t3, true);
      return f3 && (b3.$L = f3), b3;
    }, g2.clone = function() {
      return _.w(this.$d, this);
    }, g2.toDate = function() {
      return new Date(this.valueOf());
    }, g2.toJSON = function() {
      return this.isValid() ? this.toISOString() : null;
    }, g2.toISOString = function() {
      return this.$d.toISOString();
    }, g2.toString = function() {
      return this.$d.toUTCString();
    }, D2;
  }(), k = S.prototype;
  return Y.prototype = k, [["$ms", f2], ["$s", n2], ["$m", c2], ["$H", a2], ["$W", r2], ["$M", s2], ["$y", o2], ["$D", d2]].forEach(function(e3) {
    k[e3[1]] = function(t3) {
      return this.$g(t3, e3[0], e3[1]);
    };
  }), Y.extend = function(e3, t3) {
    return e3.$i || (e3(t3, S, Y), e3.$i = true), Y;
  }, Y.locale = w, Y.isDayjs = p, Y.unix = function(e3) {
    return Y(1e3 * e3);
  }, Y.en = m[M], Y.Ls = m, Y.p = {}, Y;
}());
var f = { lunarInfo: [19416, 19168, 42352, 21717, 53856, 55632, 91476, 22176, 39632, 21970, 19168, 42422, 42192, 53840, 119381, 46400, 54944, 44450, 38320, 84343, 18800, 42160, 46261, 27216, 27968, 109396, 11104, 38256, 21234, 18800, 25958, 54432, 59984, 92821, 23248, 11104, 100067, 37600, 116951, 51536, 54432, 120998, 46416, 22176, 107956, 9680, 37584, 53938, 43344, 46423, 27808, 46416, 86869, 19872, 42416, 83315, 21168, 43432, 59728, 27296, 44710, 43856, 19296, 43748, 42352, 21088, 62051, 55632, 23383, 22176, 38608, 19925, 19152, 42192, 54484, 53840, 54616, 46400, 46752, 103846, 38320, 18864, 43380, 42160, 45690, 27216, 27968, 44870, 43872, 38256, 19189, 18800, 25776, 29859, 59984, 27480, 23232, 43872, 38613, 37600, 51552, 55636, 54432, 55888, 30034, 22176, 43959, 9680, 37584, 51893, 43344, 46240, 47780, 44368, 21977, 19360, 42416, 86390, 21168, 43312, 31060, 27296, 44368, 23378, 19296, 42726, 42208, 53856, 60005, 54576, 23200, 30371, 38608, 19195, 19152, 42192, 118966, 53840, 54560, 56645, 46496, 22224, 21938, 18864, 42359, 42160, 43600, 111189, 27936, 44448, 84835, 37744, 18936, 18800, 25776, 92326, 59984, 27424, 108228, 43744, 37600, 53987, 51552, 54615, 54432, 55888, 23893, 22176, 42704, 21972, 21200, 43448, 43344, 46240, 46758, 44368, 21920, 43940, 42416, 21168, 45683, 26928, 29495, 27296, 44368, 84821, 19296, 42352, 21732, 53600, 59752, 54560, 55968, 92838, 22224, 19168, 43476, 41680, 53584, 62034, 54560], solarMonth: [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31], Gan: ["甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"], Zhi: ["子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"], Animals: ["鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"], festival: { "1-1": { title: "元旦节" }, "2-14": { title: "情人节" }, "5-1": { title: "劳动节" }, "5-4": { title: "青年节" }, "6-1": { title: "儿童节" }, "9-10": { title: "教师节" }, "10-1": { title: "国庆节" }, "12-25": { title: "圣诞节" }, "3-8": { title: "妇女节" }, "3-12": { title: "植树节" }, "4-1": { title: "愚人节" }, "5-12": { title: "护士节" }, "7-1": { title: "建党节" }, "8-1": { title: "建军节" }, "12-24": { title: "平安夜" } }, lFestival: { "12-30": { title: "除夕" }, "1-1": { title: "春节" }, "1-15": { title: "元宵节" }, "2-2": { title: "龙抬头" }, "5-5": { title: "端午节" }, "7-7": { title: "七夕节" }, "7-15": { title: "中元节" }, "8-15": { title: "中秋节" }, "9-9": { title: "重阳节" }, "10-1": { title: "寒衣节" }, "10-15": { title: "下元节" }, "12-8": { title: "腊八节" }, "12-23": { title: "北方小年" }, "12-24": { title: "南方小年" } }, solarTerm: ["小寒", "大寒", "立春", "雨水", "惊蛰", "春分", "清明", "谷雨", "立夏", "小满", "芒种", "夏至", "小暑", "大暑", "立秋", "处暑", "白露", "秋分", "寒露", "霜降", "立冬", "小雪", "大雪", "冬至"], sTermInfo: ["9778397bd097c36b0b6fc9274c91aa", "97b6b97bd19801ec9210c965cc920e", "97bcf97c3598082c95f8c965cc920f", "97bd0b06bdb0722c965ce1cfcc920f", "b027097bd097c36b0b6fc9274c91aa", "97b6b97bd19801ec9210c965cc920e", "97bcf97c359801ec95f8c965cc920f", "97bd0b06bdb0722c965ce1cfcc920f", "b027097bd097c36b0b6fc9274c91aa", "97b6b97bd19801ec9210c965cc920e", "97bcf97c359801ec95f8c965cc920f", "97bd0b06bdb0722c965ce1cfcc920f", "b027097bd097c36b0b6fc9274c91aa", "9778397bd19801ec9210c965cc920e", "97b6b97bd19801ec95f8c965cc920f", "97bd09801d98082c95f8e1cfcc920f", "97bd097bd097c36b0b6fc9210c8dc2", "9778397bd197c36c9210c9274c91aa", "97b6b97bd19801ec95f8c965cc920e", "97bd09801d98082c95f8e1cfcc920f", "97bd097bd097c36b0b6fc9210c8dc2", "9778397bd097c36c9210c9274c91aa", "97b6b97bd19801ec95f8c965cc920e", "97bcf97c3598082c95f8e1cfcc920f", "97bd097bd097c36b0b6fc9210c8dc2", "9778397bd097c36c9210c9274c91aa", "97b6b97bd19801ec9210c965cc920e", "97bcf97c3598082c95f8c965cc920f", "97bd097bd097c35b0b6fc920fb0722", "9778397bd097c36b0b6fc9274c91aa", "97b6b97bd19801ec9210c965cc920e", "97bcf97c3598082c95f8c965cc920f", "97bd097bd097c35b0b6fc920fb0722", "9778397bd097c36b0b6fc9274c91aa", "97b6b97bd19801ec9210c965cc920e", "97bcf97c359801ec95f8c965cc920f", "97bd097bd097c35b0b6fc920fb0722", "9778397bd097c36b0b6fc9274c91aa", "97b6b97bd19801ec9210c965cc920e", "97bcf97c359801ec95f8c965cc920f", "97bd097bd097c35b0b6fc920fb0722", "9778397bd097c36b0b6fc9274c91aa", "97b6b97bd19801ec9210c965cc920e", "97bcf97c359801ec95f8c965cc920f", "97bd097bd07f595b0b6fc920fb0722", "9778397bd097c36b0b6fc9210c8dc2", "9778397bd19801ec9210c9274c920e", "97b6b97bd19801ec95f8c965cc920f", "97bd07f5307f595b0b0bc920fb0722", "7f0e397bd097c36b0b6fc9210c8dc2", "9778397bd097c36c9210c9274c920e", "97b6b97bd19801ec95f8c965cc920f", "97bd07f5307f595b0b0bc920fb0722", "7f0e397bd097c36b0b6fc9210c8dc2", "9778397bd097c36c9210c9274c91aa", "97b6b97bd19801ec9210c965cc920e", "97bd07f1487f595b0b0bc920fb0722", "7f0e397bd097c36b0b6fc9210c8dc2", "9778397bd097c36b0b6fc9274c91aa", "97b6b97bd19801ec9210c965cc920e", "97bcf7f1487f595b0b0bb0b6fb0722", "7f0e397bd097c35b0b6fc920fb0722", "9778397bd097c36b0b6fc9274c91aa", "97b6b97bd19801ec9210c965cc920e", "97bcf7f1487f595b0b0bb0b6fb0722", "7f0e397bd097c35b0b6fc920fb0722", "9778397bd097c36b0b6fc9274c91aa", "97b6b97bd19801ec9210c965cc920e", "97bcf7f1487f531b0b0bb0b6fb0722", "7f0e397bd097c35b0b6fc920fb0722", "9778397bd097c36b0b6fc9274c91aa", "97b6b97bd19801ec9210c965cc920e", "97bcf7f1487f531b0b0bb0b6fb0722", "7f0e397bd07f595b0b6fc920fb0722", "9778397bd097c36b0b6fc9274c91aa", "97b6b97bd19801ec9210c9274c920e", "97bcf7f0e47f531b0b0bb0b6fb0722", "7f0e397bd07f595b0b0bc920fb0722", "9778397bd097c36b0b6fc9210c91aa", "97b6b97bd197c36c9210c9274c920e", "97bcf7f0e47f531b0b0bb0b6fb0722", "7f0e397bd07f595b0b0bc920fb0722", "9778397bd097c36b0b6fc9210c8dc2", "9778397bd097c36c9210c9274c920e", "97b6b7f0e47f531b0723b0b6fb0722", "7f0e37f5307f595b0b0bc920fb0722", "7f0e397bd097c36b0b6fc9210c8dc2", "9778397bd097c36b0b70c9274c91aa", "97b6b7f0e47f531b0723b0b6fb0721", "7f0e37f1487f595b0b0bb0b6fb0722", "7f0e397bd097c35b0b6fc9210c8dc2", "9778397bd097c36b0b6fc9274c91aa", "97b6b7f0e47f531b0723b0b6fb0721", "7f0e27f1487f595b0b0bb0b6fb0722", "7f0e397bd097c35b0b6fc920fb0722", "9778397bd097c36b0b6fc9274c91aa", "97b6b7f0e47f531b0723b0b6fb0721", "7f0e27f1487f531b0b0bb0b6fb0722", "7f0e397bd097c35b0b6fc920fb0722", "9778397bd097c36b0b6fc9274c91aa", "97b6b7f0e47f531b0723b0b6fb0721", "7f0e27f1487f531b0b0bb0b6fb0722", "7f0e397bd097c35b0b6fc920fb0722", "9778397bd097c36b0b6fc9274c91aa", "97b6b7f0e47f531b0723b0b6fb0721", "7f0e27f1487f531b0b0bb0b6fb0722", "7f0e397bd07f595b0b0bc920fb0722", "9778397bd097c36b0b6fc9274c91aa", "97b6b7f0e47f531b0723b0787b0721", "7f0e27f0e47f531b0b0bb0b6fb0722", "7f0e397bd07f595b0b0bc920fb0722", "9778397bd097c36b0b6fc9210c91aa", "97b6b7f0e47f149b0723b0787b0721", "7f0e27f0e47f531b0723b0b6fb0722", "7f0e397bd07f595b0b0bc920fb0722", "9778397bd097c36b0b6fc9210c8dc2", "977837f0e37f149b0723b0787b0721", "7f07e7f0e47f531b0723b0b6fb0722", "7f0e37f5307f595b0b0bc920fb0722", "7f0e397bd097c35b0b6fc9210c8dc2", "977837f0e37f14998082b0787b0721", "7f07e7f0e47f531b0723b0b6fb0721", "7f0e37f1487f595b0b0bb0b6fb0722", "7f0e397bd097c35b0b6fc9210c8dc2", "977837f0e37f14998082b0787b06bd", "7f07e7f0e47f531b0723b0b6fb0721", "7f0e27f1487f531b0b0bb0b6fb0722", "7f0e397bd097c35b0b6fc920fb0722", "977837f0e37f14998082b0787b06bd", "7f07e7f0e47f531b0723b0b6fb0721", "7f0e27f1487f531b0b0bb0b6fb0722", "7f0e397bd097c35b0b6fc920fb0722", "977837f0e37f14998082b0787b06bd", "7f07e7f0e47f531b0723b0b6fb0721", "7f0e27f1487f531b0b0bb0b6fb0722", "7f0e397bd07f595b0b0bc920fb0722", "977837f0e37f14998082b0787b06bd", "7f07e7f0e47f531b0723b0b6fb0721", "7f0e27f1487f531b0b0bb0b6fb0722", "7f0e397bd07f595b0b0bc920fb0722", "977837f0e37f14998082b0787b06bd", "7f07e7f0e47f149b0723b0787b0721", "7f0e27f0e47f531b0b0bb0b6fb0722", "7f0e397bd07f595b0b0bc920fb0722", "977837f0e37f14998082b0723b06bd", "7f07e7f0e37f149b0723b0787b0721", "7f0e27f0e47f531b0723b0b6fb0722", "7f0e397bd07f595b0b0bc920fb0722", "977837f0e37f14898082b0723b02d5", "7ec967f0e37f14998082b0787b0721", "7f07e7f0e47f531b0723b0b6fb0722", "7f0e37f1487f595b0b0bb0b6fb0722", "7f0e37f0e37f14898082b0723b02d5", "7ec967f0e37f14998082b0787b0721", "7f07e7f0e47f531b0723b0b6fb0722", "7f0e37f1487f531b0b0bb0b6fb0722", "7f0e37f0e37f14898082b0723b02d5", "7ec967f0e37f14998082b0787b06bd", "7f07e7f0e47f531b0723b0b6fb0721", "7f0e37f1487f531b0b0bb0b6fb0722", "7f0e37f0e37f14898082b072297c35", "7ec967f0e37f14998082b0787b06bd", "7f07e7f0e47f531b0723b0b6fb0721", "7f0e27f1487f531b0b0bb0b6fb0722", "7f0e37f0e37f14898082b072297c35", "7ec967f0e37f14998082b0787b06bd", "7f07e7f0e47f531b0723b0b6fb0721", "7f0e27f1487f531b0b0bb0b6fb0722", "7f0e37f0e366aa89801eb072297c35", "7ec967f0e37f14998082b0787b06bd", "7f07e7f0e47f149b0723b0787b0721", "7f0e27f1487f531b0b0bb0b6fb0722", "7f0e37f0e366aa89801eb072297c35", "7ec967f0e37f14998082b0723b06bd", "7f07e7f0e47f149b0723b0787b0721", "7f0e27f0e47f531b0723b0b6fb0722", "7f0e37f0e366aa89801eb072297c35", "7ec967f0e37f14998082b0723b06bd", "7f07e7f0e37f14998083b0787b0721", "7f0e27f0e47f531b0723b0b6fb0722", "7f0e37f0e366aa89801eb072297c35", "7ec967f0e37f14898082b0723b02d5", "7f07e7f0e37f14998082b0787b0721", "7f07e7f0e47f531b0723b0b6fb0722", "7f0e36665b66aa89801e9808297c35", "665f67f0e37f14898082b0723b02d5", "7ec967f0e37f14998082b0787b0721", "7f07e7f0e47f531b0723b0b6fb0722", "7f0e36665b66a449801e9808297c35", "665f67f0e37f14898082b0723b02d5", "7ec967f0e37f14998082b0787b06bd", "7f07e7f0e47f531b0723b0b6fb0721", "7f0e36665b66a449801e9808297c35", "665f67f0e37f14898082b072297c35", "7ec967f0e37f14998082b0787b06bd", "7f07e7f0e47f531b0723b0b6fb0721", "7f0e26665b66a449801e9808297c35", "665f67f0e37f1489801eb072297c35", "7ec967f0e37f14998082b0787b06bd", "7f07e7f0e47f531b0723b0b6fb0721", "7f0e27f1487f531b0b0bb0b6fb0722"], nStr1: ["日", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"], nStr2: ["初", "十", "廿", "卅"], nStr3: ["正", "二", "三", "四", "五", "六", "七", "八", "九", "十", "冬", "腊"], getFestival: function() {
  return this.festival;
}, getLunarFestival: function() {
  return this.lFestival;
}, setFestival: function() {
  var e2 = arguments.length > 0 && void 0 !== arguments[0] ? arguments[0] : {};
  this.festival = e2;
}, setLunarFestival: function() {
  var e2 = arguments.length > 0 && void 0 !== arguments[0] ? arguments[0] : {};
  this.lFestival = e2;
}, lYearDays: function(e2) {
  var t2, b2 = 348;
  for (t2 = 32768; t2 > 8; t2 >>= 1)
    b2 += this.lunarInfo[e2 - 1900] & t2 ? 1 : 0;
  return b2 + this.leapDays(e2);
}, leapMonth: function(e2) {
  return 15 & this.lunarInfo[e2 - 1900];
}, leapDays: function(e2) {
  return this.leapMonth(e2) ? 65536 & this.lunarInfo[e2 - 1900] ? 30 : 29 : 0;
}, monthDays: function(e2, t2) {
  return t2 > 12 || t2 < 1 ? -1 : this.lunarInfo[e2 - 1900] & 65536 >> t2 ? 30 : 29;
}, solarDays: function(e2, t2) {
  if (t2 > 12 || t2 < 1)
    return -1;
  var b2 = t2 - 1;
  return 1 === b2 ? e2 % 4 == 0 && e2 % 100 != 0 || e2 % 400 == 0 ? 29 : 28 : this.solarMonth[b2];
}, toGanZhiYear: function(e2) {
  var t2 = (e2 - 3) % 10, b2 = (e2 - 3) % 12;
  return 0 === t2 && (t2 = 10), 0 === b2 && (b2 = 12), this.Gan[t2 - 1] + this.Zhi[b2 - 1];
}, toAstro: function(e2, t2) {
  return "摩羯水瓶双鱼白羊金牛双子巨蟹狮子处女天秤天蝎射手摩羯".substr(2 * e2 - (t2 < [20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22][e2 - 1] ? 2 : 0), 2) + "座";
}, toGanZhi: function(e2) {
  return this.Gan[e2 % 10] + this.Zhi[e2 % 12];
}, getTerm: function(e2, t2) {
  if (e2 < 1900 || e2 > 2100 || t2 < 1 || t2 > 24)
    return -1;
  for (var b2 = this.sTermInfo[e2 - 1900], f2 = [], n2 = 0; n2 < b2.length; n2 += 5) {
    var c2 = parseInt("0x" + b2.substr(n2, 5)).toString();
    f2.push(c2[0], c2.substr(1, 2), c2[3], c2.substr(4, 2));
  }
  return parseInt(f2[t2 - 1]);
}, toChinaMonth: function(e2) {
  if (e2 > 12 || e2 < 1)
    return -1;
  var t2 = this.nStr3[e2 - 1];
  return t2 += "月";
}, toChinaDay: function(e2) {
  var t2;
  switch (e2) {
    case 10:
      t2 = "初十";
      break;
    case 20:
      t2 = "二十";
      break;
    case 30:
      t2 = "三十";
      break;
    default:
      t2 = this.nStr2[Math.floor(e2 / 10)], t2 += this.nStr1[e2 % 10];
  }
  return t2;
}, getAnimal: function(e2) {
  return this.Animals[(e2 - 4) % 12];
}, solar2lunar: function(e2, t2, b2) {
  var f2, n2 = parseInt(e2), c2 = parseInt(t2), a2 = parseInt(b2);
  if (n2 < 1900 || n2 > 2100)
    return -1;
  if (1900 === n2 && 1 === c2 && a2 < 31)
    return -1;
  var r2, i2, s2 = 0;
  n2 = (f2 = n2 ? new Date(n2, parseInt(c2) - 1, a2) : /* @__PURE__ */ new Date()).getFullYear(), c2 = f2.getMonth() + 1, a2 = f2.getDate();
  var u2 = (Date.UTC(f2.getFullYear(), f2.getMonth(), f2.getDate()) - Date.UTC(1900, 0, 31)) / 864e5;
  for (r2 = 1900; r2 < 2101 && u2 > 0; r2++)
    u2 -= s2 = this.lYearDays(r2);
  u2 < 0 && (u2 += s2, r2--);
  var o2 = /* @__PURE__ */ new Date(), d2 = false;
  o2.getFullYear() === n2 && o2.getMonth() + 1 === c2 && o2.getDate() === a2 && (d2 = true);
  var h2 = f2.getDay(), l = this.nStr1[h2];
  0 === h2 && (h2 = 7);
  var y = r2;
  i2 = this.leapMonth(r2);
  var D = false;
  for (r2 = 1; r2 < 13 && u2 > 0; r2++)
    i2 > 0 && r2 === i2 + 1 && false === D ? (--r2, D = true, s2 = this.leapDays(y)) : s2 = this.monthDays(y, r2), true === D && r2 === i2 + 1 && (D = false), u2 -= s2;
  0 === u2 && i2 > 0 && r2 === i2 + 1 && (D ? D = false : (D = true, --r2)), u2 < 0 && (u2 += s2, --r2);
  var g = r2, v = u2 + 1, M = c2 - 1, m = this.toGanZhiYear(y), $ = this.getTerm(n2, 2 * c2 - 1), p = this.getTerm(n2, 2 * c2), w = this.toGanZhi(12 * (n2 - 1900) + c2 + 11);
  a2 >= $ && (w = this.toGanZhi(12 * (n2 - 1900) + c2 + 12));
  var Y = false, _ = null;
  $ === a2 && (Y = true, _ = this.solarTerm[2 * c2 - 2]), p === a2 && (Y = true, _ = this.solarTerm[2 * c2 - 1]);
  var S = Date.UTC(n2, M, 1, 0, 0, 0, 0) / 864e5 + 25567 + 10, k = this.toGanZhi(S + a2 - 1), T = this.toAstro(c2, a2), C = n2 + "-" + c2 + "-" + a2, I = y + "-" + g + "-" + v, O = this.festival, x = this.lFestival, F = c2 + "-" + a2, A = g + "-" + v;
  return 12 === g && 29 === v && 29 === this.monthDays(y, g) && (A = "12-30"), { date: C, lunarDate: I, festival: O[F] ? O[F].title : null, lunarFestival: x[A] ? x[A].title : null, lYear: y, lMonth: g, lDay: v, Animal: this.getAnimal(y), IMonthCn: (D ? "闰" : "") + this.toChinaMonth(g), IDayCn: this.toChinaDay(v), cYear: n2, cMonth: c2, cDay: a2, gzYear: m, gzMonth: w, gzDay: k, isToday: d2, isLeap: D, nWeek: h2, ncWeek: "星期" + l, isTerm: Y, Term: _, astro: T };
}, lunar2solar: function(e2, t2, b2, f2) {
  e2 = parseInt(e2), t2 = parseInt(t2), b2 = parseInt(b2), f2 = !!f2;
  var n2 = this.leapMonth(e2);
  if (this.leapDays(e2), f2 && n2 !== t2)
    return -1;
  if (2100 === e2 && 12 === t2 && b2 > 1 || 1900 === e2 && 1 === t2 && b2 < 31)
    return -1;
  var c2 = this.monthDays(e2, t2), a2 = c2;
  if (f2 && (a2 = this.leapDays(e2, t2)), e2 < 1900 || e2 > 2100 || b2 > a2)
    return -1;
  var r2, i2 = 0;
  for (r2 = 1900; r2 < e2; r2++)
    i2 += this.lYearDays(r2);
  var s2 = 0, u2 = false;
  for (r2 = 1; r2 < t2; r2++)
    s2 = this.leapMonth(e2), u2 || s2 <= r2 && s2 > 0 && (i2 += this.leapDays(e2), u2 = true), i2 += this.monthDays(e2, r2);
  f2 && (i2 += c2);
  var o2 = Date.UTC(1900, 1, 30, 0, 0, 0), d2 = new Date(864e5 * (i2 + b2 - 31) + o2), h2 = d2.getUTCFullYear(), l = d2.getUTCMonth() + 1, y = d2.getUTCDate();
  return this.solar2lunar(h2, l, y);
} }, n = { isWorkday: r, isHoliday: function(e2) {
  return !r(e2);
}, getFestival: function(e2) {
  var t2 = i(e2);
  if (a[t2.date])
    return a[t2.date];
  if (c[t2.date])
    return c[t2.date];
  return t2.weekends ? "周末" : "工作日";
}, isAddtionalWorkday: function(e2) {
  var t2 = i(e2);
  return !!a[t2.date];
} }, c = { "2018-02-15": "春节", "2018-02-16": "春节", "2018-02-17": "春节", "2018-02-18": "春节", "2018-02-19": "春节", "2018-02-20": "春节", "2018-02-21": "春节", "2018-04-05": "清明节", "2018-04-06": "清明节", "2018-04-07": "清明节", "2018-04-29": "劳动节", "2018-04-30": "劳动节", "2018-05-01": "劳动节", "2018-06-18": "端午节", "2018-09-24": "中秋节", "2018-10-01": "国庆节", "2018-10-02": "国庆节", "2018-10-03": "国庆节", "2018-10-04": "国庆节", "2018-10-05": "国庆节", "2018-10-06": "国庆节", "2018-10-07": "国庆节", "2018-12-30": "元旦", "2018-12-31": "元旦", "2019-01-01": "元旦", "2019-02-04": "春节", "2019-02-05": "春节", "2019-02-06": "春节", "2019-02-07": "春节", "2019-02-08": "春节", "2019-02-09": "春节", "2019-02-10": "春节", "2019-04-05": "清明节", "2019-04-06": "清明节", "2019-04-07": "清明节", "2019-05-01": "劳动节", "2019-05-02": "劳动节", "2019-05-03": "劳动节", "2019-05-04": "劳动节", "2019-06-07": "端午节", "2019-06-08": "端午节", "2019-06-09": "端午节", "2019-09-13": "中秋节", "2019-09-14": "中秋节", "2019-09-15": "中秋节", "2019-10-01": "国庆节", "2019-10-02": "国庆节", "2019-10-03": "国庆节", "2019-10-04": "国庆节", "2019-10-05": "国庆节", "2019-10-06": "国庆节", "2019-10-07": "国庆节", "2020-01-01": "元旦", "2020-01-24": "春节", "2020-01-25": "春节", "2020-01-26": "春节", "2020-01-27": "春节", "2020-01-28": "春节", "2020-01-29": "春节", "2020-01-30": "春节", "2020-04-04": "清明节", "2020-04-05": "清明节", "2020-04-06": "清明节", "2020-05-01": "劳动节", "2020-05-02": "劳动节", "2020-05-03": "劳动节", "2020-05-04": "劳动节", "2020-05-05": "劳动节", "2020-06-25": "端午节", "2020-06-26": "端午节", "2020-06-27": "端午节", "2020-10-01": "国庆节", "2020-10-02": "国庆节", "2020-10-03": "国庆节", "2020-10-04": "国庆节", "2020-10-05": "国庆节", "2020-10-06": "国庆节", "2020-10-07": "国庆节", "2020-10-08": "国庆节", "2021-01-01": "元旦", "2021-02-11": "春节", "2021-02-12": "春节", "2021-02-13": "春节", "2021-02-14": "春节", "2021-02-15": "春节", "2021-02-16": "春节", "2021-02-17": "春节", "2021-04-03": "清明节", "2021-04-04": "清明节", "2021-04-05": "清明节", "2021-05-01": "劳动节", "2021-05-02": "劳动节", "2021-05-03": "劳动节", "2021-05-04": "劳动节", "2021-05-05": "劳动节", "2021-06-12": "端午节", "2021-06-13": "端午节", "2021-06-14": "端午节", "2021-09-19": "中秋节", "2021-09-20": "中秋节", "2021-09-21": "中秋节", "2021-10-01": "国庆节", "2021-10-02": "国庆节", "2021-10-03": "国庆节", "2021-10-04": "国庆节", "2021-10-05": "国庆节", "2021-10-06": "国庆节", "2021-10-07": "国庆节", "2022-01-01": "元旦", "2022-01-02": "元旦", "2022-01-03": "元旦", "2022-01-31": "春节", "2022-02-01": "春节", "2022-02-02": "春节", "2022-02-03": "春节", "2022-02-04": "春节", "2022-02-05": "春节", "2022-02-06": "春节", "2022-04-03": "清明节", "2022-04-04": "清明节", "2022-04-05": "清明节", "2022-04-30": "劳动节", "2022-05-01": "劳动节", "2022-05-02": "劳动节", "2022-05-03": "劳动节", "2022-05-04": "劳动节", "2022-06-03": "端午节", "2022-06-04": "端午节", "2022-06-05": "端午节", "2022-09-10": "中秋节", "2022-09-11": "中秋节", "2022-09-12": "中秋节", "2022-10-01": "国庆节", "2022-10-02": "国庆节", "2022-10-03": "国庆节", "2022-10-04": "国庆节", "2022-10-05": "国庆节", "2022-10-06": "国庆节", "2022-10-07": "国庆节", "2022-12-31": "元旦", "2023-01-01": "元旦", "2023-01-02": "元旦", "2023-01-21": "春节", "2023-01-22": "春节", "2023-01-23": "春节", "2023-01-24": "春节", "2023-01-25": "春节", "2023-01-26": "春节", "2023-01-27": "春节", "2023-04-05": "清明节", "2023-04-29": "劳动节", "2023-04-30": "劳动节", "2023-05-01": "劳动节", "2023-05-02": "劳动节", "2023-05-03": "劳动节", "2023-06-22": "端午节", "2023-06-23": "端午节", "2023-06-24": "端午节", "2023-09-29": "中秋节", "2023-09-30": "中秋节", "2023-10-01": "国庆节", "2023-10-02": "国庆节", "2023-10-03": "国庆节", "2023-10-04": "国庆节", "2023-10-05": "国庆节", "2023-10-06": "国庆节", "2024-01-01": "元旦", "2024-02-10": "春节", "2024-02-11": "春节", "2024-02-12": "春节", "2024-02-13": "春节", "2024-02-14": "春节", "2024-02-15": "春节", "2024-02-16": "春节", "2024-02-17": "春节", "2024-04-04": "清明节", "2024-04-05": "清明节", "2024-04-06": "清明节", "2024-05-01": "劳动节", "2024-05-02": "劳动节", "2024-05-03": "劳动节", "2024-05-04": "劳动节", "2024-05-05": "劳动节", "2024-06-10": "端午节", "2024-09-15": "中秋节", "2024-09-16": "中秋节", "2024-09-17": "中秋节", "2024-10-01": "国庆节", "2024-10-02": "国庆节", "2024-10-03": "国庆节", "2024-10-04": "国庆节", "2024-10-05": "国庆节", "2024-10-06": "国庆节", "2024-10-07": "国庆节" }, a = { "2018-02-11": "补春节", "2018-02-24": "补春节", "2018-04-08": "补清明节", "2018-04-28": "补劳动节", "2018-09-29": "补国庆节", "2018-09-30": "补国庆节", "2018-12-29": "补元旦", "2019-02-02": "补春节", "2019-02-03": "补春节", "2019-04-28": "补劳动节", "2019-05-05": "补劳动节", "2019-09-29": "补国庆节", "2019-10-12": "补国庆节", "2020-01-19": "补春节", "2020-02-01": "补春节", "2020-04-26": "补劳动节", "2020-05-09": "补劳动节", "2020-06-28": "补端午节", "2020-09-27": "补国庆节", "2020-10-10": "补国庆节", "2021-02-07": "补春节", "2021-02-20": "补春节", "2021-04-25": "补劳动节", "2021-05-08": "补劳动节", "2021-09-18": "补中秋节", "2021-09-26": "补国庆节", "2021-10-09": "补国庆节", "2022-01-29": "补春节", "2022-01-30": "补春节", "2022-04-02": "补清明节", "2022-04-24": "补劳动节", "2022-05-07": "补劳动节", "2022-10-08": "补国庆节", "2022-10-09": "补国庆节", "2023-01-28": "补春节", "2023-01-29": "补春节", "2023-04-23": "补劳动节", "2023-05-06": "补劳动节", "2023-06-25": "补端午节", "2023-10-07": "补国庆节", "2023-10-08": "补国庆节", "2024-02-04": "补春节", "2024-02-18": "补春节", "2024-04-07": "补清明节", "2024-04-28": "补劳动节", "2024-05-11": "补劳动节", "2024-09-14": "补中秋节", "2024-09-29": "补国庆节", "2024-10-12": "补国庆节" };
function r(e2) {
  var t2 = i(e2);
  return !!a[t2.date] || !c[t2.date] && !t2.weekends;
}
function i(e2) {
  var t2 = new Date(e2), b2 = "" + (t2.getMonth() + 1), f2 = (e2 = "" + t2.getDate(), t2.getFullYear());
  return b2.length < 2 && (b2 = "0" + b2), e2.length < 2 && (e2 = "0" + e2), { date: [f2, b2, e2].join("-"), weekends: [0, 6].indexOf(t2.getDay()) > -1 };
}
const s = e(n);
var u = ((e2) => (e2[e2.pre = 0] = "pre", e2[e2.current = 1] = "current", e2[e2.next = 2] = "next", e2))(u || {});
const o = ["周日", "周一", "周二", "周三", "周四", "周五", "周六"];
class d {
  constructor(e2) {
    __publicField(this, "date");
    __publicField(this, "_weekText", o);
    this.date = b(e2);
  }
  get monthText() {
    return this.date.format("YYYY-MM");
  }
  subtractMonth() {
    return this.date = this.date.subtract(1, "month"), this._getCalendar();
  }
  subtractDay(e2) {
    return this._getDayDetail(b().subtract(e2, "day"), 1);
  }
  addMonth() {
    return this.date = this.date.add(1, "month"), this._getCalendar();
  }
  month() {
    return this._getCalendar();
  }
  getCurrentDetail() {
    return this._getDayDetail(b(), 1);
  }
  _getDayDetail(e2, t2) {
    const { IDayCn: b2, IMonthCn: f2, astro: n2, Animal: c2, gzDay: a2, gzMonth: r2, gzYear: i2, isHoliday: s2, festival: u2 } = h._formatDay(e2);
    return { week: e2.day(), weekName: this._weekText[e2.day()], date: e2.format("YYYY-MM-DD"), year: e2.get("year"), month: e2.get("month") + 1, day: e2.get("date"), type: t2, IDayCn: b2, IMonthCn: f2, astro: n2, Animal: c2, gzDay: a2, gzMonth: r2, gzYear: i2, isHoliday: s2, festival: u2 };
  }
  _getPreMonthDays(e2, t2) {
    const b2 = [];
    for (let f2 = t2 - 2; f2 >= 0; f2--)
      b2.push(this._getDayDetail(e2.endOf("month").subtract(f2, "day"), 0));
    return b2;
  }
  _getMonthDays(e2) {
    const t2 = [], b2 = e2.daysInMonth();
    for (let f2 = 0; f2 < b2; f2++)
      t2.push(this._getDayDetail(e2.startOf("month").add(f2, "day"), 1));
    return t2;
  }
  _getNextMonthDays(e2, t2) {
    const b2 = [];
    for (let f2 = 0; f2 < 7 - t2; f2++)
      b2.push(this._getDayDetail(e2.startOf("month").add(f2, "day"), 2));
    return b2;
  }
  _getCalendar() {
    const e2 = this.date, t2 = e2.subtract(1, "month"), b2 = e2.add(1, "month"), f2 = e2.startOf("month"), n2 = e2.endOf("month"), c2 = f2.day(), a2 = n2.day();
    return [...this._getPreMonthDays(t2, 0 === c2 ? 7 : c2), ...this._getMonthDays(e2), ...this._getNextMonthDays(b2, 0 === a2 ? 7 : a2)];
  }
}
class h {
  static decreaseWeek(e2) {
    return b(e2).subtract(1, "week").valueOf();
  }
  static addWeek(e2) {
    return b(e2).add(1, "week").valueOf();
  }
  static getWeekList(e2) {
    const t2 = b(e2);
    let f2 = t2.day();
    0 === f2 && (f2 = 7);
    const n2 = [1, f2], c2 = [f2 + 1, 7], a2 = [];
    for (let b2 = n2[0]; b2 <= n2[1]; b2++)
      a2.push(h._formatDay(t2.subtract(f2 - b2, "day")));
    for (let b2 = c2[0]; b2 <= c2[1]; b2++)
      a2.push(h._formatDay(t2.add(b2 - f2, "day")));
    return a2;
  }
  static _formatDay(e2) {
    const t2 = e2.day(), { IDayCn: b2, IMonthCn: n2, astro: c2, Animal: a2, gzDay: r2, gzMonth: i2, gzYear: u2 } = f.solar2lunar(e2.year(), e2.month() + 1, e2.date()), d2 = s.isHoliday(e2.format("YYYY-MM-DD")), h2 = s.getFestival(e2.format("YYYY-MM-DD"));
    return { date: e2.format("YYYY-MM-DD"), week: 0 === t2 ? 7 : t2, weekZh: o[t2], IDayCn: b2, IMonthCn: n2, astro: c2, Animal: a2, gzDay: r2, gzMonth: i2, gzYear: u2, isHoliday: d2, festival: h2 };
  }
}
export {
  d as Calendar,
  h as CalendarUtils,
  u as DayType
};
