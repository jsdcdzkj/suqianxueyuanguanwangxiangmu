<template>
  <div>
      <Title :index="index" :title="title" :subTitle="subTitle" :subIndex="subIndex" />
      <div class="module-item-desc">
          本周期内，共发生告警
          <span class="num">{{ typeNums.totalNum }}</span>
          次，其中严重告警
          <span class="num">{{ typeNums.seriousNum }}</span>
          次，一般告警
          <span class="num">{{ typeNums.sameNum }}</span>
          次，轻微告警
          <span class="num">{{ typeNums.slightNum }}</span>
          次。
      </div>
      <div :class="className" ref="chartEl" :style="{ height: height, width: width }" />
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType, ref, onMounted, onBeforeUnmount, nextTick, watch, getCurrentInstance } from "vue";
import * as echarts from "echarts";
import type { ECharts, EChartsOption, PieSeriesOption } from "echarts";
import Common from "../../common.js";
import { getWarningType, warningDataForNum } from "@/api/pipeline-maintenance/report-management";
import Title from "@/pages/modules/pipeline-maintenance/report-management/components/ReportTemplate/Title.vue";

// 定义数据类型
interface TypeNums {
  sameNum: number;
  seriousNum: number;
  slightNum: number;
  totalNum: number;
}

interface WarnTypeItem {
  value: number;
  name: string;
  [key: string]: any;
}

interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
}

interface LegendFormatterParams {
  name: string;
  [key: string]: any;
}

export default defineComponent({
  cname: "告警类型统计",
  name:'TypesChart',
  mixins: [Common] as any,
  components: {
    Title
  },
  props: {
      className: {
          type: String as PropType<string>,
          default: "chart"
      },
      areaIds: {
          type: Array as PropType<string[] | number[]>,
          default: () => []
      },
      startTime: {
          type: String as PropType<string>,
          default: ""
      },
      endTime: {
          type: String as PropType<string>,
          default: ""
      },
      width: {
          type: String as PropType<string>,
          default: "100%"
      },
      height: {
          type: String as PropType<string>,
          default: "300px"
      },
      chooseTemple: {
          type: [Number, String] as PropType<number | string>,
          default: 0
      },
      // 添加外部数据传入选项
      externalData: {
          type: Object as PropType<{ warnTypeRatio?: WarnTypeItem[], typeNums?: TypeNums }>,
          default: () => ({})
      }
  },
  setup(props, { expose }) {
      // Refs
      const chartEl = ref<HTMLDivElement | null>(null);
      const chartInstance = ref<ECharts | null>(null);
      
      // 响应式数据
      const typeNums = ref<TypeNums>({
          sameNum: 100,
          seriousNum: 20,
          slightNum: 30,
          totalNum: 40
      });
      
      const warnTypeSum = ref<number>(17);
      const warnTypeRatio = ref<WarnTypeItem[]>([
          { value: 13, name: "电气火灾告警" },
          { value: 2, name: "手动告警" },
          { value: 4, name: "环境告警" },
          { value: 8, name: "其它告警" }
      ]);

      // 颜色配置
      const colorPalette = [
          '#5470c6', '#91cc75', '#fac858', '#ee6666',
          '#73c0de', '#3ba272', '#fc8452', '#9a60b4',
          '#ea7ccc', '#60acfc', '#c23531', '#2f4554',
          '#61a0a8', '#d48265', '#8c7ae6', '#e6a23c'
      ];

      // 监听外部数据变化
      watch(() => props.externalData, (newData) => {
          if (newData.warnTypeRatio) {
              warnTypeRatio.value = newData.warnTypeRatio;
              updateWarnTypeSum();
              initChart();
          }
          if (newData.typeNums) {
              typeNums.value = newData.typeNums;
          }
      }, { deep: true });

      // 计算告警类型总数
      const updateWarnTypeSum = (): void => {
          warnTypeSum.value = warnTypeRatio.value.reduce((sum, item) => {
              return sum + (Number(item.value) || 0);
          }, 0);
      };

      // 自定义图例格式化函数
      const legendFormatter = (name: string): string => {
          const target = warnTypeRatio.value.find(item => item.name === name);
          if (!target) return name;
          
          const percent = ((target.value / warnTypeSum.value) * 100);
          const padding = target.value >= 10 ? '25' : '-10';
          const richKey = target.value >= 10 ? 'b1' : 'b0';
          
          return `{a|${name}}   {${richKey}|占比 ${percent.toFixed(0)}%   ${target.value}个}`;
      };

      // 初始化图表
      const initChart = (): void => {
          if (!chartEl.value) {
              console.warn('Chart element not found');
              return;
          }
          
          // 如果已有图表实例，先销毁
          if (chartInstance.value) {
              chartInstance.value.dispose();
          }
          
          // 创建新的图表实例
          chartInstance.value = echarts.init(chartEl.value);
          
          // 更新总数
          updateWarnTypeSum();
          
          // 配置项
          const option: EChartsOption = {
              tooltip: {
                  trigger: "item",
                  backgroundColor: 'rgba(50, 50, 50, 0.9)',
                  borderColor: 'rgba(255, 255, 255, 0.2)',
                  textStyle: {
                      color: '#fff',
                      fontSize: 14
                  },
                  borderWidth: 1,
                  formatter: (params: any) => {
                      if (Array.isArray(params)) {
                          return params.map(p => `${p.name}: ${p.value}个<br/>占比: ${p.percent}%`).join('<br/>');
                      }
                      return `${params.name}: ${params.value}个<br/>占比: ${params.percent}%`;
                  }
              },
              legend: {
                  type: "scroll",
                  orient: "vertical",
                  right: "8%",
                  top: "center",
                  selectedMode: true,
                  icon: "circle",
                  itemWidth: 10,
                  itemHeight: 10,
                  itemGap: 15,
                  textStyle: {
                      rich: {
                          a: {
                              color: "#666666",
                              fontSize: 14,
                              fontWeight: 500,
                              padding: [0, 0, 0, 2]
                          },
                          b0: {
                              color: "#666666",
                              fontSize: 14,
                              fontWeight: 500,
                              padding: [0, 0, 0, 4]
                          },
                          b1: {
                              color: "#666666",
                              fontSize: 14,
                              fontWeight: 500,
                              padding: [0, 0, 0, 4]
                          }
                      }
                  },
                  formatter: legendFormatter
              },
              series: [
                  {
                      type: "pie",
                      center: ["25%", "50%"],
                      radius: ["35%", "55%"],
                      clockwise: true,
                      avoidLabelOverlap: true,
                      hoverOffset: 5,
                      emphasis: {
                          scale: true,
                          scaleSize: 5,
                          itemStyle: {
                              shadowBlur: 10,
                              shadowOffsetX: 0,
                              shadowColor: 'rgba(0, 0, 0, 0.5)'
                          }
                      },
                      label: {
                          show: true,
                          position: "outside",
                          formatter: '{b}: {d}%',
                          fontSize: 12,
                          color: '#333'
                      },
                      labelLine: {
                          length: 15,
                          length2: 20,
                          lineStyle: {
                              width: 1,
                              type: 'solid'
                          },
                          smooth: 0.2
                      },
                      itemStyle: {
                          borderRadius: 4,
                          borderColor: '#fff',
                          borderWidth: 2
                      },
                      color: colorPalette,
                      data: warnTypeRatio.value.map((item, index) => ({
                          ...item,
                          itemStyle: {
                              color: colorPalette[index % colorPalette.length]
                          }
                      }))
                  } as PieSeriesOption
              ],
              animation: true,
              animationDuration: 1000,
              animationEasing: 'cubicOut'
          };

          chartInstance.value.setOption(option);
          
          // 添加窗口大小变化监听
          const handleResize = () => {
              if (chartInstance.value) {
                  chartInstance.value.resize();
              }
          };
          
          window.addEventListener('resize', handleResize);
          
          // 返回清理函数
          return () => {
              window.removeEventListener('resize', handleResize);
          };
      };

      // 加载数据
      const loadData = async (): Promise<void> => {
          try {
              
              if (!props.areaIds || props.areaIds.length === 0) {
                  initChart();
                  return;
              }
              
              // 并行请求数据
              const [pieResponse, numsResponse] = await Promise.all([
                  getWarningType({
                      startTime: props.startTime,
                      endTime: props.endTime,
                      areaIds: props.areaIds,
                      tempId: props.chooseTemple
                  }) as Promise<ApiResponse<WarnTypeItem[]>>,
                  warningDataForNum({
                      startTime: props.startTime,
                      endTime: props.endTime,
                      areaIds: props.areaIds
                  }) as Promise<ApiResponse<TypeNums>>
              ]);
              
              if (pieResponse.data) {
                  warnTypeRatio.value = pieResponse.data.map(item => ({
                      ...item,
                      value: Number(item.value) || 0
                  }));
              }
              
              if (numsResponse.data) {
                  typeNums.value = numsResponse.data;
              }
              
              initChart();
          } catch (error) {
              console.error('Failed to load warning type data:', error);
              // 使用默认数据
              initChart();
          }
      };

      // 刷新数据
      const refreshData = async (): Promise<void> => {
          await loadData();
      };

      // 更新数据
      const updateData = (pieData: WarnTypeItem[], numsData: TypeNums): void => {
          warnTypeRatio.value = pieData;
          typeNums.value = numsData;
          initChart();
      };

      // 组件挂载
      onMounted(async () => {
          await nextTick();
          await loadData();
      });

      // 组件卸载
      onBeforeUnmount(() => {
          if (chartInstance.value) {
              chartInstance.value.dispose();
              chartInstance.value = null;
          }
      });

      // 暴露给父组件的方法
      expose({
          refreshChart: refreshData,
          getChart: () => chartInstance.value,
          updateData,
          getData: () => ({
              warnTypeRatio: warnTypeRatio.value,
              typeNums: typeNums.value,
              warnTypeSum: warnTypeSum.value
          })
      });

      return {
          chartEl,
          typeNums,
          warnTypeRatio,
          warnTypeSum,
          initChart,
          loadData,
          refreshData
      };
  },
  
  // Vue 2 选项式 API 兼容
  watch: {
      '$props.valdata': {
          handler(newVal: any) {
              if (newVal) {
                  (this as any).initChart();
              }
          },
          deep: true
      }
  },
  
  computed: {
      // 计算百分比显示
      warningPercentages() {
          const total = this.typeNums.totalNum || 1;
          return {
              serious: ((this.typeNums.seriousNum / total) * 100).toFixed(1),
              same: ((this.typeNums.sameNum / total) * 100).toFixed(1),
              slight: ((this.typeNums.slightNum / total) * 100).toFixed(1)
          };
      }
  },
  
  methods: {
      // Vue 2 方法
      async loadChartData() {
          if (this.areaIds?.length > 0) {
              try {
                  const [pieData, numsData] = await Promise.all([
                      getWarningType({
                          startTime: this.startTime,
                          endTime: this.endTime,
                          areaIds: this.areaIds,
                          tempId: this.chooseTemple
                      }),
                      warningDataForNum({
                          startTime: this.startTime,
                          endTime: this.endTime,
                          areaIds: this.areaIds
                      })
                  ]);
                  
                  this.warnTypeRatio = pieData.data;
                  this.typeNums = numsData.data;
                  this.warnTypeSum = this.warnTypeRatio.reduce((sum, item) => 
                      sum + (Number(item.value) || 0), 0);
                  
                  this.initChart();
              } catch (error) {
                  console.error('加载告警类型数据失败:', error);
                  this.initChart();
              }
          } else {
              this.initChart();
          }
      }
  }
});
</script>

<style scoped>
.module-item-desc {
    line-height: 1.8;
    color: #333;
    padding:16px 0;
}

.num {
  font-weight: bold;
  color: #1890ff;
  font-size: 16px;
  margin: 0 4px;
}

.chart {
  background-color: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.3s ease;
}

.chart:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}
</style>