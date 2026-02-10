import { ElInput, ElPopover } from "element-plus";
import { defineComponent, onMounted, ref, watch } from "vue";
import { string } from "vue-types";
import IconList from "./IconList";

export default defineComponent({
	name: "IconSelect",
	props: {
		modelValue: string().def(""),
		type: string<"all" | "element" | "project" | "remixicon">().def("all")
	},
	emits: ["update:modelValue"],
	setup(props, { emit }) {
		const selectIcon = ref<string>("");
		const showPopup = ref<boolean>(false);
		const width = ref(150);
		const input = ref();

		watch(
			() => props.modelValue,
			(newVal) => {
				selectIcon.value = newVal;
			},
			{ immediate: true }
		);

		onMounted(() => {
			if (input.value.input.clientWidth > 150) {
				width.value = input.value.input.clientWidth;
			}
		});

		return () => (
			<ElPopover width={width.value} trigger="contextmenu" visible={showPopup.value}>
				{{
					reference: () => (
						<ElInput
							ref={input}
							onFocus={() => {
								showPopup.value = true;
								if (input.value.input.clientWidth > 150) {
									width.value = input.value.input.clientWidth + 20;
								}
							}}
							onBlur={() => (showPopup.value = false)}
							v-model={selectIcon.value}
							placeholder="请选择图标"
						></ElInput>
					),
					default: () => (
						<IconList
							iconValue={selectIcon.value}
							type={props.type}
							onClick={(e) => {
								selectIcon.value = e;
								showPopup.value = false;
								emit("update:modelValue", e);
							}}
						/>
					)
				}}
			</ElPopover>
		);
	}
});
