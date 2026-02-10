<template>
	<div class="time-setting">
		<div style="max-height: 500px; overflow: auto">
			<ElForm
				:model="{ timeRanges }"
				label-position="top"
				inline
				ref="formLine"
				:rules="getTimeRangeRules()"
				style="border-bottom: 1px solid rgba(0, 0, 0, 0.06)"
			>
				<div v-for="(range, index) in timeRanges" :key="index" style="margin-bottom: 10px">
					<ElFormItem :label="`时间段${index + 1}`" :prop="`timeRanges.${index}.time`">
						<ElTimePicker
							style="width: 460px"
							is-range
							:key="index"
							v-model="range.time"
							format="HH:mm"
							value-format="HH:mm"
							range-separator="至"
							start-placeholder="开始时间"
							end-placeholder="结束时间"
						/>
						<ElButton
							type="success"
							size="small"
							style="margin-left: 12px"
							circle
							:icon="Plus"
							@click="addTimeRange"
						></ElButton>
						<ElButton
							type="danger"
							size="small"
							v-if="timeRanges.length > 1"
							circle
							:icon="Delete"
							@click="removeTimeRange(index)"
						></ElButton>
					</ElFormItem>
				</div>
			</ElForm>

			<div style="margin-top: 20px">
				<ElCheckbox v-model="selectAll" style="margin-bottom: 20px" @change="toggleAllDays">全部</ElCheckbox>
				<ElCheckboxGroup v-model="selectedDays">
					<ElCheckbox v-for="(day, index) in weekDays" :key="day" :label="index">{{ day }}</ElCheckbox>
				</ElCheckboxGroup>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts">
	import { ref, watch } from "vue";
	import { ElMessage, ElCheckbox, ElCheckboxGroup, ElForm, ElFormItem, ElTimePicker, ElButton } from "element-plus";
	import { Plus, Delete } from "@element-plus/icons-vue";
	import { useDialogForm } from "@/core/dialog/dialog-container";

	const props = defineProps({
		timeList: {
			type: Array,
			default: () => []
		}
	});

	const emit = defineEmits(["save"]);

	const formLine = ref(null);
	const timeRanges = ref([{ time: null }]);
	const selectedDays = ref([]);
	const selectAll = ref(false);
	const savedSchedules = ref([]);
	const weekDays = ["周一", "周二", "周三", "周四", "周五", "周六", "周日"];

	// 为每个时间段单独设置验证规则
	const getTimeRangeRules = () => {
		const rules = {};
		timeRanges.value.forEach((_, index) => {
			rules[`timeRanges.${index}.time`] = [{ required: true, trigger: "change", message: "请选择时间段" }];
		});
		return rules;
	};

	const { registerFormDone, formInstance } = useDialogForm();

	watch(
		() => props.timeList,
		(newVal) => {
			selectedDays.value = [];
			timeRanges.value = [];
			let fg = true;
			newVal.map((res, index) => {
				selectedDays.value.push(res.time ? index : "");
				if (res.time && fg) {
					res.time.map((val) => {
						timeRanges.value.push({ time: val });
					});
					fg = false;
				}
			});
			if (newVal.length == 0) {
				timeRanges.value = [{ time: null }];
			}
			selectAll.value = selectedDays.value.length == 7;
		}
	);

	const addTimeRange = () => {
		timeRanges.value.push({ time: null });
	};

	const removeTimeRange = (index) => {
		timeRanges.value.splice(index, 1);
	};

	const toggleAllDays = (val) => {
		selectedDays.value = val ? [0, 1, 2, 3, 4, 5, 6] : [];
	};

	registerFormDone(async () => {
		try {
			await formInstance.value?.validate();
			if (selectedDays.value.length == 0) {
				throw new Error("请至少选择一周中的一天!");
			}

			// 验证所有时间段
			for (let i = 0; i < timeRanges.value.length; i++) {
				if (!timeRanges.value[i].time) {
					throw new Error(`请选择时间段${i + 1}!`);
				}
			}

			for (const day of selectedDays.value) {
				const arr = [];
				for (const range of timeRanges.value) {
					if (range.time) {
						arr.push([range.time[0], range.time[1]]);
					}
				}
				savedSchedules.value[day] = {
					time: arr
				};
			}
			emit("save", savedSchedules.value);
			return true;
		} catch (error) {
			ElMessage.error(error.message || "保存失败，请检查输入信息");
			return false;
		}
	});
</script>
