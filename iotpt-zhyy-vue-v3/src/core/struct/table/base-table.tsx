import { ElButton, ElDivider, ElTable, ElTableColumn, type TableColumnCtx, type TableInstance } from "element-plus";
import { defineComponent, Fragment, h, ref, computed } from "vue";
import { array, object } from "vue-types";
import type { ActionsConfig, TableColumnType, TableAttrsType, PageProps } from "./use-table";

interface RowType {
	// 定义你的row对象结构
}

interface ButtonItem<T> {
	show?: boolean | ((row: T) => boolean);
}

export default defineComponent({
	name: "CustomTable",
	props: {
		// 表格列
		columns: array<TableColumnType>().def([]),
		// 操作按钮
		actions: array<ActionsConfig>().def([]),
		// 表格属性
		attrs: object<TableAttrsType>().def({}),
		// 数据源
		dataSource: array().def([]),
		// 分页
		pagination: object<PageProps>().def()
	},
	inheritAttrs: false,
	emits: ["refresh"],
	setup(props, { slots, emit }) {
		// 定义表格实例
		const table = ref<TableInstance>();
		// 定义需要排除的列
		const excluldColumn = ref<Array<string | undefined>>(props.columns.map((item) => item.columnKey));
		// 定义表格大小
		const size = ref<"" | "small" | "default" | "large">("default");

		// 计算当前页的起始索引
		const startIndex = computed(() => {
			if (!props.pagination) return 0;
			return (props.pagination.currentPage - 1) * props.pagination.pageSize;
		});

		// 判断按钮是否显示
		const isButtonVisible = <T extends RowType>(item: ButtonItem<T> | undefined | null, row: T): boolean => {
			if (!item?.show) return true;
			if (typeof item.show === "function") return item.show(row);
			return item.show;
		};

		// 渲染操作按钮
		const renderActionBtns = (row: any) => {
			const visibleActions = props.actions.filter((item) => isButtonVisible(item, row));

			if (visibleActions.length === 0) return null;

			return h(
				"div",
				{ class: "flex flex-items-center flex-justify-center" },
				visibleActions.map((item, index) => {
					const isLast = index === visibleActions.length - 1;

					return h(Fragment, { key: index }, [
						h(
							ElButton,
							{
								...item,
								text: false,
								onClick: async () => {
									await item.onClick?.(row);
									emit("refresh");
								}
							},
							() => item.text
						),
						!isLast ? h(ElDivider, { direction: "vertical" }) : null
					]);
				})
			);
		};

		// 渲染表格单元格内容
		const renderCellContent = (params: any) => {
			const { column, row, $index } = params;

			// 处理操作列
			if (column.columnKey === "actions") {
				return slots.actions ? slots.actions(params) : renderActionBtns(row);
			}

			// 处理索引列
			if (column.columnKey === "index") {
				return startIndex.value + $index + 1;
			}

			// 处理自定义单元格插槽
			// console.log(params);

			if (slots.bodyCell) {
				return slots.bodyCell(params);
			}

			// 默认返回单元格值
			return row[column.property];
		};

		const renderTableHeader = (params: TableColumnCtx<any>) => {
			if (slots.tableHeader) {
				return slots.tableHeader(params);
			}
			return params.label;
		};

		// 渲染表格列
		const renderTableColumns = () => {
			return props.columns
				.filter((item) => excluldColumn.value.includes(item.columnKey))
				.map((item) => (
					<ElTableColumn {...item} align={item.align || props.attrs.align || "center"}>
						{{
							default: renderCellContent,
							header: renderTableHeader
						}}
					</ElTableColumn>
				));
		};

		return () => (
			<ElTable
				data={props.dataSource}
				class="w-100%"
				ref={table}
				tableLayout="auto"
				size={size.value}
				border
				maxHeight={"calc(100% + 48px)"}
				{...props.attrs}
			>
				{renderTableColumns()}
			</ElTable>
		);
	}
});
