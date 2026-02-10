<template>
    <el-dialog :close-on-click-modal="false" :title="title" :visible.sync="dialogFormVisible" width="500px"
               @close="close">
        <el-form ref="form" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="类型名称" prop="deviceTypeName">
                <el-input v-model.trim="form.deviceTypeName" autocomplete="off" placeholder="请输入设备类型名称"></el-input>
            </el-form-item>
            <el-form-item label="类型编码" prop="deviceTypeCode">
                <el-input v-model.trim="form.deviceTypeCode" autocomplete="off" placeholder="请输入设备类型编码"></el-input>
            </el-form-item>

            <el-form-item label="类型描述" prop="deviceTypeDesc">
                <el-input type="textarea" v-model="form.deviceTypeDesc" placeholder="请输入设备类型描述"></el-input>
            </el-form-item>

            <el-form-item label="上传图片">
                <el-upload
                    action=""
                    :on-preview="handlePreview"
                    :on-remove="handleRemove"
                    :file-list="fileList"
                    :limit="1"
                    list-type="picture"
                    :on-change="fileChange"
                    :on-exceed="onExceed"
                    :multiple="false"
                    :auto-upload="false"
                >
                    <el-button size="small" type="primary">点击上传</el-button>
                    <div slot="tip" class="el-upload__tip">
                        只能上传一张jpg/png文件，且不超过500kb
                    </div>
                </el-upload>
            </el-form-item>

        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="close">取 消</el-button>
            <el-button type="primary" @click="save" :loading="loading" :disabled="disabled">
                {{ loading ? '确定中 ...' : '确定' }}
            </el-button>
        </div>
        <bumen ref="bumen" @fetch-data=""></bumen>
    </el-dialog>
</template>

<script>
    import {importData, saveOrUpdate} from '@/api/configDeviceType'
    import bumen from '@/components/bumen'
    import {baseURL} from '@/config'

    export default {
        name: 'UserManagementEdit',
        components: {bumen},
        data() {
            // const validateLogo = (rule, value, callback) => {
            //     if (!this.form.file) {
            //         callback(new Error('请上传图片'))
            //     } else {
            //         callback()
            //     }
            // }
            return {
                loading: false,
                disabled: false,
                form: {
                    deviceTypeName: '',
                    deviceTypeCode: '',
                    deviceTypeDesc: '',
                },
                rules: {
                    deviceTypeName: [
                        {required: true, trigger: 'blur', message: '请输入设备类型名称'},
                    ],
                    deviceTypeCode: [
                        {required: true, trigger: 'blur', message: '请输入设备类型编码'},
                    ],
                    deviceTypeDesc: [
                        {required: true, trigger: 'blur', message: '请输入设备类型描述'},
                    ],
                },
                title: '',
                dialogFormVisible: false,
                fileList: [],
            }
        },
        created() {
        },
        methods: {
            showEdit(row) {
                if (!row) {
                    this.title = '添加'
                } else {
                    this.title = '编辑'
                    this.form = Object.assign({}, row)
                    if (row.filename) {
                        const file = {}
                        file.name = row.filename
                        file.url = baseURL+'/minio/previewFile?fileName='+row.filename
                        this.fileList.push(file)
                    }
                }
                this.dialogFormVisible = true
            },
            close() {
                this.$refs['form'].resetFields()
                this.form = this.$options.data().form
                this.fileList = []
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

                        this.form.file = this.fileList[0]
                        if (!this.form.file) {
                            return this.$baseMessage('请上传图片')
                        }
                        this.form.filename = this.form.file.filename
                        this.form.originalFilename = this.form.file.originalFilename
                        
                        const {msg} = await saveOrUpdate(this.form)
                        this.$baseMessage(msg, 'success')
                        this.$emit('fetch-data')
                        this.close()
                    } else {
                        return false
                    }
                })
            },
            openwin() {
                var that = this
                that.$refs['bumen'].showDia()
            },
            onExceed(file, fileList) {
                this.$message.warning('最多上传1张图片！')
            },
            async fileChange(file, fileList) {
                // if (file.status !== 'ready') return // 阻止上传成功的触发
                // console.log(file.status)
                var testmsg = file.name.substring(file.name.lastIndexOf('.') + 1)
                const extension = testmsg === 'png' || testmsg === 'jpg'
                if (!extension) {
                    this.$message({
                        message: '上传文件只能是.png或者.jpg格式!',
                        type: 'warning',
                    })
                    return
                }
                this.fileList = fileList
                let fd = new FormData()
                //文件信息中raw才是真的文件
                fd.append('file', this.fileList[0].raw)

                var result = await importData(fd)
                console.log('11111', result)
                if (result.data.code === 0) {
                    console.log('222222222222222', result.data)
                    this.fileList = []
                    // this.$refs.upfile.clearFiles();
                    //   console.log("result.data.data:",result.data.data)
                    const file = {}
                    file.name = result.data.data.filename
                    file.url = baseURL+'/minio/previewFile?fileName='+result.data.data.filename
                    file.filename = result.data.data.filename
                    file.originalFilename = result.data.data.originalFilename

                    this.fileList.push(file)
                    console.log('1111111111111', this.fileList, file)
                    this.$baseMessage('上传成功', 'success')
                } else {
                    this.$baseMessage(result.data.msg, 'error')
                }
                // this.dialog = false
                // this.loading = false
            },
            // handleChange(file, fileList) {
            //     /*
            //       自动上传改成true会导致重复触发，所以在handleChange上传文件
            //       */
            //     // 防抖
            //     let length = fileList.length
            //     this.maxLength = Math.max(length, this.maxLength)
            //     setTimeout(() => {
            //         if (length === this.maxLength) {
            //             // 图片验证
            //             this.uploadFilesVerification(file, fileList)
            //         }
            //     }, 100)
            // },
            // // 图片验证
            // uploadFilesVerification(file, fileList) {
            //     var testmsg = file.name.substring(file.name.lastIndexOf('.') + 1)
            //     const extension = testmsg === 'png' || testmsg === 'jpg'
            //     if (!extension) {
            //         this.$message({
            //             message: '上传文件只能是.png或者.jpg格式!',
            //             type: 'warning',
            //         })
            //         return
            //     }

            //     for (const key in fileList) {
            //         console.log('fileList[key] :>> ', fileList[key])
            //         // 判断文件大小(5M)
            //         if (fileList[key].size > 1024 * 1024 * 5) {
            //             this.$message.warning('图片大小不可以超过5M')
            //             return
            //         }
            //     }

            //     // 上传图片
            //     this.customUploadFn(file, fileList)
            // },
            // // 自定义上传
            // async customUploadFn(file, fileList) {
            //     this.fileList = fileList
            //     let form = new FormData()
            //     this.fileList.forEach((item) => {
            //         form.append('files', item.raw)
            //     })
            //     var result = await importData(form)
            //     if (result.data.code === 0) {
            //         // this.$refs.upfile.clearFiles();
            //         this.fileList = []
            //         result.data.data.name = result.data.data.fileName
            //         result.data.data.url = result.data.data.filePath
            //         this.fileList.push(result.data.data)
            //         this.$baseMessage('上传成功', 'success')
            //     } else {
            //         this.$baseMessage(result.data.msg, 'error')
            //     }
            //     // this.fileList = [] // 清空图片已经上传的图片列表（绑定在上传组件的file-list）
            //     this.maxLength = 0 // 恢复默认值
            // },
            handleRemove(file, fileList) {
                console.log(file, fileList)
                this.fileList = fileList
            },
            handlePreview(file) {
                console.log(file)
            },
        },
    }
</script>
