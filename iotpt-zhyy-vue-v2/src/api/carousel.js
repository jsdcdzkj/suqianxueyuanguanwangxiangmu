import request from "@/utils/request";

export function bannerList(data) {
    return request({
        url: "/carousel/getPageList",
        method: "post",
        data
    });
}

export function getNoticeList(data) {
    return request({
        url: "/carousel/getNoticeList",
        method: "post",
        data
    });
}

export function editBanner(data) {
    return request({
        url: "/carousel/saveOrUpdate",
        method: "post",
        data
    });
}

export function delBanner(data) {
    return request({
        url: "/carousel/deleteClockingRecord",
        method: "post",
        data
    });
}

export function getPositionDict(data) {
    return request({
        url: "/carousel/getPositionDict",
        method: "post",
        data
    });
}

export function updateTime(data) {
    return request({
        url: "/carousel/updateTime",
        method: "post",
        data,
        headers: {
            "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
        }
    });
}

export function recipeAdd(data) {
    return request.post("/recipe/toAdd.do", data);
}

export function getFoodStatic(data) {
    return request.post("/dish/getFoodStatic.do", data);
}

export function getFoodStaticView(data) {
    return request.post("/dish/getFoodStaticView.do", data);
}

export function dishList(data) {
    return request.post("/recipe/dishList.do", data);
}

export function selectReserveList(data) {
    return request.post("/food/selectReserveList", data);
}

//
export function foodExport(data) {
    return request.post("/food/export", data, { responseType: "blob" });
}

export function detailsFood(data) {
    return request.post("/food/detailsFood", data);
}

export function saveOrUpdConfig(data) {
    return request.post("/food/saveOrUpdConfig", data);
}

export function deptTreeList(data) {
    return request.post("/food/deptTreeList", data);
}

export function getLeader(params) {
    return request.get("/dish/getLeaderUser.do", params);
}

export function setLeader(data) {
    return request.post("/dish/saveLeaderUser.do", data);
}
