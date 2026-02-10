<template>
	<div class="alarm-type-box-container">
        <ElRow :gutter="12">
            <ElCol :span="8">
                <div class="alarm-type-card">
                    <div class="alarm-type-box">
                        <div class="icon-bg">
                            <img class="icon-img" src="@/assets/images/alarm-center/alarm1.png" alt="">

                        </div>
                        <div class="alarm-num">
                            <div class="alarm-num-title">告警总数</div>
                            <div class="alarm-num-content">
                                <span class="alarm-success">103</span>/<span class="alarm-error">20</span>
                            </div>
                        </div>
                    </div>
                    <!--告警折线图  -->
                    <div class="alarm-line-num">
                        <AlarmTotalLine v-bind="headerData.water.chart"></AlarmTotalLine>
                    </div>
                </div>
            </ElCol>
            <ElCol :span="8">
                <div class="alarm-type-card flex">
                    <div class="alarm-type-box">
                        <div class="icon-bg">
                            <img class="icon-img" src="@/assets/images/alarm-center/alarm2.png" alt="">
                        </div>
                        <div class="alarm-num">
                            <div class="alarm-num-title">漏损告警 </div>
                            <div class="alarm-num-content">
                                <span class="alarm-success">103</span>/<span class="alarm-error">20</span>
                            </div>
                        </div>
                    </div>
                    <!-- 漏损告警 -->
                    <div class="chart-box">
                        <LeakageBar v-bind="headerData.water.chart"></LeakageBar>
                    </div>
                </div>
                <div class="alarm-type-card flex">
                    <div class="alarm-type-box">
                        <div class="icon-bg">
                            <img class="icon-img" src="@/assets/images/alarm-center/alarm3.png" alt="">
                        </div>
                        <div class="alarm-num">
                            <div class="alarm-num-title">MNF异常告警</div>
                            <div class="alarm-num-content">
                                <span class="alarm-success">103</span>/<span class="alarm-error">20</span>
                            </div>
                        </div>
                    </div>
                    <!-- MNF异常告警 -->
                    <div class="chart-box">
                        <MNFBar v-bind="headerData.water.chart"></MNFBar>
                    </div>
                </div>
            </ElCol>
            <ElCol :span="8">
                <div class="alarm-type-card flex">
                    <div class="alarm-type-box">
                        <div class="icon-bg">
                            <img class="icon-img" src="@/assets/images/alarm-center/alarm4.png" alt="">
                        </div>
                        <div class="alarm-num">
                            <div class="alarm-num-title">用能告警</div>
                            <div class="alarm-num-content">
                                <span class="alarm-success">103</span>/<span class="alarm-error">20</span>
                            </div>
                        </div>
                    </div>
                    <!-- 用能告警 -->
                    <div class="chart-box">
                        <EnergyAlarmLine v-bind="headerData.water.chart"></EnergyAlarmLine>
                    </div>
                    
                </div>
                <div class="alarm-type-card flex">
                    <div class="alarm-type-box">
                        <div class="icon-bg">
                            <img class="icon-img" src="@/assets/images/alarm-center/alarm5.png" alt="">
                        </div>
                        <div class="alarm-num">
                            <div class="alarm-num-title">设备离线告警</div>
                            <div class="alarm-num-content">
                                <span class="alarm-success">103</span>/<span class="alarm-error">20</span>
                            </div>
                        </div>
                    </div>
                    <!-- 设备离线告警 -->
                    <div class="chart-box">
                        <AlarmLeaveLine v-bind="headerData.water.chart"></AlarmLeaveLine>
                    </div>
                </div>
            </ElCol>
        </ElRow>
    </div>
</template>

<script setup lang="tsx">
import { reactive } from 'vue';
import { ElRow, ElCol } from 'element-plus';
import LeakageBar from "../echarts/leakageBar.vue";
import MNFBar from "../echarts/MNFBar.vue";
import { getRealtimeTop } from "@/api/monitoring-water/realtime";
import AlarmLeaveLine from "../echarts/alarmLeaveLine.vue";
import AlarmTotalLine from "../echarts/alarmTotalLine.vue";
import EnergyAlarmLine from "../echarts/energyAlarmLine.vue";

const headerData = reactive({
		water: {
			value: 0,
			contrastValue: 0,
			contrast: {
				value: 0,
				type: "down"
			},
			chart: {
				xdata: [],
				ydata: [],
				ydata1: []
			}
		},
		cost: {
			value: 0,
			contrastValue: 0,
			contrast: {
				value: 0,
				type: "down"
			},
			chart: {
				xdata: [],
				ydata: []
			}
		}
	});
    getRealtimeTop().then(({ data }) => {
		console.log(data);

		// 用水
		headerData.water.value = data.currentMonth.totalWater;
		headerData.water.contrastValue = data.lastMonth.totalWater;
		headerData.water.contrast.value = data.comparison.waterChangePercent;
		headerData.water.contrast.type = data.comparison.waterType;
		headerData.water.chart.xdata = data.dateLabels.slice(0, 7);
		headerData.water.chart.ydata = data.currentMonth.dailyWaterUsage.slice(0, 7);
		headerData.water.chart.ydata1 = data.lastMonth.dailyWaterUsage.slice(0, 7);

		// 费用
		headerData.cost.value = data.currentMonth.totalCost;
		headerData.cost.contrastValue = data.lastMonth.totalCost;
		headerData.cost.contrast.value = data.comparison.costChangePercent;
		headerData.cost.contrast.type = data.comparison.costType;
	});

</script>
<style lang="scss" scoped>
.alarm-line-num {
    height: 100px;
}
.chart-box {
    flex:1;
}
.alarm-type-box-container {
    width: 100%;
}
.alarm-type-card {
    background: #fff;
    padding: 12px;
    margin-bottom: 12px;
}
.alarm-type-box {
    height: 64px;
    width:176px;
    background-color: #fff;
    display: flex;
    align-items: center;

    .icon-bg {
        width: 64px;
        height: 64px;
        background: #E7F0FB;
        border-radius: 8px 8px 8px 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right:12px;

        .icon-img {
            width: 40px;
            height: 40px;
        }
    }

    .alarm-num {
        flex: 1;
        height: 100%;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: flex-start;

        .alarm-num-title {
            font-size: 16px;
            color: rgba(0,0,0,0.65);
            line-height: 24px;
        }
        .alarm-num-content {
            font-size: 16px;
            display: flex;
            align-items: center;
            color: rgba(0,0,0,0.45);
        }


        .alarm-success {
            font-family: Douyin Sans, Douyin Sans;
            font-weight: bold;
            font-size: 28px;
            color: #57BD94;
            line-height: 32px;
            margin-right: 8px;
        }

        .alarm-error {
            font-family: Douyin Sans, Douyin Sans;
            font-weight: bold;
            font-size: 28px;
            color: #ED6A5E;
            line-height: 32px;
            margin-left: 8px;
        }

    }

}
</style>
