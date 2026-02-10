<template>
	<StructureCard title="运营安全评估">
		<template #actions>
			<ElTooltip class="item" effect="dark" content="Top Center 提示文字" placement="top">
				<template #default>
					<ElIcon style="color: rgba(0,0,0,0.45); font-size: 20px"><InfoFilled /></ElIcon>
				</template>
				<template #content>
					<div style="line-height: 18px">
						<span style="font-weight: bold">功能完整性（满分5分）</span>
						<br />

						计算公式：功能完整性得分=实际实现的功能数量/计划实现的功能数量×5
						<br />

						说明：统计智慧楼宇系统实际实现的功能与计划功能的比值，评估系统功能的完备程度。
						<br />
						<span style="font-weight: bold">点位覆盖率（满分5分）</span>

						<br />

						计算公式：点位覆盖率得分=实际覆盖的点位数量/应覆盖的点位数量×5
						<br />

						说明：计算实际安装传感器、监控设备等覆盖的点位与应覆盖点位的比例，反映监控的全面性。
						<br />
						<span style="font-weight: bold">有效报警率（满分5分）</span>

						<br />

						计算公式：有效报警率得分=有效报警次数/总报警次数×5
						<br />

						说明：有效报警指真实的安全事件报警，统计有效报警在总报警中的占比，衡量报警系统的准确性。
						<br />
						<span style="font-weight: bold">报警处置率（满分5分）</span>

						<br />

						计算公式：报警处置率得分=已处置的报警次数/总报警次数×5
						<br />

						说明：评估对报警的响应和处理情况，计算已处置报警与总报警的比率。
						<br />
						<span style="font-weight: bold">处置及时率（满分5分）</span>

						<br />

						计算公式：处置及时率得分=及时处置的报警次数/应处置的报警次数×5
						<br />

						说明：及时处置指在规定时间内完成的报警处理，统计及时处置的报警占比，反映响应速度。
						<br />
						<span style="font-weight: bold">设备在线率（满分5分）</span>
						<br />
						计算公式：设备在线率得分=设备在线数量/总设备数量×5
						<br />
						说明：计算设备正常运行时间与总运行时间的比例，体现设备的稳定性和可靠性。
						<br />
					</div>
				</template>
			</ElTooltip>
		</template>
		<div class="flex" style="height: 100%">
			<div style="width:200px">
				<ElSelect placeholder="请选择评估指标">
					<ElOption label="指标1" value="1" />
					<ElOption label="指标2" value="2" />
					<ElOption label="指标3" value="3" />
				</ElSelect>
				<div class="rate-box">
					<div>综合评分：
						<span class="score">S</span>
						<ElTooltip class="item" effect="dark" content="Top Center 提示文字" placement="top">
                                <template #default>
									<ElIcon style="color: rgba(0,0,0,0.45); font-size: 17px"><InfoFilled /></ElIcon>
                                </template>
                                <template #content>
                                    <div style="line-height: 18px">
                                        <span style="font-weight: bold">规则：</span>
                                        <br />
                                        根据6元素相加得到综合分，
                                        <br />
                                        每个元素为5分，共30分,
                                        <br />
                                        分别对应7个评分等级。
                                        <br />
                                        具体为：
                                        <br />

                                        S=30
                                        <br />

                                        25≤A&lt;30
                                        <br />

                                        20≤B&lt;25
                                        <br />

                                        15≤C&lt;20
                                        <br />

                                        10≤D&lt;15
                                        <br />

                                        5≤E&lt;10
                                        <br />
                                        0≤F&lt;5
                                    </div>
                                </template>
                            </ElTooltip>
					</div>
					<img class="robat" src="/src/assets/images/alarm-center/robat.png" alt="">
				</div>
				<div class="suggestion">
					<div class="suggestion-title">建议：</div>
					<div class="suggestion-content">建议内容建议内容建议内容建议内容建议内容建议内容建议内容</div>
				</div>
			</div>
			<!-- 告警类型统计的图表 -->
			<div class="flex-1" style="height: 100%;">

				<AlarmSafeRadar />
			</div>
		</div>
	</StructureCard>
</template>

<script setup lang="tsx">
import StructureCard from "@/components/card/StructureCard";
import { ElSelect, ElOption, ElTooltip, ElIcon } from "element-plus";
import { InfoFilled } from "@element-plus/icons-vue";
import AlarmSafeRadar from "../echarts/alarmSafeRadar.vue";

</script>
<style lang="scss" scoped>
.rate-box {
	display: flex;
	align-items: center;
	position:relative;
	margin: 12px 0;
	padding:12px 0;
	font-weight: bold;
	font-size: 20px;
	color: rgba(0,0,0,0.85);
	border-top: 1px solid rgba(0,0,0,0.06);
	border-bottom: 1px solid rgba(0,0,0,0.06);

	.score {
		font-weight: bold;
		font-size: 20px;
		line-height: 20px;
		color:#50A0FF;
		margin-right:4px;
	}

	.robat {
		position: absolute;
		right:0;
		top:0;
		width: 66px;
		height: 100px;
	}
}

.suggestion {
	font-weight: bold;
	font-size: 20px;
	color: rgba(0,0,0,0.85);
	.suggestion-content {
		margin-top:8px;
		font-size: 12px;
		color: rgba(0,0,0,0.85);
		line-height:20px;
		font-weight: normal;
	}

}


</style>