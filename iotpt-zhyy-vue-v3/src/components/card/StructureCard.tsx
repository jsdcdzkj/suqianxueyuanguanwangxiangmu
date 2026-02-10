import { defineComponent } from "vue";

export default defineComponent({
	name: "ModuleCard",
	props: {
		title: {
			type: String,
			default: ""
		},
		showHeader: {
			type: Boolean,
			default: true
		},
		showTitlePopup: {
			type: Boolean,
			default: false
		},
		bodyPadding: {
			type: String,
			default: "12px"
		}
	},
	setup(props, { slots }) {
		return () => {
			const hasHeader = props.showHeader && (props.title || slots.header);

			return (
				<div class="module-card">
					{hasHeader && (
						<header class="module-card__header">
							<div class="module-card__title-wrapper">
								{/* 竖线 */}
								<span class="module-card__title-line"></span>
								{/* 标题 */}
								{props.title && <h3 class="module-card__title">{props.title}</h3>}
								<span>
									{props.showTitlePopup ? (
										<i class="ri-information-line text-color-#999 cursor-pointer font-size-18px"></i>
									) : null}
								</span>
							</div>

							{/* 头部右侧操作区插槽 */}
							<div class="module-card__actions">{slots.actions && slots.actions()}</div>
						</header>
					)}

					{/* 分割线：仅在有头部时显示 */}
					{hasHeader && <div class="module-card__divider"></div>}

					{/* 主体内容区域 */}
					<main class="module-card__body" style={{ padding: props.bodyPadding }}>
						{slots.default ? slots.default() : <div>暂无内容</div>}
					</main>
				</div>
			);
		};
	}
});
