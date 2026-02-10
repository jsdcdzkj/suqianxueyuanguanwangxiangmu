<template>
<!--    <div id="app">-->
<!--       -->
<!--    </div>-->
    <div id="vue-admin-beautiful">
<!--        <router-view/>-->
        <router-view v-if="isRouterAlive"/>
    </div>
</template>

<script>
export default {
    name: 'App',
    mounted() {
    },
    provide() {
        return {
            reload: this.reload
        }
    },
    data() {
        return {
            isRouterAlive: true
        }
    },
    methods: {
        reload() {
            this.isRouterAlive = false
            this.$nextTick(function () {
                this.isRouterAlive = true
            })
        }
    }
}
</script>
<style>
body {
    padding-right: 0 !important;
}
</style>
