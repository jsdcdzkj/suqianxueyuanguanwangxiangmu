<template>
	<StructureCard title="告警处置">
		<ElRow :gutter="8" style="height: 100%">
			<ElCol :span="12">
				<div class="deal-card flex items-center">
					<img src="@/assets/images/alarm-center/alarm-time.png" alt="">
					<div>
						<div class="deal-title">平均响应时间（分钟）</div>
						<div class="deal-num">
							177 
							<span class="deal-unit">分钟</span>
						</div>
					</div>
				</div>
			</ElCol>
			<ElCol :span="12">
				<div class="deal-card flex items-center">
					<img src="@/assets/images/alarm-center/alarm-rate.png" alt="">
					<div>
						<div class="deal-title">告警恢复率（百分比）</div>
						<div class="deal-num">
							68 
							<span class="deal-unit">%</span>
						</div>
					</div>
				</div>
			</ElCol>
			<ElCol :span="12">
				<div class="deal-card flex items-center" style="margin-top:4px;">
					<img src="@/assets/images/alarm-center/alarm-deal.png" alt="">
					<div>
						<div class="deal-title">平均处置时间（分钟）</div>
						<div class="deal-num">
							177 
							<span class="deal-unit">分钟</span>
						</div>
					</div>
				</div>
			</ElCol>
			<ElCol :span="12">
				<div class="deal-card flex items-center" style="margin-top:4px;">
					<img src="@/assets/images/alarm-center/alarm-num.png" alt="">
					<div>
						<div class="deal-title">每小时平均告警数</div>
						<div class="deal-num">
							177
							<span class="deal-unit">次</span>
						</div>
					</div>
				</div>
			</ElCol>
		</ElRow>
	</StructureCard>
</template>

<script setup lang="tsx">
import StructureCard from "@/components/card/StructureCard";
</script>
<style lang="scss" scoped>
.deal-card {
	background: linear-gradient( 0deg, #FFFFFF 0%, #F9FCFF 100%);
	box-shadow: 0px 0px 4px 0px rgba(46,119,221,0.1);
	border-radius: 12px 12px 12px 12px;
	border: 1px solid rgba(0,0,0,0.06);
	padding:18px;
	height: calc(100% - 4px);

	.deal-title {
		font-size: 14px;
		color: rgba(0,0,0,0.65);
		line-height: 22px;
		margin-bottom:8px;
	}
	.deal-num {
		font-size: 28px;
		color: rgba(0,0,0,0.85);
		line-height: 20px;
		text-align:left;
	}
	.deal-unit {
		font-size: 14px;
		color: rgba(0,0,0,0.65);
		line-height: 22px;	
	}


	img {
		width: 51px;
		height: 51px;
		margin-right: 14px;
	}
}

</style>
