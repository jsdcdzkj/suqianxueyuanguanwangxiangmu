<template>
    <el-dialog :visible.sync="dialogFormVisible" width="1440px" @close="close">
        <span slot="title" class="drawer-title">
            <b>{{ title }}</b>
        </span>
        <div class="warning-con">
            <div class="left-warning-con">
                <div class="carousel-container">
                    <!-- 主图轮播 -->
                    <div class="main-carousel-wrapper">
                        <el-carousel
                            ref="mainCarousel"
                            :initial-index="currentIndex"
                            :interval="0"
                            indicator-position="none"
                            height="612px"
                            arrow="never"
                            @change="onCarouselChange"
                        >
                            <el-carousel-item v-for="(item, index) in imageList" :key="'main-' + index">
                                <template v-if="item.url">
                                    <img :src="item.url" class="carousel-image" />
                                </template>
                                <template v-else>
                                    <div class="default-image">
                                        <i class="el-icon-picture"></i>
                                    </div>
                                </template>
                            </el-carousel-item>
                        </el-carousel>

                        <!-- 自定义切换按钮 -->
                        <div class="custom-nav">
                            <div class="nav-btn prev" @click="slidePrev">
                                <i class="el-icon-arrow-left"></i>
                            </div>
                            <div class="nav-btn next" @click="slideNext">
                                <i class="el-icon-arrow-right"></i>
                            </div>
                        </div>
                    </div>

                    <!-- 可滚动的缩略图 -->
                    <div class="thumbnail-container">
                        <div class="thumb-scroll-wrapper">
                            <div class="thumb-nav-btn left" @click="scrollThumbs('left')" v-show="showLeftScroll">
                                <i class="el-icon-arrow-left"></i>
                            </div>

                            <div class="thumbnails-scroll" ref="thumbScroll">
                                <div
                                    v-for="(item, index) in imageList"
                                    :key="'thumb-' + index"
                                    class="thumbnail-item"
                                    :class="{ active: currentIndex === index }"
                                    @click="onThumbClick(index)"
                                >
                                    <template v-if="item.url">
                                        <img :src="item.url" class="thumbnail-image" />
                                    </template>
                                    <template v-else>
                                        <div class="default-thumbnail">
                                            <i class="el-icon-picture"></i>
                                        </div>
                                    </template>
                                </div>
                            </div>

                            <div class="thumb-nav-btn right" @click="scrollThumbs('right')" v-show="showRightScroll">
                                <i class="el-icon-arrow-right"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="right-warning-con">
                <el-descriptions :column="1" border>
                    <el-descriptions-item label="告警编码" content-class-name="my-content">
                        {{ "alarm--" + entityInfo.id }}
                    </el-descriptions-item>
                    <el-descriptions-item label="告警类型" content-class-name="my-content">
                        {{ entityInfo.alarmCategoryName }}
                    </el-descriptions-item>
                    <el-descriptions-item label="告警时间" content-class-name="my-content">
                        {{ entityInfo.alarmTime }}
                    </el-descriptions-item>
                    <el-descriptions-item label="事件内容" content-class-name="my-content">
                        {{ entityInfo.alarmContentStr }}
                    </el-descriptions-item>
                    <el-descriptions-item label="处理状态" :span="2" content-class-name="my-content">
                        {{ entityInfo.handleStatusName }}
                    </el-descriptions-item>
                    <el-descriptions-item label="告警设备" content-class-name="my-content">
                        {{ entityInfo.deviceName }}
                    </el-descriptions-item>
                    <el-descriptions-item label="告警等级" content-class-name="my-content">
                        {{ entityInfo.alarmLevelName }}
                    </el-descriptions-item>
                    <!-- <el-descriptions-item
                    label="告警来源"
                    content-class-name="my-content"
                    v-if="entityInfo.warnSource == 0"
                >
                    告警的异常设备
                </el-descriptions-item>
                <el-descriptions-item
                    label="告警来源"
                    content-class-name="my-content"
                    v-if="entityInfo.warnSource == 1"
                >
                    上报的异常设备
                </el-descriptions-item> -->
                    <el-descriptions-item label="告警区域" content-class-name="my-content">
                        {{ entityInfo.areaName }}
                    </el-descriptions-item>
                    <el-descriptions-item label="告警状态" content-class-name="my-content">
                        {{ entityInfo.alarmStatusName }}
                    </el-descriptions-item>
                    <el-descriptions-item
                        label="处理状态"
                        :span="2"
                        content-class-name="my-content"
                        v-if="entityInfo.handleStatusName == 1"
                    >
                        待处理
                    </el-descriptions-item>

                    <!-- <el-descriptions-item :span="2" label="佐证" content-class-name="my-content">
                        <el-image
                            :src="commonImgUrl + entityInfo.alarmImg"
                            v-if="entityInfo.alarmImg"
                            fit="cover"
                            style="width: 50px; height: 50px; margin-top: 10px"
                            :previewSrcList="[commonImgUrl + entityInfo.alarmImg]"
                        ></el-image>
                    </el-descriptions-item>
                    <el-descriptions-item :span="2" label="回放" content-class-name="my-content">
                        <img
                            v-if="entityInfo.alarmVideo"
                            style="width: 24px; height: 24px"
                            src="@/assets/img/play-icon.png"
                            @click="seeBackVideo(entityInfo.alarmVideo)"
                            alt=""
                        />
                    </el-descriptions-item> -->
                </el-descriptions>
                <div class="ai-main-con">
                    <div class="ai-header">
                        <div class="ai-header-title">
                            <i class="icon-ai"></i>
                            <span>告警分析</span>
                        </div>
                        <div class="ai-header-btn">开始分析</div>
                    </div>
                    <div class="ai-analysis-con"></div>
                </div>

                <div class="btn-wrap">
                    <!-- <div class="btn view">
                    <img style="width: 18px;height: 15px;margin-right:8px" src="@/assets/img/alarmEvent/icon-view.png" alt="">
                    查看
                </div> -->
                    <div
                        v-if="entityInfo.handleStatus == 0"
                        class="btn report"
                        @click="handleBatchDeal(entityInfo, '2')"
                    >
                        上报
                    </div>
                    <div
                        v-if="entityInfo.handleStatus == 0"
                        class="btn ignore"
                        @click="handleBatchDeal(entityInfo, '3')"
                    >
                        忽略
                    </div>
                    <div
                        v-if="entityInfo.handleStatus == 0"
                        class="btn error"
                        @click="handleManualJudgment(entityInfo)"
                    >
                        误报
                    </div>
                    <div class="btn video" v-if="entityInfo.deviceSource == 1" @click="seeVideo(entityInfo)">
                        实时监控
                    </div>
                    <div v-if="entityInfo.handleStatus == 0" class="btn delete" @click="handleDelete(entityInfo)">
                        删除
                    </div>
                </div>
            </div>
        </div>
        <!-- <div class="footer-btn">
            <el-button @click="close">取消</el-button>
        </div> -->
        <manualJudgment ref="manualJudgment" @fetchData="handleRef"></manualJudgment>
        <batchDeal ref="batchDeal" @fetchData="handleRef"></batchDeal>
    </el-dialog>
</template>

<script>
import batchDeal from "./batchDeal.vue";
import manualJudgment from "./manualJudgment.vue";
import { showVideoPlayerDialog } from "@/components/VideoPlayerDialog";
import { getRTSPAddr } from "@/api/trackPerson";
import { batchProcessing } from "@/api/alarm-center/alarmCenter";
import { swiper, swiperSlide } from "vue-awesome-swiper";
export default {
    name: "detail",
    components: { batchDeal, manualJudgment, swiper, swiperSlide },
    data() {
        return {
            loading: false,
            disabled: false,
            title: "",
            dialogFormVisible: false,
            warnLevelList: [],
            entityInfo: {},
            list: [],
            currentIndex: 0,
            showLeftScroll: false,
            showRightScroll: false,
            thumbScrollPosition: 0,
            imageList: [
                { url: "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg" },
                { url: "https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48jpeg.jpeg" },
                { url: "https://fuss10.elemecdn.com/2/11/6535bcfb26e4c79b48ddde44f4b6fjpeg.jpeg" },
                { url: "https://fuss10.elemecdn.com/3/28/bbf893f792f03a54408b3b7a7ebf0jpeg.jpeg" },
                { url: "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg" }
            ]
        };
    },
    watch: {
        dialogFormVisible(newVal) {
            if (newVal) {
                this.$nextTick(() => {
                    this.checkScrollButtons();
                });
            }
        }
    },
    mounted() {
        window.addEventListener("resize", this.checkScrollButtons);
    },
    beforeDestroy() {
        window.removeEventListener("resize", this.checkScrollButtons);
    },
    methods: {
        onCarouselChange(currentIndex) {
            this.currentIndex = currentIndex;
            // 更新右侧显示的信息
            this.entityInfo = this.imageList[currentIndex];
            this.scrollToCurrentThumb();
        },

        onThumbClick(index) {
            this.currentIndex = index;
            this.entityInfo = this.imageList[index]; // 更新右侧信息
            this.$refs.mainCarousel.setActiveItem(index);
        },
        slidePrev() {
            if (this.currentIndex > 0) {
                this.currentIndex--;
            } else {
                this.currentIndex = this.imageList.length - 1;
            }
            this.$refs.mainCarousel.setActiveItem(this.currentIndex);
        },

        slideNext() {
            if (this.currentIndex < this.imageList.length - 1) {
                this.currentIndex++;
            } else {
                this.currentIndex = 0;
            }
            this.$refs.mainCarousel.setActiveItem(this.currentIndex);
        },

        scrollThumbs(direction) {
            const scrollContainer = this.$refs.thumbScroll;
            const scrollAmount = 200; // 每次滚动的距离

            if (direction === "left") {
                this.thumbScrollPosition = Math.max(0, this.thumbScrollPosition - scrollAmount);
            } else {
                const maxScroll = scrollContainer.scrollWidth - scrollContainer.clientWidth;
                this.thumbScrollPosition = Math.min(maxScroll, this.thumbScrollPosition + scrollAmount);
            }

            scrollContainer.scrollTo({
                left: this.thumbScrollPosition,
                behavior: "smooth"
            });

            this.$nextTick(() => {
                this.checkScrollButtons();
            });
        },

        scrollToCurrentThumb() {
            const scrollContainer = this.$refs.thumbScroll;
            if (!scrollContainer) return;

            const thumbItems = scrollContainer.querySelectorAll(".thumbnail-item");
            if (thumbItems[this.currentIndex]) {
                const thumb = thumbItems[this.currentIndex];
                const thumbLeft = thumb.offsetLeft;
                const thumbWidth = thumb.offsetWidth;
                const containerWidth = scrollContainer.clientWidth;
                const gap = 8; // 缩略图之间的间距

                // 计算目标滚动位置，确保当前缩略图居中显示
                this.thumbScrollPosition = thumbLeft - (containerWidth - thumbWidth) / 2;

                // 处理边界情况
                const maxScroll = scrollContainer.scrollWidth - containerWidth;
                this.thumbScrollPosition = Math.max(0, Math.min(this.thumbScrollPosition, maxScroll));

                scrollContainer.scrollTo({
                    left: this.thumbScrollPosition,
                    behavior: "smooth"
                });

                this.$nextTick(() => {
                    this.checkScrollButtons();
                });
            }
        },
        checkScrollButtons() {
            const scrollContainer = this.$refs.thumbScroll;
            if (!scrollContainer) return;

            this.showLeftScroll = this.thumbScrollPosition > 0;
            this.showRightScroll = this.thumbScrollPosition < scrollContainer.scrollWidth - scrollContainer.clientWidth;
        },

        showEdit(list, index = 0) {
            this.title = "查看详情";
            this.dialogFormVisible = true;
            this.list = list;
            // 将list中的alarmImg转换为轮播图所需的格式
            this.imageList = list.map((item) => ({
                url: item.alarmImg ? this.commonImgUrl + item.alarmImg : null,
                ...item // 保留原始数据
            }));
            // 使用传入的index设置当前显示项
            this.currentIndex = index;
            this.entityInfo = list[index];
            this.thumbScrollPosition = 0;

            this.$nextTick(() => {
                if (this.$refs.thumbScroll) {
                    this.$refs.thumbScroll.scrollLeft = 0;
                }
                this.checkScrollButtons();
                // 确保轮播图显示正确的项
                this.$refs.mainCarousel.setActiveItem(index);
                // 自动滚动到当前缩略图位置
                this.scrollToCurrentThumb();
            });
        },
        close() {
            this.dialogFormVisible = false;
            // 清空数据
            this.list = [];
            this.imageList = [];
            this.entityInfo = {};
            this.currentIndex = 0;
            this.thumbScrollPosition = 0;
            this.$emit("fetchData");
        },
        handleManualJudgment(row) {
            this.$refs["manualJudgment"].showEdit(row);
        },
        // 视频回放
        seeBackVideo(url) {
            this.showDetail = true;
            showVideoPlayerDialog({ videoUrl: url, type: "hls" });
        },
        handlerVideoClose() {
            this.showDetail = false;
        },
        seeVideo(row) {
            console.log(row);
            this.showDetail = true;
            getRTSPAddr({ deviceCollectId: row.deviceId }).then((res) => {
                showVideoPlayerDialog({ videoUrl: res.msg });
            });
        },
        // 批量处理
        handleBatchDeal(row, type) {
            this.$refs["batchDeal"].showEdit(row, row.id, `${type}`);
        },
        handleDelete(row) {
            this.$confirm("此操作将删除该条数据, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            }).then(() => {
                batchProcessing({ ids: row.id, handleStatus: 4 }).then((res) => {
                    this.$message.success("删除成功");
                    this.$emit("fetchData");
                    this.dialogFormVisible = false;
                });
            });
        },
        // 关闭更新
        handleRef() {
            this.$emit("fetchData");
            this.dialogFormVisible = false;
        }
    }
};
</script>
<style scoped lang="scss">
::v-deep {
    .tips-title {
        font-size: 14px;
        margin-bottom: 8px;
        font-weight: bold;
        color: #334c97;

        i {
            margin-right: 4px;
        }
    }
    .area-box {
        display: flex;
        justify-content: center;
        span {
            margin: 0 8px;
        }
    }
    .config-type-con {
        margin-bottom: 14px;
        background-color: #fafcff;
        padding-top: 8px;
        border: 1px solid #efefef;
        position: relative;
        .el-form-item--small.el-form-item {
            margin-bottom: 6px;
        }
        p {
            position: absolute;
            right: 0;
            height: 26px;
            line-height: 26px;
            text-align: center;
            display: inline-block;
            padding: 0 20px;
            margin: 0 0 6px;
            background-color: #5470c6;
            border-top-left-radius: 22px;
            border-bottom-left-radius: 22px;
            color: #fff;
            font-size: 12px;
        }
        .config-item {
            padding: 10px 0;
            background-color: #fbf2de;
            .el-form-item--small.el-form-item {
                margin-bottom: 0;
            }
        }
        .form-edit-btn {
            margin-top: 6px;
            margin-bottom: 12px !important;
        }
    }
    .footer-btn {
        padding: 12px 20px;
        border-top: 1px solid #efefef;
        position: absolute;
        bottom: 0;
        text-align: right;
        width: 100%;
        background-color: #fff;
        z-index: 9;
    }
    // .my-content {
    //     width: 356px;
    // }
    .el-descriptions-item__label {
        width: 96px !important;
    }
    .el-descriptions--small.is-bordered .el-descriptions-item__cell {
        padding: 10px;
    }
}
.mt2 {
    margin-top: 20px;
}

.view-title {
    &::before {
        content: "";
        position: absolute;
        left: -18px;
        width: 8px;
        height: 8px;
        border-radius: 50%;
        background: #1e95f4;
    }
    &::after {
        content: "";
        position: absolute;
        left: -22px;
        width: 16px;
        height: 16px;
        border-radius: 50%;
        background: #1e95f4;
        opacity: 0.2;
    }
}

.btn-wrap {
    margin-top: 12px;
    display: flex;
    justify-content: end;
    .btn {
        padding: 0 16px;
        height: 32px;
        display: flex;
        justify-content: center;
        align-items: center;
        border-radius: 4px;
        color: #fff;
        margin-left: 8px;
        cursor: pointer;
    }

    .view {
        background: linear-gradient(180deg, #47adff 0%, #1e95f4 100%);
    }

    .report {
        background: #40bfa8;
    }

    .ignore {
        background: #f5a250;
    }
    .error {
        background: #d96ba7;
    }

    .video {
        background: #409eff;
    }

    .delete {
        background: #f56c6c;
    }
}
.warning-con {
    width: 100%;
    display: flex;
    .left-warning-con {
        margin-right: 12px;
        width: 940px;
        display: flex;
        flex-direction: column;
        align-items: center;
    }
    .right-warning-con {
        flex: 1;
        .ai-main-con {
            margin-top: 12px;
            width: 100%;
            background: #f4f7ff;
            .ai-header {
                padding: 0 12px;
                height: 40px;
                display: flex;
                justify-content: space-between;
                align-items: center;
                background: #355bad;
                .ai-header-title {
                    display: flex;
                    align-items: center;
                    font-size: 14px;
                    color: #fff;
                    .icon-ai {
                        margin-right: 8px;
                        display: inline-block;
                        width: 28px;
                        height: 28px;
                        background: url("../../../../assets/img/alarmEvent/icon-ai.png") no-repeat center center;
                        background-size: contain;
                    }
                }
                .ai-header-btn {
                    padding: 0 8px;
                    height: 24px;
                    line-height: 24px;
                    background: #ebeff7;
                    border-radius: 4px;
                    font-size: 14px;
                    color: #355bad;
                    cursor: pointer;
                }
            }
            .ai-analysis-con {
                padding: 12px;
                height: 276px;
                border: 1px solid rgba(0, 0, 0, 0.06);
                border-top: none;
                box-sizing: border-box;
                overflow-y: auto;
                font-size: 14px;
                color: #355bad;
                line-height: 22px;
            }
        }
    }
    .left-warning-con {
        .carousel-container {
            width: 100%;
            height: 720px;

            .main-carousel-wrapper {
                position: relative;
                height: 612px;

                .el-carousel {
                    height: 100%;
                    .el-carousel__item {
                        width: 100%;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        background-color: #efefef;
                    }
                    .carousel-image {
                        max-width: 100%;
                        max-height: 100%;
                    }
                }

                .custom-nav {
                    .nav-btn {
                        position: absolute;
                        top: 50%;
                        transform: translateY(-50%);
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        color: #fff;
                        width: 40px;
                        height: 96px;
                        background: rgba(0, 0, 0, 0.45);
                        border-radius: 0;
                        cursor: pointer;
                        z-index: 10;
                        transition: background 0.3s;

                        &:hover {
                            background: rgba(0, 0, 0, 0.6);
                        }

                        &.prev {
                            left: 0;
                        }

                        &.next {
                            right: 0;
                        }

                        i {
                            font-size: 14px;
                        }
                    }
                }
            }

            .thumbnail-container {
                margin-top: 12px;
                height: 96px;

                .thumb-scroll-wrapper {
                    position: relative;
                    display: flex;
                    align-items: center;
                    height: 100%;

                    .thumb-nav-btn {
                        position: absolute;
                        top: 0;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        width: 40px;
                        height: 100%;
                        background: rgba(0, 0, 0, 0.45);
                        cursor: pointer;
                        z-index: 2;
                        transition: background 0.3s;
                        flex-shrink: 0;

                        &:hover {
                            background: rgba(0, 0, 0, 0.55);
                        }

                        &.left {
                            left: 0;
                        }

                        &.right {
                            right: 0;
                        }

                        i {
                            font-size: 14px;
                            color: #fff;
                        }
                    }

                    .thumbnails-scroll {
                        display: flex;
                        gap: 8px;
                        height: 100%;
                        overflow-x: auto;
                        scroll-behavior: smooth;
                        flex: 1;

                        /* 隐藏滚动条 */
                        &::-webkit-scrollbar {
                            display: none;
                        }
                        -ms-overflow-style: none;
                        scrollbar-width: none;

                        .thumbnail-item {
                            flex-shrink: 0;
                            width: 170px;
                            cursor: pointer;
                            transition: all 0.3s ease;
                            opacity: 0.6;
                            overflow: hidden;
                            box-sizing: border-box;

                            .thumbnail-image {
                                width: 100%;
                                height: 100%;
                                // object-fit: cover;
                            }

                            &.active {
                                opacity: 1;
                            }

                            &:hover {
                                opacity: 0.8;
                            }
                        }
                    }
                }
            }
            .default-image {
                width: 100%;
                height: 100%;
                display: flex;
                justify-content: center;
                align-items: center;
                background-color: #f5f7fa;
                .el-icon-picture {
                    font-size: 60px;
                    color: #909399;
                }
            }

            .default-thumbnail {
                width: 100%;
                height: 100%;
                display: flex;
                justify-content: center;
                align-items: center;
                background-color: #f5f7fa;
                .el-icon-picture {
                    font-size: 24px;
                    color: #909399;
                }
            }
        }
    }
}
</style>
