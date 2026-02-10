<template>
  <div
    class="child-sidebar--container"
    v-show="isMenuClick && route.isShowChild"
  >
    <div class="child-sidebar">
      <el-menu-item
        v-for="child in route.children"
        :key="child.path"
        :is-nest="true"
        :item="child"
        :base-path="resolvePath(child.path)"
        class="nest-menu"
        :class="{ 'has-isactive': child.name === $route.name }"
        ref="subMenu"
        :index="resolvePath(child.path)"
        popper-append-to-body
        @click.native.stop="handleClickToRoute(child)"
      >
        <template slot="title">
          <div class="flex">
            <i class="el-icon-edit"></i>
            <Item
              v-if="child.meta"
              :icon="child.meta && child.meta.icon"
              :title="child.meta.title"
            />
          </div>
        </template>
      </el-menu-item>
    </div>
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
    ...mapGetters(["sidebar"]),
    routes() {
      return this.$router.options.routes.map((item) => {
        item.isShowChild = false;
        return item;
      });
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
    variables() {
      return variables;
    },
    isCollapse() {
      return !this.sidebar.opened;
    },
  },
  created() {
    this.routes1 = [...this.routes];
  },
  data() {
    return {
      isMenuClick: false,
      basePath: "",
    };
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch("app/toggleSideBar");
    },
    handleClickToRoute(path) {
      this.$router.push({ name: path.name });
    },
    meunClick(item) {
      this.isMenuClick = false;
      this.routes1 = this.routes1.map((item) => {
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
  },
  beforeDestroy() {
    window.removeEventListener("click", this.handleWindowClick);
  },
};
</script>

<style lang="scss" scoped>
::v-deep .child-sidebar--container {
  position: relative;
  height: 100%;
  .child-sidebar {
    width: 80%;
    position: absolute;
    top: -50px;
    right: -160px;
    z-index: 100000;
    // border: 1px solid #28355f;
    // border-color: #28355f;
    // border-style: solid;
    // border-top-width: 1px;
    // border-bottom-width: 1px;
    // border-left-width: 1px;
    // border-right-width: 1px;
    // border-radius: 5px;
    transition: 0.3s;
    box-shadow: 0 10px 12px 0 rgba(0, 0, 0, 0.1);
  }
}

.sidebar-container a {
  padding: 0 20px;
}

.hamb {
  position: fixed;
  bottom: 39px;
}

.nest-menu {
  background-color: #fff !important;
  color: #28355f !important;
  height: 37px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 30px !important;
  border-left: 2px solid #28355f;
  border-right: 2px solid #28355f;
  border-bottom: 2px solid rgba(40, 53, 95, 0.6);
  &:first-child {
    border-top: 2px solid rgb(40, 53, 95);
    border-radius: 8px 8px 0 0px;
  }
  &:last-child {
    border-bottom: 2px solid #28355f;
    border-radius: 0px 0px 8px 8px;
  }
  &.has-isactive {
    background-color: #28355f !important;
    color: #ffff !important;
    i {
      color: #fff;
    }
  }
  &:hover {
    background-color: #28355f !important;
    color: #ffff !important;
    i {
      color: #fff;
    }
  }
  svg {
    display: none;
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

