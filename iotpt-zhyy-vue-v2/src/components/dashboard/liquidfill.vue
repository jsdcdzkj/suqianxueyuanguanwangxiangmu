<template>
  <div :class="className" :style="{ height: height, width: width }" />
</template>

<script>
// import echarts from "echarts";
// import 'echarts-liquidfill';
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
    size: {
      type: Number,
      default: 20,
    },
    valdata: {
      type: Object,
      default: () => {
        return {
          liquildData: 68,
          color:['#ecf3fe', '#c8dcfe'],
          color2:'#ecf3fe'
        };
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
      this.chart = this.$echarts.init(this.$el);
      let value = this.valdata.liquildData;
      this.chart.setOption({
        series: [
        {
          type: 'liquidFill',
          radius: '78.1%',
          center: ['50%', '50%'],
          color: this.valdata.color,
          data: [value, value], // data个数代表波浪数
          amplitude: 5,
          // 图形样式
          itemStyle: {
            opacity: 1, // 波浪的透明度
            shadowBlur: 0, // 波浪的阴影范围
          },
          backgroundStyle: {
            borderWidth: 1,
            color: this.valdata.color2,
          },
          label: {
            show: true,
            textStyle: {
              color: '#ffffff',
              // 文字描边
              fontSize: this.size,
              fontWeight:700,
              // // 添加文字描边
              textBorderColor: 'rgba(0,0,0,0.32)',
              // echarts 5.0.0版本以上的描边设置
              textBorderWidth: 1,
            },
            formatter: (params) => {
              return `${(params.value * 100).toFixed(2)}%`;
            },
          },
          outline: {
            show: false,
          },
        },
      ],
      });
    },
  },
};
</script>
