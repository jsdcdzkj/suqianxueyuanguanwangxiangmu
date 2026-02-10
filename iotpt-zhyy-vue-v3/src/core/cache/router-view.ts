import { computed, h, KeepAlive, Transition } from "vue";
import { useRoute } from "vue-router";

export const useRouterView = (
  defaultDepth: number,
  includes: string[],
  excludes: string[] = []
) => {
  const route = useRoute();
  const depth = computed<number>(() => {
    const { matched } = route;
    let matchedRoute: any;
    let initialDepth = defaultDepth;
    while ((matchedRoute = matched[initialDepth]) && matchedRoute.components) {
      initialDepth++;
    }
    return initialDepth;
  });
  const matchedRouteRef = computed(() => route.matched[depth.value - 1]);

  const routerView = (animationName: string = "fade") => {
    if (matchedRouteRef.value.components) {
      const Component = matchedRouteRef.value.components["default"];
      Reflect.set(Component, "name", route.meta.routerName);
      return h(Transition, { name: animationName, mode: "out-in" }, () =>
        h(KeepAlive, { include: includes, exclude: excludes }, [
          h(Component, {
            key: route.meta.routerName,
            name: route.meta.routerName,
          }),
        ])
      );
    }
    return null;
  };
  return { routerView };
};
