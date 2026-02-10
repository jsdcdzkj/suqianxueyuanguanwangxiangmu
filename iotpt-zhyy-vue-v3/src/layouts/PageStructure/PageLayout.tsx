import { defineComponent } from "vue";

export default defineComponent({
	name: "PageLayout",
	props: {
		title: {
			type: String,
			default: "Page"
		},
		// 侧边栏宽度
		sidebarWidth: {
			type: String,
			default: "250px"
		},
		// 头部高度
		headerHeight: {
			type: String,
			default: "60px"
		},
		// 内容区域内边距 (Main区域的padding)
		padding: {
			type: String,
			default: "20px"
		},
		// 布局间距 (Header与Body之间，Aside与Main之间)
		gap: {
			type: String,
			default: "0px" // 默认无间距，如果需要分割线，建议用CSS border控制
		},
		// 是否显示顶部区域
		showPageTop: {
			type: Boolean,
			default: true
		}
	},
	setup(props, { slots }) {
		return () => (
			<div class="page-layout">
				{/* 顶部区域 - 根据showPageTop决定是否渲染 */}
				{props.showPageTop && (
					<header class="page-layout__header" style={{ height: props.headerHeight, marginBottom: props.gap }}>
						{slots.pageTop ? slots.pageTop() : <h1>{props.title}</h1>}
					</header>
				)}

				<div class="page-layout__body" style={{ gap: props.gap }}>
					{/* 左侧侧边栏 */}
					{slots.pageLeft && (
						<aside class="page-layout__aside" style={{ width: props.sidebarWidth }}>
							{slots.pageLeft()}
						</aside>
					)}

					{/* 主要内容区域 */}
					<main class="page-layout__main">{slots.pageContent ? slots.pageContent() : slots.default?.()}</main>
				</div>
			</div>
		);
	}
});
