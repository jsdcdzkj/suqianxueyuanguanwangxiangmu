<template>
    <div>
        <Title :index="index" :title="title" :subTitle="subTitle" :subIndex="subIndex" />
        <div class="module-item-desc">
            本周期内，共有
            <span class="num">{{dataInfo.allTaskTotal}}</span>
            次计划任务，其中
            <span class="num">{{dataInfo.errorTaskTotal}}</span>
            次发现异常情况存在，当前存在
            <span class="num">{{dataInfo.unTaskTotal}}</span>
            次未完成的计划任务 。
        </div>
        <el-table :data="dataInfo.allTask" border>
            <el-table-column label="任务名称" prop="title"></el-table-column>
            <el-table-column label="任务类型" prop="taskTypeName"></el-table-column>
            <el-table-column label="紧急程度" prop="urgencyName"></el-table-column>
            <el-table-column label="任务状态" prop="statesName"></el-table-column>
            <el-table-column label="任务班组" prop="groupName"></el-table-column>
            <el-table-column label="计划开始时间" prop="deadDate"></el-table-column>
            <el-table-column label="实际巡检时间" prop="acreateTime"></el-table-column>
        </el-table>
    </div>
</template>

<script>
import Common from "@/components/ReportTemplate/common";
import { taskInfo } from "@/api/report";
export default {
    cname: "巡检记录",
    mixins: [Common],
    
    data(){
		return{
			dataInfo:{
                allTaskTotal: 2,
                errorTaskTotal: 1,
                unTaskTotal: 1,
                allTask:[{
                    title: "巡检计划2023",
                    taskTypeName: "巡检",
                    urgencyName: "常规",
                    statesName: "已完成",
                    groupName: "一班组",
                    deadDate: "2024-02-21 14:50",
                    acreateTime: "2024-02-21 14:50"
                },
                {
                    title: "巡检计划2023",
                    taskTypeName: "巡检",
                    urgencyName: "常规",
                    statesName: "未完成",
                    groupName: "一班组",
                    deadDate: "2024-02-21 14:50",
                    acreateTime: "2024-02-21 14:50"
                }]
			}
		}
	},
    mounted(){
        this.$nextTick(async () => {
            if (this.areaIds.length > 0) {
                taskInfo({
                    "areaIds":this.areaIds,
                    "startTime":this.startTime,
                    "endTime":this.endTime,
                    "timeType":this.timeTypes,
                    "splType":this.type
                }).then(res=>{
                    this.dataInfo = res.data;
                })
            }
        });
		
	}
};
</script>

<style lang="scss" scoped></style>
