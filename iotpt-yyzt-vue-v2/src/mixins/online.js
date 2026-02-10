export default {
  data() {
    return {
      onlineDict: [
        { dictLabel: '在线', dictValue: 1 },
        { dictLabel: '离线', dictValue: 2 },
        { dictLabel: '异常', dictValue: 3 },
      ],
      onlineDict2: [
        { dictLabel: '在线', dictValue: 1 },
        { dictLabel: '离线', dictValue: 0 },
        { dictLabel: '异常', dictValue: 3 },
      ],
    }
  },
  created() {
    this.$set(this.queryForm, 'onLineStatus', '')
  },
}
