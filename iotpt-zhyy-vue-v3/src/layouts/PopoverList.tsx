import { defineComponent } from "vue";
import PopoverItem from "./PopoverGroup";

export default defineComponent({
  name: "PopoverList",
  props: {
    list: {
      type: Array,
      default: () => [],
    },
  },
  setup(props) {
    return () => {
      return (
        <div class="flex gap-2 popover-list">
          {props.list.map((item: any) => (
            <PopoverItem list={[item]} />
          ))}
        </div>
      );
    };
  },
});
