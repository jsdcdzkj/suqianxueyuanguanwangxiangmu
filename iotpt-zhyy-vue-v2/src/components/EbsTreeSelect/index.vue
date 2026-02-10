<template>
  <el-select class="ebs-class" ref="singleTree"   :value="value" :disabled="disabled" :clearable="clearable" @clear="clearHandle" :placeholder="placeholder">
    <el-option :value="value" :label="label">
      <el-tree id="tree-option" ref="selectTree" 
      :expand-on-click-node="false"  
      :data="options" 
      :highlight-current="true" 
      :props="props" 
      :show-checkbox="selectType == 'multiple'" 
      node-key="id" 
      @check="checkNode" 
      :current-node-key="currentNodeKey" 
      :default-checked-keys="defaultSelectedKeys" 
      :default-expanded-keys="defaultSelectedKeys"
      @node-click="handleNodeClick">
    </el-tree>
      
      <!-- <el-tree id="tree-option" ref="selectTree" :accordion="accordion" :data="options" :props="props" :node-key="props.value" :expand-on-click-node="false" :default-expanded-keys="defaultExpandedKey" @node-click="handleNodeClick">
        <template slot-scope="{ node, data }">
          <div class="tree-node" @click.stop="">
            <div class="tree-node__arrow" @click.stop="handleNodeExpand(node)">
            </div>
            <div class="tree-node__label" @click.stop="handleNodeClick(node, data)">
              {{ data[props.label] }}
            </div>
          </div>
        </template>
      </el-tree> -->
    </el-option>
  </el-select>
</template>

<script>
export default {
  name: 'ebs-tree-select',
  props: {
    /* 配置项 */
    props: {
      type: Object,
      default: () => {
        return {
          value: 'id', // ID字段名
          label: 'label', // 显示名称
          children: 'children', // 子级字段名
        }
      },
    },
    /* 选项列表数据(树形结构的对象数组) */
    options: {
      type: Array,
      default: () => {
        return []
      },
    },
    /* 初始值 */
    value: {
      type:  [String, Number, Array],
      default: () => {
        return null
      },
    },
    /* 提示词 */
    placeholder: {
      type: String,
      default: () => {
        return '请选择'
      },
    },
    /* 自动收起 */
    accordion: {
      type: Boolean,
      default: () => {
        return false
      },
    },
    /* 单选/多选 */
    selectType: {
      type: String,
      default: () => {
        return 'single'
      },
    },
    
    /* 初始选中值key */
    selectedKey: {
      type: String,
      default: () => {
        return ''
      },
    },
    /* 初始选中值name */
    selectedValue: {
      type: String,
      default: () => {
        return ''
      },
    },
    /* 可做选择的层级 */
    selectLevel: {
      type: [String, Number],
      default: () => {
        return ''
      },
    },
    /* 可清空选项 */
    clearable: {
      type: Boolean,
      default: () => {
        return true
      },
    },
    disabled: {
      type: Boolean,
      default: () => {
        return false
      },
    },
  },
  data() {
    return {
      valueId: this.value, // 初始值
      valueTitle: '',
      label:'',
      defaultExpandedKey: [],
      currentNodeKey: this.selectedKey,
      defaultSelectedKeys: [], //初始选中值数组
    }
  },
  mounted() {
    this.initHandle()
  },
  methods: {
    // 初始化值
    initHandle() {

      // 这里要延迟执行，否则有BUG
      setTimeout( () => {
        if (this.valueId) {
          if (this.$refs.selectTree.getNode(this.valueId)) {
          
            this.label = this.$refs.selectTree.getNode(this.valueId).data[
              this.props.label
            ] // 初始化显示

            this.$refs.selectTree.setCurrentKey(this.valueId+'') // 设置默认选中
            this.defaultExpandedKey = [this.valueId+''] // 设置默认展开
          }
        } else {
          this.label = null // 初始化显示
          this.$refs.selectTree.setCurrentKey(null) // 设置默认选中
        }
      }, 200)

      this.$nextTick(() => {
        let scrollWrap = document.querySelectorAll(
          '.el-scrollbar .el-select-dropdown__wrap'
        )[0]
        let scrollBar = document.querySelectorAll(
          '.el-scrollbar .el-scrollbar__bar'
        )
        scrollWrap.style.cssText =
          'margin: 0px; max-height: none; overflow: hidden;'
        scrollBar.forEach((ele) => (ele.style.width = 0))
      })
    },
    // 切换选项
    handleNodeClick(node, data) {
      const checkedNodes = this.getDate(node)
      this.label = checkedNodes.data[this.props.labe]
      this.valueId = node[this.props.value]
      this.$emit('input', this.valueId + '')
      this.defaultExpandedKey = []

      this.$emit('node-click', checkedNodes)
      this.$refs.singleTree.blur()
    },
    // 获取节点数据
    getDate(node) {
      if(Object.hasOwnProperty.call(node,'data')) {
        return node
      } else {
        node.data = node
        return node
      }
    },
     // 节点选中操作
    checkNode(data, node, el) {
      // const checkedNodes = this.$refs.treeOption.getCheckedNodes()
      // const keyArr = []
      // const valueArr = []
      // checkedNodes.forEach((item) => {
      //   if (item.rank >= this.selectLevel) {
      //     keyArr.push(item.id)
      //     valueArr.push(item.name)
      //   }
      // })
      // this.selectValue = valueArr
      // this.selectKey = keyArr
    },
    handleNodeExpand(node) {
      if (node.children && node.children.length) {
        node.expanded = !node.expanded
      }
    },
    // 清除选中
    clearHandle() {
      this.label = ''
      this.valueId = null
      this.defaultExpandedKey = []
      this.clearSelected()
      this.$emit('input', null)
      this.valueId = this.value
    },
    /* 清空选中样式 */
    clearSelected() {
      let allNode = document.querySelectorAll('#tree-option .el-tree-node')
      allNode.forEach((element) => element.classList.remove('is-current'))
    },
  },
  watch: {
    value() {
      this.valueId = this.value + ''
      this.initHandle()
    },
  },
}
</script>
<style>
.ebs-class {
  width: 100%;
}
.tree-node {
  display: flex;
  align-items: center;
  padding: 5px;
}

.tree-node__arrow {
  margin-right: 5px;
  cursor: pointer;
}

.tree-node__arrow .el-icon-arrow-right.is-rotate {
  transform: rotate(90deg);
}
</style>
<style scoped>
.el-scrollbar .el-scrollbar__view .el-select-dropdown__item {
  height: auto;
  max-height: 274px;
  padding: 0;
  overflow: hidden;
  overflow-y: auto;
}
.el-select-dropdown__item.selected {
  font-weight: normal;
}
ul li >>> .el-tree .el-tree-node__content {
  height: auto;
  padding: 0 20px;
}
.el-tree-node__label {
  font-weight: normal;
}
.el-tree >>> .is-current .el-tree-node__label {
  color: #409eff;
  font-weight: 700;
}
.el-tree >>> .is-current .el-tree-node__children .el-tree-node__label {
  color: #606266;
  font-weight: normal;
}
</style>