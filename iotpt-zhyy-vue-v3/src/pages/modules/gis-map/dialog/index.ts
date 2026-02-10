import { createModelAsync } from "@/core/dialog";
import Demo from "./demo.vue";
import { h } from "vue";

export const createDemoDialog = () => {
	return createModelAsync({ title: "标题" }, {}, h(Demo));
};
