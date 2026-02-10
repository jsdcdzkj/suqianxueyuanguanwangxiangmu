<template>
  <div :class="className" :style="{ height: height, width: width }" />
</template>

<script>
import * as echarts from "echarts";
import 'echarts-liquidfill';
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
      type: Object,
      default: () => {
        return {
          resData: [],
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
      this.chart = echarts.init(this.$el);
      const resData = this.valdata.resData;
      this.chart.setOption({
      color: ['rgba(92,144,247,.85)', 'rgba(158,252,184,.85)', 'rgba(125,214,245,.85)', 'rgba(87,189,148,.85)', 'rgba(132,76,219,.85)'],
      tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
          orient: 'vertical',
          top: 'middle',
          right: 5,
          icon: '',
          type: 'scroll',
          itemWidth: 14,
          itemHeight: 14,
          textStyle: {
            fontSize: 12,
            color: '#ffffff',
            borderRadius: '4px',
          },
      },
      series: [{
          name: '告警类型',
          type: 'pie',
          radius: ['60%', '80%'],
          center: ['35%', '50%'],
          z: 12,
          avoidLabelOverlap: false,
          label: {
            show: false
          },
          labelLine: {
            show: false
          },
          data: resData
      },
      {
          name: '告警类型',
          type: 'pie',
          radius: ['50%', '75%'],
          center: ['35%', '50%'],
          avoidLabelOverlap: false,
          itemStyle: {
            opacity: 0.2
          },
          label: {
            show: false
          },
          labelLine: {
            show: false
          },
          data: resData
      }]
      });
    },
  },
};
</script>
