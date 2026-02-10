<template>
    <div>
        <Title :index="index" :title="title" :subTitle="subTitle" :subIndex="subIndex" />
        <div class="module-item-desc">
            本周期内，共有 
            <span class="num">{{dataInfo.errorTaskTotal}}</span>
            条异常记录，其中
            <span class="num">{{dataInfo.yErrorTaskTotal}}</span>
            条已处理完成，
            <span class="num">{{dataInfo.unErrorTaskTotal}}</span>
            条未处理，请分析未处理的原因并及时处理。
        </div>
        <el-table :data="dataInfo.allErrorTask" border>
            <el-table-column label="任务名称" prop="title"></el-table-column>
            <el-table-column label="巡检人" prop="createUserName"></el-table-column>
            <el-table-column label="异常内容" prop="notes"></el-table-column>
            <el-table-column label="上报时间" prop="createTime"></el-table-column>
            <el-table-column label="异常状态" prop="statesName"></el-table-column>
        </el-table>
    </div>
</template>

<script>
import Common from "@/components/ReportTemplate/common";
import { taskErrorInfo } from "@/api/report";
export default {
    cname: "巡检异常情况",
    mixins: [Common],
   
    data(){
		return{
			dataInfo:{
                errorTaskTotal: 2,
                yErrorTaskTotal: 1,
                unErrorTaskTotal: 1,
                allErrorTask: [{
                    title: "巡检计划2023",
                    createUserName: "张建设",
                    notes: "电气火灾告警",
                    createTime: "2024-02-21 14:50",
                    statesName: "已处理"
                },
                {
                    title: "巡检计划2023",
                    createUserName: "张建设",
                    notes: "电气火灾告警",
                    createTime: "2024-02-21 14:50",
                    statesName: "未处理"
                },]
			}
		}
	},
    mounted(){
        this.$nextTick(async () => {
            if (this.areaIds.length > 0) {
                taskErrorInfo({
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

<style></style>
