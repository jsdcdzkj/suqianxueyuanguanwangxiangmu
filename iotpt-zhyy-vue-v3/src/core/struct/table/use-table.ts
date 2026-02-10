import { reactive, type Component, type DefineComponent } from "vue";
import { type TableColumnCtx } from "element-plus";
// 定义按钮类型
type ButtonType = "primary" | "success" | "warning" | "danger" | "info" | "text";
// 定义大小类型
export type SizeType = "large" | "default" | "small" | "";
// 定义工具栏配置
export type ToolbarConfig = {
	onClick?: (e: Event) => void; // 点击事件
	icon?: any; // 图标
	text?: boolean; // 是否显示文本
	label: string; // 标签
	type?: ButtonType; // 按钮类型
	size?: SizeType; // 大小
	disabled?: boolean; // 是否禁用
	[key: string]: any; // 其他属性
};

// 定义表格列类型
export type TableColumnType = {
	type?: "selection" | "index" | "expand" | string; // 列类型
	index?: number | ((index: number) => number); // 索引
	label: string; // 标签
	columnKey?: string; // 列键
	prop?: string; // 属性
	width?: string | number; // 宽度
	minWidth?: string | number; // 最小宽度
	fixed?: boolean | "left" | "right" | string; // 固定
	sortable?: boolean | string; // 可排序
	sortMethod?: (a: any, b: any) => number; // 排序方法
	className?: string; // 类名
	labelClassName?: string; // 标签类名
	align?: "left" | "center" | "right"; // 对齐方式
	renderHeader?: (header: { column: TableColumnCtx<any>; $index: number }) => any;
	[key: string]: any; // 其他属性
};

// 定义显示函数类型
type ShowFunc = (row: any) => boolean;

// 定义操作配置
export type ActionsConfig = {
	onClick?: (e: RowRcordType) => void; // 点击事件
	icon?: string | Component; // 图标
	text?: string; // 文本
	type?: ButtonType; // 按钮类型
	circle?: boolean;
	danger?: boolean;
	size?: SizeType; // 大小
	plain?: boolean;
	link?: boolean;
	show?: boolean | ShowFunc; // 显示
	[key: string]: any; // 其他属性
};

// 定义行记录类型
export type RowRcordType = {
	text: string;
	value: string;
	row: Record<string, any>;
	index: number;
	column: TableColumnType;
};

// 定义ApiFunc类型，参数为可选的任意类型，返回值为任意类型或任意类型的数组
export type ApiFunc = (data?: any) => any | any[];
// 定义ApiAsyncFunc类型，参数为可选的任意类型，返回值为Promise类型的任意类型或任意类型的数组
export type ApiAsyncFunc = (data?: any) => Promise<any | any[]>;
// 定义PageProps类型，包含pageSize、currentPage、pageSizes、total、showSizeChanger、layout、background、onChange、onSizeChange、onCurrentChange等属性
export type PageProps = {
	pageSize: number; // 每页显示的条数
	currentPage: number;
	pageSizes?: number[]; // 可选择的每页显示条数
	total: number; // 总条数
	showSizeChanger?: boolean; // 是否显示每页显示条数的选择器
	layout?: string; // 分页组件的布局
	background?: boolean; // 是否显示背景色
	onChange?: (page: number, pageSize: number) => void; // 页码改变的回调，参数为当前页码和每页显示条数
	onSizeChange?: (page: number) => void; // 每页显示条数改变的回调，参数为当前页码
	onCurrentChange?: (page: number) => void; // 当前页码改变的回调，参数为当前页码
};

// 导出接口TableAttrsType
export interface TableAttrsType {
	// 表格高度，可以是字符串或数字
	height?: string | number;
	// 表格最大高度，可以是字符串或数字
	maxHeight?: string | number;
	// 是否显示斑马纹
	stripe?: boolean;
	// 是否显示边框
	border?: boolean;
	// 表格尺寸，可以是SizeType类型
	size?: SizeType;
	// 列宽是否自撑开
	fit?: boolean;
	// 行的 className 的回调方法
	rowClassName?: string | (({ row, rowIndex }: { row: any; rowIndex: number }) => string);
	// 空数据时显示的文本内容
	emptyText?: string;
	align?: "left" | "center" | "right";

	// 其他属性
	[key: string]: any;
}

// 导出接口TableConfig
export interface TableConfig {
	// 工具栏配置
	toolbar?: ToolbarConfig[];
	// 列表API
	listApi?: (data?: any) => Promise<any>;
	// 操作配置
	actions?: ActionsConfig[];
	// 表格标题
	title?: string;
	// 分页配置
	pagination?: PageProps | undefined;
	// 列配置
	columns?: TableColumnType[];
	// 是否显示边框
	bordered?: boolean;
	// 表格属性
	attrs?: TableAttrsType;
	// 数据源
	dataSource: any[];
}

// 导出接口TableAllConfig
export interface TableAllConfig {
	// 工具栏配置
	toolbar?: ToolbarConfig[];
	// 列表API
	listApi: (data?: any) => Promise<any>;
	// 操作配置
	actions?: ActionsConfig[];
	// 表格标题
	title?: string;
	// 属性
	props: {
		// 分页配置
		pagination?: PageProps | boolean;
		// 表格列配置
		columns?: TableColumnType[];
		// 行键
		rowKey: string;
		// 数据源
		dataSource: any[];
		// 是否显示边框
		bordered?: boolean;
	};
}

// 导出一个名为useTable的函数，接收一个tableConfig参数
export const useTable = (tableConfig: TableConfig) => {
	// 定义一个响应式的_tableConfig对象，用于存储tableConfig的值
	const _tableConfig = reactive({
		// 如果tableConfig中有actions属性，则赋值给_tableConfig.actions，否则赋值为空数组
		actions: tableConfig?.actions || [],
		// 如果tableConfig中有attrs属性，则赋值给_tableConfig.attrs，否则赋值为空对象
		attrs: tableConfig?.attrs || {},
		// 如果tableConfig中有pagination属性，则赋值给_tableConfig.pagination，否则赋值为undefined
		pagination: tableConfig?.pagination,
		// 如果tableConfig中有columns属性，则赋值给_tableConfig.columns，否则赋值为空数组
		columns: tableConfig?.columns || [],
		// 如果tableConfig中有dataSource属性，则赋值给_tableConfig.dataSource，否则赋值为空数组
		dataSource: tableConfig?.dataSource || []
	});

	// 定义一个methods对象，用于存储tableConfig的方法
	const methods = {
		// 定义一个setDataSource方法，用于设置_tableConfig.dataSource的值
		setDataSource(data: any[]) {
			_tableConfig.dataSource = data;
		}
	};

	// 返回_tableConfig和methods
	return { tableConfig: _tableConfig, tableApi: methods };
};
