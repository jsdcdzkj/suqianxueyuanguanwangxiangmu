<template>
	<BasePage v-bind="page">
		<template #tableActions="scope">
			<ElButton type="primary" link size="small" @click="handleDealList(scope.row)">查看处置记录</ElButton>
			<ElButton type="primary" link size="small" @click="handleView(scope.row)">详情</ElButton>
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

	const formConfig: FormConfig = {
		inline: false,
		span: 4,
		expandSpan: 6,
		labelWidth:0,
		formItems: [
			{
				type: "ElSelect",
				value: "",
				prop: "markResult",
				attrs: {
					placeholder: "请选择区域",
					clearable: true
				},
				select: {
					type: "ElOption",
					label: "label",
					value: "id",
					list: [
						{
							label: "区域1",
							id: "0"
						},
						{
							label: "区域2",
							id: "1"
						}
					]
				}
			},
			{
				type: "ElSelect",
				value: "",
				prop: "markResult",
				attrs: {
					placeholder: "告警类型",
					clearable: true
				},
				select: {
					type: "ElOption",
					label: "label",
					value: "id",
					list: [
						{
							label: "区域1",
							id: "0"
						},
						{
							label: "区域2",
							id: "1"
						}
					]
				}
			},
			{
				type: "ElSelect",
				value: "",
				prop: "markResult",
				attrs: {
					placeholder: "处理状态",
					clearable: true
				},
				select: {
					type: "ElOption",
					label: "label",
					value: "id",
					list: [
						{
							label: "区域1",
							id: "0"
						},
						{
							label: "区域2",
							id: "1"
						}
					]
				}
			},
			{
				type: "ElSelect",
				value: "",
				prop: "markResult",
				attrs: {
					placeholder: "告警等级",
					clearable: true
				},
				select: {
					type: "ElOption",
					label: "label",
					value: "id",
					list: [
						{
							label: "区域1",
							id: "0"
						},
						{
							label: "区域2",
							id: "1"
						}
					]
				}
			},
			{
				type: "ElDatePicker",
				value: "",
				prop: "time",
				attrs: {
					type: "daterange",
					rangeSeparator: "至",
					startPlaceholder: "开始日期",
					endPlaceholder: "结束日期",
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
		tableConfig: {
			// 生成20条数据
			dataSource: [{}],
			columns: [
				{
					label: "告警来源",
					prop: "name",
					columnKey: "desc",
				},
				{
					label: "告警类型",
					prop: "path",
					columnKey: "desc",
					width: 200
				},
				{
					label: "告警等级",
					prop: "icon",
					columnKey: "desc",
					width: 200
				},
				{
					label: "时间",
					prop: "desc",
					columnKey: "desc",
					width: 180
				},
				{
					label: "区域",
					prop: "icon",
					columnKey: "desc",
				},
				{
					label: "事件区域",
					prop: "desc",
					columnKey: "desc",
				},
				{
					label: "告警状态",
					columnKey: "desc",
					prop: "desc",
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
			{ title: "处置记录", width: '960px', showNext: false },
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
			<DetailDialog id={row.id} />
		).then(() => {
			pageApi.pageList();
		});
	}
</script>
<style>
</style>
