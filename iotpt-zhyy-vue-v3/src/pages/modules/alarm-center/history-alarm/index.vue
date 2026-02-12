<template>
	<BasePage v-bind="page">
		<template #pageTableCell="{ column, row }">
			<template v-if="column.columnKey === 'alarmLevelName'">
				<span class="degree">
					<i :class="row.alarmLevel == 1 || row.alarmLevel == 2 || row.alarmLevel == 3 ? 'warning' : 'danger'"></i>
					{{ row.alarmLevelName }}
				</span>
			</template>
			<!-- 处理状态 -->
			<template v-if="column.columnKey === 'handleStatus'">
				<ElTag type="warning" v-if="row.handleStatus == 2">上报</ElTag>
                <ElTag type="warning" v-if="row.handleStatus == 3">忽略</ElTag>
			</template>
		</template>
		<template #tableActions="scope">
			<ElButton type="primary" link @click="handleDealList(scope.row)">查看处置记录</ElButton>
			<ElButton type="primary" link @click="handleView(scope.row)">详情</ElButton>
		</template>
	</BasePage>
</template>

<script setup lang="tsx">
	import BasePage from "@/core/struct/page/base-page";
	import { usePage } from "@/core/struct/page";
	import { type FormConfig } from "@/core/struct/form/use-base-form";
	import { createDrawerAsync, createModelAsync } from "@/core/dialog";
	import DealListDialog from "@/pages/modules/alarm-center/history-alarm/dialog/dealListDialog.vue";
	import DetailDialog from "@/pages/modules/alarm-center/history-alarm/dialog/detailDialog.vue";
	import { areaTreeList2,getRedisDictList } from "@/api/common/common";
	import { getDetailsByDeviceIdNew, getHistoryPage, getWarnTypeDict, getHistoryList, getAlarmList } from "@/api/alarm-center/warnInfo";

	const formConfig: FormConfig = {
		inline: false,
		span: 4,
		expandSpan: 4,
		labelWidth:0,
		formItems: [
			{
				type: "ElCascader",
				value: "",
				prop: "logicalAreaIds",
				attrs: {
					placeholder: "请选择区域",
					clearable: true,
					showAllLevels: false
				},
				select: {
					type: "ElOption",
					label: "label",
					value: "id",
					listApi: async () => {
						const data = await areaTreeList2();
						for (let index = 0; index < data[0].children.length; index++) {
							delete data[0].children[index].children;
						}
						return data
					},
					list: []
				}
			},
			{
				type: "ElSelect",
				value: "",
				prop: "alarmCategory",
				attrs: {
					placeholder: "告警类型",
					clearable: true
				},
				select: {
					type: "ElOption",
					label: "name",
					value: "id",
					listApi: async () => {
						const data = await getAlarmList({});
						return data
					},
					list: []
				}
			},
			{
				type: "ElSelect",
				value: "",
				prop: "handleStatus",
				attrs: {
					placeholder: "处理状态",
					clearable: true
				},
				select: {
					type: "ElOption",
					label: "label",
					value: "value",
					list: [
						{
							value: "2",
							label: "上报"
						},
						{
							value: "3",
							label: "忽略"
						}
					]
				}
			},
			{
				type: "ElSelect",
				value: "",
				prop: "alarmLevel",
				attrs: {
					placeholder: "告警等级",
					clearable: true
				},
				select: {
					type: "ElOption",
					label: "dictLabel",
					value: "dictValue",
					listApi: async () => {
						const data = await getRedisDictList({ dictType: "alarmLevel" });
						return data
					},
					list: []
				}
			},
			{
				type: "ElDatePicker",
				value: "",
				prop: "time",
				attrs: {
					type: "daterange",
					rangeSeparator: "至",
					startPlaceholder: "告警开始日期",
					endPlaceholder: "告警结束日期",
					valueFormat: "YYYY-MM-DD"
				}
			},
		]
	};
	const { page, pageApi } = usePage({
		formConfig,
		title: "历史告警列表",
		pagination: {
			currentPage: 1,
			pageSize: 20,
			total: 15,
			background: true,
			layout: "prev, pager, next, jumper, ->, total",
			onChange(pageNo: number, pageSize: number) {
				if (page.pagination) {
					page.pagination.currentPage = pageNo;
					page.pagination.pageSize = pageSize;
					pageApi.pageList();
				}
			}
		},
		listApi: async (data) => {
			console.log('888888888888data', data)
			const [startTime, endTime ] = data.time
			delete data.time
			return await getHistoryList({ ...data, startTime, endTime });
		},
		tableConfig: {
			// 生成20条数据
			dataSource: [{}],
			columns: [
				{
					columnKey: "index",
					label: "序号",
					prop: "label",
					width: "80px",
					align: "center"
				},
				{
					label: "告警来源",
					prop: "deviceName",
					columnKey: "deviceName",
					minWidth: 200,
					showOverflowTooltip: true
				},
				{
					label: "告警类型",
					prop: "alarmCategoryName",
					columnKey: "alarmCategoryName",
					width: 140
				},
				{
					label: "告警等级",
					prop: "alarmLevelName",
					columnKey: "alarmLevelName",
					width: 100
				},
				{
					label: "时间",
					prop: "alarmTime",
					columnKey: "alarmTime",
					width: 180
				},
				{
					label: "区域",
					prop: "areaName",
					columnKey: "areaName",
					width: 180
				},
				{
					label: "事件内容",
					prop: "alarmContentStr",
					columnKey: "alarmContentStr",
					minWidth: 180,
					showOverflowTooltip: true
				},
				{
					label: "处理状态",
					columnKey: "handleStatus",
					prop: "handleStatus",
					width: 100
				},
				{
					label: "操作",
					columnKey: "actions",
					prop: "actions",
					width: 200
				}
			]
		}
	});

	// 查看处置记录
	const handleDealList = (row) => {
		createDrawerAsync(
			{ title: "处置记录", width: '960px', showNext: false, showConfirm:false },
			{},
			<DealListDialog id={row.id} />
		).then(() => {
			pageApi.pageList();
		});
	}
	const handleView = (row) => {
		createDrawerAsync(
			{ title: "详情", width: '960px', showNext: false },
			{},
			<DetailDialog id={row.id} row={row} />
		).then(() => {
			pageApi.pageList();
		});
	}
</script>
<style scope lang="scss">
.degree {
	display: inline-block;
	width: 88px;
	height: 26px;
	font-size: 14px !important;
	color: rgba(0, 0, 0, 0.65);
	line-height: 26px;
	border-radius: 4px 4px 4px 4px;
	box-sizing: border-box;
	text-align: center;
	i {
		display: inline-block;
		margin-right: 6px;
		width: 12px;
		height: 12px;
		border-radius: 50%;
		&.warning {
			background: #e6a23c;
		}
		&.success {
			background: #57BD94;
		}

		&.danger {
			background: #ED6A5E;
		}
	}
}
</style>

