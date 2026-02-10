<template>
  <div :class="className" :style="{ height: height, width: width }" />
</template>

<script>
import * as echarts from "echarts";
import "echarts-liquidfill";
require("echarts/theme/macarons"); // echarts theme
// import resize from "../mixins/resize";

export default {
  // mixins: [resize],
  props: {
    className: {
      type: String,
      default: "chart",
    },
    width: {
      type: String,
      default: "100%",
    },
    height: {
      type: String,
      default: "100%",
    },
    valdata: {
      type: Array,
      default: () => {
        return [];
      },
    },
    colors: {
      type: Array,
      default: () => {
        return ["#ebbf46", "#0067df", "#2fcf97", "#73c0de", "#fc8452"];
      },
    },
  },
  data() {
    return {
      chart: null,
    };
  },
  watch: {
    valdata(val) {
      this.initChart();
    },
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart();
      window.addEventListener("resize", () => {
        this.chart.resize();
      });
    });
  },
  beforeDestroy() {
    if (!this.chart) {
      return;
    }
    this.chart.dispose();
    this.chart = null;
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$el);
      let value = this.valdata.liquildData;
      this.chart.setOption({
        // title: {
        //   show: true,
        //   text: this.valdata.warnTypeSum,
        //   x: "29%",
        //   y: "45%",
        //   textStyle: {
        //     color: "#333",
        //     fontSize: 34,
        //     fontWeight: "400",
        //   },
        //   textAlign: "center",
        // },
        tooltip: {
          trigger: "item",
        },
        legend: {
          type: "scroll",
          orient: "vertical",
          right: "6%",
          top: "center",
          selectedMode: true,
          icon: "pin",
          itemStyle: {
            borderWidth: 1,
            // borderWidth:1
          },
          textStyle: {
            rich: {
              a: {
                color: "#666666",
                fontSize: 15,
                fontWeight: 500,
                width: 60,
                padding: [25, 0, 15, 2],
              },
              b0: {
                color: "#666666",
                fontSize: 14,
                fontWeight: 500,
                padding: [-10, 0, 0, 4],
              },
              b1: {
                color: "#666666",
                fontSize: 14,
                fontWeight: 500,
                padding: [25, 0, 15, 4],
              },
            },
          },
          formatter: (name) => {
            //   var target;
            //   var percent;
            //   for (var i = 0, l = this.valdata.length; i < l; i++) {
            //     if (this.valdata[i].name == name) {
            //       target = this.valdata[i].value;
            //       percent =
            //         (this.valdata[i].value /
            //           this.valdata.warnTypeSum) *
            //         100;
            //     }
            //   }
            // const item = this.valdata.find((item) => item.name == name);
            return `${name}`;
          },
        },
        series: [
          {
            type: "pie",
            center: ["30%", "50%"],
            radius: ["30%", "80%"],
            clockwise: true,
            avoidLabelOverlap: true,
            hoverOffset: 2,
            itemStyle: {
              borderRadius: 6,
              borderColor: "#fff",
              borderWidth: 2,
            },
            tooltip: {
              trigger: "item",
              formatter: function (params) {
                return (
                  params.name +
                  "：" +
                  params.value +
                  "个<br>占比：" +
                  params.percent.toFixed(1) +
                  "%"
                );
              },
              backgroundColor: "#f3f3f3",
              textStyle: {
                color: "#666666",
                fontSize: 14,
                fontWeight: 500,
              },
              padding: [10, 20],
              extraCssText: "box-shadow: 0 0 3px rgba(0, 0, 0, 0.3);",
            },
            emphasis: {
              itemStyle: {
                borderColor: "#f3f3f3",
                borderWidth: 5,
              },
            },
            label: {
              show: false,
              position: "outside",
              formatter: "{a|{b}：{d}%}\n{hr|}",
              rich: {
                hr: {
                  backgroundColor: "t",
                  borderRadius: 1,
                  width: 1,
                  height: 1,
                  padding: [1, 1, 0, -4],
                },
                a: {
                  padding: [-15, 7, -10, 7],
                },
              },
            },
            labelLine: {
              normal: {
                length: 10,
                length2: 15,
                lineStyle: {
                  width: 1,
                },
              },
            },
            data: this.valdata,
          },
        ],
      });
    },
  },
};
</script>
