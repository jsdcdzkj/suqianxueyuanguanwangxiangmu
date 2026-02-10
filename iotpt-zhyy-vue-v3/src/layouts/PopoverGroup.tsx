import { defineComponent } from "vue";
import { useRouter } from "vue-router";
import { array } from "vue-types";

// 定义列表项接口
interface MenuItem {
	title: string;
	fullPath: string;
	children?: MenuItem[];
}

// 子项组件
const PopoverItem = defineComponent({
	name: "PopoverItem",
	props: {
		item: {
			type: Object as () => MenuItem,
			required: true
		}
	},
	setup(props) {
		const router = useRouter();

		const handleClick = () => {
			if (props.item.fullPath) {
				router.push(props.item.fullPath);
			}
		};

		return () => (
			<div class="popover-item-content" onClick={handleClick}>
				{props.item.title}
			</div>
		);
	}
});

export default defineComponent({
	name: "PopoverList",
	props: {
		list: array<MenuItem>().def([])
	},
	setup(props) {
		const renderList = (items: MenuItem[]) => {
			return items.map((item) => {
				// 分离有子项和无子项的子菜单
				const itemsWithoutChildren =
					item.children?.filter((child) => !child.children || child.children.length === 0) || [];

				const itemsWithChildren =
					item.children?.filter((child) => child.children && child.children.length > 0) || [];

				return (
					<div class="popover-item">
						{item.title && <div class="popover-item-title">{item.title}</div>}
						{itemsWithoutChildren.map((child) => (
							<PopoverItem item={child} />
						))}
						{itemsWithChildren.map((child) => renderList([child]))}
					</div>
				);
			});
		};

		return () => <div class="popover-group">{renderList(props.list)}</div>;
	}
});
