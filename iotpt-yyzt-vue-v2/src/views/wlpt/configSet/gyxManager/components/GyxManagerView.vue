<template>
   <el-dialog :close-on-click-modal="false" title="详情" :visible.sync="dialogFormVisible" width="808px" @close="close" top="10vh">
        <!--<div class="tips-title"><i class="el-icon-s-promotion"></i>{{ '供应商名称：' + form.supplierName }}</div>-->
        <el-descriptions class="descriptions-self" :column="2" border label-class-name="my-label"
                         content-class-name="my-content">
            <el-descriptions-item>
                <template slot="label">
                    供应商名称
                </template>
                <span class="contant">{{ form.supplierName || '' }}</span>
            </el-descriptions-item>
            <el-descriptions-item>
                <template slot="label">
                    供应商类型
                </template>
                <span class="contant">{{ selectDictLabel(systematicType, form.supplierType) }}</span>
            </el-descriptions-item>
            <el-descriptions-item>
                <template slot="label">
                    联系人
                </template>
                <span class="contant">{{ form.contacts || '' }}</span>
            </el-descriptions-item>
            <el-descriptions-item>
                <template slot="label">
                    联系电话
                </template>
                <span class="contant">{{ form.telephone || '' }}</span>
            </el-descriptions-item>
            <el-descriptions-item>
                <template slot="label">
                    地址
                </template>
                <span class="contant">{{ form.address || '' }}</span>
            </el-descriptions-item>
            <el-descriptions-item>
                <template slot="label">
                    说明
                </template>
                <span class="contant">{{ form.illustrate || '' }}</span>
            </el-descriptions-item>
        </el-descriptions>
        <div slot="footer" class="dialog-footer">
            <el-button @click="close">关 闭</el-button>
        </div>
    </el-dialog>
</template>

<script>

import {selectDictLabel} from "@/utils/tools";

export default {
    name: 'configSupplierView',
    data() {
        return {
            disabled: false,
            title: '',
            dialogFormVisible: false,
            form: {
                id: '',
                supplierName: '',
                supplierType: '',
                contacts: '',
                telephone: '',
                address: '',
                illustrate: '',
            },
            systematicType: [],
        }
    },
    created() {
        this.getDictByKey().then(res => {
            this.systematicType = res.data['supplierType']
        })
    },
    methods: {
        selectDictLabel,
        showEdit(row) {
            this.title = row.supplierName

            this.dialogFormVisible = true
            console.log(row)
            this.form = row
        },
        close() {
            this.dialogFormVisible = false
        },
    },
}
</script>
<style lang="scss">
.fjzd-form {
    padding-right: 12px !important;
}
</style>
<style lang="scss" scoped>
.contant {
    cursor: default;
}

.header-title {
    text-align: center;
    position: relative;

    .title-left {
        position: absolute;
        left: 0;
    }
}

.module-title {
    height: 39px;
    line-height: 39px;
    // text-align: center;
    > span {
        margin-left: 5px;
    }

    > span:after {
        content: ''; // 必须有这个属性，否则不显示
        display: inline-block; // 必须有，因为是行内元素
        background-color: #5470c6;
        width: 4px;
        height: 14px;
        position: relative;
        left: 8px;
        top: 2px;
    }

    > svg {
        position: relative;
        top: 3px;
    }
}

.tips-title {
    font-size: 14px;
    margin-bottom: 8px;
    font-weight: bold;
    color: #334c97;

    i {
        margin-right: 4px;
    }
}
</style>
