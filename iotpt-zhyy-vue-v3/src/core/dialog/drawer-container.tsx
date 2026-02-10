import { defineComponent, inject, onMounted, provide, reactive, ref, type Ref, type VNode } from "vue";
import { array, bool, string } from "vue-types";
import type { JSX } from "vue/jsx-runtime";
import { ElButton, ElIcon, ElCheckbox, type FormInstance, ElDivider } from "element-plus";
import { ProviderSy } from "./dialog-container";
import { debounce } from "lodash-es";

type DoneProp = (type: number) => any | Promise<any>;

type ApplyFuncItem = <T>(names?: any, options?: any) => Promise<T>;

const initProps = () => ({
	showCancel: bool().def(true),
	showConfirm: bool().def(true),
	showZanCun: bool().def(false),
	confirmText: string().def("确认"),
	cancelText: string().def("取消"),
	zancunText: string().def("暂存"),
	zancunType: string().def("primary"),
	showApprove: bool().def(false),
	approveText: string().def("一键审批"),
	approveType: string().def("primary"),
	nextNotRest: array<string>().def([]),
	heihgt: string().def("100%"),
	showNext: bool().def(false),
	noEvent: bool().def(false)
});
const empty = () => {};

export default defineComponent({
	props: initProps(),
	emits: ["cancel", "confirm", "zancun"],
	setup(props, { slots, emit }) {
		const applayFunc: FormInstance[] = [];
		const loading = ref(false);
		const isNext = ref(true);
		const pFormData = reactive<{
			applayFunc: ApplyFuncItem[];
			loading: boolean;
			done: DoneProp;
		}>({
			applayFunc: [],
			loading: false,
			done: empty
		});

		const registerFormInstrance = (instance: FormInstance) => {
			applayFunc.push(instance);
		};

		const registerFormDone = (done: DoneProp) => {
			pFormData.done = done;
		};

		const handlerCancel = () => {
			if (loading.value) return;
			emit("cancel");
		};

		const handlerConfirm = async () => {
			if (loading.value) return;
			loading.value = true; // 立即上锁

			try {
				if (applayFunc.length > 0) {
					if (props.noEvent) {
						loading.value = false;
					}
					const runAll = await Promise.all(applayFunc.map((func) => func.validate()));
					if (!runAll.every(Boolean)) throw new Error("验证失败");

					const result = await pFormData.done(1); // 确保完全等待异步完成

					if (props.showNext && isNext.value) {
						applayFunc.forEach((func) => {
							if (props.nextNotRest.length == 0) {
								func.resetFields();
							} else {
								const list: string[] = [];
								func.fields.forEach((field) => {
									if (!props.nextNotRest.includes(field.prop as string)) {
										list.push(field.prop as string);
									}
								});
								func.resetFields(list);
							}
						});
					} else {
						emit("confirm", result);
					}
					if (props.noEvent) {
						loading.value = false;
					}
				} else {
					loading.value = false;
					const result = await pFormData.done(); // 确保完全等待异步完成
					emit("confirm", result);
				}
			} catch (e) {
				console.error("操作失败:", e);
			} finally {
				loading.value = false; // 统一在此解锁
			}
		};

		const handlerZanCun = async () => {
			if (loading.value) return;
			loading.value = true; // 立即上锁

			try {
				if (applayFunc.length > 0) {
					if (props.noEvent) {
						loading.value = false;
					}
					const runAll = await Promise.all(applayFunc.map((func) => func.validate()));
					if (!runAll.every(Boolean)) throw new Error("验证失败");

					const result = await pFormData.done(2); // 确保完全等待异步完成

					if (props.showNext && isNext.value) {
						applayFunc.forEach((func) => {
							if (props.nextNotRest.length == 0) {
								func.resetFields();
							} else {
								const list: string[] = [];
								func.fields.forEach((field) => {
									if (!props.nextNotRest.includes(field.prop as string)) {
										list.push(field.prop as string);
									}
								});
								func.resetFields(list);
							}
						});
					} else {
						emit("zancun", result);
					}
				} else {
					loading.value = false;
					const result = await pFormData.done(); // 确保完全等待异步完成
					emit("zancun", result);
				}
			} catch (e) {
				console.error("操作失败:", e);
			} finally {
				loading.value = false; // 统一在此解锁
			}
		};

		const handlerApprove = async () => {
			if (loading.value) return;
			loading.value = true; // 立即上锁

			try {
				if (applayFunc.length > 0) {
					if (props.noEvent) {
						loading.value = false;
					}
					const runAll = await Promise.all(applayFunc.map((func) => func.validate()));
					if (!runAll.every(Boolean)) throw new Error("验证失败");

					const result = await pFormData.done(3); // 确保完全等待异步完成

					if (props.showNext && isNext.value) {
						applayFunc.forEach((func) => {
							if (props.nextNotRest.length == 0) {
								func.resetFields();
							} else {
								const list: string[] = [];
								func.fields.forEach((field) => {
									if (!props.nextNotRest.includes(field.prop as string)) {
										list.push(field.prop as string);
									}
								});
								func.resetFields(list);
							}
						});
					} else {
						emit("approve", result);
					}
				} else {
					loading.value = false;
					const result = await pFormData.done(); // 确保完全等待异步完成
					emit("approve", result);
				}
			} catch (e) {
				console.error("操作失败:", e);
			} finally {
				loading.value = false; // 统一在此解锁
			}
		};
		const debouncedConfirm = debounce(handlerConfirm, 500, { leading: true, trailing: false });
		// 暂存
		const debouncedZanCun = debounce(handlerZanCun, 500, { leading: true, trailing: false });
		// 一键审批
		const debouncedApprove = debounce(handlerApprove, 500, { leading: true, trailing: false });
		// 向子组件传递绑定函数
		provide(ProviderSy, { registerFormInstrance, registerFormDone });

		return () => {
			const container = slots.default?.() as unknown as VNode | null;
			return (
				<div class="drawer-container">
					{/* <Close onCancel={handlerCancel} /> */}
					<Content content={container}></Content>
					<ElDivider class="m-t-0px m-b-0px" />
					<Footer
						showCancel={props.showCancel}
						showConfirm={props.showConfirm}
						noEvent={props.noEvent}
						confirmText={props.confirmText}
						zancunText={props.zancunText}
						zancunType={props.zancunType}
						showNext={props.showNext}
						showZanCun={props.showZanCun}
						showApprove={props.showApprove}
						approveType={props.approveType}
						approveText={props.approveText}
						isNext={isNext}
						loading={loading.value}
						onCancel={handlerCancel}
						onConfirm={debouncedConfirm}
						onZanCun={debouncedZanCun}
						onApprove={debouncedApprove}
					></Footer>
				</div>
			);
		};
	}
});
// 弹框主体内容
const Content = (props: { content: VNode | null }) => {
	return <div class="drawer-container-main">{props.content}</div>;
};

type footer = (props: {
	showCancel: boolean;
	cancelText: string;
	showConfirm: boolean;
	noEvent: boolean;
	confirmText: string;
	showNext: boolean;
	showZanCun: boolean;
	zancunText: string;
	zancunType: string;
	showApprove: boolean;
	approveText: string;
	approveType: string;
	isNext: Ref<boolean>;
	loading: boolean;
	onCancel: (e: MouseEvent) => void;
	onConfirm: (e: MouseEvent) => void;
	onZanCun: (e: MouseEvent) => void;
	onApprove: (e: MouseEvent) => void;
}) => JSX.Element;
const Footer: footer = (props) => {
	// 取消按钮
	const cancel = props.showCancel ? (
		<ElButton type="default" key="cancel" loading={props.loading} onClick={props.onCancel}>
			{props.cancelText || "取消"}
		</ElButton>
	) : null;
	// 确认按钮
	const confirm = props.showConfirm ? (
		<ElButton type="primary" loading={props.loading} key="confirm" onClick={props.onConfirm}>
			{props.confirmText || "确认"}
		</ElButton>
	) : null;

	const zancun = props.showZanCun ? (
		<ElButton type={props.zancunType} loading={props.loading} key="confirm" onClick={props.onZanCun}>
			{props.zancunText || "暂存"}
		</ElButton>
	) : null;
	//一键审批
	console.log(props.showApprove);
	const approve = props.showApprove ? (
		<ElButton type={props.approveType} loading={props.loading} key="confirm" onClick={props.onApprove}>
			{props.approveText || "一键审批"}
		</ElButton>
	) : null;

	if (zancun == null && confirm == null && approve == null) return null;
	return (
		<div class="drawer-container-footer">
			<div>
				{props.showNext ? (
					<ElCheckbox v-model:modelValue={props.isNext.value}>是否添加下一个</ElCheckbox>
				) : null}
			</div>
			<div>{[cancel, zancun, confirm, approve]}</div>
		</div>
	);
};
