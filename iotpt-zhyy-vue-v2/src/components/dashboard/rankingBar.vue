<template>
    <div :class="className" :style="{ height: height, width: width }" />
</template>

<script>
import * as echarts from "echarts";
import "echarts-liquidfill";
require("echarts/theme/macarons"); // echarts theme
// import resize from "../mixins/resize";

export default {
    // mixins: [resize],
    props: {
        className: {
            type: String,
            default: "chart"
        },
        width: {
            type: String,
            default: "100%"
        },
        height: {
            type: String,
            default: "100%"
        },
        valdata: {
            type: Object,
            default: () => {
                return {
                    barData: [],
                    backNum: []
                };
            }
        }
    },
    data() {
        return {
            chart: null
        };
    },
    watch: {
        valdata(val) {
            this.initChart();
        }
    },
    mounted() {
        this.$nextTick(() => {
            this.initChart();
        });
    },
    beforeDestroy() {
        if (!this.chart) {
            return;
        }
        this.chart.dispose();
        this.chart = null;
    },
    methods: {
        initChart() {
            const dataSource = this.valdata.barData;
            const bgData = this.valdata.backNum;
            console.log(bgData);
            let ranking1 = `data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAAAXNSR0IArs4c6QAAA6BJREFUaEPdmb9v00AUx9/3zFCBBOEfQO2O1HaFJV2Z0r+g7YSYCgONiiC+VEglYaD9C1o2NmBhbZhgQQ1/QQ0siAVPEEC+L7LdJI7TJk5yLim3RIrtd+/zft27O4iFQa0LcjEoCdU8RWZFuCCQglAKCfGeAB4ozQDmoxM4DWxqb9LpMa6ASOlLZp1EUcjimHKaBtydBGZkgK7icjdl4TEZ4s8I7KsA1VG9MhIAn1Zc0q7iaWoo0bi/Vc1qjUwA3NazVOaliCxkFTzhex6MWsrijaEArFVXiGDHZrhkhPMhZg3lx68GvT8QgLWKSxGdccJcXhsWUqcCTIPybYsMgjgRgLWHJYoKY35qBsRZRdl93pf06T+ihHXM4T+I+WHG8mHUYjqx+zxgapUjiVbTqRxNVd5aTGrWAzBNcX+a+ShSdcpbncLSATiu9aH1p334+KnmoLUfKtoFqLt7JFetaX9tVnBjSeRK3M/xiyfyxk5dSHohArBq/csFwa1lkWupNPrsCV/s2bJPxwsxQP3RKomJpeNWSeR6T451FbYLIG0vRACmXgnL5sR9DjYSPVirFSs/MxP/WgYQoKE2qkuwGT4RQKsl/PBO5MN7wcqdTg5YBwgT+Ke6ClvhE1n5ZjFSPISIKsTte7kCGHANwZPKDiDrtrKrZ5HJGYCUXZi6ezDBlnAgd94eEMprsFY5ijfi9kfuACIeTK1C+6rHEs8AwD/vAPIfANTd70ImD6CsRdQZhJCc/yRmzX1JYcma2ROCcvcA0Dj/C5nVViLlxrw9YIxaA5/pAn+b7+cxhGDU3HE7nU87kasHgKbaqC5GAMF2RUOJa9sLeQKE4XNhU+/HO7IwjP7wyPp6kNxWhi32t6/WbBSFz6b2Opv6vLxgTeOEoPAuwdmorkX9Vvv/3LyQA0Hb+j0AeeaCTYZTD7bak5i6eyjkxBt8m0p3ZcFT5epcz64vPVF8uMsQIpcGb2wwwEeA4Ye7UVXa1qtUZuJzorGVPeFDBGoZD3Tfbc2pFxzTVJXScT8whJIPpwFikPJ9Vegkl7OmSwT3zjwnAN8EuBeutoNCcegt5XFOzFLxQIS5nF70K4gmDJatXLOeaUgBPgPuOpvdC4xhhSCTB5JCwjJrlNEQWRkmPPPzY8XVL7XTvrjI+u3IAJ3WY1uH4VSkw/WxFz6gwYBvx1G8rcfYAGmvhDAGZgEK88IoV7r5AvhC+gI0afhJKTblh/NqVGuf5JW/FOoAsvBa9acAAAAASUVORK5CYII=`;
            let ranking2 = `data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAAAXNSR0IArs4c6QAABHVJREFUaEPNWk1oG0cU/t5KcmQkFwVK6KEFC3oJNMQ+hBxb2efU65tvkSj0GDfHQMA25B4n9xD3lpvl5BprnVMPPViFQgs5WKWFhl4iIilOInlfeWvtevW7o51dWw+EQDvz5vve387MEyECYcvMNQDTgH2dbJpnwgIDOQJyPvU1ADUQV2EbvyUMHMwWyvKbllDY2QL6Pex1ZvoOgHzCSBVMj3TITEzABW4z/dRn4TAEzuYQdhKgrUm9MhGBlvX9RuTA+2gT8Wam8HxL1RpKBI4tc/6EeRfAgqpizXG1BFFBxRuBBI6t1dsdtrcjDRc1dnWbqPRZoVweN3wsgZZlbjDzptp68YwKCqmRBKYBvGuScSSGEmi9NE02nJifGkkkjOLst7s/9wMaICAJ22E+vICYDzJWPUG02J/YAwSalZUjAPNB2i7oeTW7tLfoX7uHwDTF/SgDMfPW3PJzr7B4BLq1XqwfiVD6CiiZcT528wjcaUWiF0C9TZS/XCjXRaFHoGmtPAWjqLtKKr+G5BdLMNJXelTxh//QflNB5+8X2mT8XnAIRGF9sXj62j0Y2fxYGwiR48P7kG8N8bzgEGjum0UQPw2rUMIkfePhgNVH6Tup/44Ph/fDLufMc71wSqCycqizz5n5+gekvrrlAer88wKfjp55oZLIfYOZq3d6CAoBIaIhB9mlvQJFEj7JDC5duwcB2q49c8D3izxLLz7wfv74+gmEqI60iS6Tbvj4AaS+vIX2GFCZwtm+TMZ9ev1EBz/AVKLGvrlNxOt6mtRmR02AmR9Rs2JaAIc9EqohB5zqNHvj4VkI/fEYnTcV5fnDBjJoj85r63Dp6h3n/eDK+19+1C2loqomBFjLDAqTU/NrmMmvnVWpfyv4+OdjhZmBQ+qxExCri/VdiehF5umLlUDc4IWFEHiL3guoQL+pDEh+ftN5N8RleVdvLEksFUdeWrLFEIk6bHwGrFHDWtklhqliVZUxA+A7LRz/ejeKijNs+YNIX2TDwMueR84DcYjzInu3bxYNjZ2oF4uynV584G3Y5AATJ3hnXaISvbXMXIpZEjm0OGcBH3hRJFa3G6Mtf9I80t7MJYjy3e203naiv1yqWCKCM4FzwHcINPbNTSLeUFl42JgLIUBUyhbKOw6BbhiJv/0NCWU+EkL9Z+CgyZIjOskt4SN3RN6hXtcLQYAjfU60ky2US04eu4p1vRApwABlrvV7CESRC+dBYuTFlru47gE/ZhK17NJez73N0MvdE2a5pQiV0HERYKCeVLncFQBNyyyCw98TxUGCiFYzQ7o1Ixsc01SV+uPeb6CxLaZpIDEO/EAVGub6d5ZpGqfhdK45ITFPTHezy+WdcSEZ2KWUyd3bO+scGx/VBNFqJG1WP/u4Q0qsLn89mFsuK3dGlTzgJyHeaDM2DfDtqKqNCzxrYJu6jQtV3RMTcBU7YSV/8mCsAxyyg08HzHgVBriLIzSBfq8IGbaxQITrAEuT0GsUOgl5mpRVG/iLDVTngPKk1h7mlf8B3MMzhL0seHcAAAAASUVORK5CYII=`;
            let ranking3 = `data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAAAXNSR0IArs4c6QAABF5JREFUaEPdWkFrG0cU/p5FjY0DUXJyYjBy6CG0KclAf0ByCOTSIl/SY+xTe7NLI+FcurM+FTk09s05RbnmUrenQg9RoYccAruFBhIIjVpIYgikGwixCZWmvJVWXWlXq92dsSPyYDH2zpv5vvfevPdm1gQDIh1ZBPbLaqJ9Fm2UAJwDVBEAP12hJqCamCCX2up3oNCQ4rum7vKUdwIG3cabFYI6D4CfPOISsKVDJjOBEPDVfgvnwd+nUycU7KxeyUTgW6dqEZRp4H0sFEiui5qd1hypCEhnraTQ+qET24ciTULhQhpvjCRgO9UrbahNw+GSxgoegZalqO0kDU4kYDtVqw0l06x2UGNGhdRQAuMAPjBKEolYAtKplhUUx/zYyARoyRK124OAIgS6G9Z5BzE/ylgeoSAGN3aEgOVUngB+NR1HcW2xIcLA+giMU9wPs54C2eui1kssPQLd0GHrj7t4hJkFKaTHQHsELLdyCwpLedCXjpxCef6L1KoPXz3Az09/Sj1+cGDYCz4BXeszgaUPv0oNyH15Hzt/30k9PmZgzwtdApUlBdzKO+M7IIDACz4By6lw2szd50wVpjE7fSKWf3HyOC7NfQYew+K9fYn645vw3v6T116BXsMWGxdIN3ySUBQnj/mhxT9Zdvee+uD3W/u64H19wswxko5e+AxDctDguxlomQlsKmDFiEm6kxwGeF5KAVtM4K7KfySM8B4EbzDmI2sR8CNZTvUJoIy1DuX5yzh3/NPIYrxpOX02dn8x6ewmWU5FmZxxGIFgDSay/eiGqY3sGSdwae5zcF0IMk1xsghOpWFpvv4T9cfbRuxmnEAcqk6rcbmPCBNgIrrCBLiihC6gdKeM1x+s1twL3Xvxm/ZixjdxEqK1T+xeRTbQD/FS/ibmo2NZ2xQpJjgAAg2jhez00Y/BrXKccH1Y/eha7xWnU92UGhQyrU40QHR+9iL4iQM2VZjye6LZ6ZM9AiY2MQHcSqwWFT7Qag25cHGWCef63b1n2G/t+X86ffQMmEQg/G77Ed+V6QmhsBCcB7TaiU778GUk38fBM9dakGuLmugSqEoFZenYg0nwsZLT5TDhzMPp00Q7zeEjxUa9S8APIz7Qa9cDJjA7Pdc74HAY7e49x8NXfxgBHhiHw4fviEK3Evpe0PFgFl0C6lJsLLNOiIA5L2QBk2dsYP0+AvyLdMbfCwrKXhfXoxdbgSV0D/h5LJpeh5q2qC2Ex79/l7udUDqYg356S0dHEmgx7mvN0A8c0rkqFUirNugADusOxn1iCIVfjgOJJPCRLBRnMel8U1aY4GtH7SKX0SMeofW1FN/Xk/RGfqXs7An+zNq+a/L2YgQZl1BYNPKZ9ZBDyiOoLRnK86O8lsoD/STWSsC/vMGvjJo8w3sfOHBkM/hwkVY3M4FgYg4roMX/5LGict5sE9AA1K95gP/f1KWlmjAuRIav6M8qUGlgv3gAsZVdAH8BcIGZnazWjoPwHygq/iQW4zZfAAAAAElFTkSuQmCC`;
            let ranking4 = `data:image/png;base64,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`;

            const unit = "次";
            this.chart = echarts.init(this.$el);

            this.chart.setOption({
                grid: [
                    {
                        left: 60,
                        top: 10,
                        right: 30,
                        bottom: 10
                    }
                ],

                xAxis: [
                    {
                        gridIndex: 0,
                        type: "value",
                        axisLabel: { show: false },
                        axisLine: { show: false },
                        splitLine: { show: false },
                        axisTick: { show: false }
                    }
                ],
                yAxis: [
                    {
                        gridIndex: 0,
                        type: "category",
                        inverse: true,
                        position: "left",
                        data: dataSource,
                        axisTick: { show: false },
                        axisLine: { show: false },
                        splitLine: { show: false },
                        axisLabel: {
                            width: 70,
                            padding: [0, 0, 0, -50],
                            align: "left",
                            formatter: (name, index) => {
                                const id = index + 1;
                                if (id < 4) {
                                    return `{icon${id}|${id}}`;
                                } else {
                                    return `{count|${id}}`;
                                }
                            },
                            rich: {
                                icon1: {
                                    width: 24,
                                    height: 24,
                                    color: "#fff",
                                    fontSize: 0,
                                    align: "center",
                                    backgroundColor: {
                                        image: ranking1
                                    }
                                },
                                icon2: {
                                    width: 24,
                                    height: 24,
                                    color: "#fff",
                                    fontSize: 0,
                                    align: "center",
                                    backgroundColor: {
                                        image: ranking2
                                    }
                                },
                                icon3: {
                                    width: 24,
                                    height: 24,
                                    fontSize: 0,
                                    shadowColor: "#3374ba",
                                    borderColor: "#3374ba",
                                    color: "#fff",
                                    align: "center",
                                    backgroundColor: {
                                        image: ranking3
                                    }
                                },
                                count: {
                                    // padding: [2, 0, 0, 0],
                                    width: 24,
                                    height: 24,
                                    color: "#5E707E",
                                    align: "center",
                                    fontSize: 12,
                                    fontWeight: "bold",
                                    shadowColor: "#008AFF",
                                    borderRadius: 40,
                                    backgroundColor: "rgba(94,112,126,0.12)"
                                }
                            }
                        }
                    },
                    {
                        gridIndex: 0,
                        type: "category",
                        inverse: true,
                        data: dataSource,
                        axisTick: { show: false },
                        axisLine: { show: false },
                        splitLine: { show: false },
                        position: "left",
                        axisLabel: {
                            width: 80,
                            padding: [0, 0, 40, 10],
                            align: "left",
                            formatter: (name, index) => {
                                if (dataSource[index].name) {
                                    return `{value|${dataSource[index].name}}`;
                                }
                            },
                            rich: {
                                value: {
                                    color: "#666",
                                    fontSize: 14,
                                    fontWeight: 500
                                }
                            }
                        }
                    },
                    {
                        gridIndex: 0,
                        type: "category",
                        position: "right",
                        inverse: true,
                        data: dataSource.map((item) => item.name),
                        axisTick: { show: false },
                        axisLine: { show: false },
                        splitLine: { show: false },
                        axisLabel: {
                            align: "right",
                            padding: [0, 0, 40, -40],
                            formatter: (_, index) => {
                                if (dataSource[index].value) {
                                    return `{value|${dataSource[index].value}${unit}}`;
                                }
                            },
                            rich: {
                                value: {
                                    color: "#5E707E",
                                    fontSize: 14,
                                    fontWeight: 500
                                }
                            }
                        }
                    }
                ],
                series: [
                    {
                        name: "背景",
                        type: "bar",
                        barWidth: 16,
                        barGap: "-100%",
                        data: bgData,
                        itemStyle: {
                            normal: {
                                barBorderRadius: [30, 30, 30, 30],
                                color: new echarts.graphic.LinearGradient(
                                    0,
                                    0,
                                    0,
                                    1,
                                    [
                                        {
                                            offset: 0,
                                            color: "#EBF0F2" // 0% 处的颜色
                                        },
                                        {
                                            offset: 1,
                                            color: "#F5F7F9" // 100% 处的颜色
                                        }
                                    ],
                                    false
                                ),
                                borderWidth: 0.5,
                                borderColor: "rgba(0, 0, 0, 0.06)"
                            }
                        },
                        z: 0,
                        silent: true
                    },
                    {
                        type: "bar",
                        xAxisIndex: 0,
                        yAxisIndex: 0,
                        barWidth: 16,
                        data: dataSource.map((item) => item.value),
                        silent: true,
                        itemStyle: {
                            emphasis: {
                                barBorderRadius: [0, 20, 20, 0]
                            },
                            normal: {
                                barBorderRadius: [30, 30, 30, 30],
                                color: new echarts.graphic.LinearGradient(
                                    0,
                                    0,
                                    0,
                                    1,
                                    [
                                        {
                                            offset: 0,
                                            color: "#A1C3E1" // 0% 处的颜色
                                        },
                                        {
                                            offset: 1,
                                            color: "#7A9FDA" // 100% 处的颜色
                                        }
                                    ],
                                    false
                                )
                            }
                        }
                    }
                ]
            });
        }
    }
};
</script>
