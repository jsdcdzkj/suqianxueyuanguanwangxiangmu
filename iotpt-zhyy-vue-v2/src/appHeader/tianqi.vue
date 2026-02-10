<template>
  <div class="weather-content">
    <!--插入图标-->
    <div class="weatherIcon">
      <el-image :src="require(`@/assets/weatherIcon/pear/${weather.wea_img}.png`)"
        style='width:20px;height:20px;margin-top:4px;' />
    </div>

    <!--DIY内容-->
    <div class="weatherCont">
      <p class="temperature numberFam">{{ weather.tem }}<span>℃</span> </p>
      <p class="weather">{{ weather.wea }}</p>
      <p class="WD">{{ weather.win[0] }}</p>
      <p class="WS">{{ weather.win_speed }}</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  data() {
    return {
      tqinfo: [],
      weather: [],
    };
  },
  mounted() {
    this.getWeather()
  },
  methods: {
    getWeather() {
      let _this = this;
      axios.get('http://v1.yiketianqi.com/api?unescape=1&version=v91&appid=99936692&appsecret=Yv2puO5M&ext=hours&cityid=&city=')
        .then(function (res) {
          if (res.status == 200) {
            _this.tqinfo = res.data;
            _this.weather = res.data.data[0]
            localStorage.setItem('tianqi', JSON.stringify(res.data.data))
          }
        })
        .catch(function (error) {
          this.$message.error("获取天气接口错误!");
        })
    }
  },
};
</script>


<style lang="scss" scoped>
.weather-content {
  display: flex;
  justify-content: center;
  align-items: center;

  .weatherIcon {
    ::v-deep {
      .svg-icon {
        fill: #fff;
      }
    }

  }

  .weatherCont {
    display: flex;
    justify-content: center;
    align-items: center;
    //margin-left: 10px;

    p {
      margin-left: 10px;
    }

    .temperature {
      //font-size: 30px;
      color: #05f8f5;
    }

    .weather {
      color: #05f8f5;
      //font-size: 20px;
    }

    .WD {
      font-size: 14px;
    }

    .WS {
      font-size: 14px;
    }
  }
}
</style>
