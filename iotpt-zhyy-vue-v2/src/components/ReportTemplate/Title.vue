<template>
    <div>
        <div class="module-sub-title" v-if="this.subIndex > 0">
            {{ this.subIndex }}、{{ this.subTitle }}
        </div>
        <div class="module-item-title">{{ index }}、{{ title }}</div>
    </div>
</template>

<script>
import Common from "./common.js";
export default {
    mixins: [Common]
};
</script>

<style lang="scss" scoped>
.module-item-title {
    font-size: 14px;
    line-height: 22px;
    margin: 6px 0;
}

.module-sub-title {
    font-size: 18px;
    font-weight: bold;
    line-height: 30px;
    margin: 10px 0;
}
</style>
