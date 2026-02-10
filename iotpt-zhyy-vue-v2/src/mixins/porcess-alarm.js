import { batchIgnore } from "@/api/warnInfo";
import { areaTreeList2 } from "@/api/smartEnergyReport";

export const processAlarm = {
    data() {
        return {
            alarms: [],
            queryForm: {
                pageNum: 1,
                pageSize: 20,
                areaId: "",
                areaName: "",
                warnLevel: "",
                handleStatus: "",
                floorId: "",
                startTime: "",
                endTime: ""
            },
            warnstatus: [
                {
                    value: "1",
                    label: "待响应"
                },
                {
                    value: "2",
                    label: "处理中"
                },
                {
                    value: "3",
                    label: "已处理"
                },
                {
                    value: "5",
                    label: "忽略"
                }
            ],
            warnLevels: [
                {
                    value: "1",
                    label: "告警"
                },
                {
                    value: "2",
                    label: "次要"
                },
                {
                    value: "3",
                    label: "重要"
                },
                {
                    value: "4",
                    label: "紧急"
                }
            ],
            logicalAreaIds: [],
            logicalAreaList: [],
            timer: []
        };
    },
    methods: {
        processForm() {
            if (this.logicalAreaIds.length > 0) {
                this.queryForm.floorId = this.logicalAreaIds[1];
                // this.queryForm.areaId = this.logicalAreaIds[2];
            }
            if (this.timer.length > 0) {
                this.queryForm.startTime = this.timer[0];
                this.queryForm.endTime = this.timer[1];
            }
        },
        clearForm() {
            this.logicalAreaIds = [];
            this.timer = [];
            this.queryForm.handleStatus = "";
            this.queryForm.areaId = "";
            this.queryForm.floorId = "";
            (this.queryForm.startTime = ""), (this.queryForm.endTime = "");
        },
        // 区域列表数据
        areaTreeList2() {
            areaTreeList2().then((res) => {
                if (res.code == 0) {
                    for (let index = 0; index < res.data[0].children.length; index++) {
                        delete res.data[0].children[index].children;
                    }
                    this.logicalAreaList = res.data;
                }
            });
        },
        handleSelectionChange(select) {
            this.alarms = select;
        },
        processAlarm() {
            if (this.alarms.length > 0) {
                batchIgnore({ ids: this.alarms.map((item) => item.id) }).then((res) => {
                    this.$message.success("处理成功");
                    this.queryData();
                });
            } else {
                return this.$message.error("请先选择");
            }
        }
    },
    created() {
        this.areaTreeList2();
    }
};
