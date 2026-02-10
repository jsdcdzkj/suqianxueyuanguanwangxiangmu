<template>
    <div>
        <Title :index="index" :title="title" :subTitle="subTitle" :subIndex="subIndex" />
        <el-table :data="list" border>
            <el-table-column label="设备类型" prop="type"></el-table-column>
            <el-table-column label="总数" prop="num"></el-table-column>
            <el-table-column label="启用数" prop="open_num"></el-table-column>
            <el-table-column label="正常" prop="normal"></el-table-column>
            <el-table-column label="失联" prop="unline"></el-table-column>
        </el-table>
    </div>
</template>

<script>
import Common from "@/components/ReportTemplate/common";
import { cmdInfo } from "@/api/report";
export default {
    cname: "终端情况",
    mixins: [Common],
	data(){
		return{
			list:[
				{
				    type: "微断设备",
				    num: 20,
				    open_num: 15,
				    normal: 15,
				    unline: 0
				},
				{
				    type: "烟感设备",
				    num: 13,
				    open_num: 13,
				    normal: 13,
				    unline: 0
				}
			]
		}
	},
	mounted(){
		this.$nextTick(async () => {
		    if (this.areaIds.length > 0) {
		       cmdInfo({
		       	"areaIds":this.areaIds,
		       	"startTime":this.startTime,
		       	"endTime":this.endTime,
		       	"timeType":this.timeTypes
		       }).then(({data})=>{
		       	var arr = [];
		       	for (let n in data.result) {
		       		arr.push({
		       			type:n,
		       			num:data.result[n][0],
		       			open_num:data.result[n][1],
		       			normal:data.result[n][2],
		       			unline:data.result[n][3],
		       		})
		       	}
		       	this.list = arr;
		       })
		    }
		});
	},
};
</script>

<style lang="scss" scoped></style>
