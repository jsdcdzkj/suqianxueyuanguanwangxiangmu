<template>
    <div class="module-header" :style="`width:${A4Origin.width}px;height:${A4Origin.height}px`">
        <img src="../../assets/img/template_bg.jpg" class="img" alt="" />
        <div class="center">
            <h1 class="header-title">
                <div class="rect"></div>
                <span class="titlle">{{ title }}</span>
            </h1>
            <h4 class="header-timer">{{ timer }}</h4>
        </div>
    </div>
</template>

<script>
import { jsPDF } from "jspdf";
export default {
    props: {
        title: {
            type: String,
            default: "用户分析报告"
        },
        timer: {
            type: String,
            default: "XXXX-XX-XX"
        }
    },
    data() {
        return {
            A4Origin: {
                width: 0,
                height: 0
            }
        };
    },
    mounted() {
        const pdf = new jsPDF({
            unit: "px",
            format: "a4"
        });
        this.A4Origin = {
            width: (pdf.internal.pageSize.getWidth() - 40) * 2,
            height: (pdf.internal.pageSize.getHeight() - 40) * 2
        };
    }
};
</script>

<style lang="scss" scoped>
.img {
    width: 100%;
    height: 100%;
    position: absolute;
    z-index: 1;
    object-fit: cover;
}
.module-header {
    width: 100%;
    height: 500px;
    position: relative;
    .center {
        width: 100%;
        position: absolute;
        z-index: 2;
        top: 30%;

        height: 274px;
        background: rgba(0, 0, 0, 0.45);
    }
}
.header-title {
    margin: 0;
    color: #eee;
    font-weight: bold;
    font-size: 35px;
    position: absolute;
    top: 62px;
    z-index: 2;
    right: 50px;
    .titlle {
        position: relative;
        font-family: Source Han Sans CN, Source Han Sans CN;
        font-weight: bold;
        font-size: 48px;
        color: #ffffff;
        line-height: 64px;
        text-shadow: 0px 4px 4px rgba(0, 0, 0, 0.25);
        text-align: left;
    }
    .rect {
        position: absolute;
        content: "";
        left: -10px;
        bottom: 0px;
        display: inline-block;
        // width: 339px;
        width: calc(100% + 20px);
        height: 30px;
        background: #4096ff;
    }
}

.header-timer {
    position: absolute;
    top: 130px;
    z-index: 3;
    right: 50px;
    font-family: Source Han Sans CN, Source Han Sans CN;
    font-weight: 500;
    font-size: 32px;
    color: #ffffff;
    line-height: 32px;
    text-shadow: 0px 4px 4px rgba(0, 0, 0, 0.25);
}
</style>
