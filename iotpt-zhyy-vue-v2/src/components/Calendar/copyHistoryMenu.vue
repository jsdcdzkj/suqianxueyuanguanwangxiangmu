<script>
import { Calendar, CalendarUtils } from "@/utils/calendar";
import { getFoodStatic } from "@/api/carousel";
export default {
    name: "month",
    props: {
        show: {
            type: Boolean,
            default: false
        },
        start: {
            type: String,
            default: ""
        }
    },
    data() {
        return {
            headerCell: ["周一", "周二", "周三", "周四", "周五", "周六", "周日"],
            calendar: {
                cell: 0,
                cellHeight: 0,
                list: []
            },
            select: "",
            isNext: false,
            calendarInstance: null,
            monthText: "",
            loading: false
        };
    },
    methods: {
        pre() {
            this.computedMonth(
                CalendarUtils.getWeekList(CalendarUtils.decreaseWeek(+new Date(this.calendar.list[0].date)))
            );
        },
        next() {
            this.computedMonth(CalendarUtils.getWeekList(CalendarUtils.addWeek(+new Date(this.calendar.list[0].date))));
        },
        preMonth(date) {
            this.computedMonth(CalendarUtils.getWeekList(CalendarUtils.decreaseWeek(+new Date(date))));
        },
        current() {
            if (this.calendarInstance) {
                this.computedMonth(this.calendarInstance.month());
            }
        },
        computedMonth(list) {
            this.loading = true;
            const { date } = this.calendarInstance.getCurrentDetail();

            getFoodStatic({ startTime: list[0].date, endTime: list[list.length - 1].date, timeType: 1 }).then((res) => {
                for (const item of list) {
                    const { date: d } = item;
                    item["static"] = res.data[d];
                }

                this.calendar.cell = list.length / 7;
                this.calendar.cellHeight = Number((100 / this.calendar.cell).toFixed(2));
                this.calendar.list = list;
                this.monthText = `${list[0].date} ~ ${list[list.length - 1].date}`;
                this.isNext = false;
                const nextList = CalendarUtils.getWeekList(
                    CalendarUtils.addWeek(+new Date(this.calendar.list[0].date))
                );
                for (const item of nextList) {
                    const { date: d } = item;
                    if (+new Date(d) >= +new Date(date)) {
                        this.isNext = true;
                        break;
                    }
                }
                this.loading = false;
            });
        },
        onSelectDate(item) {
            this.select = item.date;
        },
        selectCurrent() {
            this.select = this.calendarInstance.getCurrentDetail().date;
        },
        handleClose() {
            this.$emit("update:show", false);
        },
        init() {
            this.calendarInstance = new Calendar(new Date());
            const list = CalendarUtils.getWeekList(+new Date(this.start));
            this.computedMonth(list);
            this.select = this.calendarInstance.getCurrentDetail().date;
        },
        onSelect() {
            this.$emit("update:show", false);
            this.$emit(
                "select",
                this.calendar.list.map((item) => JSON.parse(JSON.stringify(item.static)))
            );
        }
    },
    watch: {
        show(val) {
            if (val) {
                this.calendarInstance = new Calendar(new Date());
                this.select = this.calendarInstance.getCurrentDetail().date;
                this.preMonth(this.select);
            }
        }
    },
    beforeDestroy() {
        this.calendarInstance = null;
    }
};
</script>

<template>
    <el-dialog title="复制菜单" :visible="show" width="1280px" :before-close="handleClose">
        <div style="display: flex; align-items: center; justify-content: center; margin-bottom: 20px">
            <el-button size="mini" @click="pre()" icon="el-icon-arrow-left"></el-button>
            <div style="margin: 0 10px; padding-top: 4px; font-size: 16px; font-weight: bold">{{ monthText }}</div>
            <el-button size="mini" @click="next()" :disabled="isNext" icon="el-icon-arrow-right"></el-button>
        </div>
        <div class="month" v-loading="loading">
            <div class="month_body">
                <div class="body-cell" :style="`height:${calendar.cellHeight}%`">
                    <div :class="['cell-num border']">午餐</div>
                    <div class="cell-items">
                        <div :class="['cell-item border']" v-for="it in calendar.list" :key="it.date">
                            <div class="header_cell">
                                {{ it.weekZh }} {{ it.date.split("-").slice(1, 3).join("/") }}
                            </div>
                            <div class="cell-item_body">
                                <div style="padding: 10px; height: 258px; border-bottom: 1px solid rgba(0, 0, 0, 0.1)">
                                    <div :class="{ titleDefault: true, selectTitle: it.date === select }">
                                        <img
                                            src="@/assets/img/smartDing/restaurant-2.png"
                                            style="width: 24px; height: 24px"
                                            v-if="it.date === select"
                                        />
                                        <img
                                            src="@/assets/img/smartDing/restaurant-1.png"
                                            style="width: 24px; height: 24px"
                                            v-else
                                        />
                                        <span>菜品</span>
                                    </div>
                                    <div :class="{ default: true }">
                                        <span v-for="item in it.static.mealsDishes" :key="item.id">
                                            {{ item.dishName }}
                                        </span>
                                    </div>
                                </div>
                                <div style="flex: 1"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div slot="footer">
            <el-button @click="$emit('update:show', false)">取消</el-button>
            <el-button type="primary" @click="onSelect">选择</el-button>
        </div>
    </el-dialog>
</template>

<style scoped lang="scss">
.month {
    height: 100%;
    border-left: 1px solid rgba(0, 0, 0, 0.06);
    border-top: 1px solid rgba(0, 0, 0, 0.06);
    &_body {
        height: 100%;
        .body-cell {
            display: flex;
            .cell-num {
                width: 48px;
                background: #f3f3f3;
                padding: 0 10px;
                display: flex;
                align-items: center;
                text-align: center;
                font-weight: 400;
                font-size: 16px;
                color: rgba(0, 0, 0, 0.45);
                &.than {
                    background: #355bad;
                    color: #fff;
                }
            }
            .cell-items {
                flex: 1;
                display: flex;

                .cell-item {
                    width: 14.28%;

                    box-sizing: border-box;
                    cursor: pointer;

                    .header_cell {
                        height: 48px;
                        width: 100%;
                        text-align: center;
                        justify-content: center;
                        line-height: 48px;
                        border-bottom: 1px solid rgba(0, 0, 0, 0.1);
                    }
                    &.select {
                        border-top: 2px solid #355bad;
                        border-bottom: 2px solid #355bad;
                        box-sizing: border-box;
                        background-color: #f3f5f9;
                    }
                    &_header {
                        height: 20px;
                        margin-bottom: 10px;
                    }
                    &_body {
                        height: calc(100% - 30px);
                        display: flex;
                        flex-direction: column;

                        .count {
                            width: 100%;
                            height: 28%;
                            background: #ffeeee;
                            border-left: 2px solid #ff4d4f;
                            display: flex;
                            align-items: center;
                            justify-content: space-between;
                            padding: 0 6px;
                            font-size: 12px;
                            &.formerly {
                                background: rgba(0, 0, 0, 0.06);
                                border-left: 2px solid rgba(0, 0, 0, 0.45);
                            }
                            &.reserve {
                                background: #ffeeee;
                                border-left: 2px solid #ff4d4f;
                            }
                            &.actual {
                                background: #ecf5ff;
                                border-left: 2px solid #4096ff;
                            }
                            &.income {
                                background: #eef9e9;
                                border-left: 2px solid #52c41a;
                            }
                        }
                        .default {
                            display: flex;
                            flex-direction: column;
                            color: #aaa;
                            font-weight: 400;
                            font-size: 14px;
                            line-height: 22px;

                            span {
                                margin-top: 20px;
                            }
                            &.than {
                                color: #000;
                            }
                        }
                        .titleDefault {
                            font-weight: bold;
                            font-size: 16px;
                            color: rgba(0, 0, 0, 0.45);

                            display: flex;
                            align-items: center;
                            span {
                                vertical-align: middle;
                                padding-top: 4px;
                            }
                            img {
                                vertical-align: middle;
                            }
                            &.selectTitle {
                                color: #355bad;
                            }
                        }
                    }
                }
            }
        }
    }

    .border {
        border-right: 1px solid rgba(0, 0, 0, 0.1);
        border-bottom: 1px solid rgba(0, 0, 0, 0.1);
    }
}
</style>
