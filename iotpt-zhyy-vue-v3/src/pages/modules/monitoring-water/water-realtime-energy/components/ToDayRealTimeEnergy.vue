<template>
	<div class="todayrealtimeenergy">
		<div class="todayrealtimeenergy__header flex justify-between items-center">
			<span class="todayrealtimeenergy__header--title">今日实时能耗</span>
			<el-radio-group v-model="viewType" size="default">
				<!-- 表格视图 -->
				<el-radio-button label="table"><i class="ri-list-check"></i> 表格</el-radio-button>
				<!-- 卡片视图 -->
				<el-radio-button label="card"><i class="ri-apps-fill"></i> 卡片</el-radio-button>
			</el-radio-group>
		</div>
		<div class="todayrealtimeenergy__content">
			<!-- 表格视图 -->
			<el-table
				v-if="viewType === 'table'"
				:data="currentPageData"
				style="width: 100%"
				height="calc(100% - 50px)"
				border
			>
				<el-table-column align="center" prop="deviceName" label="设备名称" width="180"></el-table-column>
				<el-table-column align="center" prop="deviceType" label="设备类型" width="120"></el-table-column>
				<el-table-column align="center" prop="deviceLocation" label="设备点位描述"></el-table-column>
				<el-table-column align="center" prop="energyConsumption" label="今日设备实时能耗(m3)" width="180">
					<template #default="scope">
						<span :class="getEnergyClass(scope.row.energyConsumption)">
							{{ scope.row.energyConsumption }}
						</span>
					</template>
				</el-table-column>
				<el-table-column align="center" prop="onlineStatus" label="在线状态" width="100">
					<template #default="scope">
						<el-tag :type="scope.row.onlineStatus === '在线' ? 'success' : 'danger'">
							{{ scope.row.onlineStatus }}
						</el-tag>
					</template>
				</el-table-column>
				<el-table-column align="center" label="操作" width="100">
					<template #default="scope">
						<el-button type="primary" link @click="handleDetail(scope.row)">详情</el-button>
					</template>
				</el-table-column>
			</el-table>

			<!-- 卡片视图 -->
			<div v-else class="card-container">
				<device-card v-for="item in currentPageData" :key="item.id" :device="item" @detail="handleDetail" />
			</div>

			<!-- 分页组件 -->
			<div class="pagination-container">
				<el-pagination
					v-model:current-page="currentPage"
					v-model:page-size="pageSize"
					:page-sizes="[10, 20, 50, 100]"
					:small="false"
					:background="true"
					layout="total, sizes, prev, pager, next, jumper"
					:total="deviceList.length"
					@size-change="handleSizeChange"
					@current-change="handleCurrentChange"
				/>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts">
	import { ref, computed } from "vue";
	import { ElMessage } from "element-plus";
	import DeviceCard from "./DeviceCard.vue";

	// 视图类型：table或card
	const viewType = ref("table");

	// 分页相关
	const currentPage = ref(1);
	const pageSize = ref(10);

	// 模拟设备数据
	interface Device {
		id: number;
		deviceName: string;
		deviceType: string;
		deviceLocation: string;
		energyConsumption: string;
		onlineStatus: string;
	}

	// 生成更多模拟数据
	const generateMockData = (): Device[] => {
		const deviceTypes = ["空调", "水泵", "照明", "电梯", "通风"];
		const locations = ["一楼大厅东侧", "地下二层水泵房", "三层走廊", "主楼电梯井", "屋顶通风机房", "二楼会议室"];
		const mockData: Device[] = [];

		for (let i = 1; i <= 50; i++) {
			const typeIndex = Math.floor(Math.random() * deviceTypes.length);
			const locationIndex = Math.floor(Math.random() * locations.length);
			const isOnline = Math.random() > 0.2; // 80%概率在线
			const energy = (Math.random() * 20).toFixed(1);

			mockData.push({
				id: i,
				deviceName: `${deviceTypes[typeIndex]}设备${i}`,
				deviceType: deviceTypes[typeIndex],
				deviceLocation: locations[locationIndex],
				energyConsumption: energy,
				onlineStatus: isOnline ? "在线" : "离线"
			});
		}

		return mockData;
	};

	const deviceList = ref<Device[]>(generateMockData());

	// 当前页数据
	const currentPageData = computed(() => {
		const start = (currentPage.value - 1) * pageSize.value;
		const end = start + pageSize.value;
		return deviceList.value.slice(start, end);
	});

	// 根据能耗值返回不同的样式类
	const getEnergyClass = (value: string) => {
		const numValue = parseFloat(value);
		if (numValue > 10) return "high-energy";
		if (numValue > 5) return "medium-energy";
		return "low-energy";
	};

	// 处理详情按钮点击事件
	const handleDetail = (device: Device) => {
		ElMessage.success(`查看设备详情: ${device.deviceName}`);
		// 这里可以添加跳转到详情页的逻辑
	};

	// 处理每页显示数量变化
	const handleSizeChange = (size: number) => {
		pageSize.value = size;
		// 如果当前页超出范围，则重置为第一页
		if (currentPage.value > Math.ceil(deviceList.value.length / size)) {
			currentPage.value = 1;
		}
	};

	// 处理当前页变化
	const handleCurrentChange = (page: number) => {
		currentPage.value = page;
	};
</script>

<style lang="scss" scoped>
	.todayrealtimeenergy {
		display: flex;
		gap: 12px;
		width: 100%;
		flex: 1;
		flex-direction: column;
		min-height: 0;
		overflow: hidden;

		&__header {
			background: #f8f8f8;
			border-radius: 4px 4px 4px 4px;
			padding: 12px;
			width: 100%;

			&--title {
				font-size: 16px;
				font-weight: 600;
				color: rgba(0, 0, 0, 0.85);
			}
		}

		&__content {
			flex: 1;
			min-height: 0;
			overflow: hidden;
			width: 100%;
			display: flex;
			flex-direction: column;
		}
	}

	// 能耗值样式
	.high-energy {
		color: #f56c6c;
		font-weight: bold;
	}

	.medium-energy {
		color: #e6a23c;
		font-weight: bold;
	}

	.low-energy {
		color: #67c23a;
	}

	// 卡片容器样式
	.card-container {
		display: grid;
		grid-template-columns: repeat(auto-fill, minmax(310px, 1fr));
		gap: 12px;
		flex: 1;
		overflow-y: auto;
		padding-bottom: 8px;
	}

	// 分页容器样式
	.pagination-container {
		height: 50px;
		display: flex;
		justify-content: flex-end;
		align-items: center;
		padding: 0 8px;
		border-top: 1px solid #ebeef5;
	}
</style>
