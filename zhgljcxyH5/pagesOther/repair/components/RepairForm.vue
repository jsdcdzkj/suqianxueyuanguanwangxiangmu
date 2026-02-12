<template>
	<uni-forms ref="baseForm" :modelValue="formData" :rules="rules" :border="true" label-width="160rpx"
		label-position="left">
		<c-block customClass="yuan20">
			<!-- 工单标题 -->
			<uni-forms-item label="工单标题" name="title" required :rules="[{ required: true, errorMessage: '请输入工单标题' }]">
				<view style="margin-top: 14rpx;">
					<input type="text" v-model="formData.title" placeholder="请输入标题或选择下方默认标题" :disabled="disabled" />
					<view class="input-list">
						<view class="input-item" @click="formData.title = '报修'">报修</view>
						<view class="input-item" @click="formData.title = '保洁'">保洁</view>
						<view class="input-item" @click="formData.title = '秩序'">秩序</view>
						<view class="input-item" @click="formData.title = '其他'">其他</view>
					</view>
				</view>
			</uni-forms-item>

			<!-- 紧急程度 -->
			<uni-forms-item label="紧急程度" :name="['urgency', 'id']" required
				:rules="[{ required: true, errorMessage: '请选择紧急程度' }]">
				<view class="select-list">
					<view @click="selectTypejj(item, index)" :class="['select-item', typejjIndex == index ?'active':'']"
						v-for="(item,index) in typejjs" :key="index">{{item.dictLabel}}</view>
				</view>
			</uni-forms-item>

			<!-- 联系方式 -->
			<uni-forms-item label="联系方式" name="phone" required :rules="[{ required: true, errorMessage: '请输入联系方式' }]">
				<input type="number" style="height: 72rpx;line-height: 72rpx;" v-model="formData.phone"
					placeholder="请输入联系方式" :disabled="disabled" />
			</uni-forms-item>

			<!-- 问题地点 -->
			<uni-forms-item label="问题地点" name="areaId" required :rules="[{ required: true, errorMessage: '请输入问题地点' }]"
				class="txt-address">
				<uni-data-picker placeholder="请选择问题地点" popup-title="请选择" :localdata="areaList" :disabled="disabled"
					v-model="formData.areaId" @change="onchange" :clearIcon="!disabled" :readonly="disabled" />
			</uni-forms-item>

			<view class="line"></view>

			<!-- 问题描述 -->
			<uni-forms-item label="" name="reportRemark" required :rules="[{ required: true, errorMessage: '请输入问题描述' }]"
				label-width="0">
				<view class="tips">问题描述</view>
				<uni-easyinput type="textarea" autoHeight v-model="formData.reportRemark" placeholder="问题描述"
					:disabled="disabled" />
			</uni-forms-item>

			<!-- 图片上传 -->
			<view class="container-item">
				<view class="photolist">
					<view class="form-item" @click="handleUpImg">
						<uni-file-picker limit="6" mode="grid" :sourceType="['album', 'camera']"
							v-if="!props.id || (props.disabled && uploadImages.length > 0)"
							:sizeType="['original', 'compressed']" v-model="uploadImages"
							:image-styles="{ width: '162.5rpx', height: '162.5rpx' }" @select="uploadSelect"
							:del-icon="!props.disabled" file-mediatype="image" :auto-upload="false"
							:readonly="props.disabled" title="最多选择6张图片" @change="handleChange" />
					</view>
				</view>
			</view>

			<!-- 提交按钮 -->
			<view class="btnBox" v-if="!props.id" style="margin-top: 20rpx;">
				<button class="btntype" @click="handleSubmit" :loading="submitLoading">保存</button>
			</view>
		</c-block>
	</uni-forms>
</template>

<script setup>
	import {
		ref,
		computed,
		watch
	} from 'vue';
	import cBlock from '@/components/c-block.vue';

	const props = defineProps({
		id: {
			type: String,
			default: ''
		},
		disabled: {
			type: Boolean,
			default: false
		},
		initialData: {
			type: Object,
			default: () => ({})
		}
	});

	const emit = defineEmits(['submit', 'update:modelValue']);

	// 表单数据
	const formData = ref({
		title: '',
		phone: '',
		address: '',
		areaId: '',
		reportType: {
			id: '',
			value: ''
		},
		urgency: {
			id: '',
			value: ''
		},
		reportRemark: ''
	});

	// 监听props.initialData变化，更新表单数据
	watch(() => props.initialData, (newVal) => {
		if (newVal) {
			Object.assign(formData.value, newVal);
		}
	}, {
		deep: true
	});

	// 表单ref
	const baseForm = ref(null);

	// 紧急程度相关
	const typejjIndex = ref(-1);
	const typejjs = ref([]);

	// 图片上传相关
	const uploadImages = ref([]);
	const submitLoading = ref(false);

	// 地区列表
	const areaList = ref([]);

	// 表单验证规则
	const rules = ref({});

	// 选择紧急程度
	const selectTypejj = (item, index) => {
		typejjIndex.value = index;
		const {
			dictValue,
			dictLabel
		} = item;
		formData.value.urgency.id = dictValue;
		formData.value.urgency.value = dictLabel;
	};

	// 地址选择变化
	const onchange = (e) => {
		formData.value.areaId = e.detail.value[2].value || '';
	};

	// 处理图片上传
	const handleUpImg = () => {
		// #ifdef APP-PLUS
		if (plus.os.name == 'Android') {
			if (plus.navigator.checkPermission('android.permission.CAMERA') === 'undetermined') {
				emit('showPermissionDialog');
			} else {
				uploadSelect();
			}
		} else {
			if (checkPermission()) {
				uploadSelect();
			} else {
				uni.showToast({
					title: '暂无权限'
				});
			}
		}
		// #endif
	};

	// 选择图片
	const uploadSelect = ({
		tempFiles
	}) => {
		uploadImages.value = uploadImages.value.concat(
			tempFiles.map((file) => ({
				name: file.name,
				url: file.url,
				extname: file.extname
			}))
		);
	};

	// 处理图片变化
	const handleChange = (e) => {
		if (e.detail.type === 'remove') {
			console.log('取消操作');
		} else if (e.detail.type === 'select') {
			console.log('选择文件', e.detail.tempFiles);
		}
	};

	// 提交表单
	const handleSubmit = async () => {
		try {
			await baseForm.value.validate();
			submitLoading.value = true;

			// 处理图片上传
			const fileList = await uploadFile();

			// 发送提交事件
			emit('submit', {
				...formData.value,
				fileList: fileList.map(item => item.data)
			});
		} catch (err) {
			console.log('表单验证错误：', err);
		} finally {
			submitLoading.value = false;
		}
	};

	// 上传文件
	const uploadFile = async () => {
		try {
			return await Promise.all(
				uploadImages.value.map((file) =>
					// 这里需要替换为实际的上传API调用
					uploadAPI(file.url)
				)
			);
		} catch (e) {
			return [];
		}
	};

	// 检查权限
	const checkPermission = () => {
		// 权限检查逻辑
		return true;
	};

	// 暴露方法给父组件
	defineExpose({
		validate: () => baseForm.value.validate(),
		resetFields: () => baseForm.value.resetFields()
	});
</script>

<style lang="scss" scoped>
	.yuan20 {
		border-radius: 24rpx;
		padding: 44rpx 36rpx;
	}

	.input-list {
		display: flex;
		gap: 10rpx;
		margin-top: 20rpx;
		justify-content: space-between;

		.input-item {
			border-radius: 12rpx;
			background: rgba(0, 0, 0, 0.04);
			color: rgba(0, 0, 0, 0.45);
			padding: 4rpx 24rpx;
			font-size: 24rpx;
			line-height: 40rpx;
		}
	}

	.select-list {
		display: flex;
		gap: 10rpx;
		margin-top: 11rpx;
		justify-content: space-between;
		align-items: center;

		.select-item {
			border-radius: 12rpx;
			background-color: #eee;
			color: #000;
			padding: 4rpx 24rpx;
			font-size: 24rpx;
			line-height: 40rpx;
			color: rgba(0, 0, 0, 0.45);

			&.active {
				background-color: #5187FF;
				color: #fff;
			}
		}
	}

	.line {
		width: 100%;
		height: 1rpx;
		background-color: rgba(0, 0, 0, 0.06);
		margin-top: 44rpx;
		margin-bottom: 44rpx;
		margin-left: -36rpx;
		width: calc(100% + 72rpx);
	}

	.tips {
		color: #606266;
		font-size: 28rpx;
		font-weight: bold;
		margin: 12rpx 0 32rpx 17rpx;
	}

	.btnBox {
		background: #fff;
	}

	:deep(.uni-forms-item--border) {
		border-top: none;
		padding-bottom: 20rpx;
		padding-top: 0;
	}

	:deep(.uni-forms-item__label) {
		font-weight: bold;
		font-size: 28rpx;
	}

	:deep(.input-value-border) {
		border: none;
	}

	:deep(.uni-easyinput__content) {
		background-color: #f8f8f8 !important;
	}

	:deep(.uni-easyinput__content) {
		background-color: #f8f8f8 !important;
	}

	:deep(.file-picker__box-content) {
		&.is-add {
			background-color: #f8f8f8;
			border: none;
		}
	}

	:deep(.uni-forms) {
		margin-top: 20rpx;

		.uni-forms-item--border {
			border-top: none;
			padding-bottom: 20rpx;
			padding-top: 0px;
		}

		.uni-forms-item .uni-forms-item__label .is-required {
			position: relative;
			left: 0;
		}

		.uni-forms-item__label {
			font-weight: bold;
			font-size: 28rpx;
		}




		.input-value-border {
			border: none;
		}

		.txt-address {
			.uni-forms-item__content {
				overflow-x: hidden;
			}
		}

		.input-value {
			padding: 0;
			font-size: 26rpx;
		}

		.txt-address .uni-forms-item__content {
			overflow-x: inherit;
		}
	}
</style>