<template>
    <el-card class="left-tree-box relative" shadow="never">
        <div slot="header" class="card-header-self">
            <div class="video-title">
                <h4>请选择区域</h4>
            </div>
            <div class="check_box">
                <el-checkbox v-model="caseCheckAll" @change="handleCheckAllChange">全部区域</el-checkbox>
                <div>
                    <el-switch v-model="isArea" @change="changeIsArea" style="margin-right: 4px"
                        :width="30"></el-switch>查看区域
                </div>
            </div>
        </div>
        <el-input v-model.trim="filterText" clearable placeholder="输入关键字进行过滤" style="margin-bottom:12px;" />
        <!-- 没有区域 -->
        <el-tree ref="tree1" key="tree1" v-if="!isArea" :data="filterList" show-checkbox default-expand-all
            node-key="id" :filter-node-method="filterNode" @check="floorCheck" />

        <!-- 只有区域 -->
        <el-tree ref="tree" key="tree" v-if="isArea" :data="filterList" default-expand-all node-key="id"
            :filter-node-method="filterNode" :expand-on-click-node="false">
            <div class="custom-tree-node" slot-scope="{ node, data }">
                <div>
                    <el-checkbox v-model="data.checked" v-if="node.level == 3"
                        @input="checkChange($event, node)"></el-checkbox>
                    {{ data.label }}
                </div>
                <div v-if="node.level == 2">
                    <el-checkbox v-model="data.checkAll" @change="setALL($event, data)"></el-checkbox>
                    全选
                </div>
            </div>
        </el-tree>
    </el-card>
</template>
<script>
import { areaTreeList } from "@/api/smartEnergyReport";
export default {
    name: 'areaCheck',
    data() {
        return {
            caseCheckAll: false,
            isArea: false,
            filterText: '',
            logicalAreaList: [],
            defaultProps: {
                children: 'children',
                label: 'label'
            },
            showAreaList: [],
            filterList: []
        }
    },
    watch: {
        filterText(val) {
            if (this.$refs.tree) {
                this.$refs.tree.filter(val)
            }
            if (this.$refs.tree1) {
                this.$refs.tree1.filter(val)
            }
        }
    },
    computed: {
        allKeys() {
            return this.getKeys(this.filterList);
        }
    },
    mounted() {
        areaTreeList().then((res) => {
            if (res.code == 0) {
                this.logicalAreaList = res.data;
                this.filterList = this.setList();
            }
        });
    },
    methods: {
        getKeys(data) {
            if (this.isArea) {
                var arr = [];
                this.filterList.map(i => {
                    i.children.map(e => {
                        e.children.map(el => {
                            arr.push(el.id);
                        })
                    })
                })
                return arr;
            } else {
                var arr = [];
                data.map(el => {
                    arr.push(el.id);
                    if (el.children) {
                        arr = [...arr, ...this.getKeys(el.children)]
                    }
                })
                return arr;
            }
        },
        filterNode(value, data) {
            if (!value) return true
            return data.label.indexOf(value) !== -1
        },
        checkChange() {
            this.setChecked();
            this.emitEvent();
        },
        setALL(val, data) {
            this.filterList.map(el => {
                el.children.map(item => {
                    if (item.id == data.id) {
                        item.children.map(i => {
                            i.checked = val;
                        })
                    }
                })
            })
            this.caseCheckAll = this.filterList.every(el => {
                return el.children.every(item => item.checkAll);
            })
            this.emitEvent();
        },
        setChecked() {
            this.filterList.map(e => {
                e.children.map(el => {
                    if (el.children.every(item => item.checked)) {
                        el.checkAll = true;
                    } else {
                        el.checkAll = false;
                    }
                })
            })
            this.caseCheckAll = this.filterList.every(el => {
                return el.children.every(item => item.checkAll);
            })
        },
        floorCheck(data, { checkedKeys }) {
            if (checkedKeys.length == this.allKeys.length) {
                this.caseCheckAll = true;
            } else {
                this.caseCheckAll = false;
            }
            this.emitEvent();
        },
        // 区域全选
        handleCheckAllChange(val) {
            if (this.isArea) {
                var array = JSON.parse(JSON.stringify(this.filterList));
                array.map(el => {
                    el.children.map(item => {
                        item.checkAll = val;
                        item.children.map(li => {
                            li.checked = val;
                        })
                    })
                })
                this.filterList = array;
            } else {
                this.$refs.tree1.setCheckedKeys(val ? this.allKeys : []);
            }
            this.emitEvent();
        },
        // 选择是否显示区域
        changeIsArea(val) {
            this.$emit('isArea', val);
            this.caseCheckAll = false;
            this.filterList = this.setList();
        },
        // 设置树形列表数据
        setList() {
            var array = JSON.parse(JSON.stringify(this.logicalAreaList));
            if (this.isArea) {
                return array.map(el => {
                    el.children = el.children.map(item => {
                        item.checkAll = false;
                        item.children = item.children.map(li => {
                            li.checked = false;
                            return li;
                        })
                        return item;
                    })
                    return el
                })
            } else {
                return array.map(el => {
                    el.children = el.children.map(item => {
                        delete item.children;
                        return item
                    })
                    return el;
                })
            }
        },
        emitEvent() {
            this.throttle(() => {
                var areaIds = [];
                var floorIds = [];
                var buildIds = [];
                if (!this.isArea) {
                    var checks = this.$refs.tree1.getCheckedKeys();
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
                } else {
                    this.filterList.map(e => {
                        e.children.map(el => {
                            el.children.map(item => {
                                if (item.checked) {
                                    areaIds.push(item.id.replace('area_', ''));
                                }
                            })
                        })
                    })
                    if (this.caseCheckAll) {
                        buildIds = this.logicalAreaList.map(el => { return el.id.replace('build_', '') })
                    }
                }
                this.$emit('areaChecked', { areaIds, floorIds, buildIds });
            }, 500, false)
        }
    }
}
</script>

<style lang="scss" scoped>
.video-title {
    margin-bottom: 14px;
    padding: 0 0 15px;
    margin-top: 0;
}

.check_box {
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
}

.custom-tree-node {
    width: 100%;
    font-size: 14px;
    display: flex;
    align-items: center;
    justify-content: space-between;
}
</style>
