import { ElDivider } from "element-plus";
import { defineComponent, ref } from "vue";
import { array, string } from "vue-types";
export default defineComponent({
	props: {
		// 标题
		title: string().def("标题"),
		// 值
		value: array<string>().def([])
	},
	emits: ["command", "update:value"],
	setup(props, { slots }) {
		return () => (
			<div
				class="flex  p-b-10px flex-items-center flex-justify-between m-b-12px w-full pos-relative left--10px"
				style="width:calc(100% + 10px)"
			>
				<div class="w-4px h-24px bg-[#345BAD]"></div>
				<div class="font-size-18px flex-1 px-12px font-b font-bold w-full" style="text-align:left">
					{slots.title ? slots.title() : (props.title ?? null)}
				</div>
				{slots.default ? <div class="flex  flex-items-center">{slots.default()}</div> : null}

				<ElDivider
					style="position:absolute;left:-12px;bottom:0;width:calc(100% + 24px)"
					class="m-t-0px m-b-0px"
				></ElDivider>
			</div>
		);
	}
});
