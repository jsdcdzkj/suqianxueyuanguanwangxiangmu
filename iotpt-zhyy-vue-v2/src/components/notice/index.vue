<template>
  <div class="uniform-marquee" @mouseenter="pause" @mouseleave="resume">
    <div class="notice-header">
      <i class="el-icon-megaphone"></i>
      <span class="title">{{ title }}</span>
    </div>
    <div class="marquee-wrapper" ref="wrapper">
      <div class="marquee-content" ref="content" :style="{
        'animation-duration': animationDuration + 's',
        'animation-play-state': isPaused ? 'paused' : 'running'
      }">
        {{ notice }}&nbsp;&nbsp;&nbsp;&nbsp;{{ notice }}
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'UniformSpeedMarquee',
  props: {
    title: {
      type: String,
      default: '公告'
    },
    notice: {
      type: String,
      required: true
    },
    // 文字移动速度（像素/秒）
    speed: {
      type: Number,
      default: 60
    },
    hoverPause: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      isPaused: false,
      animationDuration: 10,
      observer: null
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initMarquee();
      // 添加ResizeObserver监听容器大小变化
      this.observer = new ResizeObserver(this.handleResize);
      this.observer.observe(this.$refs.wrapper);
    });
  },
  methods: {
    initMarquee() {
      const wrapper = this.$refs.wrapper;
      const content = this.$refs.content;

      // 计算内容总宽度（双份内容）
      const contentWidth = content.scrollWidth / 2;
      const wrapperWidth = wrapper.offsetWidth;

      // 只有当内容宽度大于容器宽度时才启用滚动
      if (contentWidth > wrapperWidth) {
        // 计算动画持续时间 = 内容宽度 / 速度
        this.animationDuration = contentWidth / this.speed;
      } else {
        // 内容不足时不滚动
        content.style.animation = 'none';
      }
    },
    pause() {
      if (this.hoverPause) {
        this.isPaused = true;
      }
    },
    resume() {
      this.isPaused = false;
    },
    handleResize() {
      this.initMarquee();
    }
  },
  beforeDestroy() {
    if (this.observer) {
      this.observer.disconnect();
    }
  },
  watch: {
    notice() {
      this.$nextTick(this.initMarquee);
    }
  }
}
</script>

<style scoped>
.uniform-marquee {
  display: flex;
  align-items: center;
  border-radius: 4px;
  box-sizing: border-box;
  overflow: hidden;
}

.notice-header {
  display: flex;
  align-items: center;
  margin-right: 15px;
  color: rgba(255, 255, 255, 0.85);
  white-space: nowrap;
  flex-shrink: 0;
}

.notice-header .title {
  margin-left: 5px;
  font-weight: bold;
}

.marquee-wrapper {
  flex: 1;
  overflow: hidden;
  white-space: nowrap;
}

.marquee-content {
  display: inline-block;
  white-space: nowrap;
  animation: marquee linear infinite;
}

@keyframes marquee {
  0% {
    transform: translateX(0);
  }

  100% {
    transform: translateX(-50%);
  }
}
</style>