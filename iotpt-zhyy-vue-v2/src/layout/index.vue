<template>
    <div :class="classObj" class="app-wrapper">
        <div v-if="device === 'mobile' && sidebar.opened" class="drawer-bg aaa" @click="handleClickOutside" />
        <div class="app-header__link">
            <AppHeader></AppHeader>
        </div>
        <sidebar class="sidebar-container" id="sidebar-container" ref="sidebar" />
        <div class="main-container">
            <div :class="{ 'fixed-header': fixedHeader }">
                <navbar />
            </div>
            <div class="index-container">
                <app-main />
            </div>
        </div>
    </div>
</template>

<script>
/* eslint-disable */
import AppHeader from "@/appHeader";
import { Navbar, Sidebar, AppMain } from "./components";
import ResizeMixin from "./mixin/ResizeHandler";
export default {
    name: "Layout",
    components: { Navbar, Sidebar, AppMain, AppHeader },
    mixins: [ResizeMixin],
    computed: {
        sidebar() {
            return this.$store.state.app.sidebar;
        },
        device() {
            return this.$store.state.app.device;
        },
        fixedHeader() {
            return this.$store.state.settings.fixedHeader;
        },
        classObj() {
            return {
                hideSidebar: !this.sidebar.opened,
                openSidebar: this.sidebar.opened,
                withoutAnimation: this.sidebar.withoutAnimation,
                mobile: this.device === "mobile"
            };
        }
    },
    mounted() {
        // sidebar-container
        this.$nextTick(() => {
            window.document.getElementById("sidebar-container").addEventListener("mouseleave", () => {
                this.$refs.sidebar.closeMenu();
            });
        });
    },
    methods: {
        handleClickOutside() {
            this.$store.dispatch("app/closeSideBar", { withoutAnimation: false });
        }
    }
};
</script>

<style lang="scss" scoped>
@import "~@/styles/mixin.scss";
@import "~@/styles/variables.scss";

.app-wrapper {
    @include clearfix;
    position: relative;
    height: 100%;
    width: 100%;
    &.mobile.openSidebar {
        position: fixed;
        top: 0;
    }

    &.hideSidebar {
        ::v-deep .title-right {
            right: -10px;
            color: #fff;
            display: none;
        }
    }
}
.main-container {
    padding: 0px;
    background: #f5f8ff;
    .index-container {
        margin: 15px;
        margin-bottom: 0;
        background: transparent;
        padding: 0px;
        box-sizing: border-box;
        // min-height:calc(100% - 80px);
    }
}

.drawer-bg {
    background: #000;
    opacity: 0.3;
    width: 100%;
    top: 0;
    height: 100%;
    position: absolute;
    z-index: 999;
}

.fixed-header {
    position: fixed;
    top: 0;
    right: 0;
    z-index: 9;
    width: calc(100% - #{$sideBarWidth});
    transition: width 0.28s;
}

.hideSidebar .fixed-header {
    width: calc(100% - 50px);
}

.mobile .fixed-header {
    width: 100%;
}
</style>
