<template>
    <el-dialog :close-on-click-modal="false" :visible.sync="dialogFormVisible" width="400px" @close="close"
               title="上传aep终端模板">
        <!-- <el-upload class="upload-demo" drag action="https://jsonplaceholder.typicode.com/posts/" multiple> -->
        <el-upload drag
                   class="upload-demo"
                   ref="upfile"
                   action=""
                   :show-file-list="false"
                   accept=".xls,.xlsx"
                   :auto-upload="false"
                   :multiple="false"
                   :on-change="fileChange"
        >
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <div class="el-upload__tip" slot="tip">aep终端
                <span class="download-link" @click="handleExport">批量导入模板</span>
                ,只能上传excel文件，且不超过200M
            </div>
        </el-upload>
        <div slot="footer" class="dialog-footer">
            <el-button @click="close">取 消</el-button>
            <el-button type="primary" @click="save" :loading="loading" :disabled="disabled">{{
                    loading ? '确定中 ...' : '确定'
                }}
            </el-button>
        </div>
    </el-dialog>
</template>

<script>
import {importUploadData, downloadTemplate} from '@/api/deviceAep'
import {Message} from "element-ui";

export default {
    name: 'uploadView',
    data() {
        return {
            dialogFormVisible: false,
            loading: false,
            disabled: false,
            fileList: [],
            selectRows: '',
            listData: [],
        }
    },
    created() {
    },
    methods: {
        handleExport() {
            // 调用接口获取返回的文件流
            downloadTemplate().then((res) => {
                const blob = new Blob([res], { type: "application/vnd.ms-excel" });
                if ("download" in document.createElement("a")) {
                    // 非IE下载
                    const elink = document.createElement("a");
                    elink.download = 'Aep模板下载.xlsx';
                    elink.style.display = "none";
                    elink.href = URL.createObjectURL(blob);
                    document.body.appendChild(elink);
                    elink.click();
                    URL.revokeObjectURL(elink.href); // 释放URL 对象
                    document.body.removeChild(elink);
                    setTimeout(() => {
                        Message.success('下载成功')
                    }, 500);
                } else {
                    // IE10+下载
                    navigator.msSaveBlob(blob, 'Aep模板下载.xlsx');
                    Message.success('下载成功')
                    setTimeout(() => {
                        Message.success('下载成功')
                    }, 500);
                }
            });
        },
        showEdit(row) {
            console.log()
            this.selectRows = row
            this.dialogFormVisible = true
        },
        close() {
            this.dialogFormVisible = false
        },
        save() {
            if (this.loading) {
                return
            }
            this.$refs['form'].validate(async (valid) => {
                if (valid) {
                    this.loading = true
                    this.disabled = true
                    setTimeout(() => {
                        this.loading = false
                        this.disabled = false
                    }, 1000)

                    const {msg} = await doEdit(this.form)
                    this.$baseMessage(msg, 'success')
                    this.$emit('fetch-data')
                    this.close()
                } else {
                    return false
                }
            })
        },
        async fileChange(file, fileList) {
            var testmsg = file.name.substring(file.name.lastIndexOf(".") + 1);
            const extension = testmsg === "xlsx" || testmsg === "xls";
            if (!extension) {
                this.$message({
                    message: "上传文件只能是.xlsx或者.xls格式!",
                    type: "warning",
                });
                return
            }
            // console.log(localStorage)
            this.dialog = true
            this.loading = true
            this.fileList = fileList;
            let fd = new FormData();
            // fd.append("id", this.selectRows.id);
            this.fileList.forEach(item => {
                //文件信息中raw才是真的文件
                fd.append("file", item.raw);
            })

            var result = await importUploadData(fd);
            if (result.data.code === 0) {
                this.$refs.upfile.clearFiles();
                this.$baseMessage("上传成功", 'success')
                // this.$baseMessage(msg, 'success')
                this.$emit('fetch-data')
                this.close()
            } else {
                this.$baseMessage(result.data.msg, 'error')
            }
            this.dialog = false
            this.loading = false
        },
    },
}
</script>
<style scoped lang="scss">
.title {
    display: flex;
    margin-top: 20px;

    div:first-child {
        margin-right: 40px;
    }
}

.upload-demo {
    width: 370px;
}
.download-link{
    color: #409EFF;
    cursor: pointer;
    &:hover{
        text-decoration: underline;
    }
}
</style>
