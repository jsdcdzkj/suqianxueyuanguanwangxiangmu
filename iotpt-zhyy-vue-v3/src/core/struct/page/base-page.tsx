import { defineComponent, ref, computed } from "vue";
import BaseForm from "../form/base-form";
import BaseTable from "../table/base-table";
import { array, number, object, string, bool } from "vue-types";
import type { FormConfig } from "../form/use-base-form";
import type { PageProps, TableConfig, ToolbarConfig } from "../table/use-table";
import TableHeader from "../table/table-header";
import { ElButton, ElPagination } from "element-plus";

type TableSize = "" | "small" | "default" | "large";

export default defineComponent({
	props: {
		form: object<FormConfig>().def({}),
		table: object<TableConfig>().def({}),
		asideLeftWidth: number().def(0),
		asideRightWidth: number().def(0),
		footerHeight: number().def(0),
		toolbar: array<ToolbarConfig>().def([]),
		pagination: object<PageProps>().def({}),
		title: string().def(""),
		isSpecialLayout: bool().def(false),
		hasGap: bool().def(true)
	},
	inheritAttrs: false,
	setup(props, { slots }) {
		const excludedColumns = computed(() => props.table?.columns?.map((item) => item.columnKey) || []);

		const size = ref<TableSize>("default");
		const handleCommand = (command: TableSize) => {
			size.value = command;
		};

		const renderSlotPageSearch = () => {
			if (props.form?.value) {
				return <BaseForm {...props.form} v-model:value={props.form.value} />;
			}
			return slots.pageSearch?.();
		};

		const renderToolbar = () => {
			if (props.toolbar?.length) {
				return props.toolbar.map((config) => <ElButton {...config}>{config.label}</ElButton>);
			}
			return slots.pageToolbar?.();
		};

		const renderAside = (width: number, slotName: "pageLeft" | "pageRight") => {
			if (!width || !slots[slotName]) return null;

			return (
				<div
					class="overflow-hidden h-full rounded-12px"
					style={{
						width: `${width}px`,
						backgroundColor: "var(--app-card-color)"
					}}
				>
					{slots[slotName]?.()}
				</div>
			);
		};

		const renderSlotPageHeader = () => {
			if (slots.pageHeader) {
				return slots.pageHeader();
			}

			if (props.title) {
				return (
					<TableHeader title={props.title} v-model:value={excludedColumns.value} onCommand={handleCommand}>
						{{
							default: renderToolbar,
							title: slots.pageTitle
						}}
					</TableHeader>
				);
			}
			return null;
		};

		const renderSlotPageTop = () => {
			if (slots.pageTop) {
				return slots.pageTop();
			}
			return null;
		};

		const renderPagination = () => {
			if (props.pagination) {
				return (
					<ElPagination
						{...props.pagination}
						v-model:currentPage={props.pagination.currentPage}
						v-model:pageSize={props.pagination.pageSize}
					/>
				);
			}
			return slots.pagePagination?.();
		};

		const renderSlotPageBody = () => {
			if (slots.pageBody) {
				return slots.pageBody();
			}

			if (props.table) {
				return (
					<BaseTable {...props.table}>
						{{
							bodyCell: slots.pageTableCell,
							tableHeader: slots.pageTableHeader,
							actions: slots.tableActions
						}}
					</BaseTable>
				);
			}
			return null;
		};

		const renderSlotFooter = () => {
			if (!slots.pageFooter) return null;
			return <div class="h-72px w-full">{slots.pageFooter()}</div>;
		};

		return () => {
			const containerStyle = {
				height: props.footerHeight > 0 ? `calc(100% - ${props.footerHeight}px)` : "100%"
			};

			const mainClass = props.hasGap
				? "h-full flex flex-col flex-1 overflow-hidden gap-12px"
				: "h-full flex flex-col flex-1 overflow-hidden";

			return (
				<div class="h-full w-full" style={containerStyle}>
					<div class="h-full flex gap-12px p-12px">
						{renderAside(props.asideLeftWidth, "pageLeft")}
						<div class={mainClass}>
							{renderSlotPageTop?.()}
							<div class="flex-1 custom-table h-full flex flex-col w-full">
								<div class="flex-1 flex flex-col w-full p-10px overflow-hidden">
									{renderSlotPageHeader()}

									{renderSlotPageSearch()}
									{renderSlotPageBody()}
								</div>
								{props.pagination && (
									<div class="flex items-center justify-center p-10px pt-0">{renderPagination()}</div>
								)}
							</div>
						</div>
						{renderAside(props.asideRightWidth, "pageRight")}
					</div>
					{renderSlotFooter()}
				</div>
			);
		};
	}
});
