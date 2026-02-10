import { defineComponent, inject, onMounted, provide, reactive, ref, type Ref, type VNode } from "vue";
import { array, bool, string } from "vue-types";
import type { JSX } from "vue/jsx-runtime";
import { Close as ElClise } from "@element-plus/icons-vue";
import { ElButton, ElIcon, ElCheckbox, type FormInstance, ElDivider } from "element-plus";
import { debounce } from "lodash-es";

type DoneProp = () => any | Promise<any>;

type ApplyFuncItem = <T>(names?: any, options?: any) => Promise<T>;

// 定义一个名为 initProps 的函数，用于初始化组件的属性
// 定义一个名为 initProps 的函数，用于初始化组件的属性
// 定义一个名为 showCancel 的属性，类型为布尔值，默认值为 true
const initProps = () => ({
	// 定义一个名为 showConfirm 的属性，类型为布尔值，默认值为 true
	showCancel: bool().def(true),
	// 定义一个名为 nextNotRest 的属性，类型为数组，数组元素为字符串，默认值为空数组
	showConfirm: bool().def(true),
	// 定义一个名为 heihgt 的属性，类型为字符串，默认值为 "100%"
	nextNotRest: array<string>().def([]),
	// 定义一个名为 isNext 的属性，类型为布尔值，默认值为 true
	isNextSel: bool().def(true),
	// 定义一个名为 showNext 的属性，类型为布尔值，默认值为 false
	heihgt: string().def("100%"),
	showNext: bool().def(false),
	// 定义一个名为 cancelText 的属性，类型为字符串，默认值为 "取消"
	cancelText: string().def("取消"),
	// 定义一个名为 confirmText 的属性，类型为字符串，默认值为 "确认"
	confirmText: string().def("确认"),
	bodyPadding: string().def("24px")
});
const empty = () => {};

export type DialogProvide = {
	registerFormInstrance: (instance?: FormInstance) => void;
	registerFormDone: (done: DoneProp) => any;
};
export interface Props {
	[key: string]: any;
}

export const ProviderSy = Symbol("dialog-form");

export const useDialogForm = () => {
	const formInstance = ref<FormInstance>();
	const dialogForm = inject(ProviderSy) as DialogProvide;

	onMounted(() => {
		if (formInstance.value) {
			dialogForm.registerFormInstrance(formInstance.value);
		}
	});

	return {
		formInstance,
		...dialogForm
	};
};

export default defineComponent({
	props: initProps(),
	emits: ["cancel", "confirm"],
	setup(props, { slots, emit }) {
		const applayFunc: FormInstance[] = [];
		const loading = ref(false);
		const isNext = ref(props.isNextSel); // 下一步是否被选中
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
					const runAll = await Promise.all(applayFunc.map((func) => func.validate()));
					if (!runAll.every(Boolean)) throw new Error("验证失败");

					const result = await pFormData.done(); // 确保完全等待异步完成

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
				} else {
					const result = await pFormData.done(); // 确保完全等待异步完成

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
				}
			} catch (e) {
				console.error("操作失败:", e);
			} finally {
				loading.value = false; // 统一在此解锁
			}
		};
		const debouncedConfirm = debounce(handlerConfirm, 500, { leading: true, trailing: false });

		// 向子组件传递绑定函数
		provide(ProviderSy, { registerFormInstrance, registerFormDone });

		return () => {
			const container = slots.default?.() as unknown as VNode | null;
			return (
				<div>
					{/* <Close onCancel={handlerCancel} /> */}
					<Content content={container} bodyPadding={props.bodyPadding}></Content>
					{props.showCancel == false && props.showConfirm == false && props.showNext == false ? null : (
						<ElDivider class="m-t-0px m-b-0px" />
					)}
					<Footer
						showCancel={props.showCancel}
						showConfirm={props.showConfirm}
						cancelText={props.cancelText}
						confirmText={props.confirmText}
						showNext={props.showNext}
						isNext={isNext}
						loading={loading.value}
						onCancel={handlerCancel}
						onConfirm={debouncedConfirm}
					></Footer>
				</div>
			);
		};
	}
});

// 关闭弹框
const Close = (props: { onCancel: (e: MouseEvent) => void }) => {
	return (
		<span onClick={props.onCancel}>
			<ElIcon class="dialog-container-close">
				<ElClise />
			</ElIcon>
		</span>
	);
};
// 弹框主体内容
const Content = (props: { content: VNode | null; bodyPadding: string }) => {
	return (
		<div class="dialog-container-main" style={{ padding: props.bodyPadding }}>
			{props.content}
		</div>
	);
};

type footer = (props: {
	showCancel: boolean;
	showConfirm: boolean;
	showNext: boolean;
	isNext: Ref<boolean>;
	loading: boolean;
	cancelText: string;
	confirmText: string;
	onCancel: (e: MouseEvent) => void;
	onConfirm: (e: MouseEvent) => void;
}) => JSX.Element;
const Footer: footer = (props) => {
	if (props.showCancel == false && props.showConfirm == false && props.showNext == false) return null;
	// 取消按钮
	const cancel = props.showCancel ? (
		<ElButton type="default" key="cancel" loading={props.loading} onClick={props.onCancel}>
			{props.cancelText}
		</ElButton>
	) : null;
	// 确认按钮
	const confirm = props.showConfirm ? (
		<ElButton type="primary" loading={props.loading} key="confirm" onClick={props.onConfirm}>
			{props.confirmText}
		</ElButton>
	) : null;
	return (
		<div class="dialog-container-footer">
			<div>
				{props.showNext ? (
					<ElCheckbox v-model:modelValue={props.isNext.value}>是否添加下一个</ElCheckbox>
				) : null}
			</div>
			<div>{[cancel, confirm]}</div>
		</div>
	);
};
