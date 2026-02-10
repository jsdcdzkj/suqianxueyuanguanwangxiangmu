<template>
  <div>
      <Title :index="index" :title="title" :subTitle="subTitle" :subIndex="subIndex" />
      <div :class="className" ref="chartEl" :style="{ height: height, width: width }" />
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType, ref, onMounted, onBeforeUnmount, watch, nextTick } from "vue";
import * as echarts from "echarts";
import Common from "../../common.js";
import { getWarningDeviceTypePie } from "@/api/pipeline-maintenance/report-management";
import Title from "@/pages/modules/pipeline-maintenance/report-management/components/ReportTemplate/Title.vue";


// 定义数据类型
interface WarnDeviceItem {
  value: number;
  name: string;
}

interface ChartOption {
  value: number;
  name: string;
}

export default defineComponent({
  cname: "设备告警占比",
  name: "DeviceChart",
  mixins: [Common],
  props: {
      className: {
          type: String as PropType<string>,
          default: "chart"
      },
      width: {
          type: String as PropType<string>,
          default: "100%"
      },
      height: {
          type: String as PropType<string>,
          default: "160px"
      },
      chooseTemple: {
          type: [Number, String] as PropType<number | string>,
          default: 0
      },
      valdata: {
          type: [Object, Array] as PropType<any>,
          default: () => ({})
      }
  },
  setup(props) {
      const chartEl = ref<HTMLDivElement | null>(null);
      const chart = ref<echarts.ECharts | null>(null);
      const warnDevice = ref<WarnDeviceItem[]>([
          { value: 3, name: "电气告警" },
          { value: 2, name: "手动告警" },
          { value: 4, name: "环境告警" },
          { value: 8, name: "其它告警" }
      ]);

      // 监听 valdata 变化
      watch(() => props.valdata, (val) => {
          if (val) {
              initChart();
          }
      });

      // 初始化图表
      const initChart = () => {
          if (!chartEl.value) return;
          
          chart.value = echarts.init(chartEl.value);
          const dataSource = warnDevice.value.map((item) => {
              return {
                  ...item,
                  value: Number(item.value)
              };
          });
          
          const dataSourcemax = dataSource.reduce((sum, item) => sum + item.value, 0);
          const color = [
              "#5470c6",
              "#91cc75",
              "#fac858",
              "#ee6666",
              "#73c0de",
              "#3ba272",
              "#fc8452",
              "#9a60b4",
              "#ea7ccc"
          ];
          
          const salvProMax = new Array(dataSource.length).fill(dataSourcemax);

          const option: echarts.EChartsOption = {
              grid: [
                  {
                      left: 40,
                      top: 0,
                      right: 50,
                      bottom: 0,
                      containLabel: true
                  }
              ],
              xAxis: [
                  {
                      gridIndex: 0,
                      type: "value",
                      axisLabel: { show: false },
                      axisLine: { show: false },
                      splitLine: { show: false },
                      axisTick: { show: false }
                  },
                  {
                      gridIndex: 0,
                      type: "value",
                      max: 100,
                      axisLabel: { show: false },
                      axisLine: { show: false },
                      splitLine: { show: false },
                      axisTick: { show: false }
                  }
              ],
              yAxis: [
                  {
                      gridIndex: 0,
                      type: "category",
                      inverse: true,
                      data: dataSource.map((item) => item.name),
                      axisTick: { show: false },
                      axisLine: { show: false },
                      splitLine: { show: false },
                      position: "left",
                      axisLabel: {
                          width: 140,
                          padding: [0, 0, 0, -80],
                          align: "left",
                          formatter: (name: string) => {
                              return `{value|${name}}`;
                          },
                          rich: {
                              value: {
                                  color: "#333",
                                  fontSize: 14,
                                  fontWeight: 500
                              }
                          }
                      }
                  },
                  {
                      gridIndex: 0,
                      type: "category",
                      position: "right",
                      inverse: true,
                      margin: 20,
                      data: dataSource.map((item) => item.name),
                      axisTick: { show: false },
                      axisLine: { show: false },
                      splitLine: { show: false },
                      axisLabel: {
                          align: "right",
                          padding: [0, -40, 0, 0],
                          formatter: (_: string, index: number) => {
                              const percentage = ((dataSource[index].value / dataSourcemax) * 100).toFixed(2);
                              return `{value|${percentage}%}`;
                          },
                          rich: {
                              value: {
                                  color: "#333",
                                  fontSize: 14,
                                  fontWeight: 500
                              }
                          }
                      }
                  }
              ],
              series: [
                  {
                      z: 1,
                      type: "bar",
                      xAxisIndex: 0,
                      yAxisIndex: 0,
                      barWidth: 14,
                      data: dataSource.map((item) => item.value),
                      silent: true,
                      itemStyle: {
                          emphasis: {
                              barBorderRadius: [0, 20, 20, 0]
                          },
                          normal: {
                              barBorderRadius: [30, 30, 30, 30],
                              color: (params: any) => {
                                  return color[params.dataIndex] || color[0];
                              },
                              label: {
                                  show: true,
                                  offset: [0, 0],
                                  textStyle: {
                                      fontSize: 12,
                                      color: "#fff",
                                      fontWeight: 500
                                  }
                              }
                          }
                      }
                  },
                  {
                      z: 3,
                      name: "背景",
                      type: "bar",
                      barWidth: 14,
                      barGap: "-100%",
                      data: salvProMax,
                      itemStyle: {
                          normal: {
                              color: (params: any) => {
                                  return color[params.dataIndex] || color[0];
                              },
                              opacity: 0.3,
                              barBorderRadius: [30, 30, 30, 30]
                          }
                      }
                  }
              ]
          };

          chart.value.setOption(option);
      };

      // 加载数据
      const loadData = async () => {
          // 从 mixin 中获取数据
          const areaIds = (this as any).areaIds;
          const startTime = (this as any).startTime;
          const endTime = (this as any).endTime;
          
          if (areaIds && areaIds.length > 0) {
              try {
                  const res = await getWarningDeviceTypePie({
                      startTime,
                      endTime,
                      areaIds,
                      tempId: props.chooseTemple
                  });
                  warnDevice.value = res.data;
                  initChart();
              } catch (error) {
                  console.error('加载告警设备数据失败:', error);
                  initChart(); // 使用默认数据
              }
          } else {
              initChart();
          }
      };

      // 组件挂载
      onMounted(() => {
          nextTick(() => {
              loadData();
          });
      });

      // 组件卸载前清理
      onBeforeUnmount(() => {
          if (chart.value) {
              chart.value.dispose();
              chart.value = null;
          }
      });

      // 暴露给模板的方法和数据
      return {
          chartEl,
          initChart
      };
  }
});
</script>

<style scoped>
.chart {
  /* 如果需要添加样式 */
}
</style>