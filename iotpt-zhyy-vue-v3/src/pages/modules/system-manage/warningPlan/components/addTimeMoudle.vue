<template>
	<div class="time-plan-template">
		<ElForm :model="form" label-width="100px" inline :rules="rules" ref="formLine">
			<ElFormItem label="模板名称" prop="name">
				<ElInput v-model="form.name" style="width: 340px" placeholder="请输入模板名称" />
			</ElFormItem>

			<ElFormItem label="复制模板" prop="copyFrom" style="position: relative">
				<ElCheckbox v-model="isCopy" class="copy"></ElCheckbox>
				<ElSelect
					v-model="form.copyFrom"
					:disabled="!isCopy"
					placeholder="请选择"
					style="width: 340px"
					@change="handleChange"
				>
					<ElOption v-for="item in moudleList" :key="item.id" :label="item.name" :value="item.id" />
				</ElSelect>
			</ElFormItem>
		</ElForm>
		<div class="time-wrap">
			<div class="schedule-wrapper">
				<div class="header-row">
					<div class="cell label"></div>
					<div class="item" v-for="(h, index) in 25" :key="index" :style="index == 24 ? 'border:0' : ''">
						<span class="hour">{{ index }}</span>
					</div>
				</div>

				<div class="day-row" v-for="(item, dIdx) in days" :key="dIdx">
					<div class="cell label">{{ item.name }}</div>
					<div class="cell" v-for="(item, index) in item.list" :key="index" :class="{ selected: item }"></div>
				</div>
			</div>
			<div class="time-btn">
				<div @click="clearAll" class="clear-btn">
					<img src="@/assets/images/setting/warningPlan/icon-clear.png" alt="" />
					清空
				</div>
				<div class="set-btn" @click="handleSet">
					<i class="ri-settings-2-fill"></i>
				</div>
			</div>
		</div>
	</div>
</template>

<script setup lang="tsx">
	import { ref, onMounted, watch, computed } from "vue";
	import { ElMessage, ElForm, ElFormItem, ElCheckbox, ElSelect, ElOption, ElInput } from "element-plus";
	import { alarmPlanTimePage, getById, saveTime } from "@/api/setting/warnInfo";
	import { useDialogForm } from "@/core/dialog/dialog-container";
	import { createModelAsync } from "@/core/dialog";
	import SettingTime from "./settingTime.vue";

	const props = defineProps({
		row: {
			type: Object,
			default: () => ({})
		}
	});

	const formLine = ref(null);
	const form = ref({
		name: "",
		copyFrom: ""
	});
	const rules = {
		name: [{ required: true, trigger: "change", message: "请输入" }],
		copyFrom: [{ required: false, trigger: "change", message: "请选择" }]
	};
	const days = ref([
		{ name: "周一", list: new Array(96).fill(false) },
		{ name: "周二", list: new Array(96).fill(false) },
		{ name: "周三", list: new Array(96).fill(false) },
		{ name: "周四", list: new Array(96).fill(false) },
		{ name: "周五", list: new Array(96).fill(false) },
		{ name: "周六", list: new Array(96).fill(false) },
		{ name: "周日", list: new Array(96).fill(false) }
	]);
	const selected = ref(new Set());
	const moudleList = ref([]);
	const timeList = ref([]);
	const isCopy = ref(false);
	const weeks = [
		"mondayList",
		"tuesdayList",
		"wednesdayList",
		"thursdayList",
		"fridayList",
		"saturdayList",
		"sundayList"
	];

	const { registerFormDone, formInstance } = useDialogForm();

	watch(isCopy, (val) => {
		rules.copyFrom[0].required = val;
	});

	const currentTime = computed(() => {
		const now = new Date();
		return now.getHours().toString().padStart(2, "0") + ":" + now.getMinutes().toString().padStart(2, "0");
	});

	const currentTimeLeft = computed(() => {
		const now = new Date();
		const minutes = now.getHours() * 60 + now.getMinutes();
		return (minutes * 10) / 10; // 1格10px
	});

	registerFormDone(async () => {
		try {
			await formInstance.value?.validate();
			// 组装数据
			let mondayList, tuesdayList, fridayList, saturdayList, thursdayList, wednesdayList, sundayList;
			timeList.value.map((val, index) => {
				if (val) {
					if (index == 0) {
						mondayList = val.time.map((item, i) => ({
							start: item[0],
							end: item[1],
							sort: i + 1
						}));
					}
					if (index == 1) {
						tuesdayList = val.time.map((item, i) => ({
							start: item[0],
							end: item[1],
							sort: i + 1
						}));
					}
					if (index == 2) {
						wednesdayList = val.time.map((item, i) => ({
							start: item[0],
							end: item[1],
							sort: i + 1
						}));
					}
					if (index == 3) {
						thursdayList = val.time.map((item, i) => ({
							start: item[0],
							end: item[1],
							sort: i + 1
						}));
					}
					if (index == 4) {
						fridayList = val.time.map((item, i) => ({
							start: item[0],
							end: item[1],
							sort: i + 1
						}));
					}
					if (index == 5) {
						saturdayList = val.time.map((item, i) => ({
							start: item[0],
							end: item[1],
							sort: i + 1
						}));
					}
					if (index == 6) {
						sundayList = val.time.map((item, i) => ({
							start: item[0],
							end: item[1],
							sort: i + 1
						}));
					}
				}
			});
			const params = {
				name: form.value.name,
				mondayList,
				tuesdayList,
				fridayList,
				saturdayList,
				thursdayList,
				wednesdayList,
				sundayList,
				id: form.value.id
			};
			await saveTime(params);
			ElMessage.success(form.value.id ? "修改成功！" : "新增成功!");
			return true;
		} catch (error) {
			console.error("保存失败:", error);
			ElMessage.error("保存失败，请检查输入信息");
			return false;
		}
	});

	onMounted(() => {
		//获取模板的数据
		alarmPlanTimePage({}).then((res) => {
			moudleList.value = res.records;
		});
		if (props.row?.id) {
			form.value.name = props.row.name;
			form.value.id = props.row.id;
			handleChange(props.row.id);
		}
	});

	const clearAll = () => {
		days.value = [
			{ name: "周一", list: new Array(96).fill(false) },
			{ name: "周二", list: new Array(96).fill(false) },
			{ name: "周三", list: new Array(96).fill(false) },
			{ name: "周四", list: new Array(96).fill(false) },
			{ name: "周五", list: new Array(96).fill(false) },
			{ name: "周六", list: new Array(96).fill(false) },
			{ name: "周日", list: new Array(96).fill(false) }
		];
		timeList.value = [];
	};

	const handleSet = () => {
		createModelAsync(
			{
				title: "设置时间段",
				width: "800px",
				closeOnClickModal: false,
				closeOnPressEscape: false
			},
			{},
			<SettingTime
				timeList={timeList.value}
				onSave={(val) => {
					handleAddTime(val);
				}}
			/>
		);
	};

	const handleAddTime = (val) => {
		clearAll();
		timeList.value = val;
		setTimeShow();
	};

	const setTimeShow = () => {
		timeList.value.map((val, i) => {
			val.time.map((arr) => {
				days.value[i].list = days.value[i].list.map((item, index) => {
					const start = arr[0].split(":")[0] * 4 + Math.floor(arr[0].split(":")[1] / 15);
					const end = arr[1].split(":")[0] * 4 + Math.ceil(arr[1].split(":")[1] / 15);
					if (start <= index && index < end) {
						item = true;
					}
					return item;
				});
			});
		});
	};

	const handleChange = (val) => {
		timeList.value = [];
		clearAll();
		getById({ id: val }).then((res) => {
			const { tuesdayList, fridayList, mondayList, saturdayList, thursdayList, wednesdayList, sundayList } =
				res.data || {};
			//转化成前端需要的数据结构
			if (mondayList) {
				timeList.value[0] = {
					time: mondayList.map((val) => [val.start, val.end])
				};
			}
			if (tuesdayList) {
				timeList.value[1] = {
					time: tuesdayList.map((val) => [val.start, val.end])
				};
			}
			if (wednesdayList) {
				timeList.value[2] = {
					time: wednesdayList.map((val) => [val.start, val.end])
				};
			}
			if (thursdayList) {
				timeList.value[3] = {
					time: thursdayList.map((val) => [val.start, val.end])
				};
			}
			if (fridayList) {
				timeList.value[4] = {
					time: fridayList.map((val) => [val.start, val.end])
				};
			}
			if (saturdayList) {
				timeList.value[5] = {
					time: saturdayList.map((val) => [val.start, val.end])
				};
			}
			if (sundayList) {
				timeList.value[6] = {
					time: sundayList.map((val) => [val.start, val.end])
				};
			}
			setTimeShow();
		});
	};
</script>

<style scoped lang="scss">
	.copy {
		position: absolute;
		left: -94px;
	}
	.time-wrap {
		display: flex;
		background: linear-gradient(180deg, #ffffff 0%, #ffffff 100%);
		box-shadow: 0px 0px 4px 0px rgba(46, 119, 221, 0.1);
		border-radius: 12px 12px 12px 12px;
		border: 1px solid rgba(0, 0, 0, 0.06);

		.time-btn {
			display: flex;
			flex-direction: column;
			margin-left: -24px;
			position: relative;
			z-index: 10;

			.clear-btn {
				width: 48px;
				height: 48px;
				background: #e8f4fe;
				border-radius: 8px 8px 8px 8px;
				border: 1px solid rgba(0, 0, 0, 0.06);
				display: flex;
				flex-direction: column;
				justify-content: center;
				align-items: center;
				color: #1e95f4;
				margin: 24px 0 160px;
				cursor: pointer;

				img {
					width: 13px;
					height: 13px;
					margin-bottom: 6px;
				}
			}

			.set-btn {
				width: 48px;
				height: 48px;
				display: flex;
				justify-content: center;
				align-items: center;
				background: #f3f3f3;
				border-radius: 8px 8px 8px 8px;
				border: 1px solid rgba(0, 0, 0, 0.06);
				color: rgba(0, 0, 0, 0.45);
				font-size: 20px;
				cursor: pointer;
			}
		}
	}
	.schedule-wrapper {
		position: relative;
		overflow: auto;
		margin-top: 20px;
		padding-bottom: 30px;
	}
	.header-row,
	.day-row {
		display: flex;
		height: 56px;
	}

	.header-row {
		height: 24px;
		.cell,
		.label {
			line-height: 24px;
			height: 24px;
		}
		.item {
			width: 40px;
			height: 24px;
			border-bottom: 1px solid #f0f0f0;

			.hour {
				margin-left: -7px;
				position: relative;
				width: 16px;
				display: flex;
				justify-content: center;

				&::before {
					content: "";
					position: absolute;
					bottom: -9px;
					left: 6px;
					width: 1px;
					height: 6px;
					background: #d0d0d0;
				}
			}
		}
	}
	.cell {
		width: 10px;
		height: 56px;
		border-right: 1px solid #f0f0f0;
		border-bottom: 1px solid #f0f0f0;
		box-sizing: border-box;
		cursor: pointer;
	}
	.cell.label {
		width: 60px;
		text-align: center;
		line-height: 56px;
		border: 0;
		border-right: 1px solid #f0f0f0;
	}
	.cell.selected {
		background-color: #409eff;
	}
</style>
