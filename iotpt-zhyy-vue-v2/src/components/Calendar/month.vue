<script>
import { Calendar } from "@/utils/calendar";
import { getFoodStatic } from "@/api/carousel";
export default {
    name: "month",
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
            loading: false
        };
    },
    methods: {
        pre() {
            if (this.calendarInstance) {
                this.computedMonth(this.calendarInstance.subtractMonth());
            }
        },
        next() {
            if (this.calendarInstance) {
                this.computedMonth(this.calendarInstance.addMonth());
            }
        },
        current() {
            if (this.calendarInstance) {
                this.computedMonth(this.calendarInstance.month());
            }
        },
        computedMonth(list) {
            this.loading = true;
            const listMonth = [];
            const { week, date: date3 } = this.calendarInstance.getCurrentDetail();
            const { date } = this.calendarInstance.subtractDay((week === 0 ? 7 : week) - 1);
            getFoodStatic({ startTime: list[0].date, endTime: list[list.length - 1].date, timeType: 2 }).then((res) => {
                for (const item of list) {
                    const { date: d } = item;
                    item["than"] = +new Date(d) >= +new Date(date);
                    item["formerly"] = +new Date(date3) > +new Date(d);
                    item["static"] = res.data[d];
                }

                for (let i = 0; i < list.length; i += 7) {
                    const slice = list.slice(i, i + 7);
                    listMonth.push({
                        isThan: slice.every((item) => item.than),
                        list: slice
                    });
                }

                this.calendar.cell = list.length / 7;
                this.calendar.cellHeight = Number((100 / this.calendar.cell).toFixed(2));
                this.calendar.list = listMonth;
                this.$emit("change", this.calendarInstance.monthText);
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
            this.calendarInstance = new Calendar(new Date());
            this.current();
        },
        onSelectWeek(week) {
            console.log(week);
            this.$emit("selectWeek", week);
        }
    },
    created() {
        this.calendarInstance = new Calendar(new Date());
        this.current();
        this.onSelectDate(this.calendarInstance.getCurrentDetail());
    },
    beforeDestroy() {
        this.calendarInstance = null;
    }
};
</script>

<template>
    <div class="month" v-loading="loading">
        <div class="month_header">
            <div class="header_num border"></div>
            <div class="header_cells">
                <div class="header_cell border" v-for="item in headerCell" :key="item">
                    {{ item }}
                </div>
            </div>
        </div>
        <div class="month_body">
            <div
                class="body-cell"
                :style="`height:${calendar.cellHeight}%`"
                v-for="(item, index) in calendar.list"
                :key="index"
            >
                <div :class="['cell-num border', item.isThan ? 'than' : '']" @click="onSelectWeek(item)">周菜单</div>
                <div class="cell-items">
                    <div
                        :class="['cell-item border', it.date === select ? 'select' : '']"
                        v-for="it in item.list"
                        :key="it.date"
                        @click="onSelectDate(it)"
                    >
                        <div class="cell-item_header">
                            {{ it.day }} {{ it.IDayCn }}
                            {{ it.isHoliday ? (it.festival === "周末" ? "" : it.festival) : "" }}
                        </div>
                        <div class="cell-item_body">
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
    &_header {
        display: flex;
        .header_num {
            width: 48px;
            height: 48px;
            background: #f3f3f3;
        }
        .header_cells {
            display: flex;
            flex: 1;

            .header_cell {
                height: 48px;
                width: 14.28%;
                text-align: center;
                justify-content: center;
                line-height: 48px;
            }
        }
    }
    &_body {
        height: calc(100% - 48px);
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
                    padding: 10px;
                    box-sizing: border-box;
                    &.select {
                        border-top: 2px solid #355bad;
                        border-bottom: 2px solid #355bad;
                    }
                    &_header {
                        height: 20px;
                        margin-bottom: 10px;
                    }
                    &_body {
                        height: calc(100% - 30px);
                        display: flex;
                        flex-direction: column;
                        justify-content: space-between;
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
