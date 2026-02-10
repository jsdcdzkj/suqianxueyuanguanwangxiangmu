// 物联监管路由
import Layout from "@/layout/index.vue";

export const ioTSupervisionRoutes = [
    {
        path: '/ioTSupervision',
        component: () => import('@/layout/index.vue'),
        redirect: '/ioTSupervision/equipmentMonitoring',
        routerPath: '@/views/ioTSupervision/equipmentMonitoring/index',
        children: [{
            path: '/ioTSupervision/equipmentMonitoring',
            name: '设备监控',
            component: () => import('@/views/ioTSupervision/equipmentMonitoring/index'),
            meta: { title: '设备监控', icon: 'satellite-dish' }
        }]
    },
    {
        path: '/ioTSupervision/lineNetwork',
        component: () => import('@/layout/index.vue'),
        routerPath: '@/views/ioTSupervision/lineNetwork/index',
        children: [
            {
                path: 'index',
                name: '线网拓扑',
                component: () => import('@/views/ioTSupervision/lineNetwork/index'),
                meta: { title: '线网拓扑', icon: 'network-wired' }
            }
        ]
    },
    {
        path: '/ioTSupervision1',
        component: () => import('@/layout/index.vue'),
        // redirect: '/ioTSupervision/equipmentMonitoring',
        routerPath: '@/views/ioTSupervision/equipmentMonitoring/index',
        children: [{
            path: '/ioTSupervision/equipmentMonitoring_200001',
            name: '照明设备',
            component: () => import('@/views/ioTSupervision/equipmentMonitoring/index'),
            meta: { title: '照明设备', icon: 'satellite-dish' }
        }]
    },
    {
        path: '/ioTSupervision2',
        component: () => import('@/layout/index.vue'),
        // redirect: '/ioTSupervision/equipmentMonitoring',
        routerPath: '@/views/ioTSupervision/equipmentMonitoring/index',
        children: [{
            path: '/ioTSupervision/equipmentMonitoring_10001',
            name: '边缘网关',
            component: () => import('@/views/ioTSupervision/equipmentMonitoring/index'),
            meta: { title: '边缘网关', icon: 'satellite-dish' }
        }]
    },
    {
        path: '/ioTSupervision3',
        component: () => import('@/layout/index.vue'),
        // redirect: '/ioTSupervision/equipmentMonitoring',
        routerPath: '@/views/ioTSupervision/equipmentMonitoring/index',
        children: [{
            path: '/ioTSupervision/equipmentMonitoring_10002',
            name: '空调',
            component: () => import('@/views/ioTSupervision/equipmentMonitoring/index'),
            meta: { title: '空调', icon: 'satellite-dish' }
        }]
    },
    {
        path: '/ioTSupervision4',
        component: () => import('@/layout/index.vue'),
        // redirect: '/ioTSupervision/equipmentMonitoring',
        routerPath: '@/views/ioTSupervision/equipmentMonitoring/index',
        children: [{
            path: '/ioTSupervision/equipmentMonitoring_10003',
            name: '温湿度',
            component: () => import('@/views/ioTSupervision/equipmentMonitoring/index'),
            meta: { title: '温湿度', icon: 'satellite-dish' }
        }]
    },
    {
        path: '/ioTSupervision5',
        component: () => import('@/layout/index.vue'),
        // redirect: '/ioTSupervision/equipmentMonitoring',
        routerPath: '@/views/ioTSupervision/equipmentMonitoring/index',
        children: [{
            path: '/ioTSupervision/equipmentMonitoring_10004',
            name: '空气质量',
            component: () => import('@/views/ioTSupervision/equipmentMonitoring/index'),
            meta: { title: '空气质量', icon: 'satellite-dish' }
        }]
    },
    {
        path: '/ioTSupervision6',
        component: () => import('@/layout/index.vue'),
        // redirect: '/ioTSupervision/equipmentMonitoring',
        routerPath: '@/views/ioTSupervision/equipmentMonitoring/index',
        children: [{
            path: '/ioTSupervision/equipmentMonitoring_10005',
            name: '智能电表',
            component: () => import('@/views/ioTSupervision/equipmentMonitoring/index'),
            meta: { title: '智能电表', icon: 'satellite-dish' }
        }]
    },
    {
        path: '/ioTSupervision7',
        component: () => import('@/layout/index.vue'),
        // redirect: '/ioTSupervision/equipmentMonitoring',
        routerPath: '@/views/ioTSupervision/equipmentMonitoring/index',
        children: [{
            path: '/ioTSupervision/equipmentMonitoring_10006',
            name: '智能水表',
            component: () => import('@/views/ioTSupervision/equipmentMonitoring/index'),
            meta: { title: '智能水表', icon: 'satellite-dish' }
        }]
    },
    {
        path: '/ioTSupervision8',
        component: () => import('@/layout/index.vue'),
        // redirect: '/ioTSupervision/equipmentMonitoring',
        routerPath: '@/views/ioTSupervision/equipmentMonitoring/index',
        children: [{
            path: '/ioTSupervision/equipmentMonitoring_10007',
            name: '智能空开',
            component: () => import('@/views/ioTSupervision/equipmentMonitoring/index'),
            meta: { title: '智能空开', icon: 'satellite-dish' }
        }]
    },
    {
        path: '/ioTSupervision9',
        component: () => import('@/layout/index.vue'),
        // redirect: '/ioTSupervision/equipmentMonitoring',
        routerPath: '@/views/ioTSupervision/equipmentMonitoring/index',
        children: [{
            path: '/ioTSupervision/equipmentMonitoring_10009',
            name: '管道压力',
            component: () => import('@/views/ioTSupervision/equipmentMonitoring/index'),
            meta: { title: '管道压力', icon: 'satellite-dish' }
        }]
    },
    {
        path: '/ioTSupervision10',
        component: () => import('@/layout/index.vue'),
        // redirect: '/ioTSupervision/equipmentMonitoring',
        routerPath: '@/views/ioTSupervision/equipmentMonitoring/index',
        children: [{
            path: '/ioTSupervision/equipmentMonitoring_10010',
            name: '风机',
            component: () => import('@/views/ioTSupervision/equipmentMonitoring/index'),
            meta: { title: '风机', icon: 'satellite-dish' }
        }]
    },
    {
        path: '/ioTSupervision11',
        component: () => import('@/layout/index.vue'),
        // redirect: '/ioTSupervision/equipmentMonitoring',
        routerPath: '@/views/ioTSupervision/equipmentMonitoring/index',
        children: [{
            path: '/ioTSupervision/equipmentMonitoring_10011',
            name: '门禁',
            component: () => import('@/views/ioTSupervision/equipmentMonitoring/index'),
            meta: { title: '门禁', icon: 'satellite-dish' }
        }]
    },
    {
        path: '/ioTSupervision12',
        component: () => import('@/layout/index.vue'),
        // redirect: '/ioTSupervision/equipmentMonitoring',
        routerPath: '@/views/ioTSupervision/equipmentMonitoring/index',
        children: [{
            path: '/ioTSupervision/equipmentMonitoring_10012',
            name: '摄像头',
            component: () => import('@/views/ioTSupervision/equipmentMonitoring/index'),
            meta: { title: '摄像头', icon: 'satellite-dish' }
        }]
    },
    {
        path: '/ioTSupervision13',
        component: () => import('@/layout/index.vue'),
        // redirect: '/ioTSupervision/equipmentMonitoring',
        routerPath: '@/views/ioTSupervision/equipmentMonitoring/index',
        children: [{
            path: '/ioTSupervision/equipmentMonitoring_10013',
            name: '人体感应',
            component: () => import('@/views/ioTSupervision/equipmentMonitoring/index'),
            meta: { title: '人体感应', icon: 'satellite-dish' }
        }]
    },
]


// 智慧安防路由
export const intelligentSecurityRoutes = [
    {
        path: '/intelligentSecurity/',
        component: () => import('@/layout/index.vue'),
        redirect: '/intelligentSecurity/videoMonitoring',
        routerPath: '@/views/intelligentSecurity/videoMonitoring/index',
        children: [{
            path: '/intelligentSecurity/videoMonitoring',
            name: '视频监控',
            component: () => import('@/views/intelligentSecurity/videoMonitoring/index'),
            meta: { title: '视频监控', icon: 'video' }
        }]
    },
    {
        path: '/intelligentSecurity/',
        component: () => import('@/layout/index.vue'),
        redirect: '/intelligentSecurity/videoPlayback',
        routerPath: '@/views/intelligentSecurity/videoPlayback/index',
        children: [{
            path: '/intelligentSecurity/videoPlayback',
            name: '视频回放',
            component: () => import('@/views/intelligentSecurity/videoPlayback/index'),
            meta: { title: '视频回放', icon: 'video' }
        }]
    },
    {
        path: '/intelligentSecurity/alarmCenter',
        component: () => import('@/layout/index.vue'),
        redirect: '/intelligentSecurity/alarmCenter',
        name: '告警中心',
        meta: { title: '告警中心', icon: 'bell' },
        routerPath: '',
        children: [
            {
                path: 'realTime',
                name: '实时告警',
                routerPath: '@/views/intelligentSecurity/alarmCenter/realTime/index',
                component: () => import('@/views/intelligentSecurity/alarmCenter/realTime/index'),
                meta: { title: '实时告警', icon: 'bullhorn' }
            },
            {
                path: 'history',
                name: '历史告警',
                routerPath: '@/views/intelligentSecurity/alarmCenter/history/index',
                component: () => import('@/views/intelligentSecurity/alarmCenter/history/index'),
                meta: { title: '历史告警', icon: 'calendar-alt' }
            },
            {
                path: 'statistic',
                name: '统计分析',
                routerPath: '@/views/intelligentSecurity/alarmCenter/statistic/index',
                component: () => import('@/views/intelligentSecurity/alarmCenter/statistic/index'),
                meta: { title: '统计分析', icon: 'chart-pie' }
            },
        ]
    },
    {
        path: '/smartEnergy/electricalSafety',
        component: Layout,
        redirect: '/smartEnergy/electricalSafety/safetyAnalysis',
        name: '电气安全',
        meta: { title: '电气安全', icon: 'charging-station' },
        routerPath: '',
        children: [
            {
                path: 'safetyAnalysis',
                name: '电气安全分析',
                routerPath: '@/views/smartEnergy/electricalSafety/safetyAnalysis/index',
                component: () => import('@/views/smartEnergy/electricalSafety/safetyAnalysis/index'),
                meta: { title: '电气安全分析', icon: 'file-medical-alt' }
            },
            {
                path: 'safetyReport',
                name: '电气安全报表',
                routerPath: '@/views/smartEnergy/electricalSafety/safetyReport/index',
                component: () => import('@/views/smartEnergy/electricalSafety/safetyReport/index'),
                meta: { title: '电气安全报表', icon: 'file-alt' }
            }
        ]
    },
    {
        path: '/intelligentSecurity/peopleServices',
        component: () => import('@/layout/index.vue'),
        redirect: '/intelligentSecurity/peopleServices',
        routerPath: '@/views/intelligentSecurity/peopleServices/index',
        children: [{
            path: '/intelligentSecurity/peopleServices',
            name: '人行服务',
            component: () => import('@/views/intelligentSecurity/peopleServices/index'),
            meta: { title: '人行服务', icon: 'walking' }
        }]
    },
    {
        path: '/intelligentSecurity/carServices',
        component: () => import('@/layout/index.vue'),
        redirect: '/intelligentSecurity/carServices',
        routerPath: '@/views/intelligentSecurity/carServices/index',
        children: [{
            path: '/intelligentSecurity/carServices',
            name: '车行服务',
            component: () => import('@/views/intelligentSecurity/carServices/index'),
            meta: { title: '车行服务', icon: 'car' }
        }]
    },
    {
        path: '/intelligentSecurity/emergencyPlan',
        component: () => import('@/layout/index.vue'),
        redirect: '/intelligentSecurity/emergencyPlan',
        routerPath: '@/views/intelligentSecurity/emergencyPlan/index',
        children: [{
            path: '/intelligentSecurity/emergencyPlan',
            name: '应急预案',
            component: () => import('@/views/intelligentSecurity/emergencyPlan/index'),
            meta: { title: '应急预案', icon: 'book' }
        }]
    },

]




// 运维管理路由
export const iTOperationManageRoutes = [
    {
        path: '/iTOperationManage',
        component: () => import('@/layout/index.vue'),
        redirect: '/iTOperationManage/mytask',
        routerPath: '@/views/iTOperationManage/mytask/index',
        children: [{
            path: '/iTOperationManage/mytask',
            name: '我的任务',
            component: () => import('@/views/iTOperationManage/mytask/index'),
            meta: { title: '我的任务', icon: 'bookmark' }
        }]
    },
    {
        path: '/iTOperationManage/taskMange',
        component: () => import('@/layout/index.vue'),
        redirect: '/iTOperationManage/taskMange',
        name: '任务管理',
        meta: { title: '任务管理', icon: 'layer-group' },
        routerPath: '',
        children: [
            {
                path: 'inspectionItems',
                name: '检查项模板',
                routerPath: '@/views/iTOperationManage/taskMange/inspectionItems/index',
                component: () => import('@/views/iTOperationManage/taskMange/inspectionItems/index'),
                meta: { title: '检查项模板', icon: 'scroll' }
            },
            // {
            //     path: 'standard',
            //     name: '标准管理',
            //     routerPath: '@/views/iTOperationManage/taskMange/standard/index',
            //     component: () => import('@/views/iTOperationManage/taskMange/standard/index'),
            //     meta: { title: '标准管理', icon: 'paste' }
            // },
            {
                path: 'taskPlan',
                name: '任务计划',
                routerPath: '@/views/iTOperationManage/taskMange/taskPlan/index',
                component: () => import('@/views/iTOperationManage/taskMange/taskPlan/index'),
                meta: { title: '任务计划', icon: 'table' }
            },
            {
                path: 'taskRecord',
                name: '任务记录',
                routerPath: '@/views/iTOperationManage/taskMange/taskRecord/index',
                component: () => import('@/views/iTOperationManage/taskMange/taskRecord/index'),
                meta: { title: '任务记录', icon: 'tasks' }
            },
        ]
    },
    {
        path: '/iTOperationManage/statisticalReport',
        component: () => import('@/layout/index.vue'),
        redirect: '/iTOperationManage/statisticalReport',
        routerPath: '@/views/iTOperationManage/statisticalReport/index',
        children: [{
            path: '/iTOperationManage/statisticalReport',
            name: '统计报表',
            component: () => import('@/views/iTOperationManage/statisticalReport/index'),
            meta: { title: '统计报表', icon: 'chart-area' }
        }]
    },
    {
        path: '/iTOperationManage/abnormalInspection',
        component: () => import('@/layout/index.vue'),
        redirect: '/iTOperationManage/abnormalInspection',
        routerPath: '@/views/iTOperationManage/abnormalInspection/index',
        children: [{
            path: '/iTOperationManage/abnormalInspection',
            name: '巡检异常',
            component: () => import('@/views/iTOperationManage/abnormalInspection/index'),
            meta: { title: '巡检异常', icon: 'exclamation-triangle' }
        }]
    },
    {
        path: '/iTOperationManage/knowledgeBase',
        component: () => import('@/layout/index.vue'),
        redirect: '/iTOperationManage/knowledgeBase',
        routerPath: '@/views/iTOperationManage/knowledgeBase/index',
        children: [{
            path: '/iTOperationManage/knowledgeBase',
            name: '知识库',
            component: () => import('@/views/iTOperationManage/knowledgeBase/index'),
            meta: { title: '知识库', icon: 'graduation-cap' }
        }]
    },
    {
        path: '/iTOperationManage/teamMange',
        component: () => import('@/layout/index.vue'),
        redirect: '/iTOperationManage/teamMange',
        routerPath: '@/views/iTOperationManage/teamMange/index',
        children: [{
            path: '/iTOperationManage/teamMange',
            name: '班组管理',
            component: () => import('@/views/iTOperationManage/teamMange/index'),
            meta: { title: '班组管理', icon: 'project-diagram' }
        }]
    },

]

// 运营管理路由
export const OperationManageRoutes = [    
    {
        path: '/OperationManage',
        component: () => import('@/layout/index.vue'),
        redirect: '/OperationManage/meeting',
        name: '会议管理',
        meta: { title: '会议管理', icon: 'layer-group' },
        routerPath: '',
        children: [
            {
                path: '/OperationManage',
                name: '会议室预约',
                routerPath: '@/views/OperationManage/meeting/MeetingAppointment/index',
                component: () => import('@/views/OperationManage/meeting/MeetingAppointment/index'),
                meta: { title: '会议室预约', icon: 'scroll' }
            },           
            {
                path: '/StatisticAnalysis',
                name: '会议统计分析',
                routerPath: '@/views/OperationManage/meeting/StatisticAnalysis/index',
                component: () => import('@/views/OperationManage/meeting/StatisticAnalysis/index'),
                meta: { title: '会议统计分析', icon: 'table' }
            },
            {
                path: 'MeetingSet',
                name: '会议室配置',
                routerPath: '@/views/OperationManage/meeting/MeetingSet/index',
                component: () => import('@/views/OperationManage/meeting/MeetingSet/index'),
                meta: { title: '会议室配置', icon: 'tasks' }
            },
        ]
    },
    {
        path: '/OperationManage/LifeManage',
        component: () => import('@/layout/index.vue'),
        redirect: '/OperationManage/LifeManage',
        routerPath: '@/views/OperationManage/LifeManage/index',
        children: [{
            path: '/OperationManage/LifeManage',
            name: '生活服务',
            component: () => import('@/views/OperationManage/LifeManage/index'),
            meta: { title: '生活服务', icon: 'chart-area' }
        }]
    },
    {
        path: '/OperationManage/AssetManage',
        component: () => import('@/layout/index.vue'),
        redirect: '/OperationManage/AssetManage',
        routerPath: '@/views/OperationManage/AssetManage/index',
        children: [{
            path: '/OperationManage/AssetManage',
            name: '资产管理',
            component: () => import('@/views/OperationManage/AssetManage/index'),
            meta: { title: '资产管理', icon: 'exclamation-triangle' }
        }]
    },
    {
        path: '/OperationManage/CustomerServiceCenter',
        component: () => import('@/layout/index.vue'),
        redirect: '/OperationManage/CustomerServiceCenter',
        routerPath: '@/views/OperationManage/CustomerServiceCenter/index',
        children: [{
            path: '/OperationManage/CustomerServiceCenter',
            name: '客服中心',
            component: () => import('@/views/OperationManage/CustomerServiceCenter/index'),
            meta: { title: '客服中心', icon: 'graduation-cap' }
        }]
    },
    {
        path: '/OperationManage/SiteManage',
        component: () => import('@/layout/index.vue'),
        redirect: '/OperationManage/SiteManage',
        routerPath: '@/views/OperationManage/SiteManage/index',
        children: [{
            path: '/OperationManage/SiteManage',
            name: '场地管理',
            component: () => import('@/views/OperationManage/SiteManage/index'),
            meta: { title: '场地管理', icon: 'project-diagram' }
        }]
    },
    {
        path: '/OperationManage/FileManage',
        component: () => import('@/layout/index.vue'),
        redirect: '/OperationManage/FileManage',
        routerPath: '@/views/iTOperationManage/FileManage/index',
        children: [{
            path: '/OperationManage/FileManage',
            name: '档案管理',
            component: () => import('@/views/OperationManage/FileManage/index'),
            meta: { title: '档案管理', icon: 'project-diagram' }
        }]
    },

]
