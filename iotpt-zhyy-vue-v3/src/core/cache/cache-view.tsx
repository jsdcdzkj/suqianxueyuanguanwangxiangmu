import { defineComponent } from "vue";
import { array, bool, string } from "vue-types";
import { useRouterView } from "./router-view";

export default defineComponent({
  props: {
    includes: array<string>().def([]),
    excludes: array<string>().def([]),
    isRefreshView: bool().def(false),
    aniamtion: string().def("fade"),
  },
  setup(props) {
    const { routerView } = useRouterView(2, props.includes, props.excludes);
    return () => {
      return routerView();
    };
  },
});
