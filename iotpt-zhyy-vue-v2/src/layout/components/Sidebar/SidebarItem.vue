<template>
  <div v-if="!item.hidden">
    <template
      v-if="
        hasOneShowingChild(item.children, item) &&
        (!onlyOneChild.children || onlyOneChild.noShowingChildren) &&
        !item.alwaysShow
      "
    >
      <app-link v-if="onlyOneChild.meta" :to="resolvePath(onlyOneChild.path)">
        <el-menu-item
          :index="resolvePath(onlyOneChild.path)"
          :class="{ 'submenu-title-noDropdown': !isNest }"
          class="custom-style"
        >
          <div class="left-border"></div>
          <item
            :icon="onlyOneChild.meta.icon || (item.meta && item.meta.icon)"
            :title="onlyOneChild.meta.title"
          />
        </el-menu-item>
      </app-link>
    </template>

    <el-menu-item
      v-else
      ref="subMenu"
      :index="resolvePath(item.path)"
      popper-append-to-body
      @click.native.stop="meunClick(item)"
      @mouseenter.native.stop="hoverShow(item)"
      style="position: relative"
      class="custom-style"
    >
      <div class="left-border"></div>
      <template slot="title">
        <item
          v-if="item.meta"
          :icon="item.meta && item.meta.icon"
          :title="item.meta.title"
        />
        <i class="el-icon-arrow-right title-right"></i>
      </template>
    </el-menu-item>
    <transition name="slide-fade">
      <slot></slot>
    </transition>
  </div>
</template>

<script>
/* eslint-disable */
import path from "path";
import { isExternal } from "@/utils/validate";
import Item from "./Item";
import AppLink from "./Link";
import FixiOSBug from "./FixiOSBug";

export default {
  name: "SidebarItem",
  components: { Item, AppLink },
  mixins: [FixiOSBug],
  props: {
    // route object
    item: {
      type: Object,
      required: true,
    },
    isNest: {
      type: Boolean,
      default: false,
    },
    basePath: {
      type: String,
      default: "",
    },
  },
  data() {
    // To fix https://github.com/PanJiaChen/vue-admin-template/issues/237
    // TODO: refactor with render function
    this.onlyOneChild = null;
    return {
      isMenuClick: false,
    };
  },
  methods: {
    hoverShow(route) {
      this.meunClick(route);
    },
    meunClick(route) {
      this.$emit("meunClick", route);
    },
    hasOneShowingChild(children = [], parent) {
      const showingChildren = children.filter((item) => {
        if (item.hidden) {
          return false;
        } else {
          // Temp set(will be used if only has one showing child)
          this.onlyOneChild = item;
          return true;
        }
      });

      // When there is only one child router, the child router is displayed by default
      if (showingChildren.length === 1) {
        return true;
      }

      // Show parent if there are no child router to display
      if (showingChildren.length === 0) {
        this.onlyOneChild = { ...parent, path: "", noShowingChildren: true };
        return true;
      }

      return false;
    },
    resolvePath(routePath) {
      if (isExternal(routePath)) {
        return routePath;
      }
      if (isExternal(this.basePath)) {
        return this.basePath;
      }
      return path.resolve(this.basePath, routePath);
    },
  },
};
</script>

<style lang="scss" scoped>
.title-right {
  position: absolute;
  right: 3%;
  top: 50%;
  // margin-left:15px;
  // margin-top:15px;
  transform: translateY(-50%);
  color: #fff;
}
.custom-style {
  position: relative;
  height: 45px;
  line-height: 45px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  overflow: hidden;
  padding: 0 10px 0 15px !important;

  .left-border {
    transition: 1s display ease-in-out;
    display: none;
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    width: 5px;
    background-color: #fff;
  }

  &:hover {
    .left-border {
      display: block;
    }
    // border-left: 5px solid #fff;
    background-color: rgba(255, 255, 255, 0.1) !important;
  }

  &.is-active {
    .left-border {
      display: block;
    }
    // border-left: 5px solid #fff;
    background-color: rgba(255, 255, 255, 0.1) !important;
  }
}

.slide-fade-enter-active {
  transition: all 0.5s ease;
}
.slide-fade-leave-active {
  transition: all 0.5s cubic-bezier(1, 0.5, 0.8, 1);
}
.slide-fade-enter,
.slide-fade-leave-to {
  transform: translateX(-100px);
  opacity: 0;
}
</style>

