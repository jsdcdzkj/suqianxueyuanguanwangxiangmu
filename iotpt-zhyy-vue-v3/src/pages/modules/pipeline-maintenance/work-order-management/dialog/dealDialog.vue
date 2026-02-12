<template>
	<div>
		<div class="base-title">上报信息</div>
		<ElDescriptions :column="2" border label-width="120px">
			<ElDescriptionsItem label="标题" label-class-name="my-label">{{ detail.title || "--" }}</ElDescriptionsItem>
			<ElDescriptionsItem label="来源" label-class-name="my-label">{{
				detail.sourceName || "--"
			}}</ElDescriptionsItem>
			<ElDescriptionsItem label="严重程度" label-class-name="my-label">{{
				detail.levelsName || "--"
			}}</ElDescriptionsItem>
			<ElDescriptionsItem label="上报人" label-class-name="my-label">{{
				detail.userName || "--"
			}}</ElDescriptionsItem>
			<ElDescriptionsItem label="上报时间" label-class-name="my-label">{{
				detail.reportingTime || "--"
			}}</ElDescriptionsItem>
			<ElDescriptionsItem label="区域名称" v-if="4 != detail.source" label-class-name="my-label">{{
				detail.areaName || "--"
			}}</ElDescriptionsItem>
			<ElDescriptionsItem label="异常设备" v-if="4 != detail.source" label-class-name="my-label">
				<span v-if="detail.deviceName == '' || detail.deviceName == null">-</span>
				<span v-else>{{ detail.deviceName }}</span>
			</ElDescriptionsItem>
			<ElDescriptionsItem label="内容" label-class-name="my-label">{{ detail.notes || "--" }}</ElDescriptionsItem>
			<ElDescriptionsItem label="异常图片" v-if="4 != detail.source" label-class-name="my-label">
				<span v-if="imageUrls.length == 0">-</span>
				<div v-else>
					<ElImage
						v-for="item in imageUrls"
						:key="item.id"
						:src="item"
						:preview-src-list="imageUrls"
						style="width: 100px; height: 100px; margin-right: 10px"
					></ElImage>
				</div>
			</ElDescriptionsItem>
		</ElDescriptions>
		<template v-if="detail.serviceType">
			<div class="base-title m-t-12px">服务类型</div>
			<ElDescriptions :column="2" border label-width="120px">
				<ElDescriptionsItem label="任务类型" label-class-name="my-label">{{
					detail.serviceType == 2 ? "有偿服务" : "公区服务" || "--"
				}}</ElDescriptionsItem>
			</ElDescriptions>
		</template>
		<div class="base-title m-t-12px" v-if="assigns.length > 0">指派信息</div>
		<ElDescriptions :column="2" border label-width="120px" v-for="(assign, index) in assigns" :key="index">
			<ElDescriptionsItem label="任务类型" label-class-name="my-label">{{
				assign.taskTypeName || "--"
			}}</ElDescriptionsItem>
			<ElDescriptionsItem label="紧急程度" label-class-name="my-label">{{
				assign.urgencyName || "--"
			}}</ElDescriptionsItem>
			<ElDescriptionsItem label="截止时间" label-class-name="my-label">{{
				assign.deadline || "--"
			}}</ElDescriptionsItem>
			<ElDescriptionsItem label="任务组" label-class-name="my-label">{{
				assign.teamGroupsName || "--"
			}}</ElDescriptionsItem>
			<ElDescriptionsItem label="备注" label-class-name="my-label">{{ assign.notes || "--" }}</ElDescriptionsItem>
		</ElDescriptions>
		<!-- 耗材类目 -->
		<template v-if="detail.consumables && detail.consumables.length">
			<div class="base-title m-t-12px">耗材类目</div>
			<ElTable border :data="detail.consumables">
				<ElTableColumn label="序号" type="index" width="80px" align="center"></ElTableColumn>
				<ElTableColumn label="耗材名称" align="center" prop="consumableName"></ElTableColumn>
				<ElTableColumn label="金额（元）" align="center" prop="money"></ElTableColumn>
			</ElTable>
		</template>
		<div class="base-title m-t-12px">处理信息</div>
		<ElForm ref="formInstance" :model="form" :rules="rules" label-width="90px" class="self-form-task" label-position="left">
            <ElRow :gutter="20">
				<ElCol :span="24">
					<ElFormItem label="指派处理人">
						<ElInput v-model="assignUser" type="text" readonly autocomplete="off"></ElInput>
					</ElFormItem>
				</ElCol>
				<ElCol :span="24">
					<ElFormItem label="处理信息" prop="crunch">
						<ElInput v-model="form.crunch" type="textarea" autocomplete="off"></ElInput>
					</ElFormItem>
				</ElCol>
				<ElCol :span="12">
            		<ElFormItem label="处理人" prop="handleId">
						<ElSelect v-model="form.handleId" clearable style="width: 100%">
							<ElOption
								v-for="item in groupUsers"
								:key="item.userId"
								:label="item.realName"
								:value="item.userId"
									></ElOption>
                        </ElSelect>
                    </ElFormItem>
                </ElCol>
                <ElCol :span="12">
                    <ElFormItem label="截至时间" prop="startTime">
                        <ElDatePicker
							style="width: 100%"
                            v-model="form.startTime"
                            value-format="YYYY-MM-DD HH:mm:ss"
                            type="datetime"
                        ></ElDatePicker>
                    </ElFormItem>
                </ElCol>
                <ElCol :span="12">
                    <ElFormItem label="协助人" prop="assistHandleIds" style="width: 100%">
                        <ElSelect v-model="form.assistHandleIds" clearable>
                            <ElOptionGroup
                                v-for="group in groupsList"
                                :key="group.label"
                                :label="group.label">
                                <ElOption
                                    v-for="item in group.options"
                                    :key="item.id"
                                    :label="item.realName"
                                    :value="item.groupId+'-'+item.userId">
                                </ElOption>
                            </ElOptionGroup>
                        </ElSelect>
                    </ElFormItem>
                </ElCol>
            </ElRow>
        </ElForm>
	</div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { number } from "vue-types";
import { tobeAssigned, teamGroupsAllList, teamGroupsList1, inspectionData, waitHandle } from "@/api/pipeline-maintenance/work-order";
import { useDialogForm } from "@/core/dialog/dialog-container";
import { getRedisDictList, selectMissionDictAll } from "@/api/common/common";
import { ElDescriptions, ElDescriptionsItem, ElTable, ElTableColumn, ElInput, ElForm, ElFormItem, ElSelect, ElOption, ElOptionGroup, ElDatePicker, ElMessage, ElRow,ElCol } from "element-plus";

const props = defineProps({
	id: number().def(0)
});
const rules = {
	crunch: [{ required: true, trigger: "blur", message: "请输入处理信息内容" }],
	handleId: [{ required: true, trigger: "blur", message: "请选择处理人" }],
	startTime: [{ required: true, trigger: "blur", message: "请选择处理时间" }]
}
const form = ref({
	handleId: "",
	assistHandleIds: [],
	startTime: "",
	crunch: ""
});
const { registerFormDone, formInstance } = useDialogForm();
registerFormDone(async () => {
	console.log("66666666", form.value);
	const valid = await formInstance.value?.validate();
	if (!valid) {
		throw '请填写完整处理信息'
	};
	let assistHandleIds = form.value.assistHandleIds.split('-')[1]
	console.log('assistHandleIds',assistHandleIds)
	const res = await waitHandle({ ...form.value, id: props.id, missionId: props.id, type: 2,assistHandleIds });
	return res;
});

// 班组数据
const groupsList = ref([]);
const getAllgroundList = () => {
	teamGroupsList1({}).then((res) => {
		console.log('9999999999999res', res)
		groupsList.value = [];
		// 先处理一下班组数据
		for (let key in res) {
			console.log('7777777777777key', key, res)
			res[key] = res[key].filter((val) => val.realName);
			if (res[key].length == 0) delete res[key];
		}
		for (let key in res) {
			groupsList.value.push({
				label: key,
				options: res[key]
			});
		}
	});
};
getAllgroundList();

// 详情页数据
const imageUrls = ref([]);
const detail = ref({});
const assigns = ref([]);
const assignUser = ref("");
const groupUsers = ref([]);
const imageSize = ref([]);

if (props.id) {
	inspectionData({ missionId: props.id }).then((res) => {
		var data = res;
		detail.value = data.bean; //上报信息
		assigns.value = data.assigns; //指派信息
		if (data.assignUser.length > 0) {
			//指派人员
			assignUser.value = data.assignUser.substring(0, data.assignUser.lastIndexOf("、"));
		}
		//获取处理人员
		groupUsers.value = data.groupUsers;
		var files = detail.value.fileList;
		imageUrls.value = [];
		for (const key in files) {
			//文件数据渲染
			if (Object.hasOwnProperty.call(files, key)) {
				const element = files[key];
				// imageUrls.value.push(process.env.VUE_APP_FILE_URL + element.fileUrl)
				imageUrls.value.push("/minio/downMinio?fileName=" + element.fileUrl);
			}
		}
		imageSize.value = imageUrls.value;
	});
}
</script>
<style lang="scss" scoped>
:deep(.my-label) {
	font-size: 14px;
	font-weight: 500;
	color: rgba(0, 0, 0, 0.85);
	background: #f8f8f8 !important;
}
.base-title {
	height: 40px;
	line-height: 40px;
	margin-bottom: 12px;
	background: #f8f8f8;
	border-radius: 4px 4px 4px 4px;
	padding: 0 12px;
	font-size: 16px;
	color: rgba(0, 0, 0, 0.85);
	margin-bottom: 12px;
}
.detail-type {
	font-size: 14px;
	font-weight: 500;
	color: rgba(0, 0, 0, 0.85);
	height: 40px;
	border: 1px solid rgba(0, 0, 0, 0.15);
	text-align: center;
	cursor: pointer;
	border-radius: 4px;
	margin-bottom: 12px;

	.type {
		flex: 1;
		line-height: 40px;
	}
	.activeType {
		color: #fff;
		background: #345bad;
	}
}
</style>
