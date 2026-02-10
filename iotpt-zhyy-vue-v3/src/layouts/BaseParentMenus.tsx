import { ArrowDown } from "@element-plus/icons-vue";
import { ElIcon, ElMenu, ElMenuItem, ElPopover } from "element-plus";
import { defineComponent, h } from "vue";
import { array, bool, string } from "vue-types";
import PopoverList from "./PopoverList";

export default defineComponent({
  props: {
    collapse: bool().def(false),
    rawList: array().def([]),
    defaultActive: string().def(),
    chList: array().def([]),
    chActive: string().def(),
    defaultOpend: array<string>().def(),
  },
  setup(props) {
    const renderItem = (item: any) => {
      return h(ElMenuItem, { index: item.path, key: item.path }, () => [
        item.title,
        <ElIcon>
          <ArrowDown />
        </ElIcon>,
      ]);
    };

    function getCMenuStruct(menusList: any[]) {
      return menusList.map((item: any) => {
        return (
          <ElPopover
            showArrow={false}
            offset={10}
            width={item.children.length * 120}
            trigger={"hover"}
          >
            {{
              default: () => {
                return <PopoverList list={item.children} />;
              },
              reference: () => renderItem(item),
            }}
          </ElPopover>
        );
      });
    }

    return () => {
      return (
        <ElMenu
          router
          defaultActive={props.defaultActive}
          class="ep-menu-not-border layout-header-custom-menu transition-none"
          collapse={props.collapse}
          mode="horizontal"
          ellipsis={false}
        >
          {getCMenuStruct(props.rawList)}
        </ElMenu>
      );
    };
  },
});
