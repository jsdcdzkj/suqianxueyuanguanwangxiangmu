import { Refresh, Search, DArrowRight, DArrowLeft } from "@element-plus/icons-vue";
import { ElButton, ElIcon } from "element-plus";
import { computed, defineComponent } from "vue";
import { bool, array, func } from "vue-types";

export type ExpandType = {
	text: string;
	icon: any;
	type?: "" | "text" | "default" | "success" | "warning" | "info" | "primary" | "danger";
	emit: string;
};

export default defineComponent({
	name: "EExpand",
	props: {
		expand: bool().def(false),
		showExpand: bool().def(false),
		// 自定义按钮配置
		buttons: array<ExpandType>().def(() => [
			{ text: "搜索", icon: Search, type: "primary", emit: "search" },
			{ text: "重置", icon: Refresh, emit: "reset" }
		]),
		// 按钮渲染函数，可用于自定义按钮渲染
		renderButtons: func().def()
	},
	emits: ["search", "reset", "expandChange", "buttonClick"],
	setup(props, { emit }) {
		const classPrefix = computed(() => (props.expand ? "w-100% text-left" : "w-100% text-left"));

		// 处理按钮点击事件
		const handleButtonClick = (button: any) => {
			if (button.emit) {
				emit(button.emit);
				return;
			}
			// 触发通用按钮点击事件
			emit("buttonClick", button);
		};

		// 默认渲染按钮
		const renderDefaultButtons = () => {
			return props.buttons.map((button, index) => (
				<ElButton
					key={index}
					icon={button.icon}
					type={button.type || "default"}
					onClick={() => handleButtonClick(button)}
				>
					{button.text}
				</ElButton>
			));
		};

		return () => (
			<div class={classPrefix.value}>
				{props.renderButtons ? props.renderButtons(props.buttons, handleButtonClick) : renderDefaultButtons()}
				{props.showExpand ? (
					<a class="m-l-10px cursor-pointer" onClick={() => emit("expandChange")}>
						<ElIcon style="transform: rotate(90deg) translateX(5px)" size="20">
							{props.expand ? <DArrowRight /> : <DArrowLeft />}
						</ElIcon>
					</a>
				) : null}
			</div>
		);
	}
});
