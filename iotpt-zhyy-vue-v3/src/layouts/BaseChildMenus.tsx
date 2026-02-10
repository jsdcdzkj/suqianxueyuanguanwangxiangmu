import { ElMenu, ElMenuItem, ElSubMenu } from "element-plus";
import { defineComponent, h, PropType } from "vue";
import { Icon } from "@/components/icon/icon";

// 定义菜单项类型
interface MenuItem {
	path: string;
	title: string;
	icon?: string;
	children?: MenuItem[];
}

export default defineComponent({
	name: "CustomMenu",
	props: {
		collapse: {
			type: Boolean,
			default: false
		},
		rawList: {
			type: Array as PropType<MenuItem[]>,
			default: () => []
		},
		defaultActive: {
			type: String,
			default: ""
		},
		defaultOpend: {
			type: Array as PropType<string[]>,
			default: () => []
		}
	},
	setup(props) {
		// 渲染图标
		const renderIcon = (icon?: string) => {
			if (!icon) return null;

			if (icon.startsWith("ri")) {
				return h("i", { class: icon, style: { fontSize: "22px" } });
			}

			return h(Icon, {
				name: icon,
				size: {
					width: "22px",
					height: "22px"
				}
			});
		};

		// 渲染菜单项
		const renderMenuItem = (item: MenuItem) => {
			return h(
				ElMenuItem,
				{ index: item.path },
				{
					title: () => h("span", { class: "el-menu-item-span m-l-8px" }, item.title),
					default: () => renderIcon(item.icon)
				}
			);
		};

		// 渲染子菜单
		const renderSubMenu = (item: MenuItem) => {
			return h(
				ElSubMenu,
				{ index: item.path },
				{
					title: () => [renderIcon(item.icon), h("span", item.title)],
					default: () =>
						item.children?.map((child) =>
							child.children?.length ? renderSubMenu(child) : renderMenuItem(child)
						)
				}
			);
		};

		// 渲染菜单结构
		const renderMenu = () => {
			return props.rawList.map((item) => (item.children?.length ? renderSubMenu(item) : renderMenuItem(item)));
		};

		return () => (
			<ElMenu
				router
				defaultActive={props.defaultActive}
				class="el-menu-vertical-demo"
				mode="vertical"
				collapse={props.collapse}
				uniqueOpened={false}
				defaultOpeneds={props.defaultOpend}
				collapseTransition={false}
			>
				{renderMenu()}
			</ElMenu>
		);
	}
});
