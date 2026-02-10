export let menuTemp = [
    {
        "id": "61",
        "parentId": "0",
        "path": "/",
        "component": "Layout",
        "name": "/",
        "redirect": "index",
        "meta": {
            "title": "首页/",
            "icon": null
        },
        "children": [
            {
                "id": "62",
                "parentId": "61",
                "path": "index",
                "component": "index/index",
                "name": "Index",
                "redirect": null,
                "meta": {
                    "title": "首页",
                    "icon": "chess-king"
                },
                "children": []
            }
        ]
    },
    {
        "id": "1",
        "parentId": "0",
        "path": "/systemmanager",
        "component": "Layout",
        "name": "systemmanager",
        "redirect": "noRedirect",
        "meta": {
            "title": "系统管理",
            "icon": "cog"
        },
        "children": [
            {
                "id": "3",
                "parentId": "1",
                "path": "space",
                "component": "EmptyLayout",
                "name": "space",
                "redirect": "noRedirect",
                "meta": {
                    "title": "空间管理",
                    "icon": "building"
                },
                "children": [
                    {
                        "id": "4",
                        "parentId": "3",
                        "path": "buildingSpace",
                        "component": "system/spaceManagement/index",
                        "name": "buildingSpace",
                        "redirect": null,
                        "meta": {
                            "title": "楼宇管理",
                            "icon": "door-open"
                        },
                        "children": []
                    }
                ]
            },
            {
                "id": "73",
                "parentId": "1",
                "path": "orgin",
                "component": "EmptyLayout",
                "name": "orgin",
                "redirect": "noRedirect",
                "meta": {
                    "title": "组织机构",
                    "icon": "network-wired"
                },
                "children": [
                    {
                        "id": "74",
                        "parentId": "73",
                        "path": "unit",
                        "component": "system/organizeManagement/index",
                        "name": "unit",
                        "redirect": null,
                        "meta": {
                            "title": "单位管理",
                            "icon": "puzzle-piece"
                        },
                        "children": []
                    }
                ]
            },
            {
                "id": "63",
                "parentId": "1",
                "path": "menumanger",
                "component": "system/menuManagement/index",
                "name": "menumanger",
                "redirect": null,
                "meta": {
                    "title": "菜单管理",
                    "icon": "list"
                },
                "children": []
            },
            {
                "id": "64",
                "parentId": "1",
                "path": "rolemanger",
                "component": "system/roleManagement/index",
                "name": "rolemanger",
                "redirect": null,
                "meta": {
                    "title": "角色管理",
                    "icon": "users"
                },
                "children": []
            },
            {
                "id": "65",
                "parentId": "1",
                "path": "usermanger",
                "component": "system/userManagement/index",
                "name": "usermanger",
                "redirect": null,
                "meta": {
                    "title": "用户管理",
                    "icon": "user"
                },
                "children": []
            },
            {
                "id": "66",
                "parentId": "1",
                "path": "zidianmanger",
                "component": "system/dictionary/index",
                "name": "zidianmanger",
                "redirect": null,
                "meta": {
                    "title": "字典管理",
                    "icon": "book"
                },
                "children": []
            },
            {
                "id": "67",
                "parentId": "1",
                "path": "rizhimanger",
                "component": "system/log/index",
                "name": "rizhimanger",
                "redirect": null,
                "meta": {
                    "title": "日志管理",
                    "icon": "bookmark"
                },
                "children": []
            }
        ]
    },
    {
                "id": "81",
                "parentId": "0",
                "path": "/equipmentCenter",
                "component": "Layout",
                "name": "equipmentCenter",
                "redirect": "noRedirect",
                "meta": {
                    "title": "配置中心",
                    "icon": "record-vinyl"
                },
                "children": [
                    
                    {
                        "id": "83",
                        "parentId": "81",
                        "path": "mjsb",
                        "component": "equipmentCenter/mjsb/index",
                        "name": "mjsb",
                        "redirect": null,
                        "meta": {
                            "title": "门禁配置",
                            "icon": "bell"
                        },
                        "children": []
                    },
                    {
                        "id": "82",
                        "parentId": "81",
                        "path": "cjzd",
                        "component": "equipmentCenter/cjzd/index",
                        "name": "cjzd",
                        "redirect": null,
                        "meta": {
                            "title": "道闸配置",
                            "icon": "allergies"
                        },
                        "children": []
                    },
                    {
                        "id": "84",
                        "parentId": "81",
                        "path": "spsb",
                        "component": "equipmentCenter/spsb/index",
                        "name": "spsb",
                        "redirect": null,
                        "meta": {
                            "title": "视频配置",
                            "icon": "file-video"
                        },
                        "children": []
                    },
                    {
                        "id": "93",
                        "parentId": "92",
                        "path": "service",
                        "component": "infomationServer/service/index",
                        "name": "service",
                        "redirect": null,
                        "meta": {
                            "title": "消息服务",
                            "icon": "chess-queen"
                        },
                        "children": []
                    },
                ]
            },
            {
                "id": "86",
                "parentId": "0",
                "path": "/warningCenter",
                "component": "Layout",
                "name": "warningCenter",
                "redirect": "noRedirect",
                "meta": {
                    "title": "配置规则",
                    "icon": "bug"
                },
                "children": [
                    {
                        "id": "88",
                        "parentId": "86",
                        "path": "warningset",
                        "component": "warningCenter/configuration/index",
                        "name": "warningset",
                        "redirect": null,
                        "meta": {
                            "title": "告警配置",
                            "icon": "file-medical-alt"
                        },
                        "children": []
                    },
                    
                ]
            },
            {
                "id": "70",
                "parentId": "0",
                "path": "/configset",
                "component": "Layout",
                "name": "configset",
                "redirect": "noRedirect",
                "meta": {
                    "title": "配置管理",
                    "icon": "crosshairs"
                },
                "children": [
                    {
                        "id": "80",
                        "parentId": "70",
                        "path": "gyxmanager",
                        "component": "configSet/gyxManager/index",
                        "name": "gyxmanager",
                        "redirect": null,
                        "meta": {
                            "title": "供应商管理",
                            "icon": "window-maximize"
                        },
                        "children": []
                    }
                ]
            },
]