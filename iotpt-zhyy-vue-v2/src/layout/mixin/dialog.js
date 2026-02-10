export default {
  props: {
    show: {
      type: Boolean,
      default: false,
    },
  },
  methods: {
    handlerCancel() {
      this.$emit("update:show", false);
    },
    handlerConfirm() {
      this.$emit("update:show", false);
    },
    beforeClose() {
      this.$emit("update:show", false);
    },
  },
};
