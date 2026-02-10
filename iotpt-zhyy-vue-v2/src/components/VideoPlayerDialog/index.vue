<script>
import { VideoPlayer } from "./video";
import * as buffer from "buffer";

export const PlayerType = {
    flv: "flv",
    hls: "hls"
};

export default {
    mixins: [VideoPlayer],
    data() {
        return {
            title: "实时视频",
            // 播放地址
            videoUrl: "",
            type: PlayerType.flv
        };
    },
    created() {
        this.dialogPlayer.loading = true;
    },
    mounted() {
        if (this.type === PlayerType.flv) {
            this.$nextTick(() => {
                this.createFlvVideoPlayer(this.videoUrl, "dialogVideo").then((player) => {
                    if (player) {
                        this.dialogPlayer.video = player;
                    }
                    this.dialogPlayer.loading = false;
                });
            });
        } else {
            this.$nextTick(() => {
                this.createVideoJsPlayer(this.videoUrl, "dialogVideo").then((player) => {
                    if (player) {
                        this.dialogPlayer.video = player;
                    }
                    this.dialogPlayer.loading = false;
                });
            });
        }
    }
};
</script>

<template>
    <el-dialog :title="title" :visible.sync="videoDialogShow" width="1100px" append-to-body @close="onDialogClose">
        <div style="text-align: right; overflow: hidden">
            <!-- <div style="background-color: #eee; padding: 10px">
                <i
                    class="el-icon-close"
                    style="color: #000; font-size: 20px; cursor: pointer"
                    @click="onDialogClose"
                ></i>
            </div> -->
            <div
                style="height: 600px; width: 100%"
                v-loading="dialogPlayer.loading"
                element-loading-text="拼命加载中"
                element-loading-spinner="el-icon-loading"
                element-loading-background="rgba(0, 0, 0, 0.8)"
            >
                <video
                    id="dialogVideo"
                    style="height: 600px; width: 100%; background-color: #5a5e66; object-fit: fill"
                />
            </div>
        </div>
    </el-dialog>
</template>

<style scoped lang="scss">
// ::v-deep .el-dialog__header {
//     display: none;
// }

// ::v-deep .el-dialog {
//     padding: 0;
//     background-color: rgba(0, 0, 0, 0.5);
//     background-size: 100% auto;
// }

// ::v-deep .el-dialog__body {
//     padding: 0;
// }

// ::v-deep .el-dialog__wrapper {
//     display: flex;
//     align-items: center;
// }
</style>
