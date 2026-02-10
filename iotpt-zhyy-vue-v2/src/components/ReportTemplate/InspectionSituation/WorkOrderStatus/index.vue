<template>
    <div>
        <Title :index="index" :title="title" :subTitle="subTitle" :subIndex="subIndex" />
        <div class="module-item-desc">
            本周期内，共产生
            <span class="num">{{dataInfo.allMissionTotal}}</span>
            条工单，其中
            <span class="num">{{dataInfo.missionTotal}}</span>
            条已处理完成，当前存在
            <span class="num">{{dataInfo.unMissionTotal}}</span>
            条工单未处理。 工单平均响应时间
            <span class="num">{{dataInfo.responseMin}}</span>
            分钟，平均处理完成时间
            <span class="num">{{dataInfo.finishMin}}</span>
            分钟。
        </div>
    </div>
</template>

<script>
import Common from "@/components/ReportTemplate/common";
import { missionInfo } from "@/api/report";
export default {
    cname: "工单情况",
    mixins: [Common],
    data(){
		return{
			dataInfo:{
				allMissionTotal:114,
                missionTotal:102,
                unMissionTotal:12,
                responseMin:65,
                finishMin:164
			}
		}
	},
	mounted(){
		this.$nextTick(async () => {
		    if (this.areaIds.length > 0) {
		        missionInfo({
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
