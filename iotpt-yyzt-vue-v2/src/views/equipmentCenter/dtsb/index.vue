<template>
    <div class="index-container">

        <vab-query-form>
            <vab-query-form-left-panel :span="19" class="left-panel-self">
                <el-button icon="el-icon-plus" plain type="success" @click="handleEdit">添加</el-button>
            </vab-query-form-left-panel>
        </vab-query-form>

        <el-table v-loading="listLoading" :data="list" element-loading-text="正在加载..." stripe
            height="calc(100vh - 190px)" border>
            <el-table-column prop="index" type="index" label="序号" width="80" align="center"></el-table-column>
            <el-table-column show-overflow-tooltip prop="liftName" label="设备名称" align="center"></el-table-column>
            <el-table-column show-overflow-tooltip prop="typeName" label="设备类型" align="center"></el-table-column>
            <el-table-column show-overflow-tooltip prop="deviceCode" label="设备编码" align="center"></el-table-column>
            <el-table-column show-overflow-tooltip prop="deviceSn" label="设备SN" align="center"></el-table-column>
            <el-table-column show-overflow-tooltip prop="areaNames" label="所属区域" align="center">
                <template #default="{row}">
                    {{ row.buildName }}/{{ row.floorName }}/{{ row.areaName }}
                </template>       
            </el-table-column>
            <el-table-column show-overflow-tooltip prop="deviceDesc" label="设备描述" align="center"></el-table-column>
            <el-table-column label="状态" width="100" align="center">
                <template slot-scope="{row}">
                    <el-link :underline="false" icon="el-icon-info" type="info" v-if="row.liftStatus == 1"> 未使用</el-link>
                    <el-link :underline="false" icon="el-icon-success" type="success" v-if="row.liftStatus == 2"> 使用中
                    </el-link>
                    <el-link :underline="false" icon="el-icon-error" type="danger" v-if="row.liftStatus == 3"> 损坏</el-link>
                    <el-link :underline="false" icon="el-icon-warning" type="warning" v-if="row.liftStatus == 4"> 维修中
                    </el-link>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="260" align="center">
                <template #default="{ row,$index }">
                    <el-button icon="el-icon-edit" type="success" size="mini" @click="handleEdit(row)">编辑</el-button>
                    <el-button icon="el-icon-delete" type="danger" size="mini" @click="handleDelete(row,$index)">删除</el-button>
                    <el-button icon="el-icon-info" type="primary" size="mini" @click="handleEdit(row,true)">详情</el-button>
                </template>
            </el-table-column>
        </el-table>
        <edit ref="edit" @fetch-data="fetchData"></edit>
    </div>
</template>

<script>
import { getList,del } from '@/api/dtsb'

import edit from './components/edit'
export default {
    name: 'dtsbIndex',
    components: { edit },
    data() {
        return {
            list: null,
            listLoading: true
        }
    },
    created() {
        this.fetchData();
    },
    methods: {
        handleEdit(row,isSee) {
            if (row.id) {
                this.$refs['edit'].showEdit(row.id,isSee);
            } else {
                this.$refs['edit'].showEdit();
            }
        },
        handleDelete(row,index) {
            if (row.id) {
                if (row.liftStatus == 2) {
                    this.$baseMessage('该设备状态为使用中暂无法删除', 'error');
                    return
                }
                this.$baseConfirm('你确定要删除当前项吗', '提示', () => {
                    del({id:row.id,isDel:1}).then(res=>{
                        if (res.code == 0) {
                            this.$baseMessage('删除成功', 'success');
                            this.list.splice(index,1);
                        }
                    })
                })
            }
        },
        fetchData() {
            this.listLoading = true;
            getList().then(res => {
                this.listLoading = false;
                this.list = res.data;
            })
        }
    },
}
</script>
<style></style>
