import router from "@/router";
import Layout from "@/layout/index.vue";

export const convertRouter2 = (asyncRoutes, first, oldPath) => {
    let num = 0;
    return asyncRoutes.map((route) => {
        num++;
        // 组装数据
        if (route.children && route.children.length > 0) {
            route.children.forEach((child) => {
                child.component = (resolve) => require([`@/views/` + child.componentUrl + ``], resolve);
                child.meta = { title: child.meta.title, icon: child.meta.icon };
            });
            route.component = Layout;
        } else {
            let oldRoute = route;
            route = {};
            if (oldRoute.path === first) {
                route.path = oldPath;
            } else {
                route.path = oldPath;
            }

            if (oldRoute.redirect !== null) {
                route.redirect = oldRoute.redirect;
            }
            // route.component = (resolve) => require([`@/layout/index_2.vue`], resolve);
            route.component = Layout;
            let url = oldRoute.componentUrl;
            // 如果第一个字符是/,则去掉
            if (url.charAt(0) === "/") {
                url = url.substr(1);
            }
            route.children = [
                {
                    path: oldRoute.path,
                    name: oldRoute.name,
                    component: (resolve) => require([`@/views/` + url + ``], resolve),
                    meta: { title: oldRoute.meta.title, icon: oldRoute.meta.icon }
                }
            ];
        }
        return route;
    });
};

export const linksRouter = (permissionList) => {
    let links = permissionList.map((item) => {
        return {
            ...item.meta,
            iconName: item.meta.icon,
            routes: [],
            homeRoutePath: "",
            bscode: item.redirect,
            hidden: false
        };
    });

    // 根据权限动态控制路由
    for (let i = 0; i < permissionList.length; i++) {
        let permission = permissionList[i];
        // 根据权限动态控制路由
        if (permission.children && permission.children.length > 0) {
            let menuList = permission.children;

            if (links[1] && permission.redirect === links[1].bscode) {
                // 智慧能源
                // let tor = that.convertRouter(menuList)
                let tor = convertRouter2(menuList, "/", "/");
                links[1].name = permission.meta.title;
                tor.push({
                    path: "/login",
                    component: (resolve) => require(["@/views/login/index"], resolve),
                    routerPath: "@/views/login/index",
                    hidden: true
                });
                tor.push({
                    path: "/404",
                    component: (resolve) => require(["@/views/404"], resolve),
                    routerPath: "@/views/404",
                    hidden: true
                });
                tor.push({ path: "*", redirect: "/404", hidden: true });
                tor.push({
                    path: "/dashboards",
                    component: (resolve) => require(["@/layout/index.vue"], resolve),
                    children: [
                        {
                            path: "/dashboard",
                            name: "我的任务",
                            component: (resolve) => require(["@/views/dashboard/index"], resolve),
                            meta: { title: "我的任务", icon: "bookmark" }
                        }
                    ],
                    hidden: true
                });
                let homePage = "/";
                if (menuList[0] && menuList[0].path !== "") {
                    homePage = menuList[0].path;
                }
                links[1].homeRoutePath = homePage;
                links[1].routes = tor;
                links[1].hidden = false;
                router.addRoutes(tor);
            } else if (links[0] && permission.redirect === links[0].bscode) {
                // 物联监督
                links[0].name = permission.meta.title;
                let tor = convertRouter2(menuList, "/ioTSupervision/equipmentMonitoring", "/ioTSupervision");
                tor.forEach((item) => {
                    item.isShowChild = false;
                });
                // let homePage = tor[0].path;
                // if (tor[0].children && tor[0].children.length > 0) {
                //     homePage = homePage + "/" + tor[0].children[0].path;
                // }
                let homePage = "/";
                if (menuList[0] && menuList[0].path !== "") {
                    homePage = menuList[0].path;
                }
                links[0].homeRoutePath = homePage;
                // links[1].homeRoutePath = tor[1].path;
                links[0].routes = tor;
                links[0].hidden = false;
                router.addRoutes(tor);
            } else if (permission.redirect === links[2].bscode) {
                // 智慧安防
                let tor = convertRouter2(menuList, "/intelligentSecurity/videoMonitoring", "/intelligentSecurity");
                links[2].name = permission.meta.title;
                // that.childrenRoutes(menuList, intelligentSecurityRoutes);\
                let homePage = tor[0].path;
                // if (tor[0].children && tor[0].children.length > 0) {
                //     homePage = homePage + "/" + tor[0].children[0].path;
                // }
                // let homePage = "/";
                // if (menuList[0] && menuList[0].path !== "") {
                //     homePage = menuList[0].path;
                // }

                links[2].homeRoutePath = homePage;
                links[2].routes = tor;
                links[2].hidden = false;
                router.addRoutes(tor);
            } else if (links[3] && permission.redirect === links[3].bscode) {
                // 运维管理
                let tor = convertRouter2(menuList, "/iTOperationManage/mytask", "/iTOperationManage");
                tor.push({
                    path: "/404",
                    component: () => import("@/views/404"),
                    routerPath: "@/views/404",
                    hidden: true
                });
                tor.push({ path: "*", redirect: "/404", hidden: true });
                // let homePage = tor[0].path;
                // if (tor[0].children && tor[0].children.length > 0) {
                //     homePage = homePage + "/" + tor[0].children[0].path;
                // }
                let homePage = "/";
                if (menuList[0] && menuList[0].path !== "") {
                    homePage = menuList[0].path;
                }
                links[3].homeRoutePath = homePage;
                links[3].homeRoutePath = tor[0].path;
                links[3].name = permission.meta.title;
                // that.childrenRoutes(menuList, iTOperationManageRoutes);
                links[3].routes = tor;
                links[3].hidden = false;
                router.addRoutes(tor);
            } else if (links[4] && permission.redirect === links[4].bscode) {
                // 运维管理\
                let tor = convertRouter2(menuList, "/OperationManage/meeting", "/OperationManage");
                tor.push({
                    path: "/404",
                    component: () => import("@/views/404"),
                    routerPath: "@/views/404",
                    hidden: true
                });
                tor.push({ path: "*", redirect: "/404", hidden: true });
                // let homePage = tor[0].path;
                // if (tor[0].children && tor[0].children.length > 0) {
                //     homePage = homePage + "/" + tor[0].children[0].path;
                // }
                let homePage = "/";
                if (menuList[0] && menuList[0].path !== "") {
                    homePage = menuList[0].path;
                }
                links[4].homeRoutePath = homePage;
                links[4].homeRoutePath = tor[0].path;
                links[4].name = permission.meta.title;
                // that.childrenRoutes(menuList, iTOperationManageRoutes);
                links[4].routes = tor;
                links[4].hidden = false;
                router.addRoutes(tor);
            } else if (links[5] && permission.redirect === links[5].bscode) {
                // 系统设置
                let tor = convertRouter2(menuList, "/Settings/dpYanshi", "/Settings");
                tor.push({
                    path: "/404",
                    component: () => import("@/views/404"),
                    routerPath: "@/views/404",
                    hidden: true
                });
                tor.push({ path: "*", redirect: "/404", hidden: true });
                // let homePage = tor[0].path;
                // if (tor[0].children && tor[0].children.length > 0) {
                //     homePage = homePage + "/" + tor[0].children[0].path;
                // }
                let homePage = "/";
                if (menuList[0] && menuList[0].path !== "") {
                    homePage = menuList[0].path;
                }
                links[5].homeRoutePath = homePage;
                // links[5].homeRoutePath = tor[0].path;
                links[5].name = permission.meta.title;
                // that.childrenRoutes(menuList, iTOperationManageRoutes);
                links[5].routes = tor;
                links[5].hidden = false;
                router.addRoutes(tor);
            }
        }
    }

    return links;
};
