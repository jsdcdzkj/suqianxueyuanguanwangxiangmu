<template>
	<PageContainer :safeArea="true" :screen="true" background="#F8F8F8">
		<FlexScroll :refresher-enabled="false" ref="flexContainer" style="position: relative;z-index: 99;">
			<template #header>
				<NavBar title="报事报修" show-back background="#4378E7" />
			</template>
			<view class="form-content">
				<RepairForm :id="id" :disabled="disabled" :initialData="initialData" @submit="handleSubmit"
					@showPermissionDialog="showPermissionDialog" />
			</view>
		</FlexScroll>
		<view class="bg"></view>
	</PageContainer>
</template>

<script setup>
	import PageContainer from "@/components/hh-page-container/hh-page-container.vue";
	import NavBar from "@/components/hh-nav-bar/hh-nav-bar.vue"
	import FlexScroll from "@/components/hh-flex-scroll.vue"
	import RepairForm from './components/RepairForm.vue';
	import {
		onLoad
	} from "@dcloudio/uni-app"
	import {
		ref,
		computed,

	} from "vue"
	const id = ref('');
	const disabled = computed(() => !!id.value);
	const tip = ref(null);
	const initialData = ref({});

	// 处理表单提交
	const handleSubmit = async (formData) => {
		try {
			uni.showLoading({
				title: '保存中...',
				icon: 'none'
			});

			// 调用保存API
			const result = await $API.operation.saveAppMission({
				source: 5,
				states: 1,
				...formData,
				areaId: formData.areaId.replace('area_', ''),
				userId: uni.getStorageSync('userInfo').id
			});

			if (result.code === 0) {
				uni.hideLoading();
				uni.showToast({
					title: '提交成功'
				});
				uni.$emit('addTodo');
				uni.navigateBack();
			}
		} catch (error) {
			console.error('提交失败：', error);
			uni.hideLoading();
		}
	};

	// 显示权限说明弹窗
	const showPermissionDialog = () => {
		tip.value.open();
	};

	// 关闭弹窗
	const closeDialog = () => {
		tip.value.close();
	};

	// 获取详情数据
	const getDetail = async (id) => {
		try {
			const res = await $API.operation.inspectionData({
				missionId: id
			});

			const {
				areaId,
				notes,
				levels,
				phone,
				title,
				fileList
			} = res.data.bean;
			initialData.value = {
				areaId: "area_" + areaId,
				reportRemark: notes,
				phone,
				title,
				urgency: {
					id: levels,
					value: typejjs.find(item => item.dictValue == levels).dictLabel
				}
			};

			uploadImages.value = fileList.map(item => ({
				name: item.fileName,
				url: `${$filePath}${item.fileUrl}`,
				extname: item.fileName
			}));
		} catch (error) {
			console.error('获取详情失败：', error);
		}
	};

	// 页面加载
	onLoad(async (options) => {
		if (options.id) {
			id.value = options.id;
			await getDetail(options.id);
		} else {
			initialData.value.phone = uni.getStorageSync('userInfo').phone || '';
		}
	})
</script>

<style lang="scss">
	.bg {
		width: 750rpx;
		height: 733rpx;
		background: linear-gradient(180deg, #4378E7 25%, rgba(67, 120, 231, 0) 100%);
		border-radius: 0rpx 0rpx 0rpx 0rpx;
		position: fixed;
		top: 0;
		left: 0;
		z-index: 1;
	}

	.form-content {
		width: 700rpx;

		background: #FFFFFF;
		box-shadow: 0rpx 2rpx 25rpx 0rpx rgba(32, 144, 225, 0.18);
		border-radius: 25rpx 25rpx 25rpx 25rpx;
		margin: 25rpx;
		position: relative;
		z-index: 88;
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

			span {
				font-size: 30rpx !important;
			}
		}

		.txt-address .uni-forms-item__content {
			overflow-x: inherit;
		}
	}
</style>