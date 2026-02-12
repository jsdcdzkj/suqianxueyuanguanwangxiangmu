<template>
    <el-dialog
        :close-on-click-modal="false"
        :visible.sync="dialogFormVisible"
        @close="close"
        width="720px"
        append-to-body
    >
        <span slot="title" class="drawer-title">
            <b>AI智能分析</b>
        </span>
        <div class="chart-height">
            <div class="ai-header">
                <img src="@/assets/img/alarmEvent/icon-ai.png" alt="">
                <div class="info">
                    <div class="name">Dincher小驰</div>
                    <div class="time">{{ this.currentDate }}</div>
                </div>
            </div>
            <div class="ai-content" v-html="markedHtml">
            </div>
        </div>
        <div slot="footer" class="dialog-footer">
            <el-button @click="copyAi">复制</el-button>
            <el-button type="primary" style="background: #1E95F4; border: 1px solid #1E95F4" @click="dialogFormVisible = false">
                确定
            </el-button>
        </div>
    </el-dialog>
</template>

<script>
import dayjs from "dayjs";
const { marked } = require('marked');
const htmlToText = require('html-to-text');
function fallbackCopy(text) {
    prompt('请手动复制以下内容:', text);
}
export default {
    name: "batchDeal",
    props: {
        markedHtml: {
            type: String,
            default: ''
        },
        aiWord: {
            type: String,
            default: ''
        }
    },
    data() {
        return {
            dialogFormVisible: false,
            queryParams: {
                id: 1,
                workingHours: 0,
                meeting_reservation_duration: 0,
                room_reserve_count: 0
            },
            rules: {
                meeting_reservation_duration: [{ required: true, message: "请输入会议平均时长", trigger: "blur" }],
                workingHours: [{ required: true, message: "请输入工作平均时长", trigger: "blur" }],
                room_reserve_count: [{ required: true, message: "请输入包间平均预定次数", trigger: "blur" }]
            },
            saveDisabled: false,
            dealTypeList: [
                {
                    dictLabel: '忽略',
                    dictValue: '1',
                },
                {
                    dictLabel: '上报',
                    dictValue: '2',
                },
                {
                    dictLabel: '误报',
                    dictValue: '3',
                },
                {
                    dictLabel: '删除',
                    dictValue: '4',
                },
            ],
            currentDate: ''
        };
    },
    methods: {
        showEdit(row) {
            const date = dayjs()
		    this.currentDate = date.format('YYYY年MM月DD日 HH:mm')
            this.dialogFormVisible = true;
            // this.queryParams = {
            //     id: row.id,
            //     workingHours: row.workingHours,
            //     meeting_reservation_duration: row.meeting_reservation_duration,
            //     room_reserve_count: row.room_reserve_count
            // };
        },
        close() {
            this.dialogFormVisible = false;
        },
         // AI相关
         copyAi() {
            if (this.aiWord) {
                const copyText = this.markedParse(this.aiWord);
                try {
                    navigator.clipboard.writeText(copyText).then(() => {
                        this.$message.success('复制成功');
                    });
                } catch (err) {
                    // 创建一个临时的 textarea 元素
                    const textarea = document.createElement('textarea');
                    textarea.value = copyText;
                    textarea.style.position = 'fixed';
                    document.body.appendChild(textarea);
                    textarea.select();
                    try {
                        const success = document.execCommand('copy');
                        if (success) {
                            this.$message.success('复制成功');
                        } else {
                            fallbackCopy(copyText); // 如果失败，尝试备用方法
                        }
                    } catch (err) {
                        fallbackCopy(copyText); // 如果出错，尝试备用方法
                    }
                    // 移除临时元素
                    document.body.removeChild(textarea);
                    // this.$message.error('复制失败');
                }
            }
        },
        markedParse(val) {
            const html = marked(val);
            // 再转换为纯文本
            const text = htmlToText.fromString(html, {
                wordwrap: false,
            });
            return text;
        },
        
    }
};
</script>
<style scoped lang="scss">
::v-deep {
    .el-dialog {
        border-radius: 8px;
    }
   .el-dialog__header {
    background: #233388;
    font-size: 16px;
    color: #FFFFFF;
    line-height: 24px;
    border-radius: 8px 8px 0 0;
   }
   .el-dialog__body {
        border-bottom: 1px solid rgba(255,255,255,0.2);
        background: #0953B2;
        padding: 20px 24px;
    }

    .el-dialog__footer {
        background: #0953B2;
        border-radius: 0 0 8px 8px;
        padding: 12px 20px;
    }
}

.drawer-title {

}

.chart-height {
    // height: 600px;

    .ai-header {
        display: flex;
        align-items: center;
        margin-bottom: 16px;

        img {
            width: 54px;
            height: 56px;
            margin-right: 12px;
        }

        .info {
            display: flex;
            flex-direction: column;
            justify-content: space-between;

            .name {
                font-size: 14px;
                color: #FFFFFF;
                line-height: 22px;
            }

            .time {
                font-size: 12px;
                color: rgba(255,255,255,0.65);
                line-height: 20px;
                margin-top: 4px;
            }
        }
        
    }

    .ai-content {
        width: 672px;
        height: 596px;
        overflow: auto;
        padding:24px;
        color: rgba(255,255,255,0.85);
        background: #0953B2;
        box-shadow: inset 0px 0px 24px 0px rgba(29,146,255,0.44);
        border-radius: 8px 8px 8px 8px;
        border: 1px solid;
        border-image: linear-gradient(180deg, rgba(112, 189, 255, 1), rgba(132, 147, 171, 0.2)) 1 1;
    }
}


</style>
