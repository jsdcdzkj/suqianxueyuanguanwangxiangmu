import flvjs from "flv.js";
import VideoJs from "video.js";
export const VideoPlayer = {
    data() {
        return {
            dialogPlayer: {
                // 视频实例
                video: null,
                // 下标
                index: -1,
                //  视频加载中
                loading: false
            },
            // 默认显示dialog
            videoDialogShow: true,
            callback: null
        };
    },
    methods: {
        onDialogClose() {
            this.videoDialogShow = false;
           
            if (this.dialogPlayer.video) {
                
                this.dialogPlayer.video.unload&& this.dialogPlayer.video.unload()
                this.dialogPlayer.video.dispose && this.dialogPlayer.video.dispose();
                this.dialogPlayer.video.destroy &&  this.dialogPlayer.video.destroy();
            }
            this.callback && this.callback();
        },
        /**
         * 创建m3u8格式视频播放对象
         * @param {String} videoUrl 后端提供的地址
         * @param {String} videoId
         * @returns {Promise<Player|null>} 视频实例
         */
        async createVideoJsPlayer(videoUrl, videoId) {
            if (!videoUrl || !videoId) return null;
            try {
                const player = VideoJs(videoId, {
                    autoplay: true, // 设置自动播放
                    muted: true, // 设置了它为true，才可实现自动播放,同时视频也被静音 （Chrome66及以上版本，禁止音视频的自动播放）
                    preload: "auto", // 预加载
                    controls: false // 显示播放的控件
                });
               
                player.src([
                    {
                        src: videoUrl,
                        // type: "application/x-mpegURL"
                    }
                ]);
                await player.play();
                return player;
            } catch (e) {
                return null;
            }
        },

        /**
         * 创建Flv格式视频播放对象
         * @param {String} videoUrl 后端提供的地址
         * @param {String} videoId
         * @returns {Promise<FlvJs.Player|null>} 视频实例
         */
        async createFlvVideoPlayer(videoUrl, videoId) {
            if (!videoUrl || !videoId) return null;
            if (flvjs.isSupported()) {
                const videoElement = document.getElementById(videoId);
                console.log(videoElement);
                if (!videoElement) return null;
                const [url, token] = videoUrl.split(",");
                const playUrl = token ? `${url}?token=${token.replace("bearer ", "")}` : url;
                try {
                    let flvPlayer = flvjs.createPlayer(
                        {
                            type: "flv",
                            isLive: true,
                            hasAudio: false,
                            url: playUrl
                        },
                        {
                            enableWorker: false, //不启用分离线程
                            enableStashBuffer: false, //关闭IO隐藏缓冲区
                            reuseRedirectedURL: true, //重用301/302重定向url，用于随后的请求，如查找、重新连接等。
                            autoCleanupSourceBuffer: true //自动清除缓存
                        }
                    );


                    // 绑定到视频节点
                    flvPlayer.attachMediaElement(videoElement);
                    // 加载视频
                    flvPlayer.load();
                    await flvPlayer.play();
                    return flvPlayer;
                } catch (e) {
                    return null;
                }
            } else {
                return null;
            }
        }
    }
};
