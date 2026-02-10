<template>
  <div :class="className" :style="{ height: height, width: width }" />
</template>

<script>
import * as echarts from "echarts";
// require('echarts/theme/macarons') // echarts theme
// import resize from './mixins/resize'
// import "echarts-gl";

const animationDuration = 6000
let colors = ['rgba(42, 101, 186, 1)', 'rgba(24, 169, 145, 1)', 'rgba(127, 163, 191, 1)', 'rgba(148, 155, 164, 1)', '#16d0ff', '#664bff', '#37a2da', '#ffdb5c', '#ff9f7f', '#9fe6b8', '#67e0e3', '#32c5e9', '#fb7293', "#9A60B4", "#ea7ccc"]
function getParametricEquation(
  startRatio,
  endRatio,
  isSelected,
  isHovered,
  k,
  height,
  i
) {
  // 计算
  let midRatio = (startRatio + endRatio) / 2;

  let startRadian = startRatio * Math.PI * 2;
  let endRadian = endRatio * Math.PI * 2;
  let midRadian = midRatio * Math.PI * 2;

  // 如果只有一个扇形，则不实现选中效果。
  if (startRatio === 0 && endRatio === 1) {
    isSelected = false;
  }

  // 通过扇形内径/外径的值，换算出辅助参数 k（默认值 1/3）
  k = typeof k !== "undefined" ? k : 1 / 3;

  // 计算选中效果分别在 x 轴、y 轴方向上的位移（未选中，则位移均为 0）
  let offsetX = isSelected ? Math.cos(midRadian) * 0.1 : 0;
  let offsetY = isSelected ? Math.sin(midRadian) * 0.1 : 0;
  let offsetZ = i == 1 ? 2 : 0;
  // 计算高亮效果的放大比例（未高亮，则比例为 1）
  let hoverRate = isHovered ? 1.05 : 1;

  // 返回曲面参数方程
  return {
    u: {
      min: -Math.PI,
      max: Math.PI * 3,
      step: Math.PI / 32,
    },

    v: {
      min: 0,
      max: Math.PI * 2,
      step: Math.PI / 20,
    },

    x: function (u, v) {
      if (u < startRadian) {
        return (
          offsetX + Math.cos(startRadian) * (1 + Math.cos(v) * k) * hoverRate
        );
      }
      if (u > endRadian) {
        return (
          offsetX + Math.cos(endRadian) * (1 + Math.cos(v) * k) * hoverRate
        );
      }
      return offsetX + Math.cos(u) * (1 + Math.cos(v) * k) * hoverRate;
    },

    y: function (u, v) {
      if (u < startRadian) {
        return (
          offsetY + Math.sin(startRadian) * (1 + Math.cos(v) * k) * hoverRate
        );
      }
      if (u > endRadian) {
        return (
          offsetY + Math.sin(endRadian) * (1 + Math.cos(v) * k) * hoverRate
        );
      }
      return offsetY + Math.sin(u) * (1 + Math.cos(v) * k) * hoverRate;
    },

    z: function (u, v) {
      if (u < -Math.PI * 0.5) {
        return Math.sin(u);
      }
      if (u > Math.PI * 2.5) {
        return Math.sin(u);
      }
      return Math.sin(v) > 0 ? 1 * height : -1;
    },
  };
}

// 生成模拟 3D 饼图的配置项
function getPie3D(pieData, internalDiameterRatio) {
  let series = [];
  let sumValue = 0;
  let startValue = 0;
  let endValue = 0;
  let legendData = [];
  let k =
    typeof internalDiameterRatio !== "undefined"
      ? (1 - internalDiameterRatio) / (1 + internalDiameterRatio)
      : 1 / 3;

  // 为每一个饼图数据，生成一个 series-surface 配置
  for (let i = 0; i < pieData.length; i++) {
    sumValue += pieData[i].value;

    let seriesItem = {
      name:
        typeof pieData[i].name === "undefined"
          ? `series${i}`
          : pieData[i].name,
      type: "surface",
      parametric: true,
      wireframe: {
        show: false,
      },
      pieData: pieData[i],
      itemStyle: {
        color: colors[i], // 自定义颜色
        opacity: '1'
      },
      pieStatus: {
        selected: false,
        hovered: false,
        k: k,
      },
    };

    if (typeof pieData[i].itemStyle != "undefined") {
      let itemStyle = {};

      typeof pieData[i].itemStyle.color != "undefined"
        ? (itemStyle.color = pieData[i].itemStyle.color)
        : null;
      typeof pieData[i].itemStyle.opacity != "undefined"
        ? (itemStyle.opacity = pieData[i].itemStyle.opacity)
        : null;

      seriesItem.itemStyle = itemStyle;
    }
    series.push(seriesItem);
  }

  // 使用上一次遍历时，计算出的数据和 sumValue，调用 getParametricEquation 函数，
  // 向每个 series-surface 传入不同的参数方程 series-surface.parametricEquation，也就是实现每一个扇形。
  for (let i = 0; i < series.length; i++) {
    endValue = startValue + series[i].pieData.value;
    series[i].pieData.startRatio = startValue / sumValue;
    series[i].pieData.endRatio = endValue / sumValue;
    series[i].parametricEquation = getParametricEquation(
      series[i].pieData.startRatio,
      series[i].pieData.endRatio,
      false,
      false,
      k,
      // 调整扇形高度
      i === 0 ? 10 : 10,
      i,
      series[i].pieData.value
    );

    startValue = endValue;

    legendData.push(series[i].name);
  }


  // // 补充一个透明的圆环，用于支撑高亮功能的近似实现。
  series.push({
    name: "pie2d",
    type: "pie",
    label: {
      opacity: 1,
      fontSize: 14,
      lineHeight: 20,
      textStyle: {
        fontSize: 14,
        color: "#fff",
      },
    },
    labelLine: {
      length: 10,
      length2: 10,
    },
    startAngle: 2, //起始角度，支持范围[0, 360]。
    clockwise: false, //饼图的扇区是否是顺时针排布。上述这两项配置主要是为了对齐3d的样式
    radius: ["50%", "60%"],
    center: ["62%", "50%"],
    data: pieData,
    itemStyle: {
      opacity: 0,
    },
    labelLine: {
      show: false
    },
    label: {
      show: false,
      position: 'center'
    },
  });

  // 准备待返回的配置项，把准备好的 legendData、series 传入。

  let option = {
    legend: {
      show: true,
      tooltip: {
        show: true,
      },
      orient: "vertical",
      data: legendData,
      top: "center",
      itemGap: 10,
      itemHeight: 14,
      itemWidth: 14,
      // itemRadius: 20,
      top: "center",
      right: "2%",
      textStyle: {
        color: "#fff",
        fontSize: 12,
        rich: {
          name: {
            // width: '45%',
            fontSize: 14,
            color: "#fff",
          },
          value: {
            fontSize: 14,
            padding: [0, 5, 0, 5],
            color: "#00C6FF",
          },
        },
      },
      formatter: function (name) {
        let res = pieData.filter((v) => v.name === name);
        res = res[0] || {};
        const p = res.value;
        return "{name|" + name + "}";
      },
    },
    animation: false,
    tooltip: {
      show: true,
      formatter: (params) => {
            if (
              params.seriesName !== "mouseoutSeries" &&
              params.seriesName !== "pie2d"
            ) {
              return `${
                params.seriesName
              }<br/><span style="display:inline-block;margin-right:5px;border-radius:10px;width:10px;height:10px;background-color:${
                params.color
              };"></span>${
                option.series[params.seriesIndex].pieData.value
              }`;
            }
          },
    },
    title: {
      x: "center",
      top: "20",
      textStyle: {
        color: "#fff",
        fontSize: 14,
      },
    },
    labelLine: {
      show: true,
      lineStyle: {
        color: "#7BC0CB",
      },
      normal: {
        show: false,
        length: 10,
        length2: 10,
      },
    },
    label: {
      show: true,
      position: "outside",
      // formatter: "{b} {d}%",
      formatter: function (pieData) {
        return (
          "{name|" +
          pieData.name +
          "}" +
          " {value|" +
          pieData.percent.toFixed(0) +
          "%}"
        );
      },
      rich: {
        name: {
          fontSize: 14,
          color: "#ffffff",
        },
        value: {
          fontSize: 14,
          color: "#ffffff",
        },
      },
      textStyle: {
        color: "#fff",
        fontSize: "14px",
      },
    },
    xAxis3D: {
      min: -1,
      max: 1,
    },
    yAxis3D: {
      min: -1,
      max: 1,
    },
    zAxis3D: {
      min: -1,
      max: 1,
    },
    grid3D: {
      show: false,
      boxHeight: 3,
      top: '-10%',
      left: "-10%",
      // bottom: "60%",
      // environment: "rgba(255,255,255,0)",
      viewControl: {
        distance: 160,
        alpha: 20,
        beta: 2,
        autoRotate: false, // 自动旋转
      },
    },
    series: series,
  };

  // let option = {
  //     //animation: false,
  //     tooltip: {
  //         show: true,
  //         formatter: params => {
  //         if (params.seriesName !== 'mouseoutSeries' && params.seriesName !== 'pie2d') {
  //            let bfb = ((option.series[params.seriesIndex].pieData.endRatio - option.series[params.seriesIndex].pieData.startRatio) *
  //               100).toFixed(2);
  //            return `${params.seriesName}<br/>` +
  //               `<span style="display:inline-block;margin-right:5px;border-radius:10px;width:10px;height:10px;background-color:${params.color};"></span>` +
  //               `${bfb}`;
  //         }
  //      }
  //     },
  //     legend: {
  //         type: 'scroll',
  //         orient: 'vertical',
  //         right: 10,
  //         //left: '50%',
  //         top: 'center',
  //         textStyle: {
  //             fontSize: 14,
  //             color: '#fff',
  //         },
  //         // icon:'diamond',
  //         data: legendData,
  //         formatter: (params) => {
  //             return params;
  //         },
  //     },
  //     xAxis3D: {},
  //     yAxis3D: {},
  //     zAxis3D: {},
  //     grid3D: {
  //         viewControl: {
  //             autoRotate: true,
  //             distance: 116,
  //         },
  //         top: '-8%',
  //         left: '-2%',
  //         bottom: '0',
  //         width: '70%',
  //         show: false,
  //         boxHeight: 54,
  //     },
  //     series: series,
  // };
  return option;
}

export default {
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '300px'
    },
    valdata: {
      type: Object,
      default: () => {
        return {
          dataNum: [
          {
            name: "第一科室",
            value: 68,
          },
          {
            name: "第二科室",
            value: 52,
          },
          {
            name: "第三科室",
            value: 37,
          },
          {
            name: "第四科室",
            value: 58,
          },
        ],
        }
      }
    }
  },
  data() {
    return {
      chart: null
    }
  },
  watch: {
    valdata(val) {
      this.initChart()
    },
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart()
    })
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    initChart() {

      this.chart = echarts.init(this.$el)

      // this.valdata.dataNum.forEach((item, index) => {
      //   iconData.push({
      //     value: item,
      //     symbol: "image://" + bottom1,
      //   })
      // })
      // var option = getPie3D(
      //   [
      //     {
      //       name: "第一科室",
      //       value: 68,
      //     },
      //     {
      //       name: "第二科室",
      //       value: 52,
      //     },
      //     {
      //       name: "第三科室",
      //       value: 37,
      //     },
      //     {
      //       name: "第四科室",
      //       value: 58,
      //     },
      //   ],
      //   0.7
      // );
      var option = getPie3D(this.valdata.dataNum, 0.7);


      this.chart.setOption(option);




    }

  }
}
</script>
