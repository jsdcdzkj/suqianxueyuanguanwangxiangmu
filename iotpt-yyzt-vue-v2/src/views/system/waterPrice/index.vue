<template>
    <div class="index-container">
        <el-form :model="ruleForm" :rules="rules" ref="ruleForm" class="demo-ruleForm">
            <div class="nav-group__title">用水基础配置项</div>
            <el-form-item label="基础水价" prop="price">
                <el-input-number v-model="ruleForm.price" :disabled="saveStatus" :precision="2" :step="0.1"
                    :max="99"></el-input-number>
            </el-form-item>
            <el-row :gutter="20">
                <el-col :span="20">
                    <el-form-item>
                        <el-button type="primary" :disabled="saveStatus" @click="submitForm('ruleForm')">保 存</el-button>
                    </el-form-item>
                </el-col>
            </el-row>
        </el-form>
    </div>
</template>

<script>

import { getWaterPrice, setWaterPrice } from "@/api/config";

export default {
    name: 'waterPriceConfig',
    components: {},
    data() {
        return {
            ruleForm: {
                price: 1,
            },
            rules: {
                price: [
                    { required: true, message: '价格不能为空' },
                ]
            },
            saveStatus: false
        }
    },
    created() {
        getWaterPrice().then(res => {
            if (res.data[0]) {
                this.ruleForm.price = res.data[0].price;
            }
        })
    },
    methods: {
        submitForm(formName) {
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    this.saveStatus = true;
                    setWaterPrice(this.ruleForm).then(() => {
                        this.$baseMessage("保存成功", 'success');
                    }).finally(() => {
                        this.saveStatus = false;
                    })
                } else {
                    console.log('error submit!!');
                    return false;
                }
            });
        },
    }
}
</script>
<style scoped lang="scss">
.mb2 {
    margin-bottom: 14px;
}

.index-container {
    height: calc(100vh - 144px);
}

.nav-group__title {
    font-size: 12px;
    color: #999;
    line-height: 26px;
}
</style>
