<template lang="">
    <div>
        <el-time-picker
            v-model="value1"
            :picker-options="{
                selectableRange: '18:30:00 - 20:30:00'
            }"
            placeholder="任意时间点"
        ></el-time-picker>
    </div>
</template>
<script>
export default {
    props: {
        timer: {
            type: Array,
            default: () => []
        },
        selectableRange: {
            type: String,
            default: "00:00:00"
        },
        format: {
            type: String,
            default: ""
        }
    }
};
</script>
<style lang=""></style>
