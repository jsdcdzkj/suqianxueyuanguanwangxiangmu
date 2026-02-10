<template>
    <div class="banner">

        <swiper :options="swiperOption" ref="mySwiper">
            <swiper-slide>
                <video autoplay="" loop="" muted="" class="custom-video" poster="" style="width: 100%">
                    <source :src="videourl" type="video/mp4">
                </video>
            </swiper-slide>
            <swiper-slide>
                <img :src="loginBg" />
            </swiper-slide>

            <div class="swiper-pagination" slot="pagination"></div>
            <!-- 箭头 -->
            <!-- <div class="swiper-button-prev" slot="button-prev"></div>
            <div class="swiper-button-next" slot="button-next"></div> -->
        </swiper>


    </div>
</template>
<script>
let loginBg = require('@/assets/img/indexbg.jpg')
// let videourl = require('@/assets/img/sp.mp4')
// 引入swiper插件
import { swiper, swiperSlide } from "vue-awesome-swiper";
// import "vue-awesome-swiper/dist/css/swiper.css";
export default {
    name: 'bgBanner',
    props: {
        swiperList: Array, // 注意此处我的数据是由接口返回的数据
    },
    components: {
        swiper,
        swiperSlide,
    },
    data() {
        return {
            loginBg,
            videourl:'https://smartpark.dincher.cn/video/yysp.mp4',
            swiperOption: {
                // loop: true,

                // autoplay: {

                //   delay: 5000,
                //   stopOnLastSlide: false,
                //   disableOnInteraction: false,
                // },
                // autoplay: false,
                // 显示分页
                pagination: {
                    el: ".swiper-pagination",
                    paginationClickable: true,
                    autoplayDisableOnInteraction: false,
                    clickable: true, //允许分页点击跳转
                    renderBullet: function (index, className) {
                        return '<span class="' + className + '">' + (index + 1) + "</span>";
                    },
                },
                // 设置点击箭头
                // navigation: {
                //   nextEl: ".swiper-button-next",
                //   prevEl: ".swiper-button-prev"
                // }
            },
        }
    },


}
</script>

<style lang="scss" scoped>

.banner {
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: 100%;
  z-index: 0;
  overflow: hidden;

  .swiper-container {
    height: 100%;
    width: 100%;

    .swiper-pagination {
        text-align:left;
        padding-left:50px;
        bottom:50px;
      ::v-deep{
        .swiper-pagination-bullet {
        width: 20px!important;
        height: 20px!important;
        text-align: center!important;
        line-height: 20px!important;
        font-size: 12px!important;
        color: #fff!important;
        opacity: 1!important;
        background: rgba(0, 0, 0, 0.2);
        border-radius:20px;
        display:inline-block;
      }
      .swiper-pagination-bullet-active{
        background: #ff7d01;
      }
      }

    }


    img {
      height: 100%;
      width: 100%;
      object-fit: cover;
    }

    video {
      min-width: 100%;
      min-height: 100%;
      position: absolute;
      left: 50%;
      top: 50%;
      transform: translate(-50%, -50%);
      object-fit: cover;
    }
  }
}
</style>
