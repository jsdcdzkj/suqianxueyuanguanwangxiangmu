<script>
import { Calendar, CalendarUtils } from "@/utils/calendar";
import { getFoodStatic } from "@/api/carousel";
export default {
    name: "month",
    props: {
        date: {
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
        current() {
            if (this.calendarInstance) {
                this.computedMonth(CalendarUtils.getWeekList(+new Date()));
            }
        },
        computedMonth(list) {
            this.loading = true;
            const { date } = this.calendarInstance.getCurrentDetail();
            getFoodStatic({ startTime: list[0].date, endTime: list[list.length - 1].date, timeType: 1 }).then((res) => {
                for (const item of list) {
                    const { date: d } = item;
                    item["than"] = +new Date(d) >= +new Date(date);
                    item["static"] = res.data[d];
                }

                this.calendar.cell = list.length / 7;
                this.calendar.cellHeight = Number((100 / this.calendar.cell).toFixed(2));
                this.calendar.list = list;
                this.$emit("change", `${list[0].date} ~ ${list[list.length - 1].date}`);
                this.loading = false;
            });
        },
        refresh() {
            this.computedMonth(this.calendar.list);
        },
        onSelectDate(item) {
            this.select = item.date;
            this.$emit("selectDate", item);
        },
        selectCurrent() {
            this.onSelectDate(this.calendarInstance.getCurrentDetail());
            this.current();
        }
    },
    created() {
        this.calendarInstance = new Calendar(new Date());

        const list = CalendarUtils.getWeekList(+new Date(this.date || +new Date()));
        this.computedMonth(list);
        this.onSelectDate(this.calendarInstance.getCurrentDetail());
    },
    beforeDestroy() {
        this.calendarInstance = null;
    }
};
</script>

<template>
    <div class="month" v-loading="loading">
        <div class="month_body">
            <div class="body-cell" :style="`height:${calendar.cellHeight}%`">
                <div :class="['cell-num border']">午餐</div>
                <div class="cell-items">
                    <div
                        :class="['cell-item border', it.date === select ? 'select' : '']"
                        v-for="it in calendar.list"
                        :key="it.date"
                        @click="onSelectDate(it)"
                    >
                        <div class="header_cell">{{ it.weekZh }} {{ it.date.split("-").slice(1, 3).join("/") }}</div>
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
                                <div :class="{ default: true, than: it.than }">
                                    <span v-for="item in it.static.mealsDishes" :key="item.id">
                                        {{ item.dishName }}
                                    </span>
                                </div>
                            </div>
                            <div
                                style="
                                    height: 148px;
                                    padding: 10px;
                                    border-bottom: 1px solid rgba(0, 0, 0, 0.1);
                                    display: flex;
                                    flex-direction: column;
                                    justify-content: space-between;
                                "
                            >
                                <div :class="['count', it.formerly ? 'formerly' : 'reserve']">
                                    <span>预定人数</span>
                                    <span>{{ it.static.foodCount }}人</span>
                                </div>
                                <div :class="['count ', it.formerly ? 'formerly' : 'actual']">
                                    <span>实际消费人次</span>
                                    <span>{{ it.static.records }}人</span>
                                </div>
                                <div :class="['count ', it.formerly ? 'formerly' : 'income']">
                                    <span>收入金额</span>
                                    <span>{{ it.static.amountSum / 100 }}元</span>
                                </div>
                            </div>
                            <div style="flex: 1"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
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
                    &:hover {
                        border-top: 2px solid #355bad;
                        border-bottom: 2px solid #355bad;
                        box-sizing: border-box;
                        background-color: #f3f5f9;
                    }
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
