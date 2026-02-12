<template>
	<div class="h-full flex gap-12px p-12px">
		<div class="flex-1 h-full flex flex-col w-full relative">
			<div class="overview-top mb-12px">
				<StructureCard title="控制台">
					<div class="flex justify-between items-center">
						<div class="flex over-records">
							<div class="over-records-item mb-8px">
								<div class="over-records-item-title">登录次数:</div>
								<div class="over-records-item-num">{{ loginRecords.loginCount }}</div>
							</div>
							<div class="over-records-item mb-8px">
								<div class="over-records-item-title">上次登录IP:</div>
								<div class="over-records-item-num">{{ loginRecords.logIp }}</div>
							</div>
							<div class="over-records-item">
								<div class="over-records-item-title">上次登录地址:</div>
								<div class="over-records-item-num">{{ loginRecords.logLocation }}</div>
							</div>
							<div class="over-records-item">
								<div class="over-records-item-title">上次登录时间:</div>
								<div class="over-records-item-num">{{ loginRecords.logTime }}</div>
							</div>
						</div>
						<div class="flex items-center over-date-con">
							<div class="over-date">
								<div class="date-year">{{ formatDate(currentDate).year }}</div>
								<div class="date-day-time">
									<div class="date-day">{{ formatDate(currentDate).day }}</div>
									<div class="date-time">{{ formatDate(currentDate).time }}</div>
								</div>
							</div>
							<div class="over-date-icon w-80px h-80px ml-24px">
								<img src="@/assets/images/setting/sys-icon7.png" class="w-full h-full" alt="icon" />
							</div>
						</div>
					</div>
				</StructureCard>
			</div>
			<div class="overview-center mb-12px flex gap-12px">
				<div class="over-center-item flex-1">
					<StructureCard title="设备统计">
						<template #actions>
							<div class="filter-form flex items-center">
								<div class="flex items-center justify-between sys-status-item">
									<div class="sys-status-item-title">运行</div>
									<div class="sys-status-item-num">{{ deviceStats.waterOnlineCounts }}</div>
								</div>
								<div class="flex items-center justify-between sys-status-item">
									<div class="sys-status-item-title danger">离线</div>
									<div class="sys-status-item-num">{{ deviceStats.waterOfflineCounts }}</div>
								</div>
								<el-button :icon="Refresh" @click="handleSystemRefresh">刷新</el-button>
							</div>
						</template>
						<div class="flex items-center sys-status-summary gap-12px">
							<div class="tatus-summary-item">
								<img src="@/assets/images/setting/sys-icon1.png" class="w-143px h-145px" alt="icon" />
								<div class="sys-status-summary-item-num">
									<span class="online-num">{{ deviceStats.waterOnlineCount }}</span
									>/
									<span class="offline-num">{{ deviceStats.waterOfflineCount }}</span>
								</div>
								<div class="sys-status-summary-item-title">智慧水表</div>
							</div>
							<div class="tatus-summary-item">
								<img src="@/assets/images/setting/sys-icon2.png" class="w-143px h-145px" alt="icon" />
								<div class="sys-status-summary-item-num">
									<span class="online-num">{{ deviceStats.waterDetectorOnlineCount }}</span
									>/
									<span class="offline-num">{{ deviceStats.waterDetectorOfflineCount }}</span>
								</div>
								<div class="sys-status-summary-item-title">漏水检测仪</div>
							</div>
						</div>
					</StructureCard>
				</div>
				<div class="over-center-item flex-1">
					<StructureCard title="数据信息">
						<div class="sys-msg-summary">
							<div class="flex items-center sys-msg-item">
								<div class="sys-msg-item-left">
									<img src="@/assets/images/setting/sys-icon3.png" class="w-44px h-44px" alt="icon" />
								</div>
								<div class="sys-msg-item-right">
									<div class="sys-msg-item-num">103</div>
									<div class="sys-msg-item-title">告警数量(条)</div>
								</div>
							</div>
							<div class="flex items-center sys-msg-item">
								<div class="sys-msg-item-left blue">
									<img src="@/assets/images/setting/sys-icon4.png" class="w-44px h-44px" alt="icon" />
								</div>
								<div class="sys-msg-item-right">
									<div class="sys-msg-item-num">103</div>
									<div class="sys-msg-item-title">年供水量(m³)</div>
								</div>
							</div>
							<div class="flex items-center sys-msg-item">
								<div class="sys-msg-item-left green">
									<img src="@/assets/images/setting/sys-icon5.png" class="w-44px h-44px" alt="icon" />
								</div>
								<div class="sys-msg-item-right">
									<div class="sys-msg-item-num">103</div>
									<div class="sys-msg-item-title">工单数量(条)</div>
								</div>
							</div>
							<div class="flex items-center sys-msg-item">
								<div class="sys-msg-item-left pink">
									<img src="@/assets/images/setting/sys-icon6.png" class="w-44px h-44px" alt="icon" />
								</div>
								<div class="sys-msg-item-right">
									<div class="sys-msg-item-num">103</div>
									<div class="sys-msg-item-title">漏水次数(次)</div>
								</div>
							</div>
						</div>
					</StructureCard>
				</div>
				<div class="over-center-item flex-1">
					<StructureCard title="分区用水排行">
						<template #actions>
							<div class="filter-form flex items-center">
								<el-date-picker
									v-model="queryParams.deviceName"
									clearable
									type="daterange"
									start-placeholder="开始时间"
									end-placeholder="结束时间"
									style="width: 210px"
								/>

								<div class="ml-8px">
									<!-- 查询按钮 -->
									<el-button type="primary" :icon="Search" @click="handleQuery">查询</el-button>
								</div>
								<div class="ml-8px">
									<!-- 重置按钮 -->
									<el-button :icon="Refresh" @click="handleReset">重置</el-button>
								</div>
							</div>
						</template>
						<el-table :data="tableData" style="width: 100%" border height="280px">
							<el-table-column type="index" label="排序" width="80" align="center" />
							<el-table-column prop="primaryArea" label="一级区域" />
							<el-table-column prop="subArea" label="所属分区" />
							<el-table-column prop="waterUsage" label="用水量(万m³)" align="right">
								<template #default="{ row }">
									{{ row.waterUsage.toFixed(1) }}
								</template>
							</el-table-column>
						</el-table>
					</StructureCard>
				</div>
			</div>
			<div class="overview-bot">
				<StructureCard title="供水量趋势">
					<template #actions>
						<div class="filter-form flex">
							<!-- 年月日单选按钮组 -->
							<el-radio-group v-model="dateType" size="default" class="mr-8px">
								<el-radio-button label="date">日</el-radio-button>
								<el-radio-button label="month">月</el-radio-button>
								<el-radio-button label="year">年</el-radio-button>
							</el-radio-group>

							<el-date-picker
								v-if="dateType === 'date'"
								v-model="queryParams.deviceName"
								clearable
								type="daterange"
								start-placeholder="开始时间"
								end-placeholder="结束时间"
								style="width: 210px"
							/>
							<el-date-picker
								v-if="dateType === 'month'"
								v-model="queryParams.deviceName"
								clearable
								type="month"
								placeholder="请选择月份"
								style="width: 210px"
							/>
							<el-date-picker
								v-if="dateType === 'year'"
								v-model="queryParams.deviceName"
								clearable
								type="year"
								placeholder="请选择年份"
								style="width: 210px"
							/>

							<div class="ml-8px">
								<!-- 查询按钮 -->
								<el-button type="primary" :icon="Search" @click="handleQuery">查询</el-button>
							</div>
							<div class="ml-8px">
								<!-- 重置按钮 -->
								<el-button :icon="Refresh" @click="handleReset">重置</el-button>
							</div>
						</div>
					</template>
					<div class="parent-container">
						<WaterContrastLine :height="'100%'" />
					</div>
				</StructureCard>
			</div>
		</div>
	</div>
</template>

<script setup lang="tsx">
	import { reactive, ref, onMounted, onUnmounted } from "vue";
	import StructureCard from "@/components/card/StructureCard";
	import { Search, Refresh } from "@element-plus/icons-vue";
	import WaterContrastLine from "./components/WaterContrastLine.vue";
	import { getCenterData } from "@/api/setting/overView";
	import { ElMessage } from "element-plus";
	// 登录记录数据
	const loginRecords = reactive({
		loginCount: 0,
		logIp: "",
		logLocation: "",
		logTime: ""
	});

	// 设备统计数据
	const deviceStats = reactive({
		waterOnlineCount: 0, // 智慧水表在线数量
		waterOfflineCount: 0, // 智慧水表离线数量
		waterDetectorOnlineCount: 0, // 漏水检测仪在线数量
		waterDetectorOfflineCount: 0, // 漏水检测仪离线数量
		waterOnlineCounts: 0, // 总运行数量
		waterOfflineCounts: 0 // 总离线数量
	});

	// 工单数量
	const orderCount = ref(0);

	// 日期类型：年/月/日
	const dateType = ref("date");
	// 添加日期时间相关的响应式数据
	const currentDate = ref(new Date());
	const timeInterval = ref<number>();
	// 格式化日期
	const formatDate = (date: Date) => {
		const months = [
			"January",
			"February",
			"March",
			"April",
			"May",
			"June",
			"July",
			"August",
			"September",
			"October",
			"November",
			"December"
		];
		const days = ["星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"];

		return {
			year: `${months[date.getMonth()]}${date.getDate()}, ${date.getFullYear()}`,
			day: days[date.getDay()],
			time: `${date.getHours().toString().padStart(2, "0")}:${date.getMinutes().toString().padStart(2, "0")}:${date.getSeconds().toString().padStart(2, "0")}`
		};
	};
	// 组件挂载时启动定时器
	onMounted(() => {
		timeInterval.value = setInterval(() => {
			currentDate.value = new Date();
		}, 1000);
		getOverviewData();
	});

	// 组件卸载时清除定时器
	onUnmounted(() => {
		if (timeInterval.value) {
			clearInterval(timeInterval.value);
		}
	});
	// 获取概览数据
	const getOverviewData = async () => {
		try {
			const res = await getCenterData();

			// 更新登录记录
			loginRecords.loginCount = res.loginCount || 0;
			loginRecords.logIp = res.logIp || "";
			loginRecords.logLocation = res.logLocation || "";
			loginRecords.logTime = res.logTime || "";

			// 更新设备统计
			deviceStats.waterOnlineCount = res.waterOnlineCount || 0;
			deviceStats.waterOfflineCount = res.waterOfflineCount || 0;
			deviceStats.waterDetectorOnlineCount = res.waterDetectorOnlineCount || 0;
			deviceStats.waterDetectorOfflineCount = res.waterDetectorOfflineCount || 0;
			deviceStats.waterOnlineCounts = res.waterOnlineCounts || 0;
			deviceStats.waterOfflineCounts = res.waterOfflineCounts || 0;

			// 更新工单数量
			orderCount.value = res.orderCount || 0;
		} catch (error) {
			console.error("获取概览数据失败:", error);
		}
	};
	// 刷新设备统计数据
	const handleSystemRefresh = async () => {
		try {
			const res = await getCenterData({});

			// 更新设备统计
			deviceStats.waterOnlineCount = res.waterOnlineCount || 0;
			deviceStats.waterOfflineCount = res.waterOfflineCount || 0;
			deviceStats.waterDetectorOnlineCount = res.waterDetectorOnlineCount || 0;
			deviceStats.waterDetectorOfflineCount = res.waterDetectorOfflineCount || 0;
			deviceStats.waterOnlineCounts = res.waterOnlineCounts || 0;
			deviceStats.waterOfflineCounts = res.waterOfflineCounts || 0;
			ElMessage.success("刷新设备统计数据成功");
		} catch (error) {
			console.error("刷新设备统计数据失败:", error);
		}
	};

	// 查询参数对象
	const queryParams = reactive({
		deviceName: "",
		status: ""
	});

	// --- 方法 ---

	// 查询方法
	const handleQuery = () => {
		console.log("查询参数:", {
			dateType: dateType.value,
			...queryParams
		});
	};

	// 重置方法
	const handleReset = () => {
		dateType.value = "date";
		queryParams.deviceName = "";
		queryParams.status = "";
	};
	// 在script setup中添加
	const tableData = ref([
		{
			rank: 1,
			primaryArea: "东区",
			subArea: "商业区",
			waterUsage: 125.6
		},
		{
			rank: 2,
			primaryArea: "西区",
			subArea: "住宅区",
			waterUsage: 98.3
		},
		{
			rank: 3,
			primaryArea: "南区",
			subArea: "工业区",
			waterUsage: 87.2
		}
	]);
</script>
<style lang="scss" scoped>
	.overview-top {
		.over-records {
			width: 612px;
			flex-wrap: wrap;
			justify-content: space-between;
			.over-records-item {
				padding: 0 8px;
				width: 300px;
				height: 38px;
				display: flex;
				align-items: center;
				justify-content: space-between;
				font-size: 14px;
				color: #666;
				background: #fdfeff;
				border: 1px solid rgba(0, 0, 0, 0.06);
			}
		}
		.over-date-con {
			.over-date {
				.date-year {
					margin-bottom: 8px;
					font-weight: bold;
					font-size: 32px;
					color: #2952ac;
					line-height: 32px;
				}
				.date-day-time {
					display: flex;
					align-items: center;
					justify-content: space-between;
					font-size: 16px;
					color: #333;
					line-height: 24px;
				}
			}
		}
	}
	.over-center-item {
		.sys-status-item {
			min-width: 114px;
			padding: 0 8px;
			margin-right: 8px;
			height: 32px;
			background: #ffffff;
			font-size: 14px;
			border-radius: 4px 4px 4px 4px;
			border: 1px solid rgba(0, 0, 0, 0.15);
			.sys-status-item-title {
				display: flex;
				align-items: center;
				color: #666;
				&::before {
					content: "";
					display: inline-block;
					width: 12px;
					height: 12px;
					border-radius: 50%;
					margin-right: 8px;
					background-color: #61c554;
				}
				&.danger {
					&::before {
						background-color: #ed6a5e;
					}
				}
			}
			.sys-status-item-num {
				color: #333;
			}
		}
		.sys-status-summary {
			width: 100%;
			.tatus-summary-item {
				flex: 1;
				display: flex;
				flex-direction: column;
				align-items: center;
				justify-content: center;
				height: 280px;
				border-radius: 0px 0px 0px 0px;
				border: 1px solid rgba(0, 0, 0, 0.06);
				background: linear-gradient(180deg, #fff 0%, #eef8ff 100%);
				.sys-status-summary-item-num {
					display: flex;
					align-items: center;
					margin: 12px 0 8px;
					font-weight: bold;
					font-size: 16px;
					color: #86898c;
					line-height: 32px;
					.online-num {
						margin-right: 8px;
						font-size: 32px;
						color: #57bd94;
					}
					.offline-num {
						margin-left: 8px;
						font-size: 32px;
						color: #ed6a5e;
					}
				}
				.sys-status-summary-item-title {
					font-size: 16px;
					color: #666;
					line-height: 24px;
				}
			}
		}
		.sys-msg-summary {
			width: 100%;
			display: grid;
			grid-template-columns: 1fr 1fr;
			gap: 12px;
			.sys-msg-item {
				padding-left: 12px;
				width: 100%;
				height: 134px;
				border: 1px solid rgba(0, 0, 0, 0.06);
				background: linear-gradient(180deg, #fff 0%, #eef8ff 100%);
				.sys-msg-item-left {
					margin-right: 16px;
					width: 64px;
					height: 64px;
					background: #fbe1df;
					border-radius: 8px;
					display: flex;
					align-items: center;
					justify-content: center;
					&.blue {
						background: #e3edfa;
					}
					&.green {
						background: #d9f2ee;
					}
					&.pink {
						background: #f9ddec;
					}
				}
				.sys-msg-item-right {
					display: flex;
					flex-direction: column;
					justify-content: center;
					.sys-msg-item-num {
						font-weight: bold;
						font-size: 32px;
						color: #333;
						line-height: 36px;
					}
					.sys-msg-item-title {
						margin-top: 6px;
						font-size: 16px;
						color: rgba(0, 0, 0, 0.65);
						line-height: 24px;
					}
				}
			}
		}
	}
	:deep(.module-card__header) {
		height: 48px;
	}
	.overview-bot {
		height: calc(100% - 530px);
	}
	.parent-container {
		height: calc(100% - 0px);
	}
</style>
