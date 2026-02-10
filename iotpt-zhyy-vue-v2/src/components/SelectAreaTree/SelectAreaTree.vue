<script>
import { areaTreeList } from "@/api/smartEnergyReport";

export default {
    name: "SelectAreaTree",
    data() {
        return {
            caseCheckAll: false,
            filterText: "",
            logicalAreaList: [],
            defaultProps: {
                children: "children",
                label: "label"
            }
            // allKeys: []
        };
    },
    created() {
        areaTreeList().then((res) => {
            if (res.code == 0) {
                this.logicalAreaList = res.data;
                this.$refs.tree.setCheckedKeys([res.data[0].id]);
                this.$nextTick(() => {
                    this.emitSelect();
                });
            }
        });
    },
    computed: {
        allKeys() {
            return this.getKeys(this.logicalAreaList).flat();
        }
    },
    methods: {
        reset() {
            this.caseCheckAll = false;
            this.$refs.tree.setCheckedKeys([]);
            this.emitSelect();
        },
        setCheckTree() {
            this.$refs.tree.setCheckedKeys([]);
            this.$refs.tree.setCheckedKeys([this.logicalAreaList[0].id]);
            this.emitSelect();
        },
        getKeys(data) {
            if (data && data.length > 0) {
                return data.map((item) => {
                    return [item.id, ...this.getKeys(item.children).flat()];
                });
            }
            return [];
        },
        handleCheckAllChange(val) {
            if (val) {
                this.$refs.tree.setCheckedKeys(this.allKeys);
            } else {
                this.$refs.tree.setCheckedKeys([]);
            }
            this.emitSelect();
        },
        filterNode(value, data) {
            if (!value) return true;
            return data.label.indexOf(value) !== -1;
        },
        handleCheckChange(data, { checkedKeys }) {
            if (checkedKeys.length == this.allKeys.length) {
                this.caseCheckAll = true;
            } else {
                this.caseCheckAll = false;
            }
            this.emitSelect();
        },
        emitSelect() {
            const listParent = this.$refs.tree.getCheckedNodes().filter((item) => Reflect.has(item, "children"));
            const threeValues = this.$refs.tree.getCheckedNodes().filter((item) => !Reflect.has(item, "children"));
            const list = [];
            this.logicalAreaList.forEach((item) => {
                const findIndex = listParent.findIndex((it) => it.id === item.id);
                if (findIndex >= 0) {
                    list.push(listParent[findIndex]);
                    listParent.splice(findIndex, 1);
                }
            });
            this.$emit("selectArea", {
                logicalBuildIds: list.map((item) => item.id.split("_")[1]),
                logicalFloorIds: listParent.map((item) => item.id.split("_")[1]),
                logicalAreaIds: threeValues.map((item) => item.id.split("_")[1])
            });
        }
    }
};
</script>

<template>
    <div>
        <el-checkbox style="margin-bottom: 12px" v-model="caseCheckAll" @change="handleCheckAllChange">
            全部区域
        </el-checkbox>
        <el-input
            v-model.trim="filterText"
            clearable
            placeholder="输入关键字进行过滤"
            style="margin-bottom: 12px; width: 234px"
        />
        <el-tree
            ref="tree"
            :data="logicalAreaList"
            show-checkbox
            default-expand-all
            node-key="id"
            :filter-node-method="filterNode"
            :check-strictly="true"
            :props="defaultProps"
            :expand-on-click-node="false"
            @check="handleCheckChange"
            :check-on-click-node="true"
        />
    </div>
</template>

<style scoped lang="scss"></style>
