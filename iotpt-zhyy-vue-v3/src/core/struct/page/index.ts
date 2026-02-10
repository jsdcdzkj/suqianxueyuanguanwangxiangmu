import type { FormInstance } from "element-plus";

import { onUnmounted, reactive, toRaw, type GlobalComponents, type VNode } from "vue";
import { useForm, type FormConfig } from "../form/use-base-form";
import { useTable, type PageProps, type TableConfig, type ToolbarConfig } from "../table/use-table";
import { bus } from "@/main";
import type { JSX } from "vue/jsx-runtime";

export type FormProps = FormInstance["$props"];

export type Element = GlobalComponents;

export type OptionsItem = {
	label: string;
	value: string;
	disabled?: boolean;
	children?: OptionsItem[];
};

export type FormItemType =
	| "ElButton"
	| "ElInput"
	| "ElSelect"
	| "ElRadioGroup"
	| "ElSwitch"
	| "ElCascader"
	| "ElDatePicker"
	| "ElCheckbox"
	| "ElRadio"
	| "ElColorPicker"
	| "ElRate"
	| "ElSlider"
	| "ElTimePicker"
	| "ElTimeSelect"
	| "ElTransfer"
	| "ElUpload"
	| "QuillEditor"
	| "IconSelect"
	| "Editor"
	| "ElCheckboxGroup"
	| "ElInputNumber";

export interface FormItem {
	value: any;
	prop: string;
	attrs?: Record<string, any> & {
		options?: any;
		placeholder?: string;
		multiple?: boolean;
	};
	type?: FormItemType;
	label?: string;
	select?: {
		label?: string;
		value?: string;
		listApi?: (data: any) => Promise<any[]> | any[];
		list?: any[];
		type: "ElOption" | "ElRadio" | "ElCheckbox";
	};
	renderChild?: () => VNode[] | undefined;
	render?: (form: any, item: any) => VNode | JSX.Element;

	node?: any;
	span?: number;
	show?: boolean;
}

export interface PageConfig {
	tableConfig?: TableConfig;
	formConfig?: FormConfig;
	pagination?: PageProps;
	toolbar?: ToolbarConfig[];
	title?: string;
	listApi?: (data?: any) => Promise<any>;
}

export const usePage = (config: PageConfig) => {
	const { form, Api: formApi } = config.formConfig ? useForm(config.formConfig) : { form: undefined, Api: {} };
	const { tableConfig } = config.tableConfig
		? useTable({ ...config.tableConfig, pagination: config?.pagination })
		: { tableConfig: undefined };
	const pageConfig = reactive({
		title: config.title,
		pagination: config.pagination,
		toolbar: config.toolbar,
		listApi: config.listApi,
		form,
		table: tableConfig
	});
	let formInstance: FormInstance | null = null;

	const methods = {
		// 分页查询

		async pageList() {
			if (!config.listApi) return;
			if (!config.tableConfig) return;
			let params = {};
			if (pageConfig.form?.value) {
				params = Object.assign({}, params, {
					...toRaw(pageConfig.form?.value)
				});
			}

			if (pageConfig.pagination) {
				const { currentPage, pageSize } = pageConfig.pagination;
				Reflect.set(params, "pageIndex", currentPage);
				Reflect.set(params, "pageSize", pageSize);
			}
			const data = await config.listApi(params);

			if (tableConfig && pageConfig.pagination) {
				tableConfig.dataSource = data.records;
			} else {
				if (tableConfig?.dataSource) {
					tableConfig.dataSource = data as any[];
				}
			}

			if (pageConfig.pagination) {
				pageConfig.pagination.total = data.total;
			}
		},
		// 获取form组件实例
		getFormInstance: (): FormInstance | null => {
			return formInstance;
		}
	};

	if (pageConfig.form) {
		// 重置
		pageConfig.form.onReset = () => {
			// console.log("重置", data);
			methods.pageList();
		};
		// 搜索
		pageConfig.form.onSearch = (data) => {
			console.log("搜索", data);
			methods.pageList();
		};
		// 注册并获取form实例
		const onInstance = pageConfig.form.onInstance;
		pageConfig.form.onInstance = (instance: FormInstance) => {
			onInstance && onInstance(instance);
			formInstance = instance;
		};
	}

	methods.pageList();

	bus.on("pageRefresh", (e: any) => {
		console.log(e);
	});

	onUnmounted(() => {
		bus.off("pageRefresh");
	});

	return { ...formApi, page: pageConfig, pageApi: methods };
};
