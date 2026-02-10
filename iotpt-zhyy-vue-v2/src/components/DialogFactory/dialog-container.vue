<template>
    <div style="display: none">
        <template v-if="config.type === 'dialog'">
            <el-dialog
                modal-append-to-body
                append-to-body
                :close-on-click-modal="false"
                :close-on-press-escape="false"
                :visible.sync="show"
                :title="config.title"
                :show-close="!btnLoading"
                :width="config.width"
                @closed="onClosed"
            >
                <section v-loading="loading" class="dialog-main">
                    <div class="dialog-container" :style="contentStyle">
                        <slot></slot>
                    </div>
                    <div slot="footer" class="dialog-footer" v-if="showFooter" style="padding-top: 10px">
                        <el-button :loading="btnLoading" @click="onCancel">{{ config.cancelText }}</el-button>
                        <el-button :loading="btnLoading" type="primary" @click="onConfirm">
                            {{ config.confirmText }}
                        </el-button>
                    </div>
                </section>
            </el-dialog>
        </template>
        <template v-else-if="config.type === 'drawer'">
            <el-drawer
                append-to-body
                :wrapperClosable="false"
                :close-on-press-escape="false"
                :visible.sync="show"
                :title="config.title"
                :show-close="!btnLoading"
                :size="config.width"
                @closed="onClosed"
            >
                <section v-loading="loading" class="dialog-main">
                    <div class="dialog-container" :style="contentStyle">
                        <slot></slot>
                    </div>
                    <div slot="footer" class="dialog-footer" v-if="showFooter">
                        <el-button :loading="btnLoading" @click="onCancel">{{ config.cancelText }}</el-button>
                        <el-button :loading="btnLoading" type="primary" @click="onConfirm">
                            {{ config.confirmText }}
                        </el-button>
                    </div>
                </section>
            </el-drawer>
        </template>
    </div>
</template>

<script lang="js">
export default {
    props: {
        done: {
            type: Function | null,
            default: null
        }
    },
    data() {
        return {
            show: true,
            btnLoading: false,
            elFormsInstance: [],
            loading: true,
            config: {
                showCancel: true,
                showConfirm: true,
                confirmText: "确认",
                cancelText: "取消",
                title: "标题",
                height: "100%",
                width: "800px",
                type: ""
            }
        };
    },
    computed: {
        showFooter() {
            return this.config.showCancel || this.config.showConfirm;
        },
        contentStyle() {
            return {
                height: this.height
            };
        }
    },
    updated() {
        if (this.elFormsInstance.length == 0) {
            this.getForms().then((list) => {
                this.elFormsInstance = list;
                this.loading = false;
            });
        }
    },
    methods: {
        getForms() {
            let elForms = [];
            // 获取所有子孙ElForm组件，用于后续的表单验证
            function getAllChildForm(children) {
                for (let i = 0; i < children.length; i++) {
                    const element = children[i];
                    if (element.$options.name == "ElForm") {
                        elForms.push(element);
                    }
                    if (element.$children && element.$children.length > 0) {
                        getAllChildForm(element.$children);
                    }
                }
            }
            return new Promise((resolve) => {
                this.$nextTick(() => {
                    getAllChildForm(this.$children);
                    resolve(elForms);
                });
            });
        },
        onCancel() {
            this.show = !this.show;
        },
        async onConfirm() {
            this.btnLoading = !this.btnLoading;

            // 有表单验证或者需要做逻辑处理
            if (this.elFormsInstance.length > 0 || this.done) {
                try {
                    await Promise.all(this.elFormsInstance.map((form) => form.validate()));
                    if (this.done) {
                        const result = await this.done();

                        // true 需要关闭弹框
                        if (result) {
                            this.onCancel();
                            return;
                        }
                        // 否者不关闭弹框，并且取消按钮加载状态
                        else {
                            this.btnLoading = !this.btnLoading;
                        }
                    } else {
                        this.onCancel();
                    }
                } catch (e) {
                    this.btnLoading = !this.btnLoading;
                }
            }
            // 关闭弹框
            else {
                this.onCancel();
            }
        },
        onClosed() {
            if (this.$parent) {
                // 通知父组件，需要销毁组件
                this.$parent.$emit("destroy");
            }
        },
        clearValidate() {
            if (this.elFormsInstance.length > 0) {
                this.elFormsInstance.forEach((element) => element.clearValidate());
            }
        },
        validateField(props) {
            const forms = this.elFormsInstance.filter((item) => {
                if (item.rules) {
                    return Reflect.has(item.rules, props);
                }
                return false;
            });
            forms.forEach((item) => {
                item.validateField(props);
            });
        },
        resetFields() {
            if (this.elFormsInstance.length > 0) {
                this.elFormsInstance.forEach((element) => element.resetFields());
            }
        }
    },
    destroyed() {
        document.body.removeChild(this.$el);
    }
};
</script>

<style lang="scss" scoped>
.dialog-footer {
    text-align: right;
    background: #fff;
}
.el-table td.el-table__cell,
.el-table th.el-table__cell.is-leaf {
    border-bottom: 1px solid #ebeef5;
}
-table__cell.is-center {
    text-align: center;
}

::v-deep .el-dialog__header {
    border-bottom: 1px solid #eee;
}

.el-table th {
    background: #f5f5f5 !important;
    padding: 5px 5px !important;
}
.el-drawer__body {
    .dialog-main {
        padding-bottom: 54px;
        padding-inline: 20px;
        min-height: 100%;
    }

    .dialog-footer {
        text-align: right;
        position: absolute;
        bottom: 0;
        left: 0;
        width: 100%;
        padding: 10px;
        border: 1px solid #eee;
    }
}
</style>
