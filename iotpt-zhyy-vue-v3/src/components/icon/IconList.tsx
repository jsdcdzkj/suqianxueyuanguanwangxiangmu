import { defineComponent, h, markRaw, ref, watch } from "vue";
import * as ElementIcons from "@element-plus/icons-vue";
import { ElDivider, ElIcon } from "element-plus";
import { getIconsNameExt, Icon, Icons, remixIcons } from "./icon";
import { string } from "vue-types";

export default defineComponent({
	emits: ["click"],
	props: {
		iconValue: string().def(""),
		type: string<"all" | "element" | "project" | "remixicon">().def("all")
	},
	setup(prop, { emit }) {
		const getElementIcons = () => {
			if (prop.type == "all" || prop.type == "element") {
				return Object.keys(ElementIcons).map((item) => {
					const component = Reflect.get(ElementIcons, item);
					return {
						name: component.name,
						type: 1,
						component: markRaw(component)
					};
				});
			}
			return [];
		};

		const getProjectIcons = () => {
			if (prop.type == "all" || prop.type == "project") {
				return getIconsNameExt().map((icon) => {
					return {
						name: icon,
						type: 2,
						component: <Icon name={icon} size={{ width: "20px", height: "20px" }}></Icon>
					};
				});
			}
			return [];
		};

		const getRemixIconscons = () => {
			if (prop.type == "all" || prop.type == "remixicon") {
				return remixIcons.map((icon) => {
					return {
						name: `ri-${icon}`,
						type: 3
					};
				});
			}
			return [];
		};

		const icons = [...getElementIcons(), ...getProjectIcons(), ...getRemixIconscons()];
		const filterIcons = ref<{ type: number; name: string; component?: any }[]>(icons);
		let timer = 0;

		watch(
			() => prop.iconValue,
			(newVal) => {
				if (newVal) {
					if (timer != 0) {
						clearTimeout(timer);
						timer = 0;
						timer = setTimeout(() => {
							clearTimeout(timer);
							timer = 0;
							filterIcons.value = icons.filter((item) => {
								return item.name.includes(newVal);
							});
						}, 200);
					} else {
						timer = setTimeout(() => {
							filterIcons.value = icons.filter((item) => {
								return item.name.includes(newVal);
							});
							clearTimeout(timer);
							timer = 0;
						}, 200);
					}
				} else {
					filterIcons.value = icons.filter((item) => {
						return item.name.includes(newVal);
					});
				}
			}
		);

		return () => (
			<div class="flex flex-wrap gap-5px" w="100%" style="overflow-y:auto;max-height:400px">
				{filterIcons.value.map((item) => {
					if (item.type == 3) {
						return (
							<span
								w="20px"
								h="20px"
								style={`font-size:${20}px`}
								onClick={() => emit("click", item.name)}
								class={item.name}
							></span>
						);
					} else {
						return (
							<div w="20px" h="20px" onClick={() => emit("click", item.name)}>
								<ElIcon size="20px" style="color:var(--el-color-primary)">
									{h(item.component)}
								</ElIcon>
							</div>
						);
					}
				})}
			</div>
		);
	}
});
