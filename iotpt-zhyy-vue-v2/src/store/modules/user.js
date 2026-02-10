import Vue from "vue";
import { login, logout, getUserInfo } from "@/api/user";
import { getToken, setToken, removeToken } from "@/utils/auth";
import { resetRouter } from "@/router";
import { linksRouter } from "@/utils/router";
import { validate } from "@/api/common";

var title = "智慧应用平台";
var tokenName = "accessToken";
const getDefaultState = () => {
    return {
        token: getToken(),
        name: "",
        avatar: "",
        userId: localStorage.getItem("userId") || "",
        permissions: [],
        isManager: false,
        isNeedUpdatePwd: false
    };
};

const state = getDefaultState();

const mutations = {
    RESET_STATE: (state) => {
        Object.assign(state, getDefaultState());
    },
    SET_TOKEN: (state, token) => {
        state.token = token;
    },
    SET_NAME: (state, name) => {
        state.name = name;
    },
    SET_USERID: (state, userId) => {
        state.userId = userId;
        // localstorge.setItem()
        localStorage.setItem("userId", userId);
    },
    SET_AVATAR: (state, avatar) => {
        state.avatar = avatar;
    },
    RESET_USER: (state) => {
        removeToken();
        resetRouter();
        Object.assign(state, getDefaultState());
    }
};
var that = this;
const actions = {
    // user login
    async login({ commit, state }, userInfo) {
        return new Promise((resolve, reject) => {
            login(userInfo).then(({ data, code, msg }) => {
                const accessToken = data ? data.token.tokenValue : undefined;
                if (data && accessToken) {
                    const hour = new Date().getHours();
                    const thisTime =
                        hour < 8
                            ? "早上好"
                            : hour <= 11
                              ? "上午好"
                              : hour <= 13
                                ? "中午好"
                                : hour < 18
                                  ? "下午好"
                                  : "晚上好";
                    Vue.prototype.$baseNotify(`欢迎登录${title}`, `${thisTime}！`);
                    commit("SET_TOKEN", data.token.tokenValue);
                    commit("SET_USERID", data.user.id);
                    localStorage.setItem("userName", data.user.realName);
                    setToken(data.token.tokenValue);
                    if ("2" != userInfo.type) {
                        validate(userInfo.password)
                            .then((res) => {
                                if (res.code == -1) {
                                    state.isNeedUpdatePwd = true;
                                }
                            })
                            .catch((e) => {
                                console.log(e);
                            });
                    }
                    resolve();
                } else {
                    if (code == -1) {
                        Vue.prototype.$baseMessage(msg, "error");
                        reject();
                    } else {
                        Vue.prototype.$baseMessage(`登录接口异常，未正确返回${tokenName}...`, "error");
                        reject();
                    }
                }
            });
        });
    },

    // get user info
    getInfo({ commit, state }) {
        return new Promise((resolve, reject) => {
            getUserInfo(state.token)
                .then((response) => {
                    const { data } = response;
                    if (!data) {
                        return reject("验证失败，请重新登录.");
                    }
                    const { username, avatar, is_manager } = data;
                    state.isManager = is_manager == 1;
                    // 如果子路由为空需要过滤掉
                    let permissionList = response.data.permissions.filter((item) => item.children.length > 0);
                    const activeIndex = localStorage.getItem("alreadyRoutes__template__index") || 0;
                    
                    commit("SET_NAME", username);
                    commit("SET_AVATAR", avatar);
                    // 有权限
                    if (permissionList.length > 0) {
                        const permissions = linksRouter(permissionList);
                        state.permissions = permissions;
                        if (activeIndex < permissions.length) {
                            resolve(permissions[activeIndex]);
                        } else {
                            resolve(permissions[0]);
                        }
                    }
                    // 暂无权限
                    else {
                        state.permissions = [];
                        resolve(null);
                    }
                })
                .catch((error) => {
                    reject(error);
                });
        });
    },

    // user logout
    logout({ commit, state }) {
        return new Promise((resolve, reject) => {
            logout(state.token)
                .then(() => {
                    removeToken(); // must remove  token  first
                    resetRouter();
                    commit("RESET_STATE");
                    resolve();
                })
                .catch((error) => {
                    reject(error);
                });
        });
    },

    // remove token
    resetToken({ commit }) {
        return new Promise((resolve) => {
            removeToken();
            resetRouter();
            commit("RESET_STATE");
            resolve();
        });
    }
};

export default {
    namespaced: true,
    state,
    mutations,
    actions
};
