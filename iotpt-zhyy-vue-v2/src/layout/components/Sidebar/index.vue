<template>
  <div :class="{ 'has-logo': showLogo }">
    <logo v-if="showLogo" :collapse="isCollapse" />
    <!-- <el-scrollbar wrap-class="scrollbar-wrapper"> -->
    <el-menu :default-active="activeMenu" :background-color="variables.menuBg" :text-color="variables.menuText"
      :unique-opened="true" :active-text-color="variables.menuText" :collapse-transition="false" mode="vertical"
      :default-openeds="subMenuActive" ref="menu">
      <!--
      :collapse="isCollapse"
     -->
      <sidebar-item v-for="route, index in routes" :key="index" :item="route" :base-path="route.path"
        @meunClick="meunClick" :class="{ 'menu-item-side': allWidth < 2000 && routes.length > 12 }">
        <transition name="slide-fade">
          <div class="child-sidebar--container" v-if="isMenuClick && route.isShowChild">
            <div class="child-sidebar">
              <el-menu-item v-for="child in route.children" :key="child.path" :is-nest="true" :item="child"
                :base-path="resolvePath(child.path)" class="nest-menu"
                :class="{ 'has-isactive': child.name === $route.name }" ref="subMenu" :index="resolvePath(child.path)"
                popper-append-to-body @click.native.stop="handleClickToRoute(child)">
                <template slot="title">
                  <Item v-if="child.meta" :icon="child.meta && child.meta.icon" :title="child.meta.title" />
                </template>
              </el-menu-item>
            </div>
          </div>
        </transition>
      </sidebar-item>
    </el-menu>

    <div class="hamb">
      <hamburger :is-active="sidebar.opened" class="hamburger-container" :isBot="true" @toggleClick="toggleSideBar" />
    </div>

    <!-- </el-scrollbar> -->
  </div>
</template>

<script>
import path from "path";
import { isExternal } from "@/utils/validate";
import { mapGetters } from "vuex";
import Hamburger from "@/components/Hamburger";

import Logo from "./Logo";
import SidebarItem from "./SidebarItem";
import Item from "./Item";

import variables from "@/styles/variables.scss";

export default {
  components: { SidebarItem, Logo, Item, Hamburger },

  computed: {
    ...mapGetters(["sidebar", "routesData"]),
    routes: {
      get() {
        return this.routesData.map((item) => {
          item.isShowChild = false;
          return item;
        });
      }
    },
    isHasActive() {
      return this.route;
    },
    activeMenu() {
      const route = this.$route;
      const { meta, path } = route;
      // if set path, the sidebar will highlight the path you set
      if (meta.activeMenu) {
        return meta.activeMenu;
      }
      return path;
    },
    showLogo() {
      return this.$store.state.settings.sidebarLogo;
    },
    variables() {
      return variables;
    },
    isCollapse() {
      return !this.sidebar.opened;
    },
  },
  data() {
    return {
      isMenuClick: false,
      basePath: "",
      subMenuActive: [],
      allWidth: 1920
    };
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch("app/toggleSideBar");
    },
    handleClickToRoute(path) {
      this.$router.push({ name: path.name });
      this.isMenuClick = false
    },
    closeMenu() {
      let eleList = window.document.getElementsByClassName("main-container")[0]
      eleList.click();
    },
    meunClick(item) {
      this.isMenuClick = false;
      this.routes = this.routes.map((item) => {
        item.isShowChild = false;
        return item;
      });
      if (item.children.length === 0) return;
      this.$set(item, "isShowChild", true);
      this.isMenuClick = true;
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
  mounted() {
    this.handleWindowClick = () => {
      this.isMenuClick = false;
    };
    window.addEventListener("click", this.handleWindowClick);
    this.allWidth = window.innerWidth;
  },
  beforeDestroy() {
    window.removeEventListener("click", this.handleWindowClick);
  },
};
</script>

<style lang="scss" scoped>
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

::v-deep .child-sidebar--container {
  position: relative;
  height: 100%;

  .child-sidebar {
    min-width: 166px;
    position: absolute;
    top: -45px;
    right: -170px;
    z-index: 100000;
    background-color: #27365e;
    border: 3px solid #27365e;
    transition: 0.3s;
    box-shadow: 0 10px 12px 0 rgba(0, 0, 0, 0.1);
  }
}

.menu-item-side {
  &:nth-last-child(1) {
    .child-sidebar {
      bottom: 0;
      top: auto;
    }
  }

  &:nth-last-child(2) {
    .child-sidebar {
      bottom: 0;
      top: auto;
    }
  }
}

.hamb {
  cursor: pointer;
  position: fixed;
  bottom: 39px;
}

.nest-menu {
  min-width: 166px;
  height: 45px;
  display: flex;
  align-items: center;
  color: #fff;

  &.has-isactive {
    background-color: rgba(255, 255, 255, 0.2) !important;
  }

  &:hover {
    background-color: rgba(255, 255, 255, 0.2) !important;
  }
}

.flex {
  display: flex;
  align-items: center;
  justify-content: center;

  i {
    font-size: 14px;
    color: #28355f;
    margin-right: 3px;
    font-weight: 700;
  }

  span {
    font-weight: 700;
  }
}
</style>
