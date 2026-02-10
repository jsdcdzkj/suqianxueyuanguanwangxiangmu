<template>
    <el-dialog :close-on-click-modal="false" :title="title" :visible.sync="dialogFormVisible" top="10vh" width="800px" @close="close">
         <el-form ref="form" :model="form" :rules="rules" label-position="top" label-width="80px">
 
             <el-form-item label="组名" prop="groupName">
                 <el-input v-model="form.groupName" autocomplete="off" placeholder="请输入"></el-input>
             </el-form-item>
             <el-form-item label="分组人员">
                 <div class="group-list">
                     <p class="sel-user-num">（<span>{{userCheckedOptions.length}}</span> / 300）</p>
                     <div class="unit-list">
                         <span class="title">单位名称</span>
                         <el-tree
                         :data="unitData"
                         ref="tree"
                         show-checkbox
                         default-expand-all
                         :check-strictly="true"
                         node-key="id"
                         :default-checked-keys="checkedKeys"
                         @check-change="checkGroupNode"
                         @node-click="handleNodeClick"
                         :render-after-expand="false"
                         :props="defaultProps">
                         </el-tree>
                     </div>
                     <div class="dept-list">
                         <span class="title">部门名称</span>
                         <div class="dept-item" v-for="(item,index) in deptListArry" :key="index">
                             <el-checkbox v-model="deptCheckedOptions" @change="toShowUser(item.id)" :disabled="item.sysUserList.length == 0" :label="item.id"><span @click.stop.prevent="justToShow(item.id)">{{item.deptName}}</span></el-checkbox>
                         </div>
                     </div>
                     <div class="user-list">
                         <span class="title">人员名称</span>
                         <div class="user-item" v-for="(item,index) in userListArry" :key="index">
                             <el-checkbox v-model="userCheckedOptions" @change="changeUser(item.id)" :label="item.id">{{item.realName}}</el-checkbox>
                         </div>
                     </div>
                 </div>
             </el-form-item>
             
             <el-form-item label="备注">
                 <el-input v-model.trim="form.remark" type="textarea" placeholder="请输入" autocomplete="off"></el-input>
             </el-form-item>
         </el-form>
 
         <div slot="footer" class="dialog-footer">
             <el-button @click="close">取 消</el-button>
             <el-button type="primary" @click="save" :loading="loading" :disabled="disabled">{{
                     loading ? '确定中 ...' : '确定'
                 }}
             </el-button>
         </div>
         <bumen ref="bumen" @fetch-data=""></bumen>
     </el-dialog>
 </template>
 
 <script>
 import {doEdit,getManageDeptUserTree} from '@/api/groupManagement'
 import bumen from '@/components/bumen'
 import {info, orgTreeList} from "@/api/org";
 import {selectOrgDeptList} from "@/api/dept";
 
 export default {
     name: 'groupManagementEdit',
     components: {bumen},
     data() {
         return {
             loading: false,
             disabled: false,
             form: {
                 id: '',
                 groupName: '',
                 itemList:[
                     {
                         orgId: '',
                         deptId: '',
                         userId: '',
                     }
                 ],
                 remark: '',
             },
             radio: 0,
             rules: {
                 groupName: [
                     {required: true, trigger: 'blur', message: '请输入分组名称'},
                 ],
                 unitId: [
                     {required: true, trigger: 'change', message: '请选择所属单位'},
                 ],
                 deptId: [
                     {required: true, trigger: 'change', message: '请选择所属部门', type:'number'},
                 ],
                 userId: [
                     {required: true, trigger: 'change', message: '请选择人员'},
                 ],
             },
             title: '',
             dialogFormVisible: false,
             options: [],
             treeData: [],
             defaultProps: {
                 children: 'children',
                 label: 'label',
                 // unitData: [],
             },
             unitData: [],
             deptChecked: [],
             userChecked: [],
             dataList: [],
             deptListArry: [],
             userListArry: [],
             deptCheckedOptions: [],
             userCheckedOptions: [],
             checkedKeys: [],
             checkedselect: true
         }
     },
     created() {
         this.userListData();
         this.getTreeData()
     },
     methods: {
         // 单位数据
         getTreeData() {
             orgTreeList().then((res) => {
                 if (res.code == 0) {
                     this.treeData = res.data;
                 }
             })
         },
         /** 部门数据 */
         // nodeClick(node){
         //     this.options = [] ;
         //     this.form.unitId = node.data.id ;
         //     this.form.deptId = '' ;
         //     var queryForm = {
         //         ids: '',
         //         username: '',
         //         orgId: node.data.id,
         //         pageNo: 1,
         //         pageSize: 10000000,
         //     }
         //     selectOrgDeptList(queryForm).then((res) => {
         //         if(res.code == 0){
         //             this.options = res.data.records ;
         //         } else {
         //             this.$message.error("获取部门数据失败, 请检查单位部门数据是否完整");
         //         }
         //     }).catch((err) => {
         //         console.log("error", err)
         //     })
         // },
         showEdit(row) {
             if (!row) {
                 this.title = '添加分组'
             } else {
                 this.title = '编辑分组'
                 this.form = Object.assign({}, row)
                 this.deptCheckedOptions = row.itemList.map(item => item.deptId)
                 this.userCheckedOptions = row.itemList.map(item => item.userId)
                 this.checkedKeys = row.itemList.map(item => item.dept.orgId)
                 this.checkedKeys = Array.from(new Set(this.checkedKeys))
                 console.log("this.checkedKeys", this.checkedKeys)
                 this.$nextTick(() => {
                     this.$refs.tree.setCheckedKeys(this.checkedKeys, true,);
                     // 模仿handleNodeClick()点击树形结构中id为this.checkedKeys数组中的第一个值的节点
                     this.handleNodeClick(this.unitData.find(item => item.id === this.checkedKeys[0]), null, null)
                     
                 })
                 
 
             }
             this.dialogFormVisible = true
 
         },
         close() {
             this.$refs['form'].resetFields();
             this.deptCheckedOptions = [];
             this.userCheckedOptions = [];
             this.checkedKeys = [];
             this.deptListArry = [];
             this.userListArry = [];
             this.form = this.$options.data().form;
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
 
                     // 循环遍历树形结构unitData，然后根据userCheckedOptions数组中的值，对比树形结构中sysUserList数组中的id值，并取其id值、deptId值 、unitId值，然后生成新的数组，放入itemList数组中,并去重
                     let arr = [];
                     this.unitData.forEach(item => {
                         item.deptList.forEach(i => {
                             i.sysUserList.forEach(j => {
                                 if(this.userCheckedOptions.includes(j.id)){
                                     arr.push({
                                         orgId: item.id,
                                         deptId: i.id,
                                         userId: j.id
                                     })
                                 }
                             })
                         })
                     })
                     this.form.itemList = Array.from(new Set(arr))
 
                     const {msg} = await doEdit(this.form)
                     this.$baseMessage(msg, 'success')
                     this.$emit('fetch-data')
                     this.close()
                 } else {
                     return false
                 }
             })
         },
        
         async userListData() {
             const {data} = await getManageDeptUserTree();
             console.log("管理员", data)
             this.unitData = data;
         },
 
         // 选择会触发checkGroupNode方法
         checkGroupNode(checkedNodes, checkedStatus) {
             console.log("checkedselect", this.checkedselect)
             if (this.checkedselect) {
                 if(checkedStatus){
                     this.deptListArry = checkedNodes.deptList;
                 
                     // 循环checkedNodes.deptList数组 并且把每项id放入deptCheckedOptions数组中
                     var deptListN = checkedNodes.deptList;
                     let arr = [];
                     deptListN.forEach(item => {
                        // 如果deptListN中的userList数组为空，则不添加到deptCheckedOptions数组中
                        if(item.sysUserList.length > 0){
                            arr.push(item.id)
                        }
                     });
                     // this.deptCheckedOptions 数组中 追加 arr数组，并去重
                     this.deptCheckedOptions = Array.from(new Set(this.deptCheckedOptions.concat(arr)));
 
                     // 循环checkedNodes.sysUserList数组 并且把每项id放入userCheckedOptions数组中
                     let sysUserListN = [];
                     checkedNodes.deptList.forEach(item => {
                         sysUserListN = sysUserListN.concat(item.sysUserList)
                     })
                     // 去重
                     this.userListArry = Array.from(new Set(sysUserListN))
 
                     let arr2 = [];
                     sysUserListN.forEach(item => {
                         arr2.push(item.id)
                     })
                 
                     this.userCheckedOptions = Array.from(new Set(this.userCheckedOptions.concat(arr2)))
                 }else{
                     // 去除deptCheckedOptions、userCheckedOptions数组中的id
                     checkedNodes.deptList.forEach(item => {
                         this.deptCheckedOptions = this.deptCheckedOptions.filter(i => i !== item.id)
                     })
                     checkedNodes.deptList.forEach(item => {
                         item.sysUserList.forEach(i => {
                             this.userCheckedOptions = this.userCheckedOptions.filter(j => j !== i.id)
                         })
                     })
 
                 }
             }
             
 
             // this.$refs.tree.setCheckedKeys(checkedKeys.checkedKeys)
         },
         // 点击节点
         handleNodeClick(data, node, instance) {
             console.log("data", data)
             console.log("node", node)
             console.log("instance", instance)
 
             this.deptListArry = data.deptList
 
             // this.userListArry 为当前点击部门的人员
             let sysUserListN = [];
             data.deptList.forEach(item => {
                 sysUserListN = sysUserListN.concat(item.sysUserList)
             })
             // 去重
             this.userListArry = Array.from(new Set(sysUserListN))
 
             
             // this.$refs.tree.setCheckedKeys(data.deptList)
         },
         // 点击部门 获取人员 并选择
         toShowUser(id) {
             this.checkedselect = false
             this.userListArry = this.deptListArry.find(item => item.id === id).sysUserList
 
             // 如果状态为true 说明是选中状态 为false 说明是取消选中状态
             if(this.deptCheckedOptions.includes(id)){
                 // 如果选中状态
                 let arr2 = [];
                 this.userListArry.forEach(item => {
                     arr2.push(item.id)
                 })
                 this.userCheckedOptions = Array.from(new Set(this.userCheckedOptions.concat(arr2)))
             }else{
                 // 如果取消选中状态
                 this.deptCheckedOptions = this.deptCheckedOptions.filter(i => i !== id)
                 this.userListArry.forEach(item => {
                     this.userCheckedOptions = this.userCheckedOptions.filter(j => j !== item.id)
                 })
             }
 

                this.checkedKeys = [];
                this.findDeptIds(this.unitData); // 调用递归函数
 
             
             //阻止tree 的check-change事件
             setTimeout(() => {
                 this.checkedselect = true
             }, 100)
 
         },

        //  递归查询
        findDeptIds(data) {
            var that = this;
            
            data.forEach(function(item) {
                // 检查当前项是否有deptList属性
                if (item.deptList) {
                    item.deptList.forEach(function(department) {
                        // 检查部门ID是否在deptCheckedOptions中
                        if (that.deptCheckedOptions.includes(department.id)) {
                            // 如果匹配，添加分公司ID到arr中
                            that.checkedKeys.push(item.id);
                        }
                    });
                }
                // 检查当前项是否有children属性，如果有则递归调用findDeptIds
                if (item.children) {
                    that.findDeptIds(item.children);
                }
            })
            // 去重
            this.checkedKeys = Array.from(new Set(this.checkedKeys))
            console.log("this.checkedKeys", this.checkedKeys)
            this.$nextTick(() => {
                 this.$refs.tree.setCheckedKeys(this.checkedKeys);
             })
        },
 
         // 点击部门名称 只显示人员
         justToShow(id) {
             this.userListArry = this.deptListArry.find(item => item.id === id).sysUserList
         },
         // 变换人员选择状态 如果全部取消选中状态 部门也取消选中状态
         changeUser(id) {
             this.checkedselect = false
             if(this.userCheckedOptions.includes(id)){
                 //如果选中状态
                 // 循环遍历当前deptListArry中的sysUserList中是否包含id 如果包含 对应部门的id放入deptCheckedOptions数组中
                 this.deptListArry.forEach(item => {
                     if(item.sysUserList.some(i => i.id === id)){
                         this.deptCheckedOptions.push(item.id)
                     }
                 })
             }else{
                 // 如果取消选中状态
                  // 循环this.deptListArry数组,并查看每项的sysUserList数组中是否有id 存在在urlCheckedOptions数组中 如果一个都没有 则取消相应部门的选中状态 如果有一个以上 则保持选中状态
                 this.deptListArry.forEach(item => {
                     let arr = [];
                     item.sysUserList.forEach(i => {
                         arr.push(i.id)
                     })
                     let arr2 = arr.filter(i => this.userCheckedOptions.includes(i))
                     if(arr2.length === 0){
                         this.deptCheckedOptions = this.deptCheckedOptions.filter(i => i !== item.id)
                     }
                 })
                 
             }
 
             //tree树形结构递归遍历其数据unitData，然后根据deptCheckedOptions数组中的值，对比树形结构中deptList中的id值，查找其orgId值，然后生成新的数组，放入treeCheckedOptions数组中
             this.checkedKeys = [];
             this.findDeptIds(this.unitData); // 调用递归函数
             
             //阻止tree 的check-change事件
             setTimeout(() => {
                 this.checkedselect = true
             }, 100)
 
             
            
 
         }
        
     },
 }
 </script>
 <style lang="scss" scoped>
     .group-list{
         display: flex;
         padding: 2px 14px 14px;
         border-radius: 4px;
         border: 1px solid #ebeef5;
         position: relative;
         .sel-user-num{
             position: absolute;
             top: -50px;
             right: 0;
             color: #909399;
             span{
                 color: #409EFF;
             }
         }
         .unit-list{
             flex: 1;
             margin-right: 20px;
             .title{
                 display: block;
                 margin-bottom: 10px;
                 &::after{
                     content: '';
                     display: block;
                     width: 100%;
                     height: 1px;
                     background-color: #ebeef5;
                     margin-bottom: 10px;
                 }
                 &::before{
                     content: '';
                     display: inline-block;
                     margin-right: 8px;
                     margin-top: -2px;
                     vertical-align: middle;
                     width: 4px;
                     height: 14px;
                     background-color: #cbd9f8;
                 }
             }
             .el-tree{
                 height: 300px;
                 overflow: auto;
             }
         }
         .dept-list{
             flex: 1;
             margin-right: 20px;
             .title{
                 display: block;
                 margin-bottom: 10px;
                 position: relative;
                 &::after{
                     content: '';
                     display: block;
                     width: 100%;
                     height: 1px;
                     background-color: #ebeef5;
                     margin-bottom: 10px;
                 }
                 &::before{
                     content: '';
                     display: inline-block;
                     margin-right: 8px;
                     margin-top: -2px;
                     vertical-align: middle;
                     width: 4px;
                     height: 14px;
                     background-color: #cbd9f8;
                 }
             }
             .dept-item{
             }
         }
         .user-list{
             flex: 1;
             .title{
                 display: block;
                 margin-bottom: 10px;
                 &::after{
                     content: '';
                     display: block;
                     width: 100%;
                     height: 1px;
                     background-color: #ebeef5;
                     margin-bottom: 10px;
                 }
                 &::before{
                     content: '';
                     display: inline-block;
                     margin-right: 8px;
                     margin-top: -2px;
                     vertical-align: middle;
                     width: 4px;
                     height: 14px;
                     background-color: #cbd9f8;
                 }
             }
             .user-item{
             }
         }
     }
 </style>