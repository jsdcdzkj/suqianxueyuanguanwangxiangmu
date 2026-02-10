const getters = {
    sidebar: (state) => state.app.sidebar,
    device: (state) => state.app.device,
    routesData: (state) => state.app.routesData,
    token: (state) => state.user.token,
    avatar: (state) => state.user.avatar,
    name: (state) => state.user.name,
    routes: (state) => state.user.routes,
    userId: (state) => state.user.userId,
    notPermission: (state) => state.user.permissions.length <= 0
};
export default getters;
