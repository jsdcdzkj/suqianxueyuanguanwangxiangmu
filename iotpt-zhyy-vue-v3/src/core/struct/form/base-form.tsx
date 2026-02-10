import {
	ElButton,
	ElCol,
	ElForm,
	ElFormItem,
	ElInputNumber,
	ElProgress,
	ElRadioGroup,
	ElRate,
	ElRow,
	ElUpload,
	ElCascader,
	type FormItemRule,
	ElCheckboxGroup,
	ElInput,
	ElSelect,
	ElDatePicker,
	ElCheckbox,
	ElRadio,
	ElRadioButton,
	ElOption,
	ElTimePicker
} from "element-plus";
import { computed, defineComponent, h, markRaw, onMounted, reactive, ref, watch, toRaw, resolveComponent } from "vue";
import { array, bool, number, object, string } from "vue-types";
import type { FormItem } from "../page";
import type { Arrayable } from "element-plus/lib/utils/typescript.js";
import Expand from "./expand";
import { filterSelectListApi } from "./use-base-form";
import IconSelect from "@/components/icon/IconSelect";
import Editor from "@/components/editor/index.vue";
import type { SizeType } from "../table/use-table";

export default defineComponent({
	name: "BaseForm",
	components: {
		ElInput,
		ElSelect,
		ElDatePicker,
		ElCheckbox,
		ElRadio,
		ElRadioButton,
		ElOption,
		ElTimePicker,
		ElRadioGroup,
		ElProgress,
		ElRate,
		ElUpload,
		IconSelect,
		Editor,
		ElInputNumber,
		ElCascader,
		ElCheckboxGroup,
		ElButton
	},
	props: {
		// 表单每列所占的栅格数
		span: number().def(5),
		// 最小行数
		minRow: number().def(1),
		// 表单项
		formItems: array<FormItem>().def([]),
		// 表单验证规则
		rules: object<Record<string, Arrayable<FormItemRule>>>().def({}),
		// 标签对齐方式
		labelPosition: string<any>().def("left"),
		// 标签宽度
		labelWidth: number().def(80),
		// 是否行内表单
		inline: bool().def(true),
		// 表单值
		value: object().def({}),
		// 是否显示校验信息
		showMessage: bool().def(false),
		// 是否显示展开按钮
		showExpand: bool().def(true),
		// 显示底部内边距
		showPaddingBottom: bool().def(false),
		// 表单大小
		size: string<SizeType>().def(""),
		// 展开时每列所占的栅格数
		expandSpan: number().def(4),
		// 是否展开
		isExpand: bool().def(false),
		// 自定义追加内容
		append: function () {
			return null;
		}
	},
	emits: ["update:value", "search", "reset", "instance"],
	setup(props, { emit }) {
		// 表单实例
		const formInstance = ref();
		// 展开状态
		const expandState = reactive({
			up: !props.isExpand
		});

		// 计算每列所占的栅格数
		const colNum = computed(() => Math.floor(24 / props.span));

		// 计算行数
		const rowCount = computed(() => Math.ceil(props.formItems.length / (24 / (props.span + 1))));

		// 是否显示展开按钮
		const showExpandButton = computed(() => rowCount.value > props.minRow);

		// 遍历表单项，解析组件
		props.formItems.forEach((item) => {
			if (item.type) {
				let node = resolveComponent(item.type);
				if (typeof node == "object") {
					item.node = markRaw(node);
				}
			}
		});

		// 过滤下拉列表数据
		filterSelectListApi(props.formItems);

		// 搜索
		const handleSearch = () => {
			emit("search", toRaw(props.value));
		};

		// 重置
		const handleReset = () => {
			formInstance.value?.resetFields();
			emit("reset", toRaw(props.value));
		};

		// 组件挂载时触发
		onMounted(() => {
			emit("instance", formInstance.value);
		});

		return () => {
			// 渲染表单项内容
			const renderFormItemContent = (item: FormItem) => {
				if (item.render) {
					return item.render(props.value, item);
				}
				if (!item.node) return null;

				return h(
					item.node,
					{
						style: { width: "100%" },
						filterable: true,
						...item.attrs,
						modelValue: props.value[item.prop],
						"onUpdate:modelValue": (e: any) => {
							props.value[item.prop] = e;
						}
					},
					{
						default: item.renderChild,
						// 修改prepend插槽支持函数式渲染，传入formData和当前item
						append: item.append ? () => item.append(props.value, item) : undefined
					}
				);
			};

			// 渲染表单项
			const renderColFormItem = (item: FormItem, index: number) => {
				if (Reflect.has(item, "show") && !item.show) return null;

				if (expandState.up && index > colNum.value - 2 && props.showExpand) {
					return null;
				}

				const bind = { label: item.label, prop: item.prop, w: "full" };
				return (
					<ElCol span={item.span || props.span} key={index}>
						<ElFormItem {...bind}>{renderFormItemContent(item)}</ElFormItem>
					</ElCol>
				);
			};

			return (
				<ElForm
					model={props.value}
					rules={props.rules}
					labelPosition={props.labelPosition}
					labelWidth={props.labelWidth}
					inline={props.inline}
					class={["base-form", props.showPaddingBottom ? "margin" : ""]}
					showMessage={props.showMessage}
					inlineMessage={true}
					ref={formInstance}
					size={props.size}
				>
					<ElRow gutter={10} w="100%">
						{props.formItems.map((item, index) => renderColFormItem(item, index))}
						{props.showExpand ? (
							<ElCol span={props.expandSpan}>
								<ElFormItem class="w-100%" style={{ marginLeft: "0px" }}>
									<Expand
										showExpand={showExpandButton.value}
										expand={expandState.up}
										onReset={handleReset}
										onSearch={handleSearch}
										onExpandChange={() => (expandState.up = !expandState.up)}
									/>
								</ElFormItem>
							</ElCol>
						) : null}
					</ElRow>
				</ElForm>
			);
		};
	}
});
