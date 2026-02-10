<template>
    <video  id="videoPlayer" class="video" muted />
  </template>
  
  <script>
  import Videojs from 'video.js'
  
  export default {
    props:{
        url: {
            type: String,
            default:''
        }
    },
    data() {
      return {
        player: null
      }
    },
    beforeDestroy() {
      if (this.player) {
        this.player.dispose() // Removing Players,该方法会重置videojs的内部状态并移除dom
      }
    },
    activated() {
      if (this.player) {
        this.player.play()
      }
    },
    deactivated() {
      if (this.player) {
        this.player.pause()
      }
    },
    mounted() {
    //   this.initVideo()
    },
    methods: {
      initVideo(url) {
        if (!this.player) {
          this.player = Videojs('videoPlayer', {
            autoplay: true, // 设置自动播放
            muted: true, // 设置了它为true，才可实现自动播放,同时视频也被静音 （Chrome66及以上版本，禁止音视频的自动播放）
            preload: 'auto', // 预加载
            controls: false // 显示播放的控件
          })
        }
        const [urltemp, token] = url?.split(",");
        this.player.src([{
          src: `${urltemp}?token=${token.replace("bearer ", "")}`,
          type: 'application/x-mpegURL' // 告诉videojs,这是一个hls流
        }])
      }
    }
  }
  </script>
  
  <style lang="scss" scoped>
  .video, video {
    width: 100%;
    height: 100%;
  }

  .no-player {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 1px solid #ccc;
  }
  ::v-deep .vjs-loading-spinner {
    position: relative;
    .vjs-control-text {
      opacity: 0;
    }
  }
  </style>