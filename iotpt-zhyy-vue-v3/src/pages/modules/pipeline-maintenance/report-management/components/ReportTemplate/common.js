export default {
    props: {
        index: {
            type: String,
            default: "1.1"
        },
        title: {
            type: String,
            default: "标题"
        },
        subTitle: {
            type: String,
            default: "标题"
        },
        subIndex: {
            type: Number,
            default: 0
        },
        startTime: {
            type: String,
            default: "2024-01-01"
        },
        endTime: {
            type: String,
            default: "2024-01-31"
        },
        areaIds: {
            type: Array,
            default: () => []
        },
        type: {
            type: Number,
            default: 1
        },
        timeType: {
            type: String,
            default: "日"
        },
        projectName: {
            type: String,
            default: ""
        }
    },
    computed: {
        timeTypes() {
            switch (this.timeType) {
                case "日":
                    return 1;
                case "月":
                    return 2;
                case "年":
                    return 3;
                default:
                    return 1;
            }
        }
    },
    data() {
        return {};
    },
    mounted() {
        this.$emit("moduleLoad", this.$el);
    }
};
