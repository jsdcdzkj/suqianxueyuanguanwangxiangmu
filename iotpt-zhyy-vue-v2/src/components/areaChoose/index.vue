<template>
    <el-card class="left-tree-box relative" shadow="never">
        <div slot="header" class="card-header-self">
            <div class="video-title">
                <h4>请选择区域</h4>
            </div>
            <el-checkbox v-model="caseCheckAll" @change="handleCheckAllChange">全部区域</el-checkbox>
        </div>
        <el-input v-model.trim="filterText" clearable placeholder="输入关键字进行过滤" style="margin-bottom:12px;" />
        <el-tree ref="tree" :data="logicalAreaList" show-checkbox default-expand-all node-key="id"
            :filter-node-method="filterNode" :check-strictly="true" :props="defaultProps" @check="handleCheckChange"
            @node-click="nodeClick" :expand-on-click-node="false" />
    </el-card>
</template>
<script>
import { areaTreeList } from "@/api/smartEnergyReport";
export default {
    name: 'areaChoose',
    data() {
        return {
            caseCheckAll: false,
            filterText: '',
            logicalAreaList: [],
            defaultProps: {
                children: 'children',
                label: 'label'
            }
        }
    },
    watch: {
        filterText(val) {
            this.$refs.tree.filter(val)
        }
    },
    computed: {
        allKeys() {
            return this.getKeys(this.logicalAreaList);
        }
    },
    mounted() {
        areaTreeList().then((res) => {
            if (res.code == 0) {
                this.logicalAreaList = res.data;
            }
        });
    },
    methods: {
        getKeys(data) {
            var arr = [];
            data.map(el => {
                arr.push(el.id);
                if (el.children) {
                    arr = [...arr, ...this.getKeys(el.children)]
                }
            })
            return arr;
        },
        filterNode(value, data) {
            if (!value) return true
            return data.label.indexOf(value) !== -1
        },
        nodeClick(data, node) {
            const checked = node.checked;
            node.checked = !checked;
            if (checked) {
                this.caseCheckAll = false;
            } else {
                const checks = this.$refs.tree.getCheckedKeys();
                if (checks.length == this.allKeys.length) {
                    this.caseCheckAll = true;
                }
            }
            this.$nextTick(() => {
                this.emitEvent();
            })
        },
        handleCheckChange(data, { checkedKeys }) {
            let node = this.$refs.tree.getNode(data);
            const checked = node.checked;
            if (node.level == 1) {
                node.childNodes.map(el => {
                    el.checked = checked;
                    el.childNodes.map(item => {
                        item.checked = checked;
                    })
                })
                if (!checked) {
                    this.caseCheckAll = false;
                } else {
                    const checks = this.$refs.tree.getCheckedKeys();
                    if (checks.length == this.allKeys.length) {
                        this.caseCheckAll = true;
                    }
                }
            } else if (node.level == 2) {
                node.childNodes.map(el => {
                    el.checked = checked;
                })
                if (!checked) {
                    this.caseCheckAll = false;
                } else {
                    const checks = this.$refs.tree.getCheckedKeys();
                    if (checks.length == this.allKeys.length) {
                        this.caseCheckAll = true;
                    }
                }
            } else {
                if (checkedKeys.length == this.allKeys.length) {
                    this.caseCheckAll = true;
                } else {
                    this.caseCheckAll = false;
                }
            }
            this.emitEvent();
        },
        handleCheckAllChange(val) {
            if (val) {
                this.$refs.tree.setCheckedKeys(this.allKeys);
            } else {
                this.$refs.tree.setCheckedKeys([]);
            }
            this.emitEvent();
        },
        emitEvent() {
            this.throttle(() => {
                var areaIds = [];
                var floorIds = [];
                var buildIds = [];
                var checks = this.$refs.tree.getCheckedKeys();
                checks.map(el => {
                    if (el.includes('build_')) {
                        buildIds.push(el.replace('build_', ''));
                    }
                    if (el.includes('floor_')) {
                        floorIds.push(el.replace('floor_', ''));
                    }
                    if (el.includes('area_')) {
                        areaIds.push(el.replace('area_', ''));
                    }
                })
                this.$emit('areaChecked', { areaIds, floorIds, buildIds });
            }, 500, false)
        }
    }
}
</script>

<style lang="scss" scoped>
.video-title {
    margin-bottom: 14px;
}
</style>
