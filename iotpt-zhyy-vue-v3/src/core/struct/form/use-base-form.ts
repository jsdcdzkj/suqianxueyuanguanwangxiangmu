import { h, reactive, resolveComponent } from "vue";
import type { FormItem } from "../page";
import type { Arrayable } from "element-plus/lib/utils/typescript.js";
import type { FormInstance, FormItemRule } from "element-plus";
import { useDialogForm } from "@/core/dialog/dialog-container";
import type { SizeType } from "../table/use-table";

// 将FormItem数组转换为对象
function bindForm(formItems: FormItem[]) {
	return formItems.reduce(
		(form, item) => {
			if (item.prop) {
				form[item.prop] = item.value;
			}
			return form;
		},
		{} as Record<string, any>
	);
}

// 用于渲染下拉选项
function renderSelectOptin(item: FormItem) {
	// 如果item有select属性且没有render属性
	if (item.select && !item.render) {
		// 判断item的类型是否为ElRadioGroup
		const isRadio = item.type == "ElRadioGroup";
		// 如果是ElRadioGroup，则label为select的value，否则为select的label
		const label = isRadio ? item.select.value : item.select.label;
		// 如果是ElRadioGroup，则value为select的label，否则为select的value
		const value = isRadio ? item.select.label : item.select.value;
		// 返回一个函数，用于渲染下拉选项
		return () =>
			item.select?.list?.map((selectItem) => {
				// 如果有label属性，则l为selectItem的label，否则为selectItem的"label"
				let l = label ? selectItem[label] : selectItem["label"];
				// 如果有value属性，则v为selectItem的value，否则为selectItem的"value"
				let v = value ? selectItem[value] : selectItem["value"];

				// 使用h函数渲染组件，传入label、value、key和disabled属性
				return h(
					resolveComponent(item.select!!.type),
					{
						label: l,
						value: v,
						key: v,
						disabled: selectItem["disabled"]
					},
					() => l
				);
			});
	}
	// 如果item没有select属性或render属性，则返回undefined
	return undefined;
}

// 用于过滤选择列表
export async function filterSelectListApi(formItems: FormItem[]) {
	// 遍历表单项数组
	for (let i = 0; i < formItems.length; i++) {
		const item = formItems[i];

		// 根据表单项的类型进行不同的处理
		switch (item.type) {
			case "ElSelect":
			case "ElCheckboxGroup":
			case "ElRadioGroup":
				// 如果表单项有选择属性
				if (item.select) {
					// 如果选择属性中有列表API
					if (item.select.listApi) {
						// 调用列表API，获取列表数据
						item.select.list = await item.select.listApi(item);
					}
					// 如果表单项没有渲染子项
					if (!item.renderChild) {
						// 调用渲染选择项函数，渲染子项
						item.renderChild = renderSelectOptin(item);
					}
				}
				break;

			case "ElCascader":
				// 如果表单项有选择属性，并且选择属性中有列表API，并且有属性
				if (item.select && item.select.listApi && item.attrs) {
					// 调用列表API，获取列表数据
					item.attrs.options = await item.select.listApi(item);
				}
				break;
		}
	}

	// 返回处理后的表单项数组
	return formItems;
}

// 根据FormItem数组中的type属性，渲染select的list
export function filterSelectRender(formItems: FormItem[]) {
	for (let i = 0; i < formItems.length; i++) {
		const item = formItems[i];
		switch (item.type) {
			case "ElSelect":
			case "ElRadioGroup":
			case "ElCheckboxGroup":
				if (item.select) {
					if (!item.renderChild) {
						item.renderChild = renderSelectOptin(item);
					}
				}
				break;
			default:
				break;
		}
	}

	return formItems;
}

// 基础表单配置
// 导出类型BaseFormConfig，用于定义表单配置
export type BaseFormConfig = {
	// 表单项数组
	formItems: FormItem[];
	// 表单验证规则
	rules?: Record<string, Arrayable<FormItemRule>>;
	// 标签位置
	labelPosition?: "left" | "right" | "top";
	// 是否为行内表单
	inline?: boolean;
	// 标签宽度
	labelWidth?: number;
	// 是否显示错误信息
	inlineMessage?: boolean;
	// 是否滚动到错误位置
	scrollToError?: boolean;
	// 表单项之间的间距
	gutter?: number;
	// 表单项所占用的列数
	span?: number;
	// 是否显示错误信息
	showMessage?: boolean;
	showExpand?: boolean;
	notMargn?: boolean;
	size?: SizeType;
	expandSpan?: number;
	value?: any;
	isExpand?: boolean;
};

// 表单配置
export type FormConfig = {
	onReset?: (data?: any) => void;
	onSearch?: (data?: any) => void;
	onInstance?: (instance: FormInstance) => void;
	className?: string;
} & BaseFormConfig;

// 使用表单
export const useForm = (config: FormConfig) => {
	// 创建一个响应式对象，包含表单的结构
	const formStruct = reactive({
		// 将传入的配置项展开
		...config,
		// 将表单项绑定到表单上
		value: bindForm(config.formItems),
		// 将表单项保存到表单结构中
		formItems: config.formItems
	});

	// 创建一个Api对象，用于操作表单
	const Api = {
		// 设置表单项
		setFormItems(formItems: FormItem[]) {
			// 过滤掉不需要渲染的表单项
			formStruct.formItems = filterSelectRender(formItems);
		}
	};
	// 返回表单结构和Api对象
	return { form: formStruct, Api };
};

// 异步使用表单
export const useAsyncForm = async (config: FormConfig) => {
	// 调用filterSelectListApi函数，传入表单项配置
	await filterSelectListApi(config.formItems);
	// 返回useForm函数，传入表单项配置
	return useForm(config);
};

// 使用对话框表单
export const useDialogStructForm = (config: FormConfig, isBindInstance = true) => {
	// 使用useDialogForm函数，获取registerFormDone、registerFormInstrance和formInstance
	const { registerFormDone, registerFormInstrance } = useDialogForm();
	// 使用useForm函数，获取form和Api
	const { form, Api } = useForm(config);
	// 如果form存在且isBindInstance为true，则将registerFormInstrance赋值给form的onInstance属性
	if (form && isBindInstance) {
		form!!.onInstance = registerFormInstrance;
	}
	// 返回form、Api和registerFormDone
	return { form, Api, registerFormDone };
};
