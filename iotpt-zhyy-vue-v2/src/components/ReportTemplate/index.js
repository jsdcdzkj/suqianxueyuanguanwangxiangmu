import BaseInfo from "./ProjectProfile/BaseInfo/index.vue";
import TerminalSituation from "./ProjectProfile/TerminalSituation/index.vue";
import Abnormal from "./InspectionSituation/Abnormal/index.vue";
import Records from "./InspectionSituation/Records/index.vue";
import ThreeUnbalancedAnalyses from "./ElectricalSafetyAnalysis/ThreeUnbalancedAnalyses/index.vue";
import Title from "./Title.vue";
import ElectricityConsumption from "./EnergyUseStatistics/ElectricityConsumption/index";
import LoadRateRanking from "./EnergyUseStatistics/LoadRateRanking/index";
import LoadRateAnalysis from "./EnergyUseStatistics/LoadRateAnalysis/index";
import TypesChart from "./AlarmAnalysis/AlarmType/index.vue";
import DeviceChart from "./AlarmAnalysis/AlarmDevice/index.vue";
import RankingChart from "./AlarmAnalysis/AlarmRank/index.vue";
import RankingChart100 from "./AlarmAnalysis/AlarmRank100/index.vue";
import WorkOrderStatus from "./InspectionSituation/WorkOrderStatus/index.vue";
import AlertHeader from "./AlertHeader.vue";

export default (Vue) => {
    Vue.component("Title", Title);
    Vue.component("BaseInfo", BaseInfo);
    Vue.component("TerminalSituation", TerminalSituation);
    Vue.component("Abnormal", Abnormal);
    Vue.component("RecordsAll", Records);
    Vue.component("ThreeUnbalancedAnalyses", ThreeUnbalancedAnalyses);
    Vue.component("ElectricityConsumption", ElectricityConsumption);
    Vue.component("LoadRateRanking", LoadRateRanking);
    Vue.component("LoadRateAnalysis", LoadRateAnalysis);
    Vue.component("TypesChart", TypesChart);
    Vue.component("DeviceChart", DeviceChart);
    Vue.component("RankingChart", RankingChart);
    Vue.component("WorkOrderStatus", WorkOrderStatus);
    Vue.component("AlertHeader", AlertHeader);
    Vue.component("RankingChart100", RankingChart100);
};

export const getTemplateList = () => {
    return [
        BaseInfo,
        TerminalSituation,
        Abnormal,
        Records,
        ThreeUnbalancedAnalyses,
        ElectricityConsumption,
        LoadRateRanking,
        LoadRateAnalysis,
        TypesChart,
        DeviceChart,
        RankingChart,
        WorkOrderStatus
    ]
        .map((item) => {
            const obj = Object.create({});
            Reflect.set(obj, "cname", item.cname);
            Reflect.set(obj, "name", item.name);
            return obj;
        })
        .concat(
            { name: "RankingChart100", cname: "区域告警排行TOP10" },
            { name: "Abnormal", cname: "异常情况" }
        );
};
